package com.github.yuyuanweb.mianshiyaplugin.view.filetab;

import com.github.yuyuanweb.mianshiyaplugin.constant.ViewConstant;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorPolicy;
import com.intellij.openapi.fileEditor.WeighedFileEditorProvider;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

/**
 * @author pine
 */
public class QuestionAnswerProvider extends WeighedFileEditorProvider {


    @Override
    public boolean accept(@NotNull Project project, @NotNull VirtualFile file) {
        return true;
    }

    @Override
    public @NotNull FileEditor createEditor(@NotNull Project project, @NotNull VirtualFile file) {
        return new QuestionAnswerPreview(project, file);
    }

    @Override
    public @NotNull String getEditorTypeId() {
        return ViewConstant.QUESTION_ANSWER_PROVIDER;
    }

    @Override
    public @NotNull FileEditorPolicy getPolicy() {
        return FileEditorPolicy.HIDE_DEFAULT_EDITOR;
    }
}
