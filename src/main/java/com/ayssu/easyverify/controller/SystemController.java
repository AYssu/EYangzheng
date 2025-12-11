package com.ayssu.easyverify.controller;

import com.ayssu.easyverify.common.Result;
import com.ayssu.easyverify.entity.Admin;
import com.ayssu.easyverify.service.SystemConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;



@Slf4j
@Tag(name = "系统控制", description = "系统类接口,不会开放给三方调用,一般为内部调用")
@RestController
@RequestMapping("/system")
public class SystemController {
    private final SystemConfigService systemConfigService;

    public SystemController(SystemConfigService systemConfigService) {
        this.systemConfigService = systemConfigService;
    }

    /**
     * 管理员权限验证
     * @return "管理员权限验证成功"
     */
    @Operation(summary = "获取是否存在超级管理员")
    @GetMapping("admin-check")
    public Result<Boolean> adminCheck() {
        boolean result = false;
        // 处理管理员逻辑
        Admin adminConfig = systemConfigService.getAdminConfig();
        if (adminConfig != null) {
            result = true;
        }
        log.info("管理员权限验证结果: {}", result);
        return Result.success(result);
    }

    @Operation(summary = "添加管理员")
    @GetMapping("add-admin")
    public Result<?> addAdmin(@RequestParam String loginName, @RequestParam String password)
    {
        // 验证账号密码是否合法
        if (loginName == null || loginName.length() < 4 || loginName.length() > 16)
            return Result.badRequest("账号长度必须在4-16位之间");
        if (password == null || password.length() < 6 || password.length() > 16)
            return Result.badRequest("密码长度必须在6-16位之间");
        boolean isAdd = systemConfigService.addAdmin(loginName, password);
        if (isAdd)
            return Result.success("添加成功");
        return Result.error();
    }
}
