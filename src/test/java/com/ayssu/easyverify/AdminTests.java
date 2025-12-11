package com.ayssu.easyverify;

import com.alibaba.fastjson2.JSONObject;
import com.ayssu.easyverify.entity.Admin;
import com.ayssu.easyverify.service.SystemConfigService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AdminTests {
    @Autowired
    private SystemConfigService systemConfigService;

    @Test
    public void test() {
        Admin adminConfig = systemConfigService.getAdminConfig();
        System.out.println(JSONObject.from(adminConfig).toString());
    }
}
