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
public class SciDepartmentConfig extends AbstractConfig {

    private final Parameter enableSubDepartmentsStep;
    private final Parameter subDepartmentsStepSortKey;
    private final Parameter enableSuperDepartmentsStep;
    private final Parameter superDepartmentsStepSortKey;
    private final Parameter enableProjectsStep;
    private final Parameter projectsStepSortKey;
    private final Parameter enableProjectDepartmentsStep;
    private final Parameter projectDepartmentsStepSortKey;
    private final Parameter shortDescMaxLength;
    private final Parameter enableDescriptionDhtml;
    private final Parameter permittedPersonType;
    private final Parameter tabs;

    public SciDepartmentConfig() {

        enableSubDepartmentsStep =
        new BooleanParameter(
                "com.arsdigita.cms.contenttypes.scidepartment.enable.subdepartments_step",
                Parameter.REQUIRED,
                Boolean.TRUE);

        subDepartmentsStepSortKey =
        new IntegerParameter(
                "com.arsdigita.cms.contenttypes.scidepartment.subdepartments_step_sortkey",
                Parameter.REQUIRED,
                10);

        enableSuperDepartmentsStep =
        new BooleanParameter(
                "com.arsdigita.cms.contenttypes.scidepartment.enable.super_departments_step",
                Parameter.REQUIRED,
                Boolean.TRUE);

        superDepartmentsStepSortKey =
        new IntegerParameter(
                "com.arsdigita.cms.contenttypes.scidepartment.superdepartments_step_sortkey",
                Parameter.REQUIRED,
                20);

        enableProjectsStep =
        new BooleanParameter(
                "com.arsdigita.cms.contenttypes.scidepartment.enable.projects_step",
                Parameter.REQUIRED,
                Boolean.TRUE);

        projectsStepSortKey =
        new IntegerParameter(
                "com.arsdigita.cms.contenttypes.scidepartment.projects_step_sortkey",
                Parameter.REQUIRED,
                30);

        enableProjectDepartmentsStep =
        new BooleanParameter(
                "com.arsdigita.cms.contenttypes.scidepartment.enable.project_departments_step",
                Parameter.REQUIRED,
                Boolean.TRUE);

        projectDepartmentsStepSortKey =
        new IntegerParameter(
                "com.arsdigita.cms.contenttypes.scidepartment.project_departments_step_sortkey",
                Parameter.REQUIRED,
                40);

        shortDescMaxLength =
        new IntegerParameter(
                "com.arsdigita.cms.contenttypes.scidepartment.short_desc.max_length",
                Parameter.REQUIRED,
                500);

        enableDescriptionDhtml =
        new BooleanParameter(
                "com.arsdigita.cms.contenttypes.scidepartment.description.dhtml_enable",
                Parameter.REQUIRED,
                Boolean.TRUE);

        permittedPersonType =
        new StringParameter(
                "com.arsdigita.cms.contenttypes.sciproject.permitted_person_type",
                Parameter.REQUIRED,
                "com.arsdigita.cms.contenttypes.GenericPerson");

        tabs =
        new StringParameter(
                "com.arsdigita.cms.contenttypes.sciproject.tabs",
                Parameter.REQUIRED,
                "summary:com.arsdigita.cms.contenttypes.ui.SciDepartmentSummaryTab;desc:com.arsdigita.cms.contenttypes.ui.SciDepartmentDescTab;members:com.arsdigita.cms.contenttypes.ui.SciDepartmentMembersTab;projects:com.arsdigita.cms.contenttypes.ui.SciDepartmentProjectsTab;publications:com.arsdigita.cms.contenttypes.ui.SciDepartmentPublicationsTab");

        register(enableSubDepartmentsStep);
        register(subDepartmentsStepSortKey);
        register(enableSuperDepartmentsStep);
        register(superDepartmentsStepSortKey);
        register(enableProjectsStep);
        register(projectsStepSortKey);
        register(enableProjectDepartmentsStep);
        register(projectDepartmentsStepSortKey);
        register(shortDescMaxLength);
        register(enableDescriptionDhtml);
        register(permittedPersonType);
        register(tabs);

        loadInfo();
    }

    public final Boolean getEnableSubDepartmentsStep() {
        return (Boolean) get(enableSubDepartmentsStep);
    }

    public Integer getSubDepartmentsStepSortKey() {
        return (Integer) get(subDepartmentsStepSortKey);
    }

    public final Boolean getEnableSuperDepartmentsStep() {
        return (Boolean) get(enableSuperDepartmentsStep);
    }

    public Integer getSuperDepartmentsStepSortKey() {
        return (Integer) get(superDepartmentsStepSortKey);
    }

    public final Boolean getEnableProjectsStep() {
        return (Boolean) get(enableProjectsStep);
    }

    public Integer getProjectsStepSortKey() {
        return (Integer) get(projectsStepSortKey);
    }

    public final Boolean getEnableProjectDepartmentsStep() {
        return (Boolean) get(enableProjectDepartmentsStep);
    }

    public Integer getProjectDepartmentsStepSortKey() {
        return (Integer) get(projectDepartmentsStepSortKey);
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
