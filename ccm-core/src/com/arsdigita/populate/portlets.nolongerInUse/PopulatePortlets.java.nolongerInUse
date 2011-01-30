/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.populate.portlets;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.arsdigita.portal.Portal;
import com.arsdigita.portal.Portlet;


// ///////////////////////////////////////////////////////////////////////////
//
// NOT USED anywhere in the source tree.
// No documentation available.
// Retained temporarily until refactoring of test cases is completed.
//
// (pboy 2011.01.30)
//
// ///////////////////////////////////////////////////////////////////////////


/**
 * @author bche
 */
public class PopulatePortlets {
    private static Logger s_log = Logger.getLogger(PopulatePortlets.class);    
    private PopulatePortletCollection m_PopPortlets;

    /**
     * Creates a new PopulatePortlets.  Takes in a collection of PopulatePortlets to
     * use in populating a portal
     * @param popPortletsthe collection of PopulatePortlets to use in populating a portal
     */        
    public PopulatePortlets(PopulatePortletCollection popPortlets) {
        m_PopPortlets = popPortlets;
    }

    /**
     * Populates portal with portlets using this object's collection of PopulatePortlets
     * @param iNumPortlets the number of instances of each portlet to add
     * @param portal the portal to populate
     * @return a list of BigDecimal ID's of all the portlets created
     */        
    public List populate(int iNumPortlets, Portal portal) {                        
        int iSize  = m_PopPortlets.getSize();
        int iTotalSize = iNumPortlets * iSize;
        s_log.info("Creating " + iNumPortlets + " instaces of " + iSize + " portlets for a total of " +
            iTotalSize + " portlets");

        ArrayList portlets = new ArrayList(iTotalSize);
                
        //iterate through populate apps
        for (int i=0; i < iSize; i++) {
            PopulatePortlet popPortlet = m_PopPortlets.getPopulatePortlet(i);            
            
            for (int j=0; j < iNumPortlets; j++) {       
                //use cell number of 1 for now                         
                popPortlet.createPortlet(portal, 1);
                popPortlet.populatePortlet();
                Portlet portlet = popPortlet.getPortlet();
                s_log.info("added portlet of type " + popPortlet.getPortletType().getTitle() + " to portal " + portal.getTitle());                             
                portlets.add(portlet.getID());           
            }                        
        }
        return portlets;
    }        
}
