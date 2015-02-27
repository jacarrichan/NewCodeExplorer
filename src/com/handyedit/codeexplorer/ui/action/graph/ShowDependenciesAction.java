package com.handyedit.codeexplorer.ui.action.graph;

import com.handyedit.codeexplorer.CodeExplorerPlugin;
import com.handyedit.codeexplorer.math.Edge;
import com.handyedit.codeexplorer.math.Subgraph;
import com.handyedit.codeexplorer.model.DependencyModel;
import com.handyedit.codeexplorer.model.MethodNode;
import com.handyedit.codeexplorer.res.CodeExplorerBundle;
import com.handyedit.codeexplorer.util.ActionUtils;
import com.handyedit.codeexplorer.util.GraphUtils;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.graph.builder.GraphBuilder;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Adds selected method dependencies (its usages or calls from it) to the graph.
 *
 * @author Alexei Orishchenko
 */
public class ShowDependenciesAction extends AnAction {

    public void actionPerformed(AnActionEvent e) { // called by shortcut
        GraphBuilder<MethodNode, Edge> builder = ActionUtils.getBuilder(e);

        Set<MethodNode> selected = GraphUtils.getSelected(builder);
        if (selected == null || selected.isEmpty()) {
            return;
        }
        if (expandDependencies(builder, selected)) {
            GraphUtils.scrollTo(builder, selected.iterator().next());
        }
    }

    private boolean expandDependencies(@NotNull GraphBuilder builder, Set<MethodNode> selected) {
        boolean result = false;
        for (MethodNode member: selected) {
            result |= expandDependencies(builder, member);
        }
        return result;
    }

    public boolean expandDependencies(@NotNull GraphBuilder builder, MethodNode member) {
        boolean isStructure = isStructure(member.getMethod().getProject());
        return expandDependencies(builder, member, isStructure);
    }

    protected boolean isStructure(Project project) {
        return CodeExplorerPlugin.getSettings(project).isStructureByClick();
    }

    private boolean expandDependencies(@NotNull GraphBuilder builder, MethodNode method, boolean structure) {
        DependencyModel model = (DependencyModel) builder.getGraphDataModel();
        if (model.isExplored(method, structure)) {
            return false;
        }


        Subgraph<MethodNode> dependencies = method.getDependencies(structure);
        boolean update = false;
        if (model.addDependencies(dependencies)) {
            update = true;
        }

        if (!model.isExplored(method, structure)) {
            model.addExplored(method, structure);
            update = true;
        }
        if (update) {
            builder.updateGraph();
        }

        return true;
    }

    public void update(AnActionEvent e) {
        super.update(e);
        boolean structure = isStructure(ActionUtils.getProject(e));
        String key = structure ? "show-structure" : "show-usages";
        e.getPresentation().setText(CodeExplorerBundle.message(key));
    }
}
