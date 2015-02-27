package com.handyedit.codeexplorer.res;

import com.intellij.CommonBundle;

import java.util.ResourceBundle;

public class CodeExplorerBundle {

    public static String message(String key, Object... params)
    {
        return CommonBundle.message(BUNDLE, key, params);
    }

    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(CodeExplorerBundle.class.getCanonicalName());

}
