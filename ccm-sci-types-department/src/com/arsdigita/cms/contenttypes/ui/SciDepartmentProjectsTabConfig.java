package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.parameter.BooleanParameter;
import com.arsdigita.util.parameter.IntegerParameter;
import com.arsdigita.util.parameter.Parameter;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class SciDepartmentProjectsTabConfig extends AbstractConfig {
    
    private final Parameter greetingSize;
    private final Parameter pageSize;
    private final Parameter enableSearchLimit;
    private final Parameter mergeProjects;
    
    public SciDepartmentProjectsTabConfig() {
        greetingSize =
        new IntegerParameter(
                "com.arsdigita.cms.contenttypes.scidepartment.tabs.projects.greeting_number",                
                Parameter.REQUIRED,
                10);
        
        pageSize =
        new IntegerParameter(
                "com.arsdigita.cms.contenttypes.scidepartment.tabs.projects.page_size",
                Parameter.REQUIRED,
                30);
        
        enableSearchLimit =
        new IntegerParameter(
                "com.arsdigita.cms.contenttypes.scidepartment.tabs.projects.enable_search_limit",
                Parameter.REQUIRED,
                2);
        
        mergeProjects =
        new BooleanParameter(
                "com.arsdigita.cms.contenttypes.scidepartments.tabs.projects.merge",
                Parameter.REQUIRED,
                Boolean.TRUE);
        
        register(greetingSize);
        register(pageSize);
        register(enableSearchLimit);
        register(mergeProjects);
        
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
}
