package com.handyedit.codeexplorer.math;

import com.intellij.psi.PsiMethod;

import java.util.Collection;

/**
 * Class for getting nodes and edges that connected to specified node.
 *
 * @author Alexei Orishchenko
 */
public interface DependencyWalker {

    /**
     * Returns all nodes that connected to specified node.
     *
     * @param method node
     * @return nodes
     */
    Collection<PsiMethod> getDependencies(PsiMethod method);

    /**
     * Returns all nodes that connected to specified node and corresponding edges between them.
     *
     * @param member node
     * @return nodes and edges (for example, all OUT edges and their endpoints)
     */
    Subgraph<PsiMethod> getDependenciesGraph(PsiMethod member);
}
