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
package com.arsdigita.bebop.form;

import com.arsdigita.bebop.BlockStylable;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.parameters.ParameterData;
import com.arsdigita.util.Assert;
import com.arsdigita.bebop.util.BebopConstants;
import com.arsdigita.xml.Element;

/**
 *  A class representing
 * an option of a widget.
 *
 * @author Rory Solomon   
 * @author Michael Pih    
 *
 * $Id: Option.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class Option extends BlockStylable {

    private String m_value;
    private OptionGroup m_group;
    private Component m_component;
    private boolean m_isSelectOption;

    public Option(String label) {
        this(label, label);
    }


    /**
     *  This creates an Option whose component is a label consisting of the
     *  string that is passed in.
     */
    public Option(String value, String label) {
        setLabel(label);
        setValue(value);
    }

    public Option(String value, Component component) {
        setComponent(component);
        setValue(value);
    }

    public String getName() {
        return m_group.getName();
    }


    /**
     *  If the component is a Label (which most of the time it is)
     *  then this returns the value of the label.  This assumes
     *  that the Component is a lable
     *
     *  @exception ClassCastException is thrown if the component is not
     *             a label
     */
    public final String getLabel() {
        return ((Label)m_component).getLabel();
    }

    /**
     *  This sets the component to the label consisting of the passed in
     *  string
     */
    public final void setLabel(String label) {
        setComponent(new Label(label));
    }

    /**
     *  @deprecated Use {@link #setComponent(Component component)} instead
     */
    public final void setLabel(Label label) {
        setComponent(label);
    }

    public final void setComponent(Component component) {
        Assert.isUnlocked(this);
        m_component = component;
    }

    public final Component getComponent() {
        return m_component;
    }

    public final void setGroup(OptionGroup group) {
        Assert.isUnlocked(this);
        Assert.exists(group);
        m_group = group;
        m_isSelectOption =
            BebopConstants.BEBOP_OPTION.equals(m_group.m_xmlElement);
    }

    public final OptionGroup getGroup() {
        return m_group;
    }

    public final String getValue() {
        return m_value;
    }

    public final void setValue(String value) {
        m_value = value;
    }

    /**
     * Sets the <tt>ONFOCUS</tt> attribute for the HTML tags that compose
     * this element.
     */
    public void setOnFocus(String javascriptCode) {
        setAttribute(Widget.ON_FOCUS,javascriptCode);
    }

    /**
     * Sets the <tt>ONBLUR</tt> attribute for the HTML tags that compose
     * this element.
     */
    public void setOnBlur(String javascriptCode) {
        setAttribute(Widget.ON_BLUR,javascriptCode);
    }

    /**
     * Sets the <tt>ONSELECT</tt> attribute for the HTML tags that compose
     * this element.
     */
    public void setOnSelect(String javascriptCode) {
        setAttribute(Widget.ON_SELECT,javascriptCode);
    }

    /**
     * Sets the <tt>ONCHANGE</tt> attribute for the HTML tags that compose
     * this element.
     */
    public void setOnChange(String javascriptCode) {
        setAttribute(Widget.ON_CHANGE,javascriptCode);
    }


    /**
     * Sets the <tt>ON_KEY_UP</tt> attribute for the HTML tags that compose
     * this element.
     **/

    public void setOnKeyUp(String javascriptCode) {
        setAttribute(Widget.ON_KEY_UP, javascriptCode);
    }

    /**
     * Sets the <tt>ONCLICK</tt> attribute for the HTML tags that compose
     * this element.
     */
    public void setOnClick(String javascriptCode) {
        setAttribute(Widget.ON_CLICK,javascriptCode);
    }

    private ParameterData getParameterData(PageState s) {
        return m_group.getParameterData(s);
    }

    public boolean isSelected(ParameterData data) {
        if (data == null || data.getValue() == null) {
            return false;
        }
        Object value = data.getValue();

        Object[] selectedValues;
        if (value instanceof Object[]) {
            selectedValues = (Object[])value;
        } else {
            selectedValues = new Object[] {value};
        }
        String optionValue = getValue();

        if (optionValue == null || selectedValues == null) {
            return false;
        }

        for (int i=0; i<selectedValues.length; i++) {
            if (selectedValues[i] != null &&
                optionValue.equalsIgnoreCase(selectedValues[i].toString())
                ) {
                return true;
            }
        }
        return false;
    }

    /**
     * Generate XML depending on what OptionGroup we belong to.
     */
    public void generateXML(PageState s, Element e) {
        Element option = e.newChildElement(m_group.m_xmlElement, BEBOP_XML_NS);
        if ( ! m_isSelectOption ) {
            option.addAttribute("name", getName());
        }
        option.addAttribute("value", getValue());

        if (m_component != null) {
            m_component.generateXML(s, option);
        } else {
            (new Label()).generateXML(s, option);
        }

        exportAttributes(option);
        if ( isSelected(getParameterData(s)) ) {
            if ( m_isSelectOption ) {
                option.addAttribute("selected", "selected");
            } else {
                option.addAttribute("checked", "checked");
            }
        }
    }

    /**
     * Kludge to live with the fact that options don't do their own
     * printing. Don't use this method, it will go away !
     * @deprecated Will be removed without replacement once option handling
     *   has been refactored.
     * 
     */
    final void generateAttributes(Element target) {
        exportAttributes(target);
    }

}
