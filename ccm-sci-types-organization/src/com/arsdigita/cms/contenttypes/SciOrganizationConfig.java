package com.arsdigita.cms.contenttypes;

import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.parameter.BooleanParameter;
import com.arsdigita.util.parameter.Parameter;

/**
 * Configuration class for the complete ccm-sci-types-organization-module.
 *
 * @author Jens Pelzetter
 */
public class SciOrganizationConfig extends AbstractConfig {

    private final Parameter m_organizationAddPersonHide;
    private final Parameter m_organizationAddDepartmentHide;
    private final Parameter m_organizationAddProjectHide;
    private final Parameter m_organizationAddContactHide;
    private final Parameter m_organizationMembersAllInOne;
    private final Parameter m_organizationMembersMerge;
    private final Parameter m_organizationProjectsAllInOne;
    private final Parameter m_organizationProjectsMerge;
    private final Parameter m_departmentAddPersonHide;
    private final Parameter m_departmentAddSubDepartmentHide;
    private final Parameter m_departmentAddProjectHide;
    private final Parameter m_departmentSetOrganizationHide;
    private final Parameter m_departmentSetSuperDepartmentHide;
    private final Parameter m_departmentAddContactHide;
    private final Parameter m_departmentPublicationsHide;
    private final Parameter m_projectAddPersonHide;
    private final Parameter m_projectAddOrganizationHide;
    private final Parameter m_projectAddDepartmentHide;
    private final Parameter m_projectAddSubProjectHide;
    private final Parameter m_projectSetSuperProjectHide;
    private final Parameter m_projectAddContactHide;
    private final Parameter m_organizationDescriptionDhtml;
    private final Parameter m_departmentDescriptionDhtml;
    private final Parameter m_projectDescriptionDhtml;
    private final Parameter m_projectFundingHide;
    private final Parameter m_projectFundingDhtml;
    private final Parameter m_projectFundingVolumeHide;
    private final Parameter m_projectMaterialsHide;
    private final Parameter m_projectMembersAllInOne;
    private final Parameter m_projectMembersMerge;

