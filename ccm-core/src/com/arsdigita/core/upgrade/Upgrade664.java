/*
 * Copyright (C) 2011 Peter Boy <pb@zes.uni-bremen.de>. All Rights Reserved.
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

package com.arsdigita.core.upgrade;

import com.arsdigita.loader.CoreLoader;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.kernel.PackageInstance;
import com.arsdigita.kernel.PackageInstanceCollection;
import com.arsdigita.kernel.PackageType;
import com.arsdigita.kernel.Resource;
import com.arsdigita.kernel.ResourceType;
import com.arsdigita.packaging.Program;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TransactionContext;
import com.arsdigita.util.StringUtils;
import com.arsdigita.web.Application;
import com.arsdigita.web.ApplicationType;

import org.apache.commons.cli.CommandLine;
import org.apache.log4j.Logger;

/**
 * Update ccm-core from version 6.6.3 to 6.6.4 where CoreLoader has been 
 * refactored to use (new style) applications in package
 *     com.arsdigita.webdeveloper 
 * instead of old style package type / sitenode applications based on 
 * com.arsdigita.kermel.[Package* & SiteNode].
 *
 * Refactored core packages are now loaded using new style application classes.
 *
 * The task at hand is to add the necessary table entries to the tables 
 * application_types and applications using the information already existing 
 * (by old style initialization) in apm_package_types, site_nodes, and apm_packages.
 *
 * @author pb
 */
public class Upgrade664 extends Program {
  
    private static Logger s_log = Logger.getLogger(Upgrade664.class);

    /**
    /* Constructor constructs a program object which initializes the CCM
     * runtime system and enables concatenation, so a following SQL script
     * may be executed.
     */
    public Upgrade664() {
        super("Upgrade664", "1.0.0", "",true,true);
    }


    /**
     * The mandatory main method
     * @param args
     */
    public static void main(final String[] args) {
		new Upgrade664().run(args);
	}


    /**
     * Worker method. Adds new style application entries.
     * 
     * @param cmdLine
     */
    public void doRun(CommandLine cmdLine) {

        new KernelExcursion() {
            public void excurse() {
                setEffectiveParty(Kernel.getSystemParty());
                final Session session = SessionManager.getSession();
                final TransactionContext tc = session.getTransactionContext();
                tc.beginTxn();


                //  Update core WebDeveloperSupport
                //  Create a (new type, legacy free) web.ApplicationType type 
                //  application
                CoreLoader.loadWebDev();


                //  Update core permission support
                //  Create a (new type, legacy free) web.ApplicationType type 
                //  application
                CoreLoader.loadPermissionsApp();


                tc.commitTxn();
            }
        }.run();
        
    }
  
}