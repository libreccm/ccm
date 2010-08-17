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
package com.arsdigita.cms;

import com.arsdigita.cms.installer.ContentSectionSetup;
import com.arsdigita.cms.installer.PageClassConfigHandler;
import com.arsdigita.cms.installer.Util;
import com.arsdigita.cms.installer.xml.XMLContentTypeHandler;
import com.arsdigita.cms.installer.WorkspaceInstaller;
//import com.arsdigita.cms.portlet.ContentDirectoryPortlet;
import com.arsdigita.cms.portlet.ContentItemPortlet;
//import com.arsdigita.cms.portlet.ContentSectionsPortlet;
import com.arsdigita.cms.portlet.TaskPortlet;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.kernel.PackageInstance;
import com.arsdigita.kernel.PackageType;
import com.arsdigita.kernel.SiteNode;
import com.arsdigita.kernel.Stylesheet;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.loader.PackageLoader;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.runtime.ConfigError;
import com.arsdigita.runtime.ScriptContext;
import com.arsdigita.util.UncheckedWrapperException;
//import com.arsdigita.util.parameter.Parameter;
// import com.arsdigita.util.parameter.BooleanParameter;
//import com.arsdigita.util.parameter.StringParameter;
//import com.arsdigita.util.parameter.URLParameter;
// import com.arsdigita.web.Application;
// import com.arsdigita.web.ApplicationType;
import com.arsdigita.xml.XML;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


import org.apache.log4j.Logger;


//  Migration status
//
//  The module in its complete version (i.e. all method invocations in run()
//  method commented IN(!) does load all packages into database and
//  ccm/admin/sitemap lists them appropriately.
//  Not yet found a way to mount them in the URL tree while initializing.


/**
 * <p>Executes nonrecurring at install time and loads (installs and initializes)
 * the Content Management System module,including the Content Center, CMS Service
 * applications, and CMS Mime Types service persistently into database.</p>
 *
 * <p>This class also optionally initializes user-defined content types. </p>
 * <p>Additional user-defined content sections can be loaded and initilized
 * using the recurring <pre>initializer</pre> at any startup.
 *
 * <p>The tasks to perform are:</p>
 * <ol>
 *   <li>create CMS package type(content-section)</li>
 *   <li>create Workspace package type and instance</li>
 *   <li>create CMS Service package type and instance</li>
 *   <li>create CMS package (content-section) instance</li>
 * </ol>
 *
 * <p>Configuration can be modified by configuration parameters before processing,
 * otherwise hardcoded default values take effect. After processing the
 * installation values can not be modified anymore without a fresh installation
 * of the whole system.</p>
 *
 * @author Peter Boy &lt;pboy@barkhof.uni-bremen.de&gt;
 * @since ccm-cms version 6.6.0
 * @version $Id: $
 */
public class Loader extends PackageLoader {

    /** Private logger instance  */
    private static final Logger s_log = Logger.getLogger(Loader.class);

    // Load main CMS configuration file
    private static final LoaderConfig s_conf = new LoaderConfig();

//  static {               // requirred to actually read the config file
//      s_config.load();
//  }

    /**
     * Constant string used as key for creating CMS (content-section) as a
     * legacy application.
     */
    private final static String CMS_PACKAGE_KEY = "content-section";
    /**
     * Dispatcher class for CMS (needed to be assigned to a legacy application).
     */
    private final static String CMS_DISPATCHER_CLASS =
            "com.arsdigita.cms.dispatcher.ContentSectionDispatcher";
    /**
     * Stylesheet which has to be assigned as part of a legacy application
     * creation.
     */
    private final static String CMS_STYLESHEET =
            "/packages/content-section/xsl/cms.xsl";

    /**
     * Constant string used as key for creating Workspace (content-center) as a
     * legacy application.
     */
    public static final String WORKSPACE_PACKAGE_KEY   = "content-center";
    private static final String WORKSPACE_INSTANCE_NAME = "Content Center";
    /**
     * Dispatcher class for Workspace (content-center) (needed to be assigned
     * to a legacy application).
     */
    private static final String WORKSPACE_DISPATCHER_CLASS =
        "com.arsdigita.cms.dispatcher.ContentCenterDispatcher";
    // To be updated soon...
    // "com.arsdigita.dispatcher.DefaultPackageDispatcher";
    /**
     * Stylesheet which has to be assigned as part of a legacy application
     * creation.
     */
    private final static String WORKSPACE_STYLESHEET =
            "/packages/content-section/xsl/content-center.xsl";

