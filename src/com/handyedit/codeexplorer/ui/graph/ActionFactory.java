package com.handyedit.codeexplorer.ui.graph;

import com.handyedit.codeexplorer.ui.action.analyze.MethodCallChainsFromAction;
import com.handyedit.codeexplorer.ui.action.analyze.MethodCallChainsInAction;
import com.handyedit.codeexplorer.ui.action.analyze.MethodCallsInAction;
import com.handyedit.codeexplorer.ui.action.analyze.MethodStructureAction;
import com.handyedit.codeexplorer.ui.action.graph.ShowDependenciesAction;
import com.handyedit.codeexplorer.ui.action.graph.toolbar.*;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.graph.builder.GraphBuilder;
import com.intellij.openapi.graph.builder.actions.*;
import com.intellij.openapi.graph.builder.actions.export.ExportToFileAction;
import com.intellij.openapi.graph.builder.actions.printing.PrintGraphAction;
import com.intellij.openapi.graph.builder.actions.printing.PrintPreviewAction;
import com.intellij.openapi.graph.view.Graph2D;

/**
 * @author Alexei Orishchenko
 */
public class ActionFactory {
    
    private static final String ACTION_ID_GOTO_SOURCE = "EditSource";

    static DefaultActionGroup createActions(GraphBuilder builder, UpdateToolbarListener listener) {
        return createActions(builder, listener, false);
    }

    public static DefaultActionGroup createContextMenuActions(GraphBuilder builder) {
        return createActions(builder, null, true);
    }

    private static DefaultActionGroup createActions(GraphBuilder builder, UpdateToolbarListener listener, boolean contextMenu) {
        DefaultActionGroup actions = new DefaultActionGroup();

        Graph2D graph = builder.getGraph();

        if (contextMenu) {
            actions.add(new MethodStructureAction());
            actions.add(new MethodCallsInAction());
            actions.add(new MethodCallChainsInAction());
            actions.add(new MethodCallChainsFromAction());
            actions.addSeparator();
            actions.add(new ShowDependenciesAction());
            actions.add(ActionManager.getInstance().getAction(ACTION_ID_GOTO_SOURCE));
            actions.addSeparator();
        } else {
            actions.add(new LoadGraphAction(builder));
            actions.add(new SaveGraphAction(builder));
            actions.add(new SynchronizeGraphAction(builder));

            actions.addSeparator();

            ChangeNodeClickAction action = new ChangeNodeClickAction(builder, true);
            if (listener != null) {
                action.setListener(listener);
            }

            actions.add(action);
            actions.add(new ChangeNodeClickAction(builder, false));
            actions.addSeparator();
        }
        actions.add(new ChangeLayoutOrintationAction(builder));
        actions.add(new ShowClassNamesAction(builder));
        actions.add(new ShowMethodBodyAction(builder));
        actions.add(new AutoscrollToSourceAction(builder));
        actions.addSeparator();

        actions.addSeparator();
        actions.add(new CloseTabAction(graph));
        actions.add(new DeleteSelectionAction());

        if (contextMenu) {
            actions.addSeparator();
        }

        actions.add(new ShowHideGridAction(graph));
        actions.add(new SnapToGridAction(graph));
        actions.addSeparator();

        actions.add(new ZoomInAction(graph));
        actions.add(new ZoomOutAction(graph));
        actions.add(new ActualZoomAction(graph));
        actions.addSeparator();

        actions.add(new ExportToFileAction(graph));
        actions.add(new PrintGraphAction(graph));
        actions.add(new PrintPreviewAction(graph));

        return actions;
    }
}
