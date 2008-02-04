/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.ui.permissions;


import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.DimensionalNavbar;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.kernel.ACSObject;

/**
 *
 * Component that Renders the Header of the Permissions Admin pages
 *
 * @author sdeusch@arsdigita.com
 * @version $Id: CMSPermissionsHeader.java 287 2005-02-22 00:29:02Z sskracic $
 */

class CMSPermissionsHeader extends BoxPanel implements CMSPermissionsConstants {

    private Label m_title;
    private CMSPermissionsPane m_parent;

    /**
     * Constructor
     */

    CMSPermissionsHeader(CMSPermissionsPane parent) {
        m_parent = parent;
        m_title = new Label();
        m_title.addPrintListener(new PrintListener() {
                public void prepare(PrintEvent e) {
                    Label t= (Label) e.getTarget();
                    t.setLabel(PAGE_TITLE.localize() +" " +
                               getObjectName(e));
                }
            });
        m_title.setClassAttr("heading");
        add(m_title);

        // Used to render the object name in the navbar

        Label objectName = new Label();
        objectName.addPrintListener(new PrintListener() {
                public void prepare(PrintEvent e) {
                    Label t= (Label) e.getTarget();
                    t.setLabel(getObjectName(e));
                }
            });

        DimensionalNavbar navbar = new DimensionalNavbar();
        navbar.add(new Link(PERSONAL_SITE.localize()+"", "/pvt/home"));
        navbar.add(new Link(MAIN_SITE.localize()+"", "/"));
        navbar.add(new Link(PERMISSIONS_INDEX.localize()+"", "/permissions/"));
        navbar.add(objectName);
        navbar.setClassAttr("permNavBar");
        add(navbar);
    }

    private String getObjectName(PrintEvent e) {
        PageState s = e.getPageState();
        ACSObject obj = m_parent.getObject(s);
        String objName = obj.getDisplayName() +
            " (ID " + obj.getID()+")";
        return objName;
    }

    /**
     * Returns the object used to render the title of the panel.
     */

    Label getTitle() {
        return m_title;
    }
}
