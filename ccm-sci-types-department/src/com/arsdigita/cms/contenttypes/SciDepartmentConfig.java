/*
 * Copyright (c) 2013 Jens Pelzetter
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package com.arsdigita.cms.contenttypes;

import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.parameter.BooleanParameter;
import com.arsdigita.util.parameter.IntegerParameter;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.StringParameter;

/**
 *  Configuration for SciDepartment.
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
    private final Parameter activeStatus;
    private final Parameter headRole;
    private final Parameter viceHeadRole;
    private final Parameter secretariatRole;
    private final Parameter showHeadInList;
    private final Parameter showViceHeadInList;
    private final Parameter showSecretriatInList;
    private final Parameter listShowRoleContacts;

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

        activeStatus = new StringParameter(
                "com.arsdigita.cms.contenttypes.scidepartment.summarytab.status.active",
                Parameter.REQUIRED,
                "active");

        headRole = new StringParameter(
                "com.arsdigita.cms.contenttypes.scidepartment.heads.role",
                Parameter.REQUIRED,
                "head");

        viceHeadRole = new StringParameter(
                "com.arsdigita.cms.contenttypes.scidepartment.viceheads.role",
                Parameter.REQUIRED,
                "vicehead");

        secretariatRole = new StringParameter(
                "com.arsdigita.cms.contenttypes.scidepartment.secretariat.role",
                Parameter.REQUIRED,
                "secretariat");

        showHeadInList = new BooleanParameter(
                "com.arsdigita.cms.contenttypes.sciproject.listview.show_head",
                Parameter.REQUIRED,
                Boolean.TRUE);

        showViceHeadInList = new BooleanParameter(
                "com.arsdigita.cms.contenttypes.sciproject.listview.show_vicehead",
                Parameter.REQUIRED,
                Boolean.TRUE);

        showSecretriatInList = new BooleanParameter(
                "com.arsdigita.cms.contenttypes.sciproject.listview.show_secretriat",
                Parameter.REQUIRED,
                Boolean.TRUE);

        listShowRoleContacts = new BooleanParameter(
                "com.arsdigita.cms.contenttypes.sciproject.listview.show_role_contacts",
                Parameter.REQUIRED,
                Boolean.TRUE);

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
        register(activeStatus);
        register(headRole);
        register(viceHeadRole);
        register(secretariatRole);
        register(showHeadInList);
        register(showViceHeadInList);
        register(showSecretriatInList);
        register(listShowRoleContacts);

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
    
    public final String getActiveStatus() {
        return (String) get(activeStatus);
    }

    public String getHeadRole() {
        return (String) get(headRole);
    }

    public String getViceHeadRole() {
        return (String) get(viceHeadRole);
    }

    public String getSecretariatRole() {
        return (String) get(secretariatRole);
    }

    public Boolean getShowHeadInList() {
        return (Boolean) get(showHeadInList);
    }

    public Boolean getShowViceHeadInList() {
        return (Boolean) get(showViceHeadInList);
    }

    public Boolean getShowSecretariatInList() {
        return (Boolean) get(showSecretriatInList);
    }

    public Boolean getListShowRoleContacts() {
        return (Boolean) get(listShowRoleContacts);
    }

}
