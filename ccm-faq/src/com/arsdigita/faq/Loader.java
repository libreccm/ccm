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
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.loader.PackageLoader;
import com.arsdigita.portal.apportlet.AppPortletType;
import com.arsdigita.runtime.ScriptContext;
import com.arsdigita.web.Application;
import com.arsdigita.web.ApplicationType;

import org.apache.log4j.Logger;

/**
 * FAQ Application Loader executes nonrecurring at install time and loads 
 * (installs and initializes) the FAQ application type, portlet type and
 * default application instance persistently into database.
 *
 * @author pboy &lt;pboy@barkhof.uni-bremen.de&gt;
 * @version $Id: Loader.java $
 */
public class Loader extends PackageLoader {


    /** Creates a s_logging category with name = full name of class */
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
                loadFaqQuestionsPortletType(null);

                setupDefaultFaqInstance();

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
     * FAQ package.
     */
    private void loadFAQApplicationType() {

        /* Setup as new stype legacy free aplcation */
        // NOTE: The title "FAQ" is used to retrieve the application's
        // name to determine the location of xsl files (by url-izing it). So
        // DON'T modify it without synchronizing web directory tree accordingly!
        ApplicationType type = new ApplicationType("FAQ",
                                                   Faq.BASE_DATA_OBJECT_TYPE);
        type.setDescription
            ("Frequently Asked Questions empower users to share knowledge.");
        type.save();

    }



    // ////////////////////////////////////////////////////////////////////////
    //
    //        S e t u p   o f   a p p l i c a t i o n   i n s t a n c e s
    //
    // ////////////////////////////////////////////////////////////////////////
    
    /**
     * Creates a default FAQ application instance at address /faq/
     * 
     */
    private void setupDefaultFaqInstance() {
        s_log.debug("About to create FAQ application instance ...");

        /* Determine a parent application.                                    */
        Application parent;

        /* For now we install FAQ as an root application. 
         * If admin needs to install several FAQ instances this decision may
         * become inappropriate. It may be better to either not install any 
         * default application and leave it to the admin to install proper 
         * instances or try to find a more flexible solution which better fits
         * to many instances.                                                 */
        // parent = Application.retrieveApplicationForPath("/admin/");
        parent=null;

        Faq faq = Faq.create("faq", "Default FAQ", parent);
        faq.setDescription("The default ccm-faq instance.");
        faq.save();
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
    private void loadFaqQuestionsPortletType(ApplicationType provider) {

        // Create the FAQ questions portlet
        AppPortletType portletType = AppPortletType.createAppPortletType
            ("Faq Questions Portlet", AppPortletType.WIDE_PROFILE,
             FaqQuestionsPortlet.BASE_DATA_OBJECT_TYPE);
        portletType.setProviderApplicationType(Faq.BASE_DATA_OBJECT_TYPE);
        portletType.setPortalApplication(true);
        portletType.save();

    }



}
