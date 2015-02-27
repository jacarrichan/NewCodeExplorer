package com.handyedit.codeexplorer.math;

import java.util.Set;

/**
 * Directed graph (nodes and directed edges).
 *
 * @author Alexei Orishchenko
 */
public interface Graph<T> {

    /**
     * Endpoints of all edges that start at specified node.
     *
     * @param node graph node
     *
     * @return nodes connected to specified node.
     * Edge direction is from specified node to this edges.
     */
    Set<T> getFromEdgesEndpoints(T node);
}
