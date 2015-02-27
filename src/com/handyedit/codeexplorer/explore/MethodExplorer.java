package com.handyedit.codeexplorer.explore;

import com.handyedit.codeexplorer.explore.filter.MethodFilter;
import com.handyedit.codeexplorer.math.*;
import com.handyedit.codeexplorer.util.ProgressUtils;
import com.handyedit.codeexplorer.util.PsiUtils;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Class with methods for method dependencies and call chains analysis.
 * Also the class provides usage and call graph walkers.
 *
 * @author Alexei Orishchenko
 */
public class MethodExplorer {

    private MethodFilter _filter;

    private MethodExplorer(MethodFilter filter) {
        _filter = filter;
    }

    private MethodFilter getFilter() {
        return _filter;
    }

    /**
     * Returns method calls from the method within the method class.
     *
     * @param method method
     * @return calls graph
     */
    public Subgraph<PsiMethod> getStructure(PsiMethod method) {
        MethodParent unit = ParentFactory.getInstance().createClass(method);
        Subgraph<PsiMethod> g = GraphWalker.walk(new CallGraph(), method, createStopCondition(unit));
        g.reverse();
        Subgraph<PsiMethod> calls = getStructureWalker().getDependenciesGraph(method);
        g.add(calls);

        return g;
    }

    /**
     * Returns all methods in the unit and calls between them.
     *
     * @param unit class, package, module or project
     * @return calls graph
     */
    public Subgraph<PsiMethod> getCalls(MethodParent unit) {
        Collection<PsiClass> classes = unit.getClasses();

        Subgraph<PsiMethod> result = new Subgraph<PsiMethod>();
        int i = 0;
        for (PsiClass aClass: classes) {
            if (ProgressUtils.isCanceled()) {
                return null;
            }
            ProgressUtils.report(aClass.getName(), i++, classes.size());
            Subgraph<PsiMethod> classCalls = getCalls(aClass, unit);
            result.add(classCalls);
        }
        return result;
    }

    private Subgraph<PsiMethod> getCalls(PsiClass classInUnit, MethodParent unit) {
        DependencyWalker walker = getStructureWalker();

        Subgraph<PsiMethod> g = new Subgraph<PsiMethod>();
        PsiMethod[] methods = classInUnit.getMethods();
        for (PsiMethod node: methods) {
            g.addNode(node);
        }
        for (PsiMethod method: methods) {
            if (ProgressUtils.isCanceled()) {
                return null;
            }
            Subgraph<PsiMethod> deps = walker.getDependenciesGraph(method);
            for (PsiMethod node: deps.getNodes()) {
                if (unit.contains(node)) {
                    g.addNode(node);
                }
            }
            for (Edge<PsiMethod> edge: deps.getEdges()) {
                PsiMethod to = edge.getTo();
                if (unit.contains(to)) {
                    g.addEdge(edge);
                }
            }
        }
        return g;
    }

    /**
     * Returns call chains to the method within method parent.
     *
     * @param to method
     * @param unit class, package, module or project of selected method
     * @return call graph
     */
    public Subgraph<PsiMethod> getCallChains(PsiMethod to, MethodParent unit) {
        return getCallChains(to, createStopCondition(unit));
    }

    private OutsideParentCondition createStopCondition(MethodParent unit) {
        return new OutsideParentCondition(unit);
    }

    private Subgraph<PsiMethod> getCallChains(@NotNull final PsiMethod methodTo, GraphWalker.StopWalkCondition<PsiMethod> stopCondition) {
        return GraphWalker.walk(new UsageGraph(), methodTo, stopCondition, true);
    }

    /**
     * Returns calls chains from some unit to the method.
     *
     * @param to method
     * @param from unit (class, package, module)
     * @return call graph
     */
    public Subgraph<PsiMethod> getCallChainsFrom(@NotNull PsiMethod to, @NotNull MethodParent from) {
        Subgraph<PsiMethod> g = GraphWalker.walk(new UsageGraph(), to, new SecondStepInParentStopCondition(from), true);
        g.removeRootsByCondition(createStopCondition(from));

        return g;
    }

