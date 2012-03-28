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
import com.arsdigita.persistence.DataObject;
import com.arsdigita.portal.PortletType;
import com.arsdigita.portal.apportlet.AppPortletSetup;
import com.arsdigita.portal.apportlet.AppPortletType;
import com.arsdigita.runtime.ScriptContext;
import com.arsdigita.web.ApplicationType;

import org.apache.log4j.Logger;


//  ///////////////////////////////////////////////////////////////////////////
//  Project: Migrate to new style legacy free type of application.
//
//  Step 1: Copy from Initializer all data base / applicationtype        *DONE*
//          related code to LOADER and use RepositoryLoader for 
//          data loading.
//
//  Step 2: Remove usage of ApplicationSetup and switch to legacy        *DONE*
//          compativle AppType xxx = new AppType.create(......)
//          Move setInstantiator back to Initializer as required.
//
//  Step 3: Move to legacy free app type
//          (a) modify new App.Tpye.....                                 *DONE*
//          (b) create AppServlet from Dispatcher                        *DONE*
//
//
//  TESTS:
//  (a) Try to instantiate an instance of each type and check            *MOSTLY
//      the UI produced by the dispatcher / servlet                       DONE
//  (b) Instantiate the portlets and try to reproduce the behaviour
//      (probably reproduce the error showing up originally)
//
//
//  ///////////////////////////////////////////////////////////////////////////



/**
 * CMS Document Manager (DocMgr) RepositoryLoader
 *
 * @author pboy &lt;pboy@barkhof.uni-bremen.de&gt;
 * @version $Id: RepositoryLoader.java $
 **/

public class RepositoryLoader extends PackageLoader {


    /** Logger instance for debugging */
    private static final Logger s_log = Logger.getLogger(RepositoryLoader.class);

    /**
     * Run script invoked by com.arsdigita.packing loader script.
     *
     * @param ctx
     */
    public void run(final ScriptContext ctx) {

        new KernelExcursion() {
            public void excurse() {
                setEffectiveParty(Kernel.getSystemParty());

                loadRepositoryApplicationType();  //former setupDocs
                loadRepositoryPortletType(null);     //former setupDocManagerPortlet

                ApplicationType categoryBrowseDocsAppType = loadCategoryBrowserType();
                setupCategoryDocsPortlet(categoryBrowseDocsAppType);

                ApplicationType legacyCategoryBrowseDocsAppType = 
                                                  loadLegacyCategoryBrowserType();
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
    private ApplicationType loadRepositoryApplicationType() {

        /* Legacy free initialization                                  
         * NOTE: The wording in the title parameter of ApplicationType determines
         * the name of the subdirectory for the XSL stylesheets.
         * It gets "urlized", i.e. trimming leading and trailing blanks and
         * replacing blanks between words and illegal characters with an hyphen
         * and converted to lower case.
         * Example: "DocRepo" will become "docrepo".
         */
        ApplicationType type =  new ApplicationType(
                                        "CMSDocs",
                                        Repository.BASE_DATA_OBJECT_TYPE );
        type.setDescription
            ("The document repository empowers users to share documents.");
        
        return type; 
    }

    private ApplicationType loadCategoryBrowserType() {

        ApplicationType type =  new ApplicationType(
                                        "cmsdocs-categories",
                                        DocumentCategoryBrowserApplication
                                        .BASE_DATA_OBJECT_TYPE );
        type.setDescription("Browse documents by category.");

        return type; 

    }

    /**
     * 
     * @return 
     */
    private ApplicationType loadLegacyCategoryBrowserType() {

        ApplicationType type =  new ApplicationType("cmsdocs-categories-legacy",
                                                    LegacyCategoryBrowserApplication
                                                     .BASE_DATA_OBJECT_TYPE );
        type.setDescription("Browse documents by legacy category (Taxonomie Browser).");

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
                                  .create( "cmsdocs-repo", 
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
    private void loadRepositoryPortletType(ApplicationType provider) {

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
