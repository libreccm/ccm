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


import com.arsdigita.ui.util.GlobalizationUtil ; 

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
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.StringUtils;

/**
 * User Search Form for permissions.
 *
 * @author Stefan Deusch 
 * @version $Id: UserSearchForm.java 1508 2007-03-22 00:04:22Z apevec $
 */

public class UserSearchForm extends Form
    implements FormProcessListener,  PermissionsConstants
{
    private PermissionsPane m_parent;
    private TextField m_search;

    public UserSearchForm(PermissionsPane parent) {
        this(DEFAULT_PRIVILEGES, parent);
    }

    // FIXME: the privs parameter is passed in but never used. -- 2002-11-26
    public UserSearchForm(PrivilegeDescriptor[] privs, PermissionsPane parent) {
        super("SearchUsers", new SimpleContainer());

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
        DataQuery query = SessionManager.getSession().
            retrieveQuery(RETRIEVE_USERS);
        query.setParameter("excludeGroupId", new Integer(0));
        Filter f = query.addFilter
            ("searchName like lower('%' || :searchQuery || '%')");
        f.set("searchQuery", search);
        if (query.isEmpty()) {
            m_parent.showNoResults(state);
        } else {
            // put search string into Page
            state.setValue(getSearchString(), data.get(SEARCH_QUERY));

            // put privileges into Page
            state.setValue(getPrivilegeModel(), getPrivileges());

            m_parent.showGrant(state);
        }
        query.close();
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
