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
public class SciInstituteSummaryTabConfig extends AbstractConfig {

    private final Parameter showHeads;
    private final Parameter headRole;
    private final Parameter activeStatus;
    private final Parameter showDepartments;
    private final Parameter showContacts;

    public SciInstituteSummaryTabConfig() {
        showHeads =
        new BooleanParameter(
                "com.arsdigita.cms.contenttypes.sciinstitute.summarytab.heads.show",
                Parameter.REQUIRED,
                true);

        headRole =
        new StringParameter(
                "com.arsdigita.cms.contenttypes.sciinstitute.summarytab.heads.role",
                Parameter.REQUIRED,
                "head");

        activeStatus =
        new StringParameter(
                "com.arsdigita.cms.contenttypes.sciinstitute.summarytab.heads.status.active",
                Parameter.REQUIRED,
                "active");

        showDepartments =
        new BooleanParameter(
                "com.arsdigita.cms.contenttypes.sciinstitute.summarytab.departments.show",
                Parameter.REQUIRED,
                true);

        showContacts =
        new BooleanParameter(
                "com.arsdigita.cms.contenttypes.sciinstitute.summarytab.contacts.show",
                Parameter.REQUIRED,
                true);

        register(showHeads);
        register(headRole);
        register(activeStatus);
        register(showDepartments);
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

    public final Boolean isShowingDepartments() {
        return (Boolean) get(showDepartments);
    }

    public final boolean isShowingContacts() {
        return (Boolean) get(showContacts);
    }
}
