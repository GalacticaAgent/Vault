package com.vault.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vault.entity.mysql.Answer;
import org.apache.ibatis.annotations.Mapper;

/**
 * 答案Mapper接口
 */
@Mapper
public interface AnswerMapper extends BaseMapper<Answer> {
}
