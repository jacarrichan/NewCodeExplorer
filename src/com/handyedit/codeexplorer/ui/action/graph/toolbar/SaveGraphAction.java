package com.handyedit.codeexplorer.ui.action.graph.toolbar;

import com.handyedit.codeexplorer.model.GraphIO;
import com.handyedit.codeexplorer.res.CodeExplorerBundle;
import com.handyedit.codeexplorer.ui.dialog.SaveDiagramDialog;
import com.handyedit.codeexplorer.util.ActionUtils;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.graph.builder.GraphBuilder;
import com.intellij.openapi.graph.builder.actions.export.SaveGraphToImageDialog;
import com.intellij.openapi.graph.view.Graph2D;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.IconLoader;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

/**
 * Saves the graph to file.
 *
 * @author Alexei Orishchenko
 */
public class SaveGraphAction extends BaseToolbarAction {
    
    private static final Icon ICON = IconLoader.getIcon("/actions/menu-saveall.png");

    public SaveGraphAction(GraphBuilder builder) {
        super(builder, CodeExplorerBundle.message("action.toolbar.save-graph.title"), ICON);
    }

    protected void actionPerformed(AnActionEvent e, Graph2D graph) {
        @NotNull Project project = ActionUtils.getProject(e); // project action
        File file = chooseFile(project);
        if (file == null) {
            return;
        }
        try {
            new GraphIO(getModel()).save(file);
        } catch (IOException e1) {
            Messages.showInfoMessage(
                CodeExplorerBundle.message("action.toolbar.save-error.text", file.getPath(), e1.getMessage()),
                CodeExplorerBundle.message("action.toolbar.save-error.title"));
        }
    }

    private File chooseFile(@NotNull Project project) {
        SaveGraphToImageDialog dialog = new SaveDiagramDialog(project, null);
        dialog.show();
        if (!dialog.isOK()) {
            return null;
        }
        return new File(dialog.getPath());
    }
}
