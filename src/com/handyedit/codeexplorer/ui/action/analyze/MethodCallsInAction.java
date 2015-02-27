package com.handyedit.codeexplorer.ui.action.analyze;

import com.handyedit.codeexplorer.explore.MethodExplorer;
import com.handyedit.codeexplorer.explore.MethodParent;
import com.handyedit.codeexplorer.explore.ParentFactory;
import com.handyedit.codeexplorer.math.Subgraph;
import com.handyedit.codeexplorer.res.CodeExplorerBundle;
import com.handyedit.codeexplorer.util.ActionUtils;
import com.handyedit.codeexplorer.util.ProgressUtils;
import com.handyedit.codeexplorer.util.PsiUtils;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;

/**
 * Shows all methods and calls between them in the unit (class, package, module or project).
 *
 * @author Alexei Orishchenko
 */
public class MethodCallsInAction extends BaseAnalyzeAction {
    public void actionPerformed(AnActionEvent e) {
        final Project project = ActionUtils.getProject(e);
        final MethodParent selected = getSelected(e);

        if (selected == null) {
            return;
        }

        String msg = CodeExplorerBundle.message("calls-in-unit", selected.getName());
        Subgraph<PsiMethod> g = ProgressUtils.run(project, msg, new ProgressUtils.Action<Subgraph<PsiMethod>>() {
            public Subgraph<PsiMethod> run() {
                return getCallChains(project, selected);
            }
        });

        if (g != null) {
            openGraph(project, g, selected.getName());
        }
    }

    private Subgraph<PsiMethod> getCallChains(Project project, MethodParent selected) {
        MethodExplorer explorer = getExplorer(project);
        return explorer.getCalls(selected);
    }

    public void update(AnActionEvent e) {
        MethodParent selected = getSelected(e);
        e.getPresentation().setEnabled(selected != null);
        if (selected != null) {
            String key = "calls-in-" + selected.getKey();
            e.getPresentation().setText(CodeExplorerBundle.message(key));
        }
    }

    private MethodParent getSelected(AnActionEvent e) {
        PsiClass psiClass = ActionUtils.getSelectedClass(e);
        ParentFactory factory = ParentFactory.getInstance();
        if (psiClass != null) {
            return factory.create(psiClass);
        }
        Module module = ActionUtils.getModule(e);
        if (module != null) {
            return factory.create(module);
        }
        PsiElement element = ActionUtils.getElement(e);
        if (element != null && element instanceof PsiDirectory) {
            PsiPackage aPackage = PsiUtils.getPackage((PsiDirectory) element);
            return factory.create(aPackage);
        }
        return null;
    }
}
