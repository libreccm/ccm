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
 * Configuration for the Publications tab of a SciDepartment.
 * 
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class SciDepartmentPublicationsTabConfig extends AbstractConfig {

    private final Parameter greetingSize;
    private final Parameter pageSize;
    private final Parameter enableSearchLimit;
    private final Parameter mergePublications;   

    public SciDepartmentPublicationsTabConfig() {
        greetingSize =
        new IntegerParameter(
                "com.arsdigita.cms.contenttypes.scidepartment.tabs.publications.greeting_number",
                Parameter.REQUIRED,
                10);

        pageSize =
        new IntegerParameter(
                "com.arsdigita.cms.contenttypes.scidepartment.tabs.publications.page_size",
                Parameter.REQUIRED,
                30);

        enableSearchLimit =
        new IntegerParameter(
                "com.arsdigita.cms.contenttypes.scidepartment.tabs.publications.enable_search_limit",
                Parameter.REQUIRED,
                2);

        mergePublications =
        new BooleanParameter(
                "com.arsdigita.cms.contenttypes.scidepartment.tabs.publications.merge",
                Parameter.REQUIRED,
                Boolean.TRUE);
     
        register(greetingSize);
        register(pageSize);
        register(enableSearchLimit);
        register(mergePublications);     

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

    public final boolean isMergingPublications() {
        return (Boolean) get(mergePublications);
    } 
}
