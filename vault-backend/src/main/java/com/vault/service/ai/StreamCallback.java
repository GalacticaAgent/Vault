package com.vault.service.ai;

/**
 * 流式响应回调接口
 */
public interface StreamCallback {
    
    /**
     * 接收到新的内容块
     */
    void onNext(String content);
    
    /**
     * 流式传输完成
     */
    void onComplete(AIResponse response);
    
    /**
     * 发生错误
     */
    void onError(Throwable error);
}
