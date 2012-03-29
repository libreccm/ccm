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

package com.arsdigita.portalserver;


import com.arsdigita.domain.*;
import com.arsdigita.kernel.ACSObjectInstantiator;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.kernel.ResourceSetup;
import com.arsdigita.loader.*;
import com.arsdigita.persistence.DataObject;

import com.arsdigita.portal.AgentPortlet;
import com.arsdigita.portal.PortletSetup;
import com.arsdigita.portal.PortletType;
import com.arsdigita.portal.apportlet.AppPortletSetup;

import com.arsdigita.portalserver.admin.PSAdmin;
import com.arsdigita.portalserver.personal.MyPortalsPortlet;
import com.arsdigita.portalserver.personal.PersonalPortal;
import com.arsdigita.portalserver.personal.PersonalPortalCreator;
import com.arsdigita.portalserver.ui.admin.PortalCreator;
import com.arsdigita.portalserver.ui.admin.PortalSiteMap;

import com.arsdigita.runtime.*;
import com.arsdigita.web.Application;
import com.arsdigita.web.ApplicationType;

//  USED BY loadSubsite() which es meant to replace the applications mounted
//  at root url dispatcher by portalserver dispatcher. Not used anymore, the
//  page at root address is now determined in a different way.
//import com.arsdigita.kernel.PackageType;
//import com.arsdigita.kernel.PackageInstance;
//import com.arsdigita.kernel.SiteNode;

import org.apache.log4j.Logger;

