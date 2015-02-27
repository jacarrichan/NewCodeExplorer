package com.handyedit.codeexplorer.ui.action.analyze;

import com.handyedit.codeexplorer.explore.MethodParent;
import com.handyedit.codeexplorer.math.Subgraph;
import com.handyedit.codeexplorer.res.CodeExplorerBundle;
import com.handyedit.codeexplorer.ui.dialog.SelectScopeDialog;
import com.handyedit.codeexplorer.util.ActionUtils;
import com.handyedit.codeexplorer.util.ProgressUtils;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiMethod;

/**
 * Shows all method call chains from the class, package or module to specified method.
 *
 * @author Alexei Orishchenko
 */
public class MethodCallChainsFromAction extends BaseAnalyzeAction {
    public void actionPerformed(AnActionEvent e) {
        Project project = ActionUtils.getProject(e);
        Context context = getContext(e);
        final PsiMethod to = context.getMethod();

        SelectScopeDialog dialog = new SelectScopeDialog(project);
        dialog.show();
        final MethodParent scope = dialog.getScope(project);
        if (scope == null) {
            return;
        }

        Subgraph<PsiMethod> g = ProgressUtils.run(project, CodeExplorerBundle.message("call-chains", to.getName()), new ProgressUtils.Action<Subgraph<PsiMethod>>() {
            public Subgraph<PsiMethod> run() {
                return getCallChains(scope, to);
            }
        });

        openAddGraph(project, context, g, null, to);
    }

    private Subgraph<PsiMethod> getCallChains(MethodParent unit, PsiMethod to) {
        if (unit == null) {
            return null;
        }

        return getExplorer(to).getCallChainsFrom(to, unit);
    }

    public void update(AnActionEvent e) {
        e.getPresentation().setEnabled(getContext(e) != null);
        e.getPresentation().setText(CodeExplorerBundle.message("call-chains-from"));
    }
}
