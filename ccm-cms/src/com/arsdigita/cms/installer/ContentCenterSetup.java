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
package com.arsdigita.cms.installer;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.initializer.InitializationException;
import com.arsdigita.kernel.PackageInstance;
import com.arsdigita.kernel.PackageType;
import com.arsdigita.kernel.Stylesheet;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.UncheckedWrapperException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.log4j.Logger;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Sets up the content section.
 *
 * @author Jon Orris (jorris@redhat.com)
 * @version $Revision: #10 $ $DateTime: 2004/08/17 23:15:09 $
 */

public final class ContentCenterSetup {

    /** URL to access the CMS Workspace, by default content-center  */
    final String m_workspaceURL;

    /** Map of URL stubs and resource handler classes for ContentCenterDispatcher
     * to use */
    final String m_contentCenterMap;

    /** Contains mapping of URL (key) to resource handler*/
    private static HashMap s_pageClasses = new HashMap();

    /** Contains mapping of resource(key) to resource handler */
    private static HashMap s_pageURLs = new HashMap();


    private final static String SERVICE_URL = "cms-service";

    private final static String STYLESHEET = "/packages/content-section/xsl/cms.xsl";
    private final static String PACKAGE_KEY = "content-section";
    private final static String DISPATCHER_CLASS =
        "com.arsdigita.cms.dispatcher.ContentSectionDispatcher";


    private static Logger s_log = Logger.getLogger(ContentSectionSetup.class);

    /**
     * Constructor
     * @param workspaceURL
     * @param contentCenterMap
     */
    public ContentCenterSetup( String workspaceURL,
                               String contentCenterMap) {

        m_contentCenterMap = contentCenterMap;
        m_workspaceURL = workspaceURL;
    }

    /** Gives you a mapping of URL (key) to resource handler
     *  Use the returned map like so: map.get("search");
     */
    public static HashMap getURLToClassMap() {
        return s_pageClasses;
    }

    /** Gives you a mapping of resource(key) to resource handler
     *  Use the returned map like so: map.get("com.arsdigita.cms.ui.WorkspacePage");
     */
    public static HashMap getClassToURLMap() {
        return s_pageURLs;
    }

    public void run() {

        // 1) Setup the CMS package.
        setupCMSPackage();

        // 2) Setup the Workspace package.

        setupWorkspacePackage();

        // 3) Setup the CMS global services package.
        setupServicePackage();

        // 8) Load the content-center page mappings
        setupContentCenter();


    }

    /**
     * Creates the CMS package type if it does not already exist.
     */
    private static void setupCMSPackage() {
        if ( !PackageType.typeExists(Installer.PACKAGE_KEY) ) {
            s_log.info("Creating the CMS package type...");

            PackageType type = PackageType.create
                    (PACKAGE_KEY, "Content Management System", "Content Management Systems",
                            "http://cms-java.arsdigita.com/");
            type.setDispatcherClass(DISPATCHER_CLASS);
            //type.addListener(LISTENER_CLASS);

            // Register a stylesheets to the CMS package.
            Stylesheet ss = Stylesheet.createStylesheet(STYLESHEET);
            ss.save();
            type.addStylesheet(ss);
            type.save();

            createPrivileges();

            s_log.info("Done creating the CMS package type.");
        }
    }


    /**
     * Creates and mounts the Workspace package.
     */
    private void setupWorkspacePackage() throws InitializationException {
        if ( !PackageType.typeExists((new WorkspaceInstaller()).getPackageKey())) {
            s_log.info("Initializing CMS Workspace...");

            Util.validateURLParameter("workspace", m_workspaceURL);

            WorkspaceInstaller workspaceInstaller = new WorkspaceInstaller();
            try {
                workspaceInstaller.createPackageType();
                PackageInstance instance = workspaceInstaller.createPackageInstance();
                workspaceInstaller.mountPackageInstance(instance, m_workspaceURL);
            } catch (DataObjectNotFoundException e) {
                throw new InitializationException
                        ("Failed to initialize the Workspace package: ", e);
            }

            s_log.info("Done initializing CMS Workspace.");
        }
    }

    /**
       * Creates and mounts the CMS Service package.
       */
      private static void setupServicePackage() {
          if ( !PackageType.typeExists(ServiceInstaller.PACKAGE_KEY) ) {
              String url = SERVICE_URL;

              try {
                  ServiceInstaller.createPackageType();
                  PackageInstance instance = ServiceInstaller.createPackageInstance();
                  ServiceInstaller.mountPackageInstance(instance, url);
              } catch (DataObjectNotFoundException e) {
                  throw new InitializationException
                      ("Failed to initialize CMS global services package: ", e);
              }
          }
      }


    /**
     * Creates the CMS privileges.
     */
    private static void createPrivileges() {

        final String CMS_PRIVILEGES = "com.arsdigita.cms.getPrivileges";
        final String PRIVILEGE = "privilege";

        DataQuery dq = SessionManager.getSession().retrieveQuery(CMS_PRIVILEGES);
        try {
            while ( dq.next() ) {
                String privilege = (String) dq.get(PRIVILEGE);
                if ( PrivilegeDescriptor.get(privilege) == null ) {
                    PrivilegeDescriptor.createPrivilege(privilege);
                }
            }

        } finally {
            dq.close();
        }
    }




    private void setupContentCenter() throws InitializationException {
        final PageClassConfigHandler handler 
            = new PageClassConfigHandler(s_pageClasses, s_pageURLs);

	final ClassLoader loader = Thread.currentThread
	    ().getContextClassLoader();
	final InputStream input = loader.getResourceAsStream
	    (m_contentCenterMap.substring(1));

	if (input == null) {
	    throw new IllegalStateException(m_contentCenterMap + " not found");
	}

	final InputSource source = new InputSource
	    (input);

        try {
            final SAXParserFactory spf = SAXParserFactory.newInstance();
            final SAXParser parser = spf.newSAXParser();
            parser.parse(source, handler);
        } catch (ParserConfigurationException e) { 
            throw new UncheckedWrapperException("error parsing dispatcher config", e);
        } catch (SAXException e) { 
            throw new UncheckedWrapperException("error parsing dispatcher config", e);
        } catch (IOException e) { 
            throw new UncheckedWrapperException("error parsing dispatcher config", e);
        }
    }

}
