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
 */

package com.arsdigita.london.navigation.ui;

import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.FilterFactory;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.Resource;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;

import com.arsdigita.london.navigation.Navigation;
import com.arsdigita.london.navigation.QuickLink;

import com.arsdigita.xml.Element;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.List;

import com.arsdigita.categorization.Category;
import com.arsdigita.cms.ContentItemXMLRenderer;

public class QuickLinks extends AbstractComponent {

    public Element generateXML(HttpServletRequest request,
                               HttpServletResponse response) {
        List ids = new ArrayList();
        Category[] path = getModel().getCategoryPath();
        Category current = getModel().getCategory();
        if (path == null || path.length == 0) {
            return null;
        }

        for (int i = 0 ; i < path.length ; i++) {
            ids.add(path[i].getID());
        }

        DataCollection objs = SessionManager.getSession().retrieve
            (QuickLink.BASE_DATA_OBJECT_TYPE);

            FilterFactory factory = objs.getFilterFactory();

              Filter cascaded = factory.and().addFilter(factory.equals(QuickLink.CASCADE, Boolean.TRUE)).addFilter(factory.simple(Category.CATEGORIES + "." + ACSObject.ID +
              " in :categoryIDs").set("categoryIDs", ids));

              Filter direct = factory.and().addFilter(factory.equals(QuickLink.CASCADE, Boolean.FALSE)).addFilter(factory.equals(Category.CATEGORIES + "." + ACSObject.ID, current.getID()));

              objs.addFilter(factory.or().addFilter(cascaded).addFilter(direct));


           /*   objs.addFilter(Category.CATEGORIES + "." + ACSObject.ID +
                        " in :categoryIDs")
                  .set("categoryIDs", ids); */  objs.addOrder(Category.CATEGORIES + "." +
                       Category.DEFAULT_ANCESTORS + " desc");
        objs.addOrder(Category.CATEGORIES + ".link." + Category.SORT_KEY);

        DomainCollection links = new DomainCollection(objs);

        Element linksEl = Navigation.newElement("quickLinks");
        // chack if current user has edit privilege
        Party party = Kernel.getContext().getParty();
        if (party == null) {
            party = Kernel.getPublicUser();
        }
        Resource resource = Kernel.getContext().getResource();
        PermissionDescriptor edit = new PermissionDescriptor(PrivilegeDescriptor.EDIT, resource, party);
        if (PermissionService.checkPermission(edit)) {
            linksEl.newChildElement("editor");
        }
        while (links.next()) {
            Element objEl = Navigation.newElement("quickLink");

            ContentItemXMLRenderer xr = new ContentItemXMLRenderer(objEl);
            xr.setNamespace(Navigation.NAV_PREFIX,
                            Navigation.NAV_NS);
            xr.setWrapRoot(false);
            xr.setWrapAttributes(true);
            xr.setWrapObjects(false);

            xr.walk(links.getDomainObject(), QuickLinks.class.getName());

            linksEl.addContent(objEl);
        }

        return linksEl;
    }
}
