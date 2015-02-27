package com.handyedit.codeexplorer.ui.action.graph.toolbar;

import com.handyedit.codeexplorer.CodeExplorerPlugin;
import com.handyedit.codeexplorer.res.CodeExplorerBundle;
import com.handyedit.codeexplorer.util.GraphUtils;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.graph.builder.GraphBuilder;
import com.intellij.openapi.graph.builder.actions.AbstractGraphToggleAction;
import com.intellij.openapi.graph.layout.hierarchic.HierarchicLayouter;
import com.intellij.openapi.graph.settings.GraphSettingsProvider;
import com.intellij.openapi.graph.view.Graph2D;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Change dependencies tree orientation: horisontal (left to right) or vertical (top to bottom).
 *
 * @author Alexei Orishchenko
 */
public class ChangeLayoutOrintationAction extends AbstractGraphToggleAction {

    private final static Icon ICON = IconLoader.getIcon("/graph/layout.png");

    public ChangeLayoutOrintationAction(GraphBuilder builder) {
        super(builder.getGraph(), ICON);
    }

    protected boolean isSelected(Graph2D graph, Project project, AnActionEvent event) {
        return CodeExplorerPlugin.getSettings(project).isHorisontalOrientation();
    }

    protected void setSelected(Graph2D graph, boolean state, Project project, AnActionEvent e) {
        CodeExplorerPlugin.getSettings(project).setHorisontalOrientation(state);
        HierarchicLayouter layouter = (HierarchicLayouter) GraphSettingsProvider.getInstance(project).getSettings(graph).getCurrentLayouter();
        GraphUtils.setLayoutOrientation(layouter, state);
        GraphUtils.layout(graph, layouter);
    }

    protected String getText(@NotNull Graph2D graph) {
        return CodeExplorerBundle.message("action.toolbar.layout.text");
    }
}
