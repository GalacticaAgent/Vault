package com.vault.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vault.entity.mysql.QuestionnaireSubmission;
import org.apache.ibatis.annotations.Mapper;

/**
 * 问卷提交记录Mapper接口
 */
@Mapper
public interface QuestionnaireSubmissionMapper extends BaseMapper<QuestionnaireSubmission> {
}

