package com.vault.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vault.entity.mysql.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 消息Mapper
 */
@Mapper
public interface MessageMapper extends BaseMapper<Message> {
    
    /**
     * 获取会话的所有消息（按时间正序）
     */
    @Select("SELECT * FROM messages WHERE chat_id = #{chatId} AND deleted = 0 " +
            "ORDER BY create_time ASC")
    List<Message> listByChatId(@Param("chatId") Long chatId);
    
    /**
     * 获取会话的最近N条消息
     */
    @Select("SELECT * FROM messages WHERE chat_id = #{chatId} AND deleted = 0 " +
            "ORDER BY create_time DESC LIMIT #{limit}")
    List<Message> listRecentMessages(@Param("chatId") Long chatId, 
                                     @Param("limit") Integer limit);
    
    /**
     * 统计会话的消息数量
     */
    @Select("SELECT COUNT(*) FROM messages WHERE chat_id = #{chatId} AND deleted = 0")
    Integer countByChatId(@Param("chatId") Long chatId);
    
    /**
     * 获取包含特定知识点的消息
     */
    @Select("SELECT * FROM messages WHERE chat_id = #{chatId} " +
            "AND JSON_CONTAINS(knowledge_points, #{knowledgePoint}) " +
            "AND deleted = 0")
    List<Message> listByKnowledgePoint(@Param("chatId") Long chatId,
                                       @Param("knowledgePoint") String knowledgePoint);
}
