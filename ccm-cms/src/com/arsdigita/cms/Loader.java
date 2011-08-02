/*
 * Copyright (C) 2009 Peter Boy <pb@zes.uni-bremen.de> All Rights Reserved.
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

import com.arsdigita.cms.contentsection.ContentSectionSetup;
import com.arsdigita.cms.util.Util;
import com.arsdigita.cms.installer.xml.XMLContentTypeHandler;
import com.arsdigita.cms.portlet.ContentDirectoryPortlet;
import com.arsdigita.cms.portlet.ContentItemPortlet;
import com.arsdigita.cms.portlet.ContentSectionsPortlet;
import com.arsdigita.cms.portlet.TaskPortlet;
import com.arsdigita.domain.DomainObject;
// import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.formbuilder.util.FormbuilderSetup;
import com.arsdigita.kernel.ACSObjectInstantiator;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
// import com.arsdigita.kernel.PackageInstance;
// import com.arsdigita.kernel.PackageType;
// import com.arsdigita.kernel.SiteNode;
// import com.arsdigita.kernel.Stylesheet;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.loader.PackageLoader;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.SessionManager;
// import com.arsdigita.runtime.ConfigError;
import com.arsdigita.runtime.ScriptContext;
import com.arsdigita.web.Application;
import com.arsdigita.web.ApplicationSetup;
import com.arsdigita.web.ApplicationType;
import com.arsdigita.xml.XML;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

//  Migration status
//
//  The module in its complete version (i.e. all method invocations in run()
//  method commented IN(!) does load all packages into database and
//  ccm/admin/sitemap lists them appropriately.
//
//  Next Try
//  Refactor using legacy compatible web/Application and ApplicationSetup
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
 * otherwise hardcoded default values take effect. After processing, the
 * installation values can not be modified anymore without a fresh installation
 * of the whole system.</p>
 *
 * @author Peter Boy &lt;pboy@barkhof.uni-bremen.de&gt;
 * @since ccm-cms version 6.6.0
 * @version $Id: Loader.java 2212 2011-06-22 08:39:04Z pboy $
 */
public class Loader extends PackageLoader {

    /** Creates a s_logging category with name = full name of class */
    private static final Logger s_log = Logger.getLogger(Loader.class);

    /** Loader configuration object, singleton design pattern               */
    private static final LoaderConfig s_conf = LoaderConfig.getInstance();

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

//  /**
//   * Stylesheet which has to be assigned as part of a legacy application
//   * creation.
//   */
//  Assigned stylesheets no longer used and base class removed. 
//  private final static String CMS_STYLESHEET =
//          "/packages/content-section/xsl/cms.xsl";
//  /**
//   * Constant string used as key for creating Workspace (content-center) as a
//   * legacy application.
//   */
//  public static final String WORKSPACE_PACKAGE_KEY   = "content-center";
    private static final String WORKSPACE_INSTANCE_NAME = "Content Center";
    /**
     * Dispatcher class for Workspace (content-center) (needed to be assigned
     * to a legacy application).
     */
    private static final String WORKSPACE_DISPATCHER_CLASS =
            "com.arsdigita.cms.dispatcher.ContentCenterDispatcher";
    // To be updated soon...
    // "com.arsdigita.dispatcher.DefaultPackageDispatcher";

//  /**
//   * Stylesheet which has to be assigned as part of a legacy application
//   * creation.
//   */
//  private final static String WORKSPACE_STYLESHEET =
//          "/packages/content-section/xsl/content-center.xsl";
    /**
     * Name of the CMS service package instance, i.e. its URL.
     */
    private final static String SERVICE_URL = "cms-service";
//  /**
//   * Constant string used as key for creating service package as a
//   * legacy application.
//   */
//  public final static String SERVICE_PACKAGE_KEY = "cms-service";
    private ArrayList m_content_type_list = new ArrayList();

    /**
     * Standard constructor.
     */
    public Loader() {
        s_log.debug("CMS.loader (Constructor) invoked");

        s_log.debug("CMS.loader (Constructor) completed");
    }

