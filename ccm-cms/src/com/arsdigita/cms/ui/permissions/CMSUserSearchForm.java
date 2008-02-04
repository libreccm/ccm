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


import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.NotEmptyValidationListener;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.PartyCollection;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.util.StringUtils;

/**
 * User Search Form for permissions.
 *
 * @author Stefan Deusch (stefan@arsdigita.com)
 * @version $Id: CMSUserSearchForm.java 287 2005-02-22 00:29:02Z sskracic $
 */

public class CMSUserSearchForm extends Form
    implements FormProcessListener,  CMSPermissionsConstants
{
    private CMSPermissionsPane m_parent;
    private TextField m_search;

    public CMSUserSearchForm(CMSPermissionsPane parent) {
        this(DEFAULT_PRIVILEGES, parent);
    }

    public CMSUserSearchForm(PrivilegeDescriptor[] privs, CMSPermissionsPane parent) {
        super("RoleSearchUsers", new SimpleContainer());

        m_parent = parent;
        setMethod(Form.POST);

        addProcessListener(this);

        add(new Label(SEARCH_LABEL));
        add(new Label("&nbsp;", false));

        StringParameter searchParam = new StringParameter(SEARCH_QUERY);
        m_search = new TextField(searchParam);
        m_search.addValidationListener(new NotEmptyValidationListener());
        m_search.setSize(20);
        add(m_search, ColumnPanel.RIGHT);

        Submit submit = new Submit("submit");
        submit.setButtonLabel(SEARCH_BUTTON);
        add(submit, ColumnPanel.LEFT);
    }


    public void process (FormSectionEvent e)
        throws FormProcessException {
        PageState state = e.getPageState();
        FormData data = e.getFormData();
        String search = (String)data.get(SEARCH_QUERY);
        search = StringUtils.stripWhiteSpace(search);

        PartyCollection parties = Party.retrieveAllParties();
        parties.filter(search);

        // DataQuery query = SessionManager.getSession().
        //     retrieveQuery(RETRIEVE_USERS);
        // query.setParameter("excludeGroupId", null);
        // Filter f = query.addFilter
        //     ("searchName like lower('%' || :searchQuery || '%')");
        // f.set("searchQuery", search);

        if (parties.isEmpty()) {
            m_parent.showNoResults(state);
        } else {
            // put search string into Page
            state.setValue(getSearchString(), data.get(SEARCH_QUERY));

            // put privileges into Page
            state.setValue(getPrivilegeModel(), getPrivileges());

            m_parent.showGrant(state);
        }
        parties.close();
        // clear query string
    }

    /**
     * Hide Delegate pattern, if parent's implementation changes.
     */
    private ParameterModel getSearchString() {
        return m_parent.getSearchString();
    }

    /**
     * Detto
     */
    private ParameterModel getPrivilegeModel() {
        return m_parent.getPrivilegeParam();
    }

    /**
     * Detto
     */
    private Object[] getPrivileges() {
        return (Object[])m_parent.getPrivileges();
    }

    public TextField getSearchWidget() {
        return m_search;
    }
}
