package com.handyedit.codeexplorer.ui.graph;

import com.handyedit.codeexplorer.CodeExplorerPlugin;
import com.handyedit.codeexplorer.CodeExplorerSettings;
import com.handyedit.codeexplorer.math.Edge;
import com.handyedit.codeexplorer.model.DependencyModel;
import com.handyedit.codeexplorer.model.MethodNode;
import com.handyedit.codeexplorer.res.CodeExplorerBundle;
import com.handyedit.codeexplorer.ui.action.graph.toolbar.UpdateToolbarListener;
import com.handyedit.codeexplorer.util.ActionUtils;
import com.handyedit.codeexplorer.util.GraphUtils;
import com.handyedit.codeexplorer.util.LayoutUtils;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.graph.GraphManager;
import com.intellij.openapi.graph.base.Node;
import com.intellij.openapi.graph.builder.GraphBuilder;
import com.intellij.openapi.graph.builder.GraphBuilderFactory;
import com.intellij.openapi.graph.builder.components.BasicGraphComponent;
import com.intellij.openapi.graph.builder.util.GraphViewUtil;
import com.intellij.openapi.graph.layout.Layouter;
import com.intellij.openapi.graph.view.Graph2D;
import com.intellij.openapi.graph.view.Graph2DView;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Method dependencies component (diagram).
 *
 * @author Alexei Orishchenko
 */
public class DiagramComponent extends BasicGraphComponent<MethodNode, Edge> {

    private static final int CALLS = 0;
    private static final int USAGES = 1;

    public DiagramComponent(@NotNull GraphBuilder<MethodNode, Edge> builder) {
        super(builder);

        GraphViewUtil.addDataProvider(builder.getView(), new GraphDataProvider(builder));

        CodeExplorerSettings s = CodeExplorerPlugin.getSettings(builder.getProject());
        Layouter layouter = GraphUtils.createLayouter(s.isHorisontalOrientation());
        GraphUtils.getSettings(builder).setCurrentLayouter(layouter);

        replaceToolbar();

//        PsiManager.getInstance(builder.getProject()).addPsiTreeChangeListener(new PsiTreeListener(builder));
    }

    private void replaceToolbar() {
        JComboBox combo = createClickModeCombo(getBuilder().getGraph());
        UpdateToolbarListener listener = new UpdateComboListener(getBuilder().getProject(), combo);

        DefaultActionGroup actions = ActionFactory.createActions(getBuilder(), listener);
        ActionToolbar actionToolbar = ActionManager.getInstance().createActionToolbar(ActionPlaces.UNKNOWN, actions, true);
        JComponent toolbar = actionToolbar.getComponent();

        JPanel comboPanel = new JPanel();
        comboPanel.add(new JLabel(CodeExplorerBundle.message("show-on-doubleclick")));
        comboPanel.add(combo);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.add(comboPanel, createConstraint(false));
        panel.add(toolbar, createConstraint(true));
        getComponent().add(panel, BorderLayout.NORTH);
    }

    private GridBagConstraints createConstraint(boolean toolbar) {
        GridBagConstraints res = LayoutUtils.create(toolbar ? 1 : 0, 0, toolbar, new Insets(0, 0, 0, 0));
        res.anchor = GridBagConstraints.WEST;
        return res;
    }

    private JComboBox createClickModeCombo(final Graph2D graph) {
        final JComboBox combo = new JComboBox(new Object[]{CodeExplorerBundle.message("calls"), CodeExplorerBundle.message("usages")});

        combo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean structure = combo.getSelectedIndex() == CALLS;
                CodeExplorerPlugin.getSettings(getBuilder().getProject()).setStructureByClick(structure);
                graph.updateViews();
            }
        });

        return combo;
    }

    public void updateGraph() {
        getBuilder().queueUpdate();
    }

    public void updateGraphNow() {
        getBuilder().updateGraph();
    }

    public void layout() {
        Layouter layouter = GraphUtils.getSettings(getBuilder()).getCurrentLayouter();
        layouter.doLayout(getBuilder().getGraph());
    }

    public DependencyModel getModel() {
        return (DependencyModel) getBuilder().getGraphDataModel();
    }

    public void select(MethodNode method) {
        if (method != null) {
            Graph2D graph = getBuilder().getGraph();
            Node node = getBuilder().getNode(method);
            graph.setSelected(node, true);
        }
    }

    public static DiagramComponent create(Project project) {
        DependencyModel model = new DependencyModel();
        Graph2D graph = GraphManager.getGraphManager().createGraph2D();
        Graph2DView view = GraphManager.getGraphManager().createGraph2DView();

        DependencyPresentationModel presentation = new DependencyPresentationModel(graph);
        GraphBuilder<MethodNode, Edge> builder =
                GraphBuilderFactory.getInstance(project).createGraphBuilder(graph, view, model, presentation);
        return new DiagramComponent(builder);
    }

    private static class GraphDataProvider implements DataProvider {

        private GraphBuilder<MethodNode, Edge> _builder;

        private GraphDataProvider(GraphBuilder<MethodNode, Edge> builder) {
            _builder = builder;
        }

        public Object getData(@NonNls String dataId) {
            if (dataId.equals(DataKeys.PROJECT.getName())) {
                return _builder.getProject();
            } else if (dataId.equals(DataKeys.PSI_ELEMENT.getName())) {
                MethodNode selected = GraphUtils.getSingleSelected(_builder);
                return selected != null ? selected.getMethod() : null;
            } else if (dataId.equals(ActionUtils.KEY_BUILDER)) {
                return _builder;
            }
            return null;
        }
    }

    private class UpdateComboListener implements UpdateToolbarListener {

        private JComboBox _combo;
        private Project _project;

        private UpdateComboListener(Project project, JComboBox combo) {
            _project = project;
            _combo = combo;
        }

        public void updated() {
            boolean structureInCombo = _combo.getSelectedIndex() == CALLS;
            boolean structure = CodeExplorerPlugin.getSettings(_project).isStructureByClick();
            if (structureInCombo != structure) {
                _combo.setSelectedIndex(structure ? CALLS : USAGES);
            }
        }
    }
}
