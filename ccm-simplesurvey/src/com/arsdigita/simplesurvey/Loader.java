/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.simplesurvey;

import com.arsdigita.domain.DomainObject;
import com.arsdigita.kernel.ACSObjectInstantiator;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.loader.PackageLoader;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.runtime.ScriptContext;
import com.arsdigita.web.Application;
import com.arsdigita.web.ApplicationSetup;
import com.arsdigita.web.ApplicationType;
import org.apache.log4j.Logger;

/**
 * Initial load (non-resurring) at install time for ccm-simplesurvey. Creates
 * application type in database.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: Loader.java 759 2005-09-02 15:25:32Z sskracic $
 */
public class Loader extends PackageLoader {

    private static final Logger s_log = Logger.getLogger(Loader.class);

    /** 
     * 
     * @param ctx
     */
    public void run(final ScriptContext ctx) {
        new KernelExcursion() {
            public void excurse() {
                setEffectiveParty(Kernel.getSystemParty());
                setupSimpleSurveyPackage();
            }
        }.run();
    }

    /**
     * Helper method which does the actual work of preparing an application
     * type and stores it in the database.
     */
    private void setupSimpleSurveyPackage() {

        ApplicationSetup setup = new ApplicationSetup(s_log);
        
        setup.setApplicationObjectType(SimpleSurvey.BASE_DATA_OBJECT_TYPE);
        setup.setKey("simplesurvey");
        setup.setTitle("Simple Survey");
        // setup.setDescription("Simple Survey");
        setup.setDescription("A simple survey application.");
        setup.setInstantiator(new ACSObjectInstantiator() {
                public DomainObject doNewInstance(DataObject dataObject) {
                    return new SimpleSurvey(dataObject);
                }
            });
        ApplicationType type = setup.run();
        type.save();
        
        if (!Application.isInstalled(SimpleSurvey.BASE_DATA_OBJECT_TYPE,
                                     "/simplesurvey/")) {
            Application app =
                Application.createApplication(type,
                                              "simplesurvey",
                                              "Simplesurvey",
                                              null);
            app.save();
        }
    }
}
