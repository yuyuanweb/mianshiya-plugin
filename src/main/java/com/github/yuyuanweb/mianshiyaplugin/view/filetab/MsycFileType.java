package com.github.yuyuanweb.mianshiyaplugin.view.filetab;

import com.github.yuyuanweb.mianshiyaplugin.constant.KeyConstant;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author pine
 */
public class MsycFileType extends LanguageFileType {

    public static final MsycFileType INSTANCE_C = new MsycFileType();

    private MsycFileType() {
        super(MsycLanguage.INSTANCE_C);
    }

    @NotNull
    @Override
    public String getName() {
        return KeyConstant.EDITOR_FILE_POSTFIX_CONTENT + "QUESTION";
    }

    @NotNull
    @Override
    public String getDescription() {
        return KeyConstant.EDITOR_FILE_POSTFIX_CONTENT;
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return KeyConstant.EDITOR_FILE_POSTFIX_CONTENT;
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return null;
    }
}