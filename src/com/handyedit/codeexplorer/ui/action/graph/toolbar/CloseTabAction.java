package com.handyedit.codeexplorer.ui.action.graph.toolbar;

import com.handyedit.codeexplorer.CodeExplorerPlugin;
import com.handyedit.codeexplorer.res.CodeExplorerBundle;
import com.handyedit.codeexplorer.util.ActionUtils;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.graph.builder.actions.AbstractGraphAction;
import com.intellij.openapi.graph.view.Graph2D;
import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

public class CloseTabAction extends AbstractGraphAction {

    private static final Icon ICON = IconLoader.getIcon("/actions/cancel.png");

    public CloseTabAction(Graph2D graph) {
        super(graph, CodeExplorerBundle.message("close-tab"), ICON);
    }

    protected void actionPerformed(AnActionEvent e, Graph2D graph) {
        CodeExplorerPlugin.getInstance(ActionUtils.getProject(e)).closeActiveTab();
    }
}
