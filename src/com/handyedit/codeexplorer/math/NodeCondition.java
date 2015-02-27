package com.handyedit.codeexplorer.math;

/**
 * Node condition.
 *
 * @author Alexei Orishchenko
 */
public interface NodeCondition<T> {

    /**
     * Return true for nodes that meet conditions.
     * @param node node
     * @return true for nodes that meet conditions
     */
    boolean match(T node);
}
