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


import org.apache.lucene.search.Filter;
import org.apache.lucene.index.IndexReader;

import java.util.BitSet;
import java.io.IOException;

import org.apache.log4j.Logger;


/**
 * A filter whose bitset corresponds to the
 * logical AND of the bitsets from the wrapped filters.
 * Thus it selects the intersection of results selected 
 * by each wrapped filter.
 */
public class IntersectionFilter extends Filter {

    private static final Logger s_log = 
        Logger.getLogger(IntersectionFilter.class);
    
    private Filter[] m_filters;
    
    public IntersectionFilter(Filter[] filters) {
        m_filters = filters;
    }
    /**
     * @return bitset for the AND of all wrapped filters
     */
    public BitSet bits(IndexReader reader)
        throws IOException {
        
        BitSet bits = new BitSet(reader.maxDoc());
        
        for (int i = 0 ; i < m_filters.length ; i++) {
            if (s_log.isDebugEnabled()) {
                s_log.debug("Adding filter " + m_filters[i].getClass());
            }
            // First filter is special since bit sets
            // start off with no bits set.
            if (i == 0) {
                bits.or(m_filters[i].bits(reader));
            } else {
                bits.and(m_filters[i].bits(reader));
            }
        }
        
        return bits;
    }
}
