package com.zachary.rpc_java.Client.circuitBreaker;

import org.checkerframework.checker.units.qual.A;

import java.util.concurrent.atomic.AtomicInteger;

public class CircuitBreaker {
    // 当前状态
    private CircuitBreakerState state = CircuitBreakerState.CLOSED;
    private AtomicInteger failureCount = new AtomicInteger(0);
    private AtomicInteger successCount = new AtomicInteger(0);
    private AtomicInteger requestCount = new AtomicInteger(0);
    // 失败次数阈值
    private final int failureThreshold;
    // 半开启 -> 关闭状态的成功次数比例
    private final double halfOpenSuccessRate;
    // 恢复时间
    private final long retryTimePeriod;
    // 上一次失败时间
    private long lastFailureTime = 0;


    public CircuitBreaker(int failureThreshold, double halfOpenSuccessRate, long retryTimePeriod) {
        this.failureThreshold = failureThreshold;
        this.halfOpenSuccessRate = halfOpenSuccessRate;
        this.retryTimePeriod = retryTimePeriod;
    }

    // 查看当前熔断器是否允许请求通过
    public synchronized boolean allowRequest() {
        long currentTime = System.currentTimeMillis();
        switch (state) {
            case OPEN:
                if (currentTime - lastFailureTime > retryTimePeriod) {
                    state = CircuitBreakerState.HALF_OPEN;
                    resetCounts();
                    return true;
                }
                return false;
            case HALF_OPEN:
                requestCount.incrementAndGet();
                return true;
            case CLOSED:
            default:
                return true;
        }
    }

    // 记录成功
    public synchronized void recordSuccess() {
        if (state == CircuitBreakerState.HALF_OPEN) {
            successCount.incrementAndGet();
            if (successCount.get() >= halfOpenSuccessRate * requestCount.get()) {
                state = CircuitBreakerState.CLOSED;
                resetCounts();
            }
        } else {
            resetCounts();
        }
    }

    // 记录失败
    public synchronized void recordFailure() {
        failureCount.incrementAndGet();
        lastFailureTime = System.currentTimeMillis();
        if (state == CircuitBreakerState.HALF_OPEN) {
            state = CircuitBreakerState.OPEN;
            lastFailureTime = System.currentTimeMillis();
        } else if (failureCount.get() >= failureThreshold) {
            state = CircuitBreakerState.OPEN;
        }
    }

    // 重置次数
    private void resetCounts() {
        failureCount.set(0);
        successCount.set(0);
        requestCount.set(0);
    }
}

enum CircuitBreakerState {
    CLOSED,
    OPEN,
    HALF_OPEN
}