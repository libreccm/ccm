/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package com.arsdigita.toolbox.rebop;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

/**
 * Represents a "run" of text with a single set of
 * style attributes. Does NOT perform word-wrapping
 * calculations (in other words they have already
 * been performed, resulting in a collection of these
 * WrappedRun objects).
 *
 * @author Gavin Doughtie
 * @see StyledText
 */
public class WrappedRun extends FormattedText {
    public static final String versionId = "$Id: WrappedRun.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";
    private int m_x = 0;
    private int m_y = 0;

    /**
     * Creates a new <code>WrappedRun</code> instance.
     *
     * @param font Text will be drawn using this font, or
     * the current font of the passed-in Graphics object if
     * font is set to null.
     * @param color Text will be drawn in this color, or
     * the current color of the passed-in Graphics object if
     * color is set to null.
     * @param string Text to draw
     * @param x horizontal location to begin drawing text at
     * @param y vertical location to begin drawing text at
     */
    public WrappedRun(Font font, Color color, String string, int x, int y) {
        super(font, color, string);
        setPoint(x, y);
    }

    /**
     * Draw the text using this run's font and color
     * @param g Graphics to draw upon
     */
    public void draw(Graphics g) {
        updateGraphics(g);
        g.drawString(getString(), m_x, m_y);
    }

    /**
     * Sets the point at which this run of text will be drawn
     * inside the current component's coordinate space.
     * @param x horizontal location
     * @param y vertical location
     */
    public void setPoint(int x, int y) {
        m_x = x;
        m_y = y;
    }
}// WrappedRun
