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

package com.arsdigita.london.search;

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
 * <p>Executes nonrecurring at install time and loads (installs and initializes)
 * the Remote Search module persistently into database.</p>
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: Loader.java 758 2005-09-02 14:26:56Z sskracic $
 */
public class Loader extends PackageLoader {

    /** Creates a s_logging category with name = full name of class */
    private static final Logger s_log = Logger.getLogger(Loader.class);

    /**
     * 
     * @param ctx 
     */
    public void run(final ScriptContext ctx) {
        new KernelExcursion() {
            public void excurse() {
                setEffectiveParty(Kernel.getSystemParty());
                setupSearch();
            }
        }.run();
    }


    /**
     * Create the Search application type and setup the default application
     * instance.
     */
    private void setupSearch() {
/*
        ApplicationSetup setup = new ApplicationSetup(s_log);

        setup.setApplicationObjectType(Search.BASE_DATA_OBJECT_TYPE);
        setup.setKey("search");
        setup.setTitle("Search");
        setup.setDescription("Public search");
        setup.setSingleton(true);
        setup.setInstantiator(new ACSObjectInstantiator() {
                public DomainObject doNewInstance(DataObject dataObject) {
                    return new Search(dataObject);
                }
            });
        ApplicationType type = setup.run();
        type.save();
*/

        /* Try: legacy free */
        ApplicationType type = new  ApplicationType("Search",
                                                    Search.BASE_DATA_OBJECT_TYPE );

        type.setDescription("Public search");
        type.save();
        
        
        if (!Application.isInstalled(Search.BASE_DATA_OBJECT_TYPE,
                                     "/search/")) {
            Application app = Application.createApplication(type,
                                                            "search",
                                                            "Search",
                                                            null);
            app.save();
        }
    }
}
