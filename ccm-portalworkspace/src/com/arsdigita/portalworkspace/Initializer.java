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

package com.arsdigita.portalworkspace;

import org.apache.log4j.Logger;

import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.db.DbHelper;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectInstantiator;
import com.arsdigita.domain.xml.TraversalHandler;
import com.arsdigita.kernel.ACSObjectInstantiator;
import com.arsdigita.kernel.ResourceType;
import com.arsdigita.kernel.ResourceTypeConfig;
import com.arsdigita.kernel.ui.ResourceConfigComponent;
import com.arsdigita.kernel.ui.ResourceConfigFormSection;
import com.arsdigita.portalworkspace.portlet.ApplicationDirectoryPortlet;
import com.arsdigita.portalworkspace.portlet.ContentDirectoryPortlet;
import com.arsdigita.portalworkspace.portlet.FlashPortletInitializer;
import com.arsdigita.portalworkspace.portlet.FreeformHTMLPortlet;
import com.arsdigita.portalworkspace.portlet.LoginPortlet;
import com.arsdigita.portalworkspace.portlet.RSSFeedPortlet;
import com.arsdigita.portalworkspace.portlet.TimeOfDayPortlet;
import com.arsdigita.portalworkspace.portlet.MyWorkspacesPortlet;
import com.arsdigita.portalworkspace.portlet.WorkspaceNavigatorPortlet;
import com.arsdigita.portalworkspace.portlet.WorkspaceSummaryPortlet;
import com.arsdigita.portalworkspace.ui.admin.WorkspaceConfigFormSection;
import com.arsdigita.portalworkspace.ui.portlet.ContentDirectoryPortletEditor;
import com.arsdigita.portalworkspace.ui.portlet.FreeformHTMLPortletEditor;
import com.arsdigita.portalworkspace.ui.portlet.RSSFeedPortletEditor;
import com.arsdigita.portalworkspace.ui.portlet.RSSFeedPortletEditorForm;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.pdl.ManifestSource;
import com.arsdigita.persistence.pdl.NameFilter;
import com.arsdigita.runtime.CompoundInitializer;
import com.arsdigita.runtime.DomainInitEvent;
import com.arsdigita.runtime.PDLInitializer;
import com.arsdigita.runtime.RuntimeConfig;
import com.arsdigita.xml.XML;


/**
 * Initializes the Portal Workspace system
 * 
 * @version $Id: Initializer.java 2070 2010-01-28 08:47:41Z pboy $
 */
public class Initializer extends CompoundInitializer {

    private static final Logger s_log = Logger.getLogger(Initializer.class);

    /**
     * Constructor
     * 
     */
    public Initializer() {
        final String url = RuntimeConfig.getConfig().getJDBCURL();
        final int database = DbHelper.getDatabaseFromURL(url);

        add(new PDLInitializer(new ManifestSource("ccm-portalworkspace.pdl.mf",
                               new NameFilter(DbHelper.getDatabaseSuffix(database),
                               "pdl"))));
    }

