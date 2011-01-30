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

import com.arsdigita.portal.apportlet.AppPortlet;
import com.arsdigita.web.Application;
import com.arsdigita.web.ApplicationType;


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
public interface PopulateApp {
    
    /**
     * Sets the BaseStringSeed to use in populating data
     * @param sSeed the seed value to use for generating String data in population
     */
    public void setBaseStringSeed(String sSeed);
    
    /**
     * Gets the BaseStringSeed used in populating data
     * @return the BaseStringSeed used in populating data
     */
    public String getBaseStringSeed();
    
    /**
     * Create the application with title, sTitle and parent Application, parent
     * @param sTitle the title of the new app to create and populate
     * @param parent the parent application or null if there is none
     */
    public void createApp(String sTitle, Application parent);        
    
    /**
     * Populates the created app using the arguments in args.
     * This method runs in its own transaction
     * @param args the arguments to use in populating the app
     */
    public void populateApp(java.util.List args);        
    
    
    /**
     * Returns a description of the arguments for populating this application
     * @return description of the args to PopulateApp()
     */
    public String getArgsDescription();
    
    /**
     * Returns the app instance created for populating data
     */
    public Application getApp();
    
    /**
     * Returns the application type of the app we are creating and populating
     * @return the Application type we are creating and populating
     */
    public ApplicationType getAppType();
    
    /**
     * Returns a new portlet for the populated application, if appropriate.  Otherwise returns null.
     * @return a new portlet for the populated application, if appropriate.  Otherwise returns null.
     */
    public AppPortlet getPortlet();
}
