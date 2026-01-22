package com.vault.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vault.entity.mysql.QuestionnaireSubmission;
import org.apache.ibatis.annotations.Mapper;

/**
<<<<<<< HEAD
 * 问卷提交记录Mapper接口
 */
@Mapper
public interface QuestionnaireSubmissionMapper extends BaseMapper<QuestionnaireSubmission> {
=======
 * 问卷提交记录 Mapper
 */
@Mapper
public interface QuestionnaireSubmissionMapper extends BaseMapper<QuestionnaireSubmission> {
    // 继承 BaseMapper 自带基础 CRUD 方法
    // 可扩展自定义 SQL 方法
>>>>>>> a7cea79f21da3f3fdf35017c3222bcd9e3417aed
}
