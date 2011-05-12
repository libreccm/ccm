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

package com.arsdigita.aplaws;

import com.arsdigita.london.navigation.Template;
import com.arsdigita.london.terms.Domain;
import com.arsdigita.london.terms.importer.Parser;
import com.arsdigita.portalworkspace.PageLayout;
import com.arsdigita.portalworkspace.Workspace;

import com.arsdigita.categorization.Category;
import com.arsdigita.categorization.RootCategoryCollection;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.cms.ui.role.RoleFactory;
import com.arsdigita.loader.PackageLoader;
import com.arsdigita.kernel.Role;
import com.arsdigita.kernel.RoleCollection;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.runtime.ScriptContext;
// import com.arsdigita.util.Assert;
// import com.arsdigita.util.UncheckedWrapperException;
// import com.arsdigita.util.parameter.BooleanParameter;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.StringParameter;
// import com.arsdigita.util.parameter.URLParameter;
import com.arsdigita.web.Application;

import org.apache.log4j.Logger;

// import java.net.URL;
// import java.net.MalformedURLException;
// import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Loader.
 *
 * Creates category domains in the terms application according to 
 * configuration files and adds jsp templates to navigation.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: Loader.java 755 2005-09-02 13:42:47Z sskracic $
 */
public class Loader extends PackageLoader {

    private static final Logger s_log = Logger.getLogger(Loader.class);

    // Name of files containing an initial category tree(s).
    // Files are stored as part of the jar, so classloader can find them.
    // ToDo: relocate files user accessible outside the jar.
    private static final String[] categoryFiles = new String[]{
        "WEB-INF/aplaws/zes-nav-domain-1.00.xml",
        "WEB-INF/aplaws/zes-nav-hierarchy-1.00.xml"
    };

    private StringParameter m_navigationDomain;
    // private StringParameter m_servicesDomain;
    // private StringParameter m_interactionDomain;
    // private StringParameter m_subjectDomain;
    // private StringParameter m_rssDomain;

    /**
     * Constructor 
     */
    public Loader() {
        
        // Es werden stumpf mehrere Kategorisierungsdomains fuer TERMS 
        // definiert und dann über xml Dateien gefüllt:
        // navigationDomain f. Navigation
        // subjectDomain f. ???
        // interactionDomain f. ???
        // rssDomain fuer vermutlich RSS Feed
        //
        m_navigationDomain = new StringParameter(
                "com.arsdigita.aplaws.navigation_domain",
                Parameter.REQUIRED,
                "ZES-NAV");
        // Registers to the context by adding the parameter to a map of parameters
        register(m_navigationDomain);

        /*
         * You may add more catagory domains by adding resources
         * according the following schema
         */

        /* currently not used
        m_subjectDomain = new StringParameter(
            "com.arsdigita.aplaws.subject_domain",
            Parameter.REQUIRED,
            "LGCL");
        register(m_subjectDomain);
         */

        /* currently not used
        m_interactionDomain = new StringParameter(
            "com.arsdigita.aplaws.subject_domain",
            Parameter.REQUIRED,
            "LGIL");
        register(m_interactionDomain);
         */
        
        /* currently not used
        m_rssDomain = new StringParameter(
            "com.arsdigita.aplaws.rss_domain",
            Parameter.REQUIRED,
            "APLAWS-RSS");
        register(m_rssDomain);
         */


    }


