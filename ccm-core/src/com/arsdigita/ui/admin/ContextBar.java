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
package com.arsdigita.ui.admin;

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.globalization.GlobalizedMessage;

/**
 * A Simple context bar for site wide admin.
 *
 * @deprecated User DimensionalNavbar in com.arsdigita.bebop.DimensionalNavbar
 * @author David Dao
 * @version $Id: ContextBar.java 287 2005-02-22 00:29:02Z sskracic $ $DateTime: 2004/08/16 18:10:38 $
 * @since
 */

public class ContextBar extends BoxPanel implements AdminConstants {

    public static final String versionId = "$Id: ContextBar.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    // String catalog
    private static final GlobalizedMessage WORKSPACE =
        new GlobalizedMessage("ui.admin.contextBar.workspace", BUNDLE_NAME);
    private static final GlobalizedMessage HOME      =
        new GlobalizedMessage("ui.admin.contextBar.home", BUNDLE_NAME);
    private static final GlobalizedMessage SEPARATOR =
        new GlobalizedMessage("ui.admin.contextBar.Separator", BUNDLE_NAME);

    // Site node name.
    private static final GlobalizedMessage SITE_NODE_NAME =
        new GlobalizedMessage("ui.admin.contextBar.siteNode", BUNDLE_NAME);

    /**
     * Constructor.
     */

    public ContextBar() {
        this(false);
    }

    /**
     * Constructor.
     *
     * @param isFinal determines whether additional elements can be
     * added to the ContextBar, and whether the default final element
     * is a Label or a Link
     */

    public ContextBar(boolean isFinal) {

        super(BoxPanel.HORIZONTAL);

        // Set the class so this element be customized using XSL
        setClassAttr("ContextBar");

        add(new Link(new Label(WORKSPACE), "/pvt/home"));
        add(HOME, "/");

        if (isFinal) {
            add(SITE_NODE_NAME);
        } else {
            add(SITE_NODE_NAME, "/admin/");
        }
    }

    /**
     * Add a link to the ContextBar.  The link will be preceded by a
     * ContextBar.SEPARATOR.
     */

    public void add(String name, String url) {
        add(new Label(SEPARATOR));
        add(new Link(name, url));
    }

    /**
     * Add a simple label to the ContextBar.  The label will be preceeded by a
     * ContextBar.SEPARATOR.
     */

    public void add(String name) {
        add(new Label(SEPARATOR));
        add(new Label(name));
    }

    /**
     * Add a link to the ContextBar.  The link will be preceded by a
     * ContextBar.SEPARATOR.
     */
    public void add(GlobalizedMessage name, String url) {
        add(new Label(SEPARATOR));
        add(new Link(new Label(name), url));
    }

    /**
     * Add a simple label to the ContextBar.  The label will be preceeded by a
     * ContextBar.SEPARATOR.
     */
    public void add(GlobalizedMessage name) {
        add(new Label(SEPARATOR));
        add(new Label(name));
    }
}
