package com.vault.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vault.entity.mysql.Question;
import org.apache.ibatis.annotations.Mapper;

/**
<<<<<<< HEAD
 * 题目Mapper接口
 */
@Mapper
public interface QuestionMapper extends BaseMapper<Question> {
=======
 * 题目 Mapper
 */
@Mapper
public interface QuestionMapper extends BaseMapper<Question> {
    // 继承 BaseMapper 自带基础 CRUD 方法
    // 可扩展自定义 SQL 方法
>>>>>>> a7cea79f21da3f3fdf35017c3222bcd9e3417aed
}
