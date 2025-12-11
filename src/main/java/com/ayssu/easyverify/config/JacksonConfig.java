package com.ayssu.easyverify.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Jackson 配置 - 解决前端 Long 类型精度丢失问题
 * 
 * JavaScript Number 类型最大安全整数：2^53 - 1 = 9007199254740991
 * Java Long 类型范围：-2^63 ~ 2^63-1
 * 
 * 解决方案：将 Long/long 类型统一序列化为 String
 */
@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper jacksonObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        
        // 创建自定义模块
        SimpleModule simpleModule = new SimpleModule();
        
        // Long 类型序列化为 String（避免前端精度丢失）
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
        
        // 注册模块
        objectMapper.registerModule(simpleModule);
        
        return objectMapper;
    }
}
