package com.github.yuyuanweb.mianshiyaplugin.view.filetab;

import com.github.yuyuanweb.mianshiyaplugin.constant.KeyConstant;
import com.github.yuyuanweb.mianshiyaplugin.model.enums.WebTypeEnum;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.AsyncFileEditorProvider;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorProvider;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.fileEditor.impl.text.PsiAwareTextEditorProvider;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.keyFMap.KeyFMap;
import org.jetbrains.annotations.NotNull;

/**
 * @author pine
 */
public class EditorProvider extends SplitTextEditorProvider {

    public static final Logger LOG = Logger.getInstance("#com.github.yuyuanweb.mianshiyaplugin");

    public EditorProvider() {
        super(new PsiAwareTextEditorProvider(), new ConvergeProvider(new FileEditorProvider[]{new BestQuestionAnswerProvider(WebTypeEnum.QUESTION), new BestQuestionAnswerProvider(WebTypeEnum.ANSWER), new BestQuestionAnswerProvider(WebTypeEnum.COMMENT)}, new String[]{"题目", "推荐答案", "讨论区"}));
    }

    @Override
    public boolean accept(@NotNull Project project, @NotNull VirtualFile file) {
        try {
            if (!file.exists()) {
                return false;
            }
            KeyFMap keyFMap = file.get();
            Long questionId = keyFMap.get(KeyConstant.QUESTION_ID_KEY);
            if (questionId == null) {
                return false;
            }
        } catch (Throwable e) {
            return false;
        }
        return true;
    }

    @NotNull
    @Override
    public AsyncFileEditorProvider.Builder createEditorAsync(@NotNull Project project, @NotNull VirtualFile file) {

        final AsyncFileEditorProvider.Builder firstBuilder = getBuilderFromEditorProvider(this.myFirstProvider, project, file);
        final AsyncFileEditorProvider.Builder secondBuilder = getBuilderFromEditorProvider(this.mySecondProvider, project, file);
        return new AsyncFileEditorProvider.Builder() {
            @NotNull
            @Override
            public FileEditor build() {
                return createSplitEditor(firstBuilder.build(), secondBuilder.build());
            }
        };
    }

    @Override
    protected FileEditor createSplitEditor(@NotNull FileEditor firstEditor, @NotNull FileEditor secondEditor) {

        LOG.warn("mianshiya log secondEditor.getFile() :" + secondEditor.getFile());
        LOG.warn("mianshiya log secondEditor.getName() :" + secondEditor.getName());
        LOG.warn("mianshiya log firstEditor :" + firstEditor);
        return new EditorWithPreview((TextEditor) secondEditor, firstEditor);
    }


}