    /**
     * Name of the CMS service package instance, i.e. its URL.
     */
    private final static String SERVICE_URL = "cms-service";
    /**
     * Constant string used as key for creating service package as a
     * legacy application.
     */
    public final static String SERVICE_PACKAGE_KEY = "cms-service";


    private static HashMap s_pageClasses = new HashMap();
    private static HashMap s_pageURLs = new HashMap();


    /**
     * Standard constructor.
     */
    public Loader() {
        s_log.debug("CMS.loader (Constructor) invoked");

        s_log.debug("CMS.loader (Constructor) completed");
    }


    public void run(final ScriptContext ctx) {
        // XXX: Should move on demand initialization stuff here.
        s_log.debug("CMS.loader.run() invoked");

        new KernelExcursion() {
            public void excurse() {
                setEffectiveParty(Kernel.getSystemParty());


                // ////////////////////////
                // Loading CMS package (esp. content center, cms service
                // Used to be step 1 in former enterprise.init file
                // Implemented by
                // com.arsdigita.cms.installer.xml.ContentTypeInitializer
                // ////////////////////////////////
                // 1 - step 1a) Setup the CMS package.
//              loadCMSPackageType();
                // loadContentSectionPackageType using (new) c.ad.web package.
                // ContentSectionSetup.setupContentSectionAppType();

                //  createPrivileges();

                // 2 - step 1b) Setup the Workspace package.
//              loadWorkspacePackage();

                // 3 - step 1c) Setup the CMS global services package.
//              loadServicePackage();

                // 4 - stgep 1d) Load the content-center page mappings
//              loadContentCenterMapping(s_conf.getContentCenterMap() );

                // ////////////////////////
                // 5) load content type definition(s)
                // Loading content type definitions
                // Used to be step 2 in former enterprise.init file
                // Implemented by
                // com.arsdigita.cms.installer.xml.ContentTypeInitializer
                // ////////////////////////////////
                loadContentTypeDefinitions(s_conf.getCTDefFiles() );

                // ////////////////////////
                // 6) Load CMS (content section) package application instance
                // Loading content section application type and instance
                // Used to be step 4 in former enterprise.init file
                // (step 3 being initialize publishToFile, not to handle in Loader)
                // Implemented by
                // com.arsdigita.cms.installer.SectionInitializer
                // ////////////////////////////////
                loadContentSection(s_conf.getContentSectionName());


                // ///////////////////////////////////////////////////////
                // Loading CMS portlets
                // Used to be step 7 (last step) in former enterprise.init
                // ///////////////////////////////////////////////////////
                s_log.debug("CMS.loader going to load portlets");
                //ContentDirectoryPortlet.loadPortletType();
                ContentItemPortlet.loadPortletType();
                //ContentSectionsPortlet.loadPortletType();
                TaskPortlet.loadPortletType();

             
            }
        }.run();
    }


    /**
     * Loads the CMS package type in the database, i.e. content-section,
     * the main CMS domain (application) class.
     *
     * (pb) WRONG:
     * Creates content-section PackageType. Is replaced by newer ApplicationType
     * mechanisam (see loadContentSection). Must nolonger be used except of
     * createPrivileges!
     */
    private void loadCMSPackageType() {
        s_log.debug("Loading the CMS package type...");

        // creating appl. type using the deprecated legacy application style.
        // Should be refactored to c.ad.web.Application.
        //  CMS_PACKAGE_KEY = "content-section"
//      PackageType type = PackageType.create
//                         (CMS_PACKAGE_KEY,
//                          "Content Management System",
//                          "Content Management Systems",
//                          "http://cms-java.arsdigita.com/");

//      type.setDispatcherClass(CMS_DISPATCHER_CLASS);
        //type.addListener(LISTENER_CLASS);  (commented out)

        // Register a stylesheets to the CMS package.
//      Stylesheet ss = Stylesheet.createStylesheet(CMS_STYLESHEET);
//      ss.save();
//      type.addStylesheet(ss);
//      type.save();


//      Creating of CMS package using new style c.ad.web.Application could
//      be done like the following.
//      ApplicationType type = ApplicationType
//          .createApplicationType(ContentSection.PACKAGE_TYPE,
//                                 "Conten Management Application (i.e. content-section)",
//                                 ContentSection.BASE_DATA_OBJECT_TYPE);
//      type.setDescription("The content management application.");
//      // Question: Do we need to use deprectated (with no replacement) method
//      // setDispatcherClass in web.Application or does it work magically without?
//      type.setDispatcherClass(CMS_DISPATCHER_CLASS);
//      type.addStylesheet(ss);


        createPrivileges();

        s_log.debug("Done creating the CMS package type.");
    }


