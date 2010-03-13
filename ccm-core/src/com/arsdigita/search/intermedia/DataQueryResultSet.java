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


import com.arsdigita.search.IndexerType;
import com.arsdigita.search.BaseDocument;
import com.arsdigita.search.ResultSet;

import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.OID;

import com.arsdigita.util.Assert;

import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Collections;

import java.math.BigDecimal;

import org.apache.log4j.Logger;


class DataQueryResultSet implements ResultSet {

    private static final Logger s_log = 
        Logger.getLogger(DataQueryResultSet.class);
    
    private DataQuery m_query;
    private long m_count;
    private long m_queryTime;

    public DataQueryResultSet(DataQuery query) {
        m_query = query;
        Date start = new Date();
        m_count = query.size();
        Date end = new Date();
        
        m_queryTime = end.getTime() - start.getTime();
        
        if (s_log.isDebugEnabled()) {
            s_log.debug("Result set count is " + m_count + 
                        ", query duration " + m_queryTime);
        }
    }

    public Iterator getDocuments(long offset,
                                 long count) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Paginating at offset " + offset + 
                        " for " + count + " rows");
        }
        if (count == 0) {
            return Collections.EMPTY_LIST.iterator();
        }

        m_query.setRange(new Integer((int)offset+1), 
                         new Integer((int)(offset+count+1)));
        
        return new DataQueryIterator(m_query);
    }
    
    public String getEngine() {
        return IndexerType.INTERMEDIA.getKey();
    }

    public long getCount() {
        return m_count;
    }
    
    public long getQueryTime() {
        return m_queryTime;
    }
    
    public void close() {
        m_query.close();
    }
    
    private class DataQueryIterator implements Iterator {
        
        private DataQuery m_query;
        private BaseDocument m_current;
        private boolean m_hasNext;
        
        public DataQueryIterator(DataQuery query) {
            m_query = query;
            peekNext();
        }
        
        public boolean hasNext() {
            return m_hasNext;
        }
        
        public Object next() {
            Assert.isTrue(m_hasNext, "hasNext");

            BaseDocument result = m_current;
            peekNext();
            return result;
        }
        
        private void peekNext() {
            m_hasNext = m_query.next();
            if (m_hasNext) {
                m_current = new BaseDocument(
                    new OID((String)m_query.get(BaseQueryEngine.OBJECT_TYPE),
                            (BigDecimal)m_query.get(BaseQueryEngine.OBJECT_ID)),
                    new Locale((String)m_query.get(BaseQueryEngine.LANGUAGE),
                               "us"),
                    (String)m_query.get(BaseQueryEngine.LINK_TEXT),
                    (String)m_query.get(BaseQueryEngine.SUMMARY),
                    null,
                    null,
                    null,
                    null,
                    (BigDecimal)m_query.get(BaseQueryEngine.SCORE),
		    (String)m_query.get(BaseQueryEngine.CONTENT_SECTION)
                );
            } else {
                m_current = null;
            }
        }
        
        public void remove() {
            throw new UnsupportedOperationException("cannot remove items");
        }
    }    
}
