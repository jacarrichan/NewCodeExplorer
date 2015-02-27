package com.handyedit.codeexplorer.ui.graph;

import com.handyedit.codeexplorer.ui.action.MoveSelectionWrapper;
import com.handyedit.codeexplorer.ui.action.graph.ShowStructureAction;
import com.handyedit.codeexplorer.ui.action.graph.ShowUsagesAction;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.CustomShortcutSet;
import com.intellij.openapi.graph.GraphManager;
import com.intellij.openapi.graph.builder.GraphBuilder;
import com.intellij.openapi.graph.view.Graph2DView;
import com.intellij.openapi.graph.view.Graph2DViewActions;

import javax.swing.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * Keys support on diagram.
 *
 * @author Alexei Orishchenko
 */
public class CustomShortcuts {

    private static final String MOVE_TOP = "FOCUS_TOP_NODE";
    private static final String MOVE_BOTTOM = "FOCUS_BOTTOM_NODE";
    private static final String MOVE_LEFT = "FOCUS_LEFT_NODE";
    private static final String MOVE_RIGHT = "FOCUS_RIGHT_NODE";

    public static void install(GraphBuilder builder) {
        Graph2DView view = builder.getView();
        JComponent comp = view.getCanvasComponent();
        
        Graph2DViewActions actions = GraphManager.getGraphManager().createGraph2DViewActions(view);
        ActionMap actionMap = actions.createActionMap();
        InputMap inputMap = actions.createDefaultInputMap(actionMap);

        final ActionManager am = ActionManager.getInstance();
        Map<String, KeyStroke> map = new HashMap<String, KeyStroke>();
        map.put(ShowUsagesAction.ACTION_ID, KeyStroke.getKeyStroke(KeyEvent.VK_F7, InputEvent.ALT_MASK));
        map.put(ShowUsagesAction.ACTION_ID, KeyStroke.getKeyStroke(KeyEvent.VK_UP, InputEvent.CTRL_MASK));
        map.put(ShowStructureAction.ACTION_ID, KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.CTRL_MASK));

        for (String action : map.keySet()) {
            am.getAction(action).registerCustomShortcutSet(new CustomShortcutSet(map.get(action)), comp);
        }

        wrapActions(actionMap, builder, MOVE_TOP, MOVE_BOTTOM, MOVE_LEFT, MOVE_RIGHT);
        
        comp.setActionMap(actionMap);
        comp.setInputMap(JComponent.WHEN_FOCUSED, inputMap);
    }

    private static void wrapActions(ActionMap aMap, GraphBuilder builder, String... actions) {
        for (String actionName : actions) {
            Object action = aMap.get(actionName);
            if (action instanceof AbstractAction) {
                aMap.put(actionName, new MoveSelectionWrapper((AbstractAction) action, builder));
            }
        }
    }
}
