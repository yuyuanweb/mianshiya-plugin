package com.github.yuyuanweb.mianshiyaplugin;

import com.intellij.DynamicBundle;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.PropertyKey;

public final class MyBundle extends DynamicBundle {

    @NonNls
    private static final String BUNDLE = "messages.MyBundle";

    private MyBundle() {
        super(BUNDLE);
    }

    public static String message(@PropertyKey(resourceBundle = BUNDLE) String key, Object... params) {
        return getInstance().getMessage(key, params);
    }

    @SuppressWarnings("unused")
    public static String messagePointer(@PropertyKey(resourceBundle = BUNDLE) String key, Object... params) {
        return getInstance().getLazyMessage(key, params).get();
    }

    private static MyBundle getInstance() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        private static final MyBundle INSTANCE = new MyBundle();
    }
}
