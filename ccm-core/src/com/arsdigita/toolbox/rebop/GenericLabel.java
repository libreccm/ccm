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


import java.util.Vector;

/**
 * GenericLabel.java
 *
 *
 * Created: Thu Sep 20 08:28:15 2001
 *
 * @author Gavin Doughtie
 * @version $Date: 2004/08/16 $
 */

public class GenericLabel extends GenericComponent {

    private Dimension m_preferredSize = null;
    private boolean m_updateSize = false;

    // for button "push down" rendering
    private boolean m_useOffset = false;
    private int m_xOffset = 2;
    private int m_yOffset = 2;

    private boolean m_wrapText = true;

    private Vector m_wrappedRuns = null;

    public GenericLabel(String text) {
        setFont(new Font("SansSerif", Font.PLAIN, 12));
        updateText(text);
    }

    public GenericLabel() {
        setFont(new Font("SansSerif", Font.PLAIN, 12));
    }

    public void setFont(Font font) {
        super.setFont(font);
        StyledText stx = getStyledText();
        stx.setBaseFont(font);
    }

    public void reshape(int x, int y, int width, int height) {
        super.reshape(x, y, width, height);
        m_updateSize = true;
    }

    public void setDrawable(GenericDrawable drawable) {
        if (!(drawable instanceof StyledText)) {
            return;
        } // end of if ()
        super.setDrawable(drawable);
        StyledText stx = getStyledText();
        super.setName(stx.toString());
        m_updateSize = true;
    }

    public void addFormattedText(Font font, Color color, String string) {
        StyledText sText = getStyledText();
        sText.addRun(new FormattedText(font, color, string));
        m_updateSize = true;
    }

    private void updateText(String text) {
        super.setName(text);
        StyledText styledText = new StyledText();
        FormattedText run = new FormattedText(text);
        styledText.addRun(run);
        setDrawable(styledText);
    }

    protected StyledText getStyledText() {
        StyledText sText = (StyledText) getDrawable();
        if (null == sText) {
            if (null != getName()) {
                updateText(getName());
                sText = (StyledText) getDrawable();
            } else {
                sText = new StyledText();
                setDrawable(sText);
            }
        }
        return sText;
    }

    public void setWrappedRuns(Vector wrappedRuns) {
        m_wrappedRuns = wrappedRuns;
        if (null != m_wrappedRuns) {
            StyledText stx = getStyledText();
            stx.setWrappedRuns(m_wrappedRuns);
        }
    }

    public Vector getWrappedRuns() {
        return m_wrappedRuns;
    }

    public void setUseOffset(boolean useOffset) {
        StyledText sText = getStyledText();
        if (useOffset) {
            sText.setXOffset(sText.getXOffset() + m_xOffset);
            sText.setYOffset(sText.getYOffset() + m_yOffset);
        } else {
            if (sText.getXOffset() >= m_xOffset) {
                sText.setXOffset(sText.getXOffset() - m_xOffset);
            } else {
                sText.setXOffset(0);
            }
            if (sText.getYOffset() >= m_yOffset) {
                sText.setYOffset(sText.getYOffset() - m_yOffset);
            } else {
                sText.setYOffset(0);
            }
        }
    }

    public void setXOffset(int xOffset) {
        m_xOffset = xOffset;
    }

    public void setYOffset(int yOffset) {
        m_yOffset = yOffset;
    }

    public void setName(String text) {
        updateText(text);
        updatePreferredSize();
        setSize(getPreferredSize());
    }

    public void setForeground(Color fgColor) {
        super.setForeground(fgColor);
        StyledText stx = getStyledText();
        stx.setRunColor(0, fgColor);
    }

    public String toString() {
        return getName();
    }

    public Dimension getPreferredSize() {
        if (m_updateSize) {
            updatePreferredSize();
        }

        if (null == m_preferredSize) {
            return getSize();
        }

        return m_preferredSize;
    }

    public void addNotify() {
        super.addNotify();
        // System.out.println("addNotify for label: " + getName());
        if (null == m_preferredSize) {
            m_updateSize = true;
        }
    }

    private void updatePreferredSize() {
        Graphics g = getGraphics();

        if (null != g) {
            Dimension size = getSize();
            StyledText sText = getStyledText();
            m_preferredSize = sText.getPreferredSize(
                                                     g,
                                                     size.width,
                                                     size.height);
            m_updateSize = false;
        }
    }

    public Dimension getMinimumSize() {
        return getSize();
    }

    public void forceUpdateSize() {
        m_updateSize = true;
    }

}// GenericLabel
