package com.ayssu.easyverify.interceptor;

import com.alibaba.fastjson2.JSONObject;
import com.ayssu.easyverify.common.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

/**
 * 本地访问拦截器
 * 限制只能通过 127.0.0.1 或 localhost 访问
 */
@Slf4j
@Component
public class LocalhostInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        String clientIp = getClientIp(request);
        String host = request.getServerName();
        
        // 检查 IP 地址是否为 127.0.0.1 或 localhost
        boolean isLocalhost = "127.0.0.1".equals(clientIp) 
                || "0:0:0:0:0:0:0:1".equals(clientIp)  // IPv6 的 localhost
                || "localhost".equalsIgnoreCase(host)
                || "127.0.0.1".equals(host);
        
        if (!isLocalhost) {
            log.warn("拒绝非本地访问：IP={}, Host={}, URI={}", clientIp, host, request.getRequestURI());
            response.setStatus(403);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            Result<?> result = Result.error("禁止访问：此接口仅允许本地访问");
            response.getWriter().write(JSONObject.toJSONString(result));
            return false;
        }
        
        log.debug("允许本地访问：IP={}, Host={}, URI={}", clientIp, host, request.getRequestURI());
        return true;
    }

    /**
     * 获取客户端真实 IP 地址
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        
        // 处理多个 IP 的情况（X-Forwarded-For 可能包含多个 IP）
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        
        return ip;
    }
}

