/*
 * Copyright (C) 2012 Peter Boy All Rights Reserved.
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

package com.arsdigita.atoz.siteproxy;

import com.arsdigita.atoz.AtoZ;
import com.arsdigita.atoz.AtoZProviderType;
import com.arsdigita.atoz.siteproxy.ui.admin.SiteProxyProviderAdmin;
import com.arsdigita.atoz.siteproxy.ui.admin.SiteProxyProviderForm;
import com.arsdigita.db.DbHelper;
import com.arsdigita.domain.xml.TraversalHandler;
import com.arsdigita.persistence.pdl.ManifestSource;
import com.arsdigita.persistence.pdl.NameFilter;
import com.arsdigita.runtime.CompoundInitializer;
import com.arsdigita.runtime.DomainInitEvent;
import com.arsdigita.runtime.PDLInitializer;
import com.arsdigita.runtime.RuntimeConfig;
import com.arsdigita.xml.XML;

/**
 * Initializes the A-Z system siteproxy extension
 * @author pb
 */
public class Initializer extends CompoundInitializer {

    /**
     * Constructor
     */
    public Initializer() {

        final String url = RuntimeConfig.getConfig().getJDBCURL();
        final int database = DbHelper.getDatabaseFromURL(url);

        add(new PDLInitializer(new ManifestSource("ccm-atoz-siteproxy.pdl.mf",
                new NameFilter(DbHelper.getDatabaseSuffix(database), "pdl"))));
    }

    /**
     * 
     * @param evt 
     */
    @Override
	public void init(DomainInitEvent evt) {
		super.init(evt);

        
        XML.parse(Config.getConfig().getTraversalAdapters(),
                  new TraversalHandler());

        AtoZ.registerProviderType(
                new AtoZProviderType("SiteProxy Provider",
                                     "Provides a SiteProxy A-Z",
                                     SiteProxyProvider.class,
                                     SiteProxyProviderForm.class,
                                     SiteProxyProviderAdmin.class));

    }    
    
}
