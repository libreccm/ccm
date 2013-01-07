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

import com.arsdigita.cms.contentsection.ContentSectionConfig;
import com.arsdigita.cms.contentsection.ContentSectionSetup;
import com.arsdigita.cms.contenttypes.XMLContentTypeHandler;
import com.arsdigita.cms.portlet.ContentDirectoryPortlet;
import com.arsdigita.cms.portlet.ContentItemPortlet;
import com.arsdigita.cms.portlet.ContentSectionsPortlet;
import com.arsdigita.cms.portlet.TaskPortlet;
import com.arsdigita.cms.util.Util;
import com.arsdigita.formbuilder.util.FormbuilderSetup;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.loader.PackageLoader;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.runtime.ScriptContext;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.StringArrayParameter;
import com.arsdigita.web.Application;
import com.arsdigita.web.ApplicationType;
import com.arsdigita.xml.XML;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * <p>Executes nonrecurring at install time and loads (installs and initializes)
 * the Content Management System module,including the Content Center, CMS
 * Service applications, and CMS Mime Types service persistently into
 * database.</p>
 *
 * <p>This class also optionally initializes user-defined content types. </p>
 * <p>Additional user-defined content sections can be loaded and initilized
 * using the recurring
 * <pre>initializer</pre> at any startup.
 *
 * <p>The tasks to perform are:</p> <ol> <li>create CMS package
 * type(content-section)</li> <li>create Workspace package type and
 * instance</li> <li>create CMS Service package type and instance</li>
 * <li>create CMS package (content-section) instance</li> </ol>
 *
 * <p>Configuration can be modified by configuration parameters before
 * processing, otherwise hardcoded default values take effect. After processing,
 * the installation values can not be modified anymore without a fresh
 * installation of the whole system.</p>
 * 
 * NOTE: Configuration parameters used at load time MUST be part of Loader 
 * class and can not delegated to a Config object (derived from AbstractConfig).
 * They will (and can) not be persisted into an registry object (file).
 *
 * @author Peter Boy &lt;pboy@barkhof.uni-bremen.de&gt;
 * @since ccm-cms version 6.6.0
 * @version $Id: Loader.java 2305 2012-05-01 12:26:33Z pboy $
 */
public class Loader extends PackageLoader {

    /**
     * Creates a s_logging category with name = full name of class
     */
    private static final Logger s_log = Logger.getLogger(Loader.class);
    /**
     * Loader configuration object, singleton design pattern NOTE: LoaderConfig
     * only supplies unmutable hard coded defaults! It is not possible to alter
     * any of the contained values by specifiying an configuration parameter
     * during load step. If a configuration value has to be configurable at load
     * time, the parameter must be relocated into this Loader class!
     */
    private static final LoaderConfig s_conf = LoaderConfig.getInstance();

    //  ///////////////////////////////////////////////////////////////////
    //  Configurable parameters during load step.
    //  ///////////////////////////////////////////////////////////////////
    /**
     * The name(s) of the content section(s). In case of more than one name the
     * first is treated as default section. Otherwise the section created is the
     * default section. More sections can always be created during a subsequent
     * system startup using initialization parameters.
     */
    private final Parameter m_contentSectionNames = new StringArrayParameter(
            "com.arsdigita.cms.loader.section_names",
            Parameter.REQUIRED,
            new String[]{"content"});
    //  ///////////////////////////////////////////////////////////////////
    //  Configurable parameters during load step END.
    //  ///////////////////////////////////////////////////////////////////

    /**
     * List of classnames of internal base content types (needed in every
     * section created), generated while loading those content types in
     * loadContentTypeDefinitions(files) for later use in register step.
     */
    private ArrayList m_content_type_list = new ArrayList();

    /**
     * Standard constructor.
     */
    public Loader() {
        s_log.debug("CMS.loader (Constructor) invoked");

        register(m_contentSectionNames);

        s_log.debug("CMS.loader (Constructor) completed");
    }

