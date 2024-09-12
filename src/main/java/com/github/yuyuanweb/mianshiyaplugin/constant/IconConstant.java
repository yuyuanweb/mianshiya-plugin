package com.github.yuyuanweb.mianshiyaplugin.constant;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

/**
 * @author pine
 */
public interface IconConstant {

    ClassLoader CLASS_LOADER = IconConstant.class.getClassLoader();

    Icon HELP = IconLoader.findIcon("/icons/help.svg", CLASS_LOADER);

    Icon LOGO = IconLoader.findIcon("/icons/favicon.svg", CLASS_LOADER);

}
