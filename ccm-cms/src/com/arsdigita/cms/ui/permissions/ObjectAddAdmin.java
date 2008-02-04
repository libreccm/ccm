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


import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.cms.ui.UserAddForm;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.dispatcher.AccessDeniedException;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.kernel.User;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.Assert;

import java.math.BigDecimal;


/**
 * <p>This component is a form for adding object administrators
 *
 * @author Michael Pih (pihman@arsdigita.com)
 * @author Uday Mathur (umathur@arsdigita.com)
 * @version $Revision: #9 $ $DateTime: 2004/08/17 23:15:09 $
 */
public class ObjectAddAdmin extends UserAddForm {

    public static final String versionId = "$Id: ObjectAddAdmin.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/17 23:15:09 $";

    private ACSObjectSelectionModel m_object;
    private static final String NAME_FILTER = "upper(firstName || lastName || email) like '%' || upper(:search) || '%'";

    public ObjectAddAdmin(ACSObjectSelectionModel object, TextField search) {
        super(search, "ObjectAddAdmin");
        m_object = object;
    }

    protected DataQuery makeQuery(PageState s) {
        Assert.truth(m_object.isSelected(s));

        DataQuery query = SessionManager.getSession().retrieveQuery
            ("com.arsdigita.cms.objectAdminUserSearch");

        ACSObject object = (ACSObject)m_object.getSelectedObject(s);
        String searchQuery = (String) getSearchWidget().getValue(s);

        makeFilter(query, object.getID(), searchQuery);
        query.addOrder("upper(lastName), upper(firstName), upper(email)");
        return query;
    }

    /**
     * Filters out members of the current group and users whose name or email
     * address matches the search string.
     */
    private void makeFilter(DataQuery dq, BigDecimal folderId, String search) {

        dq.setParameter("excludedObjectId", folderId);

        // Add the search filter if the search query is not null.
        if ( search != null ) {
            dq.clearFilter();
            Filter filter = dq.addFilter(NAME_FILTER);
            filter.set("search", search);
        }
    }

    public void process(FormSectionEvent event) throws FormProcessException {
        FormData data = event.getFormData();
        PageState state = event.getPageState();
        Assert.truth(m_object.isSelected(state));

        SecurityManager sm = Utilities.getSecurityManager(state);
        boolean isAdmin =
            sm.canAccess(state.getRequest(), SecurityManager.STAFF_ADMIN);

        if (!isAdmin) {
            throw new AccessDeniedException( (String) GlobalizationUtil.globalize("cms.ui.permissions.not_an_object_adminstrator").localize());
        }

        String[] users = (String[]) data.get("users");
        if ( users != null ) {

            ACSObject object = (ACSObject)m_object.getSelectedObject(state);

            // Add each checked user to the object
            try {
                User user;
                for ( int i = 0; i < users.length; i++ ) {

                    user = User.retrieve(new BigDecimal(users[i]));

                    PermissionDescriptor perm =
                        new PermissionDescriptor(PrivilegeDescriptor.ADMIN,
                                                 object,
                                                 user);

                    // double click protection
                    if ( !PermissionService.checkPermission(perm) ) {
                        PermissionService.grantPermission(perm);
                    }
                }
            } catch (DataObjectNotFoundException e) {
                throw new FormProcessException( (String) GlobalizationUtil.globalize("cms.ui.permissions.cannot_add_user").localize(),  e);
            }

        } else {
            throw new FormProcessException( (String) GlobalizationUtil.globalize("cms.ui.permissions.no_users_were_selected").localize());
        }

        fireCompletionEvent(state);
    }
}
