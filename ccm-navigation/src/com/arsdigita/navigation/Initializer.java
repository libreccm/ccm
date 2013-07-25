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
package com.arsdigita.navigation;

import com.arsdigita.categorization.Category;
import com.arsdigita.categorization.CategoryCollection;
import com.arsdigita.db.DbHelper;

import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.TemplateContext;

import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.DomainObjectInstantiator;

import com.arsdigita.kernel.NoValidURLException;
import com.arsdigita.kernel.URLService;
import com.arsdigita.kernel.URLFinder;
import com.arsdigita.kernel.ACSObjectInstantiator;

import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.pdl.ManifestSource;
import com.arsdigita.persistence.pdl.NameFilter;

import com.arsdigita.runtime.CompoundInitializer;
import com.arsdigita.runtime.RuntimeConfig;
import com.arsdigita.runtime.PDLInitializer;
import com.arsdigita.runtime.DomainInitEvent;

import com.arsdigita.web.Application;
import com.arsdigita.web.ParameterMap;
import com.arsdigita.web.URL;
import com.arsdigita.web.Web;

import com.arsdigita.util.Assert;

import com.arsdigita.kernel.ResourceTypeConfig;
import com.arsdigita.kernel.ResourceType;
import com.arsdigita.kernel.ui.ResourceConfigFormSection;
import com.arsdigita.bebop.RequestLocal;

import com.arsdigita.navigation.portlet.NavigationTreePortlet;
import com.arsdigita.navigation.portlet.ObjectListPortlet;
import com.arsdigita.navigation.portlet.ItemListPortlet;

import com.arsdigita.navigation.ui.portlet.ObjectListPortletEditor;
import com.arsdigita.navigation.ui.portlet.ItemListPortletEditor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.arsdigita.domain.xml.TraversalHandler;
import com.arsdigita.ui.admin.ApplicationManagers;
import com.arsdigita.xml.XML;

/**
 * Initializer for ccm-navigation.
 * Executes recursivly at each system startup.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: Initializer.java 2070 2010-01-28 08:47:41Z pboy $
 */
public class Initializer extends CompoundInitializer {

    private static final Logger s_log = Logger.getLogger(Initializer.class);

    /**
     * Constructor
     */
    public Initializer() {
        final String url = RuntimeConfig.getConfig().getJDBCURL();
        final int database = DbHelper.getDatabaseFromURL(url);

        add(new PDLInitializer(new ManifestSource("ccm-navigation.pdl.mf",
                                                  new NameFilter(DbHelper.getDatabaseSuffix(database), "pdl"))));
    }