/**
 * Portal Server Loader
 *
 * @author Jim Parsons &lt;jparsons@redhat.com&gt;
 * @version $Revision: #2 $ $Date: 2004/08/17 $
 **/

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

                loadPortalSiteApplicationType();
                buildDefaultThemes();
                setupAdminPortal();
                loadPortalTabApplicationType();   //formerly ResourceType rtype = setupResource();
                loadSubPortalTabApplicationType(); // formerly ResourceType restype = setupSubPortal();

                loadPortalSiteAdminApplicationType();
                setupPortalSiteAdminInstance();

                // formerly from  ui.admin.Initializer
                loadPortalCreatorApplicationType();
                loadPortalSitemapApplicationType();
                setupPortalCreatorInstance();
                setupPortalSitemapInstance();

                // formerly from  personal.Initializer
                loadPersonalPortalCreatorApplicationType();
                setupPersonalPortalCreatorInstance();

                loadPersonalPortalApplicationType();
                setupMyPortalsPortlet();

                // Loading internal portlets
                setupAgentPortlet();
                setupApplicationDirectoryPortlet();
                setupPortalNavigatorPortlet();
                setupPortalSummaryPortlet();
  
     //         loadSubsite();
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
     * Creates a PortalSite, one of the domain classes of the portalserver
     * package, ApplicationType as a legacy-free type of application type.
     * 
     * NOTE: The wording in the title parameter of ApplicationType determines
     * the name of the subdirectory for the XSL stylesheets.
     * It gets "urlized", i.e. trimming leading and trailing blanks and replacing
     * blanks between words and illegal characters with an hyphen and converted
     * to lower case.
     * Example: "Portal Server" will become "portal-server".
     *
     * Creates an entry in table application_types and a corresponding entry in
     * apm_package_types
     */
    private void loadPortalSiteApplicationType() {

        /* First try: create a new style, legacy compatible application      */

    //  ApplicationType type = ApplicationType.createApplicationType(
    //                                        "portalsite",
    //                                        "Portal Site",
    //                                        PortalSite.BASE_DATA_OBJECT_TYPE);
        // Current code requires an apps specific dispatcher class. Has to be
        // modified to be able to create a legacy free app type.
    //  type.setDispatcherClass
    //          ("com.arsdigita.portalserver.ui.PortalDispatcher");

        // Try: new style legacy free application
        ApplicationType type = new 
                               ApplicationType("Portal Site",  // title
                                               PortalSite.BASE_DATA_OBJECT_TYPE );

        type.setDescription
            ("A Portal Site is a center for content aggregation. It usually" +
             "has its own set of applications, such as a discussion forum " +
             "and content items, as well as a list of participants.");
        type.save();
    }
    
    /**
     *
     * @return
     */
    private void loadPortalTabApplicationType() {
    //  formerly private void setupResource() in OldInitializer {
        // legt an in application_types object_type: c.ad.workspace.WorkspaceTab
        // nichts in applications,
        // nichts in apm_package_types, ps_*, acs_objects

        ResourceSetup setup = new ResourceSetup(s_log);

        setup.setTitle("PortalTab");
        setup.setResourceObjectType(PortalTab.BASE_DATA_OBJECT_TYPE);
        setup.setDescription("A Portal Tab!");

        setup.setInstantiator(new ACSObjectInstantiator() {
              @Override
              public DomainObject doNewInstance(DataObject dataObject) {
                 return new PortalTab(dataObject);
              }
        });
        setup.run();

    }


    /**
     *
     * @return
     */
    private void  loadSubPortalTabApplicationType() {
    // formerly private void  setupSubPortalTab() { in OldInitializer
        // legt an in application_types object_type: c.ad.workspace.SubWorkspaceTab
        // ohne Eintrag in package_type_id
        // nichts in applications,
        // nichts in apm_package_types, ps_*, acs_objects

        ResourceSetup setup = new ResourceSetup(s_log);

        setup.setTitle("SubPortalTab");
        setup.setResourceObjectType(SubPortalTab.BASE_DATA_OBJECT_TYPE);
        setup.setDescription("A SubPortal Tab!");

        setup.setInstantiator(new ACSObjectInstantiator() {
              @Override
              public DomainObject doNewInstance(DataObject dataObject) {
                  return new SubPortalTab(dataObject);
              }
        });
        setup.run();

    }

    /*    FORMERLY        admin.Initializer                                 */

    /**
     *
     */
    // replaced formerly startup() meothod in admin.Initializer
    private void loadPortalSiteAdminApplicationType() {

        // erstellt Eintrag in apm_package_types (key portal-admin mit eigenem
        // dispatcher_class, nciht .... JSPApplicationDispatcher), Eintrag in
        // apm_packages, in application_tyxpes mit object_type = c.ad.admin.CWAdmin
        // (statt Klassenname PSAdmin, liegt an Eintrag für BASE_DATA_OBJECT_TYPE),
        // in applications mit primary_url=/portal-admin/
        // in acs_object mit default_domain_class=c.ad.ps.admin.PSAdmin und
        // object_type=c.ad.admin.CWAdmin, ebenfalls wg. BASE_DATA_OBJ_TYPE

    //  ApplicationType type = ApplicationType.createApplicationType(
    //                                        "portal-admin",
    //                                        "Portal Server Site Administration",
    //                                        PSAdmin.BASE_DATA_OBJECT_TYPE);
    //  // Current code requires an apps specific dispatcher class. Has to be
    //  // modified to be able to create a legacy free app type.
    //  type.setDispatcherClass
    //          ("com.arsdigita.portalserver.admin.ui.Dispatcher");

        // Try: new style legacy free application
        ApplicationType type = new 
                               ApplicationType("Portal Admin",  // title
                                               PSAdmin.BASE_DATA_OBJECT_TYPE );

        type.setDescription ("Displays common administration tasks.");
        type.save();
    }


    //    FORMERLY        ui.admin.Initializer
    /**
     * Creates a PortalCreator, another of the domain classes of the portalserver
     * package, ApplicationType as a legacy-comp type of application type.
     *
     */
    private void loadPortalCreatorApplicationType() {
    // formerly private ApplicationType setupPortalCreator() { in ui.admin.Initializer


    //  ApplicationType type = ApplicationType.createApplicationType(
    //                                        "portal-create",
    //                                        "Portal Creator",
    //                                        PortalCreator.BASE_DATA_OBJECT_TYPE);
    //  type.setDescription ("Creates portals.");
    //  // Current code requires an apps specific dispatcher class. Has to be
    //  // modified to be able to create a legacy free app type.
    //  type.setDispatcherClass
    //          ("com.arsdigita.portalserver.ui.admin.PortalCreateDispatcher");


        // Try: new style legacy free application
        ApplicationType type = new 
                               ApplicationType("Portal Creator",  // title
                                               PortalCreator.BASE_DATA_OBJECT_TYPE );

        type.setDescription ("Creates portals.");
        type.save();

    }


   /**
    *
    * @return
    */
   private void loadPortalSitemapApplicationType() {
   // formerly private ApplicationType setupPortalSitemapper() {

/*
        ApplicationType type = ApplicationType.createApplicationType(
                                              "portal-sitemap",
                                              "Portal Site Map",
                                              PortalSiteMap.BASE_DATA_OBJECT_TYPE);
        // Current code requires an apps specific dispatcher class. Has to be
        // modified to be able to create a legacy free app type.
        type.setDispatcherClass
                ("com.arsdigita.portalserver.ui.admin.PortalSiteMapDispatcher");

*/
/*        ApplicationSetup setup = new ApplicationSetup(s_log);
        setup.setApplicationObjectType(PortalSiteMap.BASE_DATA_OBJECT_TYPE);
        setup.setTitle("Portal Site Map");
        setup.setDescription("Displays and manages Portals");
        setup.setWorkspaceApplication(false);
        setup.setKey("portal-sitemap");
//      setup.setStylesheet("/packages/portalserver/xsl/portal-sitemap.xsl");
        setup.setDispatcherClass(
                 "com.arsdigita.portalserver.ui.admin.PortalSiteMapDispatcher");
        setup.setInstantiator(new ACSObjectInstantiator() {
                @Override
                protected DomainObject doNewInstance(DataObject dataObject) {
                    return new PortalSiteMap(dataObject);
                }
            });

        setup.run();   */
        // return setup.run();

        // Try: new style legacy free application
        ApplicationType type = new 
                               ApplicationType("Portal SiteMap",  // title
                                               PortalSiteMap.BASE_DATA_OBJECT_TYPE );

        type.setDescription ("Displays and manages Portals.");
        type.save();

   }


    /*    FORMERLY        personal.Initializer                                 */


    /**
     *
     * @return
     */
    private void loadPersonalPortalCreatorApplicationType() {
 // private ApplicationType loadPersonalPortalCreatorApplicationType() {
    // formerly private ApplicationType setupPersonalPortalCreatorApplication() {
/*
        ApplicationType type = ApplicationType.createApplicationType(
                                              "personal-portal-creator",
                                              "Personal Portal Creator",
                                              PersonalPortalCreator.BASE_DATA_OBJECT_TYPE);
        type.setDescription ("Responsible for creating personal portals.");
        // Current code requires an apps specific dispatcher class. Has to be
        // modified to be able to create a legacy free app type.
        type.setDispatcherClass
                ("com.arsdigita.portalserver.personal.PersonalPortalCreatorDispatcher");
*/

        // Try: new style legacy free application
        ApplicationType type = new 
                               ApplicationType("Personal Portal Creator",  // title
                                               PersonalPortalCreator.BASE_DATA_OBJECT_TYPE );

        type.setDescription ("Displays and manages Portals.");
        type.save();

     }


    private void loadPersonalPortalApplicationType() {
 // private ApplicationType loadPersonalPortalApplicationType() {
    // formerly private ApplicationType setupPersonalPortalApplication() {
/*
        ApplicationType type = ApplicationType.createApplicationType(
                                              "personal-portal",
                                              "Personal Portal",
                                              PersonalPortal.BASE_DATA_OBJECT_TYPE);
        // Current code requires an apps specific dispatcher class. Has to be
        // modified to be able to create a legacy free app type.
        type.setDispatcherClass
                ("com.arsdigita.portalserver.personal.PersonalPortalDispatcher");
*/
        // Try: new style legacy free application
        ApplicationType type = new 
                               ApplicationType("Personal Portal",  // title
                                               PersonalPortal.BASE_DATA_OBJECT_TYPE );

        type.setDescription ("A portal for an individual.");
        type.save();

/*      // XXX This must go after setup.run().  Need more elegant approach.
        // pboy: uses ResourceType.registerResourceTypeConfig() which registers
        // in a hash map. Therefore: not suitable for a loader, may have to be
        // moved into initializer!
        // new PersonalPortalConfig();

        return type;  */
    }


    // ////////////////////////////////////////////////////////////////////////
    //
    //        S e t u p    o f   P O R T A L   a p p l i c a t i o n s
    //
    // ////////////////////////////////////////////////////////////////////////


    /**
     * Instantiate an application of type PortalSite with site wide administration
     * tools.
     */
    private void setupAdminPortal() {

        PortalSite ps = PortalSite.createPortalSite("administration", 
                                                    "Administration", 
                                                    null);
        ps.setMission("Administration Portal");
        ps.save();

    }

    /**
     * Setup an PortalServer Administration Instance as legacy compatible
     * application.
     *
     * @param type
     */
    // private void setupPortalSiteAdminInstance(final ApplicationType type) {
    private void setupPortalSiteAdminInstance() {

        if (!Application.isInstalled(PSAdmin.BASE_DATA_OBJECT_TYPE,
                                     "/portal-admin/")) {
            s_log.info("There is no Portal Admin application instance on " +
                       "/portal-admin/.  Installing now.");

    //      KernelExcursion ex = new KernelExcursion() {
    //          protected void excurse() {
    //              setParty(Kernel.getSystemParty());
                    Application app = Application.createApplication
                        (PSAdmin.BASE_DATA_OBJECT_TYPE,
                         "portal-admin", "Site Administration", null);

                    app.save();
    //          }
    //      };
    //      ex.run();

            s_log.info("Done installing Portal Admin on /portal-admin/.");
        }
    }


    /*    FORMERLY        ui.admin.Initializer                                 */

    private void setupPortalCreatorInstance() {
    // formerly private void setupPortalCreate(ApplicationType type) {

        if (!Application.isInstalled(PortalCreator.BASE_DATA_OBJECT_TYPE,
                                     "/portal-admin/portal-create/")) {
            Application admin =
                Application.retrieveApplicationForPath("/portal-admin/");

            if (admin == null) {
                s_log.warn("There is no application at /portal-admin/ so I " +
                           "can't install the portal create application.");
                return;
            }

            s_log.info("There is no Portal Creator application instance " +
                       "on /user-profile/.  Installing now.");

            Application app = Application.createApplication
                (PortalCreator.BASE_DATA_OBJECT_TYPE,
                 "portal-create", "Create Top-Level Portals", admin);
            app.setDescription("Create top-level portals.");

            app.save();

            s_log.info("Done installing Portal Creator on " +
                       "/portal-admin/portal-create/.");
        }

    }

    private void setupPortalSitemapInstance() {
        // private void setupPortalSitemapInstance(ApplicationType type) {
        
        if (!Application.isInstalled(PortalSiteMap.BASE_DATA_OBJECT_TYPE,
                                     "/portal-admin/portal-sitemap/")) {
            Application admin =
                Application.retrieveApplicationForPath("/portal-admin/");

            if (admin == null) {
                s_log.warn("There is no application at /portal-admin/ so I " +
                           "can't install the portal sitemap application.");
                return;
            }

            s_log.info("There is no Portal Site Map application instance " +
                       "on /portal-admin/.  Installing now.");

            Application app = Application.createApplication
            //  (type, "portal-sitemap", "Portal Site Map", admin);
                (PortalSiteMap.BASE_DATA_OBJECT_TYPE,
                 "portal-sitemap", "Portal Site Map", admin);
            app.setDescription("Portal Site Map");

            app.save();

            s_log.info("Done installing Portal Site Map on " +
                       "/portal-admin/portal-sitemap/.");
        }
    }



    /*    FORMERLY        personal.Initializer                                 */


    private void setupPersonalPortalCreatorInstance() {

        // Create an instance of the personal portal creator at
        // /personal-portal/.

        boolean creatorInstalled = Application.isInstalled
            (PersonalPortalCreator.BASE_DATA_OBJECT_TYPE,
             "/personal-portal/");

        if (!creatorInstalled) {

       // No KernelExcursion needed here because Loader already has it.
       //   KernelExcursion ex = new KernelExcursion() {
       //       protected void excurse() {
       //           setParty(Kernel.getSystemParty());
       // creates either a legacy free application or a legacy compatible,
       // depending of its application type.
                    Application creatorApp = Application.createApplication
                        (PersonalPortalCreator.BASE_DATA_OBJECT_TYPE,
                         "personal-portal", "Personal Portal Creator", null);
                    creatorApp.save();
       //       }
       //   };
       //   ex.run();


        }
    }




    // ////////////////////////////////////////////////////////////////////////
    //
    //       S e t u p    o f   i n t e r n a l   p o r t l e t s
    //
    // ////////////////////////////////////////////////////////////////////////

    /**
     * Creates a PortletType (persistent object) for AgentPortlet.
     * Instances (Portlets) are created by user interface or programmatically
     * by configuration.
     */
    private void setupAgentPortlet() {
        PortletSetup setup = new PortletSetup(s_log);

        setup.setPortletObjectType
            (AgentPortlet.BASE_DATA_OBJECT_TYPE);
        setup.setTitle("Agent");
        setup.setDescription
            ("Acts as agent or proxy for another portlet.");
        setup.setProfile(PortletType.WIDE_PROFILE);
        setup.setInstantiator(new ACSObjectInstantiator() {
            @Override
                public DomainObject doNewInstance(DataObject dataObject) {
                    return new AgentPortlet(dataObject);
                }
            });

        setup.run();
    }

    /**
     * Creates a PortletType (persistent object) for ApplicationDirectoryPortlet.
     * Instances (Portlets) are created by user interface or programmatically
     * by configuration.
     */
    private void setupApplicationDirectoryPortlet() {
        AppPortletSetup setup =
               new AppPortletSetup(s_log);

        setup.setPortletObjectType
            (ApplicationDirectoryPortlet.BASE_DATA_OBJECT_TYPE);
        setup.setTitle("Application Directory");
        setup.setDescription("Provides links to the workspace's applications.");
        setup.setProfile(PortletType.NARROW_PROFILE);
        setup.setPortalApplication(true);
        setup.setInstantiator(new ACSObjectInstantiator() {
                @Override
                public DomainObject doNewInstance(DataObject dataObject) {
                    return new ApplicationDirectoryPortlet(dataObject);
                }
            });

        setup.run();
    }

    /**
     *
     */
    private void setupMyPortalsPortlet() {
        AppPortletSetup setup = new AppPortletSetup(s_log);

        setup.setPortletObjectType
            (MyPortalsPortlet.BASE_DATA_OBJECT_TYPE);
        setup.setTitle("My Portals");
        setup.setDescription
            ("Displays links to portals to which you belong.");
        setup.setProfile(PortletType.NARROW_PROFILE);
        setup.setPortalApplication(true);
        setup.setInstantiator(new ACSObjectInstantiator() {
                @Override
                public DomainObject doNewInstance(DataObject dataObject) {
                    return new MyPortalsPortlet(dataObject);
                }
            });

        setup.run();
    }


    /**
     * Creates a PortletType (persistent object) for PortalNavigatorPortlet.
     * Instances (Portlets) are created by user interface or programmatically
     * by configuration.
     */
    private void setupPortalNavigatorPortlet() {

        // Create a PortletType for PortalNavigatorPortlet
        AppPortletSetup setup =
               new AppPortletSetup(s_log);

        setup.setPortletObjectType
            (PortalNavigatorPortlet.BASE_DATA_OBJECT_TYPE);
        setup.setTitle("Portal Navigator");
        setup.setDescription
            ("Provides links to portal associated with the current " +
             "portal.");
        setup.setProfile(PortletType.NARROW_PROFILE);
        setup.setPortalApplication(true);
        setup.setInstantiator(new ACSObjectInstantiator() {
                @Override
                public DomainObject doNewInstance(DataObject dataObject) {
                    return new PortalNavigatorPortlet(dataObject);
                }
            });

        setup.run();
    }

    /**
     * Creates a PortletType (persistent object) for PortalSummaryPortlet.
     * Instances (Portlets) are created by user interface or programmatically
     * by configuration.
     */
    private void setupPortalSummaryPortlet() {
        AppPortletSetup setup = new AppPortletSetup(s_log);

        setup.setPortletObjectType
            (PortalSummaryPortlet.BASE_DATA_OBJECT_TYPE);
        setup.setTitle("Portal Summary");
        setup.setProfile(PortletType.NARROW_PROFILE);
        setup.setPortalApplication(true);
        setup.setInstantiator(new ACSObjectInstantiator() {
                @Override
                public DomainObject doNewInstance(DataObject dataObject) {
                    return new PortalSummaryPortlet(dataObject);
                }
            });

        setup.run();
    }



    // ////////////////////////////////////////////////////////////////////////
    //
    //
    // ////////////////////////////////////////////////////////////////////////

    /**
     * Determines the ApplicationType mounted at the root node (as a package type)
     * and replaces its dispatcher class with its own.
     * This dispatcher class sets the class which is used for login. The class is
     * persisted into database.
     * 
     * Root URL request will be redirected to the users portal page (or login) 
     * 
     * @ deprecated the method used by the dispatcher in its current version is
     * no longer available. Currently without direct replacement. The current
     * version of core now uses a different method to determine the page at
     * root address. So this method is of no use anymore!
     * Whether it is used or not makes no difference.
     */
