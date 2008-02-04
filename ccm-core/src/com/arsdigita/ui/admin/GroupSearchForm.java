/*
 * Copyright (C) 2006 Chris Gilbert. All Rights Reserved.
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

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
import com.arsdigita.kernel.Group;
import com.arsdigita.kernel.GroupCollection;
import com.arsdigita.kernel.Party;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.StringUtils;

/**
 * @author cgyg9330
 *
 * Search for groups to add as subgroups. Text entered by user is put into a groupName like '%......%' query,
 * which also excludes problematic results (groups that are already supergroups or subgroups of the current group
 * 
 */
public class GroupSearchForm extends Form implements FormProcessListener, AdminConstants {

	private ExistingGroupAddPane parentPane;
    private TextField m_search;
    private DataCollection results = null;

	private static final Logger s_log = Logger.getLogger(GroupSearchForm.class);
    public GroupSearchForm(ExistingGroupAddPane parent) {
        super("SearchGroups", new SimpleContainer());

        parentPane = parent;
        setMethod(Form.POST);

        addProcessListener(this);

        add(GROUP_SEARCH_LABEL);
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
        
		Group parent = parentPane.getParentGroup(state);
		String search = (String)m_search.getValue(state);
        if (results != null) {
			results.close();
		}
		results = SessionManager.getSession().retrieve(Group.BASE_DATA_OBJECT_TYPE);
		results.addOrder(Group.DISPLAY_NAME);
		Set excludedList = new HashSet();
		GroupCollection subgroups = parent.getAllSubgroups();
				while (subgroups.next()) {
			excludedList.add(subgroups.getGroup().getID());
		}
		GroupCollection supergroups = parent.getAllSupergroups();
				List supergroupsList = new ArrayList();
				while (supergroups.next()) {
					excludedList.add(supergroups.getGroup().getID());
				}
		
		if (!excludedList.isEmpty()) {
		
			Filter excludedGroupFilter = results.addFilter("id not in :excluded");
			excludedGroupFilter.set("excluded", excludedList);
			}
		Filter userSearchFilter = results.addFilter
		("lower(name) like lower('%' || :searchQuery || '%')");
		userSearchFilter.set("searchQuery", Utilities.prepare(search));
		
		  
        
		 
        if (results.isEmpty()) {
            parentPane.showNoResults(state);
        } else {
            // put search string into Page
            state.setValue(getSearchString(), m_search.getValue(state));
            parentPane.showGroups(state);
        }
        
    }

	/**
	 * 
	 * allow other classes to get hold of the results, to avoid constructing the same query in several places
	 * 
	 */
	public DataCollection getResults () {
		return results;
	}
	   private ParameterModel getSearchString() {
		   return parentPane.getSearchString();
	   }

	  
}
