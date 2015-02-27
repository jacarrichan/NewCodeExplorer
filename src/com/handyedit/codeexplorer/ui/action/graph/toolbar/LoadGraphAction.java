package com.handyedit.codeexplorer.ui.action.graph.toolbar;

import com.handyedit.codeexplorer.model.GraphIO;
import com.handyedit.codeexplorer.model.ModelSynchronizer;
import com.handyedit.codeexplorer.res.CodeExplorerBundle;
import com.handyedit.codeexplorer.util.ActionUtils;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.graph.builder.GraphBuilder;
import com.intellij.openapi.graph.view.Graph2D;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.File;

/**
 * Loads graph to the current tab from file.
 *
 * @author Alexei Orishchenko
 */
public class LoadGraphAction extends BaseToolbarAction {
    
    private static final Icon ICON = IconLoader.getIcon("/actions/menu-open.png");

    public LoadGraphAction(GraphBuilder builder) {
        super(builder, CodeExplorerBundle.message("action.toolbar.load-graph.title"), ICON);
    }

    protected void actionPerformed(AnActionEvent e, Graph2D graph) {
        @NotNull Project project = ActionUtils.getProject(e); // project action
        getModel().clear();
        GraphIO io = new GraphIO(getModel());
        File file = chooseFile(project, CodeExplorerBundle.message("action.toolbar.open-file-dialog.title"));
        if (file == null) {
            return;
        }
        try {
            io.load(file, project);

            ModelSynchronizer.updateNodeStatuses(getModel());

            updateGraph();
        } catch (Exception e1) {
            Messages.showInfoMessage(
                CodeExplorerBundle.message("action.toolbar.open-error.text", file.getPath(), e1.getMessage()),
                CodeExplorerBundle.message("action.toolbar.open-error.title"));
        }
    }

    private File chooseFile(@NotNull Project project, String title) {
        FileChooserDescriptor desc = FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor();
        desc.setTitle(title);
        VirtualFile curr = LocalFileSystem.getInstance().findFileByPath("");
        VirtualFile[] res = FileChooser.chooseFiles(desc, project, curr);
        return res.length > 0 ? new File(res[0].getPath()) : null;
    }
}