    public SciOrganizationConfig() {

        m_organizationAddPersonHide = new BooleanParameter(
                "com.arsdigita.cms.contenttypes.sciorganization.add_person_hide",
                Parameter.REQUIRED,
                Boolean.FALSE);
        m_organizationAddDepartmentHide = new BooleanParameter(
                "com.arsdigita.cms.contenttypes.sciorganization.add_department_hide",
                Parameter.REQUIRED,
                Boolean.FALSE);
        m_organizationAddProjectHide = new BooleanParameter(
                "com.arsdigita.cms.contenttypes.sciorganization.add_project_hide",
                Parameter.REQUIRED,
                Boolean.FALSE);
        m_organizationAddContactHide = new BooleanParameter(
                "com.arsdigita.cms.contenttypes.sciorganization.add_contact_hide",
                Parameter.REQUIRED,
                Boolean.FALSE);
        m_organizationMembersAllInOne = new BooleanParameter(
                "com.arsdigita.cms.contenttypes.sciorganization.members_all_in_one",
                Parameter.REQUIRED,
                Boolean.FALSE);
        m_organizationMembersMerge = new BooleanParameter(
                "com.arsdigita.cms.contenttypes.sciorganization.members_merge",
                Parameter.REQUIRED,
                Boolean.TRUE);
        m_organizationProjectsAllInOne = new BooleanParameter(
                "com.arsdigita.cms.contenttypes.sciorganization.projects_all_in_one",
                Parameter.REQUIRED,
                Boolean.FALSE);
        m_organizationProjectsMerge = new BooleanParameter(
                "com.arsdigita.cms.contenttypes.sciorganization.projects_merge",
                Parameter.REQUIRED,
                Boolean.TRUE);
        m_departmentAddPersonHide = new BooleanParameter(
                "com.arsdigita.cms.contenttypes.scidepartment.add_person_hide",
                Parameter.REQUIRED,
                Boolean.FALSE);
        m_departmentAddSubDepartmentHide = new BooleanParameter(
                "com.arsdigita.cms.contenttypes.scidepartment.add_subdepartment_hide",
                Parameter.REQUIRED,
                Boolean.TRUE);
        m_departmentAddProjectHide = new BooleanParameter(
                "com.arsdigita.cms.contenttypes.scidepartment.add_project_hide",
                Parameter.REQUIRED,
                Boolean.FALSE);
        m_departmentSetOrganizationHide = new BooleanParameter(
                "com.arsdigita.cms.contenttypes.scidepartment.organization_hide",
                Parameter.REQUIRED,
                Boolean.FALSE);
        m_departmentSetSuperDepartmentHide = new BooleanParameter(
                "com.arsdigita.cms.contenttypes.scidepartment.set_superdepartment_hide",
                Parameter.REQUIRED,
                Boolean.TRUE);
        m_departmentAddContactHide = new BooleanParameter(
                "com.arsdigita.cms.contenttypes.scidepartment.add_contact_hide",
                Parameter.REQUIRED,
                Boolean.FALSE);
        m_departmentPublicationsHide = new BooleanParameter(
                "com.arsdigita.cms.contenttypes.scidepartment.publications_hide",
                Parameter.REQUIRED,
                Boolean.FALSE);
        m_projectAddPersonHide = new BooleanParameter(
                "com.arsdigita.cms.contenttypes.sciproject.add_person_hide",
                Parameter.REQUIRED,
                Boolean.FALSE);
        m_projectAddOrganizationHide = new BooleanParameter(
                "com.arsdigita.cms.contenttypes.sciproject.add_organization_hide",
                Parameter.REQUIRED,
                Boolean.FALSE);
        m_projectAddDepartmentHide = new BooleanParameter(
                "com.arsdigita.cms.contenttypes.sciproject.add_department_hide",
                Parameter.REQUIRED,
                Boolean.FALSE);
        m_projectAddSubProjectHide = new BooleanParameter(
                "com.arsdigita.cms.contenttypes.sciproject.add_subproject_hide",
                Parameter.REQUIRED,
                Boolean.TRUE);
        m_projectSetSuperProjectHide = new BooleanParameter(
                "com.arsdigita.cms.contenttypes.sciproject.set_superproject_hide",
                Parameter.REQUIRED,
                Boolean.TRUE);
        m_projectAddContactHide = new BooleanParameter(
                "com.arsdigita.cms.contenttypes.sciorganization.add_project_hide",
                Parameter.REQUIRED,
                Boolean.FALSE);
        m_organizationDescriptionDhtml = new BooleanParameter(
                "com.arsdigita.cms.contenttypes.sciorganization.description_dhtml",
                Parameter.REQUIRED,
                Boolean.TRUE);
        m_departmentDescriptionDhtml = new BooleanParameter(
                "com.arsdigita.cms.contenttypes.scidepartment.description_dhtml",
                Parameter.REQUIRED,
                Boolean.TRUE);
        m_projectDescriptionDhtml = new BooleanParameter(
                "com.arsdigita.cms.contenttypes.sciproject.description_dhtml",
                Parameter.REQUIRED,
                Boolean.TRUE);

        m_projectFundingHide = new BooleanParameter(
                "com.arsdigita.cms.contenttypes.sciproject.funding_hide",
                Parameter.REQUIRED,
                Boolean.FALSE);
        m_projectFundingDhtml = new BooleanParameter(
                "com.arsdigita.cms.contenttypes.sciproject.funding_dhtml",
                Parameter.REQUIRED,
                Boolean.FALSE);
        m_projectFundingVolumeHide = new BooleanParameter(
                "com.arsdigita.cms.contenttypes.sciproject.funding_volume_hide",
                Parameter.REQUIRED,
                Boolean.FALSE);
        m_projectMaterialsHide = new BooleanParameter(
                "com.arsdigita.cms.contenttypes.sciproject.materials_hide",
                Parameter.REQUIRED,
                Boolean.FALSE);

        m_projectMembersAllInOne = new BooleanParameter(
                "com.arsdigita.cms.contenttypes.sciproject.members_all_in_one",
                Parameter.REQUIRED,
                Boolean.TRUE);
        m_projectMembersMerge = new BooleanParameter(
                "com.arsdigita.cms.contenttypes.sciproject.members_merge",
                Parameter.REQUIRED,
                Boolean.FALSE);

        register(m_organizationAddPersonHide);
        register(m_organizationAddDepartmentHide);
        register(m_organizationAddProjectHide);
        register(m_organizationMembersAllInOne);
        register(m_organizationMembersMerge);
        register(m_organizationProjectsAllInOne);
        register(m_organizationProjectsMerge);

        register(m_departmentAddPersonHide);
        register(m_departmentAddSubDepartmentHide);
        register(m_departmentAddProjectHide);
        register(m_departmentSetOrganizationHide);
        register(m_departmentSetSuperDepartmentHide);
        register(m_departmentPublicationsHide);

        register(m_projectAddPersonHide);
        register(m_projectAddOrganizationHide);
        register(m_projectAddDepartmentHide);
        register(m_projectAddSubProjectHide);
        register(m_projectSetSuperProjectHide);

        register(m_organizationDescriptionDhtml);
        register(m_departmentDescriptionDhtml);
        register(m_projectDescriptionDhtml);

        register(m_organizationAddContactHide);
        register(m_departmentAddContactHide);
        register(m_projectAddContactHide);

        register(m_projectFundingHide);
        register(m_projectFundingVolumeHide);
        register(m_projectFundingDhtml);
        register(m_projectMaterialsHide);

        register(m_projectMembersAllInOne);
        register(m_projectMembersMerge);

        loadInfo();
    }

