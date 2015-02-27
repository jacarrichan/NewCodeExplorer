package com.handyedit.codeexplorer.test;

import com.handyedit.codeexplorer.math.Graph;
import com.handyedit.codeexplorer.math.GraphWalker;
import com.handyedit.codeexplorer.math.Subgraph;
import com.handyedit.codeexplorer.math.Edge;
import junit.framework.TestCase;

import java.util.*;

/**
 * Graph walking tests.
 *
 * @author Alexei Orishchenko
 */
public class GraphTest extends TestCase {

    private SimpleGraph<Integer> createGraph() {
        SimpleGraph<Integer> graph = new SimpleGraph<Integer>();
        graph.add(1, 2, 3);
        graph.add(2, 3);
        graph.add(3, 4);
        graph.add(5, 6);
        graph.add(4, 7, 8);
        graph.add(8, 9);
        return graph;
    }

    public void testWalk() {
        SimpleGraph<Integer> graph = createGraph();

        Subgraph<Integer> res = GraphWalker.walk(graph, 1, null);
        assertEquals(toSet(1, 2, 3, 4, 7, 8, 9), res.getNodes());
        SimpleGraph<Integer> e = createGraph();
        e.removeEdge(5, 6);
        assertEquals(e.getEdges(), res.getEdges());

        res = GraphWalker.walk(graph, 2, null);
        assertEquals(toSet(2, 3, 4, 7, 8, 9), res.getNodes());
        e.removeEdge(1, 2);
        e.removeEdge(1, 3);
        assertEquals(e.getEdges(), res.getEdges());

        res = GraphWalker.walk(graph, 3, null);
        assertEquals(toSet(3, 4, 7, 8, 9), res.getNodes());
        e.removeEdge(2, 3);
        assertEquals(e.getEdges(), res.getEdges());

        res = GraphWalker.walk(graph, 4, null);
        assertEquals(toSet(4, 7, 8, 9), res.getNodes());
        e.removeEdge(3, 4);
        assertEquals(e.getEdges(), res.getEdges());

        res = GraphWalker.walk(graph, 7, null);
        assertEquals(toSet(7), res.getNodes());
        assertEquals(Collections.emptySet(), res.getEdges());

        res = GraphWalker.walk(graph, 8, null);
        assertEquals(toSet(8, 9), res.getNodes());
        assertEquals(Collections.singleton(new Edge<Integer>(9, 8)), res.getEdges());

        res = GraphWalker.walk(graph, 9, null);
        assertEquals(toSet(9), res.getNodes());
        assertEquals(Collections.emptySet(), res.getEdges());

        res = GraphWalker.walk(graph, 5, null);
        assertEquals(toSet(5, 6), res.getNodes());
        assertEquals(Collections.singleton(new Edge<Integer>(6, 5)), res.getEdges());

        res = GraphWalker.walk(graph, 6, null);
        assertEquals(toSet(6), res.getNodes());
        assertEquals(Collections.emptySet(), res.getEdges());
    }

    private static Set<Integer> toSet(Integer... nums) {
        Set<Integer> res = new HashSet<Integer>();
        for (Integer i: nums) {
            res.add(i);
        }
        return res;
    }

    private static class SimpleGraph<T> implements Graph<T> {
        private Map<T, Set<T>> _endpoints = new HashMap<T, Set<T>>();

        public void add(T from, T... to) {
            Set<T> endpoints = _endpoints.get(from);
            if (endpoints == null) {
                endpoints = new HashSet<T>();
                _endpoints.put(from, endpoints);
            }
            endpoints.addAll(Arrays.asList(to));
        }

        public Set<T> getFromEdgesEndpoints(T node) {
            Set<T> res = _endpoints.get(node);
            return res != null ? res : new HashSet<T>();
        }

        public Set<Edge<T>> getEdges() {
            Set<Edge<T>> res = new HashSet<Edge<T>>();
            for (Map.Entry<T, Set<T>> entry: _endpoints.entrySet()) {
                for (T to: entry.getValue()) {
                    res.add(new Edge<T>(to, entry.getKey()));
                }
            }
            return res;
        }

        public void removeEdge(T from, T to) {
            _endpoints.get(from).remove(to);
        }
    }
}
