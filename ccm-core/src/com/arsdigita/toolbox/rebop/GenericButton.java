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

import java.awt.AWTEventMulticaster;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.LayoutManager;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

public class GenericButton extends GenericComponent {

    public static final int STANDARD_PUSH_OFFSET = 2;
    private GenericComponent m_contentPane = new GenericComponent();
    private GenericImage m_icon = null;
    private Image m_upImage = null;
    private Image m_overImage = null;
    private Image m_downImage = null;

    private int m_hGap = STANDARD_PUSH_OFFSET;
    private int m_vGap = STANDARD_PUSH_OFFSET;

    protected GenericDrawable m_upDrawable = new BevelBox(
                                                          Color.lightGray, Color.white, Color.gray, m_hGap, false);

    protected GenericDrawable m_overDrawable = m_upDrawable;

    protected GenericDrawable m_downDrawable = new BevelBox(
                                                            Color.lightGray, Color.white, Color.gray, m_hGap, true);

    protected GenericDrawable m_disabledDrawable = new BevelBox(
                                                                null, Color.blue, Color.blue, 0, true);

    private GenericLabel m_label;

    protected boolean m_selected = false;
    protected boolean m_mouseOver = false;
    private boolean m_toggleButton = false;
    private boolean m_unlatch = false;

    private Color m_labelColor = Color.blue;
    private Color m_selectedLabelColor = Color.white;
    private Color m_rolloverLabelColor = Color.yellow;
    private Color m_disabledLabelColor = Color.gray;

    private ActionListener m_actionListeners;

    private Dimension m_preferredSize = null;

    public GenericButton() {
        init(null, "", STANDARD_PUSH_OFFSET, STANDARD_PUSH_OFFSET);
    }

    public GenericButton(String label) {
        init(null, label, STANDARD_PUSH_OFFSET, STANDARD_PUSH_OFFSET);
    }

    public GenericButton(Image icon, String label) {
        init(icon, label, STANDARD_PUSH_OFFSET, STANDARD_PUSH_OFFSET);
    }

    public GenericButton(Image icon, String label, int hGap, int vGap) {
        init(icon, label, hGap, vGap);
    }

    public void setLabel(String label) {
        m_label.setName(label);
        doLayout();
    }

    public void addFormattedText(Font font, Color color, String string) {
        m_label.addFormattedText(font, color, string);
        updateLabelSize();
    }

    public void setFont(Font font) {
        super.setFont(font);
        m_label.setFont(font);
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        updateColors();
        updateDrawable();
        repaint();
    }

    public void setLabelColor(Color color) {
        m_labelColor = color;
        updateColors();
    }

    public void setSelectedLabelColor(Color color) {
        m_selectedLabelColor = color;
        updateColors();
    }

    public void setRolloverLabelColor(Color color) {
        m_rolloverLabelColor = color;
        updateColors();
    }

    public void setIcon(Image icon) {
        m_icon.setImage(icon);
        m_contentPane.doLayout();
    }

    public void setToggleButton(boolean toggle) {
        m_toggleButton = toggle;
    }

    public void doLayout() {
        updateLabelSize();
        m_contentPane.doLayout();
        super.doLayout();
    }

    public boolean getToggleButton() {
        return m_toggleButton;
    }

    public GenericLabel getLabel() {
        return m_label;
    }

    public boolean getSelected() {
        return m_selected;
    }

    public void setSelected(boolean selected) {
        m_selected = selected;
    }

    public boolean getMouseOver() {
        return m_mouseOver;
    }

    public void setMouseOver(boolean mouseOver) {
        m_mouseOver = mouseOver;
    }

    public void setUpDrawable(GenericDrawable upDraw) {
        m_upDrawable = upDraw;
    }

    public void setDownDrawable(GenericDrawable downDraw) {
        m_downDrawable = downDraw;
    }

    public void setOverDrawable(GenericDrawable overDraw) {
        m_overDrawable = overDraw;
    }

    protected void init(Image icon, String label) {
        init(icon, label, STANDARD_PUSH_OFFSET, STANDARD_PUSH_OFFSET);
    }

    protected void init(Image icon, String label, int hGap, int vGap) {
        m_icon = new GenericImage(icon);
        m_icon.setXOffset(STANDARD_PUSH_OFFSET);
        m_icon.setYOffset(STANDARD_PUSH_OFFSET);
        m_hGap = hGap;
        m_vGap = vGap;
        m_upImage = icon;
        m_overImage = m_upImage;
        m_downImage = m_upImage;
        genericImageInit(m_icon, label);
    }

    protected LayoutManager getDefaultLayout() {
        return new GridBagLayout();
    }

