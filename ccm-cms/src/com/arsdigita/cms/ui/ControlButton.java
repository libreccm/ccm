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

import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.Image;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.cms.dispatcher.Utilities;


/**
 * Creates a {@link ToggleLink} with a button-looking image
 * as a child
 * 
 * @version $Id: ControlButton.java 754 2005-09-02 13:26:17Z sskracic $
 */
public class ControlButton extends ControlLink {

    /**
     * The URL of an "Edit" button
     */
    public static final String EDIT = "admin/button/edit.gif";

    /**
     * The URL of an "Add" button
     */
    public static final String ADD = "admin/button/add.gif";

    /**
     * The URL of an "Add Subcategory" button
     */
    public static final String ADD_SUBCATEGORY = "admin/button/add-subcategory.gif";

    /**
     * The URL of a "Delete" button
     */
    public static final String DELETE = "admin/button/delete.gif";


    /**
     * Construct a new ControlButton
     *
     * @param url The URL to the button's image
     */
    public ControlButton(String url) {
        super(new Image(Utilities.getGlobalAssetsURL() + url));
        Image img = (Image)super.getChild();
        img.setBorder("0");
    }

    /**
     * Construct a new ControlButton
     *
     * @param url The URL to the button's image
     * @param l an {@link ActionListener} that will
     */
    public ControlButton(String url, ActionListener l) {
        this(url);
        addActionListener(l);
    }

    // make sure the button responds to events
    public void setControlEvent(PageState s) {
        s.setControlEvent(this);
    }
}
