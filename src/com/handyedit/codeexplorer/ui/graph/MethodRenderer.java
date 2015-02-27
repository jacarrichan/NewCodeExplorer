package com.handyedit.codeexplorer.ui.graph;

import com.handyedit.codeexplorer.CodeExplorerPlugin;
import com.handyedit.codeexplorer.CodeExplorerSettings;
import com.handyedit.codeexplorer.math.Edge;
import com.handyedit.codeexplorer.model.DependencyModel;
import com.handyedit.codeexplorer.model.MethodNode;
import com.intellij.openapi.graph.builder.GraphBuilder;
import com.intellij.openapi.graph.builder.renderer.BasicGraphNodeRenderer;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.util.ModificationTracker;

import javax.swing.*;

/**
 * @author Alexei Orishchenko
 */
public class MethodRenderer extends BasicGraphNodeRenderer<MethodNode, Edge> {

    private static final Icon ICON_EXPAND = IconLoader.getIcon("/nodes/unknownJdk.png");

    public MethodRenderer(GraphBuilder<MethodNode, Edge> builder, ModificationTracker tracker) {
        super(builder, tracker);
    }

    protected Icon getIcon(MethodNode method) {
        DependencyModel model = (DependencyModel) getBuilder().getGraphDataModel();
        if (!model.isExplored(method, getSettings().isStructureByClick())) {
            return ICON_EXPAND;
        }
        return method.getIcon();
    }

    protected String getNodeName(MethodNode method) {
        return method.getName(getSettings().isShowClassName());
    }

    private CodeExplorerSettings getSettings() {
        return CodeExplorerPlugin.getSettings(getBuilder().getProject());
    }
}