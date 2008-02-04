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
package com.arsdigita.ui.permissions;

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SegmentedPanel;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.CheckboxGroup;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.OptionGroup;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.parameters.ArrayParameter;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.StringUtils;
import com.arsdigita.util.UncheckedWrapperException;

import java.math.BigDecimal;
import java.util.TooManyListenersException;


/**
 * Permissions Grant container for permissions assignment.
 * Widgets are currently organized on a bebop SegmentedPanel.
 *
 * @author Stefan Deusch
 * @version $Id: PermissionsGrant.java 1508 2007-03-22 00:04:22Z apevec $
 */

class PermissionsGrant implements PermissionsConstants {

    private final static String PARTIES_CBG = "parties_cbg";
    private final static String PRIVILEGES_CBG  = "privs_cbg";

    // data keys
    private static final String USER_ID        = "userID";
    private static final String FIRST_NAME     = "firstName";
    private static final String LAST_NAME      = "lastName";

    private SegmentedPanel  m_grantPanel;
    private PermissionsPane m_parent;
    private CheckboxGroup   m_parties;
    private CheckboxGroup   m_privileges;
    private Form            m_form;
    private Submit          m_save;



    /**
     * Creates a PermissionsGrant object that will be contained with
     * another component. This is currently used inside the
     * permissions pane.
     *
     * @param parent the enclosing container
     */

    public PermissionsGrant(PermissionsPane parent) {
        m_parent = parent;
        makeForm();
        m_grantPanel = new SegmentedPanel();
        m_grantPanel.addSegment(new Label(PAGE_GRANT_TITLE), m_form);
    }


    /**
     * Builds the form used to grant pivileges to users and groups.
     */

    private void makeForm() {
        m_form = new Form("GrantPrivileges", new BoxPanel());
        m_form.setMethod(Form.POST);
        m_form.addSubmissionListener(new GrantFormSubmissionListener());
        m_form.add(new Label(PAGE_GRANT_LEFT));
        m_parties = new CheckboxGroup(PARTIES_CBG);
        try {
            m_parties.addPrintListener(new UserSearchPrintListener());
        } catch (TooManyListenersException e) {
            throw new UncheckedWrapperException(e.getMessage(), e);
        }
        m_form.add(m_parties);

        m_form.add(new Label(PAGE_GRANT_RIGHT));
        m_privileges = new CheckboxGroup(PRIVILEGES_CBG);
        try {
            m_privileges.addPrintListener(new PrivilegePrintListener());
        } catch (TooManyListenersException e) {
            throw new UncheckedWrapperException(e.getMessage(), e);
        }
        m_form.add(m_privileges);

        m_save = new Submit("save", SAVE_BUTTON);
        m_form.add(m_save);
    }

    /**
     * Returns the SegmentedPanel with the permissions grant Form
     *
     * @return the SegmentedPanel with the permissions grant form
     */

    public SegmentedPanel getPanel() {
        return m_grantPanel;
    }

    private class GrantFormSubmissionListener
        implements FormSubmissionListener {
        public void submitted(FormSectionEvent e) throws FormProcessException {
            PageState state = e.getPageState();
            FormData data = e.getFormData();
            String[] gids = (String[])data.get(PARTIES_CBG);
            String[] privs = (String[])data.get(PRIVILEGES_CBG);
            if (privs != null && gids != null) {
                BigDecimal oID = m_parent.getObject(state).getID();
                BigDecimal gID = null;
                UserObjectStruct uos = null;
                PermissionDescriptor pmd = null;
                for(int j=0; j<gids.length; j++) {
                    gID = new BigDecimal(gids[j]);
                    uos = new UserObjectStruct(gID, oID);
                    for(int k=0; k<privs.length; k++) {
                        pmd = new
                            PermissionDescriptor(PrivilegeDescriptor.
                                                 get(privs[k]),
                                                 uos.getObject(),
                                                 uos.getParty());
                        PermissionService.grantPermission(pmd);
                    }
                }
            }
            m_parent.showAdmin(state);
        }
    }

    private class UserSearchPrintListener implements PrintListener {
        public void prepare(PrintEvent e) {
            PageState state = e.getPageState();
            OptionGroup cbg = (CheckboxGroup)e.getTarget();

            // get query string
            String search = (String)state.
                getValue(new StringParameter(SEARCH_QUERY));
            search = StringUtils.stripWhiteSpace(search);

            DataQuery query = SessionManager.getSession().
                retrieveQuery(RETRIEVE_USERS);
            query.setParameter("excludeGroupId", new Integer(0));

            Filter f = query.addFilter
                ("searchName like lower('%' || :searchQuery || '%')");
            f.set("searchQuery", search);

            String userID, userName;
            while(query.next()) {
                userID = query.get(USER_ID).toString();
                userName = query.get(FIRST_NAME)+" "+query.get(LAST_NAME);
                cbg.addOption(new Option(userID, userName));
            }
            query.close();
        }
    }

    private static class PrivilegePrintListener implements PrintListener {
        public void prepare(PrintEvent e) {
            PageState state = e.getPageState();
            OptionGroup cbg = (CheckboxGroup)e.getTarget();

            // get privileges from page state
            Object[] pa = (Object[])state.
                getValue(new ArrayParameter(PRIV_SET));

            // print ceckbox group with privileges
            for (int i=0; i<pa.length; i++) {
                cbg.addOption(new Option((String)pa[i], (String)pa[i]));
            }
        }
    }
}
