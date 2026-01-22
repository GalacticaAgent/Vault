package com.vault.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vault.entity.mysql.Answer;
import org.apache.ibatis.annotations.Mapper;

/**
 * 答案 Mapper
 */
@Mapper
public interface AnswerMapper extends BaseMapper<Answer> {
    // 继承 BaseMapper 自带基础 CRUD 方法
    // 可扩展自定义 SQL 方法
}
