package com.handyedit.codeexplorer.test;

import com.handyedit.codeexplorer.explore.MethodExplorer;
import com.handyedit.codeexplorer.explore.ParentType;
import com.handyedit.codeexplorer.explore.filter.LibraryFilter;
import com.handyedit.codeexplorer.explore.filter.MethodFilter;
import com.handyedit.codeexplorer.explore.filter.NamePrefixFilter;
import com.handyedit.codeexplorer.explore.filter.ScopeFilter;
import com.intellij.psi.PsiMethod;
import test.*;
import test.util.ChildUtil;

import java.util.Collection;

/**
 * @author Alexei Orishchenko
 */
public class FilterTest extends BaseIdeaTestCase {

    public void testUsages() throws Exception {
        setup(Child.class);
        addFile(Parent.class);

        PsiMethod childMethod = getMethod("depTest");
        doTestWalker(childMethod, Parent.class, false);
    }

    public void testStructure() throws Exception {
        setup(Parent.class);
        addFile(Child.class);

        PsiMethod childMethod = getMethod("depTest");
        doTestWalker(childMethod, Child.class, true);
    }

    public void testInterfaceUsage() throws Exception {
        setup(SomeTest.class);
        addFile(ITest.class);

        PsiMethod method = getMethod("test");
        doTestWalker(method, ITest.class, false);
    }

    public void testInterfaceStructure() throws Exception {
        setup(ITest.class);
        addFile(SomeTest.class);

        PsiMethod method = getMethod("test");
        doTestWalker(method, SomeTest.class, true);
    }

    public void testScopeFilter() throws Exception {
        setup(Child.class);

        PsiMethod m = getMethod("drawChild");

        ScopeFilter filter = new ScopeFilter(Child.class.getName(), ParentType.CLASS);
        assertFalse(filter.match(m));
        filter = new ScopeFilter(Parent.class.getName(), ParentType.CLASS);
        assertTrue(filter.match(m));

        filter = new ScopeFilter(Child.class.getPackage().getName(), ParentType.PACKAGE);
        assertFalse(filter.match(m));
        filter = new ScopeFilter(ChildUtil.class.getPackage().getName(), ParentType.PACKAGE);
        assertTrue(filter.match(m));
    }

    public void testNameFilter() throws Exception {
        setup(Child.class);

        PsiMethod m = getMethod("drawTitle");

        MethodFilter filter = new NamePrefixFilter("draw");
        assertFalse(filter.match(m));
        filter = new NamePrefixFilter("test");
        assertTrue(filter.match(m));
    }

    public void testLibraryFilter() throws Exception {
        setup(Child.class);
//        PsiClass t = addFile(Thread.class);

        PsiMethod log = getMethod("log");
//        PsiMethod m = getMethod(t, "start");
//        assertNotNull(m);

        MethodFilter filter = LibraryFilter.INSTANCE;
//        assertFalse(filter.match(m));
        assertTrue(filter.match(log));
    }

    private void doTestWalker(PsiMethod childMethod, Class filterClass, boolean structure) {
        Collection<PsiMethod> g = getExplorer().getWalker(structure).getDependencies(childMethod);
        assertEquals(1, g.size());

        g = MethodExplorer.getInstance(createClassFilter(filterClass)).getWalker(structure).getDependencies(childMethod);
        assertEquals(0, g.size());
    }

    private ScopeFilter createClassFilter(Class aClass) {
        return new ScopeFilter(aClass.getCanonicalName(), ParentType.CLASS);
    }
}
