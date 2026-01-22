package com.vault.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vault.entity.mysql.Chat;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 聊天会话Mapper
 */
@Mapper
public interface ChatMapper extends BaseMapper<Chat> {
    
    /**
     * 获取用户的所有会话（按时间倒序）
     */
    @Select("SELECT * FROM chats WHERE user_id = #{userId} AND deleted = 0 " +
            "ORDER BY pinned DESC, last_message_time DESC")
    List<Chat> listByUserId(@Param("userId") Long userId);
    
    /**
     * 获取用户的活跃会话
     */
    @Select("SELECT * FROM chats WHERE user_id = #{userId} AND status = 'active' " +
            "AND deleted = 0 ORDER BY last_message_time DESC")
    List<Chat> listActiveChats(@Param("userId") Long userId);
    
    /**
     * 更新会话的最后消息信息
     */
    @Select("UPDATE chats SET last_message = #{lastMessage}, " +
            "last_message_time = #{lastMessageTime}, " +
            "message_count = message_count + 1 " +
            "WHERE id = #{chatId}")
    void updateLastMessage(@Param("chatId") Long chatId, 
                          @Param("lastMessage") String lastMessage,
                          @Param("lastMessageTime") java.time.LocalDateTime lastMessageTime);
}
