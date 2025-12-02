package com.github.yuyuanweb.mianshiyaplugin.view;

import cn.hutool.core.util.StrUtil;
import com.github.yuyuanweb.mianshiyaplugin.constant.CommonConstant;
import com.github.yuyuanweb.mianshiyaplugin.manager.CookieManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.Disposer;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.jcef.JCEFHtmlPanel;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.components.BorderLayoutPanel;
import org.cef.browser.CefBrowser;
import org.cef.handler.CefLoadHandlerAdapter;
import org.cef.network.CefCookieManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

/**
 * @author pine
 */
public class LoginPanel extends DialogWrapper {

    private static final Logger logger = Logger.getInstance(LoginPanel.class);

    private final BorderLayoutPanel panel = JBUI.Panels.simplePanel();

    private final Action okAction;

    public LoginPanel(@Nullable Project project) {
        super(project, null, false, IdeModalityType.IDE, false);
        okAction = new OkAction() {
        };
        JcefPanel jcefPanel;
        try {
            jcefPanel = new JcefPanel();
        } catch (IllegalArgumentException e) {
            jcefPanel = new JcefPanel(true);
        }
        Disposer.register(getDisposable(), jcefPanel);
        jcefPanel.getComponent().setMinimumSize(new Dimension(1000, 500));
        jcefPanel.getComponent().setPreferredSize(new Dimension(1500, 800));
        panel.addToTop(new JBScrollPane(jcefPanel.getComponent(), JBScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JBScrollPane.HORIZONTAL_SCROLLBAR_NEVER));

        setModal(true);
        init();
        setTitle("Login");

        // 调整对话框大小
        pack();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return panel;
    }

    @NotNull
    @Override
    protected Action getOKAction() {
        return okAction;
    }

    @Override
    protected void doOKAction() {
        super.doOKAction();
    }

    private class JcefPanel extends JCEFHtmlPanel {


        private CefLoadHandlerAdapter cefLoadHandler;

        public JcefPanel(boolean old) {
            super(null);
            init();
        }

        public JcefPanel() {
            super(null, null);
            init();
        }

        private void init() {
            logger.warn("init----------------------------------------------------------------------------------------------------");
            getJBCefClient().addLoadHandler(cefLoadHandler = new CefLoadHandlerAdapter() {

                @Override
                public void onLoadingStateChange(CefBrowser browser, boolean isLoading, boolean canGoBack, boolean canGoForward) {
                    // 建议：只在 isLoading = false (页面加载完毕) 时检查，
                    // 或者如果你是 SPA (单页应用)，可能需要更频繁的检查。
                    // 但原来的逻辑每次变化都检查也没大问题，就是性能损耗。

                    String url = browser.getURL();
                    if (StrUtil.isNotBlank(url) && url.contains("user/login")) { // 简单的防卫，防止在别的页面瞎检查
                        CefCookieManager cefCookieManager = getJBCefCookieManager().getCefCookieManager();
                        // 传入 url
                        CookieManager.handleCookie(cefCookieManager, url, LoginPanel.this::doOKAction);
                    }
                }
            }, getCefBrowser());
            loadURL(CommonConstant.WEB_HOST + "user/login");
        }

        @Override
        public void dispose() {
            SwingUtilities.invokeLater(() -> {
                getJBCefClient().removeLoadHandler(cefLoadHandler, getCefBrowser());
                super.dispose();
            });
        }
    }
}
