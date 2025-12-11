package com.ayssu.easyverify.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.ayssu.easyverify.entity.Admin;
import com.ayssu.easyverify.entity.SystemConfig;
import com.ayssu.easyverify.mapper.SystemConfigMapper;
import com.ayssu.easyverify.service.SystemConfigService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SystemConfigServiceImpl implements SystemConfigService {

    private final SystemConfigMapper systemConfigMapper;

    public SystemConfigServiceImpl(SystemConfigMapper systemConfigMapper) {
        this.systemConfigMapper = systemConfigMapper;
    }

    @Override
    public Admin getAdminConfig() {
        // 查询系统配置
        LambdaQueryWrapper<SystemConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SystemConfig::getConfigKey, "system-admin").or().eq(SystemConfig::getConfigKey, "admin-password");
        List<SystemConfig> systemConfigs = systemConfigMapper.selectList(queryWrapper);
        // 获取管理员信息
        if (systemConfigs == null)
            return null;
        // 封装管理员信息
        Admin admin = new Admin();
        // 遍历系统配置
        systemConfigs.forEach(systemConfig -> {
            if (systemConfig.getConfigKey().equals("system-admin"))
            {
                admin.setLoginName(systemConfig.getConfigValue());
            }
            if (systemConfig.getConfigKey().equals("admin-password"))
            {
                admin.setPassword(systemConfig.getConfigValue());
            }
        });
        // 判断管理员信息是否完整
        if (admin.getLoginName() == null || admin.getPassword() == null)
            return null;
        return admin;
    }

    @Override
    public boolean addAdmin(String loginName, String password) {
        // 判断system-admin 是否存在，不存在就新增，如果存在就修改
        SystemConfig adminConfig = systemConfigMapper.selectOne(
            new LambdaQueryWrapper<SystemConfig>().eq(SystemConfig::getConfigKey, "system-admin")
        );
        if (adminConfig == null) {
            // 不存在，新增
            adminConfig = new SystemConfig();
            adminConfig.setConfigKey("system-admin");
            adminConfig.setConfigValue(loginName);
            adminConfig.setConfigType("BASIC");
            adminConfig.setDescription("系统管理员账号");
            systemConfigMapper.insert(adminConfig);
        } else {
            // 存在，修改
            adminConfig.setConfigValue(loginName);
            systemConfigMapper.updateById(adminConfig);
        }

        // 判断admin-password 是否存在，不存在就新增，如果存在就修改
        SystemConfig passwordConfig = systemConfigMapper.selectOne(
            new LambdaQueryWrapper<SystemConfig>().eq(SystemConfig::getConfigKey, "admin-password")
        );
        if (passwordConfig == null) {
            // 不存在，新增
            passwordConfig = new SystemConfig();
            passwordConfig.setConfigKey("admin-password");
            // 加密密码
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
            passwordConfig.setConfigValue(hashedPassword);
            passwordConfig.setConfigType("BASIC");
            passwordConfig.setDescription("系统管理员密码");
            systemConfigMapper.insert(passwordConfig);
        } else {
            // 存在，修改
            // 加密密码
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
            passwordConfig.setConfigValue(hashedPassword);
            systemConfigMapper.updateById(passwordConfig);
        }

        return true;
    }
}
