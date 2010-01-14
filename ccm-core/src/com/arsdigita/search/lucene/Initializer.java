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
package com.arsdigita.search.lucene;

// import com.arsdigita.initializer.Configuration;
// import com.arsdigita.initializer.InitializationException;
// import com.arsdigita.runtime.CompoundInitializer;
import com.arsdigita.runtime.ConfigError;
import com.arsdigita.runtime.ContextInitEvent;
import com.arsdigita.runtime.ContextCloseEvent;
// import com.arsdigita.runtime.DataInitEvent;
//import com.arsdigita.runtime.DomainInitEvent;
import com.arsdigita.runtime.GenericInitializer;
// import com.arsdigita.runtime.LegacyInitializer;
// import com.arsdigita.runtime.LegacyInitEvent;
import com.arsdigita.search.FilterType;
import com.arsdigita.search.IndexerType;
import com.arsdigita.search.QueryEngineRegistry;
import com.arsdigita.search.Search;
import com.arsdigita.search.filters.CategoryFilterType;
import com.arsdigita.search.filters.ObjectTypeFilterType;
import com.arsdigita.search.filters.PermissionFilterType;
import java.io.File;
// import java.io.IOException;
import java.util.Date;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.BooleanQuery;

/**
 * Initializes the Lucene package.
 *
 * This initializer is a sub-initializer of the core initializer which adds it
 * to the list of initializers to be executed
 *
 * @author Richard Su (richard.su@alum.mit.edu)
 * @version $Id: Initializer.java 1044 2005-12-09 13:21:16Z sskracic $
 *
 */
public class Initializer extends GenericInitializer {

    // Creates a s_logging category with name = to the full name of class
    public static final Logger s_log = Logger.getLogger(Initializer.class);

    public final static Loader LOADER = new IndexId.LoaderImpl();

    /**
     * 
     */
    public Initializer() {
    }

    /**
     * An empty implementation of {@link Initializer#init(DataInitEvent)}.
     *
     * @param evt The data init event.
     **/
//  public void init(DataInitEvent evt) {
//  }


    /**
     * An empty implementation of {@link Initializer#init(DomainInitEvent)}.
     *
     * @param evt The domain init event.
     **/
//  public void init(DomainInitEvent evt) {
//  }


    /**
     * Implementation of the {@link Initializer#init(LegacyInitEvent)}
     * method.
     *
     * @param evt The legacy init event.
     */
//  public void init(LegacyInitEvent evt) {
//      if (Search.getConfig().isLuceneEnabled()) {
//          // startup();
//      } else {
//          s_log.debug("Lucene search engine not enabled. Initialization skipped.");
//      }
//   }

    /**
     * Implementation of the {@link Initializer#init(ContextInitEvent)}
     * method.
     *
     * @param evt The context init event.
     */
    public void init(ContextInitEvent evt) {
        if (Search.getConfig().isLuceneEnabled()) {
            // startup();

        LuceneConfig conf = LuceneConfig.getConfig();

        String location = conf.getIndexLocation();
        int interval = conf.getIndexerInterval();
        Analyzer analyzer = conf.getAnalyzer();
        s_log.info("Lucene index location: " + location);
        s_log.info("Lucene Analyzer = " + analyzer.getClass().getName());
        BooleanQuery.setMaxClauseCount(Integer.MAX_VALUE);

        try {
            if (!IndexReader.indexExists(location)) {
                File f = new File(location);
                f.mkdirs();
                IndexWriter iw = new IndexWriter
                    (location, analyzer, true);
                iw.close();
                s_log.info("Lucene created index directory");
            }
        } catch (Exception ex) {
            s_log.warn("Lucene initialization: No index directory!");
            throw new ConfigError("lucene index id has not been initialized");
        }

        Index.setLocation(location);
        // FIXME: This is a hack.  Delay the start of the timer task by 5
        // seconds and keep your fingers crosses in hopes that
        // Index.getIndexerID() will be ready to return a valid value by
        // then.  -- vadimn@redhat.com, 2004-01-07
        Date delayedStart = new Date(System.currentTimeMillis() + 5000L);

        s_log.debug("Lucene initialization: Starting index timer!");
        Index.getTimer().schedule
            (new Indexer(Index.getLocation()),
             delayedStart,
             ((long)interval * 1000l));

        QueryEngineRegistry.registerEngine
            (IndexerType.LUCENE, new FilterType[] {
                 new CategoryFilterType(),
                 new ObjectTypeFilterType(),
                 new PermissionFilterType()
              },
             new BaseQueryEngine());

        } else {
            s_log.debug("Lucene search engine not enabled. Initialization skipped.");
        }
     }

    /**
     *
     */
    public void close(ContextCloseEvent evt) {
        if (Search.getConfig().isLuceneEnabled()) {
            shutdown();
        } else {
            s_log.debug("Lucene search engine not enabled. Shutdown skipped.");
        }
     }


    /**
     *  Starts the search indexing timer
     * 
     */
    public void startup() {

        LuceneConfig conf = LuceneConfig.getConfig();

        String location = conf.getIndexLocation();
        int interval = conf.getIndexerInterval();
        Analyzer analyzer = conf.getAnalyzer();
        s_log.info("Lucene index location: " + location);
        s_log.info("Lucene Analyzer = " + analyzer.getClass().getName());
        BooleanQuery.setMaxClauseCount(Integer.MAX_VALUE);

        try {
            if (!IndexReader.indexExists(location)) {
                File f = new File(location);
                f.mkdirs();
                IndexWriter iw = new IndexWriter
                    (location, analyzer, true);
                iw.close();
                s_log.info("Lucene created index directory");
            }
        } catch (Exception ex) {
            s_log.warn("Lucene initialization: No index directory!");
            throw new ConfigError("lucene index id has not been initialized");
        }

        Index.setLocation(location);
        // FIXME: This is a hack.  Delay the start of the timer task by 5
        // seconds and keep your fingers crosses in hopes that
        // Index.getIndexerID() will be ready to return a valid value by
        // then.  -- vadimn@redhat.com, 2004-01-07
        Date delayedStart = new Date(System.currentTimeMillis() + 5000L);

        s_log.debug("Lucene initialization: Starting index timer!");
        Index.getTimer().schedule
            (new Indexer(Index.getLocation()),
             delayedStart,
             ((long)interval * 1000l));

        QueryEngineRegistry.registerEngine
            (IndexerType.LUCENE, new FilterType[] {
                 new CategoryFilterType(),
                 new ObjectTypeFilterType(),
                 new PermissionFilterType()
              },
             new BaseQueryEngine());
    }


    /**
     *  Stopps the search indexing timer
     *
     */
    public void shutdown() {
        s_log.debug("Lucene initialization: Trying to stop index timer!");
        Index.getTimer().cancel();
        s_log.debug("Lucene initialization: Index timer stopped!");
    }

    /**
     * Only used by <code>com.arsdigita.loader.CoreLoader</code>.
     */
    public interface Loader {
        void load();
    }

}
