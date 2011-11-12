package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.cms.contenttypes.SciDepartment;
import com.arsdigita.cms.contenttypes.SciDepartmentConfig;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class SciDepartmentExtraXmlGenerator 
extends GenericOrgaUnitExtraXmlGenerator  {

    @Override
    public String getTabConfig() {
        final SciDepartmentConfig config = SciDepartment.getConfig();
        
        return config.getTabs();
    }
    
}
