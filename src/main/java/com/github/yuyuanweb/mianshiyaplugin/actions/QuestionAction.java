package com.github.yuyuanweb.mianshiyaplugin.actions;

import com.github.yuyuanweb.mianshiyaplugin.view.QuestionListManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class QuestionAction extends AnAction {

    private final QuestionListManager questionListManager;

    public QuestionAction() {
        questionListManager = new QuestionListManager();
    }

    public QuestionAction(String text, Icon icon) {
        super(text, text, icon);
        questionListManager = new QuestionListManager();
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        questionListManager.addQuestionTab(null, anActionEvent.getProject());
    }

}