    public final boolean getOrganizationAddPersonHide() {
        return (Boolean) get(m_organizationAddPersonHide);
    }

    public final boolean getOrganizationAddDepartmentHide() {
        return (Boolean) get(m_organizationAddDepartmentHide);
    }

    public final boolean getOrganizationAddProjectHide() {
        return (Boolean) get(m_organizationAddProjectHide);
    }

    public final boolean getOrganizationAddContactHide() {
        return (Boolean) get(m_organizationAddContactHide);
    }

    public final boolean getOrganizationMembersAllInOne() {
        return (Boolean) get(m_organizationMembersAllInOne);
    }

    public final boolean getOrganizationMembersMerge() {
        return (Boolean) get(m_organizationMembersMerge);
    }

    public final boolean getOrganizationProjectsAllInOne() {
        return (Boolean) get(m_organizationProjectsAllInOne);
    }

    public final boolean getOrganizationProjectsMerge() {
        return (Boolean) get(m_organizationProjectsMerge);
    }

    public final boolean getDepartmentAddPersonHide() {
        return (Boolean) get(m_departmentAddPersonHide);
    }

    public final boolean getDepartmentAddSubDepartmentHide() {
        return (Boolean) get(m_departmentAddSubDepartmentHide);
    }

    public final boolean getDepartmentAddProjectHide() {
        return (Boolean) get(m_departmentAddProjectHide);
    }

    public final boolean getDepartmentSetOrganizationHide() {
        return (Boolean) get(m_departmentSetOrganizationHide);
    }

    public final boolean getDepartmentSetSuperDepartmentHide() {
        return (Boolean) get(m_departmentSetSuperDepartmentHide);
    }

    public final boolean getDepartmentAddContactHide() {
        return (Boolean) get(m_departmentAddContactHide);
    }

    public final boolean getDepartmentPublicationsHide() {
        return (Boolean) get(m_departmentPublicationsHide);
    }

    public final boolean getProjectAddPersonHide() {
        return (Boolean) get(m_projectAddPersonHide);
    }

    public final boolean getProjectAddOrganizationHide() {
        return (Boolean) get(m_projectAddOrganizationHide);
    }

    public final boolean getProjectAddDepartmentHide() {
        return (Boolean) get(m_projectAddDepartmentHide);
    }

    public final boolean getProjectAddSubProjectHide() {
        return (Boolean) get(m_projectAddSubProjectHide);
    }

    public final boolean getProjectSetSuperProjectHide() {
        return (Boolean) get(m_projectSetSuperProjectHide);
    }

    public final boolean getProjectAddContactHide() {
        return (Boolean) get(m_projectAddContactHide);
    }

    public final boolean getOrganizationDescriptionDhtml() {
        return (Boolean) get(m_organizationDescriptionDhtml);
    }

    public final boolean getDepartmentDescriptionDhtml() {
        return (Boolean) get(m_departmentDescriptionDhtml);
    }

    public final boolean getProjectDescriptionDhtml() {
        return (Boolean) get(m_projectDescriptionDhtml);
    }

    public final boolean getProjectFundingHide() {
        return (Boolean) get(m_projectFundingHide);
    }

    public final boolean getProjectFundingDhtml() {
        return (Boolean) get(m_projectFundingDhtml);
    }

     public final boolean getProjectFundingVolumeHide() {
        return (Boolean) get(m_projectFundingVolumeHide);
    }

    public final boolean getProjectMaterialsHide() {
        return (Boolean) get(m_projectMaterialsHide);
    }

    public final boolean getProjectMembersAllInOne() {
        return (Boolean) get(m_projectMembersAllInOne);
    }

    public final boolean getProjectMembersMerge() {
        return (Boolean) get(m_projectMembersMerge);
    }
}
