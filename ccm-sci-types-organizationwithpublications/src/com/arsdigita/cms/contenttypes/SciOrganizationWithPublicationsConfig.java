package com.arsdigita.cms.contenttypes;

import com.arsdigita.util.parameter.BooleanParameter;
import com.arsdigita.util.parameter.Parameter;

/**
 *
 * @author Jens Pelzetter 
 */
public class SciOrganizationWithPublicationsConfig extends SciOrganizationConfig {

    private final Parameter m_organizationPublicationsMerge;

    public SciOrganizationWithPublicationsConfig() {
        super();

        m_organizationPublicationsMerge =
        new BooleanParameter(
                "com.arsdigita.cms.contenttypes.sciorganization.publications_merge",
                             Parameter.REQUIRED,
                             Boolean.TRUE);
        
        register(m_organizationPublicationsMerge);
        
        loadInfo();
    }
    
    public final boolean getOrganizationPublicationsMerge() {
        return (Boolean) get(m_organizationPublicationsMerge);
    }
}