//  USED BY loadSubsite() which es meant to replace the applications mounted
//  at root url dispatcher by portalserver dispatcher. Not used anymore, the
//  page at root address is now determined in a different way.
/*
    private void loadSubsite() {
   //   String stylesheetName = "";
        String sDispatcher = "";

        SiteNode rootNode = SiteNode.getRootSiteNode();

        PackageInstance packageInstance = rootNode.getPackageInstance();
        if (packageInstance == null) {
            throw new IllegalStateException
                ("No package instance mounted at the root node");
        }
        PackageType subsite = packageInstance.getType();

        // getType() returns a disconnected object.  To get a connected object
        // we do a findByKey(key).
        String packageKey = subsite.getKey();
        try {
            subsite = PackageType.findByKey(packageKey);
        } catch (DataObjectNotFoundException e) {
            throw new IllegalStateException
                ("Package Type with key \"" + packageKey + "\" was not found.\n");
        }


        // Set subsite dispatcher class.
        subsite.setDispatcherClass(
                     "com.arsdigita.portalserver.pslogin.PSSubsiteDispatcher");
    }
*/
        
    /**
     * PortalSite may have its own styling elements (portal themes). Loads some
     * default styles which are available in a drop down menue for a user.
     */
    private void buildDefaultThemes() {

    Theme theme0 = new Theme("Red Hat");
    theme0.setDescription("A cool corporate theme");
    theme0.setContextBarColor("#E1E1E1");
    theme0.setContextBarTextColor("#3F3F3F");
    theme0.setActiveTabColor("#a21e1e");
    theme0.setInactiveTabColor("#dddddd");
    theme0.setInactiveTabTextColor("#555555");
    theme0.setActiveTabTextColor("#ffffff");
    theme0.setTopRuleColor("#a21e1e");
    theme0.setBottomRuleColor("#a21e1e");
    theme0.setPortletHeaderColor("#a21e1e");
    theme0.setPortletIconColor("#a21e1e");
    theme0.setPortletHeaderTextColor("#ffffff");
    theme0.setPageBGColor("#ffffff");
    theme0.setBodyTextColor("#000000");
    theme0.setNarrowBGColor("#dddddd");
    theme0.save();
/*
    Theme theme1 = new Theme("Celtic Fever");
    theme1.setDescription("A theme for Celtic NBA fans");
    theme1.setContextBarColor("#008800");
    theme1.setActiveTabColor("#008800");
    theme1.setInactiveTabColor("#002200");
    theme1.setInactiveTabTextColor("#ffffff");
    theme1.setActiveTabTextColor("#ffffff");
    theme1.setTopRuleColor("#008800");
    theme1.setBottomRuleColor("#008800");
    theme1.setPortletHeaderColor("#006600");
    theme1.setPortletIconColor("#006600");
    theme1.setPortletHeaderTextColor("#000000");
    theme1.setPageBGColor("#ffffff");
    theme1.setPageBGImage("/assets/cw/workspace/bg_bos.gif");
    theme1.setBodyTextColor("#000000");
    theme1.setNarrowBGColor("#ddeedd");
    theme1.save();
*/
    Theme theme2 = new Theme("Bright Day");
    theme2.setDescription("A sunny, cloudless sky");
    theme2.setContextBarColor("#003366");
    theme2.setContextBarTextColor("#FFFFFF");
    theme2.setActiveTabColor("#93bee2");
    theme2.setInactiveTabColor("#d8e8f5");
    theme2.setInactiveTabTextColor("#000000");
    theme2.setActiveTabTextColor("#000000");
    theme2.setTopRuleColor("#93bee2");
    theme2.setBottomRuleColor("#93bee2");
    theme2.setPortletHeaderColor("#cccccc");
    theme2.setPortletIconColor("#cccccc");
    theme2.setPortletHeaderTextColor("#000000");
    theme2.setPageBGColor("#ffffff");
    theme2.setPageBGImage("");
    theme2.setNarrowBGColor("#dddddd");
    theme2.setBodyTextColor("#000000");

    theme2.save();

    Theme theme3 = new Theme("Harvest");
    theme3.setDescription("High Heat");
    theme3.setContextBarColor("#ff9966");
    theme3.setContextBarTextColor("#ffffff");
    theme3.setActiveTabColor("#ff9966");
    theme3.setInactiveTabColor("#ffcc99");
    theme3.setInactiveTabTextColor("#000000");
    theme3.setActiveTabTextColor("#000000");
    theme3.setTopRuleColor("#ff9966");
    theme3.setBottomRuleColor("#ff9966");
    theme3.setPortletHeaderColor("#ff9966");
    theme3.setPortletIconColor("#ff9966");
    theme3.setPortletHeaderTextColor("#000000");
    theme3.setPageBGColor("#ffffff");
    theme3.setPageBGImage("");
    theme3.setNarrowBGColor("#ffffcc");
    theme3.setBodyTextColor("#000000");

    theme3.save();

    Theme theme4 = new Theme("Desert");
    theme4.setDescription("High Heat");
    theme4.setContextBarColor("#cccc99");
    theme4.setContextBarTextColor("#FFFFFF");
    theme4.setActiveTabColor("#cccc99");
    theme4.setInactiveTabColor("#ededca");
    theme4.setInactiveTabTextColor("#000000");
    theme4.setActiveTabTextColor("#000000");
    theme4.setTopRuleColor("#cccc99");
    theme4.setBottomRuleColor("#cccc99");
    theme4.setPortletHeaderColor("#cccc99");
    theme4.setPortletIconColor("#cccc99");
    theme4.setPortletHeaderTextColor("#000000");
    theme4.setPageBGColor("#ffffff");
    theme4.setPageBGImage("");
    theme4.setNarrowBGColor("#ffffcc");
    theme4.setBodyTextColor("#000000");

    theme4.save();

    Theme theme5 = new Theme("Stars and Bars");
    theme5.setDescription("Patriotic");
    theme5.setContextBarColor("#cc0000");
    theme5.setContextBarTextColor("#FFFFFF");
    theme5.setActiveTabColor("#cc0000");
    theme5.setInactiveTabColor("#ffffff");
    theme5.setInactiveTabTextColor("#000000");
    theme5.setActiveTabTextColor("#ffffff");
    theme5.setTopRuleColor("#cc0000");
    theme5.setBottomRuleColor("#cc0000");
    theme5.setPortletHeaderColor("#cc0000");
    theme5.setPortletIconColor("#cc0000");
    theme5.setPortletHeaderTextColor("#ffffff");
    theme5.setPageBGColor("#ffffff");
    theme5.setPageBGImage("/assets/cw/backgrounds/stars.gif");
    theme5.setNarrowBGColor("#eeeeee");
    theme5.setBodyTextColor("#000000");

    theme5.save();

    }

}
