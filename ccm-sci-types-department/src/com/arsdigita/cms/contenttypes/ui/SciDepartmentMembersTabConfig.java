/*
 * Copyright (c) 2013 Jens Pelzetter
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
package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.parameter.BooleanParameter;
import com.arsdigita.util.parameter.IntegerParameter;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.StringArrayParameter;

/**
 * Configuration for the members tab.
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class SciDepartmentMembersTabConfig extends AbstractConfig {

    private Parameter statusValues;
    private Parameter pageSize;
    private Parameter enableSearchLimit;
    private Parameter mergeMembers;
    private Parameter membersTabStatusFilter;
    private Parameter membersTabSurnameFilter;

    public SciDepartmentMembersTabConfig() {
        statusValues = new StringArrayParameter(
            "com.arsdigita.cms.contenttypes.scidepartment.tabs.members.status_values",
            Parameter.REQUIRED,
            new String[]{"active", "associated", "former"});

        pageSize = new IntegerParameter(
            "com.arsdigita.cms.contenttypes.scidepartment.tabs.members.page_size",
            Parameter.REQUIRED,
            30);

        enableSearchLimit = new IntegerParameter(
            "com.arsdigita.cms.contenttypes.scidepartment.tabs.members.enable_search_limit",
            Parameter.REQUIRED,
            0);

        mergeMembers = new BooleanParameter(
            "com.arsdigita.cms.contenttypes.scidepartments.tabs.members.merge",
            Parameter.REQUIRED,
            Boolean.FALSE);

        membersTabStatusFilter = new BooleanParameter(
            "com.arsdigita.cms.contenttypes.scidepartment.tabs.members.status_filter",
            Parameter.REQUIRED,
            Boolean.TRUE);
        membersTabSurnameFilter = new BooleanParameter(
            "com.arsdigita.cms.contenttypes.scidepartment.tabs.members.surname_filter",
            Parameter.REQUIRED,
            Boolean.TRUE);

        register(statusValues);
        register(pageSize);
        register(enableSearchLimit);
        register(mergeMembers);
        register(membersTabStatusFilter);
        register(membersTabSurnameFilter);

        loadInfo();
    }

    public final String[] getStatusValues() {
        return (String[]) get(statusValues);
    }

    public final int getPageSize() {
        return (Integer) get(pageSize);
    }

    public final int getEnableSearchLimit() {
        return (Integer) get(enableSearchLimit);
    }

    public final boolean isMergingMembers() {
        return (Boolean) get(mergeMembers);
    }

    public Boolean getMembersTabStatusFilter() {
        return (Boolean) get(membersTabStatusFilter);
    }

    public Boolean getMembersTabSurnameFilter() {
        return (Boolean) get(membersTabSurnameFilter);
    }

}
