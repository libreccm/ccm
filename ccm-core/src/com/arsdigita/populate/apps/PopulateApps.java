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
package com.arsdigita.populate.apps;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TransactionContext;
import com.arsdigita.populate.Utilities;
import com.arsdigita.portal.Portal;
import com.arsdigita.web.Application;

/**
 * @author bche
 */
public class PopulateApps {
    private static Logger s_log = Logger.getLogger(PopulateApps.class);
    private PopulateAppPairCollection m_PopAppPairs;
    private String m_sBaseStringSeed;
    
    /**
     * Constructor.  returns a new PopulateApps, which populates a collection of applications
     * @param popAppPairs popAppPairs collection for population
     */
    public PopulateApps(PopulateAppPairCollection popAppPairs) {
        m_PopAppPairs = popAppPairs;
    }

    public void setBaseStringSeed(String sSeed) {
        m_sBaseStringSeed = sSeed;
    }
    
    public String getBaseStringSeed() {
        return m_sBaseStringSeed;
    }    
    
    /**
     * Populates a Portal.  Also adds appropriate portlets from the created applications
     * to the portal
     * @param iNumApps number of app instances to create
     * @param appParent the parent application of the apps created
     * @param portal the portal to populate
     * @return a List of BigDecimals which are the ID'sof the apps created during this method      
     */    
    public List populate(int iNumApps, Application appParent, Portal portal) {
        Session ses = SessionManager.getSession();
        TransactionContext txn = ses.getTransactionContext();                                
        
        String sBaseString = Utilities.getBaseString(m_sBaseStringSeed);
        
        int iSize  = m_PopAppPairs.getSize();
        int iTotalSize = iNumApps * iSize;
        s_log.info("Creating " + iNumApps + " instaces of " + iSize + " applications for a total of " +
            iTotalSize + " apps");

        ArrayList apps = new ArrayList(iTotalSize);
                        
        //iterate through populate apps        
        for (int i=0; i < iSize; i++) {
            PopulateAppPair popAppPair = m_PopAppPairs.getPopulateApp(i);            
            PopulateApp popApp = popAppPair.getPopulateApp();
            List args = popAppPair.getArgs();
            
            popApp.setBaseStringSeed(m_sBaseStringSeed);
            
            for (int j=0; j < iNumApps; j++) {
                //remove spaces from the url
                String sTitle = popApp.getAppType().getTitle().replace(' ', '_') + sBaseString + j;
                s_log.info("Creating app " + sTitle + " with args " + args);
                popApp.createApp(sTitle, appParent);
                popApp.populateApp(args);             
                apps.add(popApp.getApp().getID());    
                
                //get the portlet to add if we are populating a portlet
                if (portal != null) {      
                    //put this in a txn so that the updates to portalParent are committed
                    txn.beginTxn();              
                    //refresh view of portal so we can save it        
                    portal = (Portal)Portal.retrieve(portal.getOID());                  
                    //use a cellnumber of 1 for now
                    portal.addPortlet(popApp.getPortlet(), 1);                     
                    portal.save();
                    txn.commitTxn();                                     
                }                       
            }                        
        }
        return apps;            
        
    }
    
    /**
     * creates and populates application instances
     * @param iNumApps the number of application instances to create for each
     * application in this object's popAppPairs collection
     * @param parent the parent app under which to create the new app instances, or null if there is no parent     
     * @return a List of BigDecimals, which are the ID's of the applications created during this method
     */
    public List populate(int iNumApps, Application parent) {        
        return populate(iNumApps, parent, null);
    }
    
    /**
     * Populates a Portal.  Also adds appropriate portlets from the created applications
     * to the portal
     * @param iNumApps number of app instances to create
     * @param parent the portal to populate
     * @return a List of BigDecimals which are the ID'sof the apps created during this method      
     */
    public List populate(int iNumApps, Portal parent) {    
        return populate(iNumApps, null, parent);            
    }
    
    /**
     * Convenience method that creates and populates application instances as root apps
     * @param iNumApps the number of application instances to create for each
     * application in this object's popAppPairs collection
     * @return a List of BigDecimals, which are the ID's of the applications created during this method
     */
    public List populate(int iNumApps) {        
        return populate(iNumApps, null, null);
    }
}
