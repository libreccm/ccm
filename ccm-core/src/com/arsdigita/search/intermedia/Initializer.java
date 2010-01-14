/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
 *
 */
package com.arsdigita.search.intermedia;

import com.arsdigita.db.DbHelper;
// import com.arsdigita.initializer.Configuration;
// import com.arsdigita.initializer.InitializationException;
import com.arsdigita.runtime.ConfigError;
import com.arsdigita.runtime.ContextCloseEvent;
import com.arsdigita.runtime.DataInitEvent;
import com.arsdigita.runtime.DomainInitEvent;
import com.arsdigita.runtime.ContextInitEvent;
import com.arsdigita.search.FilterType;
import com.arsdigita.search.IndexerType;
import com.arsdigita.search.QueryEngineRegistry;
import com.arsdigita.search.Search;
import com.arsdigita.search.filters.CategoryFilterType;
import com.arsdigita.search.filters.ObjectTypeFilterType;
import com.arsdigita.search.filters.PermissionFilterType;

import org.apache.log4j.Logger;

/**
 * Initializes the Intermedia package.
 *
 * This initializer is a sub-initializer of the core initializer which adds it
 * to the list of initializers to be executed
 *
 * @author Peter Boy (pboy@barkhof.uni-bremen.de)
 * @version $Id: $
 *
 */
public class Initializer extends com.arsdigita.runtime.GenericInitializer {

    // Creates a s_logging category with name = to the full name of class
    public static final Logger s_log = Logger.getLogger(Initializer.class);


    /**
     * 
     */
    public Initializer() {
    }


    /**
     * Implementation of the {@link Initializer#init(ContextInitEvent)}
     * method.
     *
     * @param evt The context init event.
     **/

    public void init(ContextInitEvent evt) {
        if (Search.getConfig().isIntermediaEnabled()) {

            if (DbHelper.getDatabase() != DbHelper.DB_ORACLE) {
                throw new ConfigError(
                    "Intermedia searching only works on Oracle, not " +
                    DbHelper.getDatabaseName(DbHelper.getDatabase()));
            }

            IntermediaConfig conf = IntermediaConfig.getConfig();

            // Multiply by 1000 to convert from seconds to milliseconds
            BuildIndex.setParameterValues( conf.getTimerDelay() * 1000,
                                           conf.getSyncDelay() * 1000,
                                           conf.getMaxSyncDelay() * 1000,
                                           conf.getMaxIndexingTime() * 1000,
                                           conf.getIndexingRetryDelay() * 1000 );

            BuildIndex.startTimer();

            s_log.debug("Registering query engines");
            QueryEngineRegistry.registerEngine(IndexerType.INTERMEDIA,
                                               new FilterType[] {
                                                   new PermissionFilterType(),
                                                   new ObjectTypeFilterType(),
                                                   new CategoryFilterType()
                                               },
                                               new BaseQueryEngine());
            } else {
                s_log.debug("Intermedia search engine not enabled. Initialization skipped.");
            }
     }

    /**
     *
     */
    public void close(ContextCloseEvent evt) {
            if (Search.getConfig().isIntermediaEnabled()) {
                BuildIndex.stopTimer();
        }
    }


}
