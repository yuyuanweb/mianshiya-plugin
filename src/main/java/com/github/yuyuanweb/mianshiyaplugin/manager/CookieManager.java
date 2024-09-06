package com.github.yuyuanweb.mianshiyaplugin.manager;

import com.github.yuyuanweb.mianshiyaplugin.config.ApiConfig;
import com.github.yuyuanweb.mianshiyaplugin.model.response.User;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * Cookie 管理器
 *
 * @author pine
 */
@Slf4j
public class CookieManager {

    /**
     * 获取当前登录用户
     */
    public static User getLoginUser() {
        User user = null;
        try {
            user = ApiConfig.mianShiYaApi.getLoginUser().execute().body().getData();
        } catch (IOException e) {
            log.error("Failed to get login user", e);
            throw new RuntimeException(e);
        }
        return user;
    }

}
