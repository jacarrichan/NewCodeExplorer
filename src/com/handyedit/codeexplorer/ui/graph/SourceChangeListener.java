package com.handyedit.codeexplorer.ui.graph;

import com.handyedit.codeexplorer.CodeExplorerPlugin;
import com.handyedit.codeexplorer.explore.filter.LibraryFilter;
import com.handyedit.codeexplorer.explore.filter.MethodFilter;
import com.handyedit.codeexplorer.math.Edge;
import com.handyedit.codeexplorer.model.DependencyModel;
import com.handyedit.codeexplorer.model.MethodNode;
import com.handyedit.codeexplorer.util.PsiUtils;
import com.intellij.openapi.graph.builder.GraphBuilder;
import com.intellij.psi.*;

import java.util.Collection;
import java.util.List;

/**
 * @author Alexei Orishchenko
 */
public class SourceChangeListener extends PsiTreeChangeAdapter {

    private DependencyModel _model;
    private GraphBuilder _builder;

    public SourceChangeListener(GraphBuilder builder) {
        _builder = builder;
        _model = (DependencyModel) _builder.getGraphDataModel();
    }

    public void beforeChildRemoval(PsiTreeChangeEvent event) {
        PsiElement elem = event.getChild();
        List<PsiMethodCallExpression> calls = PsiUtils.getCalls(elem, getFilter());
        List<PsiMethod> methods = PsiUtils.getMethods(elem);

        for (PsiMethod method: methods) {
            MethodNode methodNode = _model.getNode(method);
            if (methodNode != null) {
                _model.remove(methodNode);
                _builder.queueUpdate();
            }
        }

        for (PsiMethodCallExpression call: calls) {
            PsiMethod parent = PsiUtils.getContainingMethod(call);
            PsiMethod method = call.resolveMethod();
            if (method == null) {
                return;
            }
            Edge<PsiMethod> edge = new Edge<PsiMethod>(parent, method);
            if (_model.contains(edge)) {
                _model.remove(edge);
                _builder.queueUpdate();
            }
        }
    }

    private MethodNode add(PsiMethod method) {
        MethodNode node = MethodNode.create(method);
        _model.add(node);
        return node;
    }

    public void childAdded(PsiTreeChangeEvent event) {
        PsiElement elem = event.getChild();
        List<PsiMethodCallExpression> calls = PsiUtils.getCalls(elem, getFilter());
        List<PsiMethod> methods = PsiUtils.getMethods(elem);

        for (PsiMethodCallExpression call: calls) {
            PsiMethod parent = PsiUtils.getContainingMethod(call);
            PsiMethod child = call.resolveMethod();
            if (child == null) {
                return;
            }
            MethodNode parentNode = _model.getNode(parent);
            MethodNode childNode = _model.getNode(child);
            boolean parentStructureExplored = parentNode != null && _model.isExplored(parentNode, true);
            boolean childUsagesExplored = childNode != null && _model.isExplored(childNode, false);
            if (parentStructureExplored || childUsagesExplored) {
                if (addEdge(parent, child)) {
                    _builder.queueUpdate();
                }
            }
        }
        for (PsiMethod method: methods) {
            if (_model.getNode(method) != null) {
                continue;
            }

            Collection<PsiMethod> usages = CodeExplorerPlugin.getExplorer(method).getWalker(false).getDependencies(method);
            for (PsiMethod usage: usages) {
                MethodNode usageNode = _model.getNode(usage);
                if (usageNode != null && _model.isExplored(usageNode, true)) {
                    addEdge(usage, method);
                    _builder.queueUpdate();
                }
            }
        }
    }

    private boolean addEdge(PsiMethod from, PsiMethod to) {
        boolean result = false;

        MethodNode fromNode = _model.getNode(from);
        if (fromNode == null) {
            fromNode = add(from);
            result = true;
        }
        MethodNode toNode = _model.getNode(to);
        if (toNode == null) {
            toNode = add(to);
            result = true;
        }
        Edge<MethodNode> edge = new Edge<MethodNode>(fromNode, toNode);
        if (!_model.contains(edge)) {
            _model.add(edge);
            result = true;
        }
        return result;
    }

    public void beforeChildReplacement(PsiTreeChangeEvent event) {
    }

    public void childReplaced(PsiTreeChangeEvent event) {
    }

    private MethodFilter getFilter() {
        return LibraryFilter.INSTANCE;
    }
}
