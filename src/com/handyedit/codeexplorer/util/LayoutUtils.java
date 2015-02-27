package com.handyedit.codeexplorer.util;

import java.awt.*;

/**
 * @author Alexei Orishchenko
 */
public class LayoutUtils {
    public static GridBagConstraints create(int x, int y, boolean compFillRow, Insets insets) {
        return create(x, y, compFillRow, false, compFillRow, false, insets);
    }

    public static GridBagConstraints create(int x, int y, boolean cellFillHorisontal, boolean cellFillVertical,
                                            boolean compFillHorisontal, boolean compFillVertical,
                                            Insets insets) {
        
        return new GridBagConstraints(x, y, 1, 1, cellFillHorisontal ? 1 : 0, cellFillVertical ? 1 : 0,
                GridBagConstraints.NORTHWEST, getFill(compFillHorisontal, compFillVertical), insets, 0, 0);
    }

    private static int getFill(boolean horis, boolean vert) {
        if (horis) {
            if (vert) {
                return GridBagConstraints.BOTH;
            } else {
                return GridBagConstraints.HORIZONTAL;
            }
        } else {
            if (vert) {
                return GridBagConstraints.VERTICAL;
            } else {
                return GridBagConstraints.NONE;
            }
        }
    }
}
