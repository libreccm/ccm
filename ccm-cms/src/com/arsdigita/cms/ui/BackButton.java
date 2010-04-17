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
package com.arsdigita.cms.ui;

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.Image;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.dispatcher.Utilities;

import javax.servlet.ServletException;


/**
 * Sticks a back button in the upper right hand corner of the component.
 * Just override the back(PageState s) method to use.
 *
 * @author Michael Pih (pihman@arsdigita.com)
 * @version $Id: BackButton.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class BackButton extends ColumnPanel {

    private final static String BACK_EVENT = "back";
    private final static String IMAGE_URL =
        Utilities.getGlobalAssetsURL() + "back.gif";

    private Component m_child;
    private ControlLink m_back;

    public BackButton(Component child) {
        super(2);
        setClassAttr("CMS Admin");

        m_child = child;
        add(m_child, ColumnPanel.TOP);

        Image img = new Image(IMAGE_URL);
        img.setBorder("0");
        img.setAlt("Back");
        m_back = new Back(img);
        add(m_back, ColumnPanel.TOP | ColumnPanel.RIGHT);
    }

    /**
     * Remove the selected member from the selected group.
     */
    public void respond(PageState state) throws ServletException {
        String event = state.getControlEventName();

        if ( BACK_EVENT.equals(event) ) {
            back(state);
        }
    }


    /**
     * Called whenever the back button is clicked.
     *
     * Override this method.
     *
     * @param state The page state
     */
    protected void back(PageState state) {
        return;
    }

    /**
     * Register the control event listener to this page.
     */
    public void register(Page p) {
        p.addComponent(this);
    }


    /**
     * The back button.
     */
    private class Back extends ControlLink {

        public Back(Component c) {
            super(c);
        }

        public void setControlEvent(PageState s) {
            s.setControlEvent(BackButton.this, BACK_EVENT, BACK_EVENT);
        }
    }

}
