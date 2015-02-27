package com.handyedit.codeexplorer.test;

import test.AnonymousTest;
import com.intellij.psi.PsiMethod;
import com.handyedit.codeexplorer.util.PsiUtils;
import com.handyedit.codeexplorer.explore.ParentFactory;
import com.handyedit.codeexplorer.math.Subgraph;

import java.util.Set;

/**
 * Method usages in anonymous classes.
 *
 * @author Alexei Orishchenko
 */
public class AnonymousClassTest extends BaseIdeaTestCase {

    public void testUsagesInInner() throws Exception {
        setup(AnonymousTest.class);

        PsiMethod other = getMethod("other");
        Set<PsiMethod> usages = PsiUtils.getUsages(other);
        assertEquals(2, usages.size());
    }

    // doesn't go to the Runnable.run() - outside project
    // doesn't handle subsequent calls as usage (calls sequence analysis, NOT structure)
    public void testCallChains() throws Exception {
        setup(AnonymousTest.class);

        PsiMethod other = getMethod("other");
        Subgraph<PsiMethod> g = getExplorer().getCallChains(other, ParentFactory.getInstance().createProject(other));
        assertEquals(3, g.getNodes().size());
        assertEquals(2, g.getEdges().size());
    }
}
