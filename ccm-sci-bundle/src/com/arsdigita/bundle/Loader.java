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

package com.arsdigita.bundle;

import com.arsdigita.london.terms.Domain;
import com.arsdigita.london.terms.importer.Parser;

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
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.StringArrayParameter;
import com.arsdigita.web.Application;

import org.apache.log4j.Logger;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Loader executes nonrecurring at install time and loads (installs and
 * initializes) the ScientificCMS integration module persistently into database.
 *
 * Creates category domains in the terms application according to 
 * configuration files and adds jsp templates to navigation.
 * 
 * NOTE: Configuration parameters used at load time MUST be part of Loader 
 * class and can not delegated to a Config object (derived from AbstractConfig).
 * They will (and can) not be persisted into an registry object (file).
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @author Peter Boy &lt;pboy@barkhof.uni-bremen.de&gt;
 * @version $Id: Loader.java 755 2005-09-02 13:42:47Z sskracic $
 */
public class Loader extends PackageLoader {

    /** Logger instance for debugging  */
    private static final Logger s_log = Logger.getLogger(Loader.class);
    
    /**
     * List of comma separated sets of application instance specifications,
     * optionally used to create arbitrary custom application instances 
     * according to local sites requirements.
     * It's the developers / administrators responsibility to ensure all
     * necessary application types have been created previously.
     * Example:
     *   "FULL_QUALIFIED_CLASS_NAME : URL : TITLE , 
     *    FULL_QUALIFIED_CLASS_NAME : URL : TITLE ,
     *    ....                                    ,
     *    FULL_QUALIFIED_CLASS_NAME : URL : TITLE "
     * E.G.
     * "com.arsdigita.navigation.Navigation:local:Local Navigation"
     */
    private Parameter m_customApplicationInstances = new StringArrayParameter(
                "com.arsdigita.bundle.loader.custom_app_instances",
                Parameter.OPTIONAL, null
                );
    
    /**
     * Comma separated list of fully qualified filenames, each file containing
     * a set of Terms domain catagories definitions. These form an initial set
     * of category tree(s), at minimum for navigation, optionally additional
     * domains.
     * Files are stored as part of the jar, so classloader can find them.
     */
    private Parameter m_categoryFiles = new StringArrayParameter(
                "com.arsdigita.bundle.loader.category_files",
                Parameter.REQUIRED,new String[]{
                    "bundle/categories/sci-nav-domain-1.00.xml",
                    "bundle/categories/sci-nav-hierarchy-1.00.xml" }
                );

    /**
     * List of comma separated sets of domain mappings.
     * It's the developers / administrators responsibility to ensure all
     * necessary category domains have been imported previously and are spelled
     * correctly. The list may contain set for different domain key and/or
     * different applications and contexts. If an domain key requires several
     * applications, repeat the key and specify a different application and/or
     * context
     * Example:
     *   "DOMAIN_KEY_1 : APP_URL_1 [: CONTEXT_1] , 
     *    DOMAIN_KEY_1 : APP_URL_2 [: CONTEXT_1] ,
     *    DOMAIN_KEY_2 : APP_URL_1 [: CONTEXT] ,
     *    ....                             ,
     *    DOMAIN_KEY_n : APP_URL_n [: CONTEXT_n] "
     * 
     */
    private Parameter m_domainMappings = new StringArrayParameter(
                "com.arsdigita.bundle.loader.domain_mappings",
                Parameter.REQUIRED,new String[]{ "STD-NAV:/navigation/",
                                                 "STD-NAV:/content/",
                                                 "STD-NAV:/portal/"      }
                );

    
    /**
     * Constructor
     */
    public Loader() {

        // Register defined parameters to the context by adding 
        // the parameter to a map of parameters
        register(m_customApplicationInstances);
        register(m_categoryFiles);
        register(m_domainMappings);

    }


