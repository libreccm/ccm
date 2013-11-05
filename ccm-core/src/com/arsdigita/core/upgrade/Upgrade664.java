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

import com.arsdigita.core.Loader;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.util.cmd.Program;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TransactionContext;
import com.arsdigita.ui.admin.Admin;
import com.arsdigita.ui.login.Login;
import com.arsdigita.ui.permissions.Permissions;
import com.arsdigita.webdevsupport.WebDevSupport;
import java.sql.Connection;
import java.sql.SQLException;

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
        super("Upgrade664", "1.0.0", "", true, true);
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


                if (!appAlreadyInstalled(Login.BASE_DATA_OBJECT_TYPE, session)) {
                    //  Update core Login application (if not already installed)
                    //  Previously login had been managed by a (virtual) root 
                    //  sitenode with login dispatcher associated.
                    //  Login application is newly created, old sitenote deactivated.
                    Loader.loadLoginApp();
                }

                if (!appAlreadyInstalled(Admin.BASE_DATA_OBJECT_TYPE, session)) {
                    //  Update core Admin application (if not already installed)
                    //  Old style package type already removed by sql script.
                    //  Create a (new type, legacy free) web.ApplicationType type 
                    //  application
                    Loader.loadAdminApp();
                }


                if (!appAlreadyInstalled(Permissions.BASE_DATA_OBJECT_TYPE, session)) {
                    //  Update core permission support (if not already installed)
                    //  Old style package type already removed by sql script.
                    //  Create a (new type, legacy free) web.ApplicationType type 
                    //  application
                    Loader.loadPermissionsApp();
                }


                if (!appAlreadyInstalled(WebDevSupport.BASE_DATA_OBJECT_TYPE, session)) {
                    //  Update core WebDeveloperSupport
                    //  Old style package type already removed by sql script.
                    //  Create a (new type, legacy free) web.ApplicationType type 
                    //  application
                    Loader.loadWebDev();
                }


                // Note: Old PackageType sitenode removed. It's useless now
                // because it is based on SiteNode / PackageType which is
                // empty when all applications are migrated to new style
                // legacy free applications.
                // SQL script removes its table entries.


                // Note 2: SQL script part of this update removes bebop
                // PackageType. It had never been used and not instantiated.
                // So no replacement is needed.
                // SQL script removes its table entries.

                tc.commitTxn();
            }

        }.run();

    }

    final boolean appAlreadyInstalled(final String type, final Session session) {
        final DataCollection appTypes = session.retrieve(type);
        final boolean result = !appTypes.isEmpty();
        appTypes.close();

        return result;
    }

}
