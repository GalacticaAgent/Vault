package com.vault.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;
import java.util.function.Predicate;

/**
 * 重试工具类 - 支持各种重试策略
 * 对标 chat-skills 的重试机制
 */
@Slf4j
@Component
public class RetryUtil {
    
    /**
     * 重试配置
     */
    public static class RetryConfig {
        private int maxAttempts = 3;
        private long initialDelayMs = 100;
        private long maxDelayMs = 5000;
        private double backoffMultiplier = 2.0;
        private Class<? extends Exception>[] retryableExceptions;
        private Predicate<Exception> retryPredicate;
        
        public static RetryConfig defaults() {
            return new RetryConfig();
        }
        
        public RetryConfig maxAttempts(int attempts) {
            this.maxAttempts = attempts;
            return this;
        }
        
        public RetryConfig initialDelay(long delayMs) {
            this.initialDelayMs = delayMs;
            return this;
        }
        
        public RetryConfig maxDelay(long delayMs) {
            this.maxDelayMs = delayMs;
            return this;
        }
        
        public RetryConfig backoffMultiplier(double multiplier) {
            this.backoffMultiplier = multiplier;
            return this;
        }
        
        @SafeVarargs
        public final RetryConfig retryOn(Class<? extends Exception>... exceptions) {
            this.retryableExceptions = exceptions;
            return this;
        }
        
        public RetryConfig retryIf(Predicate<Exception> predicate) {
            this.retryPredicate = predicate;
            return this;
        }
        
        private boolean shouldRetry(Exception e) {
            if (retryPredicate != null) {
                return retryPredicate.test(e);
            }
            
            if (retryableExceptions != null && retryableExceptions.length > 0) {
                for (Class<? extends Exception> exClass : retryableExceptions) {
                    if (exClass.isInstance(e)) {
                        return true;
                    }
                }
                return false;
            }
            
            // 默认重试所有异常
            return true;
        }
        
        private long calculateDelay(int attemptNumber) {
            long delay = (long) (initialDelayMs * Math.pow(backoffMultiplier, attemptNumber - 1));
            return Math.min(delay, maxDelayMs);
        }
    }
    
    /**
     * 执行带重试的操作
     * 
     * @param operation 要执行的操作
     * @param config 重试配置
     * @param <T> 返回值类型
     * @return 操作结果
     * @throws Exception 如果所有重试都失败
     */
    public <T> T executeWithRetry(Callable<T> operation, RetryConfig config) throws Exception {
        Exception lastException = null;
        
        for (int attempt = 1; attempt <= config.maxAttempts; attempt++) {
            try {
                return operation.call();
            } catch (Exception e) {
                lastException = e;
                
                // 检查是否应该重试
                if (!config.shouldRetry(e)) {
                    log.warn("Exception is not retryable, failing immediately", e);
                    throw e;
                }
                
                // 如果是最后一次尝试，不再重试
                if (attempt == config.maxAttempts) {
                    log.error("All {} retry attempts failed", config.maxAttempts, e);
                    break;
                }
                
                // 计算延迟并等待
                long delay = config.calculateDelay(attempt);
                log.warn("Attempt {} failed, retrying in {}ms: {}", 
                    attempt, delay, e.getMessage());
                
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Retry interrupted", ie);
                }
            }
        }
        
        throw lastException;
    }
    
    /**
     * 简化的重试方法（使用默认配置）
     */
    public <T> T retry(Callable<T> operation) throws Exception {
        return executeWithRetry(operation, RetryConfig.defaults());
    }
    
    /**
     * 带自定义最大尝试次数的重试
     */
    public <T> T retry(Callable<T> operation, int maxAttempts) throws Exception {
        return executeWithRetry(operation, RetryConfig.defaults().maxAttempts(maxAttempts));
    }
}
