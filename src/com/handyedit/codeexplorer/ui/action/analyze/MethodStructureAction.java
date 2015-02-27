package com.handyedit.codeexplorer.ui.action.analyze;

import com.handyedit.codeexplorer.math.Subgraph;
import com.handyedit.codeexplorer.res.CodeExplorerBundle;
import com.handyedit.codeexplorer.util.ActionUtils;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiMethod;

/**
 * Shows selected method and calls from it (or methods that call it - usages).
 *
 * @author Alexei Orishchenko
 */
public class MethodStructureAction extends BaseAnalyzeAction {
    public void actionPerformed(AnActionEvent e) {
        Project project = ActionUtils.getProject(e);
        Context context = getContext(e);
        PsiMethod method = context.getMethod();

        Subgraph<PsiMethod> g = getExplorer(method).getStructure(method);

        openAddGraph(project, context, g, method, method);
    }

    public void update(AnActionEvent e) {
        e.getPresentation().setEnabled(getContext(e) != null);
        e.getPresentation().setText(CodeExplorerBundle.message("method-structure"));
    }
}
