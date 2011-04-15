/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.portalserver.ui.admin;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.DimensionalNavbar;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.portalserver.util.GlobalizationUtil;

import com.arsdigita.portalserver.ApplicationPage;
import com.arsdigita.portalserver.PortalSite;

class PortalArchivePage extends ApplicationPage {
    public static final String versionId =
        "$Id: //portalserver/dev/src/com/arsdigita/portalserver/ui/admin/PortalArchivePage.java#4 $" +
        "$Author: dennis $" +
        "$DateTime: 2004/08/17 23:19:25 $";

    public PortalArchivePage() {

        // TODO: parent portal selection after basic properties entered
        //final Component panel = new PortalArchivePanel(this);
        final Component panel = new PortalSiteMapPanel(this);

        add(panel);

    }

    protected void buildContextBar() {
        DimensionalNavbar navbar = new DimensionalNavbar();
        navbar.setClassAttr("portalNavbar");

        navbar.add(new Link(new PersonalPortalLinkPrinter()));
        navbar.add(new Link(new ParentApplicationLinkPrinter()));
        navbar.add(new Label(new CurrentApplicationLabelPrinter()));

        getHeader().add(navbar);
    }

}
