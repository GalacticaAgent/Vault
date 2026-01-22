package com.vault.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vault.entity.mysql.ChatShare;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 聊天分享Mapper
 */
@Mapper
public interface ChatShareMapper extends BaseMapper<ChatShare> {
    
    /**
     * 根据分享ID查询
     */
    @Select("SELECT * FROM chat_shares WHERE share_id = #{shareId} AND deleted = 0")
    ChatShare getByShareId(@Param("shareId") String shareId);
    
    /**
     * 获取用户创建的所有分享
     */
    @Select("SELECT * FROM chat_shares WHERE user_id = #{userId} AND deleted = 0 " +
            "ORDER BY create_time DESC")
    List<ChatShare> listByUserId(@Param("userId") Long userId);
    
    /**
     * 增加访问次数
     */
    @Update("UPDATE chat_shares SET view_count = view_count + 1 WHERE id = #{id}")
    void incrementViewCount(@Param("id") Long id);
    
    /**
     * 撤销分享
     */
    @Update("UPDATE chat_shares SET status = 'revoked' WHERE id = #{id}")
    void revokeShare(@Param("id") Long id);
}
