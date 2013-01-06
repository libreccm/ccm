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

package com.arsdigita.navigation;

import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.loader.PackageLoader;
import com.arsdigita.navigation.portlet.ItemListPortlet;
import com.arsdigita.navigation.portlet.NavigationTreePortlet;
import com.arsdigita.navigation.portlet.ObjectListPortlet;
import com.arsdigita.portal.PortletType;
import com.arsdigita.runtime.ScriptContext;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.StringParameter;
import com.arsdigita.web.Application;
import com.arsdigita.web.ApplicationType;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;


/**
 * Loader executes nonrecurring at install time and loads (installs and
 * initializes) the Navigation module persistently into database.
 *
 * NOTE: Configuration parameters used at load time MUST be part of Loader 
 * class and can not delegated to a Config object (derived from AbstractConfig).
 * They will (and can) not be persisted into an registry object (file).
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: Loader.java 2070 2010-01-28 08:47:41Z pboy $
 */
public class Loader extends PackageLoader {

    /** Creates a s_logging category with name = full name of class */
    private static final Logger s_log = Logger.getLogger(Loader.class);

    private StringParameter m_templatesFile = new StringParameter(
        "com.arsdigita.navigation.templates_file",
        Parameter.REQUIRED,
        "WEB-INF/navigation/templates.txt");

    /**
     * Constructor, just registers parameters.
     */
    public Loader() {
        register( m_templatesFile );
    }

    /**
     * Run script invoked by com.arsdigita.packing loader script.
     * 
     * @param ctx
     */
    public void run(final ScriptContext ctx) {
        new KernelExcursion() {
            public void excurse() {
                setEffectiveParty(Kernel.getSystemParty());

                loadNavigationApplicationType();
                setupNavigation();

                loadItemListPortlet();
                loadObjectListPortlet();
                NavigationTreePortlet.loadPortletType();

                String templatesFile = (String)get(m_templatesFile);
                try {
                    setupTemplates(templatesFile);
                } catch( IOException ex ) {
                    throw new UncheckedWrapperException( ex );
                }
            }
        }.run();
    }

    // ////////////////////////////////////////////////////////////////////////
    //
    //          S e t u p    o f   a p p l i c a t i o n   t y p e s
    //
    // ////////////////////////////////////////////////////////////////////////

    /**
     * Creates a Navigation ApplicationType as a legacy-free type of application,
     * ie loads the class definition into persistent storage (application_types).
     */
    private void loadNavigationApplicationType() {

        // NOTE: The title "Navigation" is used to retrieve the application's
        // name to determine the location of xsl files (by url-izing it). So
        // DON'T modify it without synchronizing web directory tree accordingly!
        ApplicationType type =
                new ApplicationType( "Navigation",
                                     Navigation.BASE_DATA_OBJECT_TYPE );
        type.setDescription("A category based navigation system.");

    }


    /**
     * Creates ItemListPortlet portlet type, ie loads the class definition
     * into persistent storage (table application_types).
     */
    public static void loadItemListPortlet() {
        PortletType type = PortletType
            .createPortletType("Navigation Content Item List",
                               PortletType.WIDE_PROFILE,
                               ItemListPortlet.BASE_DATA_OBJECT_TYPE);
        type.setDescription("Displays a list of content items");
    }

    /**
     * Creates ObjectListPortlet portlet type, ie loads the class definition
     * into persistent storage (table application_types).
     */
    public static void loadObjectListPortlet() {
        PortletType type = PortletType
            .createPortletType("Navigation Object List",
                               PortletType.WIDE_PROFILE,
                               ObjectListPortlet.BASE_DATA_OBJECT_TYPE);
        type.setDescription("Displays a list of objects");
    }


    // ////////////////////////////////////////////////////////////////////////
    //
    //        S e t u p    o f   N A V I G A T I O N   i n s t a n c e s
    //
    // ////////////////////////////////////////////////////////////////////////

    /**
     * Creating a default navigation instance at address /navigation/
     *
     */
    public void setupNavigation() {
/*
        ApplicationSetup setup = new ApplicationSetup(s_log);
        
        setup.setApplicationObjectType(
            Navigation.BASE_DATA_OBJECT_TYPE);
        setup.setKey("navigation");
        setup.setTitle("Navigation");
        setup.setDescription("Category navigation");
        setup.setSingleton(true);
        setup.setInstantiator(new ACSObjectInstantiator() {
                public DomainObject doNewInstance(DataObject dataObject) {
                    return new Navigation(dataObject);
                }
            });
        ApplicationType type = setup.run();
        type.save();
*/
        Navigation app = (Navigation)
            Application.createApplication(Navigation.BASE_DATA_OBJECT_TYPE,
                                          "navigation",
                                          "Navigation Control Center",
                                          null);
        app.setDescription("The default ccm-navigation instance.");
        app.save();
    }

    /**
     * Processes a file with Navigation template specificatgions and
     * registers JSP templates with Navigation.
     * 
     * @param templatesFile file containing templates specification
     * @throws IOException 
     */
    public static void setupTemplates(String templatesFile) throws IOException {
        
        InputStream file = Thread.currentThread().getContextClassLoader()
                                 .getResourceAsStream(templatesFile);
        if (file == null) {
            throw new UncheckedWrapperException(String.format(
                         "Failed to open templates files %s.", templatesFile));
        }
        BufferedReader templates =
            new BufferedReader( new InputStreamReader( file ) );

        String template = templates.readLine();
        while ( null != template ) {
            StringTokenizer tok = new StringTokenizer( template, "," );

            String title = null;
            String desc = null;
            String path = null;
            for ( int i = 0; tok.hasMoreTokens(); i++ ) {
                if ( 0 == i ) {
                    title = tok.nextToken();
                } else if ( 1 == i ) {
                    desc = tok.nextToken();
                } else if ( 2 == i ) { 
                    path = tok.nextToken();
                } else {
                    path = null;
                }
            }

            if ( null == path ) {
                throw new UncheckedWrapperException
                    ( "Template should have 3 comma seperated fields: " +
                      template );
            }

            if (s_log.isDebugEnabled()) {
                s_log.debug( "Title=\"" + title + "\"" );
                s_log.debug( "Description=\"" + desc + "\"" );
                s_log.debug( "Path=\"" + path + "\"" );
            }

            Template.create(title, desc, path);

            template = templates.readLine();
        }
        
    }
}
