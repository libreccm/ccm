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

/**
 * Configuration for the projects tab.
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class SciDepartmentProjectsTabConfig extends AbstractConfig {

    private final Parameter greetingSize;
    private final Parameter pageSize;
    private final Parameter enableSearchLimit;
    private final Parameter mergeProjects;
    private final Parameter showAllProjects;

    public SciDepartmentProjectsTabConfig() {
        greetingSize = new IntegerParameter(
            "com.arsdigita.cms.contenttypes.scidepartment.tabs.projects.greeting_number",
            Parameter.REQUIRED,
            10);

        pageSize = new IntegerParameter(
            "com.arsdigita.cms.contenttypes.scidepartment.tabs.projects.page_size",
            Parameter.REQUIRED,
            30);

        enableSearchLimit = new IntegerParameter(
            "com.arsdigita.cms.contenttypes.scidepartment.tabs.projects.enable_search_limit",
            Parameter.REQUIRED,
            2);

        mergeProjects = new BooleanParameter(
            "com.arsdigita.cms.contenttypes.scidepartments.tabs.projects.merge",
            Parameter.REQUIRED,
            Boolean.TRUE);

        showAllProjects = new BooleanParameter(
            "com.arsdigita.cms.contenttypes.scidepartments.tabs.projects.show_all",
            Parameter.REQUIRED,
            Boolean.FALSE);

        register(greetingSize);
        register(pageSize);
        register(enableSearchLimit);
        register(mergeProjects);
        register(showAllProjects);

        loadInfo();
    }

    public final int getGreetingSize() {
        return (Integer) get(greetingSize);
    }

    public final int getPageSize() {
        return (Integer) get(pageSize);
    }

    public final int getEnableSearchLimit() {
        return (Integer) get(enableSearchLimit);
    }

    public final boolean isMergingProjects() {
        return (Boolean) get(mergeProjects);
    }
    
    public final boolean isShowingAllProjects() {
        return (Boolean) get(showAllProjects);
    }

}
