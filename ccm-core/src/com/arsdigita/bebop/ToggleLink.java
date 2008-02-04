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

import java.util.Collections;
import java.util.Iterator;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.util.Assert;
import com.arsdigita.xml.Element;
import org.apache.log4j.Logger;

/**
 * A component that is either selected or not. By default, a link is only
 * generated when the component is not selected. When it is selected, only
 * a label is printed.
 *
 * <p>See {@link BaseLink} for a description
 * of all Bebop Link classes.
 *
 * @author David Lutterkort
 * @version $Id: ToggleLink.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class ToggleLink extends ControlLink {
    public static final String versionId =
        "$Id: ToggleLink.java 287 2005-02-22 00:29:02Z sskracic $" +
        "$Author: sskracic $" +
        "$DateTime: 2004/08/16 18:10:38 $";

    private static final Logger s_log = Logger.getLogger(ToggleLink.class);

    /**
     * The value for the XML type attribute for a {@link ToggleLink}
     */
    protected final String TYPE_TOGGLE = "toggle";

    /**
     * The name of the string parameter in which we keep the state.
     */
    private final String STATE_NAME="state";

    /**
     * The name of the control event we use to toggle the button's state.
     */
    private final String TOGGLE_EVENT="toggle";

    /**
     * The string parameter that keeps the state of the toggle link.
     * A value of <code>null</code> means that the link is not selected, any
     * other value means it is selected.
     */
    private StringParameter m_state;

    /**
     * The component to display when the button is selected.
     */
    private Component m_selectedComponent;

    /**
     * Create a <code>ToggleLink</code> that uses <code>child</code>
     * to label the link it generates when it is not selected, and
     * that displays <code>child</code> by itself when it is
     * selected. The <code>child</code> is selected by default.
     *
     * @param child the component used to label this link
     */
    public ToggleLink(Component child) {
        super(child);
        m_state = new StringParameter(STATE_NAME);
        m_selectedComponent = child;
        setTypeAttr(TYPE_TOGGLE);
    }

    /**
     * Creates a <code>ToggleLink</code> that uses a <code>Label</code>
     * containing <code>label</code> to label the link it generates when it
     * is not selected, and that displays just the label by itself when it is
     * selected.
     *
     * @param label the string used to label this link
     */
    public ToggleLink(String label) {
        this(new Label(label));
    }

    /**
     * Registers the link and its state with the page.
     *
     * @param p the page that contains this link
     */
    public void register(Page p) {
        s_log.debug("Registering with the page");

        super.register(p);

        p.addComponent(this);
        p.addComponentStateParam(this, m_state);
    }

    /**
     * Responds to the incoming request represented by <code>s</code>. Changes
     * whether the link is selected or not according to what is indicated in
     * <code>s</code>. Fires an {@link com.arsdigita.bebop.event.ActionEvent}
     * after updating its state.
     *
     * @param s represents the current request
     */
    public void respond(PageState s) {
        String event = s.getControlEventName();

        if (TOGGLE_EVENT.equals(event)) {
            setSelected(s, s.getControlEventValue() != null);
        } else {
            throw new IllegalArgumentException("Unknown event '" + event + "'");
        }

        fireActionEvent(s);
    }

    /**
     * Returns the selected component if it has been set.
     *
     * @return an iterator over the link's children.
     */
    public Iterator children() {
        if (getSelectedComponent() != m_child) {
            return Collections.singletonList(getSelectedComponent()).iterator();
        } else {
            return Collections.EMPTY_LIST.iterator();
        }
    }

    /**
     * Returns <code>true</code> if the link is currently selected in the request
     * represented by <code>s</code>
     *
     * @param s describes the current request
     * @return <code>true</code> if the link is selected;
     * <code>false</code> otherwise.
     */
    public boolean isSelected(PageState s) {
        final boolean result = s.getValue(m_state) != null;

        if (s_log.isDebugEnabled()) {
            s_log.debug(this + " is selected: " + result);
        }

        return result;
    }

    /**
     * Sets whether the link is selected in the context of the request
     * described by <code>s</code>.
     *
     * @param s describes the current request
     * @param v <code>true</code> if the link is currently selected
     */
    public void setSelected(PageState s, boolean  v) {
        if (v) {
            s_log.debug("Setting the toggle link selected");

            s.setValue(m_state, "1");
        } else {
            s_log.debug("Deselecting the toggle link");

            s.setValue(m_state, null);
        }
    }

    /**
     * Gets the component that is displayed if the link is selected.
     * @return the component to display if the link is selected.
     */
    public final Component getSelectedComponent() {
        return m_selectedComponent;
    }

    /**
     * Sets the component that is displayed if the link is selected.
     * @param v the component to display if the link is selected
     * @pre ! isLocked()
     */
    public void setSelectedComponent(Component  v) {
        Assert.assertNotLocked(this);

        m_selectedComponent = v;
    }

    public void generateXML(PageState s, Element e) {
        if ( isVisible(s) ) {
            if (isSelected(s)) {
                s.setControlEvent(this, TOGGLE_EVENT, null);
                m_selectedComponent.generateXML(s, e);
            } else {
                s.setControlEvent(this, TOGGLE_EVENT, "1");
                super.generateXML(s, e);
            }
            s.clearControlEvent();
        }
    }

    /**
     * Sets the page state's control event so that generated links cause this
     * link's selected state to be toggled when the user clicks them.
     *
     * @param ps the current page state
     */
    public void setControlEvent(PageState s) {
        s.setControlEvent(this, TOGGLE_EVENT, isSelected(s) ? null : "1");
    }

    /**
     * Adds a <code>selected</code> attribute to the standard XML generated by
     * {@link Link#generateXML Link}. The value of the attribute is either
     * <code>yes</code> or <code>no</code>, reflecting whether the link is
     * selected or not.
     *
     * @param s a <code>PageState</code> value
     * @param link an <code>Element</code> value
     */
    protected void generateExtraXMLAttributes(PageState s, Element link) {
        link.addAttribute("selected", isSelected(s) ? "yes" : "no");
    }


}
