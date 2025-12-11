
package com.ayssu.easyverify.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MyBatis Plus 字段自动填充处理器
 */
@Slf4j
@Component
public class MetaObjectHandlerConfig implements MetaObjectHandler {

    /**
     * 插入时自动填充
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.debug("插入时自动填充字段");
        LocalDateTime now = LocalDateTime.now();
        
        // 使用 fillStrategy 方法，如果字段为 null 则填充
        // fillStrategy 会在字段值为 null 时填充，如果字段已有值则不填充
        this.fillStrategy(metaObject, "createTime", now);
        this.fillStrategy(metaObject, "updateTime", now);
        this.fillStrategy(metaObject, "deleted", 0);
        
        log.debug("插入填充完成 - createTime: {}, updateTime: {}, deleted: 0", now, now);
    }

    /**
     * 更新时自动填充
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        log.debug("更新时自动填充字段");
        LocalDateTime now = LocalDateTime.now();
        
        // 更新时填充 updateTime
        // fillStrategy 会在字段值为 null 时填充，如果字段已有值则不填充
        this.fillStrategy(metaObject, "updateTime", now);
        
        log.debug("更新填充完成 - updateTime: {}", now);
    }
}

