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
    private final Parameter enableSuperDepartmentsStep;
    private final Parameter enableProjectsStep;
    private final Parameter enableProjectDepartmentsStep;
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

        enableSuperDepartmentsStep =
        new BooleanParameter(
                "com.arsdigita.cms.contenttypes.scidepartment.enable.super_departments_step",
                Parameter.REQUIRED,
                Boolean.TRUE);

        enableProjectsStep =
        new BooleanParameter(
                "com.arsdigita.cms.contenttypes.scidepartment.enable.projects_step",
                Parameter.REQUIRED,
                Boolean.TRUE);

        enableProjectDepartmentsStep =
        new BooleanParameter(
                "com.arsdigita.cms.contenttypes.scidepartment.enable.project_departments_step",
                Parameter.REQUIRED,
                Boolean.TRUE);

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
                "summary:com.arsdigita.cms.contenttypes.ui.SciDepartmentSummaryTab;desc:com.arsdigita.cms.contenttypes.ui.SciDepartmentDescTab;members:com.arsdigita.cms.contenttypes.ui.SciDepartmentMembersTab");

        register(enableSubDepartmentsStep);
        register(enableSuperDepartmentsStep);
        register(enableProjectsStep);
        register(enableProjectDepartmentsStep);
        register(shortDescMaxLength);
        register(enableDescriptionDhtml);
        register(permittedPersonType);
        register(tabs);

        loadInfo();
    }

    public final Boolean getEnableSubDepartmentsStep() {
        return (Boolean) get(enableSubDepartmentsStep);
    }

    public final Boolean getEnableSuperDepartmentsStep() {
        return (Boolean) get(enableSuperDepartmentsStep);
    }
    
    public final Boolean getEnableProjectsStep() {
        return (Boolean) get(enableProjectsStep);
    }
    
     public final Boolean getEnableProjectDepartmentsStep() {
        return (Boolean) get(enableProjectDepartmentsStep);
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
