/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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
import java.math.BigDecimal;
import com.arsdigita.kernel.Party;

/**
 *
 * PartyFilter that filters the query based on the passed in party
 *
 * @author Randy Graebner (randyg@alum.mit.edu)
 * @version $Id: PartyFilter.java 610 2005-06-23 15:50:05Z sskracic $
 *
 **/

public class PartyFilter extends Filter {

    private BigDecimal m_partyID;
    private String m_partyType;

    /**
     * Creates a new PartyFilter
     *
     * @param party The party to use for the filter
     * @param partyType The type of the party to restrict.  Examples
     *  would be Document.CREATION_USER and Document.LAST_MODIFIED_USER
     *
     **/
    public PartyFilter(Party party, String partyType) {
        m_partyID = party.getID();
        m_partyType = partyType;
    }

    /**
     *  This creates a PartyFilter based on the party with the
     *  passed in ID
     */
    public PartyFilter(BigDecimal partyID, String partyType) {
        m_partyID = partyID;
        m_partyType = partyType;
    }

    /**
     * Returns a BitSet with true for documents which
     * should be permitted in search results, and false
     * for those that should not.
     **/
    final public BitSet bits(IndexReader reader) throws IOException {
        BitSet bits = new BitSet(reader.maxDoc());
        TermDocs enu = reader.termDocs(new Term(m_partyType, m_partyID.toString()));

        while (enu.next()) {
            bits.set(enu.doc());
        }

        return bits;
    }
}