    private DependencyWalker getStructureWalker() {
        return new MethodCallWalker();
    }

    private DependencyWalker getUsageWalker() {
        return new MethodUsageWalker();
    }

    /**
     * Returns call or usage graph walker
     *
     * @param structure if true then return call walker else return usage walker
     *
     * @return object for getting called methods (method structure) or method usages.
     */
    public DependencyWalker getWalker(boolean structure) {
        return structure ? getStructureWalker() : getUsageWalker();
    }

    private static Subgraph<PsiMethod> createDependencies(PsiMethod member, Collection<PsiMethod> methods, boolean fromMember) {
        Set<Edge<PsiMethod>> deps = new HashSet<Edge<PsiMethod>>();
        for (PsiMethod method : methods) {
            deps.add(fromMember ? new Edge<PsiMethod>(member, method) : new Edge<PsiMethod>(method, member));
        }

        return new Subgraph<PsiMethod>(deps, new HashSet<PsiMethod>(methods));
    }

    private class MethodCallWalker implements DependencyWalker {
        public Collection<PsiMethod> getDependencies(PsiMethod method) {
            if (PsiUtils.isIntefaceMethod(method)) {
                if (!getFilter().match(method)) {
                    return new HashSet<PsiMethod>();
                } else {
                    return PsiUtils.getImplementations(method, getFilter());
                }
            }

            return PsiUtils.getMethodsCalledFrom(method, getFilter());
        }

        public Subgraph<PsiMethod> getDependenciesGraph(PsiMethod method) {
            return createDependencies(method, getDependencies(method), true);
        }
    }

    private class MethodUsageWalker implements DependencyWalker {
        public Collection<PsiMethod> getDependencies(PsiMethod method) {
            if (PsiUtils.isIntefaceMethod(method)) { // todo: usages of the library interface method?
                if (PsiUtils.isLibraryMethod(method)) {
                    return new HashSet<PsiMethod>();
                }
            }


            Set<PsiMethod> methods = PsiUtils.getUsages(method, getFilter());

            PsiMethod def = PsiUtils.getInterfaceMethod(method, getFilter());
            if (def != null) {
                methods.add(def);
            }
            return methods;
        }

        public Subgraph<PsiMethod> getDependenciesGraph(PsiMethod method) {
            return createDependencies(method, getDependencies(method), false);
        }
    }

    private class UsageGraph implements Graph<PsiMethod> {
        public Set<PsiMethod> getFromEdgesEndpoints(PsiMethod node) {
            Collection<PsiMethod> res = getUsageWalker().getDependencies(node);
            return new HashSet<PsiMethod>(res);
        }
    }

    private class CallGraph implements Graph<PsiMethod> {
        public Set<PsiMethod> getFromEdgesEndpoints(PsiMethod node) {
            Collection<PsiMethod> res = getStructureWalker().getDependencies(node);
            return new HashSet<PsiMethod>(res);
        }
    }

    public static MethodExplorer getInstance(MethodFilter filter) {
        return new MethodExplorer(filter != null ? filter : MethodFilter.EMPTY);
    }

    private static class OutsideParentCondition implements GraphWalker.StopWalkCondition<PsiMethod>, NodeCondition<PsiMethod> {
        private MethodParent _unit;

        private OutsideParentCondition(MethodParent unit) {
            _unit = unit;
        }

        public boolean stop(PsiMethod node, PsiMethod from) {
            return match(node);
        }

        public boolean match(PsiMethod node) {
            return node == null || !_unit.contains(node);
        }
    }

    private static class SecondStepInParentStopCondition implements GraphWalker.StopWalkCondition<PsiMethod> {

        private MethodParent _unit;

        private SecondStepInParentStopCondition(@NotNull MethodParent unit) {
            _unit = unit;
        }

        public boolean stop(PsiMethod node, PsiMethod fromNode) {
            if (node != null && fromNode != null) {
                return _unit.contains(node) && _unit.contains(fromNode);
            }
            return false;
        }
    }
}
