package com.handyedit.codeexplorer.ui.action.graph.toolbar;

import com.handyedit.codeexplorer.CodeExplorerPlugin;
import com.handyedit.codeexplorer.res.CodeExplorerBundle;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.graph.builder.GraphBuilder;
import com.intellij.openapi.graph.builder.actions.AbstractGraphToggleAction;
import com.intellij.openapi.graph.view.Graph2D;
import com.intellij.openapi.project.Project;
import com.intellij.util.Icons;
import org.jetbrains.annotations.NotNull;

/**
 * Show / hide method body in node tooltip.
 *
 * @author Alexei Orishchenko
 */
public class ShowMethodBodyAction extends AbstractGraphToggleAction {
    public ShowMethodBodyAction(GraphBuilder builder) {
        super(builder.getGraph(), Icons.METHOD_ICON);
    }

    protected boolean isSelected(Graph2D graph, Project project, AnActionEvent event) {
        return CodeExplorerPlugin.getSettings(project).isShowMethodBody();
    }

    protected void setSelected(Graph2D graph, boolean state, Project project, AnActionEvent e) {
        CodeExplorerPlugin.getSettings(project).setShowMethodBody(state);
    }

    protected String getText(@NotNull Graph2D graph) {
        return CodeExplorerBundle.message("show-body");
    }
}
