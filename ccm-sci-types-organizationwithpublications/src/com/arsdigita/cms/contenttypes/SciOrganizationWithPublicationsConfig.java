package com.arsdigita.cms.contenttypes;

import com.arsdigita.util.parameter.BooleanParameter;
import com.arsdigita.util.parameter.Parameter;

/**
 *
 * @author Jens Pelzetter 
 */
public class SciOrganizationWithPublicationsConfig extends SciOrganizationConfig {

    private final Parameter m_organizationPublicationsMerge;
    private final Parameter m_organizationPublicationsSeparateWorkingPapers;
    private final Parameter m_departmentPublicationsSeparateWorkingPapers;
    private final Parameter m_projectPublicationsSeparateWorkingPapers;

    public SciOrganizationWithPublicationsConfig() {
        super();

        m_organizationPublicationsMerge =
        new BooleanParameter(
                "com.arsdigita.cms.contenttypes.sciorganization.publications_merge",
                Parameter.REQUIRED,
                Boolean.TRUE);
        register(m_organizationPublicationsMerge);

        m_organizationPublicationsSeparateWorkingPapers =
        new BooleanParameter(
                "com.arsdigita.cms.contenttypes.sciorganization..organization.workingpapers_separate",
                Parameter.REQUIRED,
                Boolean.TRUE);
        register(m_organizationPublicationsSeparateWorkingPapers);

        m_departmentPublicationsSeparateWorkingPapers =
        new BooleanParameter(
                "com.arsdigita.cms.contenttypes.sciorganization.department.workingpapers_separate",
                Parameter.REQUIRED,
                Boolean.TRUE);
        register(m_departmentPublicationsSeparateWorkingPapers);

        m_projectPublicationsSeparateWorkingPapers =
        new BooleanParameter(
                "com.arsdigita.cms.contenttypes.sciorganization.project.workingpapers_separate",
                Parameter.REQUIRED,
                Boolean.FALSE);
        register(m_projectPublicationsSeparateWorkingPapers);

        loadInfo();
    }

    public final boolean getOrganizationPublicationsMerge() {
        return (Boolean) get(m_organizationPublicationsMerge);
    }

    public final boolean getOrganizationPublicationsSeparateWorkingPapers() {
        return (Boolean) get(m_organizationPublicationsSeparateWorkingPapers);
    }

    public final boolean getDepartmentPublicationsSeparateWorkingPapers() {
        return (Boolean) get(m_departmentPublicationsSeparateWorkingPapers);
    }

    public final boolean getProjectPublicationsSeparateWorkingPapers() {
        return (Boolean) get(m_projectPublicationsSeparateWorkingPapers);
    }
    
}
