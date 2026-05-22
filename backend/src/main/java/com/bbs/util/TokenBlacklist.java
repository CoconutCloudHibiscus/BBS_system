package com.bbs.util;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Token黑名单管理器
 * <p>用于管理已注销或已失效的JWT Token，防止Token被重复使用</p>
 * <p>使用ConcurrentHashMap保证线程安全，定时清理过期条目</p>
 */
@Component
public class TokenBlacklist {

    /** 黑名单Map，key为Token，value为过期时间戳（毫秒） */
    private final ConcurrentHashMap<String, Long> blacklist = new ConcurrentHashMap<>();

    /**
     * 将Token加入黑名单
     *
     * @param token           JWT Token
     * @param expirationMillis Token的剩余有效时间（毫秒）
     */
    public void addToBlacklist(String token, long expirationMillis) {
        blacklist.put(token, System.currentTimeMillis() + expirationMillis);
    }

    /**
     * 检查Token是否在黑名单中
     * <p>如果Token已过期（黑名单中的过期时间已到），自动移除并返回false</p>
     *
     * @param token JWT Token
     * @return true-在黑名单中，false-不在黑名单中
     */
    public boolean isBlacklisted(String token) {
        Long expiration = blacklist.get(token);
        if (expiration == null) {
            return false;
        }
        if (System.currentTimeMillis() > expiration) {
            blacklist.remove(token);
            return false;
        }
        return true;
    }

    /**
     * 定时清理过期的黑名单条目
     * <p>每小时执行一次，移除已过期的Token记录</p>
     */
    @Scheduled(fixedRate = 3600000)
    public void cleanup() {
        long now = System.currentTimeMillis();
        blacklist.entrySet().removeIf(entry -> now > entry.getValue());
    }
}
