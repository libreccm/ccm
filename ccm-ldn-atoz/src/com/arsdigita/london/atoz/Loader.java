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

package com.arsdigita.london.atoz;

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
 * Loader.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: Loader.java 758 2005-09-02 14:26:56Z sskracic $
 */
public class Loader extends PackageLoader {
    public final static String versionId =
        "$Id: Loader.java 758 2005-09-02 14:26:56Z sskracic $" +
        "$Author: sskracic $" +
        "$DateTime: 2003/10/28 14:21:31 $";

    private static final Logger s_log = Logger.getLogger(Loader.class);

    public void run(final ScriptContext ctx) {
        new KernelExcursion() {
            public void excurse() {
                setEffectiveParty(Kernel.getSystemParty());
                setupAtoZ();
            }
        }.run();
    }


    private void setupAtoZ() {
        ApplicationSetup setup = new ApplicationSetup(s_log);

        setup.setApplicationObjectType(AtoZ.BASE_DATA_OBJECT_TYPE);
        setup.setKey("atoz");
        setup.setTitle("A-Z");
        setup.setDescription("A-Z of content");
        setup.setSingleton(true);
        setup.setInstantiator(new ACSObjectInstantiator() {
                public DomainObject doNewInstance(DataObject dataObject) {
                    return new AtoZ(dataObject);
                }
            });
        ApplicationType type = setup.run();
        type.save();

        if (!Application.isInstalled(AtoZ.BASE_DATA_OBJECT_TYPE,
                                     "/atoz/")) {
            Application app =
                Application.createApplication(type,
                                              "atoz",
                                              "AtoZ",
                                              null);
            app.save();
        }
    }
}
