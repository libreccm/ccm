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
import com.arsdigita.domain.DomainObject;
import com.arsdigita.kernel.ACSObjectInstantiator;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.pdl.ManifestSource;
import com.arsdigita.persistence.pdl.NameFilter;
import com.arsdigita.runtime.CompoundInitializer;
import com.arsdigita.runtime.ContextCloseEvent;
import com.arsdigita.runtime.ContextInitEvent;
import com.arsdigita.runtime.DomainInitEvent;
import com.arsdigita.runtime.PDLInitializer;
import com.arsdigita.runtime.RuntimeConfig;
import com.arsdigita.search.IndexerType;
import com.arsdigita.search.FilterType;
import com.arsdigita.search.QueryEngineRegistry;
import com.arsdigita.search.filters.ObjectTypeFilterType;
import com.arsdigita.ui.admin.ApplicationManagers;

import org.apache.log4j.Logger;

/**
 * The Search initializer.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: Initializer.java 755 2005-09-02 13:42:47Z sskracic $
 */
public class Initializer extends CompoundInitializer {

    private static final Logger s_log = Logger.getLogger(Initializer.class);

    private Thread[] m_workers;

    /**
     * 
     */
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
    @Override
    public void init(DomainInitEvent e) {
        super.init(e);

        /* Register object instantiator for Search                            */
        e.getFactory().registerInstantiator(
            Search.BASE_DATA_OBJECT_TYPE,
            new ACSObjectInstantiator() {
                @Override
                public DomainObject doNewInstance(DataObject dataObject) {
                    return new Search(dataObject);
                }
            });

        SearchGroup.setSearchTimeout
            (Search.getConfig().getSearchTimeout().intValue());

      	registerLimitedSimpleSearch();
        registerRemoteSearch();
        
        //Register the ApplicationManager implementation for this Application
        ApplicationManagers.register(new SearchAppManager());
    }

    /**
     * 
     * @param evt 
     */
    @Override
    public void init(ContextInitEvent evt) {

        int nWorkers = Search.getConfig().getNumberOfThreads().intValue();
        if (s_log.isDebugEnabled()) {
            s_log.debug("Starting " + nWorkers + " worker threads");
        }

        m_workers = new Thread[nWorkers];
        for (int i = 0 ; i < nWorkers ; i++) {
            if (s_log.isDebugEnabled()) {
                s_log.debug("Starting thread " + i);
            }
            m_workers[i] = new RemoteSearcher(SearchJobQueue.getInstance());
            m_workers[i].start();
        }
         
    }

    /**
     * 
     */
    @Override
    public void close(ContextCloseEvent evt) {

        int nWorkers = m_workers.length;
        for (int i = 0 ; i < nWorkers ; i++) {
            if (s_log.isDebugEnabled()) {
                s_log.debug("Starting thread " + i);
            }
            m_workers[i].stop();
        }

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
