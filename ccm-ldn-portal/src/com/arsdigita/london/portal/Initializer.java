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

package com.arsdigita.london.portal;

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
import com.arsdigita.london.navigation.ApplicationNavigationModel;
import com.arsdigita.london.navigation.DefaultNavigationModel;
import com.arsdigita.london.portal.portlet.ApplicationDirectoryPortlet;
import com.arsdigita.london.portal.portlet.ContentDirectoryPortlet;
import com.arsdigita.london.portal.portlet.FlashPortletInitializer;
import com.arsdigita.london.portal.portlet.FreeformHTMLPortlet;
import com.arsdigita.london.portal.portlet.LoginPortlet;
import com.arsdigita.london.portal.portlet.NavigationDirectoryPortlet;
import com.arsdigita.london.portal.portlet.RSSFeedPortlet;
import com.arsdigita.london.portal.portlet.TimeOfDayPortlet;
import com.arsdigita.london.portal.portlet.WorkspaceDirectoryPortlet;
import com.arsdigita.london.portal.portlet.MyWorkspacesPortlet;
import com.arsdigita.london.portal.portlet.WorkspaceNavigatorPortlet;
import com.arsdigita.london.portal.portlet.WorkspaceSummaryPortlet;
import com.arsdigita.london.portal.ui.admin.WorkspaceConfigFormSection;
import com.arsdigita.london.portal.ui.portlet.ContentDirectoryPortletEditor;
import com.arsdigita.london.portal.ui.portlet.FreeformHTMLPortletEditor;
import com.arsdigita.london.portal.ui.portlet.RSSFeedPortletEditor;
import com.arsdigita.london.portal.ui.portlet.RSSFeedPortletEditorForm;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.pdl.ManifestSource;
import com.arsdigita.persistence.pdl.NameFilter;
import com.arsdigita.runtime.CompoundInitializer;
import com.arsdigita.runtime.DomainInitEvent;
import com.arsdigita.runtime.PDLInitializer;
import com.arsdigita.runtime.RuntimeConfig;
import com.arsdigita.xml.XML;

/**
 * Initializes the Portal system
 * 
 * @version $Id: Initializer.java 1739 2008-08-15 01:15:21Z terry $
 */
public class Initializer extends CompoundInitializer {
	// public final static String versionId = "$Id: Initializer.java 1739 2008-08-15 01:15:21Z terry $"
	//		+ "$Author: terry $" + "$DateTime: 2004/03/02 06:33:42 $";

	private static final Logger s_log = Logger.getLogger(Initializer.class);

	public Initializer() {
		final String url = RuntimeConfig.getConfig().getJDBCURL();
		final int database = DbHelper.getDatabaseFromURL(url);

		add(new PDLInitializer(new ManifestSource("ccm-ldn-portal.pdl.mf",
				new NameFilter(DbHelper.getDatabaseSuffix(database), "pdl"))));
	}