    public void run(final ScriptContext ctx) {

        /* Create site specific custom applications instances of arbitrary
         * type specified by optional configuration parameter.
         * Typically used to create additional navigation instances for alternativ 
         * navigation tree(s) or additional content sections.
         */
        String[] customApplicationInstances = (String[]) get(m_customApplicationInstances);
        if ( customApplicationInstances != null) {
            
            for (int i = 0 ; i < customApplicationInstances.length ; i++) {
            
                final String aCustomApplicationInstance = customApplicationInstances[i];

                StringTokenizer tok = new StringTokenizer( aCustomApplicationInstance, ":" );
                String type = null;    // full qualified class name
                String url = null;     // url fragment (last part)
                String title = null;   // title of new application instance
                String parent = null;  // parent class name
                for ( int j = 0; tok.hasMoreTokens(); j++ ) {
                    if ( 0 == j ) {
                        type = tok.nextToken();
                    } else if ( 1 == j ) {
                        url = tok.nextToken();
                    } else if ( 2 == j ) { 
                        title = tok.nextToken();
                    } else if ( 3 == j ) { 
                        parent = tok.nextToken();
                    } else {
                        parent = null;
                    }
                }

                Application.createApplication(type, url, title, null);
            }

        }

        
        /* Import from the categories definition files: Create Terms domains 
         * and populate them with categories                                  
         * (alternatively this could be delegated to terms.Loader because it's
         * all Terms)                                                         
         * Creates one or more Terms domains, but NO domain mapping for navigation.
         * Therefore, registerDomains is required for /navigation/ otherwise
         * the systems throws NPE for ccm/navigation.
         */
        String[] files = (String[]) get(m_categoryFiles);
        final Parser parser = new Parser();
        // for each filename in the array of files containing categories
        for (int i = 0 ; i < files.length ; i++) {
            final String file = files[i];
            if (s_log.isInfoEnabled()) {
                s_log.info("Process " + file);
            }
            /* Import a Terms category domain.                                */
            parser.parse(Thread.currentThread().getContextClassLoader().
                    getResourceAsStream(file));
        }


        /* Creates domain mappings according to configuration file. By default
         * at least one domain mapping for standard navigation is created,
         * otherwise navigation wouldn't work.
         * IOt is the developers / administrators responsibility that KEY is
         * existent, i.e. previously importet in the previous step. 
         */
        String[] domainMappings = (String[]) get(m_domainMappings);
        for (int i = 0 ; i < domainMappings.length ; i++) {
            
            final String aDomainMapping = domainMappings[i];

            StringTokenizer tok = new StringTokenizer( aDomainMapping, ":" );
            String key = null;
            String app = null;
            String context = null;
            for ( int j = 0; tok.hasMoreTokens(); j++ ) {
                if ( 0 == j ) {
                    key = tok.nextToken();
                } else if ( 1 == j ) {
                    app = tok.nextToken();
                } else if ( 2 == j ) { 
                    context = tok.nextToken();
                } else {
                    context = null;
                }
            }

            registerDomain(key, app, context);

        }
        

        // registerServicesTemplate("/services/");  wird nicht gebraucht        

    }  // end run method

    
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
     * Determines the Terms domain using domainKey as well as the application
     * instance using appURL and then creates a domain mapping using context
     * as domain context.
     * 
     * Uses Package com.arsdigita.london.terms.Domain (!) 
     * 
     * @param domainKey
     * @param appURL
     * @param context 
     */
    public void registerDomain(String domainKey,
                               String appURL,
                               String context) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Mapping domain " + domainKey + " to app " + appURL
                        + " in context " + context);
        }

        /* Determine Domain and Application objects, both MUST exist!         */
        Domain domain = Domain.retrieve(domainKey);  
        Application app = Application.retrieveApplicationForPath(appURL);
        
        /* Create domain mapping                                              */
        domain.setAsRootForObject(app, context);
        
        /* Create permissions and roles for content-center applications only  */
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
