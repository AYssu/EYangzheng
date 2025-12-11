package com.ayssu.easyverify.service;

import com.ayssu.easyverify.entity.Admin;

public interface SystemConfigService {
    /**
     * 获取管理员信息
     * @return 管理员信息
     */
    Admin getAdminConfig();

    /**
     * 添加管理员
     * @param loginName 登录名
     * @param password 密码
     * @return 是否添加成功
     */
    boolean addAdmin(String loginName, String password);
}
