package com.handyedit.codeexplorer.ui.dialog;

import com.handyedit.codeexplorer.res.CodeExplorerBundle;
import com.intellij.openapi.graph.builder.actions.export.SaveGraphToImageDialog;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;

public class SaveDiagramDialog extends SaveGraphToImageDialog {
    public SaveDiagramDialog(Project project, @Nullable VirtualFile directory) {
        super(project, directory, "com/handyedit/codeexplorer", Collections.singleton("xml"));
        setTitle(CodeExplorerBundle.message("action.toolbar.save-file-dialog.title"));
    }
}
