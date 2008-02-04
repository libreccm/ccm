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
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Point;

import java.util.Vector;

/**
 * Represents text that contains multiple fonts and colors.
 * A simple word processor could use a single StyledText object
 * for managing its contents.
 * @author Gavin Doughtie
 */
public class StyledText extends AbstractDrawable implements GenericDrawable {
    public static final String versionId = "$Id: StyledText.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private Vector m_runs = new Vector();
    private Vector m_wrappedRuns = null;
    private Font m_baseFont = new Font("SansSerif", Font.PLAIN, 12);

    public StyledText() {
    }

    /**
     * Adds a run of text that uses the same
     * font and color. Runs are appended to an
     * internal list of runs and are rendered in order.
     * @param run Font, Color and String specification
     */
    public void addRun(FormattedText run) {
        m_runs.addElement(run);
    }

    /**
     * if a caller is caching runs after they have
     * been wrapped once, the pre-wrapped runs can
     * be added here. if a StyledText object has
     * any wrapped runs, they will be drawn instead
     * of re-wrapping.
     * @param runs Vector of WrappedRun objects
     */
    public void setWrappedRuns(Vector runs) {
        m_wrappedRuns = runs;
    }

    /**
     * @return Vector of WrappedRun objects
     */
    public Vector getWrappedRuns() {
        return m_wrappedRuns;
    }

    // GenericDrawable implementation
    /**
     * GenericDrawable implementation. Draws wrapped runs, or
     * wraps and draws all runs.
     */
    public void draw(Graphics g, int x, int y, int width, int height) {
        if (null != m_wrappedRuns && 0 < m_wrappedRuns.size()) {
            // Optimization -- short circuit everything
            drawWrappedText(
                            g,
                            m_baseFont,
                            m_runs,
                            new Dimension(width, height),
                            x + getXOffset(),
                            false,
                            null,
                            null,
                            m_wrappedRuns
                            );
            return;
        }

        // First, offset for the font metrics
        if (null == g || 0 == m_runs.size()) {
            return;
        }

        FormattedText firstRun = (FormattedText) m_runs.elementAt(0);
        firstRun.updateGraphics(g);
        Point startPoint = new Point(x + getXOffset(), y + getYOffset());
        Dimension preferredDimension = new Dimension(0, 0);
        drawWrappedText(
                        g,
                        m_baseFont,
                        m_runs,
                        new Dimension(width, height),
                        x + getXOffset(),
                        false,
                        startPoint,
                        preferredDimension,
                        m_wrappedRuns
                        );
    }

    /**
     * Sets the default font for all runs that don't specify a font
     * @param font Font to draw in
     */
    public void setBaseFont(Font font) {
        m_baseFont = font;
    }

    /**
     * @param index index of a specific run to retrieve
     * @return the retrieved run or null if no run exists
     * at the specified index.
     */
    public FormattedText getFormattedTextAt(int index) {
        if (index < m_runs.size()) {
            FormattedText ft = (FormattedText) m_runs.elementAt(index);
            return ft;
        }
        return null;
    }

    /**
     * Replaces an existing run with a new one
     * @param index index of an existing run
     * @param ft FormattedText to add at index
     */
    public void setFormattedTextAt(int index, FormattedText ft) {
        if (index == m_runs.size()) {
            m_runs.addElement(ft);
        } else if (index < m_runs.size()) {
            m_runs.setElementAt(ft, index);
        }
    }

    /**
     * Changes the color of a specific run. Typically
     * called by buttons or list items that want to change
     * their text colors when selected.
     * @param index index of an existing run
     * @param color new color for the run
     */
    public void setRunColor(int runIndex, Color color) {
        FormattedText ft = getFormattedTextAt(runIndex);
        if (null != ft) {
            ft.setColor(color);
        }
    }

    /**
     * @return the unformatted text of all runs as a single string
     */
    public String toString() {
        if (0 >= m_runs.size()) {
            return "";
        } else if (1 == m_runs.size()) {
            FormattedText ft = (FormattedText) m_runs.elementAt(0);
            return ft.getString();
        } else {
            StringBuffer buffer = new StringBuffer();
            FormattedText ft = null;
            for (int i = 0; i < m_runs.size(); i++) {
                ft = (FormattedText) m_runs.elementAt(i);
                buffer.append(ft.getString());
            }
            return buffer.toString();
        }
    }

