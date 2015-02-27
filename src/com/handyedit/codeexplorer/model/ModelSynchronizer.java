package com.handyedit.codeexplorer.model;

import com.handyedit.codeexplorer.CodeExplorerPlugin;
import com.handyedit.codeexplorer.math.Subgraph;
import com.intellij.psi.PsiMethod;

import java.util.Collection;
import java.util.Set;

/**
 * Updates model (changes nodes statuses or adds new nodes and edges to the diagram) by sources.
 *
 * @author Alexei Orishchenko
 */
public class ModelSynchronizer {

    /**
     * Updates node statuses (explored method calls or usages): 
     * method calls (usages) for method node are explored if method calls (usages) on diagram and in source are same.
     *
     * @param model graph model
     */
    public static void updateNodeStatuses(DependencyModel model) {
        updateNodeStatuses(model, true);
        updateNodeStatuses(model, false);
    }

    private static void updateNodeStatuses(DependencyModel model, boolean structure) {
        for(MethodNode node: model.getNodes()) {
            PsiMethod method = node.getMethod();
            Collection<PsiMethod> sourceMethods = CodeExplorerPlugin.getExplorer(method).getWalker(structure).getDependencies(method);
            Set<PsiMethod> graphMethods = MethodNode.getMethods(structure ? model.getCalls(node) : model.getUsages(node));
            if (sourceMethods.equals(graphMethods)) {
                model.addExplored(node, structure);
            }
        }
    }

    /**
     * Updates model by current sources: adds method calls for method nodes with explored method calls,
     * usages - for method nodes with explored usages.
     *
     * @param model graph model
     */
    public static void updateExploredNodes(DependencyModel model) {
        updateExploredNodes(model, true);
        updateExploredNodes(model, false);
    }

    private static void updateExploredNodes(DependencyModel model, boolean structure) {
        Set<MethodNode> nodes = model.getExplored(structure);
        for (MethodNode node: nodes) {
            PsiMethod method = node.getMethod();
            Subgraph<PsiMethod> deps = CodeExplorerPlugin.getExplorer(method).getWalker(structure).getDependenciesGraph(method);
            model.addDependencies(MethodNode.getNodesGraph(deps));
        }
    }
}
