package com.github.yuyuanweb.mianshiyaplugin.constant;

import com.github.yuyuanweb.mianshiyaplugin.model.enums.WebTypeEnum;
import com.intellij.openapi.util.Key;

/**
 * key 常量
 *
 * @author pine
 */
public interface KeyConstant {

    String QUESTION_ID = "questionId";

    String WEB_TYPE = "webType";

    Key<Long> QUESTION_ID_KEY = new Key<>(KeyConstant.QUESTION_ID);

    Key<WebTypeEnum> WEB_TYPE_KEY = new Key<>(KeyConstant.WEB_TYPE);

    String QUESTION_BANK_ZH = "题库";
    String QUESTION_BANK = "QUESTION_BANK";

    String QUESTION_ZH = "题目";
    String QUESTION = "QUESTION";

    String WEB_ZH = "网页端";
    String WEB = "WEB";

    String HELP_ZH = "帮助";
    String HELP = "HELP";

    String LOGIN_ZH = "登录";
    String LOGIN = "LOGIN";

    String LOGOUT_ZH = "注销";
    String LOGOUT = "LOGOUT";

    String VIP_ZH = "会员";
    String VIP = "VIP";

    String PLUGIN_NAME = "面试鸭";

    String ACTION_BAR = "ToolWindowToolbar";

    String EDITOR_FILE_POSTFIX = "msy";

    String EDITOR_FILE_POSTFIX_CONTENT = "msyc";

}
