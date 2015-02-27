package com.handyedit.codeexplorer.test;

import com.handyedit.codeexplorer.explore.MethodExplorer;
import com.handyedit.codeexplorer.math.Edge;
import com.handyedit.codeexplorer.util.PsiUtils;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.CharsetToolkit;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.testFramework.LightCodeInsightTestCase;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Base lightweight Idea test.
 *
 * @author Alexei Orishchenko
 *
 * IMPORTANT. Side effect:
 * a test method sees ALL test project classes that are added by previous test methods.
 */
public abstract class BaseIdeaTestCase extends LightCodeInsightTestCase {
    private static final File SOURCE_ROOT = new File("e:/projects/CodeExplorer/testProject");

    protected PsiClass _class;

    protected PsiClass addFile(Class aClass) throws IOException {
        String name = getFileName(aClass);
        VirtualFile vfile = addFile(getSourceRoot(), name);
        vfile.setCharset(CharsetToolkit.UTF8_CHARSET);
        File file = new File(SOURCE_ROOT, name);
        String fileText = new String(FileUtil.loadFileText(file, CharsetToolkit.UTF8));
        VfsUtil.saveText(vfile, fileText);

        PsiFile psiFile = getPsiManager().findFile(vfile);
        return PsiUtils.getTopLevelClass(psiFile);
    }

    private VirtualFile addFile(VirtualFile parent, String path) throws IOException {
        int pos = path.indexOf("/");
        if (pos == -1) {
            return parent.createChildData(null, path);
        } else {
            VirtualFile f = parent.createChildDirectory(null, path.substring(0, pos));
            return addFile(f, path.substring(pos + 1));
        }
    }

    protected PsiMethod getMethod(String name) {
        return getMethod(_class, name);
    }

    protected PsiMethod getMethod(PsiClass aClass, String name) {
        return aClass.findMethodsByName(name, false)[0];
    }

    protected MethodExplorer getExplorer() {
        return MethodExplorer.getInstance(null);
    }

    protected void setup(Class aClass) throws Exception {
        _class = addFile(aClass);
    }

    private String getFileName(Class aClass) {
        return aClass.getCanonicalName().replace(".", "/") + ".java";
    }

    protected Set<PsiMethod> getNodes(PsiMethod... nodes) {
        Set<PsiMethod> res = new HashSet<PsiMethod>();
        for (PsiMethod node: nodes) {
            res.add(node);
        }
        return res;
    }

    private Set<Edge<PsiMethod>> _edges;

    protected void setUp() throws Exception {
        super.setUp();
        clearEdges();
    }

    protected void clearEdges() {
        _edges = new HashSet<Edge<PsiMethod>>();
    }

    protected void addEdge(PsiMethod from, PsiMethod to) {
        _edges.add(new Edge<PsiMethod>(from, to));
    }

    protected Set<Edge<PsiMethod>> getEdges() {
        return _edges;
    }
}
