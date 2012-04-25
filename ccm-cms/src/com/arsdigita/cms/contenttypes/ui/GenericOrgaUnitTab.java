package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnit;
import com.arsdigita.xml.Element;

/**
 * Interface for a tab rendered by {@link GenericOrgaUnitExtraXmlGenerator}.
 * 
 * @author Jens Pelzetter 
 * @version $Id$
 */
public interface GenericOrgaUnitTab {
            
    /**
     * 
     * @param orgaunit
     * @param state 
     * @return Returns true if the provided orgaunit has data to show in this
     * tab, false otherwise.
     */
    boolean hasData(GenericOrganizationalUnit orgaunit,
                    PageState state);
    
    /**
     * The implementation of this method creates the XML output for the tab.
     * 
     * @param orgaunit The orgaunit which is the source of the data to use
     * @param parent parent element for the XML
     * @param state The current page state
     */
    void generateXml(GenericOrganizationalUnit orgaunit, 
                     Element parent, 
                     PageState state);
    
}