    /**
     * Loads and instantiates the Workspace package (content-center) in the
     * database.
     */
    private void loadWorkspacePackage() {
        s_log.debug("Creating CMS Workspace...");

            WorkspaceInstaller workspaceInstaller = new WorkspaceInstaller();
            try {
                workspaceInstaller.createPackageType();
                // type.setDispatcherClass(WORKSPACE_DISPATCHER_CLASS);

//              PackageInstance instance = workspaceInstaller.createPackageInstance();
                // Does the following:
                // type = PackageType.findByKey(WORKSPACE_PACKAGE_KEY);
                // PackageInstance instance = type.createInstance(WORKSPACE_INSTANCE_NAME);
                // instance.save();
                PackageInstance instance = workspaceInstaller.createPackageInstance();

//              workspaceInstaller.mountPackageInstance(instance, m_workspaceURL);
                // Does the following:
                // SiteNode node = SiteNode.createSiteNode(
                //                          s_conf.getWorkspaceURL(),
                //                          SiteNode.getRootSiteNode());
                // node.mountPackage(instance);
                // node.save();

                // m_workspaceURL == WORKSPACE_PACKAGE_KEY
                // workspaceInstaller.mountPackageInstance(instance, m_workspaceURL);
                workspaceInstaller.mountPackageInstance(instance, WORKSPACE_PACKAGE_KEY);

            } catch (DataObjectNotFoundException e) {
                throw new ConfigError(
                         "Failed to initialize the Workspace package: ");
            }



//      Creating of Workspace package using new style c.ad.web.Application could
//      be done like the following (legacy compatible style!).
//      Needs refactoring of the Workspace package.
//      // Step 1: Create application type
//      ApplicationType type = ApplicationType
//          .createApplicationType("content-center",
//                                 "The Content Center Workspace",
//                                 ContentCenter.BASE_DATA_OBJECT_TYPE);  NEEDED!
//      type.setDescription("The content center workspace for content creators.");
//
//       // Step 2: Create application
//      Application app = Application.createRootApplication(
//                                    type,
//                                    "Content Center Workspace");
//      app.setPath((String) get(m_workspaceURL));

        s_log.debug("Done loading CMS Workspace.");
    }


    /**
     * CMS Service application is used by the Content Management System as a 
     * store for global resources and assets.
     * 
     */
    private void loadServicePackage() {
        s_log.debug("Loading CMS Servce Package...");

            try {
//              ServiceInstaller.createPackageType();
                PackageType type = PackageType.create
                                   (SERVICE_PACKAGE_KEY,
                                    "Content Management System Services",
                                    "Content Management System Services",
                                    "http://cms-service.arsdigita.com/");
                type.setDispatcherClass(
                        "com.arsdigita.cms.dispatcher.ServiceDispatcher");
                type.save();

//              PackageInstance instance = ServiceInstaller.createPackageInstance();
                type = PackageType.findByKey(SERVICE_PACKAGE_KEY);
                PackageInstance instance = type.createInstance(SERVICE_PACKAGE_KEY);
                instance.save();

//              ServiceInstaller.mountPackageInstance(instance, url);
                SiteNode node = SiteNode.createSiteNode(SERVICE_URL,
                                                        SiteNode.getRootSiteNode());
                node.mountPackage(instance);
                node.save();

            } catch (DataObjectNotFoundException e) {
                  throw new ConfigError
                      ("Failed to initialize CMS global services package.");
              }



//      Creating of Service package using new style c.ad.web.Application could
//      be done like the following (legacy compatible style!).
//      Needs refactoring of the Servcie package.
//      // Step 1: Create application type
//      ApplicationType type = ApplicationType
//          .createApplicationType("cms-service",  // Type = PACKAGE_KEY,
//                                 "Content Management System Services",
//                                 SERVICE_BASE_DATA_OBJECT_TYPE);
//       // Step 2: Create application
//      Application workspace = Application.createRootApplication(
//                                    type,
//                                    "Content Center Workspace");
//      app.setPath((String) get(m_workspaceURL));

        s_log.debug("Done creating CMS Servce Package.");
    }


