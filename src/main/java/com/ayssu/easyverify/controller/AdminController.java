package com.ayssu.easyverify.controller;

import cn.hutool.crypto.digest.BCrypt;
import com.ayssu.easyverify.common.Result;
import com.ayssu.easyverify.entity.Admin;
import com.ayssu.easyverify.service.SystemConfigService;
import com.ayssu.easyverify.utils.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@Tag(name = "管理员接口", description = "管理员接口")
@Slf4j
@RestController
public class AdminController {

    private static final String REDIS_TOKEN_PREFIX = "admin:token:";
    private static final String REDIS_TOKEN_PWD_PREFIX = "admin:token:pwd:";

    private final SystemConfigService systemConfigService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final JwtUtil jwtUtil;

    public AdminController(SystemConfigService systemConfigService,
                           RedisTemplate<String, Object> redisTemplate,
                           JwtUtil jwtUtil) {
        this.systemConfigService = systemConfigService;
        this.redisTemplate = redisTemplate;
        this.jwtUtil = jwtUtil;
    }

    @Operation(summary = "登录接口")
    @PostMapping("/admin/login")
    public Result<?> login(@RequestBody @Validated LoginParam loginParam) {
        Admin adminConfig = systemConfigService.getAdminConfig();
        if (adminConfig == null || !adminConfig.getLoginName().equals(loginParam.getLoginName()) || !BCrypt.checkpw(loginParam.getPassword(), adminConfig.getPassword())) {
            return Result.unauthorized("用户名或密码错误");
        }

        String token = jwtUtil.generateToken(1000001L, loginParam.getLoginName());

        String redisKey = REDIS_TOKEN_PREFIX + loginParam.getLoginName();
        redisTemplate.opsForValue().set(redisKey, token, 7, TimeUnit.DAYS);

        // 保存当前密码哈希，用于后续校验密码是否被修改
        String pwdKey = REDIS_TOKEN_PWD_PREFIX + loginParam.getLoginName();
        redisTemplate.opsForValue().set(pwdKey, adminConfig.getPassword(), 7, TimeUnit.DAYS);

        return Result.success("登录成功", token);
    }

    @Operation(summary = "校验Token是否有效")
    @PostMapping("/admin/token/validate")
    public Result<?> validateToken(@RequestBody @Validated TokenValidateParam param) {
        String token = param.getToken();

        if (!jwtUtil.verify(token)) {
            return Result.unauthorized("Token 无效");
        }
        if (jwtUtil.isExpired(token)) {
            return Result.unauthorized("Token 已过期");
        }

        String username = jwtUtil.getUsername(token);
        if (username == null) {
            return Result.unauthorized("Token 缺少用户名");
        }

        Admin adminConfig = systemConfigService.getAdminConfig();
        if (adminConfig == null || !username.equals(adminConfig.getLoginName())) {
            return Result.unauthorized("管理员不存在或已变更");
        }

        Object cachedToken = redisTemplate.opsForValue().get(REDIS_TOKEN_PREFIX + username);
        if (cachedToken == null || !token.equals(cachedToken.toString())) {
            return Result.unauthorized("Token 已失效，请重新登录");
        }

        Object cachedPwd = redisTemplate.opsForValue().get(REDIS_TOKEN_PWD_PREFIX + username);
        if (cachedPwd != null && !adminConfig.getPassword().equals(cachedPwd.toString())) {
            return Result.unauthorized("密码已修改，请重新登录");
        }

        long remainSeconds = jwtUtil.getRemainSeconds(token);
        return Result.success("Token 有效", remainSeconds);
    }

    @Data
    public static class LoginParam {
        @NotBlank(message = "登录名不能为空")
        @Pattern(regexp = "^.{2,20}$", message = "登录名长度必须在2到20个字符之间")
        private String loginName;

        @NotBlank(message = "密码不能为空")
        @Pattern(regexp = "^.{6,20}$", message = "密码长度必须在6到20个字符之间")
        private String password;
    }

    @Data
    public static class TokenValidateParam {
        @NotBlank(message = "Token 不能为空")
        private String token;
    }
}

