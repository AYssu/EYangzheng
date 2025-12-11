package com.ayssu.easyverify.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 管理端 - 系统管理控制器
 */
@Slf4j
@Tag(name = "测试接口", description = "一个用于检测服务状态的接口")
@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {
    /**
     * 测试接口
     * @return "Hello World!"
     */
    @Operation(summary = "打印测试信息")
    @GetMapping("hello")
    public String test() {
        return "Hello 阿夜?!!!";
    }
}