    /**
     * @param g Graphics from which to calculate dimensions
     * @param width width within which to calculate wrapped text
     * @param height height of area for wrapped text
     * @return a Dimension within which all text could be displayed
     * @todo Currently the width parameter is simply passed back in
     * the returned Dimension. If the width is not large enough to
     * accomodate the longest word in a run, it should be adjusted in
     * the return value to reflect this.
     */
    public Dimension getPreferredSize(Graphics g, int width, int height) {
        Point startPoint = new Point(getXOffset(), getYOffset());
        Dimension preferredDimension = new Dimension(0, 0);
        drawWrappedText(
                        g,
                        m_baseFont,
                        m_runs,
                        new Dimension(width, height),
                        getXOffset(),
                        true,
                        startPoint,
                        preferredDimension,
                        null
                        );
        return preferredDimension;
    }

    /**
     * Draws and/or measures runs of styled text
     *
     * @param g Graphics context to draw the wrapped text upon.
     * @param baseFont Font to use if none is specified in the run
     * @param runs FormattedText runs to draw
     * @param size text will be wrapped within the width of this Dimension.
     * Text will not be drawn below the bottom of this size, but if the
     * measureOnly flag is set, the max height WILL be calculated.
     * @param leftMargin new lines after the first line will begin at this
     * horizontal offset in the current coordinate system.
     * @param startPoint The location in the current coordinate system to
     * begin drawing the first line of text
     * @param preferredDimension @out if all the text were to be drawn, this
     * dimension would be required to hold it. Only valid if measureOnly is set.
     * @param measureOnly if true, calculate preferredDimension but do not draw.
     * @param wrappedRuns if non-null but empty, WrappedRuns as used to draw
     * the StyledText will be returned in this parameter. If non-null AND
     * non-empty, wrapped runs will be drawn and no further word wrapping or
     * calculations will be performed.
     */
    public static void drawWrappedText(
                                       Graphics g,
                                       Font baseFont,
                                       Vector runs,
                                       Dimension size,
                                       int leftMargin,
                                       boolean measureOnly,
                                       Point startPoint,
                                       Dimension preferredDimension,
                                       Vector wrappedRuns
                                       ) {
        int initialY = startPoint.y;
        int initialX = startPoint.x;
        Dimension tempDimension = null;
        int i = 0;
        if (null != preferredDimension) {
            tempDimension = new Dimension(0, 0);
        }

        // For drawing only -- does no calculation
        if (null != wrappedRuns && 0 < wrappedRuns.size()) {
            for (i = 0; i < wrappedRuns.size(); i++) {
                WrappedRun run = (WrappedRun) wrappedRuns.elementAt(i);
                run.updateGraphics(g);
                run.draw(g);
            }
            return;
        }

        Vector currentRunWrap = null;
        if (null != wrappedRuns) {
            currentRunWrap = new Vector();
        }

        for (i = 0; i < runs.size(); i++) {
            FormattedText currentRun = (FormattedText) runs.elementAt(i);
            currentRun.updateGraphics(g);
            if (null == currentRun.getFont() && null != baseFont) {
                g.setFont(baseFont);
            }

            FormattedText.drawWrappedText(
                                          g,
                                          currentRun.getString(),
                                          size,
                                          leftMargin,
                                          measureOnly,
                                          startPoint,
                                          tempDimension,
                                          currentRunWrap
                                          );
            if (null != wrappedRuns) {
                for (int j = 0; j < currentRunWrap.size(); j++) {
                    wrappedRuns.addElement(currentRunWrap.elementAt(j));
                } // end of for

                currentRunWrap.removeAllElements();
            } // end of if
        }

        if (null != preferredDimension) {
            // The graphics object will still
            // be set from the last update, thus
            // the font metrics will be correct
            FontMetrics fm = g.getFontMetrics();
            int ascent = fm.getHeight();
            int descent = fm.getMaxDescent();

            preferredDimension.height = startPoint.y + ascent + descent;
            if (initialY == startPoint.y) { // single-line
                preferredDimension.width = startPoint.x - initialX;
            } else {
                preferredDimension.width = size.width;
            }
        }
    }

}// StyledText
