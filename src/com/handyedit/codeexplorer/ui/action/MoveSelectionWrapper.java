package com.handyedit.codeexplorer.ui.action;

import com.intellij.openapi.graph.builder.GraphBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;

/**
 * @author Konstantin Bulenkov
 *         from UML plugin
 */
public class MoveSelectionWrapper extends GraphActionWrapper {
    public MoveSelectionWrapper(@NotNull AbstractAction action, GraphBuilder builder) {
        super(action, builder);
    }

    public void actionPerformed(final ActionEvent e) {
        Point2D point2D = getBuilder().getView().getCenter();
        getAction().actionPerformed(e);
        getBuilder().getView().setCenter(point2D.getX(), point2D.getY());
    }
}