package com.ayssu.easyverify.interceptor;

import com.alibaba.fastjson2.JSONObject;
import com.ayssu.easyverify.common.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

/**
 * JWT 认证拦截器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

//    private final AuthServiceImpl authServiceImpl;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        // OPTIONS 请求直接放行
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }

        // 1. 从请求头获取 Token
        String token = request.getHeader("Authorization");

        if (token == null || token.isEmpty()) {
            log.warn("请求未携带 Token：{}", request.getRequestURI());
            response.setStatus(401);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            Result<?> result = Result.unauthorized("请求未携带 Token");
            response.getWriter().write(JSONObject.toJSONString(result));
            return false;
        }

        // 移除 "Bearer " 前缀
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

//        // 2. 验证 Token
//        Long adminId = authServiceImpl.validateToken(token);
//
//        if (adminId == null) {
//            log.warn("Token 验证失败：{}", request.getRequestURI());
//            response.setStatus(401);
//            response.setContentType("application/json");
//            response.setCharacterEncoding("UTF-8");
//            Result<?> result = Result.unauthorized("Token已过期或不存在");
//            response.getWriter().write(JSONObject.toJSONString(result));
//            return false;
//        }

//        // 3. 将管理员ID存入请求属性（后续可用）
//        request.setAttribute("adminId", adminId);
//
//        log.debug("Token 验证成功，adminId: {}", adminId);
        return true;
    }
}
