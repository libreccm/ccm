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

package com.arsdigita.subsite;

import com.arsdigita.loader.PackageLoader;

import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;

import com.arsdigita.runtime.ScriptContext;

import com.arsdigita.web.ApplicationType;
import com.arsdigita.web.Application;

import org.apache.log4j.Logger;

/**
 * Executes nonrecurring at install time and loads (installs and initializes)
 * the ccm-subsite module persistently into database.
 *
 * @author Daniel Berrange
 * @author Peter Boy &lt;pboy@barkhof.uni-bremen.de&gt;
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
                
                createApplication();
            }
        }.run();
    }

    /**
     * Creates ccm-subsite as a legacy-free application type and initializes
     * an administration instance. Subsite itself is a filter servlet activated
     * in web applications web.xml.
     */
    private void createApplication() {
        
        /* Create new application type, legacy free application type 
         * 
         * NOTE: The wording in the title parameter of ApplicationType (first
         * parameter) determines the name of the subdirectory for the 
         * XSL stylesheets. It gets "urlized", i.e. trimming leading and 
         * trailing blanks and replacing blanks between words and illegal 
         * characters with an hyphen and converted to lower case.
         * Example: "Subsite" will become "subsite".
         */
        ApplicationType type = new ApplicationType
                                       ("Subsite",
                                        Subsite.BASE_DATA_OBJECT_TYPE);
        type.setDescription("CCM subsite administration");

        Application admin = Application.retrieveApplicationForPath("/admin/");

        /* Create an application instance as a legacy free app.
         * Whether a legacy compatible or a legacy free application is
         * created depends on the type of ApplicationType above. No need to
         * modify anything here                                               */
        Application app = Application
                          .createApplication(type,
                                             "subsite",
                                             "Subsite Administration",
                                             admin);
        app.setDescription("CCM subsite administration GUI");
    }
}
