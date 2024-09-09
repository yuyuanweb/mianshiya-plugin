package com.github.yuyuanweb.mianshiyaplugin.actions;

import cn.hutool.core.util.StrUtil;
import com.github.yuyuanweb.mianshiyaplugin.config.GlobalState;
import com.github.yuyuanweb.mianshiyaplugin.constant.CommonConstant;
import com.github.yuyuanweb.mianshiyaplugin.constant.KeyConstant;
import com.github.yuyuanweb.mianshiyaplugin.manager.CookieManager;
import com.github.yuyuanweb.mianshiyaplugin.model.response.User;
import com.github.yuyuanweb.mianshiyaplugin.view.LoginPanel;
import com.intellij.icons.AllIcons;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.ProjectManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

import static com.github.yuyuanweb.mianshiyaplugin.constant.KeyConstant.LOGOUT_ZH;

/**
 * 登录
 *
 * @author pine
 */
public class LoginAction extends AnAction implements DumbAware {

    private final DefaultActionGroup actionGroup;

    // 构造函数
    public LoginAction(String text, Icon icon, DefaultActionGroup actionGroup) {
        // Action 名称
        super(text, text, icon);
        this.actionGroup = actionGroup;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        LoginPanel loginPanel = new LoginPanel(ProjectManager.getInstance().getDefaultProject());
        loginPanel.showAndGet();

        GlobalState globalState = GlobalState.getInstance();
        String cookie = globalState.getSavedCookie();
        if (StrUtil.isBlank(cookie)) {
            return;
        }
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            User loginUser = CookieManager.getLoginUser();
            if (loginUser == null) {
                return;
            }
            ApplicationManager.getApplication().invokeLater(() -> {
                ActionManager actionManager = ActionManager.getInstance();

                // 3.1 删除 登录
                AnAction loginAction = actionManager.getAction(KeyConstant.LOGIN);
                actionGroup.remove(loginAction);
                actionManager.unregisterAction(KeyConstant.LOGIN);

                // 3.2 增加 会员
                OpenUrlAction vipAction = new OpenUrlAction(loginUser.getUserName(), CommonConstant.VIP, AllIcons.General.User);
                actionGroup.add(vipAction);
                actionManager.registerAction(KeyConstant.VIP, vipAction);

                // 3.3 增加 注销
                LogoutAction logoutAction = new LogoutAction(LOGOUT_ZH, AllIcons.Actions.Exit, actionGroup);
                actionGroup.add(logoutAction);
                actionManager.registerAction(KeyConstant.LOGOUT, logoutAction);
            });
        });

    }
}
