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
package com.arsdigita.populate.portalserver;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.arsdigita.kernel.EmailAddress;
import com.arsdigita.kernel.User;
import com.arsdigita.kernel.UserCollection;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TransactionContext;
import com.arsdigita.populate.Utilities;
import com.arsdigita.populate.apps.PopulateAppPairCollection;
import com.arsdigita.populate.apps.PopulateApps;
import com.arsdigita.populate.portlets.PopulatePortletCollection;
import com.arsdigita.populate.portlets.PopulatePortlets;
import com.arsdigita.portalserver.PortalSite;
import com.arsdigita.portalserver.PortalTab;
import com.arsdigita.portalserver.PortalTabCollection;

/**
 * @author bche
 */
public class PopulatePortalSites {
	private static Logger s_log = Logger.getLogger(PopulatePortalSites.class);
	private PopulateAppPairCollection m_popAppPairs = null;
	private PopulatePortletCollection m_popPortlets = null;
        private String m_sBaseStringSeed = null;

	public PopulatePortalSites(
		PopulateAppPairCollection popAppPairs,
		PopulatePortletCollection popPortlets) {
		m_popAppPairs = popAppPairs;
		m_popPortlets = popPortlets;
	}

        public void setBaseStringSeed(String sSeed) {
            m_sBaseStringSeed = sSeed;
        }
    
        public String getBaseStringSeed() {
            return m_sBaseStringSeed;
        }    

	public void populate(
		int iPortalSiteLevels,
		int iPortalSitesPerLevel,
		PortalSite rootPortal,
		UserCollection users) {
		Session ses = SessionManager.getSession();
		TransactionContext txn = ses.getTransactionContext();

		String sBaseName = "Portal" + Utilities.getBaseString(m_sBaseStringSeed);

		//      create the portal sites
		ArrayList portalSitesList =
			new ArrayList(iPortalSiteLevels * iPortalSitesPerLevel);
		ArrayList siblings = null;
		PortalSite parent = rootPortal;

		for (int i = 0; i < iPortalSiteLevels; i++) {
			txn.beginTxn();

			if (i > 0) {
				parent = (PortalSite) siblings.get(0);
			}

			siblings = new ArrayList(iPortalSitesPerLevel);

			for (int j = 0; j < iPortalSitesPerLevel; j++) {
				String sName = sBaseName + i + j;
				PortalSite portal = setupPortalSite(sName, parent, users);
                                //rewind users so we can use it again
                                users.rewind();
				s_log.info("created PortalSite " + sName);
                                if (s_log.isDebugEnabled()) {
                                    String sURL = "";
                                    PortalSite p = portal;
                                    do {
                                        sURL = p.getTitle() + "/" + sURL;
                                        p = (PortalSite)p.getParentApplication();                                        
                                    } while (p != null);
                                    s_log.debug("PortalSite " + sName + " has url " + sURL);
                                }
				if (parent != null) {
					s_log.info("PortalSite " + sName + " is a child of " + parent.getTitle());
				}

				portalSitesList.add(portal);
				siblings.add(portal);
			}
			//make all the portal sites on this level related to each other
			//also make them all the child of a portal site on the previous level
			//note that this will make the number of relationsips
			//in the system = iPortalSitesPerLevel^2 * iPortalSiteLevels
			for (int y = 0; y < siblings.size(); y++) {
				PortalSite bro = (PortalSite) siblings.get(y);

				for (int z = 0; z < siblings.size(); z++) {
					if (z != y) {
						PortalSite sis = (PortalSite) siblings.get(z);
						bro.addRelatedPortalSite(sis);
						bro.save();
						s_log.info("PortalSite " + bro.getTitle() + " is related to " + sis.getTitle());
                        
					}
				}
			}

			txn.commitTxn();
		}
		                
                s_log.info("Adding applications and AppPortlets to PortalSites");                
                for (int i=0; i < portalSitesList.size(); i++) {
                        PopulateApps popApps = new PopulateApps(m_popAppPairs);                        
                        popApps.setBaseStringSeed(Utilities.getBaseString(getBaseStringSeed()));
                        
                        PopulatePortlets popPortlets = new PopulatePortlets(m_popPortlets);
                        PortalSite portal = (PortalSite)portalSitesList.get(i);                   
                        if (s_log.isDebugEnabled()) {
                            s_log.debug("adding apps and portlets to PortalSite " + portal.getTitle());
                        }                             
                                                                        
                        //get a tab on the portal site to which to add portlets                        
                        PortalTabCollection tabs = portal.getTabsForPortalSite();
                        tabs.next();
                        PortalTab tab = tabs.getPortalTab();      
                        tabs.close();
                                                
                        //create applications and add app portlets
                        if (s_log.isDebugEnabled()) {
                            s_log.debug("add apps and ApplicationPortlets to tab " + tab.getTitle());
                        }                        
                        popApps.populate(1, portal, tab);
                                               
                        //add portlets to Portaltabs                        
                        if (s_log.isDebugEnabled()) {
                            s_log.debug("add portlets to tab " + tab.getTitle());
                        }
                        popPortlets.populate(1, tab);                                                                            
                }                	                
	}
    
        private PortalSite setupPortalSite(String sName, PortalSite parent, UserCollection users) {
                PortalSite portal = PortalSite.createPortalSite(sName, sName, parent);
                
                //add PortalTabs
                int iTabs = 3;
                for (int i=0; i < iTabs; i ++) {
                    PortalTab tab = PortalTab.createTab("Tab" + i);
                    //use wide layout to ensure compatibility                        
                    tab.setLayout("W");
                    portal.addPortalTab(tab);
                }

                //add members
                while (users.next()) {
                    User user = users.getUser();                    
                    portal.addMember(user);
                    
                    String sUser;
                    Iterator iter = user.getEmailAddresses();
                    if (iter.hasNext()){
                        sUser = ((EmailAddress)iter.next()).toString();
                    } else {
                        sUser = user.getDisplayName();
                    }
                    s_log.info("added user " + sUser + " to portalsite " + sName);
                }
                
                portal.save();                            
                return portal;
        }
}

