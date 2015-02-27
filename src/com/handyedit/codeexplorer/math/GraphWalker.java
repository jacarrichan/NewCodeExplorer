package com.handyedit.codeexplorer.math;

import com.handyedit.codeexplorer.util.ProgressUtils;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Methods for graph walking (determine what graph nodes are reachable from selected node if walk by graph edges).
 * Some graph nodes are stop nodes (can't walk from them even if they have OUT edges) -
 * determined by stop condition passed to the walk methods.
 *
 * @author Alexei Orishchenko
 */
public class GraphWalker {

    public static <T> Subgraph<T> walk(@NotNull Graph<T> g, @NotNull T from, StopWalkCondition<T> stopCondition) {
        return walk(g, from, stopCondition, false);
    }

    public static <T> Subgraph<T> walk(@NotNull Graph<T> g, @NotNull T from, StopWalkCondition<T> stopCondition, boolean progress) {
        Set<T> currentSources = new HashSet<T>();
        Set<T> reached = new HashSet<T>();
        Map<T, Set<T>> reachedFrom = new HashMap<T, Set<T>>();

        currentSources.add(from);

        do {
            if (progress) {
                if (ProgressUtils.isCanceled()) {
                    return new Subgraph<T>();
                }
                T node = currentSources.iterator().next();
                ProgressUtils.report(node.toString());
            }

            reached.addAll(currentSources);
            currentSources = walkAll(g, currentSources, reached, reachedFrom, stopCondition);
        } while (!currentSources.isEmpty());

        Subgraph<T> result = new Subgraph<T>();
        result.addNodes(reached);
        for (Map.Entry<T, Set<T>> entry : reachedFrom.entrySet()) {
            for (T dest : entry.getValue()) {
                T src = entry.getKey();
                if (reached.contains(src) && reached.contains(dest)) {
                    result.addEdge(new Edge<T>(src, dest));
                }
            }
        }

        return result;
    }

    private static <T> Set<T> walkAll(Graph<T> graph, Set<T> fromNodes, Set<T> reached, Map<T, Set<T>> reachedFrom, StopWalkCondition<T> stopCondition) {
        Set<T> result = new HashSet<T>();
        for (T from : fromNodes) {
            Set<T> newNodes = new HashSet<T>(graph.getFromEdgesEndpoints(from));
            if (stopCondition != null) {
                for (Iterator<T> i = newNodes.iterator(); i.hasNext();) {
                    T node = i.next();
                    if (stopCondition.stop(node, from)) {
                        i.remove();
                    }
                }
            }
            addReachedFromAll(reachedFrom, from, newNodes);
            newNodes.removeAll(reached);
            result.addAll(newNodes);
        }
        return result;
    }

    private static <T> void addReachedFromAll(Map<T, Set<T>> reachedFrom, T from, Set<T> newNodes) {
        if (reachedFrom != null) {
            for (T newNode : newNodes) {
                Set<T> fromNodes = reachedFrom.get(newNode);
                if (fromNodes == null) {
                    fromNodes = new HashSet<T>();
                    reachedFrom.put(newNode, fromNodes);
                }
                fromNodes.add(from);
            }
        }
    }

    /**
     * Condition for determining stop nodes.
     */
    public static interface StopWalkCondition<T> {
        boolean stop(T node, T from);
    }
}
