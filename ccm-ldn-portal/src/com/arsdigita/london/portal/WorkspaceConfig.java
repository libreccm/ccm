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

package com.arsdigita.london.portal;

import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.util.parameter.BooleanParameter;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.StringArrayParameter;
import com.arsdigita.util.parameter.StringParameter;
import com.arsdigita.util.parameter.URLParameter;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

public class WorkspaceConfig extends AbstractConfig {

	private static final Logger s_log = Logger.getLogger(WorkspaceConfig.class);

	private URLParameter m_adapters;

	private StringParameter m_defaultLayout;

	private BooleanParameter m_createUserWorkspaces;

	private Parameter m_excludedPortletTypes;

	private Parameter m_adminPortletTypes;

        private BooleanParameter m_htmlPortletWysiwygEditor;

	private StringParameter m_workspacePartyPrivilege;

	private BooleanParameter m_checkWorkspaceReadPermissions;

	public WorkspaceConfig() {
		try {
			m_adapters = new URLParameter(
					"com.arsdigita.london.portal.traversal_adapters",
					Parameter.REQUIRED,
					new URL(null,
							"resource:WEB-INF/resources/portal-adapters.xml"));
		} catch (MalformedURLException ex) {
			throw new UncheckedWrapperException("Cannot parse URL", ex);
		}

		m_defaultLayout = new StringParameter(
				"com.arsdigita.london.portal.default_layout",
				Parameter.REQUIRED, PageLayout.FORMAT_THREE_COLUMNS);

		m_createUserWorkspaces = new BooleanParameter(
				"com.arsdigita.portal.create_user_workspaces",
				Parameter.REQUIRED, Boolean.TRUE);

		m_excludedPortletTypes = new StringArrayParameter(
				"com.arsdigita.london.portal.excluded_portlet_types",
				Parameter.OPTIONAL, new String[0]);

		m_adminPortletTypes = new StringArrayParameter(
				"com.arsdigita.london.portal.admin_only_portlet_types",
				Parameter.OPTIONAL, new String[0]);

                m_htmlPortletWysiwygEditor = new BooleanParameter(
                                "com.arsdigita.london.portal.portlet.freeform_html.wysiwyg_editor",
                                Parameter.REQUIRED,
                                Boolean.FALSE);

		m_workspacePartyPrivilege = new StringParameter(
				"com.arsdigita.london.portal.workspacePartyPrivilege",
				Parameter.OPTIONAL, "read");

		m_checkWorkspaceReadPermissions = new BooleanParameter(
				"com.arsdigita.london.portal.checkWorkspaceReadPermissions",
				Parameter.OPTIONAL, Boolean.FALSE);

		register(m_adapters);
		register(m_defaultLayout);
		register(m_createUserWorkspaces);
		register(m_excludedPortletTypes);
		register(m_adminPortletTypes);
                register(m_htmlPortletWysiwygEditor);
		register(m_workspacePartyPrivilege);
		register(m_checkWorkspaceReadPermissions);

		loadInfo();
	}

	InputStream getTraversalAdapters() {
		try {
			return ((URL) get(m_adapters)).openStream();
		} catch (IOException ex) {
			throw new UncheckedWrapperException("Cannot read stream", ex);
		}
	}

	public String getDefaultLayout() {
		return (String) get(m_defaultLayout);
	}

	public boolean getCreateUserWorkspaces() {
		return ((Boolean) get(m_createUserWorkspaces)).booleanValue();
	}

	public List getExcludedPortletTypes() {
		String[] excludedTypes = (String[]) get(m_excludedPortletTypes);
		return Arrays.asList(excludedTypes);
	}

	public List getAdminPortletTypes() {
		String[] adminTypes = (String[]) get(m_adminPortletTypes);
		return Arrays.asList(adminTypes);
	}

        public boolean useWysiwygEditor() {
                return ((Boolean) get(m_htmlPortletWysiwygEditor)).booleanValue();
        }

    private PrivilegeDescriptor workspacePartyPrivilegeDescriptor = null;

    private void initWorkspacePartyPrivilegeDescriptor() {
        String val = (String) get(m_workspacePartyPrivilege);
        if (val.equals("edit")) {
            // recommended
            workspacePartyPrivilegeDescriptor = PrivilegeDescriptor.EDIT;
        } else if (val.equals("admin")) {
            // risky
            workspacePartyPrivilegeDescriptor = PrivilegeDescriptor.ADMIN;
        } else {
            // default
            workspacePartyPrivilegeDescriptor = PrivilegeDescriptor.READ;
        }
    }

	public PrivilegeDescriptor getWorkspacePartyPrivilege() {
        if (workspacePartyPrivilegeDescriptor == null) {
            initWorkspacePartyPrivilegeDescriptor();
        }
		return workspacePartyPrivilegeDescriptor;
	}

	public boolean getCheckWorkspaceReadPermissions() {
		return ((Boolean) get(m_checkWorkspaceReadPermissions)).booleanValue();
	}
}
