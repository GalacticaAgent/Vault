package com.vault.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vault.entity.mysql.Questionnaire;
import org.apache.ibatis.annotations.Mapper;

/**
 * 问卷 Mapper
 */
@Mapper
public interface QuestionnaireMapper extends BaseMapper<Questionnaire> {
    // 继承 BaseMapper 自带基础 CRUD 方法
    // 可扩展自定义 SQL 方法
}
