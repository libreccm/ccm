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

import com.arsdigita.categorization.Category;
import com.arsdigita.categorization.RootCategoryCollection;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.cms.ui.role.RoleFactory;
import com.arsdigita.kernel.Role;
import com.arsdigita.kernel.RoleCollection;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.loader.PackageLoader;
import com.arsdigita.london.terms.Domain;
import com.arsdigita.london.terms.importer.Parser;
import com.arsdigita.navigation.Navigation;
import com.arsdigita.navigation.Template;
import com.arsdigita.navigation.TemplateMapping;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.runtime.ScriptContext;
import com.arsdigita.util.Assert;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.util.parameter.BooleanParameter;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.StringParameter;
import com.arsdigita.util.parameter.URLParameter;
import com.arsdigita.web.Application;

import java.net.URL;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * Loader executes nonrecurring at install time and loads (installs and
 * initializes) the APLAWS integration module persistently into database.
 *
 * Creates category domains in the terms application according to 
 * configuration files and adds jsp templates to navigation.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: Loader.java 2255 2011-11-17 08:46:40Z pboy $
 */
public class Loader extends PackageLoader {

    private static final Logger s_log = Logger.getLogger(Loader.class);

    private static final String[] s_esdFiles = new String[] {
        "bundle/esd-toolkit/domain-gcl-2.10.xml",
        "bundle/esd-toolkit/domain-lgcl-1.04.xml",
        "bundle/esd-toolkit/domain-lgdl-2.01.xml",
        "bundle/esd-toolkit/domain-lgil-0.01.xml",
        "bundle/esd-toolkit/domain-lgsl-2.01.xml",
        "bundle/esd-toolkit/domain-lgal-0.01.xml",
        "bundle/esd-toolkit/hierarchy-lgcl-1.04.xml",
        "bundle/esd-toolkit/hierarchy-lgdl-2.01-lgsl-2.01.xml",
        "bundle/esd-toolkit/hierarchy-lgdl-2.01.xml",
        "bundle/esd-toolkit/hierarchy-lgil-0.01.xml",
        "bundle/esd-toolkit/mapping-lgcl-1.04-gcl-2.10.xml",
        "bundle/esd-toolkit/mapping-lgcl-1.04-lgsl-2.01.xml",
        "bundle/esd-toolkit/mapping-lgsl-2.00-lgcl-1.04.xml",
        "bundle/esd-toolkit/related-lgcl-1.04.xml",
        "bundle/aplaws/domain-rss-1.00.xml",
        "bundle/aplaws/hierarchy-rss-1.00.xml",
        "bundle/aplaws/domain-nav-1.03.xml",
        "bundle/aplaws/hierarchy-nav-1.03.xml"
    };

    private static final String[] s_esdFilesLite = new String[] {
        "bundle/esd-toolkit/domain-gcl-2.10.xml",
        "bundle/esd-toolkit/domain-lgcl-lite-1.04.xml",
        "bundle/esd-toolkit/domain-lgdl-2.01.xml",
        "bundle/esd-toolkit/domain-lgil-0.01.xml",
        "bundle/esd-toolkit/domain-lgsl-2.01.xml",
        "bundle/esd-toolkit/domain-lgal-0.01.xml",
        "bundle/esd-toolkit/hierarchy-lgcl-lite-1.04.xml",
        "bundle/esd-toolkit/hierarchy-lgdl-2.01-lgsl-2.01.xml",
        "bundle/esd-toolkit/hierarchy-lgdl-2.01.xml",
        "bundle/esd-toolkit/hierarchy-lgil-0.01.xml",
        "bundle/esd-toolkit/mapping-lgcl-1.04-gcl-2.10.xml",
        "bundle/esd-toolkit/mapping-lgcl-1.04-lgsl-2.01.xml",
        "bundle/esd-toolkit/mapping-lgsl-2.00-lgcl-1.04.xml",
        "bundle/esd-toolkit/related-lgcl-1.04.xml",
        "bundle/aplaws/domain-rss-1.00.xml",
        "bundle/aplaws/hierarchy-rss-1.00.xml",
        "bundle/aplaws/domain-nav-1.03.xml",
        "bundle/aplaws/hierarchy-nav-1.03.xml"
    };

    private final StringParameter m_servicesDomain;
    private final StringParameter m_navigationDomain;
    private final StringParameter m_interactionDomain;
    private final StringParameter m_subjectDomain;
    private final StringParameter m_rssDomain;
    private final BooleanParameter m_liteLoad;
    private final StringParameter m_customNavKey;
    private final StringParameter m_customNavPath;
    private final StringParameter m_customNavUseContext;
    private final StringParameter m_customNavTitle;
    private final StringParameter m_customNavDesc;

    private URLParameter m_customNavDomainURL;

