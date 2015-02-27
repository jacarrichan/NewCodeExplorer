package com.handyedit.codeexplorer.math;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Subgraph: sets of nodes and edges.
 * 
 * @author Alexei Orishchenko
 */
public class Subgraph<T> {
    private Set<Edge<T>> _edges;
    private Set<T> _nodes;

    public Subgraph() {
        _edges = new HashSet<Edge<T>>();
        _nodes = new HashSet<T>();
    }

    public Subgraph(Set<Edge<T>> edges, Set<T> nodes) {
        _edges = edges;
        _nodes = nodes;
    }

    public Set<Edge<T>> getEdges() {
        return _edges;
    }

    public Set<T> getNodes() {
        return _nodes;
    }

    public void addNode(T node) {
        _nodes.add(node);
    }

    public void addEdge(Edge<T> edge) {
        _edges.add(edge);
    }

    public void addNodes(Collection<T> nodes) {
        _nodes.addAll(nodes);
    }

    public void addEdges(Collection<Edge<T>> edges) {
        _edges.addAll(edges);
    }

    public void add(Subgraph<T> g) {
        addNodes(g.getNodes());
        addEdges(g.getEdges());
    }

    public Set<T> getLeaves() {
        HashSet<T> result = new HashSet<T>(_nodes);
        for (Edge<T> edge: _edges) {
            result.remove(edge.getFrom());
        }
        return result;
    }

    public Set<T> getRoots() {
        HashSet<T> result = new HashSet<T>(_nodes);
        for (Edge<T> edge: _edges) {
            result.remove(edge.getTo());
        }
        return result;
    }

    public void remove(Set<T> nodes) {
        _nodes.removeAll(nodes);
        removeEdges(nodes);
    }

    /**
     * Removes edges that start or end on specified nodes.
     *
     * @param nodes nodes
     */
    private void removeEdges(Set<T> nodes) {
        for (Iterator<Edge<T>> i = _edges.iterator(); i.hasNext(); ) {
            Edge<T> edge = i.next();
            if (nodes.contains(edge.getFrom()) || nodes.contains(edge.getTo())) {
                i.remove();
            }
        }
    }

    public void clear() {
        _edges.clear();
        _nodes.clear();
    }

    public void removeRootsByCondition(NodeCondition<T> removeCondition) {
        Set<T> roots = getRoots();
        Set<T> outsideRoots = getNodes(roots, removeCondition);
        while (!outsideRoots.isEmpty()) {
            remove(outsideRoots);
            roots = getRoots();
            outsideRoots = getNodes(roots, removeCondition);
        }
    }


    private Set<T> getNodes(Set<T> nodes, NodeCondition<T> condition) {
        Set<T> result = new HashSet<T>();
        for (T n : nodes) {
            if (condition.match(n)) {
                result.add(n);
            }
        }
        return result;
    }

    public void reverse() {
        for (Edge<T> edge: _edges) {
            edge.reverse();
        }
    }
}