    public void run(final ScriptContext ctx) {

        String[] files = categoryFiles;

        final Parser parser = new Parser();
        // for each filename in the array of files containing categories
        for (int i = 0 ; i < files.length ; i++) {
            final String file = files[i];
            if (s_log.isInfoEnabled()) {
                s_log.info("Process " + file);
            }
            parser.parse(Thread.currentThread().getContextClassLoader().
                    getResourceAsStream(file));
        }

        String navigationKey = (String) get(m_navigationDomain);
        registerDomain(navigationKey, "/navigation/", null);
        registerDomain(navigationKey, "/content/", null);
        registerDomain(navigationKey, "/portal/", null);
        //registerDomain(navigationKey, "/atoz/", null);
        //registerDomain(navigationKey, "/admin/subsite/", null);

        /*
         * You may add more catagory domains by adding resources
         * according the following schema
         */

        // String subjectKey = (String)get(m_subjectDomain);
        // registerDomain(subjectKey, "/search/", null);
        // registerDomain(subjectKey, "/content/", "subject");

        // String servicesKey = (String)get(m_servicesDomain);
        // registerDomain(servicesKey, "/services/", null);
        // registerDomain(servicesKey, "/content/", "services");

        // String rssKey = (String)get(m_rssDomain);
        // registerDomain(rssKey, "/channels/", null);
        // registerDomain(rssKey, "/content/", "rss");

        // String interactionKey = (String)get(m_interactionDomain);
        // registerDomain(interactionKey, "/content/", "interaction");



        // register new / addidional JSP templates (index pages) in Navigation
        // registerServicesTemplate("/services/");  wird nicht gebraucht
        registerNavigationTemplates();

        // Switch /portal/ to use 1 column layout for funky aplaws stuff.
        // pboy: This will have no effect at all. A portal page created at
        // url /portal/ (and beneath) will always use the homepage jsp's which
        // are hardcoded to create a three column design and ignore any
        // column configuration. All portal pages at other urls are not
        // affect by this setting which touches only the one application (portal)
        // at url /portal/. Portal pages at other urls use the corresponding
        // configuration parameter for its initial value and number of columns
        // may be modified at any time using configuration ui.
/*      Workspace portal = (Workspace)Application
              .retrieveApplicationForPath("/portal/");
        portal.setDefaultLayout(PageLayout
              .findLayoutByFormat(PageLayout.FORMAT_ONE_COLUMN));             */
    }   // end run method

//  public void registerServicesTemplate(String appURL) {
//      Application app = Application.retrieveApplicationForPath(appURL);
//      Assert.exists(app, Application.class);
//      Category root = Category.getRootForObject(app);
//      Assert.exists(root, Category.class);
//
//      Template template = Template.create(
//          "APLAWS Services",
//          "APLAWS ESD Toolkit Services",
//          "/packages/navigation/templates/aplaws-services.jsp");
//
//      new TemplateMapping( template, 
//                           root, 
//                           Template.DEFAULT_DISPATCHER_CONTEXT, 
//                           Template.DEFAULT_USE_CONTEXT );
//  }
    
    /**
     * Use Package com.arsdigita.london.navigation to add additional
     * templates (JSP page - index page) for use in navigation.
     * These JSP pages can be choosen in admin/navigation as index
     * pages for one or more specific categories.
     * 
     * TODO: make configurable without recompiling!
     */
    public void registerNavigationTemplates() {

        Template template;

        /*  In navigation werden bereits Grund-Templates erstellt.
         */
        template = Template.create(
                "ZeS AtoZ paginator",
                "ZeS AtoZ paginator index page",
                "/packages/navigation/templates/zes-atoz.jsp");

        template = Template.create(
                "ZeS Default",
                "ZeS default index page",
                "/packages/navigation/templates/zes-default.jsp");

        template = Template.create(
                "ZeS Portalseite",
                "ZeS Portal Page",
                "/packages/navigation/templates/zes-portal.jsp");

        template = Template.create(
                "ZeS Recent",
                "ZeS reverse order page",
                "/packages/navigation/templates/zes-recent.jsp");

        template = Template.create(
                "ZeS Welcome Page",
                "ZeS Welcome Page for navigation",
                "/packages/navigation/templates/zes-welcome.jsp");

        template =
        Template.create(
                "MultiPartArticle as Index Item",
                "Display a MultiPartArticle as index item",
                "/packages/navigation/templates/mparticle-index.jsp");


        template =
        Template.create(
                "Specializing list",
                "Displays a list of items as the ordinary template, but specializes the objects in the list.",
                "/packages/navigation/templates/SpecializingList.jsp");

        template =
        Template.create(
                "SciProject list",
                "Displays a list of SciProject items, including some attributes.",
                "/packages/navigation/templates/SciProjectList.jsp");

        template =
        Template.create(
                "SciPublication list",
                "Displays a list of publication items, including some attributes.",
                "/packages/navigation/templates/SciPublicationList.jsp");

    }

    /**
     * Function to create an empty default domain in terms, preconfigured
     * for navigation. It may be populated manually by the user/publisher 
     * using the terms admin application.
     * This step is useful only if no specific navigation tree is
     * delivered.  
     */
    // -- public void registerDefaultNavigationDomain() {
    // -- private StringParameter m_customNavKey;
    // -- private URLParameter    m_customNavDomainURL;
    // -- private StringParameter m_customNavPath;
    // -- private StringParameter m_customNavUseContext;
    // -- private StringParameter m_customNavTitle;
    // -- private StringParameter m_customNavDesc;
    // -- m_customNavKey = new StringParameter(
    // --     "com.arsdigita.aplaws.custom_nav_key",
    // --     Parameter.REQUIRED,
    // --     "APLAWS-NAVIGATION");

