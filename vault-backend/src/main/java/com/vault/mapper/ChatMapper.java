package com.vault.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vault.entity.mysql.Chat;
import org.apache.ibatis.annotations.Mapper;

/**
 * 对话Mapper接口
 */
@Mapper
public interface ChatMapper extends BaseMapper<Chat> {
}
