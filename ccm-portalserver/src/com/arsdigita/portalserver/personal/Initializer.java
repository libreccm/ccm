/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.portalserver.personal;

import org.apache.log4j.Category;
import com.arsdigita.web.ApplicationType;
import com.arsdigita.web.Application;
import com.arsdigita.web.ApplicationSetup;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.TransactionContext;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.kernel.ACSObjectInstantiator;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.initializer.Configuration;
import com.arsdigita.initializer.InitializationException;
import com.arsdigita.portal.PortletType;
import com.arsdigita.portal.apportlet.AppPortletSetup;

/**
 * <p><strong>Experimental</strong></p>
 *
 * @author <a href="mailto:justin@arsdigita.com">Justin Ross</a>
 * @version $Id: //portalserver/dev/src/com/arsdigita/portalserver/personal/Initializer.java#12 $
 */
public class Initializer implements com.arsdigita.initializer.Initializer {
    public static final String versionId =
        "$Id: //portalserver/dev/src/com/arsdigita/portalserver/personal/Initializer.java#12 $" +
        "$Author: dennis $" +
        "$DateTime: 2004/08/17 23:19:25 $";

    private static Category s_log = Category.getInstance
        (Initializer.class);

    private Configuration m_conf = new Configuration();

    public Initializer() throws InitializationException {
        /* Empty */
    }

    public Configuration getConfiguration() {
        return m_conf;
    }

    public void startup() {
        s_log.info("Initializing Personal Portal...");

        TransactionContext txn =
            SessionManager.getSession().getTransactionContext();

        txn.beginTxn();

        ApplicationType creatorAppType =
            setupPersonalPortalCreatorApplication();

        // Create an instance of the personal portal creator at
        // /personal-portal/.

        boolean creatorInstalled = Application.isInstalled
            (PersonalPortalCreator.BASE_DATA_OBJECT_TYPE,
             "/personal-portal/");

        if (!creatorInstalled) {

            KernelExcursion ex = new KernelExcursion() {
                protected void excurse() {
                    setParty(Kernel.getSystemParty());
                    Application creatorApp = Application.createApplication
                        (PersonalPortalCreator.BASE_DATA_OBJECT_TYPE,
                         "personal-portal", "Personal Portal Creator", null);
                    creatorApp.save();
                }
            };
            ex.run();


        }

        ApplicationType pwAppType = setupPersonalPortalApplication();

        setupMyPortalsPortlet();

        txn.commitTxn();

        s_log.info("Done initializing Personal Portal.");
    }

    private ApplicationType setupPersonalPortalCreatorApplication() {
        ApplicationSetup setup = new ApplicationSetup(s_log);

        setup.setApplicationObjectType
            (PersonalPortalCreator.BASE_DATA_OBJECT_TYPE);
        setup.setKey("personal-portal-creator");
        setup.setTitle("Personal Portal Creator");
        setup.setDescription("Responsible for creating personal portals.");
        // db based stylesheets nolonger used
        // setup.setStylesheet("/packages/portalserver/xsl/portalserver.xsl");
        setup.setDispatcherClass
            ("com.arsdigita.portalserver.personal.PersonalPortalCreatorDispatcher");
        setup.setPortalApplication(false);
        setup.setInstantiator(new ACSObjectInstantiator() {
                protected DomainObject doNewInstance(DataObject dataObject) {
                    return new PersonalPortalCreator(dataObject);
                }
            });

        return setup.run();
    }

    private ApplicationType setupPersonalPortalApplication() {
        ApplicationSetup setup = new ApplicationSetup(s_log);

        setup.setApplicationObjectType(PersonalPortal.BASE_DATA_OBJECT_TYPE);
        setup.setKey("personal-portal");
        setup.setTitle("Personal Portal");
        setup.setDescription("A portal for an individual.");
        // setup.setStylesheet("/packages/portalserver/xsl/portalserver.xsl");
        setup.setDispatcherClass
            ("com.arsdigita.portalserver.personal.PersonalPortalDispatcher");
        setup.setPortalApplication(false);
        setup.setInstantiator(new ACSObjectInstantiator() {
                protected DomainObject doNewInstance(DataObject dataObject) {
                    return new PersonalPortal(dataObject);
                }
            });

        ApplicationType type = setup.run();

        // XXX This must go after setup.run().  Need more elegant approach.
        new PersonalPortalConfig();

        return type;
    }

    private void setupMyPortalsPortlet() {
        AppPortletSetup setup = new AppPortletSetup(s_log);

        setup.setPortletObjectType
            (MyPortalsPortlet.BASE_DATA_OBJECT_TYPE);
        setup.setTitle("My Portals");
        setup.setDescription
            ("Displays links to portals to which you belong.");
        setup.setProfile(PortletType.NARROW_PROFILE);
        setup.setPortalApplication(true);
        setup.setInstantiator(new ACSObjectInstantiator() {
                public DomainObject doNewInstance(DataObject dataObject) {
                    return new MyPortalsPortlet(dataObject);
                }
            });

        setup.run();
    }

    public void shutdown() {
        /* Empty */
    }
}
