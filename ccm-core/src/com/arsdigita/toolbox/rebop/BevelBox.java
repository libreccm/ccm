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
import java.awt.Graphics;

/**
 * Used to draw 3D boxes and edges around components.
 * Can be used to draw solid flat rectangles, buttons,
 * and edges around other controls. Implements the
 * GenericDrawable interface so it can be used as a
 * drawable for any GenericComponent.
 *
 * @author Gavin Doughtie
 */
public class BevelBox extends AbstractDrawable implements GenericDrawable {
    public static final int STANDARD_BEVEL = 2;
    private Color m_fillColor = Color.gray;
    private Color m_lightEdge = Color.lightGray;
    private Color m_darkEdge = Color.darkGray;
    private int m_bevelWidth = STANDARD_BEVEL;
    private boolean m_in = false;

    /**
     * Construct a new BevelBox. Will draw as a
     * standard-looking gray button.
     */
    public BevelBox() {
    }

    /**
     * Construct a new BevelBox with the specified
     * fillColor and the "raised button" appearance.
     * @param fillColor box will be drawn with this as its fill color.
     * fillColor shouldn't be null, because then there's nothing to draw!
     */
    public BevelBox(Color fillColor) {
        this(fillColor, STANDARD_BEVEL, false);
    }

    /**
     * Construct a new BevelBox with the specified
     * appearance.
     * @param fillColor box will be drawn with this as its fill color.
     * fillColor shouldn't be null, because then there's nothing to draw!
     * @param bevelWidth number of pixels to use for bevelled edges
     * @param in if true, then the lightEdge will be the lower-right
     * edges of the box, otherwise the lightEdge will be the upper-left edges.
     */
    public BevelBox
        (
         Color fillColor,
         int bevelWidth,
         boolean in
         ) {
        Color dark = null;
        Color light = null;
        if (null != fillColor) {
            light = fillColor.brighter().brighter();
            dark = fillColor.darker().darker();
        }

        init(fillColor,
             light,
             dark,
             bevelWidth,
             in);
    }

    /**
     * Construct a new BevelBox with the specified
     * appearance.
     * @param fillColor box will be drawn with this as its fill color.
     * if null, then no center will be drawn.
     * @param lightEdge color to draw the light edge in.  If null, no
     * light edge will be drawn.
     * @param darkEdge color to draw the light edge in. If null, no
     * dark edge will be drawn.
     * @param bevelWidth number of pixels to use for bevelled edges
     * @param in if true, then the lightEdge will be the lower-right
     * edges of the box, otherwise the lightEdge will be the upper-left edges.
     */
    public BevelBox
        (
         Color fillColor,
         Color lightEdge,
         Color darkEdge,
         int bevelWidth,
         boolean in
         ) {
        init(fillColor,
             lightEdge,
             darkEdge,
             bevelWidth,
             in);
    }

    /**
     * Ultimately called by all constructors. See BevelBox()
     */
    private void init(
                      Color fillColor,
                      Color lightEdge,
                      Color darkEdge,
                      int bevelWidth,
                      boolean in
                      ) {
        m_fillColor = fillColor;
        m_lightEdge = lightEdge;
        m_darkEdge = darkEdge;
        m_bevelWidth = bevelWidth;
        m_in = in;
    }

    public void setIn(boolean in) {
        m_in = in;
    }

    public void toggleIn() {
        m_in = !m_in;
    }

    public boolean getIn() {
        return m_in;
    }

    /**
     * Implementation of the GenericDrawable interface
     */
    public void draw(Graphics g, int x, int y, int width, int height) {
        if (null != m_fillColor) {
            g.setColor(m_fillColor);
            g.fillRect(x, y, width, height);
        }

        Color tlEdgeColor = m_lightEdge;
        Color brEdgeColor = m_darkEdge;

        if (m_in) {
            tlEdgeColor = m_darkEdge;
            brEdgeColor = m_lightEdge;
        }

        int currentX = x;
        int currentY = y;
        int currentLineWidth = width - 1;
        if (null != tlEdgeColor) {
            g.setColor(tlEdgeColor);
            for (int i = 0; i < m_bevelWidth; i++) {
                g.drawLine(
                           currentX,
                           currentY,
                           currentX + currentLineWidth,
                           currentY);
                currentX++;
                currentY++;
                currentLineWidth -= 2;
            }

            currentX = x;
            currentY = y;
            currentLineWidth = height - 1;

            for (int i = 0; i < m_bevelWidth; i++) {
                g.drawLine(
                           currentX,
                           currentY,
                           currentX,
                           currentY + currentLineWidth);
                currentX++;
                currentY++;
                currentLineWidth -= 2;
            }
        } // end of if ()

        if (null != brEdgeColor) {
            g.setColor(brEdgeColor);

            // bottom
            currentX = x;
            currentY = height - 1;
            currentLineWidth = width - 1;
            for (int i = 0; i < m_bevelWidth; i++) {
                g.drawLine(
                           currentX,
                           currentY,
                           currentX + currentLineWidth,
                           currentY);
                currentX++;
                currentY--;
                currentLineWidth -= 2;
            }

            // right
            currentX = width - 1;
            currentY = y;
            currentLineWidth = height - 1;

            for (int i = 0; i < m_bevelWidth; i++) {
                g.drawLine(
                           currentX,
                           currentY,
                           currentX,
                           currentY + currentLineWidth);
                currentX--;
                currentY++;
                currentLineWidth -= 2;
            }
        }
    }
}// BevelBox
