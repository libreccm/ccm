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
package com.arsdigita.atoz;

// import com.arsdigita.atoz.siteproxy.AtoZSiteProxyProvider;
// import com.arsdigita.atoz.siteproxy.ui.admin.SiteProxyProviderAdmin;
// import com.arsdigita.atoz.siteproxy.ui.admin.SiteProxyProviderForm;
import com.arsdigita.atoz.ui.admin.AtoZApplicationManager;
import com.arsdigita.atoz.ui.admin.CategoryProviderAdmin;
import com.arsdigita.atoz.ui.admin.CategoryProviderForm;
import com.arsdigita.atoz.ui.admin.ItemProviderAdmin;
import com.arsdigita.atoz.ui.admin.ItemProviderForm;
import com.arsdigita.db.DbHelper;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.DomainObjectInstantiator;
import com.arsdigita.domain.xml.TraversalHandler;
import com.arsdigita.kernel.ACSObjectInstantiator;
import com.arsdigita.navigation.ApplicationNavigationModel;
import com.arsdigita.navigation.DefaultNavigationModel;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.pdl.ManifestSource;
import com.arsdigita.persistence.pdl.NameFilter;
import com.arsdigita.runtime.CompoundInitializer;
import com.arsdigita.runtime.DomainInitEvent;
import com.arsdigita.runtime.PDLInitializer;
import com.arsdigita.runtime.RuntimeConfig;
import com.arsdigita.ui.admin.ApplicationManagers;
import com.arsdigita.xml.XML;

/**
 * Initializes the A-Z system
 * 
 * @version $Id: Initializer.java 1741 2008-09-01 15:38:21Z clasohm $
 */
public class Initializer extends CompoundInitializer {

    /**
     * Constructor
     */
    public Initializer() {

        final String url = RuntimeConfig.getConfig().getJDBCURL();
        final int database = DbHelper.getDatabaseFromURL(url);

        add(new PDLInitializer(new ManifestSource(
                    "ccm-atoz.pdl.mf",
                    new NameFilter(DbHelper.getDatabaseSuffix(database), "pdl"))
                ));
    }

    /**
     * 
     * @param evt 
     */
    @Override
    public void init(DomainInitEvent evt) {
        super.init(evt);

        // Was previously invoked by ApplicationSetup, added here in the process
        // of code cleanup. See release notes version 2.0
        /* Register object instantiator for AtoZ domain class                */
        evt.getFactory().registerInstantiator(AtoZ.BASE_DATA_OBJECT_TYPE,
                                              new ACSObjectInstantiator() {
            @Override
            public DomainObject doNewInstance(DataObject dataObject) {
                return new AtoZ(dataObject);
            }

        });


        DomainObjectFactory f = evt.getFactory();
        f.registerInstantiator(CategoryAlias.BASE_DATA_OBJECT_TYPE,
                               new DomainObjectInstantiator() {
            protected DomainObject doNewInstance(DataObject dataObject) {
                return new CategoryAlias(dataObject);
            }

        });


        XML.parse(AtoZ.getConfig().getTraversalAdapters(),
                  new TraversalHandler());


        AtoZ.registerProviderType(
                new AtoZProviderType("Item Provider",
                                     "Provides an item A-Z",
                                     ItemProvider.class,
                                     ItemProviderForm.class,
                                     ItemProviderAdmin.class));

        AtoZ.registerProviderType(
                new AtoZProviderType("Category Provider",
                                     "Provides a category A-Z",
                                     CategoryProvider.class,
                                     CategoryProviderForm.class,
                                     CategoryProviderAdmin.class));

        // Introduces a dependency on ccm-types-siteproxy
        // Must be refactored into its own package.
        /* MOVED to ccm-atoz-siteproxy
         AtoZ.registerProviderType(
         new AtoZProviderType("SiteProxy Provider",
         "Provides a SiteProxy A-Z",
         AtoZSiteProxyProvider.class,
         SiteProxyProviderForm.class,
         SiteProxyProviderAdmin.class));
         */

        // Introduces dependenciy on navigation package 
        // Function / purpose ??
        ApplicationNavigationModel.register(AtoZ.class.getName(),
                                            new DefaultNavigationModel());

        // Introduces dependency on ccm-ldn-typesesdervise ??
/*        AtoZ.registerProviderType(
         new AtoZProviderType("ESD Toolkit Domain Provider",
         "Provides a ESD Toolkit A-Z", 
         DomainProvider.class,
         DomainProviderForm.class, 
         DomainProviderAdmin.class));
         */
        
        //Register the ApplicationManager implementation for the AtoZ application
        ApplicationManagers.register(new AtoZApplicationManager());
    }

}
