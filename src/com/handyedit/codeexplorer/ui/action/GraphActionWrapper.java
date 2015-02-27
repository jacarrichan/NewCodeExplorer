package com.handyedit.codeexplorer.ui.action;

import com.intellij.openapi.graph.builder.GraphBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Konstantin Bulenkov
 *         from UML plugin
 */
public abstract class GraphActionWrapper extends AbstractAction {
    private final AbstractAction myAction;
    private final GraphBuilder myBuilder;

    public GraphActionWrapper(@NotNull AbstractAction action, GraphBuilder builder) {
        myAction = action;
        myBuilder = builder;
    }

    protected AbstractAction getAction() {
        return myAction;
    }

    public GraphBuilder getBuilder() {
        return myBuilder;
    }

    public abstract void actionPerformed(final ActionEvent e);

    @Override
    public Object getValue(final String key) {
        return myAction.getValue(key);
    }

    @Override
    public void putValue(final String key, final Object newValue) {
        myAction.putValue(key, newValue);
    }

    @Override
    public boolean isEnabled() {
        return myAction.isEnabled();
    }

    @Override
    public void setEnabled(final boolean newValue) {
        myAction.setEnabled(newValue);
    }

    @Override
    public Object[] getKeys() {
        return myAction.getKeys();
    }

    @Override
    public synchronized void addPropertyChangeListener(final PropertyChangeListener listener) {
        myAction.addPropertyChangeListener(listener);
    }

    @Override
    public synchronized void removePropertyChangeListener(final PropertyChangeListener listener) {
        myAction.removePropertyChangeListener(listener);
    }

    @Override
    public synchronized PropertyChangeListener[] getPropertyChangeListeners() {
        return myAction.getPropertyChangeListeners();
    }

    @Override
    public String toString() {
        return myAction.toString();
    }
}