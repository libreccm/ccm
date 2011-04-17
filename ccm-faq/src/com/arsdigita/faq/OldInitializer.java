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
package com.arsdigita.faq;

import com.arsdigita.faq.ui.FaqQuestionsPortlet;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.DomainObjectInstantiator;
import com.arsdigita.initializer.Configuration;
import com.arsdigita.initializer.InitializationException;
import com.arsdigita.kernel.*;
import com.arsdigita.persistence.*;
import com.arsdigita.web.Application;
import com.arsdigita.web.ApplicationType;
import com.arsdigita.portal.apportlet.AppPortletType;
import com.arsdigita.dispatcher.ObjectNotFoundException;

import org.apache.log4j.Logger;


/**
 * OldInitializer
 *
 * Initializes the faq package.
 *
 *
 * @author <a href="mailto:teadams@arsdigita.com">Tracy Adams</a>
 * @version $Revision: #8 $ $Date: 2004/08/17 $
 */

public class OldInitializer

    implements com.arsdigita.initializer.Initializer {

    private Configuration m_conf = new Configuration();
    public static final String versionId = "$Id: //apps/faq/dev/src/com/arsdigita/faq/Initializer.java#8 $ by $Author: dennis $, $DateTime: 2004/08/17 23:26:27 $";

    private static Logger s_log =
        Logger.getLogger(OldInitializer.class);

    public OldInitializer() throws InitializationException {

    }

    /**
     * Returns the configuration object used by this initializer.
     **/

    public Configuration getConfiguration() {
        return m_conf;
    }


    /**
     * Called on startup. Note. As you can not find a call
     * to this method in enterprise.ini, this method
     * may appear to execute mysteriously.
     * However, the process that runs through enterprise.ini
     * automitically calls the startup() method of any
     * class that implements com.arsdigita.util.initializer.OldInitializer
     * present in enterprise.ini
     *
     **/


    public void startup() {

        s_log.info("Faq Initializer starting.");


        TransactionContext txn = SessionManager.getSession()
            .getTransactionContext();
        txn.beginTxn();

        // Register Faq domain object

        DomainObjectInstantiator instantiator;

        instantiator = new ACSObjectInstantiator() {
                protected DomainObject doNewInstance(DataObject dataObject) {
                    return new Faq(dataObject);
                }
            };

        DomainObjectFactory.registerInstantiator
            (Faq.BASE_DATA_OBJECT_TYPE, instantiator);

        checkFaqSetup();


        // Register the portlets
        instantiator = new ACSObjectInstantiator() {
                protected DomainObject doNewInstance(DataObject dataObject) {
                    return new FaqQuestionsPortlet(dataObject);
                }
            };

        DomainObjectFactory.registerInstantiator
            (FaqQuestionsPortlet.BASE_DATA_OBJECT_TYPE, instantiator);

        txn.commitTxn();

        URLFinder faqFinder = new URLFinder() {
                public String find(OID oid, String context) throws NoValidURLException {
		    return find(oid);
		}
                public String find(OID oid) throws NoValidURLException {
                    QAPair pair;
                    try {
                        pair = (QAPair) DomainObjectFactory.newInstance(oid);
                    } catch (DataObjectNotFoundException e) {
                        throw new ObjectNotFoundException("No such FAQ item: " + oid + " .may have been deleted.");
                    }

                    String url = pair.getFaq().getPrimaryURL() + "#" + pair.getID();
                    return url;

                }
            };

        URLService.registerFinder(QAPair.BASE_DATA_OBJECT_TYPE, faqFinder);

        s_log.debug("Faq Initializer done.");
    }

    private void checkFaqSetup() {
        /* This checks to see if a package by this name
         * is present.  If it isn't, setupFaq
         * will do the necessary setup such as add the
         * package type, package instance, site node
         * and style sheet.
         */
        try {
            s_log.debug("Faq Initializer - verifying setup.");
            PackageType FaqType = PackageType.findByKey("faq");
        } catch (DataObjectNotFoundException e) {
            setupFaq();
        }
    }


    private void setupFaq() {
        s_log.info("Faq Initializer - setting up new package");

        /** Adding the package type to the installation
         */

        PackageType FaqType = PackageType.create(
                                                 "faq", "FAQ ", "FAQ s",
                                                 "http://arsdigita.com/faq");
        s_log.debug("Just added package type FAQ ");

        /** Adding a style sheet
         */

  //    Stylesheet FaqSheet =
  //        Stylesheet.createStylesheet("/packages/faq/xsl/faq.xsl");
  //    FaqType.addStylesheet(FaqSheet);


        /** Mapping the package type to a dispatcher
         *  class
         */

        FaqType.setDispatcherClass("com.arsdigita.faq.FaqDispatcher");

        /** Saving changes
         */

        FaqType.save();

        final ApplicationType faqAppType = ApplicationType.createApplicationType
            (FaqType, "FAQ Application", Faq.BASE_DATA_OBJECT_TYPE);
        faqAppType.save();

        KernelExcursion ex = new KernelExcursion() {
            protected void excurse() {
                setParty(Kernel.getSystemParty());
                Application faqApp = Application.createApplication
                    (faqAppType, "faq", "FAQ", null);
                faqApp.save();
            }
        };
        ex.run();


        // register the faq portlet
        AppPortletType portletType = AppPortletType.createAppPortletType
            ("Faq Questions Portlet", AppPortletType.WIDE_PROFILE,
             FaqQuestionsPortlet.BASE_DATA_OBJECT_TYPE);
        portletType.setProviderApplicationType(faqAppType);
        portletType.setPortalApplication(true);
        portletType.save();

    }

    /**
     * Called on shutdown. It's probably not a good idea to depend on this
     * being called.
     **/

    public void shutdown() {
    }

}
