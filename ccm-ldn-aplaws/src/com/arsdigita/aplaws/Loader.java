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
import com.arsdigita.loader.PackageLoader;
import com.arsdigita.kernel.Role;
import com.arsdigita.kernel.RoleCollection;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.runtime.ScriptContext;
import com.arsdigita.util.Assert;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.util.parameter.BooleanParameter;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.StringParameter;
import com.arsdigita.util.parameter.URLParameter;
import com.arsdigita.web.Application;

import com.arsdigita.london.navigation.Navigation;
import com.arsdigita.london.navigation.Template;
import com.arsdigita.london.navigation.TemplateMapping;
import com.arsdigita.london.terms.Domain;
import com.arsdigita.london.terms.importer.Parser;
import com.arsdigita.portalworkspace.PageLayout;
import com.arsdigita.portalworkspace.Workspace;

import java.net.URL;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * Loader.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: Loader.java 755 2005-09-02 13:42:47Z sskracic $
 */
public class Loader extends PackageLoader {

    private static final Logger s_log = Logger.getLogger(Loader.class);

    private static final String[] s_esdFiles = new String[] {
        "WEB-INF/esd-toolkit/domain-gcl-2.10.xml",
        "WEB-INF/esd-toolkit/domain-lgcl-1.04.xml",
        "WEB-INF/esd-toolkit/domain-lgdl-2.01.xml",
        "WEB-INF/esd-toolkit/domain-lgil-0.01.xml",
        "WEB-INF/esd-toolkit/domain-lgsl-2.01.xml",
        "WEB-INF/esd-toolkit/domain-lgal-0.01.xml",
        "WEB-INF/esd-toolkit/hierarchy-lgcl-1.04.xml",
        "WEB-INF/esd-toolkit/hierarchy-lgdl-2.01-lgsl-2.01.xml",
        "WEB-INF/esd-toolkit/hierarchy-lgdl-2.01.xml",
        "WEB-INF/esd-toolkit/hierarchy-lgil-0.01.xml",
        "WEB-INF/esd-toolkit/mapping-lgcl-1.04-gcl-2.10.xml",
        "WEB-INF/esd-toolkit/mapping-lgcl-1.04-lgsl-2.01.xml",
        "WEB-INF/esd-toolkit/mapping-lgsl-2.00-lgcl-1.04.xml",
        "WEB-INF/esd-toolkit/related-lgcl-1.04.xml",
        "WEB-INF/aplaws/domain-rss-1.00.xml",
        "WEB-INF/aplaws/hierarchy-rss-1.00.xml",
        "WEB-INF/aplaws/domain-nav-1.03.xml",
        "WEB-INF/aplaws/hierarchy-nav-1.03.xml"
    };

    private static final String[] s_esdFilesLite = new String[] {
        "WEB-INF/esd-toolkit/domain-gcl-2.10.xml",
        "WEB-INF/esd-toolkit/domain-lgcl-lite-1.04.xml",
        "WEB-INF/esd-toolkit/domain-lgdl-2.01.xml",
        "WEB-INF/esd-toolkit/domain-lgil-0.01.xml",
        "WEB-INF/esd-toolkit/domain-lgsl-2.01.xml",
        "WEB-INF/esd-toolkit/domain-lgal-0.01.xml",
        "WEB-INF/esd-toolkit/hierarchy-lgcl-lite-1.04.xml",
        "WEB-INF/esd-toolkit/hierarchy-lgdl-2.01-lgsl-2.01.xml",
        "WEB-INF/esd-toolkit/hierarchy-lgdl-2.01.xml",
        "WEB-INF/esd-toolkit/hierarchy-lgil-0.01.xml",
        "WEB-INF/esd-toolkit/mapping-lgcl-1.04-gcl-2.10.xml",
        "WEB-INF/esd-toolkit/mapping-lgcl-1.04-lgsl-2.01.xml",
        "WEB-INF/esd-toolkit/mapping-lgsl-2.00-lgcl-1.04.xml",
        "WEB-INF/esd-toolkit/related-lgcl-1.04.xml",
        "WEB-INF/aplaws/domain-rss-1.00.xml",
        "WEB-INF/aplaws/hierarchy-rss-1.00.xml",
        "WEB-INF/aplaws/domain-nav-1.03.xml",
        "WEB-INF/aplaws/hierarchy-nav-1.03.xml"
    };

