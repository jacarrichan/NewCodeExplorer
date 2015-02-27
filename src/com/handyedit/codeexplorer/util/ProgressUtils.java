package com.handyedit.codeexplorer.util;

import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;

public class ProgressUtils {

    public static <T> T run(Project project, String title, final Action<T> action) {
        final Object[] res = new Object[1];
        Runnable r = new Runnable() {
            public void run() {
                res[0] = action.run();
            }
        };
        if (ProgressManager.getInstance().runProcessWithProgressSynchronously(r, title, true, project)) {
            return (T) res[0];
        } else {
            return null;
        }
    }

    public static void report(String msg) {
        ProgressIndicator indicator = ProgressManager.getInstance().getProgressIndicator();
        if (indicator == null) {
            return;
        }
        indicator.setText(msg);
    }

    public static void report(String msg, int stepNo, int steps) {
        ProgressIndicator indicator = ProgressManager.getInstance().getProgressIndicator();
        if (indicator == null) {
            return;
        }
        indicator.setText(msg);
        double fraction = (double) stepNo / steps;
        indicator.setFraction(fraction);
    }

    public static boolean isCanceled() {
        try {
            ProgressManager.getInstance().checkCanceled();
            return false;
        } catch (ProcessCanceledException e) {
            return true;
        }
    }

    public static interface Action<T> {
        T run();
    }
}
