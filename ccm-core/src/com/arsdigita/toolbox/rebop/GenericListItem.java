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
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.LayoutManager;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;


/**
 * A component which can be added to generic lists
 *
 *
 * @author Gavin Doughtie
 */
public class GenericListItem extends GenericButton {

    public static final Color DEFAULT_UNSELECTED_COLOR = Color.white;
    public static final Color DEFAULT_SELECTED_COLOR = new Color(0x000d59);

    private int m_currentIndex = -1;
    private Font m_currentFont = null;
    private Color m_textColor = Color.black;
    private Color m_selectedTextColor = Color.white;
    private Color m_rolloverTextColor = Color.blue;

    private Object m_userData = null;

    public GenericListItem() {
        this(null, "", "", null);
    }

    public GenericListItem(
                           Image icon,
                           String label,
                           String text,
                           Object userData) {
        this(icon,
             label,
             text,
             userData,
             null,
             null,
             null);
    }

    public GenericListItem(
                           Image icon,
                           String label,
                           String text,
                           Object userData,
                           GenericDrawable upDrawable,
                           GenericDrawable downDrawable,
                           GenericDrawable overDrawable
                           ) {
        super.init(icon, label);
        setText(text);
        setUserData(userData);
        getLabel().setXOffset(0);
        getLabel().setYOffset(0);
        setUpDrawable(upDrawable);
        setDownDrawable(downDrawable);
        setOverDrawable(overDrawable);
        updateDrawable();
    }

    protected void updateDrawable() {
        if (m_selected) {
            setDrawable(m_downDrawable);
        } else if (m_mouseOver) {
            setDrawable(m_overDrawable);
        } else {
            setDrawable(m_upDrawable);
        }
    }

    protected LayoutManager getDefaultLayout() {
        return new FlowLayout(FlowLayout.LEFT, 2, 2);
    }

    public void setText(String text) {
        StyledText stx = getLabel().getStyledText();
        FormattedText ft = stx.getFormattedTextAt(1);
        if (null == ft) {
            ft = new FormattedText(m_currentFont, getCurrentTextColor(), text);
            stx.setFormattedTextAt(1, ft);
        } else {
            ft.setString(text);
        }
        updateLabelSize();
    }

    public void setUserData(Object userData) {
        m_userData = userData;
    }

    public Object getUserData() {
        return m_userData;
    }

    public String getLabelString() {
        StyledText stx = getLabel().getStyledText();
        FormattedText label = stx.getFormattedTextAt(0);
        String dLabel = "";
        if (null != label) {
            dLabel = label.getString();
        }
        return dLabel;
    }

    public void setLabelString(String newLabel) {
    }

    public String getText() {
        StyledText stx = getLabel().getStyledText();
        String dText = "";
        FormattedText text = stx.getFormattedTextAt(1);
        if (null != text) {
            dText = text.getString();
        }
        return dText;
    }

    public String toString() {
        return getLabelString() + " " + getBounds();
    }

    public Dimension getPreferredSize() {
        Dimension superSize = super.getPreferredSize();
        return new Dimension(getSize().width, superSize.height);
    }

    private FormattedText getFormattedText() {
        StyledText stx = getLabel().getStyledText();
        FormattedText ft = stx.getFormattedTextAt(1);
        return ft;
    }

    public void setTextColor(Color color) {
        m_textColor = color;
    }

    public void setSelectedTextColor(Color color) {
        m_selectedTextColor = color;
    }

    public void setRolloverTextColor(Color color) {
        m_rolloverTextColor = color;
    }

    protected void updateColors() {
        super.updateColors();
        // Checking for null here so this can be called
        // in the superclass constructor without blowing up
        FormattedText ft = getFormattedText();
        if (null != ft && null != getCurrentTextColor()) {
            ft.setColor(getCurrentTextColor());
        }
    }

    protected Color getCurrentTextColor() {
        if (getSelected()) {
            return m_selectedTextColor;
        } else if (getMouseOver()) {
            return m_rolloverTextColor;
        }
        return m_textColor;
    }

    /**
     * @return the index of this item relative to a longer
     * list of data items.
     */
    public int getCurrentIndex() {
        return m_currentIndex;
    }

    public void setCurrentIndex(int index) {
        m_currentIndex = index;
    }

    public void mousePressed(MouseEvent e) {
        setSelected(true);
        updateColors();
        updateDrawable();
        repaint();
        ActionEvent ae = new ActionEvent(this, MouseEvent.MOUSE_PRESSED, "mousePressed");
        processActionEvent(ae);
    }

    public void setSelected(boolean selected) {
        super.setSelected(selected);
        updateColors();
        updateDrawable();
        repaint();
    }

    public void mouseReleased(MouseEvent e) {
        // still nothing
    }

    public void validate() {
        validateTree();
        super.validate();
    }

}// GenericListItem
