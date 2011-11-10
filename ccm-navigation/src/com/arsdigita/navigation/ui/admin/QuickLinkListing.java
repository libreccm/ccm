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

package com.arsdigita.navigation.ui.admin;

import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.categorization.Category;
import com.arsdigita.bebop.PageState;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.london.util.ui.AbstractDomainObjectList;
import com.arsdigita.london.util.ui.event.DomainObjectActionListener;
import com.arsdigita.london.util.ui.event.DomainObjectActionEvent;
import com.arsdigita.navigation.QuickLink;
import com.arsdigita.navigation.Navigation;

public class QuickLinkListing extends AbstractDomainObjectList {

    private ACSObjectSelectionModel m_category;
    private ACSObjectSelectionModel m_link;

    public static final String ACTION_EDIT = "edit";
    public static final String ACTION_DELETE = "delete";

    public QuickLinkListing(ACSObjectSelectionModel category,
                            ACSObjectSelectionModel link) {
        super("quickLinkListing",
              Navigation.NAV_PREFIX,
              Navigation.NAV_NS);

        m_category = category;
        m_link = link;
        
        registerDomainObjectAction(ACTION_EDIT);
        registerDomainObjectAction(ACTION_DELETE);
        
        addDomainObjectActionListener(
            ACTION_DELETE,
            new DomainObjectActionListener() {
                public void actionPerformed(DomainObjectActionEvent e) {
                    m_link.clearSelection(e.getPageState());

                    DomainObject dobj = e.getObject();
                    dobj.delete();
                }
            });
        addDomainObjectActionListener(
            ACTION_EDIT,
            new DomainObjectActionListener() {
                public void actionPerformed(DomainObjectActionEvent e) {
                    m_link.setSelectedObject(e.getPageState(),
                                             e.getObject());
                }
            });
    }

    

    protected DomainCollection getDomainObjects(PageState state) {
        Category cat = (Category)m_category.getSelectedObject(state);

        DataCollection objs = SessionManager.getSession().retrieve
            (QuickLink.BASE_DATA_OBJECT_TYPE);
        objs.addEqualsFilter(Category.CATEGORIES + "." + ACSObject.ID,
                             cat.getID());
        objs.addOrder(Category.CATEGORIES + ".link." + Category.SORT_KEY);

        return new DomainCollection(objs);
    }

}
