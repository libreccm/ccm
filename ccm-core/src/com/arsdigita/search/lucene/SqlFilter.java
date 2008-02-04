/*
 * Copyright (C) 2005 Red Hat Inc. All Rights Reserved.
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

import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.FilterFactory;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.search.lucene.Document;
import com.redhat.persistence.engine.rdbms.RDBMSEngine;
import java.io.IOException;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.search.Filter;

/**
 *
 * A filter based on the arbitrary SQL query that returns
 * document IDs that are allowed in search results.
 *
 * @author Sebastian Skracic (sskracic@redhat.com)
 * @version $Id: SqlFilter.java 1375 2006-11-17 10:01:37Z sskracic $
 *
 **/

public class SqlFilter extends Filter {

    private static final Logger s_log = Logger.getLogger(SqlFilter.class);

    private DataQuery m_query;

    private int m_clauses;

    public SqlFilter() {
        m_query = SessionManager.getSession().retrieveQuery("com.arsdigita.search.getLuceneDocIDs");
        m_clauses = 0;
    }

    /**
     * Appends a clause to the SqlFilter
     *
     * @param query PDL query that will be used to constraint the result set
     * @param propertyName name of the query property that holds the document ID column
     *
     **/
    public com.arsdigita.persistence.Filter appendClause(String query, String propertyName) {
        m_clauses++;
        return m_query.addInSubqueryFilter("id", propertyName, query);
    }

    /**
     * Appends a (persistence) filter to the SqlFilter
     *
     * @param f PDL filter to be ANDed with all other clauses
     *
     **/
    public void appendClause(com.arsdigita.persistence.Filter f) {
        m_clauses++;
        m_query.addFilter(f);
    }

    /**
     * Returns a BitSet with true for documents which
     * should be permitted in search results, and false
     * for those that should not.
     **/
    final public BitSet bits(IndexReader reader) throws IOException {

        // First build CCM ID <-> lucene ID mappings
        long st = System.currentTimeMillis();
        Map luceneIds = new HashMap();
        TermDocs docs = reader.termDocs();
        TermEnum terms = reader.terms();
        boolean hasTerms = terms.skipTo(new Term(Document.ID, ""));
        while (hasTerms) {
            Term t = terms.term();
            String field = t.field();
            String text = t.text();
            if (!Document.ID.equals(field)) {
                break;
            }
            docs.seek(t);
            if (docs.next()) {
                luceneIds.put(text, new Integer(docs.doc()));
            }
            hasTerms = terms.next();
        }
        terms.close();
        docs.close();
        if (s_log.isDebugEnabled()) {
            s_log.debug("Mapped " + luceneIds.size() + " IDs in " + (System.currentTimeMillis() - st) + "ms");
            st = System.currentTimeMillis();
        }
        BitSet bits = new BitSet(reader.maxDoc());
        if (s_log.isDebugEnabled()) {
            s_log.debug("Created BitSet with " + bits.size() + " bits");
        }

        int i=0;
        // Don't limit the size of the resultset window, so that we fetch
        // rows as fast as possible.
        m_query.setOption(RDBMSEngine.OPTION_WINDOW_SIZE, new Integer(0));
        while (m_query.next()) {
            String ccmID = m_query.get("id").toString();
            Integer luceneID = (Integer) luceneIds.get(ccmID);
            if (luceneID == null) {
                s_log.info("Could not find document ID " + ccmID + " in Lucene index");
            } else {
                i++;
                bits.set(luceneID.intValue());
            }
        }
        if (s_log.isDebugEnabled()) {
            s_log.debug("Created SQLFilter with " + m_clauses + " clause(s) matching " + i + " object(s)");
            s_log.debug("Time taken: " + (System.currentTimeMillis() - st) + "ms");
        }
        return bits;
    }

    public int getNumClauses() {
        return m_clauses;
    }

    public FilterFactory getFilterFactory() {
        return m_query.getFilterFactory();
    }
}
