package com.handyedit.codeexplorer.ui.graph;

import com.handyedit.codeexplorer.CodeExplorerPlugin;
import com.handyedit.codeexplorer.CodeExplorerSettings;
import com.handyedit.codeexplorer.math.Edge;
import com.handyedit.codeexplorer.model.DependencyModel;
import com.handyedit.codeexplorer.model.MethodNode;
import com.handyedit.codeexplorer.res.CodeExplorerBundle;
import com.handyedit.codeexplorer.ui.action.graph.ShowDependenciesAction;
import com.handyedit.codeexplorer.util.GraphUtils;
import com.handyedit.codeexplorer.util.PsiUtils;
import com.handyedit.codeexplorer.util.StringUtils;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.graph.base.Graph;
import com.intellij.openapi.graph.base.Node;
import com.intellij.openapi.graph.builder.DeleteProvider;
import com.intellij.openapi.graph.builder.components.BasicGraphPresentationModel;
import com.intellij.openapi.graph.builder.util.GraphViewUtil;
import com.intellij.openapi.graph.view.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * Graph presentation (edit / select node listeners, disable editing)
 *
 * @author Alexei Orishchenko
 */
public class DependencyPresentationModel extends BasicGraphPresentationModel<MethodNode, Edge> {

    private static final String RENDERER_NAME = "CodeExplorer.MethodRenderer";

    private NodeCellRenderer _renderer;

    public DependencyPresentationModel(Graph graph) {
        super(graph);

        Graph2D g = (Graph2D) graph;
        g.addGraph2DSelectionListener(new ShowSourceSelectionListener());
    }

    public DefaultActionGroup getCommonActionGroup() {
        return ActionFactory.createContextMenuActions(getGraphBuilder());
    }

    public String getNodeTooltip(@Nullable MethodNode method) {
        if (method != null) {
            CodeExplorerSettings s = CodeExplorerPlugin.getSettings(getGraphBuilder().getProject());
            String source = method.getSource(s.isShowMethodBody());
            source = addTooltip(source, method);
            return StringUtils.toHTLM(source);
        }
        return null;
    }

    private String addTooltip(String source, @NotNull MethodNode method) {
        boolean structure = CodeExplorerPlugin.getSettings(method.getMethod().getProject()).isStructureByClick();
        String key = null;
        if (!getModel().isExplored(method, structure)) {
            key = structure ? "click-see-structure" : "click-see-usages";
        } else {
            Set deps = structure ? getModel().getCalls(method) : getModel().getUsages(method);
            if (deps.isEmpty()) {
                key = structure ? "no-calls" : "no-usages";
            }
        }

        StringBuffer res = new StringBuffer();
        res.append(source);
        if (key != null) {
            appendTip(res, key);
        }
        appendTip(res, structure ? "switch-usages" : "switch-structure");
        return res.toString();
    }

    private void appendTip(StringBuffer res, String key) {
        String tip = CodeExplorerBundle.message(key);
        res.append("<br><br>");
        res.append("<b>");
        res.append(tip);
        res.append("</b>");
    }

    public void customizeSettings(Graph2DView view, EditMode editMode) {
        editMode.allowBendCreation(false);
        editMode.allowEdgeCreation(false);
        editMode.allowNodeCreation(false);

        editMode.allowResizeNodes(false);
        editMode.allowMoveSelection(true);

        view.setFitContentOnResize(false);

        CustomShortcuts.install(getGraphBuilder());
    }

    public boolean editNode(MethodNode method) { // called on node double click
        ShowDependenciesAction action = new ShowDependenciesAction();
        if (action.expandDependencies(getGraphBuilder(), method)) {
            GraphUtils.scrollTo(getGraphBuilder(), method);
        }

        return false;
    }

    @NotNull
    public NodeRealizer getNodeRealizer(MethodNode method) { // todo: nice renderer
        if (_renderer == null) {
            _renderer = new MethodRenderer(getGraphBuilder(), null);
        }
        return GraphViewUtil.createNodeRealizer(RENDERER_NAME, _renderer);
    }

    private DependencyModel getModel() {
        return (DependencyModel) getGraphBuilder().getGraphDataModel();
    }

    public DeleteProvider<MethodNode, Edge<MethodNode>> getDeleteProvider() {
        return new MyDeleteProvider();
    }

    private class MyDeleteProvider extends DeleteProvider<MethodNode, Edge<MethodNode>> {
        public boolean canDeleteNode(@NotNull MethodNode node) {
            return true;
        }

        public boolean canDeleteEdge(@NotNull Edge<MethodNode> edge) {
            return false;
        }

        public boolean deleteNode(@NotNull MethodNode node) {
            DependencyModel model = getModel();
            model.remove(node);
            model.remove(model.getEdges(node));
            return true;
        }

        public boolean deleteEdge(@NotNull Edge<MethodNode> edge) {
            return false;
        }
    }

    private class ShowSourceSelectionListener implements Graph2DSelectionListener {
        public void onGraph2DSelectionEvent(Graph2DSelectionEvent e) {
            CodeExplorerSettings s = CodeExplorerPlugin.getSettings(getGraphBuilder().getProject());
            if (s.isGotoSource() && e.isNodeSelection()) {
                Node node = (Node) e.getSubject();
                MethodNode method = getGraphBuilder().getNodeObject(node);
                PsiUtils.scrollTo(method.getMethod());
            }
        }
    }
}
