package com.vault.service.skill;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Skill加载器 - 完全对标 chat-skills 的 SkillLoader
 * 参考chat-skills/chat_skills/skills/loader.py
 */
@Slf4j
@Component
public class SkillLoader {
    
    @Value("${vault.skill.directory:skills}")
    private String skillDirectory;
    
    private final Map<String, SkillDefinition> skills = new ConcurrentHashMap<>();
    private WatchService watchService;
    private ExecutorService watchExecutor;
    
    @PostConstruct
    public void init() {
        loadAllSkills();
        startWatchService();
    }
    
    @PreDestroy
    public void destroy() {
        if (watchExecutor != null) {
            watchExecutor.shutdownNow();
        }
        try {
            if (watchService != null) {
                watchService.close();
            }
        } catch (IOException e) {
            log.error("Error closing watch service", e);
        }
    }
    
    public List<SkillDefinition> loadAllSkills() {
        Path skillDirPath = resolveSkillsDir();
        
        if (!Files.exists(skillDirPath) || !Files.isDirectory(skillDirPath)) {
            log.warn("Skills directory does not exist: {}", skillDirPath);
            return new ArrayList<>();
        }
        
        skills.clear();
        
        try {
            Files.walk(skillDirPath)
                .filter(path -> path.getFileName().toString().equals("SKILL.md"))
                .forEach(this::loadSkillFromPath);
        } catch (IOException e) {
            log.error("Error scanning skills directory", e);
        }
        
        log.info("Loaded {} skills: {}", skills.size(), 
            skills.keySet().stream().collect(Collectors.joining(", ")));
        
        return new ArrayList<>(skills.values());
    }
    
    private Path resolveSkillsDir() {
        if (skillDirectory != null && !skillDirectory.isEmpty()) {
            return Paths.get(skillDirectory);
        }
        
        List<Path> candidates = Arrays.asList(
            Paths.get("skills"),
            Paths.get("demo_skills"),
            Paths.get(System.getProperty("user.home"), ".vault", "skills")
        );
        
        for (Path candidate : candidates) {
            if (Files.exists(candidate) && Files.isDirectory(candidate)) {
                return candidate;
            }
        }
        
        return candidates.get(0);
    }
    
    private void loadSkillFromPath(Path skillPath) {
        try {
            SkillDefinition skill = parseSkill(skillPath);
            if (skill != null && skill.validate()) {
                skills.put(skill.getName().toLowerCase(), skill);
                log.info("Loaded skill: {} - {}", skill.getId(), skill.getName());
            } else {
                log.warn("Skill validation failed: {}", skillPath);
                if (skill != null) {
                    skill.getValidationErrors().forEach(error -> log.warn("  - {}", error));
                }
            }
        } catch (Exception e) {
            log.error("Failed to parse skill at {}", skillPath, e);
        }
    }
    
    private SkillDefinition parseSkill(Path skillPath) {
        try {
            String content = Files.readString(skillPath);
            SkillDefinition skill = new SkillDefinition();
            
            skill.setFullPath(skillPath);
            skill.setContent(content);
            skill.setLoadTime(LocalDateTime.now());
            skill.setFileModifiedTime(LocalDateTime.ofInstant(
                Files.getLastModifiedTime(skillPath).toInstant(),
                ZoneId.systemDefault()
            ));
            
            skill.setName(extractName(content, skillPath));
            skill.setId(generateId(skillPath));
            skill.setDescription(extractDescription(content));
            skill.setTriggers(extractTriggers(content));
            skill.setTriggerKeywords(extractTriggerKeywords(content));
            skill.setSteps(extractWorkflowSteps(content));
            skill.setExamples(extractExamples(content));
            skill.setVersion(extractVersion(content));
            skill.setAuthor(extractAuthor(content));
            
            return skill;
        } catch (IOException e) {
            log.error("Error reading skill file: {}", skillPath, e);
            return null;
        }
    }
    