    private void loadContentCenterMapping(String mapFileName) throws ConfigError {

        final PageClassConfigHandler handler
            = new PageClassConfigHandler(s_pageClasses, s_pageURLs);

	final ClassLoader loader = Thread.currentThread
	    ().getContextClassLoader();
	final InputStream input = loader.getResourceAsStream
	    (mapFileName.substring(1));

	if (input == null) {
	    throw new IllegalStateException(mapFileName + " not found");
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


    /**
     * Parses XML file definition of content types and loads them to the
     * database.  The XML config looks like the example below, the
     * "parentType" and "name" attributes are optional, and only required
     * for creating User Defined ContentTypes. Label corresponds to
     * ContentType's label and can be multiple words, and "name" to
     * DynamicObject's name property, and must be a single word. The
     * objectType attribute is ignored for UDCTs, as it gets dynamically
     * generated.
     *
     * <b>UDCT Copyright</b>
     * <pre>
     * &lt;ccm:content-types&gt;
     *   &lt;ccm:content-type
     *             name="Copyright"
     *             label="UDCT Copyright"
     *             parentType="com.arsdigita.cms.contenttypes.Address"
     *             classname="com.arsdigita.cms.contenttypes.Address"
     *             description="Copyright for storing copyright information"
     *             objectType="com.arsdigita.cms.contentTypes.Address" &gt;
     *
     *      &lt;ccm:authoring-kit&gt;
     *      &lt;/ccm:authoring-kit&gt;
     *   &lt;/ccm:content-type&gt;
     * &lt;/ccm:content-types&gt;
     *</pre>
     *
     * @see XMLContentTypeHandler
     */
    private void loadContentTypeDefinitions(List ctDefFiles) {
        s_log.debug("Loading content type definitions ...");

        if ( ctDefFiles != null) {
            Iterator i = ctDefFiles.iterator();
            while (i.hasNext()) {
            //  TransactionContext txn = SessionManager.getSession()
            //      .getTransactionContext();
            //  txn.beginTxn();

                String xmlFile = (String)i.next();
                s_log.debug("Processing contentTypes in: " + xmlFile);
                XML.parseResource(xmlFile, new XMLContentTypeHandler());

            //  txn.commitTxn();
            }
        }

        s_log.debug("Done loading content type definitions.");
    }


    /**
     * Creates the CMS privileges.
     */
    private static void createPrivileges() {
        s_log.debug("Creating Privileges...");

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
        s_log.debug("Done creating Privileges.");
    }


    /**
     * Load an initial default content-section, specified in LoaderConfig.
     *
     *
     */
    private void loadContentSection(String name) {

        s_log.info("Creating content section on /" + name);

        // Step 1: Creating content section application type first so that
        //         concrete content-section instance can be created.
        ContentSectionSetup.setupContentSectionAppType();


        // Step 2: Validatge name for section
        Util.validateURLParameter("name", name);


        // Step 3: Create the installation default content section "name"
        ContentSection section = ContentSection.create(name);

        // ContentSectionSetup is a convenient class for ContentSection.create()
        ContentSectionSetup setup = new ContentSectionSetup(section);

        // ContentSection.create uses the following properties:
        // Name, see above
        // Root & template folder, set autonomously by ContentSection.create()
        // Roles (staff group) used in content section. Register roles using
        // a complete set of default roles defined in ContentSectionSetup
        setup.registerRoles(s_conf.getStuffGroup());
        // ViewerGroup populated in ContentSection, public access is determined
        // by parameter (affecting characteristics of the viewer group)
        setup.registerViewers(s_conf.isPubliclyViewable());
        // Page resolver class, set autonomously by ContentSection.create()
        // Item resolver class, configurable, defaults in place.
        // Template resolver class, configurable, defaults in place.
        // We should not overwrite the default in the initial default configuration

        // registers a predefined standard recipient for alerts
        setup.registerAlerts();
        // register a predefined one-phase lifecycle for items.
        setup.registerPublicationCycles();
        // registers predefined "Authoring", "Approval", "Publishing' steps
        setup.registerWorkflowTemplates();
        setup.registerContentTypes(s_conf.getContentSectionsContentTypes());

        setup.registerResolvers(s_conf.getItemResolverClass(),
                                s_conf.getTemplateResolverClass() );
        // XML generator class, set autonomously by ContentSection.create()

        // section specific categories, usually not used.
        // During initial load at install time nor used at all!
        // default value is false so no categories get loaded.
        if (s_conf.getUseSectionCategories()) {
            Iterator files = ((List) s_conf.getCategoryFileList()).iterator();
            while ( files.hasNext() ) {
                setup.registerCategories((String) files.next());
            }
        }

        section.save();  //persists any changes in the database (DomainObject)
                         //i.e. creates an object (instance)

    }



}
