package com.handyedit.codeexplorer.ui.action.analyze;

import com.handyedit.codeexplorer.CodeExplorerPlugin;
import com.handyedit.codeexplorer.explore.MethodExplorer;
import com.handyedit.codeexplorer.explore.filter.CompositeFilter;
import com.handyedit.codeexplorer.math.Edge;
import com.handyedit.codeexplorer.math.Subgraph;
import com.handyedit.codeexplorer.model.DependencyModel;
import com.handyedit.codeexplorer.model.MethodNode;
import com.handyedit.codeexplorer.util.ActionUtils;
import com.handyedit.codeexplorer.util.GraphUtils;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.graph.builder.GraphBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;

/**
 * Base action that shows a graph in a new CodeExplorer tab (analyze action).
 *
 * @author Alexei Orishchenko
 */
abstract class BaseAnalyzeAction extends AnAction {

    /**
     * Opens the graph in new CodeExplorer tab.
     * called from UI thread: AnAction.actionPerformed()
     *
     * @param project project
     * @param deps graph to show in the tab
     * @param title tab title
     */
    protected void openGraph(Project project, Subgraph<PsiMethod> deps, String title) {
        CodeExplorerPlugin.getInstance(project).openGraph(deps, null, null, title);
    }

    /**
     * Opens the graph in new CodeExplorer tab.
     * called from UI thread: AnAction.actionPerformed()
     *
     * @param project project
     * @param deps graph to show in the tab
     * @param exploredMethod method with explored structure
     * @param selectMethod method to select
     */
    private void openGraph(Project project, Subgraph<PsiMethod> deps, PsiMethod exploredMethod, PsiMethod selectMethod) {
        CodeExplorerPlugin.getInstance(project).openGraph(deps, exploredMethod, selectMethod, null);
    }

    protected void openAddGraph(Project project, Context context, Subgraph<PsiMethod> deps, PsiMethod methodExploredStructure, PsiMethod methodSelect) {
        if (deps == null) {
            return;
        }

        if (context.isOpenNew()) {
            openGraph(project, deps, methodExploredStructure, methodSelect);
        } else {
            addGraph(context.getBuilder(), deps, methodExploredStructure);
        }
    }

    private void addGraph(GraphBuilder<MethodNode, Edge> builder, Subgraph<PsiMethod> deps, PsiMethod methodExploredStructure) {
        DependencyModel model = (DependencyModel) builder.getGraphDataModel();
        model.addDependencies(MethodNode.getNodesGraph(deps), MethodNode.create(methodExploredStructure));
        builder.updateGraph();
    }

    protected MethodExplorer getExplorer(PsiMethod method) {
        return getExplorer(method.getProject());
    }
    
    protected MethodExplorer getExplorer(Project project) {
        CompositeFilter filter = CodeExplorerPlugin.getInstance(project).getFilter();
        return MethodExplorer.getInstance(filter);
    }

    protected Context getContext(AnActionEvent e) {
        GraphBuilder<MethodNode, Edge> builder = ActionUtils.getBuilder(e);
        MethodNode selected = GraphUtils.getSingleSelected(builder);
        if (selected != null) {
            return new Context(selected.getMethod(), builder);
        } else {
            PsiMethod method = ActionUtils.getMethodAtCursor(e);
            if (method != null) {
                return new Context(method);
            }
        }
        return null;
    }

    protected static class Context {
        private PsiMethod _method;
        private GraphBuilder<MethodNode, Edge> _builder;

        public Context(@NotNull PsiMethod method, GraphBuilder<MethodNode, Edge> builder) {
            _method = method;
            _builder = builder;
        }

        public Context(@NotNull PsiMethod method) {
            _method = method;
        }

        @NotNull
        public PsiMethod getMethod() {
            return _method;
        }

        public GraphBuilder<MethodNode, Edge> getBuilder() {
            return _builder;
        }

        public boolean isOpenNew() {
            return _builder == null;
        }
    }
}
