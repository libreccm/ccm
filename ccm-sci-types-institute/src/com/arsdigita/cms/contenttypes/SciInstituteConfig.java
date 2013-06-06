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
    private final Parameter departmentsStepSortKey;
    private final Parameter enableDepartmentInstitutesStep;
    private final Parameter departmentInstitutesStepSortKey;
    private final Parameter enableProjectsStep;
    private final Parameter projectsStepSortKey;
    private final Parameter enableProjectInstitutesStep;
    private final Parameter projectInstitutesStepSortKey;
    private final Parameter shortDescMaxLength;
    private final Parameter enableDescriptionDhtml;
    private final Parameter permittedPersonType;
    private final Parameter tabs;

    public SciInstituteConfig() {

        enableDepartmentsStep =
        new BooleanParameter(
                "com.arsdigita.cms.contenttypes.sciinstitute.enable.departments_step",
                Parameter.REQUIRED,
                Boolean.TRUE);

        departmentsStepSortKey =
        new IntegerParameter(
                "com.arsdigita.cms.contenttypes.sciinstitute.departments_step_sortkey",
                Parameter.REQUIRED,
                10);

        enableDepartmentInstitutesStep =
        new BooleanParameter(
                "com.arsdigita.cms.contenttypes.sciinstitute.enable.department_institutes_step",
                Parameter.REQUIRED,
                Boolean.TRUE);

        departmentInstitutesStepSortKey =
        new IntegerParameter(
                "com.arsdigita.cms.contenttypes.sciinstitute.department_institutes_step_sortkey",
                Parameter.REQUIRED,
                20);

        enableProjectsStep =
        new BooleanParameter(
                "com.arsdigita.cms.contenttypes.sciinstitute.enable.projects_step",
                Parameter.REQUIRED,
                Boolean.TRUE);

        projectsStepSortKey =
        new IntegerParameter(
                "com.arsdigita.cms.contenttypes.sciinstitute.projects_step_sortkey",
                Parameter.REQUIRED,
                30);

        enableProjectInstitutesStep =
        new BooleanParameter(
                "com.arsdigita.cms.contenttypes.sciinstitute.enable.project_institutes_step",
                Parameter.REQUIRED,
                Boolean.TRUE);

        projectInstitutesStepSortKey =
        new IntegerParameter(
                "com.arsdigita.cms.contenttypes.sciinstitute.project_institutes_step_sortkey",
                Parameter.REQUIRED,
                40);

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
                "summary:com.arsdigita.cms.contenttypes.ui.SciInstituteSummaryTab;desc:com.arsdigita.cms.contenttypes.ui.SciInstituteDescTab;members:com.arsdigita.cms.contenttypes.ui.SciInstituteMembersTab;projects:com.arsdigita.cms.contenttypes.ui.SciInstituteProjectsTab;publications:com.arsdigita.cms.contenttypes.ui.SciInstitutePublicationsTab");

        register(enableDepartmentsStep);
        register(departmentsStepSortKey);
        register(enableDepartmentInstitutesStep);
        register(departmentInstitutesStepSortKey);
        register(enableProjectsStep);
        register(projectsStepSortKey);
        register(enableProjectInstitutesStep);
        register(projectInstitutesStepSortKey);
        register(shortDescMaxLength);
        register(enableDescriptionDhtml);
        register(permittedPersonType);
        register(tabs);

        loadInfo();
    }

    public final Boolean getEnableDepartmentsStep() {
        return (Boolean) get(enableDepartmentsStep);
    }

    public final Integer getDepartmentsStepSortKey() {
        return (Integer) get(departmentsStepSortKey);
    }

    public final Boolean getEnableDepartmentInstitutesStep() {
        return (Boolean) get(enableDepartmentInstitutesStep);
    }

    public final Integer getDepartmentInstitutesStepSortKey() {
        return (Integer) get(departmentInstitutesStepSortKey);
    }

    public final Boolean getEnableProjectsStep() {
        return (Boolean) get(enableProjectsStep);
    }

    public final Integer getProjectsStepSortKey() {
        return (Integer) get(projectsStepSortKey);
    }

    public final Boolean getEnableProjectInstitutesStep() {
        return (Boolean) get(enableProjectInstitutesStep);
    }

    public final Integer getProjectInstitutesStepSortKey() {
        return (Integer) get(projectInstitutesStepSortKey);
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
