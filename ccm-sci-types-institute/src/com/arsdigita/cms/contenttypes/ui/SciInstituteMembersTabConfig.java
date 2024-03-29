package com.arsdigita.cms.contenttypes.ui;


import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.parameter.BooleanParameter;
import com.arsdigita.util.parameter.IntegerParameter;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.StringArrayParameter;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class SciInstituteMembersTabConfig extends AbstractConfig {
    
     private Parameter statusValues;
    private Parameter pageSize;
    private Parameter enableSearchLimit;
    private Parameter mergeMembers;
    
    public SciInstituteMembersTabConfig() {
         statusValues =
        new StringArrayParameter(
                "com.arsdigita.cms.contenttypes.sciinstitute.tabs.members.status_values",
                Parameter.REQUIRED,
                new String[]{"active", "associated", "former"});

        pageSize =
        new IntegerParameter(
                "com.arsdigita.cms.contenttypes.sciinstitute.tabs.embers.page_size",
                             Parameter.REQUIRED,
                             30);

        enableSearchLimit =
        new IntegerParameter(
                "com.arsdigita.cms.contenttypes.sciinstitute.tabs.members.enable_search_limit",
                Parameter.REQUIRED,
                2);

        mergeMembers =
        new BooleanParameter(
                "com.arsdigita.cms.contenttypes.sciinstitutes.tabs.members.merge",
                Parameter.REQUIRED,
                Boolean.TRUE);

        register(statusValues);
        register(pageSize);
        register(enableSearchLimit);
        register(mergeMembers);

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
}
