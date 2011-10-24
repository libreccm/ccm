package com.arsdigita.cms.contenttypes;

import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.parameter.BooleanParameter;
import com.arsdigita.util.parameter.IntegerParameter;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.StringParameter;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class SciInstituteConfig extends AbstractConfig {

    private final Parameter enableDepartmentsStep;
    private final Parameter enableDepartmentInstitutesStep;
    private final Parameter enableProjectsStep;
    private final Parameter enableProjectInstitutesStep;
    private final Parameter shortDescMaxLength;
    private final Parameter enableDescriptionDhtml;
    private final Parameter permittedPersonType;
    private final Parameter tabs;

    public SciInstituteConfig() {
        enableDepartmentsStep =
        new BooleanParameter(
                "com.arsdigita.cms.contenttypes.sciinstitute.enable.departments_step",
                Parameter.REQUIRED, Boolean.TRUE);

        enableDepartmentInstitutesStep =
        new BooleanParameter(
                "com.arsdigita.cms.contenttypes.sciinstitute.enable.department_institutes_step",
                Parameter.REQUIRED,
                Boolean.TRUE);

        enableProjectsStep =
        new BooleanParameter(
                "com.arsdigita.cms.contenttypes.sciinstitute.enable.projects_step",
                Parameter.REQUIRED,
                Boolean.TRUE);

        enableProjectInstitutesStep =
        new BooleanParameter(
                "com.arsdigita.cms.contenttypes.sciinstitute.enable.project_institutes_step",
                Parameter.REQUIRED,
                Boolean.TRUE);

        shortDescMaxLength =
        new IntegerParameter(
                "com.arsdigita.cms.contenttypes.sciinstitute.short_desc.max_length",
                             Parameter.REQUIRED, 500);

        enableDescriptionDhtml =
        new BooleanParameter(
                "com.arsdigita.cms.contenttypes.sciinstitute.description.dhtml_enable",
                             Parameter.REQUIRED, Boolean.TRUE);

        permittedPersonType =
        new StringParameter(
                "com.arsdigita.cms.contenttypes.sciinstitute.permitted_person_type",
                Parameter.REQUIRED,
                "com.arsdigita.cms.contenttypes.GenericPerson");

        tabs =
        new StringParameter(
                "com.arsdigita.cms.contenttypes.sciinstitute.tabs",
                Parameter.REQUIRED,
                "summary:com.arsdigita.cms.contenttypes.ui.SciInstituteSummaryTab");

        register(enableDepartmentsStep);
        register(enableDepartmentInstitutesStep);
        register(enableProjectsStep);
        register(enableProjectInstitutesStep);
        register(shortDescMaxLength);
        register(enableDescriptionDhtml);
        register(permittedPersonType);
        register(tabs);

        loadInfo();
    }

    public final Boolean getEnableDepartmentsStep() {
        return (Boolean) get(enableDepartmentsStep);
    }
    
    public final Boolean getEnableDepartmentInstitutesStep() {
        return (Boolean) get(enableDepartmentInstitutesStep);
    }
    
    public final Boolean getEnableProjectsStep() {
        return (Boolean) get(enableProjectsStep);
    }
    
     public final Boolean getEnableProjectInstitutesStep() {
        return (Boolean) get(enableProjectInstitutesStep);
    }

    public Integer getShortDescMaxLength() {
        return (Integer) get(shortDescMaxLength);
    }

    public Boolean getEnableDescriptionDhtml() {
        return (Boolean) get(enableDescriptionDhtml);
    }

    public final String getPermittedPersonType() {
        return (String) get(permittedPersonType);
    }

    public final String getTabs() {
        return (String) get(tabs);
    }
}
