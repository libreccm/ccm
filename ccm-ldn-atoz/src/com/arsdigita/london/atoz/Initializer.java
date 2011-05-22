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

package com.arsdigita.london.atoz;

import com.arsdigita.db.DbHelper;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.DomainObjectInstantiator;
import com.arsdigita.domain.xml.TraversalHandler;
import com.arsdigita.london.atoz.terms.DomainProvider;
import com.arsdigita.london.atoz.ui.admin.CategoryProviderAdmin;
import com.arsdigita.london.atoz.ui.admin.CategoryProviderForm;
import com.arsdigita.london.atoz.ui.admin.ItemProviderAdmin;
import com.arsdigita.london.atoz.ui.admin.ItemProviderForm;
import com.arsdigita.london.atoz.ui.admin.SiteProxyProviderAdmin;
import com.arsdigita.london.atoz.ui.admin.SiteProxyProviderForm;
import com.arsdigita.london.atoz.ui.terms.DomainProviderAdmin;
import com.arsdigita.london.atoz.ui.terms.DomainProviderForm;
import com.arsdigita.london.navigation.ApplicationNavigationModel;
import com.arsdigita.london.navigation.DefaultNavigationModel;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.pdl.ManifestSource;
import com.arsdigita.persistence.pdl.NameFilter;
import com.arsdigita.runtime.CompoundInitializer;
import com.arsdigita.runtime.DomainInitEvent;
import com.arsdigita.runtime.PDLInitializer;
import com.arsdigita.runtime.RuntimeConfig;
import com.arsdigita.xml.XML;

/**
 * Initializes the A-Z system
 * 
 * @version $Id: Initializer.java 1741 2008-09-01 15:38:21Z clasohm $
 */
public class Initializer extends CompoundInitializer {

    public Initializer() {
        final String url = RuntimeConfig.getConfig().getJDBCURL();
        final int database = DbHelper.getDatabaseFromURL(url);

        add(new PDLInitializer(new ManifestSource("ccm-ldn-atoz.pdl.mf",
                new NameFilter(DbHelper.getDatabaseSuffix(database), "pdl"))));
    }

    @Override
	public void init(DomainInitEvent evt) {
		super.init(evt);

        DomainObjectFactory f = evt.getFactory();
    	f.registerInstantiator(AtoZCategoryAlias.BASE_DATA_OBJECT_TYPE,
                               new DomainObjectInstantiator() {
            protected DomainObject doNewInstance(DataObject dataObject) {
                return new AtoZCategoryAlias(dataObject);
            }
        });

        XML.parse(AtoZ.getConfig().getTraversalAdapters(),
                new TraversalHandler());

        AtoZ.getConfig()
                .registerProviderType(
                        new AtoZProviderType("Category Provider",
                                "Provides a category A-Z",
                                AtoZCategoryProvider.class,
                                CategoryProviderForm.class,
                                CategoryProviderAdmin.class));

        AtoZ.getConfig().registerProviderType(
                new AtoZProviderType("Item Provider", "Provides an item A-Z",
                        AtoZItemProvider.class, ItemProviderForm.class,
                        ItemProviderAdmin.class));

        AtoZ.getConfig().registerProviderType(
                new AtoZProviderType("SiteProxy Provider",
                        "Provides a SiteProxy A-Z",
                        AtoZSiteProxyProvider.class,
                        SiteProxyProviderForm.class,
                        SiteProxyProviderAdmin.class));

        ApplicationNavigationModel.register(AtoZ.class.getName(),
                new DefaultNavigationModel());

        AtoZ.getConfig().registerProviderType(
                new AtoZProviderType("ESD Toolkit Domain Provider",
                        "Provides a ESD Toolkit A-Z", DomainProvider.class,
                        DomainProviderForm.class, DomainProviderAdmin.class));

    }

}
