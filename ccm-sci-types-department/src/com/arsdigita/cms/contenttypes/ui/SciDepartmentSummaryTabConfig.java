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
    private final Parameter showViceHeads;
    private final Parameter viceHeadRole;
    private final Parameter showSecretariat;
    private final Parameter secretariatRole;
    private final Parameter showRoleContacts;
    private final Parameter activeStatus;
    private final Parameter showSubDepartments;
    private final Parameter showContacts;

    public SciDepartmentSummaryTabConfig() {
        super();

        showHeads = new BooleanParameter(
                "com.arsdigita.cms.contenttypes.scidepartment.summarytab.heads.show",
                Parameter.REQUIRED,
                true);

        headRole = new StringParameter(
                "com.arsdigita.cms.contenttypes.scidepartment.summarytab.heads.role",
                Parameter.REQUIRED,
                "head");

        showViceHeads = new BooleanParameter(
                "com.arsdigita.cms.contenttypes.scidepartment.summarytab.viceheads.show",
                Parameter.REQUIRED,
                true);

        viceHeadRole = new StringParameter(
                "com.arsdigita.cms.contenttypes.scidepartment.summarytab.viceheads.role",
                Parameter.REQUIRED,
                "vicehead");

        showSecretariat = new BooleanParameter(
                "com.arsdigita.cms.contenttypes.scidepartment.summarytab.secretariat.show",
                Parameter.REQUIRED,
                true);

        secretariatRole = new StringParameter(
                "com.arsdigita.cms.contenttypes.scidepartment.summarytab.secretariat.role",
                Parameter.REQUIRED,
                "secretariat");

        showRoleContacts = new BooleanParameter(
                "com.arsdigita.cms.contenttypes.scidepartment.summarytab.role_contacts.show",
                Parameter.REQUIRED,
                true);

        activeStatus = new StringParameter(
                "com.arsdigita.cms.contenttypes.scidepartment.summarytab.status.active",
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
        register(showViceHeads);
        register(viceHeadRole);
        register(showSecretariat);
        register(secretariatRole);
        register(showRoleContacts);
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

    public final boolean isShowingViceHead() {
        return (Boolean) get(showViceHeads);
    }

    public final String getViceHeadRole() {
        return (String) get(viceHeadRole);
    }

    public final boolean isShowingSecretriat() {
        return (Boolean) get(showSecretariat);
    }

    public final String getSecretariatRole() {
        return (String) get(secretariatRole);
    }
    
    public final boolean isShowingRoleContacts() {
        return (Boolean) get(showRoleContacts);
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
