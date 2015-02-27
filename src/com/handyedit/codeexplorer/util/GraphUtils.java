package com.handyedit.codeexplorer.util;

import com.handyedit.codeexplorer.math.Edge;
import com.intellij.openapi.graph.GraphManager;
import com.intellij.openapi.graph.base.Node;
import com.intellij.openapi.graph.builder.GraphBuilder;
import com.intellij.openapi.graph.builder.util.GraphViewUtil;
import com.intellij.openapi.graph.layout.LayoutOrientation;
import com.intellij.openapi.graph.layout.Layouter;
import com.intellij.openapi.graph.layout.hierarchic.HierarchicGroupLayouter;
import com.intellij.openapi.graph.layout.hierarchic.HierarchicLayouter;
import com.intellij.openapi.graph.settings.GraphSettings;
import com.intellij.openapi.graph.settings.GraphSettingsProvider;
import com.intellij.openapi.graph.view.Graph2D;
import com.intellij.openapi.graph.view.Graph2DView;
import com.intellij.openapi.project.Project;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Graph utility methods.
 *
 * @author Alexei Orishchenko
 */
public class GraphUtils {

    /**
     * Scrolls graph component to specified element if the element isn't visible.
     * @param builder graph builder
     * @param method method that represents node in graph
     */
    public static <T> void scrollTo(GraphBuilder<T, Edge> builder, T method) {
        Graph2D graph = builder.getGraph();
        Graph2DView view = builder.getView();

        Node node = builder.getNode(method);
        double x = graph.getCenterX(node);
        double y = graph.getCenterY(node);
        if (view.getVisibleRect().contains(x, y)) {
            return;
        }
        view.setCenter(x, y);
    }

    /**
     * Returns selected methods in graph component.
     *
     * @param builder graph builder
     * @return selected methods
     */
    public static <T> Set<T> getSelected(GraphBuilder<T, Edge> builder) {
        Set<T> result = new HashSet<T>();
        List<Node> nodes = GraphViewUtil.getSelectedNodes(builder.getGraph());
        for (Node node : nodes) {
            result.add(builder.getNodeObject(node));
        }
        return result;
    }

    /**
     * Returns selected method in graph component if one node is selected else returns null.
     *
     * @param builder graph builder
     * @return selected element for one node selection else returns null
     */
    public static <T> T getSingleSelected(GraphBuilder<T, Edge> builder) {
        if (builder == null) {
            return null;
        }

        List<Node> selected = GraphViewUtil.getSelectedNodes(builder.getGraph());
        if (selected != null && selected.size() == 1) {
            return builder.getNodeObject(selected.get(0));
        }
        return null;
    }

    public static GraphSettings getSettings(GraphBuilder builder) {
        return GraphSettingsProvider.getInstance(builder.getProject()).getSettings(builder.getGraph());
    }

    /**
     * Creates hierarchic layouter with specified orientation
     *
     * @param horisontal orientation
     * @return created layouter
     */
    public static Layouter createLayouter(boolean horisontal) {
        HierarchicGroupLayouter layouter = GraphManager.getGraphManager().createHierarchicGroupLayouter();
        layouter.setLayoutOrientation(horisontal ? LayoutOrientation.LEFT_TO_RIGHT : LayoutOrientation.TOP_TO_BOTTOM);
        setLayoutOrientation(layouter, horisontal);
        return layouter;
    }

    public static void setLayoutOrientation(HierarchicLayouter layouter, boolean horisontal) {
        layouter.setLayoutOrientation(horisontal ? LayoutOrientation.LEFT_TO_RIGHT : LayoutOrientation.TOP_TO_BOTTOM);
    }

    public static void layout(Graph2D graph, Project project) {
        HierarchicLayouter layouter = (HierarchicLayouter) GraphSettingsProvider.getInstance(project).getSettings(graph).getCurrentLayouter();
        layout(graph, layouter);
    }
    
    public static void layout(Graph2D graph, HierarchicLayouter layouter) {
        layouter.doLayout(graph);
        graph.updateViews();
    }
}
