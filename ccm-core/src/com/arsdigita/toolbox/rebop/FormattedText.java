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
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;

import java.text.BreakIterator;
import java.util.Vector;

/**
 * Represents a "run" of text with a single set of
 * style attributes. Includes logic for word-wrapping
 * the text within a given area.
 *
 * @author Gavin Doughtie
 * @see StyledText
 */
public class FormattedText {
    private Font m_font = null;
    private Color m_color = null;
    private String m_string = null;

    public FormattedText() {
    }

    public FormattedText(String text) {
        m_string = text;
    }

    /**
     * Creates a new <code>FormattedText</code> instance.
     *
     * @param font Text will be drawn using this font, or
     * the current font of the passed-in Graphics object if
     * font is set to null.
     * @param color Text will be drawn in this color, or
     * the current color of the passed-in Graphics object if
     * color is set to null.
     * @param string Text to draw
     */
    public FormattedText(Font font, Color color, String string) {
        m_font = font;
        m_color = color;
        m_string = string;
    }

    /**
     * Updates a Graphics object with font and color, if set
     * @param g Graphics to update
     */
    public void updateGraphics(Graphics g) {
        if (null != m_font) {
            g.setFont(m_font);
        }
        if (null != m_color) {
            g.setColor(m_color);
        }
    }

    public String getString() {
        return m_string;
    }

    public void setString(String string) {
        m_string = string;
    }

    public Font getFont() {
        return m_font;
    }

    public void setFont(Font font) {
        m_font = font;
    }

    public Color getColor() {
        return m_color;
    }

    public void setColor(Color color) {
        m_color = color;
    }

    // GenericDrawable implementation
    /**
     * Allows a FormattedText object to be used as a component's
     * Drawable object.
     */
    public void draw(Graphics g, int x, int y, int width, int height) {
        updateGraphics(g);
        Point startPoint = new Point(x, y);
        Dimension preferredDimension = new Dimension(0, 0);
        drawWrappedText(
                        g,
                        m_string,
                        new Dimension(width, height),
                        x,
                        false,
                        startPoint,
                        preferredDimension,
                        null
                        );
    }

    /**
     * Draws and/or measures text wrapped within a set of constraints.
     *
     * @param g Graphics context to draw the wrapped text upon.
     * @param text to draw
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
     * @param wrappedRuns if non-null, store cached runs here
     */
    public static void drawWrappedText(
                                       Graphics g,
                                       String text,
                                       Dimension size,
                                       int leftMargin,
                                       boolean measureOnly,
                                       Point startPoint,
                                       Dimension preferredDimension,
                                       Vector wrappedRuns) {
        if (null == text) {
            return;
        }
        int x = startPoint.x;
        int y = startPoint.y;
        int initialY = y;
        BreakIterator bi = BreakIterator.getLineInstance();
        bi.setText(text);
        FontMetrics fm = g.getFontMetrics();
        int lineHeight = fm.getHeight();
        int ascent = fm.getMaxAscent();
        int descent = fm.getMaxDescent();
        int start = 0;
        int end = bi.next();
        int stop = end;
        String currentString = text;
        int maxX = size.width;
        int width = 0;
        int strLen = text.length();
        String substring;
        Font currentFont = null;
        Color currentColor = null;
        if (null != wrappedRuns) {
            currentFont = g.getFont();
            currentColor = g.getColor();
        }

        while (BreakIterator.DONE != end) {
            currentString = text.substring(start, end);
            boolean bNewLine = false;
            int newlineIndex = currentString.indexOf("\n");
            if (newlineIndex > 0) {
                bNewLine = true;
            }

            if (0 == newlineIndex) {
                start++;
                currentString = text.substring(start, end);
            }

            width = fm.stringWidth(currentString);
            int currentX = (width + x);

            if ((!bNewLine && (currentX < maxX)) && end < strLen) {
                // We haven't hit the end, so we can attempt
                // to get a longer string before we draw
                stop = end;
                end = bi.next();
                continue;
            }

            if (currentX <= maxX) {
                stop = end;
            }

            if (bNewLine) {
                stop = end - 1;
                end = bi.next();
            }

            // ready to draw now
            if (!measureOnly) {
                substring = text.substring(start, stop).replace('\r', ' ');
                substring = substring.replace('\n', ' ');
                g.drawString(substring, x, y + ascent);
                if (null != wrappedRuns) {
                    wrappedRuns.addElement(new WrappedRun(
                                                          currentFont,
                                                          currentColor,
                                                          substring,
                                                          x,
                                                          y + ascent));
                }
            }

            if ((currentX > maxX) || bNewLine) {
                x = leftMargin;
                y += lineHeight;
            } else {
                x += width;
            }

            start = stop;
            end = bi.next();
        }

        // Draw the last little bit, if any
        substring = text.substring(start);
        if (!measureOnly && substring.length() > 0) {
            g.drawString(substring, x, y + ascent);
            if (null != wrappedRuns) {
                wrappedRuns.addElement(new WrappedRun(
                                                      currentFont,
                                                      currentColor,
                                                      substring,
                                                      x,
                                                      y + ascent));
            }
        }
        x += fm.stringWidth(substring);
        startPoint.x = x;
        startPoint.y = y;

        preferredDimension.width = size.width;
        preferredDimension.height = (startPoint.y - initialY) + descent + ascent;
    }

}// FormattedText
