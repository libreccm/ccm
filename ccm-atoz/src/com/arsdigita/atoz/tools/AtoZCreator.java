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

package com.arsdigita.atoz.tools;

import com.arsdigita.atoz.AtoZ;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.DomainObjectInstantiator;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.london.util.Transaction;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.util.cmd.Program;
import com.arsdigita.web.Application;

import org.apache.commons.cli.CommandLine;
import org.apache.log4j.Logger;


/**
 * Command line utility to create an application instance of AtoZ. 
 * 
 * Usually Loader creates a (default) application instance. 
 * 
 */
public class AtoZCreator extends Program {

    /** A logger instance to assist debugging.  */
    private static final Logger s_log = Logger.getLogger(AtoZCreator.class);

    /**
     * Default Constructor
     */
    public AtoZCreator() {
        super("Add AtoZ instance", "1.0.0", "URL-FRAGMENT TITLE");
    }

    private void addAtoZ(String atozURL, String atozTitle) {

        if (!Application.isInstalled(AtoZ.BASE_DATA_OBJECT_TYPE, "/"+atozURL+"/")) {

            DomainObjectFactory.registerInstantiator(
                    AtoZ.BASE_DATA_OBJECT_TYPE, new DomainObjectInstantiator() {
                        public DomainObject doNewInstance(DataObject dataObject) {
                            return new AtoZ(dataObject);
                        }
                    });
            Application app = Application.createApplication(
                    AtoZ.BASE_DATA_OBJECT_TYPE, atozURL, atozTitle, null);
            app.save();

        } else {

            System.err.println(AtoZ.BASE_DATA_OBJECT_TYPE
                    + " already installed at " + atozURL);
            System.exit(1);

        }
    }

    protected void doRun(final CommandLine cmdLine) {
        new Transaction() {
            public void doRun() {
                new KernelExcursion() {
                    public void excurse() {
                        setEffectiveParty(Kernel.getSystemParty());

                        String[] args = cmdLine.getArgs();
                        if (args.length == 2) {
                            String atozURL = args[0];
                            String atozTitle = args[1];
                            if (atozURL != null && atozURL.length() != 0
                                    && atozTitle != null && atozTitle.length() != 0) {
                                addAtoZ(atozURL, atozTitle);
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
        new AtoZCreator().run(args);
    }

}
