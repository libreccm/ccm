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
package com.arsdigita.portalserver.admin;

import com.arsdigita.initializer.Configuration;
import org.apache.log4j.Logger;

import com.arsdigita.persistence.*;
import com.arsdigita.domain.*;
import com.arsdigita.kernel.*;
import com.arsdigita.portalserver.*;
import com.arsdigita.web.*;

/**
 * Initializer
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #9 $ $Date: 2004/08/17 $
 **/

public class Initializer implements com.arsdigita.initializer.Initializer {

    public final static String versionId = "$Id: //portalserver/dev/src/com/arsdigita/portalserver/admin/Initializer.java#9 $ by $Author: dennis $, $DateTime: 2004/08/17 23:19:25 $";

    private static Logger s_log = Logger.getLogger(Initializer.class);

    private Configuration m_conf = new Configuration();

    public Configuration getConfiguration() {
        return m_conf;
    }

    public void startup() {
        s_log.info("Initializing PortalServer Admin...");

        TransactionContext txn =
            SessionManager.getSession().getTransactionContext();

        txn.beginTxn();

        ApplicationSetup setup = new ApplicationSetup(s_log);

        setup.setApplicationObjectType(PSAdmin.BASE_DATA_OBJECT_TYPE);
        setup.setTitle("Portal Server Site Administration");
        setup.setDescription("Displays common administration tasks.");
        setup.setPortalApplication(false);
        setup.setKey("portal-admin");
        // db based stylesheets no longer used.
        // setup.setStylesheet("/packages/portalserver/xsl/portalserver.xsl");
        setup.setDispatcherClass("com.arsdigita.portalserver.admin.ui.Dispatcher");
        setup.setInstantiator(new ACSObjectInstantiator() {
                protected DomainObject doNewInstance(DataObject dataObject) {
                    return new PSAdmin(dataObject);
                }
            });
        setupAdminInstance(setup.run());

        txn.commitTxn();

        s_log.info("Done initializing PortalServer Admin.");
    }

    private void setupAdminInstance(final ApplicationType type) {

        if (!Application.isInstalled(PSAdmin.BASE_DATA_OBJECT_TYPE,
                                     "/portal-admin/")) {
            s_log.info("There is no Portal Admin application instance on " +
                       "/portal-admin/.  Installing now.");

            KernelExcursion ex = new KernelExcursion() {
                protected void excurse() {
                    setParty(Kernel.getSystemParty());
                    Application app = Application.createApplication
                        (type, "portal-admin", "Site Administration", null);

                    app.save();
                }
            };
            ex.run();

            s_log.info("Done installing Portal Admin on /portal-admin/.");
        }
    }

    public void shutdown() {
        // Do nothing.
    }

}
