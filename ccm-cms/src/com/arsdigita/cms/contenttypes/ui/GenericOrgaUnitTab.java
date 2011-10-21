package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnit;
import com.arsdigita.xml.Element;

/**
 * 
 * @author Jens Pelzetter 
 * @version $Id$
 */
public interface GenericOrgaUnitTab {
            
    boolean hasData(GenericOrganizationalUnit orgaunit);
    
    void generateXml(GenericOrganizationalUnit orgaunit, 
                     Element parent, 
                     PageState state);
    
}
