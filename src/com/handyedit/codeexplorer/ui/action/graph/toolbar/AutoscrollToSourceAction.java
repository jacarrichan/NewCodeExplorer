package com.handyedit.codeexplorer.ui.action.graph.toolbar;

import com.handyedit.codeexplorer.CodeExplorerPlugin;
import com.handyedit.codeexplorer.CodeExplorerSettings;
import com.handyedit.codeexplorer.res.CodeExplorerBundle;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.graph.builder.GraphBuilder;
import com.intellij.openapi.graph.builder.actions.AbstractGraphToggleAction;
import com.intellij.openapi.graph.view.Graph2D;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Enable / disable autoscroll to source on node selection.
 *
 * @author Alexei Orishchenko
 */
public class AutoscrollToSourceAction extends AbstractGraphToggleAction {

    private static final Icon ICON = IconLoader.getIcon("/general/autoscrollToSource.png");

    public AutoscrollToSourceAction(GraphBuilder builder) {
        super(builder.getGraph(), ICON);
    }

    protected boolean isSelected(Graph2D graph, Project project, AnActionEvent event) {
        return CodeExplorerPlugin.getSettings(project).isGotoSource();
    }

    protected void setSelected(Graph2D graph, boolean state, Project project, AnActionEvent e) {
        CodeExplorerSettings s = CodeExplorerPlugin.getSettings(project);
        s.setGotoSource(!s.isGotoSource());
    }

    protected String getText(@NotNull Graph2D graph) {
        return CodeExplorerBundle.message("action.toolbar.autoscroll.text");
    }
}
