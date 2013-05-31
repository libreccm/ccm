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
package com.arsdigita.london.terms;

import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.loader.PackageLoader;
import com.arsdigita.runtime.ScriptContext;
import com.arsdigita.web.Application;
import com.arsdigita.web.ApplicationType;

/**
 * Terms Loader executes nonrecurring at install time and loads (installs and 
 * initializes) the ccm-ldn-terms module into database.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: Loader.java 1878 2009-04-21 13:56:23Z terry $
 */
public class Loader extends PackageLoader {
    public void run(final ScriptContext ctx) {
        new KernelExcursion() {
            public void excurse() {
                setEffectiveParty(Kernel.getSystemParty());
                setupApplication();
            }
        }.run();
    }

    /**
     * Creates a legacy free application type and a admin application instance.
     */
    public static void setupApplication() {

        // NOTE: The title "Navigation" is used to retrieve the application's
        // name to determine the location of xsl files (by url-izing it). So
        // DON'T modify it without synchronizing web directory tree accordingly!
        ApplicationType type =
                new ApplicationType( "Terms",
                                     Terms.BASE_DATA_OBJECT_TYPE );
        type.setSingleton(true);
        type.setDescription("CCM Terms administration");

        Application admin = Application.retrieveApplicationForPath("/admin/");

        Application.createApplication(type,
                                      "terms",
                                      "CCM Terms Admin",
                                      admin);
    }

}