    private String extractName(String content, Path skillPath) {
        String[] lines = content.split("\n");
        for (int i = 0; i < Math.min(5, lines.length); i++) {
            String line = lines[i].trim();
            if (line.startsWith("# ")) {
                return line.substring(2).trim();
            }
        }
        
        String dirName = skillPath.getParent().getFileName().toString();
        return dirName.replace("_", " ").replace("-", " ").trim();
    }
    
    private String generateId(Path skillPath) {
        return skillPath.getParent().getFileName().toString();
    }
    
    private String extractDescription(String content) {
        String[] lines = content.split("\n");
        List<String> descLines = new ArrayList<>();
        boolean started = false;
        
        for (String line : lines) {
            line = line.trim();
            
            if (!started) {
                if (line.startsWith("#") || line.isEmpty()) {
                    continue;
                }
                started = true;
            }
            
            if (started && line.startsWith("##")) {
                break;
            }
            
            if (!line.isEmpty()) {
                descLines.add(line);
                if (descLines.size() >= 2) {
                    break;
                }
            }
        }
        
        String description = String.join(" ", descLines);
        if (description.length() > 200) {
            description = description.substring(0, 197) + "...";
        }
        
        return description;
    }
    
    private String extractTriggers(String content) {
        Pattern pattern = Pattern.compile(
            "#+\\s*(?:when to use|适用场景|triggers?|use cases?|scenario|场景)\\s*\\n(.*?)(?=\\n#|\\Z)",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL
        );
        
        Matcher matcher = pattern.matcher(content.toLowerCase());
        if (matcher.find()) {
            String triggerText = content.substring(
                matcher.start(1),
                Math.min(matcher.end(1), matcher.start(1) + 500)
            ).trim();
            
            String[] lines = triggerText.split("\n");
            for (String line : lines) {
                line = line.trim()
                    .replaceAll("^[-*+]\\s*", "")
                    .replaceAll("[*_`\\[\\]]", "");
                if (!line.isEmpty()) {
                    if (line.length() > 150) {
                        line = line.substring(0, 147) + "...";
                    }
                    return line;
                }
            }
        }
        
        return "";
    }
    
    private List<String> extractTriggerKeywords(String content) {
        List<String> keywords = new ArrayList<>();
        
        Pattern pattern = Pattern.compile(
            "#+\\s*(?:keywords?|关键词|触发词)\\s*\\n(.*?)(?=\\n#|\\Z)",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL
        );
        
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            String keywordSection = matcher.group(1).trim();
            String[] lines = keywordSection.split("\n");
            for (String line : lines) {
                line = line.trim()
                    .replaceAll("^[-*+]\\s*", "")
                    .replaceAll("[`\"']", "");
                if (!line.isEmpty() && !line.startsWith("#")) {
                    keywords.add(line);
                }
            }
        }
        
