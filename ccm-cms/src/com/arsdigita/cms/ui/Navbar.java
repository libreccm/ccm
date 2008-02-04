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


import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Label;

/**
 * A navigation bar. Unlike a tabbed dialog, the navbar is completely
 * static. All of its links lead to other pages.
 */
public class Navbar extends BoxPanel {

    public static final String versionId = "$Id: Navbar.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/17 23:15:09 $";

    private String currentLabel;

    /**
     * Construct a new, empty navbar
     *
     * @param currentLabel The currently selected label. The label will
     *   appear in bold.
     */
    public Navbar(String currentLabel) {
        super(HORIZONTAL);
        setBorder(0);

        this.currentLabel = currentLabel;
    }

    /**
     * Construct a new, empty navbar
     *
     */
    public Navbar() {
        this(null);
    }

    /**
     * Add a label to the navbar
     *
     * @param label The string label
     * @param url The URL of the link where the label will link to
     *
     */
    public void add(String label, String url) {

        if(size() > 0) {
            add(new Label(" | "));
        }

        if(label != null && label.equalsIgnoreCase(currentLabel)) {
            Label boldLabel = new Label(label);
            boldLabel.setFontWeight(Label.BOLD);
            add(boldLabel);
        } else {
            add(new com.arsdigita.bebop.Link(label, url));
        }
    }

    /**
     * @return The currently selected label
     */
    public String getCurrentLabel() { return currentLabel; }
}
