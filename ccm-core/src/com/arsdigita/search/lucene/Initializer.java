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

import com.arsdigita.initializer.Configuration;
import com.arsdigita.initializer.InitializationException;
import com.arsdigita.search.FilterType;
import com.arsdigita.search.IndexerType;
import com.arsdigita.search.QueryEngineRegistry;
import com.arsdigita.search.Search;
import com.arsdigita.search.filters.CategoryFilterType;
import com.arsdigita.search.filters.ObjectTypeFilterType;
import com.arsdigita.search.filters.PermissionFilterType;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.BooleanQuery;

/**
 * Initializer
 *
 * Initializes the Lucene package
 *
 * @author Richard Su (richard.su@alum.mit.edu)
 * @version $Id: Initializer.java 1044 2005-12-09 13:21:16Z sskracic $
 *
 */
public class Initializer implements com.arsdigita.initializer.Initializer {

    private static final Logger LOG = Logger.getLogger(Initializer.class);

    public final static Loader LOADER = new IndexId.LoaderImpl();

    private Configuration m_conf = new Configuration();

    public Initializer() {}

    public Configuration getConfiguration() {
        return m_conf;
    }

    /*
     * Called on startup.
     * @throws InitializationException
     */
    public void startup() {
        if (Search.getConfig().isLuceneEnabled()) {
            LuceneConfig conf = LuceneConfig.getConfig();
            String location = conf.getIndexLocation();
            int interval = conf.getIndexerInterval();
            Analyzer analyzer = conf.getAnalyzer();
            LOG.info("Lucene index location: " + location);
            LOG.info("Lucene Analyzer = " + analyzer.getClass().getName());
            BooleanQuery.setMaxClauseCount(Integer.MAX_VALUE);

            try {
                if (!IndexReader.indexExists(location)) {
                    File f = new File(location);
                    f.mkdirs();
                    IndexWriter iw = new IndexWriter
                        (location, analyzer, true);
                    iw.close();
                    LOG.info("Lucene created index directory");
                }
            } catch (IOException ex) {
                throw new InitializationException
                    ("lucene index id has not been initialized", ex);
            }

            Index.setLocation(location);
            // FIXME: This is a hack.  Delay the start of the timer task by 5
            // seconds and keep your fingers crosses in hopes that
            // Index.getIndexerID() will be ready to return a valid value by
            // then.  -- vadimn@redhat.com, 2004-01-07
            Date delayedStart = new Date(System.currentTimeMillis() + 5000L);
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
    }

    /**
     * Called on shutdown. It's probably not a good idea to depend on this
     * being called.
     **/
    public void shutdown() {
    }

    /**
     * Only used by <code>CoreLoader</code>.
     **/
    public interface Loader {
        void load();
    }
}
