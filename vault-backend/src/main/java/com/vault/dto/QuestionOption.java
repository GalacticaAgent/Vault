package com.vault.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 题目选项DTO
 * 用于表示单个选项（key-value对）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionOption {
    
    /**
     * 选项键（A, B, C, D 等）
     */
    private String key;
    
    /**
     * 选项值（选项内容）
     */
    private String value;
}