    public void run(final ScriptContext ctx) {
        s_log.debug("CMS.loader.run() invoked");

        new KernelExcursion() {

            public void excurse() {
                setEffectiveParty(Kernel.getSystemParty());

                // 1 - step 1a) Setup the CMS (content section) package type.
                // Uses old style application setup kernel.Package and Sitenode.
                // It is not needed, this step is included in new style setup
                // of content section (see below). It is here for reference
                // purpose during migration of the whole CMS to new style.
                // loadCMSPackageType();

                // 2 - step 1b) Setup the Workspace package.
                // loadWorkspacePackage();  // using old stype
                // new style:
                ApplicationType appType = loadWorkspaceApplicationType();
                setupDefaultWorkspaceApplicationInstance(appType);

                // 3 - step 1c) Setup the CMS global services package.
                // loadServicePackage();   // using olde style
                // new style:
                appType = loadServiceApplicationType();
                setupDefaultServiceApplicationInstance(appType);

                // 4 - step 1d) Load the content-center page mappings
                // Wrong! Is Initializer task, must be done each startup, in
                // any way not a Loader task. It is here for reference
                // purpose during migration of the whole CMS to new style.
                // loadContentCenterMapping(s_conf.getContentCenterMap() );

                // 5) load content type definition(s)
                // Used to be step 2 in former enterprise.init file
                loadContentTypeDefinitions(s_conf.getCTDefFiles());

                // 6) Load CMS (content section) package application instance
                // Used to be step 4 in former enterprise.init file
                // (step 3 being initialize publishToFile, not to handle in Loader)
                // Implemented by
                // com.arsdigita.cms.installer.SectionInitializer
                // Loads content section application type and instance in one step
                loadContentSection(s_conf.getContentSectionName());

                // Loading CMS portlets
                // Used to be step 7 (last step) in former enterprise.init
                s_log.debug("CMS.loader going to load portlets");
                ContentDirectoryPortlet.loadPortletType();
                ContentItemPortlet.loadPortletType();
                ContentSectionsPortlet.loadPortletType();
                TaskPortlet.loadPortletType();

                // Loading forms widget into database
                FormbuilderSetup fbs = new FormbuilderSetup();
                fbs.setup(s_conf.getWidgetTypes(),
                          s_conf.getProcessListenerTypes(),
                          s_conf.getDataQueries());


            }
        }.run();
    }

//  Migration Status:
//  Method completely replaced by loadContentSection()
//  Code kept for reference purpose untill the complete migration will be done.
//
//  /**
//   * Loads the CMS package type in the database, i.e. content-section,
//   * the main CMS domain (application) class.
//   *
//   * (pb) WRONG:
//   * Creates content-section PackageType. Is replaced by newer ApplicationType
//   * mechanisam (see loadContentSection). Must nolonger be used.
//   * createPrivileges might be included in load, but has te be executed at
//   * each startup to take modifications into accout. So it has to be an
//   * initializer task anyway.
//   */
//  private void loadCMSPackageType() {
//      s_log.debug("Loading the CMS package type...");
//
//      // creating appl. type using the deprecated legacy application style.
//      // Should be refactored to c.ad.web.Application.
//      //  CMS_PACKAGE_KEY = "content-section"
//      PackageType type = PackageType.create
//                         (CMS_PACKAGE_KEY,
//                          "Content Management System",
//                          "Content Management Systems",
//                          "http://cms-java.arsdigita.com/");
//
//      type.setDispatcherClass(CMS_DISPATCHER_CLASS);
//      //type.addListener(LISTENER_CLASS);  (commented out)
//
//      // Register a stylesheets to the CMS package.
//      Stylesheet ss = Stylesheet.createStylesheet(CMS_STYLESHEET);
//      ss.save();
//      type.addStylesheet(ss);
//      type.save();
//
//
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
//
//
//      createPrivileges();
//
//      s_log.debug("Done creating the CMS package type.");
//  }
//  /**
//   * Loads and instantiates the Workspace package (content-center) in the
//   * database using old style application.
//   * Will be replaced by new style application in the migration process,
//   */
/*
    private void loadWorkspacePackage() {
        s_log.debug("Creating CMS Workspace...");

        // from WorkspaceInstaller workspaceInstaller = new WorkspaceInstaller();
        try {
            // workspaceInstaller.createPackageType();
            PackageType type = PackageType.create(CMS.WORKSPACE_PACKAGE_KEY,
                    "Content Center",
                    "Content Centers",
                    "http://cms-workspace.arsdigita.com/");
            type.setDispatcherClass(WORKSPACE_DISPATCHER_CLASS);
            type.save();

            // from PackageInstance instance = workspaceInstaller.createPackageInstance();
            // Does the following:
            type = PackageType.findByKey(CMS.WORKSPACE_PACKAGE_KEY);
            PackageInstance instance = type.createInstance(WORKSPACE_INSTANCE_NAME);
            instance.save();

            // from: workspaceInstaller.mountPackageInstance(instance, m_workspaceURL);
            // Does the following:
            // We really don't want it configurable.
            // SiteNode node = SiteNode.createSiteNode(CMS.WORKSPACE_PACKAGE_KEY,
            //                                      SiteNode.getRootSiteNode());
            SiteNode node = SiteNode.createSiteNode(CMS.WORKSPACE_PACKAGE_KEY,
                    SiteNode.getRootSiteNode());
            node.mountPackage(instance);
            node.save();

            // m_workspaceURL == WORKSPACE_PACKAGE_KEY
            // workspaceInstaller.mountPackageInstance(instance, m_workspaceURL);
            // workspaceInstaller.mountPackageInstance(instance, CMS.WORKSPACE_PACKAGE_KEY);

        } catch (DataObjectNotFoundException e) {
            throw new ConfigError(
                    "Failed to initialize the Workspace package: ");
        }

    }
*/
    /**
     * Loads and instantiates the Workspace package (content-center) in the
     * database.
     * It is made public to be able to invoke it from the update script
     * (e.g. 6.6.1-6.6.2).
     */
    public static ApplicationType loadWorkspaceApplicationType() {
        s_log.warn("Creating CMS Workspace...");

//      Creating of Workspace package using new style c.ad.web.Application 
//      in legacy compatible mode. Needs refactoring of the Workspace package.
//      In a first step these instructions replace c.ad.installer.WorkspaceInstaller

        // create application type
        ApplicationSetup appsetup = new ApplicationSetup(s_log);
        // new style properties
        appsetup.setApplicationObjectType(Workspace.BASE_DATA_OBJECT_TYPE);
        appsetup.setTitle(Workspace.INSTANCE_NAME);  // same as for instance
        // there is only one
        appsetup.setDescription("The content center workspace for content creators.");
        // old style / legacy compatible properties
        appsetup.setKey(Workspace.PACKAGE_KEY);
        appsetup.setDispatcherClass(Workspace.DISPATCHER_CLASS);
        // should not be needed anymore, stypesheets handled by StylesheetResolver
        appsetup.setSingleton(true);
        appsetup.setPortalApplication(false);
        appsetup.setInstantiator(new ACSObjectInstantiator() {
            @Override
            protected DomainObject doNewInstance(DataObject dataObject) {
                return new Workspace(dataObject);
            }
        });

        ApplicationType workspaceType = appsetup.run();
        workspaceType.save();
        s_log.warn("CMS Workspace type created.");

        return workspaceType;
    }

