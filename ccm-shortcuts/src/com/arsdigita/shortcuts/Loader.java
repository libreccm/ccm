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

package com.arsdigita.shortcuts;

import com.arsdigita.loader.PackageLoader;

import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;

import com.arsdigita.runtime.ScriptContext;

import com.arsdigita.web.ApplicationType;
import com.arsdigita.web.Application;

import org.apache.log4j.Logger;

/**
 * Executes nonrecurring at install time and loads (and configures ) the
 * shortcut application type and the administration instance.
 * Shortcuts works by a filter servlet activated in web.xml
 *
 * Loads the shortcuts application and type.
 *
 * @author Daniel Berrange
 * @version $Id: Loader.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class Loader extends PackageLoader {

    private static final Logger s_log = Logger.getLogger(Loader.class);

    /**
     * Run script invoked by com.arsdigita.packing loader script.
     *
     * @param ctx
     */
    public void run(final ScriptContext ctx) {
        new KernelExcursion() {
            public void excurse() {
                setEffectiveParty(Kernel.getSystemParty());
                
                // setup application
                setupShortcuts();
            }
        }.run();
    }

    /**
     * Creates a shortcuts as a legacy-free application type.
     *
     * No localization here because it is an invariant configuration.
     */
    private void setupShortcuts() {
        s_log.debug("Creating an application type for shortcuts. " +
                    "Base Data Object Type: " + Shortcuts.BASE_DATA_OBJECT_TYPE);

        /* Create legacy-campatible application type                          */
/*      ApplicationType type = ApplicationType
            .createApplicationType("shortcuts",
                                   "CCM Shortcuts Admin",
                                   Shortcuts.BASE_DATA_OBJECT_TYPE);          */
        /* Create legacy-free application type                               
         * NOTE: The wording in the title parameter of ApplicationType
         * determines the name of the subdirectory for the XSL stylesheets.
         * It gets "urlized", i.e. trimming leading and trailing blanks and
         * replacing blanks between words and illegal characters with an
         * hyphen and converted to lower case.
         * "Shortcuts" will become "shortcuts".                               */
        ApplicationType type = new ApplicationType( 
                                       "Shortcuts",
                                        Shortcuts.BASE_DATA_OBJECT_TYPE );
        type.setDescription("CCM Shortcuts Administration instance");

        Application admin = Application.retrieveApplicationForPath("/admin/");

        Application app = Application.createApplication(type,
                                                        "shortcuts",
                                                        "CCM Shortcuts Admin",
                                                        admin);
    }
}
