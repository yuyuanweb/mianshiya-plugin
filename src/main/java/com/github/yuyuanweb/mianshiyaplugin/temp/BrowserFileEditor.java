package com.github.yuyuanweb.mianshiyaplugin.temp;

import com.github.yuyuanweb.mianshiyaplugin.config.GlobalState;
import com.github.yuyuanweb.mianshiyaplugin.constant.CommonConstant;
import com.github.yuyuanweb.mianshiyaplugin.constant.KeyConstant;
import com.github.yuyuanweb.mianshiyaplugin.model.enums.WebTypeEnum;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.jcef.JBCefBrowser;
import com.intellij.ui.jcef.JBCefCookie;
import com.intellij.ui.jcef.JBCefCookieManager;
import org.cef.CefApp;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeListener;

/**
 * @author pine
 */
public class BrowserFileEditor implements FileEditor {

    private final JBCefBrowser browser;
    private final JPanel panel;

    public BrowserFileEditor(@NotNull Project project, @NotNull VirtualFile file) {
        this.browser = new JBCefBrowser();
        this.panel = new JPanel(new BorderLayout());
        this.panel.add(browser.getComponent(), BorderLayout.CENTER);
        this.panel.setPreferredSize(new Dimension());

        // 加载文件内容到浏览器
        CefApp.getInstance().onInitialization(a -> {
            JBCefCookieManager cookieManager = browser.getJBCefCookieManager();
            String cookie = GlobalState.getInstance().getSavedCookie();
            // todo JBCefCookie jbCefCookie = new JBCefCookie("SESSION", cookie.replace("SESSION=", ""), ".mianshiya.com", "/api", true, true);
            JBCefCookie jbCefCookie = new JBCefCookie("SESSION", cookie.replace("SESSION=", ""), "localhost", "/api", true, true);
            cookieManager.setCookie(CommonConstant.WEB_HOST, jbCefCookie);
            browser.setJBCefCookieManager(cookieManager);

            Long questionId = file.get().get(KeyConstant.QUESTION_ID_KEY);
            WebTypeEnum webTypeEnum = file.get().get(KeyConstant.WEB_TYPE_KEY);
            if (questionId != null && webTypeEnum != null) {
                String url = String.format(CommonConstant.PLUGIN_QD, questionId, webTypeEnum.getValue());
                browser.loadURL(url);
            }
        });
    }

    @Override
    public @NotNull JComponent getComponent() {
        return panel;
    }

    @Override
    public @Nullable JComponent getPreferredFocusedComponent() {
        return browser.getComponent();
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
        return false;
    }

    @Override
    public void addPropertyChangeListener(@NotNull PropertyChangeListener listener) {

    }

    @Override
    public void removePropertyChangeListener(@NotNull PropertyChangeListener listener) {

    }

    @Override
    public void dispose() {
        browser.dispose();
    }

    @Override
    public <T> @Nullable T getUserData(@NotNull Key<T> key) {
        return null;
    }

    @Override
    public <T> void putUserData(@NotNull Key<T> key, @Nullable T value) {

    }

}
