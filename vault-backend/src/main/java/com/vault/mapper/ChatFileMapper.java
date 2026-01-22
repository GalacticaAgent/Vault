package com.vault.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vault.entity.mysql.ChatFile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 聊天文件Mapper
 */
@Mapper
public interface ChatFileMapper extends BaseMapper<ChatFile> {
    
    /**
     * 获取会话的所有文件
     */
    @Select("SELECT * FROM chat_files WHERE chat_id = #{chatId} AND deleted = 0 " +
            "ORDER BY create_time DESC")
    List<ChatFile> listByChatId(@Param("chatId") Long chatId);
    
    /**
     * 获取消息关联的文件
     */
    @Select("SELECT * FROM chat_files WHERE message_id = #{messageId} AND deleted = 0")
    List<ChatFile> listByMessageId(@Param("messageId") Long messageId);
    
    /**
     * 获取用户上传的所有文件
     */
    @Select("SELECT * FROM chat_files WHERE user_id = #{userId} AND deleted = 0 " +
            "ORDER BY create_time DESC")
    List<ChatFile> listByUserId(@Param("userId") Long userId);
    
    /**
     * 根据文件类型查询
     */
    @Select("SELECT * FROM chat_files WHERE user_id = #{userId} " +
            "AND file_type = #{fileType} AND deleted = 0 " +
            "ORDER BY create_time DESC")
    List<ChatFile> listByFileType(@Param("userId") Long userId,
                                  @Param("fileType") String fileType);
}
