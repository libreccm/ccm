package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.cms.contenttypes.SciInstitute;
import com.arsdigita.cms.contenttypes.SciInstituteConfig;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class SciInstituteExtraXmlGenerator 
extends GenericOrgaUnitExtraXmlGenerator {

    @Override
    public String getTabConfig() {
        final SciInstituteConfig config = SciInstitute.getConfig();
        return config.getTabs();
    }
    
}
