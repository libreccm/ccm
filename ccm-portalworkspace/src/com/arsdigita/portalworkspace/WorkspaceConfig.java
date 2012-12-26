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

package com.arsdigita.portalworkspace;

import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.parameter.BooleanParameter;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.ResourceParameter;
import com.arsdigita.util.parameter.StringArrayParameter;
import com.arsdigita.util.parameter.StringParameter;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

public class WorkspaceConfig extends AbstractConfig {

    /** A logger instance.  */
	private static final Logger s_log = Logger.getLogger(WorkspaceConfig.class);

    /** Singelton config object.  */
    private static WorkspaceConfig s_conf;

    /**
     * Gain a WorkspaceConfig object.
     *
     * Singelton pattern, don't instantiate a config object using the
     * constructor directly!
     * @return
     */
    public static synchronized WorkspaceConfig getConfig() {
        if (s_conf == null) {
            s_conf = new WorkspaceConfig();
            s_conf.load();
        }

        return s_conf;
    }


    // set of configuration parameters
    /** File with rules for configuring information in generated XML         */
    private final Parameter m_adapters =
            new ResourceParameter(
                    "com.arsdigita.portalworkspace.traversal_adapters",
                    Parameter.REQUIRED,
                    "/WEB-INF/resources/portalworkspace-adapters.xml");

    /** Default column layout for workspace portals                          */
    private final Parameter m_defaultLayout =
            new StringParameter(
                    "com.arsdigita.portalworkspace.default_layout",
                    Parameter.REQUIRED, PageLayout.FORMAT_THREE_COLUMNS);

    /** Whether non-admin users should have their own custom workspaces      */
    private final Parameter m_createUserWorkspaces =
            new BooleanParameter(
                    "com.arsdigita.portalworkspace.create_user_workspaces",
                    Parameter.REQUIRED, Boolean.TRUE);

    /** Types not to be included in the drop down list of portlets to add to a page*/
    private final Parameter m_excludedPortletTypes =
            new StringArrayParameter(
                    "com.arsdigita.portalworkspace.excluded_portlet_types",
                    Parameter.OPTIONAL, new String[0]);

    /** Types only available to administrator of homepage, or subsite frontpage*/
    private final Parameter m_adminPortletTypes =
            new StringArrayParameter(
                    "com.arsdigita.portalworkspace.admin_only_portlet_types",
                    Parameter.OPTIONAL, new String[0]);

    /** Whether to use editor specified by waf.bebop.dhtml_editor for editing 
        freeform html portlet*/
    private final Parameter m_htmlPortletWysiwygEditor =
            new BooleanParameter(
                    "com.arsdigita.portalworkspace.portlet.freeform_html_editor",
                    Parameter.REQUIRED, Boolean.FALSE);

	/** Which privilege ("read" or "edit") is granted to the workspace party. */
    private final Parameter m_workspacePartyPrivilege =
            new StringParameter(
                    "com.arsdigita.portalworkspace.workspacePartyPrivilege",
                    Parameter.OPTIONAL, "read");

	/** Whether READ permissions will be checked when viewing workspaces. 
        By default we don't, which is odd.                                   */
    private final Parameter m_checkWorkspaceReadPermissions =
            new BooleanParameter(
                    "com.arsdigita.portalworkspace.checkWorkspaceReadPermissions",
                    Parameter.OPTIONAL, Boolean.FALSE);

	public WorkspaceConfig() {

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
		return (InputStream) get(m_adapters);
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