    /*   Zugriff auf Website wird nicht benötigt, aber der Parameter bei Einrichtung
     *   der Kategorien. Funktion URL prüft auf korrekte Syntax, nicht auf Existenz
     */
    // -- try {
    // --     m_customNavDomainURL = new URLParameter(
    // --         "com.arsdigita.aplaws.custom_nav_domain_url",
    // --         Parameter.REQUIRED,
    // --         new URL("http://www.aplaws.org.uk/" +
    // --                 "standards/custom/1.00/termslist.xml"));
    // -- } catch (MalformedURLException ex) {
    // --     throw new UncheckedWrapperException("Cannot parse url", ex);
    // -- }
    // -- m_customNavPath = new StringParameter(
    // --     "com.arsdigita.aplaws.custom_nav_path",
    // --     Parameter.REQUIRED,
    // --     "local");
    // -- m_customNavUseContext = new StringParameter(
    // --     "com.arsdigita.aplaws.custom_nav_use_context",
    // --     Parameter.REQUIRED,
    // --     "local");
    // -- m_customNavTitle = new StringParameter(
    // --     "com.arsdigita.aplaws.custom_nav_title",
    // --     Parameter.REQUIRED,
    // --     "APLAWS Custom Navigation");
    // -- m_customNavDesc = new StringParameter(
    // --     "com.arsdigita.aplaws.custom_nav_desc",
    // --     Parameter.REQUIRED,
    // --     "Installation specific navigation tree");
    // -- register(m_customNavDesc);
    // -- register(m_customNavDomainURL);
    // -- register(m_customNavKey);
    // -- register(m_customNavPath);
    // -- register(m_customNavTitle);
    // -- register(m_customNavUseContext);
    // -- String customNavPath = (String)get(m_customNavPath);
    // -- String customNavTitle = (String)get(m_customNavTitle);
    // Package com.arsdigita.web
    // Application.createApplication(Navigation.BASE_DATA_OBJECT_TYPE,
    //                               customNavPath,
    //                               customNavTitle,
    //                               null);
    // -- String customNavDesc = (String)get(m_customNavDesc);
    // -- String customNavKey = (String)get(m_customNavKey);
    // -- String customNavUseContext = (String)get(m_customNavUseContext);
    // -- URL customNavDomainURL = (URL)get(m_customNavDomainURL);
    // -- Domain.create(customNavKey, customNavDomainURL,
    // --               customNavTitle, customNavDesc, "1.0.0", new Date());
    // registerDomain(customNavKey, '/'+customNavPath+'/', null);
    // -- registerDomain(customNavKey, "/content/", customNavUseContext);
    // -- }
    /**
     * Use Package com.arsdigita.london.terms to register a Domain for
     * Categorisation
     */
    public void registerDomain(String domainKey,
                               String appURL,
                               String context) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Mapping domain " + domainKey + " to app " + appURL
                        + " in context " + context);
        }

        Domain domain = Domain.retrieve(domainKey);  // package com.arsdigita.london.terms
        Application app = Application.retrieveApplicationForPath(appURL);
        domain.setAsRootForObject(app, context);
        if (app instanceof ContentSection) {
            RoleCollection coll = ((ContentSection) app).getStaffGroup().getOrderedRoles();
            Set adminRoles = new HashSet();
            Set categorizeRoles = new HashSet();
            while (coll.next()) {
                Role role = coll.getRole();
                final DataQuery privs = RoleFactory.getRolePrivileges(
                        app.getID(), role.getGroup().getID());
                while (privs.next()) {
                    String priv = (String) privs.get(RoleFactory.PRIVILEGE);
                    if (priv.equals(SecurityManager.CMS_CATEGORY_ADMIN)) {
                        adminRoles.add(role);
                    } else if (priv.equals(SecurityManager.CMS_CATEGORIZE_ITEMS)) {
                        categorizeRoles.add(role);
                    }
                }

            }
            RootCategoryCollection catCollection = Category.getRootCategories(((ContentSection) app));
            while (catCollection.next()) {
                Iterator adminIter = adminRoles.iterator();
                while (adminIter.hasNext()) {
                    ((Role) adminIter.next()).grantPermission(catCollection.getCategory(),
                                                              PrivilegeDescriptor.ADMIN);
                }
                Iterator categorizeIter = categorizeRoles.iterator();
                while (categorizeIter.hasNext()) {
                    ((Role) categorizeIter.next()).grantPermission(catCollection.getCategory(),
                                                                   Category.MAP_DESCRIPTOR);
                }
            }
        }
    }
}
