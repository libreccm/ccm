/*
 * Copyright (C) 2011-2013 Universitaet Bremen. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */

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
    * @returns The key identifing this tab.
    */
    String getKey();
    
    /**
     * Sets the key which identifies the tab.
     *
     *@param key 
     */
    void setKey(String key);
    
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
