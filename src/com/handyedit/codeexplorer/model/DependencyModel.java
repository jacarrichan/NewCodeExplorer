package com.handyedit.codeexplorer.model;

import com.handyedit.codeexplorer.math.Edge;
import com.handyedit.codeexplorer.math.Subgraph;
import com.intellij.openapi.graph.builder.CachedGraphDataModel;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Graph model : methods and their dependencies.
 *
 * @author Alexei Orishchenko
 */
public class DependencyModel extends CachedGraphDataModel<MethodNode, Edge> {

    private Set<MethodNode> _exploredStructure = new HashSet<MethodNode>();
    private Set<MethodNode> _exploredUsages = new HashSet<MethodNode>();

    public DependencyModel() {
    }

    public DependencyModel(Subgraph<MethodNode> deps) {
        addNodesEdges(deps.getNodes(), deps.getEdges());
    }

    public void addExplored(MethodNode node, boolean structure) {
        if (node == null) {
            return;
        }

        if (structure) {
            _exploredStructure.add(node);
        } else {
            _exploredUsages.add(node);
        }
    }

    public boolean isExplored(MethodNode node, boolean structure) {
        return getExplored(structure).contains(node);
    }

    public Set<MethodNode> getExplored(boolean structure) {
        return structure ? _exploredStructure : _exploredUsages;
    }

    protected void buildGraph() {
    }

    private void addNodesEdges(Set<MethodNode> nodes, Set<Edge<MethodNode>> edges) {
        for (MethodNode node: nodes) {
            add(node);
        }
        for (Edge<MethodNode> edge: edges) {
            add(edge);
        }
    }

    public void add(MethodNode method) {
        createNode(method);
    }

    public void add(Edge<MethodNode> edge) {
        createEdge(edge, edge.getFrom(), edge.getTo());
    }

    public void remove(MethodNode method) {
        myNodes.remove(method);
    }

    public void remove(Edge edge) {
        myEdges.remove(edge);
    }

    public void remove(Collection<Edge<MethodNode>> edges) {
        for (Edge<MethodNode> edge: edges) {
            remove(edge);
        }
    }

    public Set<Edge<MethodNode>> getEdges(MethodNode node) {
        Set<Edge<MethodNode>> result = new HashSet<Edge<MethodNode>>();
        for (Edge<MethodNode> edge: getEdges()) {
            if (edge.getFrom().equals(node) || edge.getTo().equals(node)) {
                result.add(edge);
            }
        }
        return result;
    }

    public void addDependencies(Subgraph<MethodNode> deps, MethodNode exploredNode) {
        if (exploredNode != null) {
            _exploredStructure.add(exploredNode);
            deps.addNode(exploredNode);
        }
        addDependencies(deps);
    }

    public boolean addDependencies(Subgraph<MethodNode> deps) {
        Set<MethodNode> newClasses = new HashSet<MethodNode>(deps.getNodes());
        newClasses.removeAll(getNodes());


        Set<Edge<MethodNode>> newDependencies = new HashSet<Edge<MethodNode>>(deps.getEdges());
        newDependencies.removeAll(getEdges());

        if (newClasses.isEmpty() && newDependencies.isEmpty()) {
            return false;
        }

        addNodesEdges(newClasses, newDependencies);

        return true;
    }

    @NotNull
    public String getNodeName(MethodNode node) { // legacy method
        return "";
    }

    @NotNull
    public String getEdgeName(Edge dep) {
        return dep.getLabel();
    }

    public void clear() {
        super.clear();
        _exploredStructure.clear();
        _exploredUsages.clear();
    }

    public MethodNode getNode(PsiMethod method) {
        for (MethodNode node: getNodes()) {
            if (node.getMethod().equals(method)) {
                return node;
            }
        }
        return null;
    }

    public boolean contains(Edge edge) {
        for (Edge e: getEdges()) {
            if (edge.equals(e)) {
                return true;
            }
        }
        return false;
    }

    public Set<Edge<MethodNode>> getEdgesFrom(MethodNode node) {
        Set<Edge<MethodNode>> result = new HashSet<Edge<MethodNode>>();
        for (Edge<MethodNode> edge: getEdges()) {
            if (edge.getFrom().equals(node)) {
                result.add(edge);
            }
        }
        return result;
    }

    public Set<MethodNode> getCalls(MethodNode node) {
        Set<MethodNode> result = new HashSet<MethodNode>();
        for (Edge<MethodNode> edge: getEdges()) {
            if (edge.getFrom().equals(node)) {
                result.add(edge.getTo());
            }
        }
        return result;
    }

    public Set<MethodNode> getUsages(MethodNode node) {
        Set<MethodNode> result = new HashSet<MethodNode>();
        for (Edge<MethodNode> edge: getEdges()) {
            if (edge.getTo().equals(node)) {
                result.add(edge.getFrom());
            }
        }
        return result;
    }
}
