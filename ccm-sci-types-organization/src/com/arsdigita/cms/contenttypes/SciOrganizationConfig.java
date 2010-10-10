package com.arsdigita.cms.contenttypes;

import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.parameter.BooleanParameter;
import com.arsdigita.util.parameter.Parameter;

/**
 *
 * @author Jens Pelzetter
 */
public class SciOrganizationConfig extends AbstractConfig {

    private final Parameter m_projectFundingHide;
    private final Parameter m_projectFundingDhtml;
    private final Parameter m_projectMaterialsHide;
    private final Parameter m_organizationPersonsHide;

    public SciOrganizationConfig() {
        m_projectFundingHide = new BooleanParameter(
                "com.arsdigita.cms.contenttypes.sciproject.funding_hide",
                Parameter.REQUIRED,
                Boolean.FALSE);
        m_projectFundingDhtml = new BooleanParameter(
                "com.arsdigita.cms.contenttypes.sciproject.funding_dhtml",
                Parameter.REQUIRED,
                Boolean.FALSE);
        m_projectMaterialsHide = new BooleanParameter(
                "com.arsdigita.cms.contenttypes.sciproject.materials_hide",
                Parameter.REQUIRED,
                Boolean.FALSE);
        m_organizationPersonsHide = new BooleanParameter(
                "com.arsdigita.cms.contenttypes.sciorganization.persons_hide",
                Parameter.REQUIRED,
                Boolean.TRUE);

        register(m_projectFundingHide);
        register(m_projectFundingDhtml);
        register(m_projectMaterialsHide);
        register(m_organizationPersonsHide);

        loadInfo();
    }

    public final boolean getProjectFundingHide() {
        return (Boolean) get(m_projectFundingHide);
    }

    public final boolean  getProjectFundingDhtml() {
        return (Boolean) get(m_projectFundingDhtml);
    }

    public final boolean getProjectMaterialsHide() {
        return (Boolean) get(m_projectMaterialsHide);
    }

    public final boolean getOrganizationPersonsHide() {
        return (Boolean) get(m_organizationPersonsHide);
    }
}