    /**
     * Initializes domain-coupling machinery, usually consisting of
     * registering object instantiators and observers.
     *
     * This starts up the search threads according to the values in the
     * properties file
     */
    public void init(DomainInitEvent e) {
        super.init(e);
        // Don't use a private configuration parameter for default content-section!
        // Use the content-sections configuration directly!
        // System.setProperty( NavigationConstants.DEFAULT_CONTENT_SECTION_URL,
        //                     Navigation.getConfig().getDefaultContentSectionURL() );
        System.setProperty(NavigationConstants.DEFAULT_CONTENT_SECTION_URL,
                           ContentSection.getConfig().getDefaultContentSection());

        e.getFactory().registerInstantiator(Template.BASE_DATA_OBJECT_TYPE,
                                            new DomainObjectInstantiator() {
            public DomainObject doNewInstance(DataObject dataObject) {
                return new Template(dataObject);
            }

            @Override
            public DomainObjectInstantiator resolveInstantiator(DataObject obj) {
                return this;
            }

        });

        e.getFactory().registerInstantiator(Navigation.BASE_DATA_OBJECT_TYPE,
                                            new ACSObjectInstantiator() {
            @Override
            public DomainObject doNewInstance(DataObject dataObject) {
                return new Navigation(dataObject);
            }

        });

        NavigationTreePortlet.registerInstantiator();
        NavigationTreePortlet.registerResourceTypeConfig();

        e.getFactory().registerInstantiator(ItemListPortlet.BASE_DATA_OBJECT_TYPE,
                                            new ACSObjectInstantiator() {
            @Override
            public DomainObject doNewInstance(DataObject dataObject) {
                return new ItemListPortlet(dataObject);
            }

        });

        e.getFactory().registerInstantiator(ObjectListPortlet.BASE_DATA_OBJECT_TYPE,
                                            new ACSObjectInstantiator() {
            @Override
            public DomainObject doNewInstance(DataObject dataObject) {
                return new ObjectListPortlet(dataObject);
            }

        });

        e.getFactory().registerInstantiator(TemplateMapping.BASE_DATA_OBJECT_TYPE,
                                            new DomainObjectInstantiator() {
            public DomainObject doNewInstance(DataObject dataObject) {
                return new TemplateMapping(dataObject);
            }

            @Override
            public DomainObjectInstantiator resolveInstantiator(DataObject obj) {
                return this;
            }

        });



        new ResourceTypeConfig(ObjectListPortlet.BASE_DATA_OBJECT_TYPE) {
            @Override
            public ResourceConfigFormSection getCreateFormSection(final ResourceType resType,
                                                                  final RequestLocal parentAppRL) {
                final ResourceConfigFormSection config =
                                                new ObjectListPortletEditor(resType, parentAppRL);

                return config;
            }

            @Override
            public ResourceConfigFormSection getModifyFormSection(final RequestLocal application) {
                final ObjectListPortletEditor config =
                                              new ObjectListPortletEditor(application);

                return config;
            }

        };

        new ResourceTypeConfig(ItemListPortlet.BASE_DATA_OBJECT_TYPE) {
            @Override
            public ResourceConfigFormSection getCreateFormSection(final ResourceType resType,
                                                                  final RequestLocal parentAppRL) {
                final ResourceConfigFormSection config =
                                                new ItemListPortletEditor(resType, parentAppRL);

                return config;
            }

            @Override
            public ResourceConfigFormSection getModifyFormSection(final RequestLocal application) {
                final ItemListPortletEditor config =
                                            new ItemListPortletEditor(application);

                return config;
            }

        };

        URLService.registerFinder(Category.BASE_DATA_OBJECT_TYPE, new NavigationUrlFinder());

//        URLService.registerFinder(Category.BASE_DATA_OBJECT_TYPE,
//                                  new URLFinder() {
//                                      
//            public String find(OID oid, String context)
//                    throws NoValidURLException {
//                return find(oid);
//            }
//
//            public String find(OID oid)
//                    throws NoValidURLException {
//
//                if (s_log.isInfoEnabled()) {
//                    s_log.info("Locating " + oid);
//                }
//
//                Category cat = (Category) DomainObjectFactory.newInstance(oid);
//                CategoryCollection ancestors = cat.getDefaultAscendants();
//                // XXX bz 118313
//                ancestors.clearOrder();
//                ancestors.addOrder(Category.DEFAULT_ANCESTORS);
//
//                List ids = new ArrayList();
//                List paths = new LinkedList();
//                boolean first = true;
//                while (ancestors.next()) {
//                    Category anc = ancestors.getCategory();
//                    if (s_log.isDebugEnabled()) {
//                        s_log.debug("Process parent " + anc);
//                    }
//                    ids.add(anc.getID());
//                    if (first) {
//                        first = false;
//                        paths.add("");
//                        continue;
//                    }
//                    if (paths != null) {
//                        String url = anc.getURL();
//                        if (url != null && !"".equals(url)) {
//                            s_log.debug("Appending '" + url + "' for anc");
//                            paths.add(url);
//                        } else {
//                            if (s_log.isInfoEnabled()) {
//                                s_log.info("Cat " + anc + " has no url ");
//                            }
//                            paths = null;
//                        }
//                    } else {
//                        s_log.debug("Path is null");
//                    }
//                }
//
//                if (s_log.isDebugEnabled() && null != paths) {
//                    StringBuffer buf = new StringBuffer();
//                    Iterator idsit = ids.iterator();
//                    Iterator pathsit = paths.iterator();
//                    while (idsit.hasNext()) {
//                        buf.append(idsit.next().toString()).append(' ');
//                        buf.append(pathsit.next().toString()).append('/');
//                    }
//                    s_log.debug("Full path is " + buf.toString());
//                }
//
//                TemplateContext tContext =
//                                Navigation.getContext().getTemplateContext();
//                String useContext = null == tContext ? null : tContext.getContext();
//                if (s_log.isDebugEnabled()) {
//                    s_log.debug("Use Context: " + useContext);
//                }
//
//                DataCollection apps = SessionManager.getSession().retrieve(Application.BASE_DATA_OBJECT_TYPE);
//                apps.addEqualsFilter("objectType", Navigation.BASE_DATA_OBJECT_TYPE);
//                apps.addEqualsFilter("rootUseContext.useContext", useContext);
//                Filter f = apps.addFilter("rootUseContext.rootCategory in :ids");
//                f.set("ids", ids);
//
//                apps.addPath("rootUseContext.rootCategory.id");
//
//                String appURL = null;
//                BigDecimal rootCatID = null;
//
//                if (!apps.next()) {
//                    appURL = Navigation.getConfig().getDefaultCategoryRootPath();
//                    // We can only use named paths if the category is mapped
//                    // to a navigation app instance in the current
//                    // use context 
//                    paths = null;
//                    if (s_log.isDebugEnabled()) {
//                        s_log.debug("Using default nav path " + appURL);
//                    }
//                } else {
//                    Application app = (Application) DomainObjectFactory.newInstance(apps.getDataObject());
//                    //appURL = app.getPrimaryURL();
//                    appURL = String.format("%s/", app.getPath());
//                    rootCatID = (BigDecimal) apps.get("rootUseContext.rootCategory.id");
//                    apps.close();
//                }
//
//                if (s_log.isInfoEnabled()) {
//                    s_log.info("Application path is " + appURL);
//                }
//
//                Assert.isTrue(appURL.startsWith("/"), "url starts with '/'");
//                Assert.isTrue(appURL.endsWith("/"), "url ends with '/'");
//
//                ParameterMap map = new ParameterMap();
//                String path;
//                if (paths == null) {
//                    map.setParameter("categoryID", cat.getID());
//                    path = "category.jsp";
//                } else {
//                    if (s_log.isDebugEnabled()) {
//                        s_log.debug("Generating path from category " + rootCatID);
//                    }
//                    StringBuffer buf = new StringBuffer();
//                    Iterator pathsit = paths.iterator();
//                    Iterator idsit = ids.iterator();
//                    boolean gotRoot = false;
//                    while (pathsit.hasNext()) {
//                        String frag = (String) pathsit.next();
//                        BigDecimal id = (BigDecimal) idsit.next();
//                        if (gotRoot) {
//                            buf.append(frag).append('/');
//                        } else if (id.equals(rootCatID)) {
//                            gotRoot = true;
//                        }
//                    }
//                    path = buf.toString();
//                }
//
//                String url = URL.there(Web.getRequest(), appURL + path, map).toString();
//                if (s_log.isInfoEnabled()) {
//                    s_log.info("Final url is " + url);
//                }
//                return url;
//            }
//
//        });
        
        //Register the ApplicationManager implementation for this application
        ApplicationManagers.register(new NavigationAppManager());

        XML.parse(Navigation.getConfig().getTraversalAdapters(),
                  new TraversalHandler());

    }

}
