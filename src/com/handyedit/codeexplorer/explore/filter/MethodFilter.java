package com.handyedit.codeexplorer.explore.filter;

import com.handyedit.codeexplorer.math.NodeCondition;
import com.intellij.psi.PsiMethod;

/**
 * Method filter.
 *
 * @author Alexei Orishchenko
 */
public abstract class MethodFilter implements NodeCondition<PsiMethod> {

    public static final MethodFilter EMPTY = new CompositeFilter();

    /**
     * Returns false if method is filtered.
     *
     * @param node method
     * @return false for filtered method else returns true
     */
    public abstract boolean match(PsiMethod node);
}
