package com.handyedit.codeexplorer.ui.action.graph.toolbar;

import com.handyedit.codeexplorer.model.DependencyModel;
import com.intellij.openapi.graph.builder.GraphBuilder;
import com.intellij.openapi.graph.builder.actions.AbstractGraphAction;

import javax.swing.*;

/**
 * @author Alexei Orishchenko
 */
public abstract class BaseToolbarAction extends AbstractGraphAction {

    protected GraphBuilder _builder;

    public BaseToolbarAction(GraphBuilder builder, String tooltip, Icon icon) {
        super(builder.getGraph(), tooltip, icon);
        _builder = builder;
    }

    protected DependencyModel getModel() {
        return (DependencyModel) _builder.getGraphDataModel();
    }

    protected void updateGraph() {
        _builder.queueUpdate();
    }
}
