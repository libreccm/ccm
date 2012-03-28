/*
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
package org.undp.weblog;

import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.loader.PackageLoader;

import com.arsdigita.portal.apportlet.AppPortletType;
import com.arsdigita.runtime.ScriptContext;
import com.arsdigita.web.Application;
import com.arsdigita.web.ApplicationType;
import org.apache.log4j.Logger;
import org.undp.weblog.ui.WebLogPortlet;

/**
 * Executes nonrecurring at install time and loads (installs and initializes)
 * the WebLog application and type persistently into database.</p>
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

                ApplicationType weblogType = loadWebLogApplicationType();
                loadWebLogPortletType(weblogType);

                // NOTE: Perhaps it is not usefull to create a default instance,
                // because a site might have more than one blog and a more
                // appropriate location has to be determined.
                setupDefaultWebLogApplicationInstance();

            }
        }.run();

        s_log.info("Done");
        
    }
    /**
     * Creates a WebLog application type, the domain class of the weblog
     * package, as a legacy-free type of application.
     *
     * Creates an entry in table application_types
     *
     */
    private ApplicationType loadWebLogApplicationType() {
        s_log.info("WebLog Loader - setting up application type");

        /* NOTE: The wording in the title parameter of ApplicationType determines
         * the name of the subdirectory for the XSL stylesheets.
         * It gets "urlized", i.e. trimming leading and trailing blanks and replacing
         * blanks between words and illegal characters with an hyphen and converted
         * to lower case.
         * Example: "WebLog" will become "weblog".
         */
        ApplicationType type =  new ApplicationType("WebLog",
                                        WebLogApplication.BASE_DATA_OBJECT_TYPE );
        type.setDescription("The WebLog application enables user .....");
        type.save();

        return type;
    }


    /**
     * 
     * @param webLogAppType 
     */
    private void loadWebLogPortletType(ApplicationType webLogAppType ) {
        
        AppPortletType type = AppPortletType.createAppPortletType(
                                      "WebLog Portlet", 
                                      AppPortletType.WIDE_PROFILE,
                                      WebLogPortlet.BASE_DATA_OBJECT_TYPE);
        type.setDescription("Displays WebLogs for this portal.");
        type.setProviderApplicationType(webLogAppType);
        type.setPortalApplication(true);

        type.save();

    }


    /**
     * Instantiates the WebLog application admin instance.
     * 
     */
    public static void setupDefaultWebLogApplicationInstance() {

        /* Determine a parent application. WebLog default page will be 
         * installed beyond the admin's applications URL. It enshures
         * the user has to be logged in.                                     */
        /* NOTE: The location /admin/ might not be a good selection and a
         * more appropriate one should be found.                             */
        Application parent = Application.retrieveApplicationForPath("/admin/");

        // create application instance 
        // Whether a legacy compatible or a legacy free application is
        // created depends on the type of ApplicationType above. No need to
        // modify anything here in the migration process
        // old-style package key used as url fragment where to install the instance
        s_log.debug("Creating WebLogApplication instance ...");

        WebLogApplication app = WebLogApplication.create("blog", 
                                                         "Weblog", 
                                                         parent);
        app.setDescription("Default WebLog application instance.");
        app.save();

        s_log.debug("WebLog instance " + " created.");
    }

}
