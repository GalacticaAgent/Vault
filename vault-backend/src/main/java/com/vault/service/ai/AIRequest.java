package com.vault.service.ai;

import lombok.Builder;
import lombok.Data;

/**
 * AI请求参数
 */
@Data
@Builder
public class AIRequest {
    
    /**
     * 模型名称
     */
    private String model;
    
    /**
     * 温度参数（0-2）
     */
    private Double temperature;
    
    /**
     * 最大token数
     */
    private Integer maxTokens;
    
    /**
     * top_p参数
     */
    private Double topP;
    
    /**
     * 频率惩罚
     */
    private Double frequencyPenalty;
    
    /**
     * 存在惩罚
     */
    private Double presencePenalty;
    
    /**
     * 是否流式返回
     */
    private Boolean stream;
    
    /**
     * 停止序列
     */
    private String[] stop;
}
