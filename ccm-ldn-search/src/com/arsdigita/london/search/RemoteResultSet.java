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

import com.arsdigita.search.ResultSet;
import com.arsdigita.search.Document;
import com.arsdigita.search.BaseDocument;

import com.arsdigita.kernel.ACSObject;
import com.arsdigita.persistence.OID;

import com.arsdigita.util.UncheckedWrapperException;

import java.util.Collection;
import java.util.Iterator;
import java.util.Date;
import java.util.Locale;

import java.net.URL;
import java.net.MalformedURLException;

import org.apache.log4j.Logger;


public class RemoteResultSet implements ResultSet {

    private static final Logger s_log
        = Logger.getLogger(RemoteResultSet.class);

    private long m_time;
    private Document[] m_results;

    public RemoteResultSet(SearchGroup group) {
        Date start = new Date();
        Collection results = group.search();
        Date end = new Date();
        
        m_time = end.getTime() - start.getTime();

        m_results = new Document[results.size()];
        
        Iterator iter = results.iterator();
        int i = 0;
        while (iter.hasNext()) {
            SearchResult res = (SearchResult)iter.next();
            
            URL url = null;
            try {
                url = new URL(res.getUrlStub());
            } catch (MalformedURLException ex) {
                throw new UncheckedWrapperException(
                    "malformed url" + res.getUrlStub(),
                    ex);
            }

            m_results[i++] = new BaseDocument(
                url,
                new OID(ACSObject.BASE_DATA_OBJECT_TYPE,
                        res.getID()),
                new Locale("en", "us"),
                res.getLink(),
                res.getSummary(),
                null,
                null,
                null,
                null,
                res.getScore());
        }
    }
    
    public Iterator getDocuments(long offset,
                                 long count) {
        if (offset > m_results.length) {
            throw new IndexOutOfBoundsException(
                "offset: " + offset + 
                ",is greater than result count: " + m_results.length);
        }
        
        return new ResultIterator(m_results,
                                  (int)offset,
                                  (int)count);
    }

    public String getEngine() {
        return "remote";
    }
    
    public long getCount() {
        return m_results.length;
    }

    public long getQueryTime() {
        return m_time;
    }

    public void close() {
        // No-op
    }

    private class ResultIterator implements Iterator {
        private Document[] m_results;
        private int m_current;
        private int m_remaining;

        public ResultIterator(Document[] results,
                              int offset,
                              int count) {
            m_results = results;

            int remaining = m_results.length - offset;
            if (remaining < count) {
                count = remaining;
            }

            m_current = offset;
            m_remaining = count;
        }
        
        public boolean hasNext() {
            return m_remaining > 0;
        }
        
        public Object next() {
            Object result = m_results[m_current];
            m_current++;
            m_remaining--;
            return result; 
        }
        
        public void remove() {
            throw new UnsupportedOperationException(
                "cannot remove search results");
        }
    }
}
