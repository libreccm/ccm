package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.parameter.BooleanParameter;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.StringParameter;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class SciDepartmentSummaryTabConfig extends AbstractConfig {

    private final Parameter showHeads;
    private final Parameter headRole;
    private final Parameter activeStatus;
    private final Parameter showSubDepartments;
    private final Parameter showContacts;

    public SciDepartmentSummaryTabConfig() {

        showHeads =
        new BooleanParameter(
                "com.arsdigita.cms.contenttypes.scidepartment.summarytab.heads.show",
                Parameter.REQUIRED,
                true);
        
        headRole =
        new StringParameter(
                "com.arsdigita.cms.contenttypes.scidepartment.summarytab.heads.role",
                Parameter.REQUIRED,
                "head");
        
        activeStatus = new StringParameter(
                "com.arsdigita.cms.contenttypes.scidepartment.summarytab.heads.status.active",
                Parameter.REQUIRED,
                "active");
        
        showSubDepartments = new BooleanParameter(
                "com.arsdigita.cms.contenttypes.scidepartment.summarytab.subdepartments.show",
                Parameter.REQUIRED,
                true);
        
        showContacts = new BooleanParameter(
                "com.arsdigita.cms.contenttypes.scidepartment.summarytab.contacts.show",
                Parameter.REQUIRED,
                true);
        
        register(showHeads);
        register(headRole);
        register(activeStatus);
        register(showSubDepartments);
        register(showContacts);

        loadInfo();
    }
    
    public final boolean isShowingHead() {
        return (Boolean) get(showHeads);
    }
    
    public final String getHeadRole() {
        return (String) get(headRole);
    }
    
    public final String getActiveStatus() {
        return (String) get(activeStatus);
    }
    
    public final boolean isShowingSubDepartment() {
        return (Boolean) get(showSubDepartments);
    }
    
    public final boolean isShowingContacts() {
        return (Boolean) get(showContacts);
    }
}
