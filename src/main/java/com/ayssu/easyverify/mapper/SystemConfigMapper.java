package com.ayssu.easyverify.mapper;

import com.ayssu.easyverify.entity.SystemConfig;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统信息Mapper
 * 获取配置文件中的信息
 */
@Mapper
public interface SystemConfigMapper extends BaseMapper<SystemConfig> {

}
