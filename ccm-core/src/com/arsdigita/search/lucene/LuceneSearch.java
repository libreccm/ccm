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

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.HitCollector;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;

import com.arsdigita.search.Search;
import com.arsdigita.util.UncheckedWrapperException;

/**
 *
 * LuceneSearch is a wrapper for the Lucene search facilities. It contains
 * constructors to create new searches and methods to iterate over and to get
 * information for each hit.
 *
 * @author Richard Su (richard.su@alum.mit.edu)
 * @version $Id: LuceneSearch.java 1846 2009-03-06 04:17:36Z terry $
 *
 **/

public class LuceneSearch {

    private static final Logger LOG = Logger.getLogger(LuceneSearch.class);

    private org.apache.lucene.document.Document m_doc;
    private float m_score;

    private Query m_query;
    private Filter m_filter;

    private long m_offset;
    private long m_howmany;

    private List m_hits;
    private Iterator m_hitIterator;
    private int m_size;
    private long m_searchTime = 0;

    private boolean m_rangeSet = false;

    private static final LuceneLock LOCK = LuceneLock.getInstance();

    /**
     *  This is maximum number of search results that Lucene can return by default.
     * If we need more than this, eg. for page 12 of search results we need entries
     * 111-120, we have to use different (slower) search methods.
     */
    public static final int LUCENE_MAX_HITS = 100;

    /**
     * Search over all objects in the system. Returns objects that matches
     * the search string.
     *
     * @param searchString user specified search string
     **/
    public LuceneSearch(String searchString) {
        this(searchString, (Filter) null);
    }

    /**
     * Search for a specific ACS object and search string.
     *
     * @param searchString user specified search string
     * @param objectType ACS object type
     **/
    public LuceneSearch(String searchString, String objectType) {
        this(searchString, new ObjectTypeFilter(objectType));
    }

    /**
     * Search over all objects in the system using a filter
     *
     * @param searchString user specified search string
     * @param f a filter
     **/
    public LuceneSearch(String searchString, Filter f) {
        m_filter = f;
        try {
            LuceneConfig conf = LuceneConfig.getConfig();
            Analyzer analyzer = conf.getAnalyzer();
            QueryParser parser = new QueryParser(Document.CONTENT, analyzer);
            m_query = parser.parse(searchString);
        } catch (ParseException ex) {
            LOG.fatal("failed parsing the expression: " + searchString, ex);
        }
    }

    /**
     * Search given a preformed query.
     *
     * @param q a performed query
     **/
    public LuceneSearch(Query q) {
        m_query = q;
        m_filter = null;
    }

    /**
     *   @param offset indicates how many documents will be skipped, 0 means
     *                 retrieve top-most hits
     *   @param howmany maximum size of the result set
     */
    public void setResultRange(long offset, long howmany) {
        m_rangeSet = true;
        m_offset = offset;
        m_howmany = howmany;
    }


    private void performSearch() {
        if (LOG.isInfoEnabled()) {
            LOG.info("About to perform search, query = " + m_query + ", filter = " + m_filter);
        }
        long st = System.currentTimeMillis();
        m_hits = new ArrayList();
        if (!m_rangeSet) {
            // Result range not set.  This happens if we're retrieving first 5 pages of the
            // search results.  On later pages, CachedResultSet concludes that requested
            // documents cannot possibly be in the result cache, so it issues another
            // search request, with explicitly stated requested document range.
            m_offset = 0;
            m_howmany = Search.CACHE_SIZE;
            LOG.info("Result range not set.  Retrieving first " + m_howmany + " hits from the result set");
        }
        if (m_offset + m_howmany <= LUCENE_MAX_HITS) {
            performClassicSearch();
        } else {
            performCustomSearch();
        }
        m_hitIterator = m_hits.iterator();
        m_searchTime = System.currentTimeMillis() - st;
    }


