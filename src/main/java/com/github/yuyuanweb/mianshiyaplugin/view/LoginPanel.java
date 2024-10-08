package com.github.yuyuanweb.mianshiyaplugin.view;

import com.github.yuyuanweb.mianshiyaplugin.config.GlobalState;
import com.github.yuyuanweb.mianshiyaplugin.constant.CommonConstant;
import com.github.yuyuanweb.mianshiyaplugin.constant.KeyConstant;
import com.github.yuyuanweb.mianshiyaplugin.manager.CookieManager;
import com.github.yuyuanweb.mianshiyaplugin.model.response.User;
import com.github.yuyuanweb.mianshiyaplugin.utils.PanelUtil;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.Disposer;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.jcef.JCEFHtmlPanel;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.components.BorderLayoutPanel;
import org.cef.browser.CefBrowser;
import org.cef.callback.CefCookieVisitor;
import org.cef.handler.CefLoadHandlerAdapter;
import org.cef.misc.BoolRef;
import org.cef.network.CefCookie;
import org.cef.network.CefCookieManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

/**
 * @author pine
 */
public class LoginPanel extends DialogWrapper {

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

        ActionManager actionManager = ActionManager.getInstance();
        DefaultActionGroup actionGroup = (DefaultActionGroup) actionManager.getAction(KeyConstant.ACTION_BAR);
        User loginUser = GlobalState.getInstance().getSavedUser();
        PanelUtil.modifyActionGroupWhenLogin(actionGroup, loginUser);
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
            getJBCefClient().addLoadHandler(cefLoadHandler = new CefLoadHandlerAdapter() {

                @Override
                public void onLoadingStateChange(CefBrowser browser, boolean isLoading, boolean canGoBack, boolean canGoForward) {
                    CefCookieManager cefCookieManager = getJBCefCookieManager().getCefCookieManager();
                    cefCookieManager.visitAllCookies(new CefCookieVisitor() {
                        @Override
                        public boolean visit(CefCookie cefCookie, int count, int total, BoolRef boolRef) {
                            String session = "SESSION";
                            if (session.equals(cefCookie.name)) {
                                GlobalState globalState = GlobalState.getInstance();
                                globalState.saveCookie(session + "=" + cefCookie.value);
                                User loginUser = CookieManager.getLoginUser();
                                if (loginUser != null) {
                                    // Ensure the UI updates are done on the Event Dispatch Thread
                                    globalState.saveUser(loginUser);
                                    SwingUtilities.invokeLater(LoginPanel.this::doOKAction);
                                } else {
                                    globalState.removeSavedCookie();
                                    globalState.removeSavedUser();
                                }
                            }
                            return true;
                        }
                    });
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
