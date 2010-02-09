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

package com.arsdigita.london.navigation;

// same package
// import com.arsdigita.london.navigation.Navigation;

import com.arsdigita.domain.DomainObject;
import com.arsdigita.kernel.ACSObjectInstantiator;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.loader.PackageLoader;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.runtime.ScriptContext;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.StringParameter;
import com.arsdigita.web.Application;
import com.arsdigita.web.ApplicationSetup;
import com.arsdigita.web.ApplicationType;

import com.arsdigita.london.navigation.portlet.ObjectListPortlet;
import com.arsdigita.london.navigation.portlet.ItemListPortlet;

import com.arsdigita.portal.PortletType;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

/**
 * Loader.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: Loader.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class Loader extends PackageLoader {

    private static final Logger s_log = Logger.getLogger(Loader.class);

    private StringParameter m_templatesFile = new StringParameter(
        "com.arsdigita.london.navigation.templates_file",
        Parameter.REQUIRED,
        "WEB-INF/navigation/templates.txt");

    public Loader() {
        register( m_templatesFile );
    }

    public void run(final ScriptContext ctx) {
        new KernelExcursion() {
            public void excurse() {
                setEffectiveParty(Kernel.getSystemParty());
                setupNavigation();
                Loader.loadObjectListPortlet();
                Loader.loadItemListPortlet();

                try {
                    setupTemplates();
                } catch( IOException ex ) {
                    throw new UncheckedWrapperException( ex );
                }
            }
        }.run();
    }

    public void setupNavigation() {
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
        
        Navigation app = (Navigation)
            Application.createApplication(type,
                                          "navigation",
                                          "Navigation Control Center",
                                          null);
        app.save();
    }

    public void setupTemplates() throws IOException {
        String templatesFile = (String)get(m_templatesFile);
        InputStream file = Thread.currentThread().getContextClassLoader()
                                 .getResourceAsStream(templatesFile);

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
    
    public static void loadItemListPortlet() {
        PortletType type = PortletType
            .createPortletType("Object List", 
                               PortletType.WIDE_PROFILE,
                               ObjectListPortlet.BASE_DATA_OBJECT_TYPE);
        type.setDescription("Displays a list of objects");
    }
    
    public static void loadObjectListPortlet() {
        PortletType type = PortletType
            .createPortletType("Content Item List", 
                               PortletType.WIDE_PROFILE,
                               ItemListPortlet.BASE_DATA_OBJECT_TYPE);
        type.setDescription("Displays a list of content items");
    }
}
