package com.handyedit.codeexplorer.test;

import com.handyedit.codeexplorer.explore.ParentFactory;
import com.handyedit.codeexplorer.explore.filter.MethodFilter;
import com.handyedit.codeexplorer.math.Subgraph;
import com.handyedit.codeexplorer.util.PsiUtils;
import com.intellij.psi.PsiMethod;

import java.util.HashSet;
import java.util.Set;

import test.Structure;

/**
 * Call chains and usages tests.
 * 
 * @author Alexei Orishchenko
 */
public class StructureTest extends BaseIdeaTestCase {

    private void setup() throws Exception {
        setup(Structure.class);
    }

    public void testUsage() throws Exception {
        setup();
        
        Set<PsiMethod> usages = PsiUtils.getUsages(getMethod("init"));
        assertNotNull(usages);
        assertEquals(2, usages.size());

        Set<PsiMethod> expected = getNodes(getMethod("anotherRoot"), getMethod("doWork"));
        assertEquals(expected, usages);
    }

    public void testUsageRecursy() throws Exception {
        setup();

        PsiMethod factorial = getMethod("factorial");
        Set<PsiMethod> usages = PsiUtils.getUsages(factorial, MethodFilter.EMPTY);
        assertNotNull(usages);
        assertEquals(getNodes(factorial), usages);
    }

    public void testUsageEmpty() throws Exception {
        setup();

        Set<PsiMethod> usages = PsiUtils.getUsages(getMethod("anotherRoot"), MethodFilter.EMPTY);
        assertNotNull(usages);
        assertTrue(usages.isEmpty());
    }

    public void testCallChainsTo() throws Exception {
        setup();

        PsiMethod anotherRoot = getMethod("anotherRoot");
        PsiMethod multiply = getMethod("multiply");
        PsiMethod doMultiply = getMethod("doMultiply");

        // anotherRoot --> multiply --> doMultiply
        Subgraph<PsiMethod> path = getExplorer().getCallChains(doMultiply, ParentFactory.getInstance().createProject(doMultiply));

        assertEquals(getNodes(anotherRoot, multiply, doMultiply), path.getNodes());

        addEdge(multiply, doMultiply);
        addEdge(anotherRoot, multiply);
        assertEquals(getEdges(), path.getEdges());
    }

    public void testManyCallChainsTo() throws Exception {
        setup();

        PsiMethod root = getMethod("root");
        PsiMethod sum = getMethod("sum");
        PsiMethod calculate = getMethod("calculate");
        PsiMethod doWork = getMethod("doWork");
        PsiMethod anotherRoot = getMethod("anotherRoot");

        // anotherRoot -> sum
        // root -> doWork -> calculate -> sum
        Subgraph<PsiMethod> g = getExplorer().getCallChains(sum, ParentFactory.getInstance().createClass(sum));
        Set<PsiMethod> expected = getNodes(root, doWork, calculate, sum, anotherRoot);
        assertEquals(expected, g.getNodes());

        addEdge(root, doWork);
        addEdge(doWork, calculate);
        addEdge(calculate, sum);
        addEdge(anotherRoot, sum);
        
        assertEquals(getEdges(), g.getEdges());
    }

    public void testCycledCallChainsTo() throws Exception {
        setup();

        // anotherRoot -> sum
        // root-> doWork-> calculate -> sum
        // anotherRoot -> multiply
        // sum, multiply -> log
        PsiMethod sum = getMethod("sum");
        PsiMethod multiply = getMethod("multiply");
        PsiMethod log = getMethod("log");

        PsiMethod anotherRoot = getMethod("anotherRoot");
        PsiMethod root = getMethod("root");
        PsiMethod doWork = getMethod("doWork");
        PsiMethod calculate = getMethod("calculate");

        Subgraph<PsiMethod> g = getExplorer().getCallChains(log, ParentFactory.getInstance().createClass(log));
        Set<PsiMethod> expected = new HashSet<PsiMethod>();
        expected.add(sum);
        expected.add(multiply);
        expected.add(log);
        expected.add(anotherRoot);
        expected.add(root);
        expected.add(doWork);
        expected.add(calculate);
        assertEquals(expected, g.getNodes());

        addEdge(anotherRoot, sum);
        addEdge(root, doWork);
        addEdge(doWork, calculate);
        addEdge(calculate, sum);
        addEdge(anotherRoot, multiply);
        addEdge(sum, log);
        addEdge(multiply, log);

        assertEquals(getEdges(), g.getEdges());
    }

    public void testCallChainsToRoot() throws Exception { // one node only
        setup();

        PsiMethod anotherRoot = getMethod("anotherRoot");
        
        Subgraph<PsiMethod> g = getExplorer().getCallChains(anotherRoot, ParentFactory.getInstance().createClass(anotherRoot));
        assertEquals(getNodes(anotherRoot), g.getNodes());
        assertEmpty(g.getEdges());
    }

    public void testCallsIn() throws Exception {
        setup();

        PsiMethod root = getMethod("root");
        PsiMethod anotherRoot = getMethod("anotherRoot");
        PsiMethod doWork = getMethod("doWork");
        PsiMethod init = getMethod("init");
        PsiMethod calculate = getMethod("calculate");
        PsiMethod sum = getMethod("sum");
        PsiMethod multiply = getMethod("multiply");
        PsiMethod doMultiply = getMethod("doMultiply");
        PsiMethod factorial = getMethod("factorial");
        PsiMethod log = getMethod("log");

        Subgraph<PsiMethod> g = getExplorer().getCalls(ParentFactory.getInstance().createClass(log));
        Set<PsiMethod> expected = new HashSet<PsiMethod>();
        expected.add(sum);
        expected.add(multiply);
        expected.add(log);
        expected.add(anotherRoot);
        expected.add(root);
        expected.add(doWork);
        expected.add(calculate);
        expected.add(init);
        expected.add(doMultiply);
        expected.add(factorial);
        assertEquals(expected, g.getNodes());

        addEdge(root, doWork);
        addEdge(anotherRoot, init);
        addEdge(anotherRoot, multiply);
        addEdge(anotherRoot, sum);

        addEdge(doWork, init);
        addEdge(doWork, calculate);
        addEdge(calculate, sum);

        addEdge(sum, log);
        addEdge(multiply, doMultiply);
        addEdge(multiply, log);

        addEdge(factorial, factorial);

        assertEquals(getEdges(), g.getEdges());
    }

}
