/*
 * Copyright (C) 2012 Peter Boy <pb@zes.uni-bremen.de> All Rights Reserved.
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
package com.arsdigita.cms.docmgr;

import com.arsdigita.cms.docmgr.ui.CategoryDocsNavigatorPortlet;
import com.arsdigita.cms.docmgr.ui.LegacyCategoryDocsNavigatorPortlet;
import com.arsdigita.cms.docmgr.ui.RecentUpdatedDocsPortlet;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.kernel.ACSObjectInstantiator;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.loader.PackageLoader;
// import com.arsdigita.mimetypes.*;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.portal.PortletType;
import com.arsdigita.portal.apportlet.AppPortletSetup;
import com.arsdigita.portal.apportlet.AppPortletType;
import com.arsdigita.runtime.ScriptContext;
import com.arsdigita.web.ApplicationSetup;
import com.arsdigita.web.ApplicationType;

import org.apache.log4j.Logger;


//  ///////////////////////////////////////////////////////////////////////////
//  Project: Migrate to new style legacy free type of application.
//
//  Step 1: Copy from Initializer all data base / applicationtype
//          related code to LOADER and use Loader for data loading.
//
//  Step 2: Remove usage of ApplicationSetup and switch to legacy
//          compativle AppType xxx = new AppType.create(......)
//          Move setInstantiator back to Initializer as required.
//
//  Step 3: Move to legacy free app type
//          (a) modify new App.Tpye.....
//          (b) create AppServlet from Dispatcher
//
//
//  TESTS:
//  (a) Try to instantiate an instance of each type and check the UI
//      produced by the dispatcher / servlet
//  (b) Instantiate the porlets and try to reproduce the behaviour
//      (probably reproduce the error showing up originally)
//
//
//  ///////////////////////////////////////////////////////////////////////////



/**
 * CMS Document Manager (DocMgr) Loader
 *
 * @author pboy &lt;pboy@barkhof.uni-bremen.de&gt;
 * @version $Id: Loader.java $
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

                loadDocRepositoryApplicationType();  //former setupDocs
                setupDocRepositoryPortlet(null);     //former setupDocManagerPortlet

                ApplicationType categoryBrowseDocsAppType = setupCategoryBrowsing();
                setupCategoryDocsPortlet(categoryBrowseDocsAppType);

                ApplicationType legacyCategoryBrowseDocsAppType = 
                                                  setupLegacyCategoryBrowsing();
                setupLegacyCategoryDocsPortlet(legacyCategoryBrowseDocsAppType);

                // de-activate search for now
                //SearchUtils.setSearcher
                //    (new com.arsdigita.cms.docmgr.search.IntermediaSearcher());

                setupDefaultDocRepository();  //new here!

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
     * COPY & PASTE, has to be adopted !!
     * Creates a document repository application type, the domain class of the
     * document repository (docrepo) package, as a legacy-compatible type of
     * application.
     *
     * Creates an entry in table application_types and a corresponding entry in
     * apm_package_types
     *
     * TODO: migrate to a new style, legacy free application type.
     */
    // formerly setupDocs()
    private ApplicationType loadDocRepositoryApplicationType() {

/*      ApplicationSetup setup = new ApplicationSetup(s_log);
        setup.setApplicationObjectType(Repository.BASE_DATA_OBJECT_TYPE);
        setup.setKey("cmsdocs");
        setup.setTitle("Document Manager (CMS) Application");
        setup.setSingleton(false);
        setup.setDescription
            ("The document manager empowers users to share documents.");
        setup.setDispatcherClass("com.arsdigita.cms.docmgr.ui.DMDispatcher");
        setup.setInstantiator(new ACSObjectInstantiator() {
                @Override
                public DomainObject doNewInstance(DataObject dataObject) {
                    return new Repository(dataObject);
                }
            });

        return setup.run();
*/
        /* Create new type legacy compatible application type               */
        ApplicationType type =  ApplicationType
                                .createApplicationType("cmsdocs",
                                                       "DocRepo",
                                                       Repository.BASE_DATA_OBJECT_TYPE);
        type.setDispatcherClass("com.arsdigita.cms.docmgr.ui.DMDispatcher");
        type.setDescription("The document manager empowers users to share documents.");


        /* Legacy free initialization                                  
     * NOTE: The wording in the title parameter of ApplicationType determines
     * the name of the subdirectory for the XSL stylesheets.
     * It gets "urlized", i.e. trimming leading and trailing blanks and replacing
     * blanks between words and illegal characters with an hyphen and converted
     * to lower case.
     * Example: "DocRepo" will become "docrepo".
         */
     // ApplicationType type =  new
     //                         ApplicationType("DocRepo",
     //                                         Repository.BASE_DATA_OBJECT_TYPE );

     // type.setDescription
     //     ("The document repository empowers users to share documents.");
        
        return type; 
    }

    private ApplicationType setupCategoryBrowsing() {
/*      ApplicationSetup setup = new ApplicationSetup(s_log);
        setup.setApplicationObjectType(DocumentCategoryBrowserApplication
                                       .BASE_DATA_OBJECT_TYPE);
        setup.setKey("cmsdocs-categories");
        setup.setTitle("Browse Documents Application");
        setup.setSingleton(true);
        setup.setDescription
            ("Browse documents by category.");
        setup.setDispatcherClass("com.arsdigita.cms.docmgr.ui.DCNDispatcher");
        setup.setInstantiator(new ACSObjectInstantiator() {
            @Override
                public DomainObject doNewInstance(DataObject dataObject) {
                    return new DocumentCategoryBrowserApplication(dataObject);
                }
            });
        return setup.run();
*/
        /* Create new type legacy compatible application type               */
        ApplicationType type =  ApplicationType
                                .createApplicationType("cmsdocs-categories",
                                                 "Browse Documents Application",
                       DocumentCategoryBrowserApplication.BASE_DATA_OBJECT_TYPE);
        type.setDispatcherClass("com.arsdigita.cms.docmgr.ui.DCNDispatcher");
        type.setDescription("Browse documents by category.");

        return type; 

    }
    private ApplicationType setupLegacyCategoryBrowsing() {
/*      ApplicationSetup setup = new ApplicationSetup(s_log);
        setup.setApplicationObjectType(LegacyCategoryBrowserApplication
                                       .BASE_DATA_OBJECT_TYPE);
        setup.setKey("cmsdocs-categories-legacy");
        setup.setTitle("Taxonomy Browser");
        setup.setSingleton(true);
        setup.setDescription
            ("Browse documents by category.");
        setup.setDispatcherClass("com.arsdigita.cms.docmgr.ui.DCNDispatcher");
        setup.setInstantiator(new ACSObjectInstantiator() {
            @Override
                public DomainObject doNewInstance(DataObject dataObject) {
                    return new LegacyCategoryBrowserApplication(dataObject);
                }
            });
        return setup.run();
*/
        /* Create new type legacy compatible application type               */
        ApplicationType type =  ApplicationType
                                .createApplicationType("cmsdocs-categories-legacy",
                                                            "Taxonomy Browser",
                       LegacyCategoryBrowserApplication.BASE_DATA_OBJECT_TYPE);
        type.setDispatcherClass("com.arsdigita.cms.docmgr.ui.DCNDispatcher");
        type.setDescription("Browse documents by category.");

        return type; 

    }



    // ////////////////////////////////////////////////////////////////////////
    //
    //        S e t u p    a   D O C M G R   a p p l i c a t i o n
    //
    // ////////////////////////////////////////////////////////////////////////
    private void setupDefaultDocRepository() {

    //  try {
    //      SiteNode sn = SiteNode.getSiteNode("/administration", false);
    //      if (!"administration".equals(sn.getName())) {
                Repository repo = Repository
                                  .create( "docrepo", 
                                           "Default DocumentMgr Repository", 
                                           null);
                repo.save();
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
    //former setupDocManagerPortlet
    private void setupDocRepositoryPortlet(ApplicationType provider) {

        // Create the document repository portlet
/*      AppPortletSetup setup = new AppPortletSetup(s_log);

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
		AppPortletType type = AppPortletType.createAppPortletType(
                                       "Portal Bookmarks",
                                       PortletType.NARROW_PROFILE,
                                       RecentUpdatedDocsPortlet.BASE_DATA_OBJECT_TYPE);
        // type.setProviderApplicationType(provider);
        type.setProviderApplicationType(Repository.BASE_DATA_OBJECT_TYPE);
        type.setDescription(
             "Displays the most recent documents in the document repository.");
        

    }

    private void setupCategoryDocsPortlet(ApplicationType provider) {

        // Create the document manager portlet
        AppPortletSetup setup = new AppPortletSetup(s_log);

        setup.setPortletObjectType(CategoryDocsNavigatorPortlet
                                   .BASE_DATA_OBJECT_TYPE);
        setup.setTitle("Document Category Navigator");
        setup.setDescription("Browse documents by category.");
        setup.setProfile(PortletType.WIDE_PROFILE);
        setup.setProviderApplicationType(provider);
        setup.setInstantiator(new ACSObjectInstantiator() {
            @Override
                protected DomainObject doNewInstance(DataObject dataObject) {
                    return new CategoryDocsNavigatorPortlet(dataObject);
                }
            });

        setup.run();

    }

    private void setupLegacyCategoryDocsPortlet(ApplicationType provider) {

        // Create the document manager portlet
        AppPortletSetup setup = new AppPortletSetup(s_log);

        setup.setPortletObjectType(LegacyCategoryDocsNavigatorPortlet
                                   .BASE_DATA_OBJECT_TYPE);
        setup.setTitle("Taxonomy Browser");
        setup.setDescription("Browse documents by category.");
        setup.setProfile(PortletType.WIDE_PROFILE);
        setup.setProviderApplicationType(provider);
        setup.setInstantiator(new ACSObjectInstantiator() {
            @Override
                protected DomainObject doNewInstance(DataObject dataObject) {
                    return new LegacyCategoryDocsNavigatorPortlet(dataObject);
                }
            });

        setup.run();

    }


}
