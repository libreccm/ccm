package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.cms.contenttypes.SciProject;
import com.arsdigita.cms.contenttypes.SciProjectConfig;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class SciProjectExtraXmlGenerator
        extends GenericOrgaUnitExtraXmlGenerator {

    @Override
    public String getTabConfig() {
        final SciProjectConfig config = SciProject.getConfig();
        return config.getTabs();        
    }
}
