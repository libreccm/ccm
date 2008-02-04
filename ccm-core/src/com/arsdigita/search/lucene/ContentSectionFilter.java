/*
 * Copyright (C) 2005 Runtime Collective Ltd. All Rights Reserved.
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
 * ContentSectionFilter - checks whether the Document belongs to a ContentSection
 *
 * @author matt@runtime-collective.com
 * @version $Id: ContentSectionFilter.java 610 2005-06-23 15:50:05Z sskracic $
 *
 **/

public class ContentSectionFilter extends Filter {

    private String m_contentSection;

    /**
     * Creates a new ContentSectionFilter
     *
     * @param content section title
     **/
    public ContentSectionFilter(String contentSection) {
        m_contentSection = contentSection;
    }

    /**
     * Returns a BitSet with true for documents which
     * should be permitted in search results, and false
     * for those that should not.
     **/
    final public BitSet bits(IndexReader reader) throws IOException {
        BitSet bits = new BitSet(reader.maxDoc());
        TermDocs enu = reader.termDocs(new Term(Document.CONTENT_SECTION, m_contentSection));

        while (enu.next()) {
            bits.set(enu.doc());
        }

        return bits;
    }

}

