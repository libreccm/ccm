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

package com.arsdigita.themedirector;


import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.loader.PackageLoader;
import com.arsdigita.runtime.ScriptContext;
import com.arsdigita.web.Application;
import com.arsdigita.web.ApplicationType;

import org.apache.log4j.Logger;

/**
 * Loader.
 *
 * @author Randy Graebner &lt;randyg@redhat.com&gt;
 * @author Peter Boy &lt;pboy@barkhof.uni-bremen.de&gt;
 * @version $Id: Loader.java 2004 2009-10-03 22:23:08Z pboy $
 */
public class Loader extends PackageLoader implements ThemeDirectorConstants {

    private static final Logger s_log = Logger.getLogger(Loader.class);

    @Override
    public void run(final ScriptContext ctx) {
        new KernelExcursion() {
            @Override
            public void excurse() {
                setEffectiveParty(Kernel.getSystemParty());
                
                setupThemeDirector();
            }
        }.run();
    }


    /** 
     * Creates theme director as a legacy-free application type.
     * 
     * NOTE: The wording in the title parameter of ApplicationType determines
     * the name of the subdirectory for the XSL stylesheets.
     * It gets "urlized", i.e. trimming leading and trailing blanks and replacing
     * blanks between words and illegal characters with an hyphen and converted
     * to lower case.
     * Example: "Theme Director" will become "theme-director".
     *
     * Creates an entry in table application_types 
     */
    private void setupThemeDirector() {

        ApplicationType type =
                new ApplicationType(  "Theme Director",
                                      ThemeDirector.BASE_DATA_OBJECT_TYPE );
        type.setSingleton(true);
        type.setDescription("CCM themes administration");

        Application admin = Application.retrieveApplicationForPath("/admin/");

        // create application instance as a legacy free app.
        // Whether a legacy compatible or a legacy free application is
        // created depends on the type of ApplicationType above. No need to
        // modify anything here
        Application app =
                Application.createApplication(type,
                                              "themes",
                                              "CCM Themes Administration",
                                              admin);

        app.setDescription("CCM themes administration");
    }
}
