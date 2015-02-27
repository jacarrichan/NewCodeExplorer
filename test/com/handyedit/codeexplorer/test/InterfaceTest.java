package com.handyedit.codeexplorer.test;

import test.Action;
import test.SomeAction;
import test.ActionTest;
import test.util.ChildUtil;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.handyedit.codeexplorer.math.Subgraph;
import com.handyedit.codeexplorer.util.PsiUtils;
import com.handyedit.codeexplorer.explore.ParentFactory;
import com.handyedit.codeexplorer.explore.ParentType;

import java.util.Set;

/**
 * Tests for calls through interface, interface method usages and structure.
 *
 * Notes:
 * interface method structure = methods that implement the interface method
 * call through interface = interface method usage
 * direct call = interface method usage + implementation usage
 *
 * @author Alexei Orishchenko
 */
public class InterfaceTest extends BaseIdeaTestCase {

    public void testInterfaceStructure() throws Exception {
        setup(Action.class);
        PsiClass someAction = addFile(SomeAction.class);

        PsiMethod execute = getMethod("execute");
        PsiMethod executeImpl = getMethod(someAction, "execute");

        Subgraph<PsiMethod> g = getExplorer().getStructure(execute);
        assertEquals(getNodes(execute, executeImpl), g.getNodes());
        addEdge(execute, executeImpl);
        assertEquals(getEdges(), g.getEdges());
    }

    public void testInterfaceUsage() throws Exception {
        setup(Action.class);
        PsiClass actionTest = addFile(ActionTest.class);
        PsiClass childUtil = addFile(ChildUtil.class);
        addFile(SomeAction.class);

        PsiMethod execute = getMethod("execute");

        Set<PsiMethod> usages = PsiUtils.getUsages(execute);

        PsiMethod interfaceCall = getMethod(actionTest, "interfaceCall");
        PsiMethod interfaceCallUtil = getMethod(childUtil, "interfaceCall");

        PsiMethod directCall = getMethod(actionTest, "directCall");
        PsiMethod directCallUtil = getMethod(childUtil, "directCall");

        assertEquals(getNodes(interfaceCall, interfaceCallUtil, directCall, directCallUtil), usages);
    }

    public void testDirectUsage() throws Exception {
        setup(Action.class);
        PsiClass someAction = addFile(SomeAction.class);
        PsiClass actionTest = addFile(ActionTest.class);
        PsiClass childUtil = addFile(ChildUtil.class);

        PsiMethod executeImpl = getMethod(someAction, "execute");

        Set<PsiMethod> usages = PsiUtils.getUsages(executeImpl);

        PsiMethod directCall = getMethod(actionTest, "directCall");
        PsiMethod directCallUtil = getMethod(childUtil, "directCall");

        assertEquals(getNodes(directCall, directCallUtil), usages);
    }

    public void testPackageCallChainsInterface() throws Exception {
        setup(Action.class);
        PsiClass actionTest = addFile(ActionTest.class);
        addFile(SomeAction.class);

        PsiMethod execute = getMethod("execute");

        PsiMethod interfaceCall = getMethod(actionTest, "interfaceCall");
        PsiMethod directCall = getMethod(actionTest, "directCall");

        Subgraph<PsiMethod> g = getExplorer().getCallChains(execute, ParentFactory.getInstance().createPackage(execute));
        assertEquals(getNodes(execute, interfaceCall, directCall), g.getNodes());

        addEdge(interfaceCall, execute);
        addEdge(directCall, execute);
        assertEquals(getEdges(), g.getEdges());
    }

    public void testModuleCallChainsInterface() throws Exception {
        doTestInterface(ParentType.MODULE);
    }

    public void testProjectCallChainsInterface() throws Exception {
        doTestInterface(ParentType.PROJECT);
    }

    private void doTestInterface(ParentType type) throws Exception {
        setup(Action.class);
        PsiClass actionTest = addFile(ActionTest.class);
        PsiClass childUtil = addFile(ChildUtil.class);
        addFile(SomeAction.class);

        PsiMethod execute = getMethod("execute");

        PsiMethod interfaceCall = getMethod(actionTest, "interfaceCall");
        PsiMethod interfaceCallUtil = getMethod(childUtil, "interfaceCall");

        PsiMethod directCall = getMethod(actionTest, "directCall");
        PsiMethod directCallUtil = getMethod(childUtil, "directCall");

        Subgraph<PsiMethod> g = getExplorer().getCallChains(execute, ParentFactory.getInstance().getParent(execute, type));
        assertEquals(getNodes(execute, interfaceCall, interfaceCallUtil, directCall, directCallUtil), g.getNodes());

        addEdge(interfaceCall, execute);
        addEdge(interfaceCallUtil, execute);
        addEdge(directCall, execute);
        addEdge(directCallUtil, execute);
        assertEquals(getEdges(), g.getEdges());
    }

    public void testPackageCallChainsDirect() throws Exception {
        setup(Action.class);
        PsiClass someAction = addFile(SomeAction.class);
        PsiClass actionTest = addFile(ActionTest.class);

        PsiMethod execute = getMethod("execute");
        PsiMethod executeImpl = getMethod(someAction, "execute");

        PsiMethod interfaceCall = getMethod(actionTest, "interfaceCall");
        PsiMethod directCall = getMethod(actionTest, "directCall");

        Subgraph<PsiMethod> g = getExplorer().getCallChains(executeImpl, ParentFactory.getInstance().createPackage(executeImpl));
        assertEquals(getNodes(execute, executeImpl, interfaceCall, directCall), g.getNodes());

        addEdge(execute, executeImpl);
        addEdge(interfaceCall, execute);
        addEdge(directCall, executeImpl);
        addEdge(directCall, execute);
        assertEquals(getEdges(), g.getEdges());
    }

    public void testModuleCallChainsDirect() throws Exception {
        doTestDirect(ParentType.MODULE);
    }

    public void testProjectCallChainsDirect() throws Exception {
        doTestDirect(ParentType.PROJECT);
    }

    private void doTestDirect(ParentType type) throws Exception {
        setup(Action.class);
        PsiClass someAction = addFile(SomeAction.class);
        PsiClass actionTest = addFile(ActionTest.class);
        PsiClass childUtil = addFile(ChildUtil.class);

        PsiMethod execute = getMethod("execute");
        PsiMethod executeImpl = getMethod(someAction, "execute");

        PsiMethod interfaceCall = getMethod(actionTest, "interfaceCall");
        PsiMethod interfaceCallUtil = getMethod(childUtil, "interfaceCall");

        PsiMethod directCall = getMethod(actionTest, "directCall");
        PsiMethod directCallUtil = getMethod(childUtil, "directCall");

        Subgraph<PsiMethod> g = getExplorer().getCallChains(executeImpl, ParentFactory.getInstance().getParent(executeImpl, type));
        assertEquals(getNodes(execute, executeImpl, interfaceCall, directCall, interfaceCallUtil, directCallUtil), g.getNodes());

        addEdge(execute, executeImpl);

        addEdge(interfaceCall, execute);
        addEdge(directCall, executeImpl);
        addEdge(directCall, execute);

        addEdge(interfaceCallUtil, execute);
        addEdge(directCallUtil, executeImpl);
        addEdge(directCallUtil, execute);
        
        assertEquals(getEdges(), g.getEdges());
    }
}
