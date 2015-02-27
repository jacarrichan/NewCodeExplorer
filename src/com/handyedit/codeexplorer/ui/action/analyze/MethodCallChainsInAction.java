package com.handyedit.codeexplorer.ui.action.analyze;

import com.handyedit.codeexplorer.explore.MethodExplorer;
import com.handyedit.codeexplorer.explore.MethodParent;
import com.handyedit.codeexplorer.explore.ParentFactory;
import com.handyedit.codeexplorer.explore.ParentType;
import com.handyedit.codeexplorer.math.Subgraph;
import com.handyedit.codeexplorer.res.CodeExplorerBundle;
import com.handyedit.codeexplorer.util.ActionUtils;
import com.handyedit.codeexplorer.util.ProgressUtils;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiMethod;

/**
 * Shows all method call chains to the method.
 * All methods in call chains belong to the same unit (class, package, module or project) that the method.
 *
 * @author Alexei Orishchenko
 */
public class MethodCallChainsInAction extends BaseAnalyzeAction {
    
    public void actionPerformed(AnActionEvent e) {
        Context context = getContext(e);
        final PsiMethod method = context.getMethod();
        Project project = ActionUtils.getProject(e);
        final int scope = getScope(CodeExplorerBundle.message("action.call-chain-in.title"),
                CodeExplorerBundle.message("action.call-chain-in.text"));
        if (scope == -1) {
            return;
        }

        Subgraph<PsiMethod> methods = ProgressUtils.run(project, CodeExplorerBundle.message("call-chains", method.getName()), new ProgressUtils.Action<Subgraph<PsiMethod>>() {
            public Subgraph<PsiMethod> run() {
                return getCallChains(method, scope);
            }
        });

        openAddGraph(project, context, methods, null, method);
    }

    private int getScope(String title, String text) {
        String labelClass = CodeExplorerBundle.message("scope.class");
        String labelPackage = CodeExplorerBundle.message("scope.package");
        String labelModule = CodeExplorerBundle.message("scope.module");
        String labelProject = CodeExplorerBundle.message("scope.project");
        return Messages.showChooseDialog(text, title,
                new String[]{
                        labelClass,
                        labelPackage,
                        labelModule,
                        labelProject},
                        labelProject, null);
    }

    private Subgraph<PsiMethod> getCallChains(PsiMethod method, int opt) {
        ParentType type = ParentType.get(opt);
        MethodParent parent = ParentFactory.getInstance().getParent(method, type);
        MethodExplorer explorer = getExplorer(method);
        return explorer.getCallChains(method, parent);
    }

    public void update(AnActionEvent e) {
        e.getPresentation().setEnabled(getContext(e) != null);
        e.getPresentation().setText(CodeExplorerBundle.message("call-chains-to"));
    }
}
