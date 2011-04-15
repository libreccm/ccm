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

import java.util.List;

import org.apache.log4j.Logger;

import com.arsdigita.initializer.Configuration;
import com.arsdigita.initializer.InitializationException;
import com.arsdigita.kernel.BaseInitializer;
import com.arsdigita.kernel.UserCollection;
import com.arsdigita.populate.PopulateUsers;
import com.arsdigita.populate.apps.PopulateApp;
import com.arsdigita.populate.apps.PopulateAppPair;
import com.arsdigita.populate.apps.PopulateAppPairCollection;
import com.arsdigita.populate.portlets.PopulatePortlet;
import com.arsdigita.populate.portlets.PopulatePortletCollection;
import com.arsdigita.util.Assert;

/**
 * @author bche
 */
public class Initializer extends BaseInitializer {
        private static final Logger s_log =
            Logger.getLogger(Initializer.class.getName());
        private Configuration m_conf = new Configuration();    

	/* (non-Javadoc)
	 * @see com.arsdigita.initializer.Initializer#getConfiguration()
	 */
	public Configuration getConfiguration() {		
                m_conf.initParameter("baseStringSeed",
                        "The base string seed to use for generating String names.",
                        String.class,
                        null);           
                m_conf.initParameter("numUsers", "number of users to create", Integer.class);
                m_conf.initParameter("numPortalSiteLevels", "number of levels/depth to nest portal sites", Integer.class);
                m_conf.initParameter("numPortalSitesPerLevel", "number of portal sites to create for each level", Integer.class);                
                m_conf.initParameter(
                    "populateAppPairs",
                    "A list pair, consisting of the PopulateApp class with which to populate and"
                        + "the arguments for that class",
                    List.class);        
                m_conf.initParameter("populatePortlets", "A list of PopulatePortlet classes", List.class);

                return m_conf;
	}

	/* (non-Javadoc)
	 * @see com.arsdigita.kernel.BaseInitializer#doStartup()
	 */
	protected void doStartup() {             
                String sBaseStringSeed = (String)m_conf.getParameter("baseStringSeed");   
                int iUsers = ((Integer) m_conf.getParameter("numUsers")).intValue();
                int iPortalSiteLevels = ((Integer) m_conf.getParameter("numPortalSiteLevels")).intValue();
                int iPortalSitesPerLevel = ((Integer) m_conf.getParameter("numPortalSitesPerLevel")).intValue();                
                List popAppsList = (List) m_conf.getParameter("populateAppPairs");
                List popPortletsList = (List) m_conf.getParameter("populatePortlets");
                                        
                if (s_log.isDebugEnabled()) {            
                    s_log.debug("Using BaseStringSeed " + sBaseStringSeed);
                }                                        

                //get the popApps
                PopulateAppPairCollection popApps = initPopApps(popAppsList);
                
                //get the popPortlets
                PopulatePortletCollection popPortlets = initPopPortlets(popPortletsList);                
                
                //populate users
                s_log.info("Begin populating " + iUsers + " users");

                PopulateUsers popUsers = new PopulateUsers();
                popUsers.setBaseStringSeed(sBaseStringSeed);
                popUsers.populate(iUsers);
                UserCollection users = popUsers.getPopulatedUsers();

                s_log.info("End populating " + iUsers + " users");

                //populate portalserver
                s_log.info("Begin populating portal server");
                
                PopulatePortalSites popPortals = new PopulatePortalSites(popApps, popPortlets);
                popPortals.setBaseStringSeed(sBaseStringSeed);
                popPortals.populate(iPortalSiteLevels, iPortalSitesPerLevel, null, users);
                
                s_log.info("End populating portal server");
	}
    
        private PopulateAppPairCollection initPopApps(List popAppsList) {
            PopulateAppPairCollection popAppsColl = new PopulateAppPairCollection();

            int iSize = popAppsList.size();
            for (int i = 0; i < iSize; i++) {
                List popAppParam = (List) popAppsList.get(i);
                // Assert.assertTrue(popAppParam.size() == 2);
                Assert.isTrue(popAppParam.size() == 2);

                String sPopApp = (String) popAppParam.get(0);
                s_log.debug("PopulateApp is " + sPopApp);
                PopulateApp popApp;
                try {
                    popApp = (PopulateApp) Class.forName(sPopApp).newInstance();
                } catch (Exception e) {
                    throw new InitializationException(e.getMessage());
                }
                List args = (List) popAppParam.get(1);
                s_log.debug("args are" + args);
                PopulateAppPair popAppPair = new PopulateAppPair(popApp, args);
                popAppsColl.addPopulateApp(popAppPair);
            }

            return popAppsColl;    
        }
        
        private PopulatePortletCollection initPopPortlets(List popPortletsList){
            PopulatePortletCollection popPortletsColl = new PopulatePortletCollection();
            
            int iSize = popPortletsList.size();
            for (int i=0; i < iSize; i++) {
                String sPopPortlet = (String )popPortletsList.get(i);
                s_log.debug("PopulatePortlet is " + sPopPortlet);
                PopulatePortlet popPortlet;
                try {
                    popPortlet = (PopulatePortlet)Class.forName(sPopPortlet).newInstance();
                } catch (Exception e) {
                    throw new InitializationException(e.getMessage());
                }
                popPortletsColl.addPopulatePortlet(popPortlet);
            }
            
            return popPortletsColl;            
        }

	/* (non-Javadoc)
	 * @see com.arsdigita.kernel.BaseInitializer#doShutdown()
	 */
	protected void doShutdown() {		
	}

}