    /**
     * 
     * @param e
     */
    @Override
    public void init(DomainInitEvent e) {

        super.init(e);

        XML.parse(Workspace.getConfig().getTraversalAdapters(),
                  new TraversalHandler());

        e.getFactory().registerInstantiator(
                Workspace.BASE_DATA_OBJECT_TYPE,
                new ACSObjectInstantiator() {
                    @Override
                    public DomainObject doNewInstance(DataObject dataObject) {
                        return new Workspace(dataObject);
                    }
                });

        e.getFactory().registerInstantiator(
                WorkspacePage.BASE_DATA_OBJECT_TYPE,
                new ACSObjectInstantiator() {
                    @Override
                    public DomainObject doNewInstance(DataObject dataObject) {
                        return new WorkspacePage(dataObject);
                    }
                });

        e.getFactory().registerInstantiator(
                PageLayout.BASE_DATA_OBJECT_TYPE,
                new DomainObjectInstantiator() {
                    public DomainObject doNewInstance(DataObject dataObject) {
                        return new PageLayout(dataObject);
                    }
                });


        e.getFactory().registerInstantiator(
                ApplicationDirectoryPortlet.BASE_DATA_OBJECT_TYPE,
                new ACSObjectInstantiator() {
                    @Override
                    public DomainObject doNewInstance(DataObject dataObject) {
                        return new ApplicationDirectoryPortlet(dataObject);
                    }
                });

        e.getFactory().registerInstantiator(
                ContentDirectoryPortlet.BASE_DATA_OBJECT_TYPE,
                new ACSObjectInstantiator() {
                    public DomainObject doNewInstance(DataObject dataObject) {
                        return new ContentDirectoryPortlet(dataObject);
                    }
                });

        e.getFactory().registerInstantiator(
                FreeformHTMLPortlet.BASE_DATA_OBJECT_TYPE,
                new ACSObjectInstantiator() {
                    public DomainObject doNewInstance(DataObject dataObject) {
                        return new FreeformHTMLPortlet(dataObject);
                    }
                });

        e.getFactory().registerInstantiator(
                LoginPortlet.BASE_DATA_OBJECT_TYPE,
                new ACSObjectInstantiator() {
                    @Override
                    public DomainObject doNewInstance(DataObject dataObject) {
                            return new LoginPortlet(dataObject);
                    }
                });

        e.getFactory().registerInstantiator(
                MyWorkspacesPortlet.BASE_DATA_OBJECT_TYPE,
                new ACSObjectInstantiator() {
                    @Override
                    public DomainObject doNewInstance(DataObject dataObject) {
                        return new MyWorkspacesPortlet(dataObject);
                    }
                });

        e.getFactory().registerInstantiator(
                RSSFeedPortlet.BASE_DATA_OBJECT_TYPE,
                new ACSObjectInstantiator() {
                    @Override
                    public DomainObject doNewInstance(DataObject dataObject) {
                        return new RSSFeedPortlet(dataObject);
                    }
                });

        e.getFactory().registerInstantiator(
                TimeOfDayPortlet.BASE_DATA_OBJECT_TYPE,
                new ACSObjectInstantiator() {
                   public DomainObject doNewInstance(DataObject dataObject) {
                       return new TimeOfDayPortlet(dataObject);
                   }
                });

//     WorkspaceDirectoryPortlet,  imports ccm-ldn-rss
//      e.getFactory().registerInstantiator(
//              WorkspaceDirectoryPortlet.BASE_DATA_OBJECT_TYPE,
//              new ACSObjectInstantiator() {
//                  public DomainObject doNewInstance(DataObject dataObject) {
//                      return new WorkspaceDirectoryPortlet(dataObject);
//                  }
//              });

        e.getFactory().registerInstantiator(
                WorkspaceNavigatorPortlet.BASE_DATA_OBJECT_TYPE,
                new ACSObjectInstantiator() {
                    @Override
                    public DomainObject doNewInstance(DataObject dataObject) {
                        return new WorkspaceNavigatorPortlet(dataObject);
                    }
                });

        e.getFactory().registerInstantiator(
                WorkspaceSummaryPortlet.BASE_DATA_OBJECT_TYPE,
                new ACSObjectInstantiator() {
                    @Override
                    public DomainObject doNewInstance(DataObject dataObject) {
                        return new WorkspaceSummaryPortlet(dataObject);
                    }
                });

        new ResourceTypeConfig(Workspace.BASE_DATA_OBJECT_TYPE) {
                @Override
                public ResourceConfigFormSection getCreateFormSection(
                            final ResourceType resType,
                            final RequestLocal parentAppRL) {
                    final ResourceConfigFormSection config =
                            new WorkspaceConfigFormSection(resType, parentAppRL);
                    return config;
                 }

                @Override
                public ResourceConfigFormSection getModifyFormSection(
                            final RequestLocal application) {
                    final ResourceConfigFormSection config =
                            new WorkspaceConfigFormSection(application);
                    return config;
                }
        };

        new ResourceTypeConfig(ContentDirectoryPortlet.BASE_DATA_OBJECT_TYPE) {
                @Override
                public ResourceConfigFormSection getCreateFormSection(
                            final ResourceType resType,
                            final RequestLocal parentAppRL) {
                    final ResourceConfigFormSection config =
                            new ContentDirectoryPortletEditor(resType,
						              parentAppRL);
                    return config;
                }

                @Override
                public ResourceConfigFormSection getModifyFormSection(
                            final RequestLocal application) {
                    final ContentDirectoryPortletEditor config =
                            new ContentDirectoryPortletEditor(application);
                    return config;
                }
        };
		
        new ResourceTypeConfig(RSSFeedPortlet.BASE_DATA_OBJECT_TYPE) {
                @Override
                public ResourceConfigFormSection getCreateFormSection(
                            final ResourceType resType,
                            final RequestLocal parentAppRL) {
                    final RSSFeedPortletEditorForm config =
                            new RSSFeedPortletEditorForm(resType, parentAppRL);
                    return config;
                }

                @Override
                public ResourceConfigFormSection getModifyFormSection(
                            final RequestLocal application) {
                    final RSSFeedPortletEditorForm config =
                            new RSSFeedPortletEditorForm(application);
                    return config;
                }

                @Override
                public ResourceConfigComponent getCreateComponent(
                            final ResourceType resType,
                            final RequestLocal parentAppRL) {
                    final ResourceConfigComponent config =
                            new RSSFeedPortletEditor(resType, parentAppRL);
                    return config;
                }

                @Override
                public ResourceConfigComponent getModifyComponent(
                            final RequestLocal application) {
                    final RSSFeedPortletEditor config =
                            new RSSFeedPortletEditor(application);
                    return config;
                }
        };

        new ResourceTypeConfig(FreeformHTMLPortlet.BASE_DATA_OBJECT_TYPE) {
                @Override
                public ResourceConfigFormSection getCreateFormSection(
                            final ResourceType resType,
                            final RequestLocal parentAppRL) {
                    final ResourceConfigFormSection config =
                            new FreeformHTMLPortletEditor(resType, parentAppRL);
                    return config;
                }

                @Override
                public ResourceConfigFormSection getModifyFormSection(
                            final RequestLocal application) {
                    final FreeformHTMLPortletEditor config =
                            new FreeformHTMLPortletEditor(application);
                    return config;
                }
        };


        FlashPortletInitializer.initialize();

        // import from navigation required 
        // causes horizontal dependency between portal and navigation
        // required by NavigationTreePortlet (formerly NavigationDirectroryPortlet)
        // now moved to navigation ??
//      ApplicationNavigationModel.register(Workspace.class.getName(),
//			            new DefaultNavigationModel());
    }
}
