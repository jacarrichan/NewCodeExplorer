package com.handyedit.codeexplorer.ui.action.graph.toolbar;

import com.handyedit.codeexplorer.model.ModelSynchronizer;
import com.handyedit.codeexplorer.res.CodeExplorerBundle;
import com.handyedit.codeexplorer.util.ActionUtils;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.graph.builder.GraphBuilder;
import com.intellij.openapi.graph.view.Graph2D;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.psi.PsiDocumentManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Updates model by current sources: adds method calls for method nodes with explored method calls,
 * usages - for method nodes with explored usages.
 *
 * @author Alexei Orishchenko
 */
public class SynchronizeGraphAction extends BaseToolbarAction {

    private static final Icon ICON = IconLoader.getIcon("/actions/sync.png");
    
    public SynchronizeGraphAction(GraphBuilder builder) {
        super(builder, CodeExplorerBundle.message("action.toolbar.sync"), ICON);
    }

    protected void actionPerformed(AnActionEvent e, Graph2D graph) {
        @NotNull Project project = ActionUtils.getProject(e); // project action
        PsiDocumentManager.getInstance(project).commitAllDocuments();

        ModelSynchronizer.updateExploredNodes(getModel());
        updateGraph();
    }
}
