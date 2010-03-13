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
package com.arsdigita.bebop;


import com.arsdigita.bebop.util.GlobalizationUtil ; 


import com.arsdigita.util.Assert;
import com.arsdigita.xml.Element;

/**
 * Consists of two table cells with a dividing
 * bar in the middle. This container is used only for layout.
 * It is intended to be used as a parent class for a wizard-type
 * SplitWizard component.
 * <p>
 *
 * This container contains three components: "left", "right" and "header".
 * All three components must be present (non-null) before <code>SplitPanel</code>
 * is isLocked. An exception will be thrown if this is not the case.
 *
 * @author Stanislav Freidin 
 * @version $Id: SplitPanel.java 287 2005-02-22 00:29:02Z sskracic $
 */

public class SplitPanel extends SimpleContainer {

    private Component m_left, m_right, m_header;
    private int m_divider;

    /**
     * The border attribute.
     */
    public final static String BORDER = "border";

    /**
     * Constructs a new, empty SplitPanel.
     */
    public SplitPanel() {
        this(new Label("&nbsp;", false), new Label("&nbsp;", false), new Label("&nbsp;", false));
    }

    /**
     * Constructs a new SplitPanel with the specified left and right
     * components.
     */
    public SplitPanel(Component left, Component right) {
        this(new Label("&nbsp;", false), left, right);
    }

    /**
     * Constructs a new SplitPanel with the specified left, right and header
     * components.
     */
    public SplitPanel(Component header, Component left, Component right) {
        super();
        setDivider(25);
        if (header != null) {
            setHeader(header);
        }
        if (left != null) {
            setLeftComponent(left);
        }
        if (right != null) {
            setRightComponent(right);
        }
        setAttribute("cellpadding", "5");
        setAttribute("cellspacing" , "0");
        setAttribute("width", "100%");
        setBorder(true);
    }

    /**
     * Sets the divider position. The position must be an integer in
     * the range of 0 (all the way to the left) to 100
     * (all the way to the right).
     *
     * @param divider the position of the divider
     */
    public void setDivider(int divider) {
        Assert.isUnlocked(this);

        if (divider < 0 || divider > 100) {
            throw new IllegalArgumentException("Divider must be in range 0..100");
        }

        m_divider = divider;
    }

    /**
     * Retrieves the divider position
     *
     * @return the divider position in HTML, such as "25%".
     */
    public final int getDivider() {
        return m_divider;
    }

    /**
     * 
     * Sets the border.
     * @param hasBorder <code>true</code> if the split panel
     * will have a border
     *
     */
    public void setBorder(boolean border) {
        setAttribute(BORDER, border ? "1" : "0");
    }

    /**
     * 
     * Determine whether this panel has a border.
     *
     * @return <code>true</code> if the split panel has a border;
     * <code>false</code> otherwise.
     */
    public boolean getBorder() {
        String border = getAttribute(BORDER);
        return ("1".equals(border));
    }

    /**
     * Gets the left component.
     *
     * @return the component on the left.
     */
    public final Component getLeftComponent() {
        return m_left;
    }

    /**
     * Gets the right component.
     *
     * @return the component on the right.
     */
    public final Component getRightComponent() {
        return m_right;
    }

    /**
     * Gets the header component.
     *
     * @return the component at the top.
     */
    public final Component getHeader() {
        return m_header;
    }

    /**
     * Sets the header. Will throw an IllegalStateException
     * if the header component has already been set.
     *
     * @param c the new component to be put in the header
     */
    public void setHeader(Component c) {
        Assert.isUnlocked(this);

        if (!super.contains(c)) {
            super.add(c);
        }

        m_header = c;
    }

    /**
     * Sets the left component. Will throw an IllegalStateException
     * if the left component has already been set.
     *
     * @param c the new component to be put in the left slot
     */
    public void setLeftComponent(Component c) {
        Assert.isUnlocked(this);

        if (!super.contains(c)) {
            super.add(c);
        }

        m_left = c;
    }

    /**
     * Sets the right component. Will throw an IllegalStateException
     * if the right component has already been set.
     *
     * @param c the new component to be put in the right slot
     */
    public void setRightComponent(Component c) {
        Assert.isUnlocked(this);

        if (!super.contains(c)) {
            super.add(c);
        }

        m_right = c;
    }

    /**
     * Generates XML for the panel. The DOM fragment will look
     * like the following:
     * <p><code><pre>
     * &lt;bebop:splitPanel&gt;
     *   &lt;XML for the left component /&gt;
     *   &lt;XML for the right component /&gt;
     * &lt;/bebop:splitPanel&gt;</pre></code>
     * @param state the current page state
     * @param parent the parent under which the XML should be placed
     *
     */
    public void generateXML(PageState state, Element parent) {

        if ( ! isVisible(state) ) {
            return;
        }

        Element panel = parent.newChildElement("bebop:splitPanel", BEBOP_XML_NS);
        exportAttributes(panel);
        panel.addAttribute("divider_left", Integer.toString(m_divider) + "%");
        panel.addAttribute("divider_right", Integer.toString(100 - m_divider) + "%");
        Element header = panel.newChildElement("bebop:cell", BEBOP_XML_NS);
        Element left = panel.newChildElement("bebop:cell", BEBOP_XML_NS);
        Element right = panel.newChildElement("bebop:cell", BEBOP_XML_NS);
        getHeader().generateXML(state, header);
        getLeftComponent().generateXML(state, left);
        getRightComponent().generateXML(state, right);
    }

    /**
     * Verifies that the header, left, and right components exist.
     */
    public void lock() {
        Assert.exists(getHeader(), "Header");
        Assert.exists(getLeftComponent(), "Left Component");
        Assert.exists(getRightComponent(), "Right Component");
        super.lock();
    }
}