    protected void genericImageInit(GenericImage icon, String label) {
        setLayout(getDefaultLayout());
        // m_contentPane.setLayout(new FlowLayout(FlowLayout.LEFT, m_hGap, m_vGap));
        m_contentPane.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();

        m_label = new GenericLabel();

        addFormattedText(getFont(), m_labelColor, label);
        updateLabelSize();

        c.anchor = GridBagConstraints.NORTHWEST;
        m_contentPane.add(m_icon, c);
        m_contentPane.add(m_label, c);
        add(m_contentPane);

        addMouseListener(this);
        updateColors();
        updateDrawable();
    }

    public String toString() {
        return "GenericButton -- " + m_label.getName();
    }

    public void update(Graphics g) {
        paint(g);
    }

    public void addNotify() {
        super.addNotify();
        updateLabelSize();
        updateDrawable();
        forceInvalid();
    }

    protected void updateLabelSize() {
        forceInvalid();
        int labelHeight = m_label.getSize().height;
        int myWidth = getSize().width;
        int labelWidth = myWidth - (2 * m_hGap) -
            m_icon.getPreferredSize().width;

        if (0 >= labelWidth) {
            labelWidth = 4 * m_hGap;
        }

        m_label.setSize(labelWidth, labelHeight);
        int newWidth = m_label.getPreferredSize().width;
        if (newWidth != labelWidth) {
            m_label.setSize(newWidth + (2 * m_hGap), labelHeight);
        }
    }

    public void forceInvalid() {
        super.forceInvalid();
        m_label.forceUpdateSize();
        m_preferredSize = null;
    }

    public Dimension getPreferredSize() {
        if (null == m_preferredSize) {
            int height = m_label.getPreferredSize().height + (4 * m_vGap);
            int preferredWidth = m_label.getPreferredSize().width + (4 * m_hGap);
            Dimension preferredIconSize = m_icon.getPreferredSize();

            height = Math.max(height, (preferredIconSize.height + (m_vGap * 2)));
            int iconWidth = m_icon.getPreferredSize().width;

            if (iconWidth > 0) {
                iconWidth += m_hGap;
                preferredWidth += iconWidth;
            }

            int width = getSize().width;

            m_preferredSize = new Dimension(width, height);

            //              System.out.println("height: " + height +
            //                                 " preferredWidth: " + preferredWidth +
            //                                 " preferredIconSize: " + preferredIconSize +
            //                                 " m_preferredSize: " + m_preferredSize);
        }
        return m_preferredSize;
    }

    protected void toggleSelected() {
        if (!isEnabled()) {
            return;
        }

        m_selected = !m_selected;
        m_label.setUseOffset(m_selected);
        m_icon.setUseOffset(m_selected);
        updateDrawable();
        updateColors();
        repaint();
    }

    protected void updateDrawable() {
        if (!isEnabled()) {
            setDrawable(m_disabledDrawable);
        } else if (m_selected) {
            setDrawable(m_downDrawable);
        } else if (m_mouseOver) {
            setDrawable(m_overDrawable);
        } else {
            setDrawable(m_upDrawable);
        }
    }

    protected void updateColors() {
        m_label.setForeground(getCurrentLabelColor());
    }

    protected Color getCurrentLabelColor() {
        if (!isEnabled()) {
            return m_disabledLabelColor;
        } else if (m_selected) {
            return m_selectedLabelColor;
        } else if (m_mouseOver) {
            return m_rolloverLabelColor;
        }
        return m_labelColor;
    }

    protected void toggleMouseOver(boolean mouseOver) {
        m_mouseOver = mouseOver;
        updateColors();
        updateDrawable();
        repaint();
    }

    public void mouseEntered(MouseEvent e) {
        toggleMouseOver(true);
    }

    public void mouseExited(MouseEvent e) {
        toggleMouseOver(false);
    }

    public void mousePressed(MouseEvent e) {
        if (m_toggleButton && !m_selected) {
            toggleSelected();
            ActionEvent ae = new ActionEvent(this, MouseEvent.MOUSE_PRESSED, "");
            processActionEvent(ae);
            super.mousePressed(e);
        } else {
            m_unlatch = true;
            toggleSelected();
        }

        repaint();
    }

    public void mouseReleased(MouseEvent e) {
        if (!m_toggleButton) {
            toggleSelected();
            ActionEvent ae = new ActionEvent(this, MouseEvent.MOUSE_RELEASED, "");
            processActionEvent(ae);
            super.mousePressed(e);
            repaint();
        } else if (m_selected) {
            if (m_unlatch) {
                toggleSelected();
                m_unlatch = false;
            } // end of if ()
            repaint();
        }
    }

    public void addActionListener(ActionListener l) {
        m_actionListeners = AWTEventMulticaster.add(m_actionListeners, l);
    }

    protected void processActionEvent(ActionEvent e) {
        if (null != m_actionListeners) {
            m_actionListeners.actionPerformed(e);
        }
    }

}// GenericButton
