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
import java.util.BitSet;

import org.apache.log4j.Logger;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.search.Filter;

/**
 *
 * TypeSpecificFilter
 *
 * @author Vadim Nasardinov (vadimn@redhat.com)
 * @version $Id: TypeSpecificFilter.java 610 2005-06-23 15:50:05Z sskracic $
 *
 **/

public class TypeSpecificFilter extends Filter {

    private final static Logger s_log =
        Logger.getLogger(TypeSpecificFilter.class);

    private String m_typeSpecificInfo;

    /**
     * Creates a new lucene <code>Filter</code> that filters search results
     * based on whether the "type-specific field" matches the terms supplied in
     * the <code>typeSpecificInfo</code> argument.
     *
     * @param typeSpecificInfo the object type to filter on
     *
     **/
    public TypeSpecificFilter(String typeSpecificInfo) {
        m_typeSpecificInfo = typeSpecificInfo;
    }

    /**
     * Returns a <code>BitSet</code> with <code>true</code> for documents which
     * should be permitted in search results, and <code>false</code> for those
     * that should not.
     **/
    public final BitSet bits(IndexReader reader) throws IOException {
        BitSet bits = new BitSet(reader.maxDoc());
        TermDocs enu = reader.termDocs(new Term(Document.TYPE_SPECIFIC_INFO,
                                                 m_typeSpecificInfo));

        while (enu.next()) {
            bits.set(enu.doc());
        }

        return bits;
    }

}
