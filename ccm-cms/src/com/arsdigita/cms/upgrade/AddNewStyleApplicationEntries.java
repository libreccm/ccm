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
package com.arsdigita.cms.upgrade;

import com.arsdigita.cms.Loader;
import com.arsdigita.kernel.Group;
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
import com.arsdigita.web.ApplicationType;

import org.apache.commons.cli.CommandLine;
import org.apache.log4j.Logger;

/**
 * Update from CCM version 6.6.1 to 6.6.2 where loader has been refactored to
 * use (new style) applications in package com.arsdigita.weg instead of old
 * style applications using com.arsdigita.kermel.[Package* & SiteNode].
 *
 * Affected are the packages  CMS Workspace and Service. They are now loaded
 * using new style application classes Workspace and Service.
 *
 * The task at hand is to add the necessary table entries for CMS Workspace and
 * Service to the tables application_types and applications using the
 * information already existing (by old style initialization) in
 * apm_package_types, site_nodes, and apm_packages.
 *
 * @author pb
 */
public class AddNewStyleApplicationEntries extends Program {

    private static Logger s_log = Logger.getLogger(CreateGenericContentTypes.class);

    /**
    /* Constructor
     */
    public AddNewStyleApplicationEntries() {
        super("AddNewStyleApplicationEntries", "1.0.0", "");
    }


    /**
     * The mandatory main method
     * @param args
     */
    public static void main(final String[] args) {
		new AddNewStyleApplicationEntries().run(args);
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

                //  experimental
                ApplicationType appType = null;
                appType = Loader.loadWorkspaceApplicationType();

                // get corresponding package type
                PackageType packageType = appType.getPackageType();
                // get all installed instances
                PackageInstanceCollection allPackages = packageType
                                                        .getInstances();
                PackageInstance aPackage = null ;
                Resource res = null;
                while ( allPackages.next() ) {
                    aPackage = allPackages.getPackageInstance();
                    res = Resource.createResource((ResourceType)appType,
                                                  aPackage.getDisplayName(),
                                                  null);
                }


                appType = null;
                appType = Loader.loadServiceApplicationType();


                tc.commitTxn();
            }
        }.run();
    }

}
