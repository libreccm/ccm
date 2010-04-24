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

package com.arsdigita.london.rss;

import com.arsdigita.domain.DomainObjectInstantiator;

import com.arsdigita.db.DbHelper;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.kernel.ACSObjectInstantiator;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.pdl.ManifestSource;
import com.arsdigita.persistence.pdl.NameFilter;
import com.arsdigita.runtime.CompoundInitializer;
import com.arsdigita.runtime.DomainInitEvent;
import com.arsdigita.runtime.LegacyInitializer;
import com.arsdigita.runtime.PDLInitializer;
import com.arsdigita.runtime.RuntimeConfig;
import com.arsdigita.london.rss.portlet.WorkspaceDirectoryPortlet;

// import com.arsdigita.bebop.RequestLocal;
// import com.arsdigita.domain.DomainObjectInstantiator;
// import com.arsdigita.domain.xml.TraversalHandler;
// import com.arsdigita.kernel.ResourceType;
// import com.arsdigita.kernel.ResourceTypeConfig;
// import com.arsdigita.kernel.ui.ResourceConfigComponent;
// import com.arsdigita.kernel.ui.ResourceConfigFormSection;
// import com.arsdigita.london.navigation.ApplicationNavigationModel;
// import com.arsdigita.london.navigation.DefaultNavigationModel;
// import com.arsdigita.london.portal.portlet.ApplicationDirectoryPortlet;
// import com.arsdigita.london.portal.portlet.ContentDirectoryPortlet;
// import com.arsdigita.london.portal.portlet.FlashPortletInitializer;
// import com.arsdigita.london.portal.portlet.FreeformHTMLPortlet;
// import com.arsdigita.london.portal.portlet.LoginPortlet;
// import com.arsdigita.london.portal.portlet.NavigationDirectoryPortlet;
// import com.arsdigita.london.portal.portlet.RSSFeedPortlet;
// import com.arsdigita.london.portal.portlet.TimeOfDayPortlet;
// import com.arsdigita.london.portal.portlet.MyWorkspacesPortlet;
// import com.arsdigita.london.portal.portlet.WorkspaceNavigatorPortlet;
// import com.arsdigita.london.portal.portlet.WorkspaceSummaryPortlet;
// import com.arsdigita.london.portal.ui.admin.WorkspaceConfigFormSection;
// import com.arsdigita.london.portal.ui.portlet.ContentDirectoryPortletEditor;
// import com.arsdigita.london.portal.ui.portlet.FreeformHTMLPortletEditor;
// import com.arsdigita.london.portal.ui.portlet.RSSFeedPortletEditor;
// import com.arsdigita.london.portal.ui.portlet.RSSFeedPortletEditorForm;
// import com.arsdigita.xml.XML;



/**
 * The CMS initializer.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: Initializer.java 758 2005-09-02 14:26:56Z sskracic $
 */
public class Initializer extends CompoundInitializer {

    public Initializer() {
        final String url = RuntimeConfig.getConfig().getJDBCURL();
        final int database = DbHelper.getDatabaseFromURL(url);

        add(new PDLInitializer
            (new ManifestSource
             ("ccm-ldn-rss.pdl.mf",
              new NameFilter(DbHelper.getDatabaseSuffix(database), "pdl"))));

        add(new LegacyInitializer("com/arsdigita/london/rss/enterprise.init"));
    }

    /**
     *
     * @param e
     */
    public void init(DomainInitEvent e) {

        super.init(e);


        e.getFactory().registerInstantiator(
                WorkspaceDirectoryPortlet.BASE_DATA_OBJECT_TYPE,
                new ACSObjectInstantiator() {
                    public DomainObject doNewInstance(DataObject dataObject) {
                        return new WorkspaceDirectoryPortlet(dataObject);
                    }
                });
    }
}
