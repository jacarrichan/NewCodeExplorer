package com.handyedit.codeexplorer.test;

import com.handyedit.codeexplorer.explore.ParentFactory;
import com.handyedit.codeexplorer.explore.ParentType;
import com.handyedit.codeexplorer.math.Subgraph;
import com.handyedit.codeexplorer.util.PsiUtils;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;

import java.util.Set;

import test.Child;
import test.Parent;
import test.ChildTest;
import test.util.ChildUtil;

/**
 * Call chains and usages tests.
 * 
 * @author Alexei Orishchenko
 */
public class ProjectStructureTest extends BaseIdeaTestCase {

    public void testUsages() throws Exception {
        setup(Child.class);
        PsiClass parent = addFile(Parent.class);
        PsiClass childTest = addFile(ChildTest.class);
        PsiClass childUtil = addFile(ChildUtil.class);

        PsiMethod drawChild = getMethod("drawChild");
        PsiMethod draw = getMethod(parent, "draw");
        PsiMethod testChild = getMethod(childTest, "testChild");
        PsiMethod doSomething = getMethod(childUtil, "doSomething");

        Set<PsiMethod> usages = PsiUtils.getUsages(drawChild);
        assertEquals(getNodes(draw, testChild, doSomething), usages);
    }

    public void testCallChainsClass() throws Exception {
        setup(Child.class);
        addFile(Parent.class);
        addFile(ChildTest.class);
        addFile(ChildUtil.class);

        PsiMethod drawChild = getMethod("drawChild");
        PsiMethod drawTitle = getMethod("drawTitle");
        PsiMethod drawIcon = getMethod("drawIcon");
        PsiMethod log = getMethod("log");

        Subgraph<PsiMethod> g = getExplorer().getCallChains(log, ParentFactory.getInstance().createClass(log));
        Set<PsiMethod> expected = getNodes(drawChild, drawTitle, drawIcon, log);
        assertEquals(expected, g.getNodes());

        addEdge(drawChild, drawIcon);
        addEdge(drawChild, drawTitle);
        addEdge(drawIcon, log);
        addEdge(drawTitle, log);
        assertEquals(getEdges(), g.getEdges());
    }

    public void testCallChainsPackage() throws Exception {
        setup(Child.class);
        PsiClass parent = addFile(Parent.class);
        PsiClass childTest = addFile(ChildTest.class);

        PsiMethod drawChild = getMethod("drawChild");
        PsiMethod drawTitle = getMethod("drawTitle");
        PsiMethod drawIcon = getMethod("drawIcon");
        PsiMethod log = getMethod("log");
        PsiMethod draw = getMethod(parent, "draw");
        PsiMethod testChild = getMethod(childTest, "testChild");

        Subgraph<PsiMethod> g = getExplorer().getCallChains(log, ParentFactory.getInstance().createPackage(log));
        Set<PsiMethod> expected = getNodes(drawChild, drawTitle, drawIcon, log, draw, testChild);
        assertEquals(expected, g.getNodes());

        addEdge(drawChild, drawIcon);
        addEdge(drawChild, drawTitle);
        addEdge(drawIcon, log);
        addEdge(drawTitle, log);
        addEdge(draw, drawChild);
        addEdge(testChild, drawChild);
        assertEquals(getEdges(), g.getEdges());
    }

    public void testCallChainsModule() throws Exception {
        doTest(ParentType.MODULE);
    }

    public void testCallChainsProject() throws Exception {
        doTest(ParentType.PROJECT);
    }

    private void doTest(ParentType type) throws Exception {
        setup(Child.class);
        PsiClass parent = addFile(Parent.class);
        PsiClass childTest = addFile(ChildTest.class);
        PsiClass childUtil = addFile(ChildUtil.class);

        PsiMethod drawChild = getMethod("drawChild");
        PsiMethod drawTitle = getMethod("drawTitle");
        PsiMethod drawIcon = getMethod("drawIcon");
        PsiMethod log = getMethod("log");
        PsiMethod draw = getMethod(parent, "draw");
        PsiMethod testChild = getMethod(childTest, "testChild");
        PsiMethod doSomething = getMethod(childUtil, "doSomething");

        Subgraph<PsiMethod> g = getExplorer().getCallChains(log, ParentFactory.getInstance().getParent(log, type));
        Set<PsiMethod> expected = getNodes(drawChild, drawTitle, drawIcon, log, draw, testChild, doSomething);
        assertEquals(expected, g.getNodes());

        addEdge(drawChild, drawIcon);
        addEdge(drawChild, drawTitle);
        addEdge(drawIcon, log);
        addEdge(drawTitle, log);
        addEdge(draw, drawChild);
        addEdge(testChild, drawChild);
        addEdge(doSomething, drawChild);
        assertEquals(getEdges(), g.getEdges());
    }
}
