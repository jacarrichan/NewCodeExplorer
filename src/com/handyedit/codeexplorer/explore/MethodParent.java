package com.handyedit.codeexplorer.explore;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;

import java.util.Set;

/**
 * Wrapper for method containers provided by Open API (PsiClass, PsiPackage, Module, Project).
 * Common name and classes getters, checking class containment.
 *
 * @author Alexei Orishchenko
 */
public interface MethodParent {
    String getName();
    String getQualifiedName();
    String getKey();
    Set<PsiClass> getClasses();

    ParentType getType();

    boolean contains(PsiMethod method);
}