        return keywords;
    }
    
    private List<SkillDefinition.WorkflowStep> extractWorkflowSteps(String content) {
        List<SkillDefinition.WorkflowStep> steps = new ArrayList<>();
        
        Pattern pattern = Pattern.compile(
            "#+\\s*(?:workflow|steps|工作流|步骤)\\s*\\n(.*?)(?=\\n##|\\Z)",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL
        );
        
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            String workflowSection = matcher.group(1).trim();
            String[] lines = workflowSection.split("\n");
            
            SkillDefinition.WorkflowStep currentStep = null;
            for (String line : lines) {
                line = line.trim();
                
                if (line.matches("^\\d+\\.\\s+.+") || line.matches("^[-*+]\\s+.+")) {
                    if (currentStep != null) {
                        steps.add(currentStep);
                    }
                    currentStep = new SkillDefinition.WorkflowStep();
                    currentStep.setId("step_" + (steps.size() + 1));
                    currentStep.setDescription(line.replaceAll("^[\\d.\\-*+]+\\s*", ""));
                    currentStep.setType("custom");
                } else if (currentStep != null && !line.isEmpty()) {
                    currentStep.setDescription(currentStep.getDescription() + " " + line);
                }
            }
            
            if (currentStep != null) {
                steps.add(currentStep);
            }
        }
        
        return steps;
    }
    
    private List<SkillDefinition.Example> extractExamples(String content) {
        List<SkillDefinition.Example> examples = new ArrayList<>();
        
        Pattern pattern = Pattern.compile(
            "#+\\s*(?:examples?|示例)\\s*\\n(.*?)(?=\\n##|\\Z)",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL
        );
        
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            String exampleSection = matcher.group(1).trim();
            Pattern codePattern = Pattern.compile("```[\\w]*\\n(.*?)```", Pattern.DOTALL);
            Matcher codeMatcher = codePattern.matcher(exampleSection);
            
            while (codeMatcher.find()) {
                SkillDefinition.Example example = new SkillDefinition.Example();
                example.setInput(codeMatcher.group(1).trim());
                examples.add(example);
            }
        }
        
        return examples;
    }
    
    private String extractVersion(String content) {
        Pattern pattern = Pattern.compile("version[:\\s]+(\\S+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(content);
        return matcher.find() ? matcher.group(1) : "1.0.0";
    }
    
    private String extractAuthor(String content) {
        Pattern pattern = Pattern.compile("author[:\\s]+(.+?)(?=\\n|$)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(content);
        return matcher.find() ? matcher.group(1).trim() : "Unknown";
    }
    
    public String generateIndex() {
        if (skills.isEmpty()) {
            return "No skills available.";
        }
        
        StringBuilder index = new StringBuilder("Available Skills:\n");
        for (SkillDefinition skill : skills.values()) {
            index.append(skill.toIndexEntry());
        }
        
        return index.toString();
    }
    
    public SkillDefinition getSkillByName(String name) {
        if (name == null) {
            return null;
        }
        return skills.get(name.toLowerCase());
    }
    
    public List<String> getSkillNames() {
        return new ArrayList<>(skills.keySet());
    }
    
    public List<SkillDefinition> getAllSkills() {
        return new ArrayList<>(skills.values());
    }
    
    /**
     * 获取技能 (别名方法)
     */
    public SkillDefinition getSkill(String name) {
        return getSkillByName(name);
    }
    
    /**
     * 重新加载所有技能
     */
    public int reloadSkills() {
        loadAllSkills();
        return skills.size();
    }
    
    /**
     * 搜索技能
     */
    public List<SkillDefinition> searchSkills(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllSkills();
        }
        
        String lowerKeyword = keyword.toLowerCase();
        return skills.values().stream()
            .filter(skill -> 
                skill.getName().toLowerCase().contains(lowerKeyword) ||
                (skill.getDescription() != null && skill.getDescription().toLowerCase().contains(lowerKeyword))
            )
            .collect(Collectors.toList());
    }
    
    private void startWatchService() {
        try {
            Path skillDirPath = resolveSkillsDir();
            if (!Files.exists(skillDirPath)) {
                log.warn("Cannot start watch service - skills directory doesn't exist");
                return;
            }
            
            watchService = FileSystems.getDefault().newWatchService();
            skillDirPath.register(
                watchService,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_MODIFY,
                StandardWatchEventKinds.ENTRY_DELETE
            );
            
            watchExecutor = Executors.newSingleThreadExecutor();
            watchExecutor.submit(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        WatchKey key = watchService.take();
                        for (WatchEvent<?> event : key.pollEvents()) {
                            log.info("Skill file changed: {} - {}", 
                                event.kind(), event.context());
                            loadAllSkills();
                        }
                        key.reset();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    } catch (Exception e) {
                        log.error("Error in watch service", e);
                    }
                }
            });
            
            log.info("Started skill watch service for: {}", skillDirPath);
        } catch (IOException e) {
            log.error("Failed to start watch service", e);
        }
    }
}
