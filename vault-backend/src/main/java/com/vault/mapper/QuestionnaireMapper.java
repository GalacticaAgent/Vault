package com.vault.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vault.entity.mysql.Questionnaire;
import org.apache.ibatis.annotations.Mapper;

/**
 * 问卷Mapper接口
 */
@Mapper
public interface QuestionnaireMapper extends BaseMapper<Questionnaire> {
}
