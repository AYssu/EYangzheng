package com.ayssu.easyverify.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 配置（Spring Boot 4.x 兼容）
 */
@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // String 的序列化
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        
        // 使用 RedisSerializer.json()（Spring Boot 4.0+ 推荐方式）
        RedisSerializer<Object> jsonSerializer = RedisSerializer.json();

        // key 采用 String 的序列化方式
        template.setKeySerializer(stringSerializer);
        // hash 的 key 也采用 String 的序列化方式
        template.setHashKeySerializer(stringSerializer);
        // value 序列化方式采用 jackson
        template.setValueSerializer(jsonSerializer);
        // hash 的 value 序列化方式采用 jackson
        template.setHashValueSerializer(jsonSerializer);
        
        template.afterPropertiesSet();

        return template;
    }
}
