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

// import com.arsdigita.mimetypes.*;
//import com.arsdigita.domain.DomainObject;
import com.arsdigita.faq.ui.FaqQuestionsPortlet;
//import com.arsdigita.kernel.ACSObjectInstantiator;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.loader.PackageLoader;
//import com.arsdigita.persistence.DataObject;
//import com.arsdigita.portal.PortletType;
//import com.arsdigita.portal.apportlet.AppPortletSetup;
import com.arsdigita.portal.apportlet.AppPortletType;
import com.arsdigita.runtime.ScriptContext;
import com.arsdigita.web.ApplicationType;

import org.apache.log4j.Logger;

/**
 * FAQ Application Loader
 *
 * @author pboy &lt;pboy@barkhof.uni-bremen.de&gt;
 * @version $Id: Loader.java $
 */
public class Loader extends PackageLoader {


    /** Logger instance for debugging */
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

                loadFAQApplicationType();
                setupFaqQuestionsPortlet(null);

                setupDefaultFaq();

            }
        }.run();

        s_log.info("Done");
    }

    // ////////////////////////////////////////////////////////////////////////
    //
    //          S e t u p    o f   a p p l i c a t i o n   t y p e s
    //
    // ////////////////////////////////////////////////////////////////////////

    /**
     * Creates a FAQ application type, the domain class of the
     * FAQ package, as a legacy-compatible type of application.
     *
     * Creates an entry in table application_types and a corresponding entry in
     * apm_package_types
     *
     * TODO: migrate to a new style, legacy free application type.
     */
    private void loadFAQApplicationType() {

        ApplicationType type = ApplicationType.createApplicationType(
                                              "faq",
                                              "Frequently Asked Questions",
                                              Faq.BASE_DATA_OBJECT_TYPE);
        type.setDescription
            ("FAQ's empower users to share knowledge.");
        // Current code requires an apps specific dispatcher class. Has to be
        // modified to be able to create a legacy free app type.
        type.setDispatcherClass
                ("com.arsdigita.faq.FaqDispatcher");

    }



    // ////////////////////////////////////////////////////////////////////////
    //
    //        S e t u p    a   F A Q   a p p l i c a t i o n
    //
    // ////////////////////////////////////////////////////////////////////////
    private void setupDefaultFaq() {

    //  try {
    //      SiteNode sn = SiteNode.getSiteNode("/administration", false);
    //      if (!"administration".equals(sn.getName())) {
                Faq faq = Faq.create(
                        "faq", "Default FAQ", null);
                faq.save();
    //      }
    //  } catch (DataObjectNotFoundException e) {
    //      Assert.fail(e.getMessage());
    //  }
    }


    // ////////////////////////////////////////////////////////////////////////
    //
    //       S e t u p    o f   i n t e r n a l   p o r t l e t s
    //
    // ////////////////////////////////////////////////////////////////////////


    /**
     * Creates a PortletType (persistent object) for the RecentUpdatedDocs
     * Portlet.
     *
     * Instances (Portlets) are created by user interface or programmatically
     * by configuration.
     */
    private void setupFaqQuestionsPortlet(ApplicationType provider) {

        // Create the FAQ questions portlet
/*  Copied from docfrepo as an example
        AppPortletSetup setup = new AppPortletSetup(s_log);

        setup.setPortletObjectType(RecentUpdatedDocsPortlet.BASE_DATA_OBJECT_TYPE);
        setup.setTitle("Recently Updated Documents");
        setup.setDescription(
              "Displays the most recent documents in the document repository.");
        setup.setProfile(PortletType.WIDE_PROFILE);
        // setup.setProviderApplicationType(provider);
        setup.setProviderApplicationType(Repository.BASE_DATA_OBJECT_TYPE);
        setup.setInstantiator(new ACSObjectInstantiator() {
                @Override
                protected DomainObject doNewInstance(DataObject dataObject) {
                    return new RecentUpdatedDocsPortlet(dataObject);
                }
            });

        setup.run();
*/
        AppPortletType portletType = AppPortletType.createAppPortletType
            ("Faq Questions Portlet", AppPortletType.WIDE_PROFILE,
             FaqQuestionsPortlet.BASE_DATA_OBJECT_TYPE);
        portletType.setProviderApplicationType(Faq.BASE_DATA_OBJECT_TYPE);
        portletType.setPortalApplication(true);
        portletType.save();

    }



}
