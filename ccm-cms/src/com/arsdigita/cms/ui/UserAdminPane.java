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
package com.arsdigita.cms.ui;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.cms.UserHomeFolderMap;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.cms.util.SecurityConstants;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.kernel.User;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.ui.admin.Admin;
import com.arsdigita.web.Application;
import com.arsdigita.web.Web;
import com.arsdigita.xml.Element;

import java.math.BigDecimal;

import org.apache.log4j.Logger;

/**
 * A pane that contains user administration details.
 *
 * @author Tzu-Mainn Chen
 * @version $Id: UserAdminPane.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class UserAdminPane extends SimpleContainer {

    private static final Logger s_log = Logger.getLogger(UserAdminPane.class);

    private final UserSearchForm m_userFolderResetForm;
    private final UserFolderResetProcessForm m_userFolderResetProcessForm;
    private final Label m_submitLabel;
    private final Label m_deniedLabel;
    private final Link m_link;

    public UserAdminPane() {
        m_userFolderResetForm = new UserSearchForm
            ("userFolderResetForm",
             new Label("Reset a user's home folder to none"));
        add(m_userFolderResetForm);

        m_userFolderResetProcessForm = new UserFolderResetProcessForm();
        add(m_userFolderResetProcessForm);

        m_submitLabel = new Label("No default folder set for users");
        add(m_submitLabel);

        m_deniedLabel = new Label("You do not have permission to view this pane.");
        add(m_deniedLabel);
        Application adminApp = Admin.getInstance();
        // Note that if the admin app is remounted after this form has been
        // created, the link will be incorrect.  Since this never happens in
        // practice, we are not going to worry about it. -- 2004-01-27
        m_link = new Link("Administer users site-wide",
                          adminApp==null ?
                          "not_found" : adminApp.getPrimaryURL());
        add(m_link);
    }

    private static boolean hasSiteWideAdmin(User user) {
        Application adminApp = Admin.getInstance();
        if ( adminApp == null ) { return false; }

        PermissionDescriptor admin = new PermissionDescriptor
                (PrivilegeDescriptor.ADMIN, adminApp, user);
        return PermissionService.checkPermission(admin);
    }

    public final void generateXML(final PageState state,
                                  final Element parent) {
        if ( !isVisible(state) ) { return; }

        SecurityManager sm = CMS.getContext().getSecurityManager();
        User user = Web.getWebContext().getUser();

        if ( !sm.canAccess(user,SecurityConstants.STAFF_ADMIN) ) {
            m_deniedLabel.generateXML(state, parent);
            return;
        }

        final FormData one = m_userFolderResetForm.getFormData(state);
        final FormData two =
            m_userFolderResetProcessForm.getForm().getFormData(state);

        if (one != null && one.isSubmission()) {
            m_userFolderResetForm.generateXML(state, parent);
            m_userFolderResetProcessForm.generateXML(state, parent);
        } else if (two != null && two.isSubmission()) {
            m_userFolderResetForm.generateXML(state, parent);
            m_submitLabel.generateXML(state, parent);
        } else {
            m_userFolderResetForm.generateXML(state, parent);

        }
        if ( hasSiteWideAdmin(user) ) {
            m_link.generateXML(state, parent);
        }
    }

    private class UserFolderResetProcessForm extends UserAddForm {

        public UserFolderResetProcessForm() {
            super(m_userFolderResetForm.getSearchWidget(),"UserFolderResetProcess","Select the users you wish to reset.", "Set no default folder for users");
        }

        protected final DataQuery makeQuery(final PageState state) {
            final Session session = SessionManager.getSession();

            final DataQuery query = session.retrieveQuery
                ("com.arsdigita.cms.workflow.searchToAddMembers");
            final String search = (String) getSearchWidget().getValue(state);

            makeFilter(query, search);
            query.addOrder("lastName, firstName, email");

            final OID oid = CMS.getContext().getContentSection().getOID();

            PermissionService.objectFilterQuery
                (query, "userId", PrivilegeDescriptor.READ, oid);

            return query;
        }

        private void makeFilter(final DataQuery query, final String search) {
            query.clearFilter();

            final Filter filter = query.addFilter
                ("upper(firstName || lastName || email) " +
                 "like '%' || upper(:search) || '%'");

            filter.set("search", search);
        }

        public final void process(final FormSectionEvent e)
                throws FormProcessException {
            final FormData data = e.getFormData();
            final PageState state = e.getPageState();

            final String[] users = (String[]) data.get("users");

            if (users == null) {
                throw new FormProcessException
                    (lz("cms.ui.workflow.no_users_were_selected"));
            } else {
                User user;
                ContentSection section = CMS.getContext().getContentSection();
                for (int i = 0; i < users.length; i++) {
                    user = User.retrieve(new BigDecimal(users[i]));
                    UserHomeFolderMap map = UserHomeFolderMap.findUserHomeFolderMap(user,section);
                    if ( map != null ) {
                        map.delete();
                    }
                }
            }
        }
    }

    private static GlobalizedMessage gz(final String key) {
        return GlobalizationUtil.globalize(key);
    }

    private static String lz(final String key) {
        return (String) gz(key).localize();
    }

}
