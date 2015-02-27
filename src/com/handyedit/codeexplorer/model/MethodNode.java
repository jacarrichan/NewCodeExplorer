package com.handyedit.codeexplorer.model;

import com.handyedit.codeexplorer.CodeExplorerPlugin;
import com.handyedit.codeexplorer.explore.MethodExplorer;
import com.handyedit.codeexplorer.math.Edge;
import com.handyedit.codeexplorer.math.Subgraph;
import com.handyedit.codeexplorer.util.PsiUtils;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.SmartPointerManager;
import com.intellij.psi.SmartPsiElementPointer;
import com.intellij.util.Icons;

import javax.swing.*;
import java.util.*;

/**
 * Diagram node that represents a Java method.
 *
 * @author Alexei Orishchenko
 */
public class MethodNode {

    private SmartPsiElementPointer<PsiMethod> _method; // store method pointer in the model

    private MethodNode(PsiMethod method) {
        _method = SmartPointerManager.getInstance(method.getProject()).createSmartPsiElementPointer(method);
    }

    public String getName(boolean showClassName) {
        return PsiUtils.asString(getMethod(), showClassName);
    }

    public String getSource(boolean showBody) {
        return PsiUtils.asHTML(getMethod(), showBody);
    }

    public PsiMethod getMethod() {
        return _method.getElement();
    }

    public Icon getIcon() {
        PsiClass aClass = getMethod().getContainingClass();
        return aClass != null && aClass.isInterface() ? Icons.INTERFACE_ICON : Icons.METHOD_ICON;
    }

    public Subgraph<MethodNode> getDependencies(boolean structure) {
        PsiMethod method = getMethod();
        Project project = method.getProject();
        MethodExplorer explorer = MethodExplorer.getInstance(CodeExplorerPlugin.getInstance(project).getFilter());
        Subgraph<PsiMethod> dependencies = explorer.getWalker(structure).getDependenciesGraph(method);
        return getNodesGraph(dependencies);
    }

    public static Subgraph<MethodNode> getNodesGraph(Subgraph<PsiMethod> deps) {
        Subgraph<MethodNode> result = new Subgraph<MethodNode>();

        Map<PsiMethod, MethodNode> methodByNode = new HashMap<PsiMethod, MethodNode>();
        for (PsiMethod method: deps.getNodes()) {
            MethodNode node = create(method);
            result.addNode(node);
            methodByNode.put(method, node);
        }
        for (Edge<PsiMethod> edge: deps.getEdges()) {
            MethodNode from = methodByNode.get(edge.getFrom());
            if (from == null) {
                from = create(edge.getFrom());
            }
            MethodNode to = methodByNode.get(edge.getTo());
            if (to == null) {
                to = create(edge.getTo());
            }
            result.addEdge(new Edge<MethodNode>(from, to));
        }
        return result;
    }

    public int hashCode() {
        return _method.hashCode();
    }

    public boolean equals(Object obj) {
        if (obj != null && obj instanceof MethodNode) {
            MethodNode node = (MethodNode) obj;
            return getMethod().equals(node.getMethod());
        }
        return false;
    }

    public static MethodNode create(PsiMethod method) {
        return method != null ? new MethodNode(method) : null;
    }

    public static Set<PsiMethod> getMethods(Collection<MethodNode> nodes) {
        Set<PsiMethod> result = new HashSet<PsiMethod>();
        for (MethodNode node: nodes) {
            result.add(node.getMethod());
        }
        return result;
    }
}
