package com.handyedit.codeexplorer.ui.action.graph.toolbar;

import com.handyedit.codeexplorer.CodeExplorerPlugin;
import com.handyedit.codeexplorer.res.CodeExplorerBundle;
import com.handyedit.codeexplorer.util.GraphUtils;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.graph.builder.GraphBuilder;
import com.intellij.openapi.graph.builder.actions.AbstractGraphToggleAction;
import com.intellij.openapi.graph.view.Graph2D;
import com.intellij.openapi.project.Project;
import com.intellij.util.Icons;
import org.jetbrains.annotations.NotNull;

/**
 * Show / hide class names for method nodes.
 *
 * @author Alexei Orishchenko
 */
public class ShowClassNamesAction extends AbstractGraphToggleAction {

    private GraphBuilder _builder;

    public ShowClassNamesAction(GraphBuilder builder) {
        super(builder.getGraph(), Icons.CLASS_ICON);
        _builder = builder;
    }

    protected boolean isSelected(Graph2D graph, Project project, AnActionEvent event) {
        return CodeExplorerPlugin.getSettings(project).isShowClassName();
    }

    protected void setSelected(Graph2D graph, boolean state, Project project, AnActionEvent e) {
        CodeExplorerPlugin.getSettings(project).setShowClassName(state);
        _builder.updateGraph();
        GraphUtils.layout(graph, project);
    }

    protected String getText(@NotNull Graph2D graph) {
        return CodeExplorerBundle.message("action.toolbar.show-class-names");
    }
}
