package com.github.yuyuanweb.mianshiyaplugin.file.preview;

import com.github.yuyuanweb.mianshiyaplugin.config.GlobalState;
import com.github.yuyuanweb.mianshiyaplugin.constant.CommonConstant;
import com.github.yuyuanweb.mianshiyaplugin.constant.KeyConstant;
import com.github.yuyuanweb.mianshiyaplugin.manager.CookieManager;
import com.github.yuyuanweb.mianshiyaplugin.model.enums.WebTypeEnum;
import com.github.yuyuanweb.mianshiyaplugin.utils.ThemeUtil;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorLocation;
import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.jcef.JBCefBrowser;
import com.intellij.ui.jcef.JBCefCookie;
import com.intellij.ui.jcef.JBCefCookieManager;
import lombok.Getter;
import org.cef.browser.CefBrowser;
import org.cef.handler.CefLoadHandlerAdapter;
import org.cef.network.CefCookieManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeListener;
import java.net.URI;

/**
 * 内嵌浏览器 文件编辑器
 *
 * @author pine
 */
public class BrowserFileEditor implements FileEditor {

    @Getter
    private final JBCefBrowser jbCefBrowser;
    private final JPanel panel;
    // 持有文件引用以检查 isValid
    private final VirtualFile file;

    @Getter
    private final WebTypeEnum webTypeEnum;

    public BrowserFileEditor(@NotNull Project project, @NotNull VirtualFile file) {
        this.file = file;
        this.jbCefBrowser = new JBCefBrowser();
        this.panel = new JPanel(new BorderLayout());
        this.panel.add(jbCefBrowser.getComponent(), BorderLayout.CENTER);
        // 使用 JCEF 组件作为焦点组件，防止输入焦点丢失
        this.panel.setFocusable(true);

        // 1. 在加载 URL 之前先注入 Cookie
        injectCookie();

        // 2. 合并 LoadHandler，只添加一次
        jbCefBrowser.getJBCefClient().addLoadHandler(new CefLoadHandlerAdapter() {
            @Override
            public void onLoadingStateChange(CefBrowser browser, boolean isLoading, boolean canGoBack, boolean canGoForward) {
                // 只在加载结束时检查，避免重复执行
                if (!isLoading) {
                    CefCookieManager cefCookieManager = jbCefBrowser.getJBCefCookieManager().getCefCookieManager();
                    // 传入当前 URL
                    String currentUrl = browser.getURL();
                    if (currentUrl != null && !currentUrl.isEmpty()) {
                        CookieManager.handleCookie(cefCookieManager, currentUrl, () -> {
                        });
                    }
                }
            }
        }, jbCefBrowser.getCefBrowser());

        Long questionId = file.get().get(KeyConstant.QUESTION_ID_KEY);
        webTypeEnum = file.get().get(KeyConstant.WEB_TYPE_KEY);
        if (questionId != null && webTypeEnum != null) {
            String theme = ThemeUtil.getTheme();
            String url = String.format(CommonConstant.PLUGIN_QD, questionId, webTypeEnum.getValue(), theme);
            jbCefBrowser.loadURL(url);
        }
    }

    /**
     * 注入全局 Cookie 到当前浏览器实例
     */
    private void injectCookie() {
        try {
            String cookieStr = GlobalState.getInstance().getSavedCookie();
            if (cookieStr == null || !cookieStr.contains("=")) {
                return;
            }

            JBCefCookieManager cookieManager = jbCefBrowser.getJBCefCookieManager();
            String value = cookieStr.replace("SESSION=", "");

            String domain = ".mianshiya.com";
            try {
                URI uri = new URI(CommonConstant.WEB_HOST);
                String host = uri.getHost();
                if (host != null) {
                    // 如果 host 是 www.mianshiya.com，处理成 .mianshiya.com
                    domain = host.startsWith("www.") ? host.substring(3) : "." + host;
                }
            } catch (Exception ignored) {
            }

            JBCefCookie jbCefCookie = new JBCefCookie(
                    "SESSION",
                    value,
                    domain,
                    "/",
                    true,
                    true
            );

            // 立即设置 Cookie
            cookieManager.setCookie(CommonConstant.WEB_HOST, jbCefCookie, false);
        } catch (Exception e) {
            // 记录日志，防止 Cookie 注入失败影响整个编辑器加载
            e.printStackTrace();
        }
    }

    @Override
    public @NotNull JComponent getComponent() {
        return panel;
    }

    @Override
    public @Nullable JComponent getPreferredFocusedComponent() {
        return jbCefBrowser.getComponent();
    }

    @Override
    public @NotNull String getName() {
        return "Browser Editor";
    }

    @Override
    public void setState(@NotNull FileEditorState state) {
    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public boolean isValid() {
        // 必须返回文件是否有效，否则 IDEA 可能会异常关闭此 Tab
        return file.isValid();
    }

    @Override
    public void addPropertyChangeListener(@NotNull PropertyChangeListener listener) {
    }

    @Override
    public void removePropertyChangeListener(@NotNull PropertyChangeListener listener) {
    }

    @Override
    public void dispose() {
        // 最好把 component 从 parent 移除
        panel.removeAll();
        jbCefBrowser.dispose();
    }

    @Override
    public <T> @Nullable T getUserData(@NotNull Key<T> key) {
        return null;
    }

    @Override
    public <T> void putUserData(@NotNull Key<T> key, @Nullable T value) {
    }

    @Override
    public @Nullable FileEditorLocation getCurrentLocation() {
        return null;
    }
}