package com.vault.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vault.entity.mysql.Student;
import org.apache.ibatis.annotations.Mapper;

/**
 * 学生Mapper接口
 */
@Mapper
public interface StudentMapper extends BaseMapper<Student> {
}

