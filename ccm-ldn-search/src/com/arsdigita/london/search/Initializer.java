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

package com.arsdigita.london.search;

import com.arsdigita.cms.search.VersionFilterType;
import com.arsdigita.cms.search.LuceneQueryEngine;
import com.arsdigita.db.DbHelper;
import com.arsdigita.persistence.pdl.ManifestSource;
import com.arsdigita.runtime.PDLInitializer;
import com.arsdigita.runtime.CompoundInitializer;
import com.arsdigita.persistence.pdl.NameFilter;
import com.arsdigita.runtime.RuntimeConfig;
import com.arsdigita.runtime.DomainInitEvent;
import com.arsdigita.search.IndexerType;
import com.arsdigita.search.FilterType;
import com.arsdigita.search.QueryEngineRegistry;
import com.arsdigita.search.filters.ObjectTypeFilterType;
import com.arsdigita.london.search.RemoteQueryEngine;
import com.arsdigita.london.search.HostFilterType;

/**
 * The Search initializer.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: Initializer.java 755 2005-09-02 13:42:47Z sskracic $
 */
public class Initializer extends CompoundInitializer {
    public final static String versionId =
        "$Id: Initializer.java 755 2005-09-02 13:42:47Z sskracic $" +
        "$Author: sskracic $" +
        "$DateTime: $";

    public Initializer() {
        final String url = RuntimeConfig.getConfig().getJDBCURL();
        final int database = DbHelper.getDatabaseFromURL(url);

        add(new PDLInitializer
            (new ManifestSource
             ("ccm-ldn-search.pdl.mf",
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

        SearchGroup.setSearchTimeout
            (Search.getConfig().getSearchTimeout().intValue());

      	registerLimitedSimpleSearch();
        registerRemoteSearch();
    }

    private void registerRemoteSearch() {
        QueryEngineRegistry.registerEngine
            ("remote", new FilterType[] {
                new HostFilterType()
            }, new RemoteQueryEngine());
    }

    private void registerLimitedSimpleSearch() {
        QueryEngineRegistry.registerEngine
            (IndexerType.LUCENE,
                new FilterType[] {
                    new VersionFilterType(),
                    new ObjectTypeFilterType()
                },
                new LuceneQueryEngine());
    }
}