    /**
     * 
     * @param workspaceType
     */
    public static void setupDefaultWorkspaceApplicationInstance(
                                                ApplicationType workspaceType) {
        // create legacy compatible  application instance,
        // old-style package key used as url fragment where to install the instance
        s_log.warn("Creating CMS Workspace instance ...");
        Workspace app = (Workspace) Application.createApplication(
                workspaceType, // type
                Workspace.PACKAGE_KEY, // url fragment
                Workspace.INSTANCE_NAME,// title
                null);                  // parent
        app.setDescription("The default CMS workspace instance.");
        app.save();
        s_log.warn("CMS Workspace instance created.");

        s_log.debug("Done loading CMS Workspace.");
    }

//  /**
//   * CMS Service application is used by the Content Management System as a
//   * store for global resources and assets.
//   * Using old style application, will be replaced by new style in the
//   * migration process.
//   */
/*  private void loadServicePackage() {
        s_log.debug("Loading CMS Servce Package...");

        try {
            // from ServiceInstaller.createPackageType();
            PackageType type = PackageType.create(CMS.SERVICE_PACKAGE_KEY,
                    "Content Management System Services",
                    "Content Management System Services",
                    "http://cms-service.arsdigita.com/");
            type.setDispatcherClass(
                    "com.arsdigita.cms.dispatcher.ServiceDispatcher");
            type.save();

            // from PackageInstance instance = ServiceInstaller.createPackageInstance();
            type = PackageType.findByKey(CMS.SERVICE_PACKAGE_KEY);
            PackageInstance instance = type.createInstance(CMS.SERVICE_PACKAGE_KEY);
            instance.save();

            // from ServiceInstaller.mountPackageInstance(instance, url);
            SiteNode node = SiteNode.createSiteNode(SERVICE_URL,
                    SiteNode.getRootSiteNode());
            node.mountPackage(instance);
            node.save();

        } catch (DataObjectNotFoundException e) {
            throw new ConfigError("Failed to initialize CMS global services package.");
        }
    }
*/
    /**
     * CMS Service application is used by the Content Management System as a
     * store for global resources and assets.
     * It is made public to be able to invoke it from the update script
     * (e.g. 6.6.1-6.6.2).
     */
    public static ApplicationType loadServiceApplicationType() {
        s_log.debug("Loading CMS Servce Package...");


//      Creating Service package using new style c.ad.web.Application
//      in legacy compatible mode. Needs refactoring of the Service package.
//      In a first step these instructions replace c.ad.installer.ServiceInstaller

        // create application type
        ApplicationSetup appsetup = new ApplicationSetup(s_log);
        // new style properties
        appsetup.setApplicationObjectType(Service.BASE_DATA_OBJECT_TYPE);
        appsetup.setTitle(Service.INSTANCE_NAME);  // same as for instance
        // there is only one
        appsetup.setDescription("Services to store global resources and assets.");
        // old style / legacy compatible properties
        appsetup.setKey(Service.PACKAGE_KEY);
        appsetup.setDispatcherClass(Service.DISPATCHER_CLASS);
        appsetup.setSingleton(true);
        appsetup.setPortalApplication(false);
        appsetup.setInstantiator(new ACSObjectInstantiator() {
            @Override
            protected DomainObject doNewInstance(DataObject dataObject) {
                return new Service(dataObject);
            }
        });

        ApplicationType serviceType = appsetup.run();
        serviceType.save();

        return serviceType;
    }