    private StringParameter m_servicesDomain;
    private StringParameter m_navigationDomain;
    private StringParameter m_interactionDomain;
    private StringParameter m_subjectDomain;
    private StringParameter m_rssDomain;
    private BooleanParameter m_liteLoad;
    private StringParameter m_customNavKey;
    private URLParameter m_customNavDomainURL;
    private StringParameter m_customNavPath;
    private StringParameter m_customNavUseContext;
    private StringParameter m_customNavTitle;
    private StringParameter m_customNavDesc;

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


    public void run(final ScriptContext ctx) {
        Application.createApplication(Navigation.BASE_DATA_OBJECT_TYPE,
                                      "services",
                                      "Services",
                                      null);

        String customNavPath = (String)get(m_customNavPath);
        String customNavTitle = (String)get(m_customNavTitle);

        Application.createApplication(Navigation.BASE_DATA_OBJECT_TYPE,
                                      customNavPath,
                                      customNavTitle,
                                      null);

        String[] files = Boolean.TRUE.equals(get(m_liteLoad)) ?
            s_esdFilesLite : s_esdFiles;

        final Parser parser = new Parser();
        for (int i = 0 ; i < files.length ; i++) {
            final String file = files[i];
            if (s_log.isInfoEnabled()) {
                s_log.info("Process " + file);
            }
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
        registerDomain(navigationKey, "/content/", null);
        registerDomain(navigationKey, "/portal/", null);
        //registerDomain(navigationKey, "/atoz/", null);
        //registerDomain(navigationKey, "/admin/subsite/", null);

        registerDomain(subjectKey, "/search/", null);
        registerDomain(subjectKey, "/content/", "subject");

        registerDomain(servicesKey, "/services/", null);
        registerDomain(servicesKey, "/content/", "services");

        registerDomain(rssKey, "/channels/", null);
        registerDomain(rssKey, "/content/", "rss");

        registerDomain(interactionKey, "/content/", "interaction");
        
        registerServicesTemplate("/services/");
        registerPortalTemplate();

        String customNavDesc = (String)get(m_customNavDesc);
        String customNavKey = (String)get(m_customNavKey);
        String customNavUseContext = (String)get(m_customNavUseContext);
        URL customNavDomainURL = (URL)get(m_customNavDomainURL);

        Domain.create(customNavKey, customNavDomainURL,
                      customNavTitle, customNavDesc, "1.0.0", new Date());

        registerDomain(customNavKey, '/'+customNavPath+'/', null);
        registerDomain(customNavKey, "/content/", customNavUseContext);

        // Switch /portal/ to use 1 column layout for funky aplaws stuff.
        // pboy: This will have no effect at all. A portal page created at
        // url /portal/ (and beneath) will always use the homepage jsp's which
        // are hardcoded to create a three column design and ignore any
        // column configuration. All portal pages at other urls are not
        // affect by this setting which touches only the one application (portal)
        // at url /portal/. Portal pages at other urls use the corresponding
        // configuration parameter for its initial value and number of columns
        // may be modified at any time using configuration ui.
        /*
        Workspace portal = (Workspace)Application
            .retrieveApplicationForPath("/portal/");
        portal.setDefaultLayout(PageLayout
                                .findLayoutByFormat(PageLayout.FORMAT_ONE_COLUMN));
         */
    }

    public void registerServicesTemplate(String appURL) {
        Application app = Application.retrieveApplicationForPath(appURL);
        Assert.exists(app, Application.class);
        Category root = Category.getRootForObject(app);
        Assert.exists(root, Category.class);

        Template template = Template.create(
            "APLAWS Services",
            "APLAWS ESD Toolkit Services",
            "/packages/navigation/templates/aplaws-services.jsp");

        new TemplateMapping( template, root, Template.DEFAULT_DISPATCHER_CONTEXT,
                                             Template.DEFAULT_USE_CONTEXT );
    }
    
    public void registerPortalTemplate() {
        Template template = Template.create(
            "APLAWS Portal",
            "APLAWS Portal in category",
            "/packages/navigation/templates/aplaws-portal.jsp");
    }
    
    public void registerDomain(String domainKey,
                               String appURL,
                               String context) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Mapping domain " + domainKey + 
                        " to app " + appURL + 
                        " in context " + context);
        }

        Domain domain = Domain.retrieve(domainKey);
        Application app = Application.retrieveApplicationForPath(appURL);
        domain.setAsRootForObject(app, context);
        if (app instanceof ContentSection) {
            RoleCollection coll = ((ContentSection) app).getStaffGroup().getOrderedRoles();
            Set adminRoles = new HashSet();
            Set categorizeRoles = new HashSet();
            while (coll.next()) {
                Role role = coll.getRole();
                final DataQuery privs = RoleFactory.getRolePrivileges
                    (app.getID(), role.getGroup().getID());
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