	public void init(DomainInitEvent e) {
		super.init(e);

		XML.parse(Workspace.getConfig().getTraversalAdapters(),
				new TraversalHandler());

		e.getFactory().registerInstantiator(Workspace.BASE_DATA_OBJECT_TYPE,
				new ACSObjectInstantiator() {
					public DomainObject doNewInstance(DataObject dataObject) {
						return new Workspace(dataObject);
					}
				});

		e.getFactory().registerInstantiator(
				WorkspacePage.BASE_DATA_OBJECT_TYPE,
				new ACSObjectInstantiator() {
					public DomainObject doNewInstance(DataObject dataObject) {
						return new WorkspacePage(dataObject);
					}
				});

		e.getFactory().registerInstantiator(PageLayout.BASE_DATA_OBJECT_TYPE,
				new DomainObjectInstantiator() {
					public DomainObject doNewInstance(DataObject dataObject) {
						return new PageLayout(dataObject);
					}
				});

		e.getFactory().registerInstantiator(
				ApplicationDirectoryPortlet.BASE_DATA_OBJECT_TYPE,
				new ACSObjectInstantiator() {
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
				WorkspaceDirectoryPortlet.BASE_DATA_OBJECT_TYPE,
				new ACSObjectInstantiator() {
					public DomainObject doNewInstance(DataObject dataObject) {
						return new WorkspaceDirectoryPortlet(dataObject);
					}
				});

		e.getFactory().registerInstantiator(
				FreeformHTMLPortlet.BASE_DATA_OBJECT_TYPE,
				new ACSObjectInstantiator() {
					public DomainObject doNewInstance(DataObject dataObject) {
						return new FreeformHTMLPortlet(dataObject);
					}
				});

		e.getFactory().registerInstantiator(LoginPortlet.BASE_DATA_OBJECT_TYPE,
				new ACSObjectInstantiator() {
					public DomainObject doNewInstance(DataObject dataObject) {
						return new LoginPortlet(dataObject);
					}
				});

		e.getFactory().registerInstantiator(
				TimeOfDayPortlet.BASE_DATA_OBJECT_TYPE,
				new ACSObjectInstantiator() {
					public DomainObject doNewInstance(DataObject dataObject) {
						return new TimeOfDayPortlet(dataObject);
					}
				});

		e.getFactory().registerInstantiator(
				RSSFeedPortlet.BASE_DATA_OBJECT_TYPE,
				new ACSObjectInstantiator() {
					public DomainObject doNewInstance(DataObject dataObject) {
						return new RSSFeedPortlet(dataObject);
					}
				});

		e.getFactory().registerInstantiator(
				MyWorkspacesPortlet.BASE_DATA_OBJECT_TYPE,
				new ACSObjectInstantiator() {
					public DomainObject doNewInstance(DataObject dataObject) {
						return new MyWorkspacesPortlet(dataObject);
					}
				});

		e.getFactory().registerInstantiator(
				WorkspaceNavigatorPortlet.BASE_DATA_OBJECT_TYPE,
				new ACSObjectInstantiator() {
					public DomainObject doNewInstance(DataObject dataObject) {
						return new WorkspaceNavigatorPortlet(dataObject);
					}
				});

		e.getFactory().registerInstantiator(
				WorkspaceSummaryPortlet.BASE_DATA_OBJECT_TYPE,
				new ACSObjectInstantiator() {
					public DomainObject doNewInstance(DataObject dataObject) {
						return new WorkspaceSummaryPortlet(dataObject);
					}
				});

		new ResourceTypeConfig(Workspace.BASE_DATA_OBJECT_TYPE) {
			public ResourceConfigFormSection getCreateFormSection(
					final ResourceType resType, final RequestLocal parentAppRL) {
				final ResourceConfigFormSection config = new WorkspaceConfigFormSection(
						resType, parentAppRL);

				return config;
			}

			public ResourceConfigFormSection getModifyFormSection(
					final RequestLocal application) {
				final ResourceConfigFormSection config = new WorkspaceConfigFormSection(
						application);

				return config;
			}
		};

		new ResourceTypeConfig(ContentDirectoryPortlet.BASE_DATA_OBJECT_TYPE) {
			public ResourceConfigFormSection getCreateFormSection(
					final ResourceType resType, final RequestLocal parentAppRL) {
				final ResourceConfigFormSection config = new ContentDirectoryPortletEditor(
						resType, parentAppRL);

				return config;
			}

			public ResourceConfigFormSection getModifyFormSection(
					final RequestLocal application) {
				final ContentDirectoryPortletEditor config = new ContentDirectoryPortletEditor(
						application);

				return config;
			}
		};
		
		new ResourceTypeConfig(RSSFeedPortlet.BASE_DATA_OBJECT_TYPE) {
			public ResourceConfigFormSection getCreateFormSection(
					final ResourceType resType, final RequestLocal parentAppRL) {
				final RSSFeedPortletEditorForm config = new RSSFeedPortletEditorForm(
						resType, parentAppRL);

				return config;
			}

			public ResourceConfigFormSection getModifyFormSection(
					final RequestLocal application) {
				final RSSFeedPortletEditorForm config = new RSSFeedPortletEditorForm(
						application);

				return config;
			}

			public ResourceConfigComponent getCreateComponent(
					final ResourceType resType, final RequestLocal parentAppRL) {
				final ResourceConfigComponent config = new RSSFeedPortletEditor(
						resType, parentAppRL);

				return config;
			}

			public ResourceConfigComponent getModifyComponent(
					final RequestLocal application) {
				final RSSFeedPortletEditor config = new RSSFeedPortletEditor(
						application);

				return config;
			}
		};
		new ResourceTypeConfig(FreeformHTMLPortlet.BASE_DATA_OBJECT_TYPE) {
			public ResourceConfigFormSection getCreateFormSection(
					final ResourceType resType, final RequestLocal parentAppRL) {
				final ResourceConfigFormSection config = new FreeformHTMLPortletEditor(
						resType, parentAppRL);

				return config;
			}

			public ResourceConfigFormSection getModifyFormSection(
					final RequestLocal application) {
				final FreeformHTMLPortletEditor config = new FreeformHTMLPortletEditor(
						application);

				return config;
			}
		};

        	NavigationDirectoryPortlet.registerInstantiator();
        	NavigationDirectoryPortlet.registerResourceTypeConfig();

        	FlashPortletInitializer.initialize();

            // import from london.navigation required 
            // causes horizontal dependency between portal and navigation
            ApplicationNavigationModel.register(Workspace.class.getName(),
				new DefaultNavigationModel());
	}
}