    /**
     *
     * @param serviceType
     */
    public static void setupDefaultServiceApplicationInstance(
                                                ApplicationType serviceType) {
        // create legacy compatible  application instance,
        // old-style package key used as url fragment where to install the instance
        Service app = (Service) Application.createApplication(
                serviceType, // type
                Service.PACKAGE_KEY, // url fragment
                Service.INSTANCE_NAME,// title
                null);                // parent
        app.setDescription("The default CMS service instance.");
        app.save();

        s_log.debug("Done creating CMS Service Package.");
    }

    /**
     * Load an content section application type and an initial default
     * content-section instance as specified in LoaderConfig.
     * Uses new style application in legacy compatible mode.
     *
     */
    private void loadContentSection(String name) {

        s_log.info("Creating content section on /" + name);

        // Step 1: Validate name for section
        Util.validateURLParameter("name", name);

        // Step 2: Creating content section application type first so that
        //         concrete content-section instance can be created.
        // from: ContentSectionSetup.setupContentSectionAppType();
        // Install application type using new application classes
        ApplicationSetup appType = new ApplicationSetup(s_log);
        appType.setApplicationObjectType(ContentSection.BASE_DATA_OBJECT_TYPE);
        appType.setKey(ContentSection.PACKAGE_TYPE); // by default: content-section
        appType.setTitle("CMS Content Section");
        appType.setDescription("A CMS Content Section");
        appType.setPortalApplication(false);
        //setup.setDispatcherClass(ContentItemDispatcher.class.getName());
        // contains the xsl to generate the page

        // ApplicationSetup requires an Instantiator which has to be set here
        // Setting it up in Initializer prior to creating the application type
        // doesn't work!
        appType.setInstantiator(new ACSObjectInstantiator() {
            @Override
            public DomainObject doNewInstance(DataObject dataObject) {
                return new ContentSection(dataObject);
           }
         });

        appType.run();

        // Step 3:
        createPrivileges();

        // Step 4: Create the installation default content section "name"
        // ContentSection.create creates a section with several default values
        // which have to be adopted for a concrete installation.
        ContentSection section = ContentSection.create(name);
        // ContentSectionSetup is a convenient class to adopt a section created
        // by ContentSection.create()
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

        // register a predefined one-phase lifecycle for items.
        setup.registerPublicationCycles();
        // registers predefined "Authoring", "Approval", "Publishing' steps
        setup.registerWorkflowTemplates();
        setup.registerResolvers(s_conf.getItemResolverClass(),
                s_conf.getTemplateResolverClass());
        // XML generator class, set autonomously by ContentSection.create()

        setup.registerContentTypes(m_content_type_list);
        setup.registerContentTypes(s_conf.getContentSectionsContentTypes());
        // Section specific categories, usually not used.
        // During initial load at install time nor used at all!
        // default value is false so no categories get loaded.
        if (s_conf.getUseSectionCategories()) {
            Iterator files = ((List) s_conf.getCategoryFileList()).iterator();
            while (files.hasNext()) {
                setup.registerCategories((String) files.next());
            }
        }

        // registers a predefined standard recipient for alerts
        setup.registerAlerts();

        // Load a list of cms tasks and associated alert events
        // Currently no functionality to persist them. Not a loader task yet
        // setup.loadTaskAlerts(s_conf.getTaskAlerts());

        section.save();  //persists any changes in the database (DomainObject)
        //i.e. creates an object (instance)

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

        if (ctDefFiles != null) {
            XMLContentTypeHandler handler = new XMLContentTypeHandler();
            Iterator i = ctDefFiles.iterator();
            while (i.hasNext()) {
                String xmlFile = (String) i.next();
                s_log.debug("Processing contentTypes in: " + xmlFile);
                XML.parseResource(xmlFile, handler);
            }

            Iterator iter = handler.getContentTypes().iterator();

            while (iter.hasNext()) {
                ContentType ct = (ContentType) iter.next();
                if (!ct.isInternal()) {
                    m_content_type_list.add(ct.getClassName());
                }
            }

        }

        s_log.debug("Done loading content type definitions.");
    }

    /**
     * Integrates the CMS privileges into the Core permision system.
     *
     * Skips through the CMS specific privileges and integrates those which are
     * missing in core's acs_privileges into it, so the systems security system
     * is aware of it.
     */
    private static void createPrivileges() {
        s_log.debug("Creating Privileges...");

        final String CMS_PRIVILEGES = "com.arsdigita.cms.getPrivileges";
        final String PRIVILEGE = "privilege";

        DataQuery dq = SessionManager.getSession().retrieveQuery(CMS_PRIVILEGES);
        try {
            while (dq.next()) {
                String privilege = (String) dq.get(PRIVILEGE);
                s_log.debug(String.format("privilege = %s", privilege));
                if (PrivilegeDescriptor.get(privilege) == null) {
                    PrivilegeDescriptor.createPrivilege(privilege);
                }
            }

        } finally {
            dq.close();
        }
        s_log.debug("Done creating Privileges.");
    }
}
