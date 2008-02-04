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

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.search.Filter;

import java.io.IOException;
import java.util.BitSet;
/**
 *
 * ObjectTypeFilter
 *
 * @author Richard Su (richard.su@alum.mit.edu)
 * @version $Id: ObjectTypeFilter.java 610 2005-06-23 15:50:05Z sskracic $
 *
 **/

public class ObjectTypeFilter extends Filter {

    private String m_objectType;

    /**
     * Creates a new ObjectTypeFilter
     *
     * @param objectType the object type to filter on
     *
     **/
    public ObjectTypeFilter(String objectType) {
        m_objectType = objectType;
    }

    /**
     * Returns a BitSet with true for documents which
     * should be permitted in search results, and false
     * for those that should not.
     **/
    final public BitSet bits(IndexReader reader) throws IOException {
        BitSet bits = new BitSet(reader.maxDoc());
        TermDocs enu = reader.termDocs(new Term(Document.TYPE, m_objectType));

        while (enu.next()) {
            bits.set(enu.doc());
        }

        return bits;
    }

}
