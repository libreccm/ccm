/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
 */

package com.arsdigita.navigation.tools;

import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.DomainObjectInstantiator;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.london.terms.Domain;
import com.arsdigita.util.cmd.Program;
import com.arsdigita.london.util.Transaction;
import com.arsdigita.navigation.Navigation;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.web.Application;

import org.apache.commons.cli.CommandLine;
import org.apache.log4j.Logger;

/**
 * Command line utility to create an application instance of Navigation. 
 * 
 * Usually Loader creates a (default) application instance. 
 * 
 */
public class NavigationCreator extends Program {

    /** A logger instance to assist debugging.  */
    private static final Logger s_log = Logger.getLogger(NavigationCreator.class);

    /**
     * Default Constructor
     */
    public NavigationCreator() {
        super("Add Navigation instance", "1.0.0", "URL-FRAGMENT TITLE DOMAIN-KEY");
    }

    /**
     * 
     * @param navURL
     * @param navTitle
     * @param defaultDomain 
     */
    private void createNavigation(String navURL, 
                                  String navTitle, 
                                  String defaultDomain) {
        
        if (!Application.isInstalled(Navigation.BASE_DATA_OBJECT_TYPE, 
                                     "/"+navURL+"/")) {
            
            DomainObjectFactory.registerInstantiator(
                    Navigation.BASE_DATA_OBJECT_TYPE, new DomainObjectInstantiator() {
                        public DomainObject doNewInstance(DataObject dataObject) {
                            return new Navigation(dataObject);
                        }
                    });
                    /* Create Instance beyond root (4. parameter null)       */
                    Application app = Application.createApplication(
                    Navigation.BASE_DATA_OBJECT_TYPE, navURL, navTitle, null);
            app.save();
            Domain domain = Domain.retrieve(defaultDomain);
            domain.setAsRootForObject(app, null);

        } else {

            System.err.println(Navigation.BASE_DATA_OBJECT_TYPE
                    + " already installed at " + navURL);
            System.exit(1);

        }

    }

    /**
     * 
     * @param cmdLine 
     */
    protected void doRun(final CommandLine cmdLine) {
        new Transaction() {
            public void doRun() {
                new KernelExcursion() {
                    public void excurse() {
                        setEffectiveParty(Kernel.getSystemParty());
                        String[] args = cmdLine.getArgs();
                        if (args.length == 3) {
                            String navURL = args[0];
                            String navTitle = args[1];
                            String domainKey = args[2];
                            if (navURL != null && navURL.length() != 0
                                    && navTitle != null && navTitle.length() != 0
                                    && domainKey != null && domainKey.length() != 0) {
                                createNavigation(navURL, navTitle, domainKey);
                            } else {
                                help(System.err);
                                System.exit(1);
                            }
                        } else {
                            help(System.err);
                            System.exit(1);
                        }
                    }
                }.run();
            }
        }.run();
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        new NavigationCreator().run(args);
    }

}