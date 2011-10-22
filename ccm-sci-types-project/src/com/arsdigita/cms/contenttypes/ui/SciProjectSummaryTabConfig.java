package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.parameter.BooleanParameter;
import com.arsdigita.util.parameter.Parameter;

/**
 * Configuration for the {@link SciProjectSummaryTab}.
 *  
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class SciProjectSummaryTabConfig extends AbstractConfig {

    private final Parameter showMembers;
    private final Parameter mergeMembers;
    private final Parameter showContacts;
    private final Parameter showInvolvedOrgas;
    private final Parameter showSubProjects;

    public SciProjectSummaryTabConfig() {
        showMembers =
        new BooleanParameter(
                "com.arsdigita.cms.contenttypes.sciproject.summarytab.members.show",
                Parameter.REQUIRED,
                true);

        mergeMembers =
        new BooleanParameter(
                "com.arsdigita.cms.contenttypes.sciproject.summarytab.members.merge",
                Parameter.REQUIRED,
                true);

        showContacts =
        new BooleanParameter(
                "com.arsdigita.cms.contenttypes.sciproject.summarytab.contacts.show",
                Parameter.REQUIRED,
                true);

        showInvolvedOrgas =
        new BooleanParameter(
                "com.arsdigita.cms.contenttypes.sciproject.summarytab.involved_orgas.show",
                Parameter.REQUIRED,
                true);

        showSubProjects =
        new BooleanParameter(
                "com.arsdigita.cms.contenttypes.sciproject.summarytab.subprojects.show",
                Parameter.REQUIRED,
                true);
        
        register(showMembers);
        register(mergeMembers);
        register(showContacts);
        register(showInvolvedOrgas);
        register(showSubProjects);
        
        loadInfo();
    }
    
    public final boolean isShowingMembers() {
        return (Boolean) get(showMembers);
    }
    
    public final boolean isMergingMembers() {
        return (Boolean) get(mergeMembers);
    }
    
    public final boolean isShowingContacts() {
        return (Boolean) get(showContacts);
    }
    
    public final boolean isShowingInvolvedOrgas() {
        return (Boolean) get(showInvolvedOrgas);
    }
    
    public final boolean isShowingSubProjects() {
        return (Boolean) get(showSubProjects);
    }
}
