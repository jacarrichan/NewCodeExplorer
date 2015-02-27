package com.handyedit.codeexplorer.ui.action.graph.toolbar;

import com.handyedit.codeexplorer.CodeExplorerPlugin;
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
 * Changes action on method node click: add (to the diagram) calls from the method or its usages.
 *
 * @author Alexei Orishchenko
 */
public class ChangeNodeClickAction extends AbstractGraphToggleAction {

    private static final Icon ICON_UP = IconLoader.getIcon("/hierarchy/caller.png");
    private static final Icon ICON_DOWN = IconLoader.getIcon("/hierarchy/callee.png");

    private boolean _isStructureNavigation;
    private UpdateToolbarListener _listener;

    public ChangeNodeClickAction(GraphBuilder builder, boolean structureNavigation) {
        super(builder.getGraph(), structureNavigation ? ICON_DOWN : ICON_UP);
        _isStructureNavigation = structureNavigation;
    }

    protected boolean isSelected(Graph2D graph, Project project, AnActionEvent event) {
        return CodeExplorerPlugin.getSettings(project).isStructureByClick() ^ !_isStructureNavigation;
    }

    protected void setSelected(Graph2D graph, boolean state, Project project, AnActionEvent e) {
        CodeExplorerPlugin.getSettings(project).setStructureByClick(state ^ !_isStructureNavigation);
        graph.updateViews();
    }

    protected String getText(@NotNull Graph2D graph) {
        return _isStructureNavigation ? CodeExplorerBundle.message("action.toolbar.expand-structure.text") :
                CodeExplorerBundle.message("action.toolbar.expand-usage.text");
    }

    public void update(AnActionEvent e) {
        super.update(e);

        if (_listener != null) {
            _listener.updated();
        }
    }

    public void setListener(UpdateToolbarListener listener) {
        _listener = listener;
    }
}