    /**
     *
     */
    public void run(final ScriptContext ctx) {
        s_log.debug("CMS.loader.run() invoked");

        new KernelExcursion() {

            public void excurse() {
                setEffectiveParty(Kernel.getSystemParty());

                // Step 1) Setup the CMS Workspace
                ApplicationType appType = loadWorkspaceApplicationType();
                setupDefaultWorkspaceApplicationInstance(appType);

                // Step 2) Setup the CMS global services
                appType = loadServiceApplicationType();
                setupDefaultServiceApplicationInstance(appType);

                // Step 3) load (cms internal) content type definition(s)
                loadContentTypeDefinitions(s_conf.getCTDefFiles());

                // Step 4) Load CMS content section 
                // Loads content section application type and instance in one step
                loadContentSection((String[]) get(m_contentSectionNames));

                // Step 5) Loading CMS portlets
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

    /**
     * Loads the Workspace subpackage (content-center) into the database.
     *
     * It is made public to be able to invoke it from the update script (e.g.
     * 6.6.1-6.6.2). We need separate steps for loading and instantiating
     * because update skript requires.
     */
    public static ApplicationType loadWorkspaceApplicationType() {
        s_log.debug("Creating CMS Workspace...");

        /*
         * Create new type legacy free application type NOTE: The wording in the
         * title parameter of ApplicationType determines the name of the
         * subdirectory for the XSL stylesheets. It gets "urlized", i.e.
         * trimming leading and trailing blanks and replacing blanks between
         * words and illegal characters with an hyphen and converted to lower
         * case. "Content Center" will become "content-center".
         */
        ApplicationType type = new ApplicationType(Workspace.INSTANCE_NAME,
                Workspace.BASE_DATA_OBJECT_TYPE);

        type.setDescription("The content center workspace for content creators.");
        type.save();

        s_log.debug("CMS Workspace type created.");
        return type;
    }

    /**
     * Instantiates the Workspace subpackage (content-center) (in the database).
     *
     * It is made public to be able to invoke it from the update script (e.g.
     * 6.6.1-6.6.2). We need separate steps for loading and instantiating
     * because update skript requires.
     *
     * @param workspaceType
     */
    public static void setupDefaultWorkspaceApplicationInstance(
            ApplicationType workspaceType) {

        // create application instance 
        // Whether a legacy compatible or a legacy free application is
        // created depends on the type of ApplicationType above. No need to
        // modify anything here in the migration process
        // old-style package key used as url fragment where to install the instance
        s_log.debug("Creating CMS Workspace instance ...");
        Workspace app = (Workspace) Application.createApplication(
                Workspace.BASE_DATA_OBJECT_TYPE, // type
                Workspace.PACKAGE_KEY, // url fragment
                Workspace.INSTANCE_NAME, // title
                null);                      // parent
        app.setDescription("The default CMS workspace instance.");
        app.save();

        s_log.debug("CMS Workspace instance " + Workspace.PACKAGE_KEY + " created.");
        s_log.debug("Done loading CMS Workspace.");
    }

    /**
     * CMS Service application is used by the Content Management System as a
     * store for global resources and assets. It is made public to be able to
     * invoke it from the update script (e.g. 6.6.1-6.6.2).
     */
    public static ApplicationType loadServiceApplicationType() {
        s_log.debug("Loading CMS Servce Package...");

        /*
         * Create new type legacy free application type NOTE: The wording in the
         * title parameter of ApplicationType determines the name of the
         * subdirectory for the XSL stylesheets. It gets "urlized", i.e.
         * trimming leading and trailing blanks and replacing blanks between
         * words and illegal characters with an hyphen and converted to lower
         * case. "CMS Service" will become "cms-service".
         */
        ApplicationType type = new ApplicationType("CMS Service",
                Service.BASE_DATA_OBJECT_TYPE);
        type.setDescription("Services to store global resources and assets.");
        type.save();

        return type;
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
                Service.PRIMARY_URL_STUB, // url fragment
                "CMS Service Instance", // title
                null);                            // parent
        app.setDescription("The default CMS service instance.");
        app.save();

        s_log.debug("Done creating CMS Service Package.");
    }

    /**
     * Load a content section application type and an initial default
     * content-section instance(s). Some configuration values which are to be
     * considered as unmutable are specified in LoaderConfig. Uses new style
     * application in legacy compatible mode.
     *
     * NOTE: At ccm-cms load time no content type packages are available because
     * any content type depends on ccm-cms. Therefore, the loading step can not
     * process content type package assignment! Instead each content type itself
     * must assign itself to an appropriate content section at it's load time.
     * Cf. {@link com.arsdigita.cms.contenttype.AbstractContentTypeLoader}.
     *
     * But the load step has to process the cms internal content types!
     */
    private void loadContentSection(String[] sectionNames) {

        // Step 1: Create content section application type
        //         prerequisite for concrete content-section instance creation.

        /*
         * Create legacy-free application type NOTE: The wording in the title
         * parameter of ApplicationType determines the name of the subdirectory
         * for the XSL stylesheets. It gets "urlized", i.e. trimming leading and
         * trailing blanks and replacing blanks between words and illegal
         * characters with an hyphen and converted to lower case. "Content
         * Section" will become "content-section".
         */
        ApplicationType type = new ApplicationType(
                "Content Section",
                ContentSection.BASE_DATA_OBJECT_TYPE);
        type.setDescription("The CMS Content Section application.");
        type.save();

        // Step 2: Load CMS specific privileges into central (core) privilege
        // system.
        createPrivileges();

        // Step 3: Create the installation default content section(s).
        // ContentSection.create creates a section with several default values
        // which have to be adopted for a concrete installation.
        for (int i = 0; i < sectionNames.length; i++) {

            final String sectionName = sectionNames[i];
            s_log.debug("Creating content section on /" + sectionName);

            // Step 1: Validate name for section
            Util.validateURLParameter("name", sectionName);

            ContentSectionConfig conf = new ContentSectionConfig();
            conf.load();
            
            ContentSectionSetup.setupContentSectionAppInstance(sectionName,
                                                               conf.getDefaultRoles(),
                                                               conf.getDefaultWorkflows(),
                                                               s_conf.isPubliclyViewable(),
                                                               s_conf.getItemResolverClass(),
                                                               s_conf.getTemplateResolverClass(),
                                                               m_content_type_list,
                                                               s_conf.getUseSectionCategories(),
                                                               s_conf.getCategoryFileList());
                    
        }

    }

    /**
     * Parses XML file definition of (internal) base content types and loads
     * them into database. It fulfills a task similiar to the Loader of external
     * content type packages.
     *
     * The XML config looks like the example below, the "parentType" and "name"
     * attributes are optional, and only required for creating User Defined
     * ContentTypes. Label corresponds to ContentType's label and can be
     * multiple words, and "name" to DynamicObject's name property, and must be
     * a single word. The objectType attribute is ignored for UDCTs, as it gets
     * dynamically generated.
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
     * </pre>
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
