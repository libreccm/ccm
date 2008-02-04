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

/**
 * @author bche
 */
public abstract class AbstractPopulatePortlet implements PopulatePortlet {
        protected Logger s_log = Logger.getLogger(AbstractPopulatePortlet.class);    
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
                
		m_portlet = Portlet.createPortlet(getPortletType(), parent);                
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
