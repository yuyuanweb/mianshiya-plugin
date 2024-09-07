package com.github.yuyuanweb.mianshiyaplugin.view.filetab;

import com.github.yuyuanweb.mianshiyaplugin.config.GlobalState;
import com.github.yuyuanweb.mianshiyaplugin.constant.KeyConstant;
import com.github.yuyuanweb.mianshiyaplugin.constant.TextConstant;
import com.github.yuyuanweb.mianshiyaplugin.constant.ViewConstant;
import com.github.yuyuanweb.mianshiyaplugin.model.enums.WebTypeEnum;
import com.github.yuyuanweb.mianshiyaplugin.model.response.Question;
import com.github.yuyuanweb.mianshiyaplugin.model.response.QuestionAnswer;
import com.github.yuyuanweb.mianshiyaplugin.model.response.User;
import com.github.yuyuanweb.mianshiyaplugin.temp.BrowserFileEditorProvider;
import com.github.yuyuanweb.mianshiyaplugin.utils.FileUtils;
import com.github.yuyuanweb.mianshiyaplugin.utils.PanelUtil;
import com.github.yuyuanweb.mianshiyaplugin.utils.UserUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.UserDataHolderBase;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.keyFMap.KeyFMap;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.components.BorderLayoutPanel;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.beans.PropertyChangeListener;
import java.io.File;

import static com.github.yuyuanweb.mianshiyaplugin.config.ApiConfig.mianShiYaApi;

/**
 * @author pine
 */
public class BestQuestionAnswerPreview extends UserDataHolderBase implements FileEditor {

    private final Project project;

    @Override
    public @Nullable FileEditorLocation getCurrentLocation() {
        return FileEditor.super.getCurrentLocation();
    }

    private final VirtualFile file;
    private JBScrollPane jbScrollPane;
    private BorderLayoutPanel myComponent;
    private FileEditor fileEditor;

    private boolean isLoad = false;

    private final WebTypeEnum webTypeEnum;

    public BestQuestionAnswerPreview(Project project, VirtualFile file, WebTypeEnum webTypeEnum) {
        this.project = project;
        this.file = file;
        this.webTypeEnum = webTypeEnum;
        if (WebTypeEnum.QUESTION.equals(webTypeEnum)) {
            initComponent();
        }
    }

    @Override
    public @NotNull JComponent getComponent() {
        if (myComponent == null) {
            myComponent = JBUI.Panels.simplePanel();
            jbScrollPane = new JBScrollPane(JBScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JBScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            myComponent.addToCenter(jbScrollPane);
            if (isLoad) {
                initComponent();
            }
        }
        return myComponent;
    }

    private void initComponent() {
        isLoad = true;
        ApplicationManager.getApplication().invokeLater(() -> {
            try {
                KeyFMap keyFMap = file.get();
                Long questionId = keyFMap.get(KeyConstant.QUESTION_ID_KEY);
                if (questionId == null) {
                    jbScrollPane.setViewportView(new JBLabel(TextConstant.LOGIN));
                    return;
                }
                this.openArticle(questionId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void openArticle(Long questionId) {
        User loginUser = GlobalState.getInstance().getSavedUser();
        boolean needTipPanel = !UserUtil.hasVipAuth(loginUser);
        if (needTipPanel) {
            JBPanel<?> tipPanel = new JBPanel<>();
            if (loginUser != null) {
                JBPanel<?> needVipPanel = PanelUtil.getNeedVipPanel();
                tipPanel.add(needVipPanel);
            } else {
                tipPanel.add(new JBLabel(TextConstant.LOGIN));
            }
            myComponent.removeAll();
            myComponent.addToTop(tipPanel);
            myComponent.repaint();
            return;
        }

        File file = null;
        file = FileUtils.openArticle(project, false);
        if (!file.exists()) {
            myComponent.addToCenter(new JBLabel("No solution"));
        } else {
            VirtualFile vf = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(file);
            if (vf != null) {
                KeyFMap map = KeyFMap.EMPTY_MAP.plus(KeyConstant.QUESTION_ID_KEY, questionId);
                map = map.plus(KeyConstant.WEB_TYPE_KEY, webTypeEnum);
                vf.set(map);
                BrowserFileEditorProvider contentProvider = new BrowserFileEditorProvider();
                FileEditor newEditor = contentProvider.createEditor(project, vf);
                if (fileEditor != null) {
                    jbScrollPane.setViewportView(new JBLabel("Loading......"));
                    FileEditor temp = fileEditor;
                    Disposer.dispose(temp);
                }
                fileEditor = newEditor;
                Disposer.register(this, fileEditor);
                BorderLayoutPanel browserComponent = JBUI.Panels.simplePanel(newEditor.getComponent());
                browserComponent.addToCenter(newEditor.getComponent());
                jbScrollPane.setViewportView(browserComponent);
            }
        }
    }

    @Override
    public @Nullable JComponent getPreferredFocusedComponent() {
        return myComponent;
    }

    @Override
    public @Nls(capitalization = Nls.Capitalization.Title) @NotNull String getName() {
        return ViewConstant.BEST_QUESTION_ANSWER_PREVIEW;
    }

    @Override
    public void setState(@NotNull FileEditorState state) {
        if (state instanceof ConvergePreview.TabFileEditorState) {
            if (!isLoad && ((ConvergePreview.TabFileEditorState) state).isLoad()) {
                initComponent();
            }
        } else if (state instanceof ConvergePreview.TabSelectFileEditorState) {
            if (!isLoad) {
                initComponent();
            }
        }
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
        if (fileEditor != null) {
            Disposer.dispose(fileEditor);
        }
    }

    @Override
    public @Nullable VirtualFile getFile() {
        if (fileEditor != null) {
            return fileEditor.getFile();
        } else {
            return null;
        }
    }
}
