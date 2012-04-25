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