    private void performClassicSearch() {
        if (LOG.isInfoEnabled()) {
            LOG.info("Performing classic search, offset + howmany = " + (m_offset + m_howmany));
        }
        Hits hits = null;
        IndexSearcher index = null;
        try {
            synchronized(LOCK) {
                index = new IndexSearcher(Index.getLocation());
                hits = index.search(m_query, m_filter);
                m_size = hits.length();
            }
        } catch (IOException ex) {
            LOG.error("search failed", ex);
            return;
        }
        int docID = 0;
        try {
            int ndx = (int) m_offset;
            while (ndx < hits.length()  &&  ndx < m_offset + m_howmany) {
                docID = hits.id(ndx);
                float score = hits.score(ndx);
                m_hits.add(new SearchResultWrapper(docID, score, hits.doc(ndx++)));
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Match #" + ndx + " doc: " + docID + " score: " + score);
                }
            }
        } catch (IOException ioe) {
            LOG.error("Can't retrieve doc #" + docID + " from the result set", ioe);
            return;
        }
        closeSearchIndex(index);
    }




    private void performCustomSearch() {
        if (LOG.isInfoEnabled()) {
            LOG.info("Performing CUSTOM search, offset + howmany = " + (m_offset + m_howmany));
        }
        IndexSearcher index = null;
        List unsortedHits = new ArrayList();
        try {
            synchronized(LOCK) {
                index = new IndexSearcher(Index.getLocation());
                index.search(m_query, m_filter, new CustomHitCollector(unsortedHits));
            }
        } catch (IOException ex) {
            LOG.error("search failed" , ex);
            return;
        }
        m_size = unsortedHits.size();
        // Results from a custom search are not sorted.  We must explicitly sort them now,
        // with the documents having highest score on the top.
        Collections.sort(unsortedHits, new Comparator() {
            public int compare(Object o1, Object o2) {
                CustomHitWrapper hit1 = (CustomHitWrapper) o1;
                CustomHitWrapper hit2 = (CustomHitWrapper) o2;
                if (hit1.getScore() > hit2.getScore()) {
                    return -1;
                }
                if (hit1.getScore() < hit2.getScore()) {
                    return 1;
                }
                // by default, Lucene orders documents by something similar to
                // "ORDER BY score DESC, id ASC", so make sure we follow this
                // to avoid all surprises
                return hit1.getDocID() - hit2.getDocID();
            }
            public boolean equals(Object obj) {
                return equals(obj);
            }
          });

        if (LOG.isDebugEnabled()) {
            LOG.debug("Dumping sorted result set");
            int count = 0;
            Iterator i = unsortedHits.iterator();
            while (i.hasNext()) {
                CustomHitWrapper hit = (CustomHitWrapper) i.next();
                LOG.debug("SORT #" + ++count + " doc: " + hit.getDocID() + " score: " + hit.getScore());
            }
        }
        // Finally populate the search results

        int ndx = (int) m_offset;
        while (ndx < m_size  &&  ndx < m_offset + m_howmany) {
            CustomHitWrapper chw = (CustomHitWrapper) unsortedHits.get(ndx++);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Match #" + ndx + " is within the requested document range");
                LOG.debug("HIT doc: " + chw.getDocID() + " score: " + chw.getScore());
            }
            try {
                m_hits.add(new SearchResultWrapper(chw.getDocID(), chw.getScore(), index.doc(chw.getDocID())));
            } catch (IOException ioe) {
                LOG.error("Could not retrieve doc #" + chw.getDocID() + " from search index", ioe);
            }
        }

        closeSearchIndex(index);
    }


    private class CustomHitCollector extends HitCollector {

        private List m_unsortedHits;
        private int count = 0;

        public CustomHitCollector(List unsortedHits) {
            m_unsortedHits = unsortedHits;
        }

        public void collect(int doc, float score) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Match #" + ++count + " doc: " + doc + " score: " + score);
            }
            m_unsortedHits.add(new CustomHitWrapper(doc, score));
        }
    }



    private void closeSearchIndex(IndexSearcher index) {
        try {
            index.close();
        } catch (IOException ioe) {
            LOG.error("Can't close search index", ioe);
        }
    }

    /**
     * Returns the number of hits in this query.
     **/

    public int size() {
        if (m_hits == null) {
            performSearch();
        }
        return m_size;
    }

    public long getSearchTime() {
        if (m_hits == null) {
            LOG.error("Search must be performed before calling getSearchTime()");
        }
        return m_searchTime;
    }

    /**
     * Returns true if the search has more results
     *
     * @return true if the search has more results
     **/
    public boolean next() {
        if (m_hits == null) {
            performSearch();
        }
        if (m_hitIterator.hasNext()) {
            SearchResultWrapper shw = (SearchResultWrapper) m_hitIterator.next();
            m_doc = shw.getDoc();
            m_score = shw.getScore();
            return true;
        }
        return false;
    }

    /**
     * Closes this search.  This is now no-op.
     **/
    public void close() {
    }

    /**
     * Returns the score of the current hit.
     **/

    public float getScore() {
        return m_score;
    }

    /**
     * Returns the ACS object ID of the current search hit.
     *
     * @return the object id
     **/
    public BigDecimal getID() {
        return new BigDecimal(m_doc.get(Document.ID));
    }

    /**
     * Returns the locale the current search hit is in.
     *
     * @return the locale the content is in
     **/
    public Locale getLocale() {
        String language = m_doc.get(Document.LANGUAGE);
        String country = m_doc.get(Document.COUNTRY);
        if ("".equals(language) && "".equals(country)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug( "Document: " + getID() + ", Language: " + language +
                             ", Country: " + country );
            }
            return null;
        } else {
            return new Locale(language, country);
        }
    }

    /**
     * Returns the object type of the current search hit.
     *
     * @return the object type
     **/
    public String getType() {
        return m_doc.get(Document.TYPE);
    }

    /**
     * Returns type-specific info of the current search hit.
     *
     * @return the type-specific info
     **/
    public String getTypeSpecificInfo() {
        return m_doc.get(Document.TYPE_SPECIFIC_INFO);
    }

    /**
     * Returns the title of the current search hit.
     *
     * @return the link text for the object
     **/
    public String getTitle() {
        return m_doc.get(Document.TITLE);
    }

    /**
     * Returns a summary for the current search hit.
     *
     * @return a summary for the hit
     **/
    public String getSummary() {
        return m_doc.get(Document.SUMMARY);
    }

    /**
     * Returns the content of the current search hit, as it is stored in Lucene index.
     *
     * @return the content
     **/
    public String getContent() {
        return m_doc.get(Document.CONTENT);
    }


    /**
     * Returns the creation date of the current search hit.
     *
     * @return the creation date.
     **/
    public Date getCreationDate() {
        return toDate(m_doc.get(Document.CREATION_DATE));
    }

    /**
     * Returns the last modified date of the current search hit.
     *
     * @return the last modified date.
     **/
    public Date getLastModifiedDate() {
        return toDate(m_doc.get(Document.LAST_MODIFIED_DATE));
    }

    /**
     * Returns a Date.
     *
     * @return Date.
     **/
    private Date toDate(String date) {
        if (date == null || date.equals("")) {
            return null;
        } else {
            try {
                return DateTools.stringToDate(date);
            } catch (java.text.ParseException e) {
                throw new UncheckedWrapperException(e);
            }
        }
    }

    /**
     * Gets the title of the content section of the current search hit.
     *
     * @return content section title
     **/
    public String getContentSection() {
        return m_doc.get(Document.CONTENT_SECTION);
    }


    private class CustomHitWrapper {

        // Lucene search document id
        private int id;
        private float score;

        public CustomHitWrapper(int docID, float s) {
            id = docID;
            score = s;
        }

        public float getScore() {
            return score;
        }

        public int getDocID() {
            return id;
        }
    }

    private class SearchResultWrapper extends CustomHitWrapper {

        private org.apache.lucene.document.Document doc;

        public SearchResultWrapper(int docID, float s, org.apache.lucene.document.Document d) {
            super(docID, s);
            doc = d;
        }

        public org.apache.lucene.document.Document getDoc() {
            return doc;
        }

    }
}