    public Loader() {
        m_servicesDomain = new StringParameter(
            "com.arsdigita.aplaws.services_domain",
            Parameter.REQUIRED,
            "LGDL");
        
        m_navigationDomain = new StringParameter(
            "com.arsdigita.aplaws.navigation_domain",
            Parameter.REQUIRED,
            "APLAWS-NAV");
        
        m_subjectDomain = new StringParameter(
            "com.arsdigita.aplaws.subject_domain",
            Parameter.REQUIRED,
            "LGCL");

        m_interactionDomain = new StringParameter(
            "com.arsdigita.aplaws.subject_domain",
            Parameter.REQUIRED,
            "LGIL");
        
        m_rssDomain = new StringParameter(
            "com.arsdigita.aplaws.rss_domain",
            Parameter.REQUIRED,
            "APLAWS-RSS");
        
        m_liteLoad = new BooleanParameter(
            "com.arsdigita.aplaws.lite_load",
            Parameter.REQUIRED,
            Boolean.FALSE);
        
        m_customNavKey = new StringParameter(
            "com.arsdigita.aplaws.custom_nav_key",
            Parameter.REQUIRED,
            "APLAWS-CUSTOM");
        
        try {
            m_customNavDomainURL = new URLParameter(
                "com.arsdigita.aplaws.custom_nav_domain_url",
                Parameter.REQUIRED,
                new URL("http://www.aplaws.org.uk/" +
                        "standards/custom/1.00/termslist.xml"));
        } catch (MalformedURLException ex) {
            throw new UncheckedWrapperException("Cannot parse url", ex);
        }
        
        m_customNavPath = new StringParameter(
            "com.arsdigita.aplaws.custom_nav_path",
            Parameter.REQUIRED,
            "local");
        
        m_customNavUseContext = new StringParameter(
            "com.arsdigita.aplaws.custom_nav_use_context",
            Parameter.REQUIRED,
            "local");
        
        m_customNavTitle = new StringParameter(
            "com.arsdigita.aplaws.custom_nav_title",
            Parameter.REQUIRED,
            "Local Custom Navigation");
        
        m_customNavDesc = new StringParameter(
            "com.arsdigita.aplaws.custom_nav_desc",
            Parameter.REQUIRED,
            "Installation specific custom navigation tree");
        
        register(m_servicesDomain);
        register(m_navigationDomain);
        register(m_interactionDomain);
        register(m_subjectDomain);
        register(m_rssDomain);
        register(m_liteLoad);
        register(m_customNavDesc);
        register(m_customNavDomainURL);
        register(m_customNavKey);
        register(m_customNavPath);
        register(m_customNavTitle);
        register(m_customNavUseContext);
    }


    @Override
    public void run(final ScriptContext ctx) {
        
        /*                                                                   */
        Application.createApplication(Navigation.BASE_DATA_OBJECT_TYPE,
                                      "services",
                                      "Services",
                                      null);

        /* Create an additional Navigation application instance used as an
         * custom navigation tree in addition or as an alternative to the
         * standard APLAWS LGL navigation tree.
         */
        String customNavPath = (String)get(m_customNavPath);
        String customNavTitle = (String)get(m_customNavTitle);
        Application.createApplication(Navigation.BASE_DATA_OBJECT_TYPE,
                                      customNavPath,
                                      customNavTitle,
                                      null);

        String[] files = Boolean.TRUE.equals(get(m_liteLoad)) ?
                             s_esdFilesLite : s_esdFiles;

        final Parser parser = new Parser();
        // for each filename in the array of files containing categories
        for (int i = 0 ; i < files.length ; i++) {
            final String file = files[i];
            if (s_log.isInfoEnabled()) {
                s_log.info("Process " + file);
            }
            /* Import a Terms category domain.                                */
            parser.parse(Thread.currentThread().getContextClassLoader
                         ().getResourceAsStream
                         (file));
        }

        String navigationKey = (String)get(m_navigationDomain);
        String interactionKey = (String)get(m_interactionDomain);
        String servicesKey = (String)get(m_servicesDomain);
        String subjectKey = (String)get(m_subjectDomain);
        String rssKey = (String)get(m_rssDomain);
        
        registerDomain(navigationKey, "/navigation/", null);
        registerDomain(navigationKey, "/info/", null);
        registerDomain(navigationKey, "/portal/", null);
        //registerDomain(navigationKey, "/atoz/", null);
        //registerDomain(navigationKey, "/admin/subsite/", null);

        registerDomain(subjectKey, "/search/", null);
        registerDomain(subjectKey, "/info/", "subject");

        registerDomain(servicesKey, "/services/", null);
        registerDomain(servicesKey, "/info/", "services");

        registerDomain(rssKey, "/channels/", null);
        registerDomain(rssKey, "/info/", "rss");

        registerDomain(interactionKey, "/info/", "interaction");
        
        registerServicesTemplate("/services/");
        registerPortalTemplate();

        String customNavDesc = (String)get(m_customNavDesc);
        String customNavKey = (String)get(m_customNavKey);
        String customNavUseContext = (String)get(m_customNavUseContext);
        URL customNavDomainURL = (URL)get(m_customNavDomainURL);

        Domain.create(customNavKey, customNavDomainURL,
                      customNavTitle, customNavDesc, "1.0.0", new Date());

        registerDomain(customNavKey, '/'+customNavPath+'/', null);
        registerDomain(customNavKey, "/info/", customNavUseContext);

    }

    public void registerServicesTemplate(String appURL) {
        Application app = Application.retrieveApplicationForPath(appURL);
        Assert.exists(app, Application.class);
        Category root = Category.getRootForObject(app);
        Assert.exists(root, Category.class);

        Template template = Template.create(
            "APLAWS Services",
            "APLAWS ESD Toolkit Services",
            "/templates/ccm-navigation/navigation/aplaws-services.jsp");

        new TemplateMapping( template, root, Template.DEFAULT_DISPATCHER_CONTEXT,
                                             Template.DEFAULT_USE_CONTEXT );
    }
    
    public void registerPortalTemplate() {
        Template template = Template.create(
            "APLAWS Portal",
            "APLAWS Portal in category",
            "/templates/ccm-navigation/navigation/aplaws-portal.jsp");
    }
    
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
        Domain domain = Domain.retrieve(domainKey);  // package com.arsdigita.london.terms
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
