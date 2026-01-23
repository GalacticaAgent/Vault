package com.vault.service.chat;

import com.vault.dto.response.FileUploadResponse;
import com.vault.entity.mysql.ChatFile;
import com.vault.mapper.ChatFileMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 文件服务
 * 处理文件上传、解析、存储
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {
    
    private final ChatFileMapper chatFileMapper;
    
    @Value("${vault.file.upload-dir:uploads}")
    private String uploadDir;
    
    private String absoluteUploadPath;
    
    @Value("${vault.file.max-size:10485760}") // 10MB
    private long maxFileSize;
    
    // MinIO配置（实际应该注入MinIO客户端）
    @Value("${vault.minio.endpoint:http://localhost:9000}")
    private String minioEndpoint;
    
    @Value("${vault.minio.bucket:vault-files}")
    private String minioBucket;
    
    /**
     * 初始化上传目录的绝对路径
     */
    @PostConstruct
    public void init() {
        try {
            Path path = Paths.get(uploadDir);
            if (!path.isAbsolute()) {
                // 如果是相对路径，转换为绝对路径（相对于项目根目录）
                absoluteUploadPath = Paths.get(System.getProperty("user.dir"), uploadDir).toString();
            } else {
                absoluteUploadPath = uploadDir;
            }
            
            // 创建目录
            Files.createDirectories(Paths.get(absoluteUploadPath));
            log.info("文件上传目录初始化成功: {}", absoluteUploadPath);
        } catch (IOException e) {
            log.error("文件上传目录初始化失败", e);
            throw new RuntimeException("文件上传目录初始化失败", e);
        }
    }
    
    /**
     * 上传文件
     */
    @Transactional
    public FileUploadResponse uploadFile(Long userId, Long chatId, MultipartFile file, String fileType) {
        try {
            // 验证文件
            validateFile(file);
            
            // 确定文件类型
            if (fileType == null) {
                fileType = detectFileType(file);
            }
            
            // 保存文件
            String filePath = saveFile(file);
            
            // 提取内容
            String extractedContent = extractContent(file, fileType);
            
            // 保存文件信息到数据库
            ChatFile chatFile = new ChatFile();
            chatFile.setUserId(userId);
            chatFile.setChatId(chatId);
            chatFile.setFileName(file.getOriginalFilename());
            chatFile.setFileType(fileType);
            chatFile.setFileSize(file.getSize());
            chatFile.setFilePath(filePath);
            chatFile.setFileUrl(buildFileUrl(filePath));
            chatFile.setExtractedContent(extractedContent);
            chatFile.setStatus("completed");
            
            // 检测编程语言（如果是代码）
            if ("code".equals(fileType)) {
                String language = detectLanguage(file.getOriginalFilename());
                chatFile.setLanguage(language);
            }
            
            chatFileMapper.insert(chatFile);
            
            log.info("File uploaded: {} by user {}", file.getOriginalFilename(), userId);
            
            // 构建响应
            return FileUploadResponse.builder()
                    .id(chatFile.getId())
                    .fileName(chatFile.getFileName())
                    .fileType(chatFile.getFileType())
                    .fileSize(chatFile.getFileSize())
                    .fileUrl(chatFile.getFileUrl())
                    .language(chatFile.getLanguage())
                    .contentSummary(getSummary(extractedContent))
                    .createTime(chatFile.getCreateTime())
                    .build();
                    
        } catch (Exception e) {
            log.error("Failed to upload file", e);
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }
    
    /**
     * 验证文件
     */
    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("文件为空");
        }
        
        if (file.getSize() > maxFileSize) {
            throw new RuntimeException("文件大小超过限制");
        }
        
        String filename = file.getOriginalFilename();
        if (filename == null || filename.contains("..")) {
            throw new RuntimeException("非法文件名");
        }
    }
    
    /**
     * 检测文件类型
     */
    private String detectFileType(MultipartFile file) {
        String filename = file.getOriginalFilename();
        if (filename == null) {
            return "other";
        }
        
        String extension = getFileExtension(filename).toLowerCase();
        
        // 代码文件
        Set<String> codeExtensions = Set.of("java", "py", "js", "cpp", "c", "h", 
                                           "ts", "go", "rs", "rb", "php");
        if (codeExtensions.contains(extension)) {
            return "code";
        }
        
        // PDF文件
        if ("pdf".equals(extension)) {
            return "pdf";
        }
        
        // 图片文件
        Set<String> imageExtensions = Set.of("jpg", "jpeg", "png", "gif", "bmp");
        if (imageExtensions.contains(extension)) {
            return "image";
        }
        
        return "other";
    }
    
    /**
     * 保存文件
     */
    private String saveFile(MultipartFile file) throws IOException {
        // 生成文件路径：uploads/yyyy/MM/dd/uuid_filename
        LocalDateTime now = LocalDateTime.now();
        String datePath = now.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String uuid = UUID.randomUUID().toString();
        String filename = uuid + "_" + file.getOriginalFilename();
        
        // 使用绝对路径
        Path dirPath = Paths.get(absoluteUploadPath, datePath);
        Files.createDirectories(dirPath);
        
        Path filePath = dirPath.resolve(filename);
        file.transferTo(filePath.toFile());
        
        log.info("文件保存成功: {}", filePath.toString());
        
        // 返回相对路径（用于URL访问）
        return datePath + "/" + filename;
    }
    
    /**
     * 提取文件内容
     */
    private String extractContent(MultipartFile file, String fileType) {
        try {
            switch (fileType) {
                case "code":
                    return extractCodeContent(file);
                case "pdf":
                    return extractPdfContent(file);
                case "image":
                    return ""; // 图片不提取文本
                default:
                    return extractTextContent(file);
            }
        } catch (Exception e) {
            log.error("Failed to extract content from file", e);
            return "";
        }
    }
    
    /**
     * 提取代码内容
     */
    private String extractCodeContent(MultipartFile file) throws IOException {
        return new String(file.getBytes(), "UTF-8");
    }
    
    /**
     * 提取PDF内容
     */
    private String extractPdfContent(MultipartFile file) throws IOException {
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }
    
    /**
     * 提取普通文本内容
     */
    private String extractTextContent(MultipartFile file) throws IOException {
        return new String(file.getBytes(), "UTF-8");
    }
    
    /**
     * 检测编程语言
     */
    private String detectLanguage(String filename) {
        String extension = getFileExtension(filename).toLowerCase();
        
        Map<String, String> languageMap = new HashMap<>();
        languageMap.put("java", "Java");
        languageMap.put("py", "Python");
        languageMap.put("js", "JavaScript");
        languageMap.put("ts", "TypeScript");
        languageMap.put("cpp", "C++");
        languageMap.put("c", "C");
        languageMap.put("h", "C/C++");
        languageMap.put("go", "Go");
        languageMap.put("rs", "Rust");
        languageMap.put("rb", "Ruby");
        languageMap.put("php", "PHP");
        
        return languageMap.getOrDefault(extension, "Unknown");
    }
    
    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        if (lastDot > 0) {
            return filename.substring(lastDot + 1);
        }
        return "";
    }
    
    /**
     * 构建文件URL
     */
    private String buildFileUrl(String filePath) {
        // 实际应该返回MinIO或CDN的URL
        return "/files/" + filePath;
    }
    
    /**
     * 获取内容摘要
     */
    private String getSummary(String content) {
        if (content == null || content.isEmpty()) {
            return "";
        }
        
        int maxLength = 200;
        if (content.length() <= maxLength) {
            return content;
        }
        
        return content.substring(0, maxLength) + "...";
    }
    
    /**
     * 获取文件详情
     */
    public ChatFile getFile(Long fileId) {
        return chatFileMapper.selectById(fileId);
    }
    
    /**
     * 删除文件
     */
    @Transactional
    public void deleteFile(Long userId, Long fileId) {
        ChatFile file = chatFileMapper.selectById(fileId);
        if (file == null || !file.getUserId().equals(userId)) {
            throw new RuntimeException("文件不存在");
        }
        
        // 删除物理文件
        try {
            Path filePath = Paths.get(uploadDir, file.getFilePath());
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.error("Failed to delete physical file", e);
        }
        
        // 删除数据库记录
        chatFileMapper.deleteById(fileId);
        
        log.info("Deleted file {} by user {}", fileId, userId);
    }
    
    /**
     * 获取用户的所有文件
     */
    public List<ChatFile> listUserFiles(Long userId) {
        return chatFileMapper.listByUserId(userId);
    }
}
