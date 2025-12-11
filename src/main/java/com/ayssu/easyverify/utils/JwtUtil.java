package com.ayssu.easyverify.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import cn.hutool.jwt.RegisteredPayload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 工具类（基于 Hutool）
 */
@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expire-days}")
    private Integer expireDays;

    @Value("${jwt.issuer}")
    private String issuer;

    /**
     * 生成 JWT Token
     *
     * @param adminId  管理员ID
     * @param username 管理员用户名
     * @return JWT Token
     */
    public String generateToken(Long adminId, String username) {
        Date now = new Date();
        Date expireDate = DateUtil.offsetDay(now, expireDays);

        Map<String, Object> payload = new HashMap<>();
        payload.put(RegisteredPayload.ISSUED_AT, now);           // 签发时间
        payload.put(RegisteredPayload.EXPIRES_AT, expireDate);   // 过期时间
        payload.put(RegisteredPayload.ISSUER, issuer);           // 颁发者
        payload.put("adminId", adminId);                         // 自定义：管理员ID
        payload.put("username", username);                       // 自定义：用户名

        String token = JWTUtil.createToken(payload, secret.getBytes());
        log.info("生成 JWT Token，adminId: {}, username: {}, 有效期: {}天", adminId, username, expireDays);
        return token;
    }

    /**
     * 验证 Token 是否有效
     *
     * @param token JWT Token
     * @return 是否有效
     */
    public boolean verify(String token) {
        try {
            return JWTUtil.verify(token, secret.getBytes());
        } catch (Exception e) {
            log.warn("JWT Token 验证失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 解析 Token 获取管理员ID
     *
     * @param token JWT Token
     * @return 管理员ID
     */
    public Long getAdminId(String token) {
        JWT jwt = JWTUtil.parseToken(token);
        Object adminId = jwt.getPayload("adminId");
        return adminId != null ? Long.parseLong(adminId.toString()) : null;
    }

    /**
     * 解析 Token 获取用户名
     *
     * @param token JWT Token
     * @return 用户名
     */
    public String getUsername(String token) {
        JWT jwt = JWTUtil.parseToken(token);
        Object username = jwt.getPayload("username");
        return username != null ? username.toString() : null;
    }

    /**
     * 检查 Token 是否过期
     *
     * @param token JWT Token
     * @return 是否过期
     */
    public boolean isExpired(String token) {
        try {
            JWT jwt = JWTUtil.parseToken(token);
            Object exp = jwt.getPayload(RegisteredPayload.EXPIRES_AT);
            if (exp != null) {
                Date expireDate = parseExpireTime(exp);
                boolean expired = expireDate.before(new Date());
                log.debug("Token 过期检查: expireDate={}, now={}, expired={}", expireDate, new Date(), expired);
                return expired;
            }
            log.warn("Token 中没有过期时间");
            return true;
        } catch (Exception e) {
            log.error("Token 过期检查失败: {}", e.getMessage(), e);
            return true;
        }
    }

    /**
     * 解析过期时间（处理不同类型和单位）
     */
    private Date parseExpireTime(Object exp) {
        if (exp instanceof Date) {
            return (Date) exp;
        }
        
        long timestamp;
        if (exp instanceof Long) {
            timestamp = (Long) exp;
        } else if (exp instanceof Integer) {
            timestamp = ((Integer) exp).longValue();
        } else {
            timestamp = Long.parseLong(exp.toString());
        }
        
        // 判断是秒级还是毫秒级时间戳
        // 秒级时间戳通常是10位数（< 10000000000）
        // 毫秒级时间戳通常是13位数（>= 10000000000）
        if (timestamp < 10000000000L) {
            // 秒级时间戳，转换为毫秒
            return new Date(timestamp * 1000);
        } else {
            // 毫秒级时间戳
            return new Date(timestamp);
        }
    }

    /**
     * 获取 Token 剩余有效时间（秒）
     *
     * @param token JWT Token
     * @return 剩余秒数
     */
    public long getRemainSeconds(String token) {
        try {
            JWT jwt = JWTUtil.parseToken(token);
            Object exp = jwt.getPayload(RegisteredPayload.EXPIRES_AT);
            if (exp != null) {
                Date expireDate = parseExpireTime(exp);
                long remain = (expireDate.getTime() - System.currentTimeMillis()) / 1000;
                return Math.max(remain, 0);
            }
            return 0;
        } catch (Exception e) {
            log.error("获取 Token 剩余时间失败: {}", e.getMessage());
            return 0;
        }
    }
}
