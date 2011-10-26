package com.arsdigita.cms.contenttypes;

import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.parameter.BooleanParameter;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.StringParameter;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class PublicationsConfig extends AbstractConfig {

    private final Parameter attachOrgaUnitsStep;
    private final Parameter attachPublicationsStepTo;

    public PublicationsConfig() {
        attachOrgaUnitsStep =
        new BooleanParameter(
                "com.arsdigita.cms.contenttypes.publications.attach_orgaunits_step",
                Parameter.REQUIRED,
                Boolean.FALSE);
        
        attachPublicationsStepTo =
        new StringParameter(
                "com.arsdigita.cms.contenttypes.publications.attach_publications_step_to", 
                Parameter.REQUIRED, 
                "");
        
        register(attachOrgaUnitsStep);
        register(attachPublicationsStepTo);
        
        loadInfo();
    }
    
    public Boolean getAttachOrgaUnitsStep() {
        return (Boolean) get(attachOrgaUnitsStep);
    }
    
    public String getAttachPublicationsStepTo() {
        return (String) get(attachPublicationsStepTo);
    }
}
