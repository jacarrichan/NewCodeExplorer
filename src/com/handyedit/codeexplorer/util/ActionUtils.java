package com.handyedit.codeexplorer.util;

import com.handyedit.codeexplorer.math.Edge;
import com.handyedit.codeexplorer.model.MethodNode;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.graph.builder.GraphBuilder;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;

public class ActionUtils {
    public static final String KEY_BUILDER = "CodeExplorer.builder";

    public static GraphBuilder<MethodNode, Edge> getBuilder(AnActionEvent e) {
        return (GraphBuilder<MethodNode, Edge>) e.getDataContext().getData(KEY_BUILDER);
    }

    public static Project getProject(AnActionEvent e) {
        return DataKeys.PROJECT.getData(e.getDataContext());
    }

    public static PsiClass getSelectedClass(AnActionEvent e) {
        PsiFile psiFile = getPsiFile(e);
        if (psiFile == null || !(psiFile instanceof PsiJavaFile)) {
            return null;
        }

        PsiElement elem = getElementAtCursor(e);
        PsiClass aClass = PsiUtils.getContainingClass(elem);
        if (aClass != null) {
            return aClass;
        } else {
            return PsiUtils.getTopLevelClass(psiFile);
        }
    }

    public static PsiElement getElement(AnActionEvent e) {
        return DataKeys.PSI_ELEMENT.getData(e.getDataContext());
    }

    public static Module getModule(AnActionEvent e) {
        return DataKeys.MODULE_CONTEXT.getData(e.getDataContext());
    }

    public static PsiMethod getMethodAtCursor(AnActionEvent e) {
        PsiElement elem = getElementAtCursor(e);
        return PsiUtils.getCalledContainingMethod(elem);
    }

    private static PsiElement getElementAtCursor(AnActionEvent e) {
        PsiFile psiFile = getPsiFile(e);
        Editor editor = getEditor(e);
        return PsiUtils.getElement(psiFile, editor);
    }

    private static PsiFile getPsiFile(AnActionEvent e) {
        return DataKeys.PSI_FILE.getData(e.getDataContext());
    }

    private static Editor getEditor(AnActionEvent e) {
        return DataKeys.EDITOR.getData(e.getDataContext());
    }
}
