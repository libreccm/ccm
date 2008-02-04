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

import com.arsdigita.search.IndexerType;
import com.arsdigita.search.BaseDocument;
import com.arsdigita.search.ResultSet;

import com.arsdigita.persistence.OID;




import java.util.Iterator;
import java.util.Collections;

import java.math.BigDecimal;

import org.apache.log4j.Logger;


class LuceneResultSet implements ResultSet {

    private static final Logger s_log =
        Logger.getLogger(LuceneResultSet.class);

    private LuceneSearch m_search;

    public LuceneResultSet(LuceneSearch search) {
        m_search = search;
    }

    public Iterator getDocuments(long offset,
                                 long count) {
        m_search.setResultRange(offset, count);
        if (s_log.isDebugEnabled()) {
            s_log.debug("Result set count is " + getCount() +
                        ", query duration " + getQueryTime());
        }
        if (s_log.isDebugEnabled()) {
            s_log.debug("Paginating at offset " + offset +
                        " for " + count + " rows");
        }
        if (count == 0) {
            return Collections.EMPTY_LIST.iterator();
        }

        return new LuceneIterator();
    }

    public void close() {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Closing search result set");
        }
        m_search.close();
    }

    public String getEngine() {
        return IndexerType.LUCENE.getKey();
    }

    public long getCount() {
        return m_search.size();
    }

    public long getQueryTime() {
        return m_search.getSearchTime();
    }

    private class LuceneIterator implements Iterator {

        public boolean hasNext() {
            return m_search.next();
        }

        public Object next() {
            BaseDocument result = new BaseDocument(
                new OID(m_search.getType(),
                        m_search.getID()),
                m_search.getLocale(),
                m_search.getTitle(),
                m_search.getSummary(),
                m_search.getCreationDate(),
                null,
                m_search.getLastModifiedDate(),
                null,
                new BigDecimal(m_search.getScore() * 100.0).setScale(0,BigDecimal.ROUND_HALF_UP),
                m_search.getContentSection()
            );
            return result;
        }

        public void remove() {
            throw new UnsupportedOperationException("cannot remove items");
        }
    }
}
