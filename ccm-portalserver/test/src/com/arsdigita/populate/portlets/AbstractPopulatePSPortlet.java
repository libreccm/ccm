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

import org.apache.log4j.Logger;

import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TransactionContext;
import com.arsdigita.portal.Portal;
import com.arsdigita.portal.Portlet;
import com.arsdigita.portal.PortletType;
import com.arsdigita.portal.apportlet.AppPortlet;
import com.arsdigita.portal.apportlet.AppPortletType;
import com.arsdigita.portalserver.PortalSite;
import com.arsdigita.portalserver.PortalTab;
import com.arsdigita.util.Assert;


/**
 * Abstract class for populating portlets.  
 * This differs from com.arsdigita.populate.portlets.AbstractPopulatePortlet in that
 * it can handle PortalServer AppPortlets as well as normal Portlets.  It does this by
 * setting the AppPortlet's parent application to the containing PortalSite. 
 * @author bche
 */
public abstract class AbstractPopulatePSPortlet implements PopulatePortlet {
    protected Logger s_log = Logger.getLogger(AbstractPopulatePSPortlet.class);
    private Portlet m_portlet = null;

    /* (non-Javadoc)
     * @see com.arsdigita.populate.portlets.PopulatePortlet#createPortlet(com.arsdigita.portal.Portal, int)
     */
    public void createPortlet(Portal parent, int iCellNumber) {
        Session ses = SessionManager.getSession();
        TransactionContext txn = ses.getTransactionContext();
        txn.beginTxn();
        //refresh the portal view so we can save it
        parent = Portal.retrieve(parent.getOID());
        
        m_portlet = Portlet.createPortlet(getPortletType(), null);
        
        //check if the portlet is an AppPortlet or a normal Portlet        
        if (m_portlet instanceof AppPortlet ) {            
            //this is an AppPortlet, so set its application to the portal site
            
            //try to get a PortalTab version of the parent
            //this will throw a NPE if the Portal is not a PortalTab
            PortalTab tab = PortalTab.retrieveTab(parent.getID());            
            PortalSite ps = ((PortalTab)tab).getPortalSite();
                        
            ((AppPortlet)m_portlet).setParentApplication(ps);            
            
            if (s_log.isDebugEnabled()) {
                s_log.debug("created AppPortlet: " + m_portlet.getTitle());
            }
        } else {
            //this is a normal portlet
            m_portlet.setParentResource(parent);            
            
            if (s_log.isDebugEnabled()) {
                s_log.debug("created normal Portlet: " + m_portlet.getTitle());
            }
        }                
        
        //no need to save m_portlet because Portal.afterSave() will save it                               
        parent.addPortlet(m_portlet, iCellNumber);
        parent.save();
        txn.commitTxn();              
                
        //refresh m_portlet so it is available to other txn's
        m_portlet = Portlet.retrievePortlet(m_portlet.getOID());         

    }

    /* (non-Javadoc)
     * @see com.arsdigita.populate.portlets.PopulatePortlet#populatePortlet()
     */
    public abstract void populatePortlet();

    /* (non-Javadoc)
     * @see com.arsdigita.populate.portlets.PopulatePortlet#getPortletType()
     */
    public abstract PortletType getPortletType();

    /* (non-Javadoc)
     * @see com.arsdigita.populate.portlets.PopulatePortlet#getPortlet()
     */
    public Portlet getPortlet() {
        return m_portlet;
    }

}
