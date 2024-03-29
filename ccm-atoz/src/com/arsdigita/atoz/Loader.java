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
package com.arsdigita.atoz;

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
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: Loader.java 758 2005-09-02 14:26:56Z sskracic $
 */
public class Loader extends PackageLoader {

    private static final Logger s_log = Logger.getLogger(Loader.class);

    @Override
    public void run(final ScriptContext ctx) {
        new KernelExcursion() {
            @Override
            public void excurse() {
                setEffectiveParty(Kernel.getSystemParty());

                setupAtoZ();
            }

        }.run();
    }

    private void setupAtoZ() {
        s_log.debug("Creating AtoZ application...");

//  The old ApplicationSetup code is retained here as an example for
//  developers how to migrate existing legacy code. See release notes 2.0
//  Should be removed in subsequent releases.

        /*
         ApplicationSetup setup = new ApplicationSetup(s_log);

         setup.setApplicationObjectType(AtoZ.BASE_DATA_OBJECT_TYPE);
         setup.setKey("atoz");
         setup.setTitle("A-Z");
         setup.setDescription("A-Z of content");
         setup.setSingleton(true);
         // setInstantiator is a task of the initalizer now!
         setup.setInstantiator(new ACSObjectInstantiator() {
         public DomainObject doNewInstance(DataObject dataObject) {
         return new AtoZ(dataObject);
         }
         });
         ApplicationType type = setup.run();
         type.save();
         */

        /* Create new type legacy free application type                 
         * NOTE: The wording in the title parameter of ApplicationType
         * determines the name of the subdirectory for the XSL stylesheets.
         * It gets "urlized", i.e. trimming leading and trailing blanks and
         * replacing blanks between words and illegal characters with an
         * hyphen and converted to lower case.
         * "AtoZ" will become "atoz".                   */
        final ApplicationType type = new ApplicationType("AtoZ",
                                                         AtoZ.BASE_DATA_OBJECT_TYPE);
        type.setDescription("A-Z of content.");
        type.setSingleton(false);
        type.save();

        if (!Application.isInstalled(AtoZ.BASE_DATA_OBJECT_TYPE,
                                     "/atoz/")) {
            final Application app = Application.createApplication(type,
                                                                  "atoz",
                                                                  "AtoZ",
                                                                  null);
            app.save();
        }
        s_log.debug("AtoZ application type created.");
    }

}
