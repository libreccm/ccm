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
 *
 */
package com.arsdigita.bebop;

import java.util.Iterator;
import com.arsdigita.xml.Element;
import com.arsdigita.persistence.DataQuery;

/**
 * .  A
 * container that outputs its components repeatedly for each row in a
 * RowSequence.
 *
 * @author Christian Brechb&uuml;hler (christian@arsdigita.com)
 *
 * @version $Id: Multiple.java 287 2005-02-22 00:29:02Z sskracic $
 * */
public class Multiple extends SimpleContainer
{

    public static final String versionId = "$Id: Multiple.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";
    private RequestLocal m_rows;        // a RowSequence
    private RowSequenceBuilder m_builder = null;

    public Multiple (RowSequenceBuilder builder) {
        m_rows = new RequestLocal();
        m_builder = builder;
    }

    /**
     * Obtains a column from the current row.
     * @param state for getting the (RequestLocal) RowSequence.
     * @param name the column to return
     * @return the named column.
     */
    public Object getColumn(PageState state, String name) {
        return ((DataQuery)m_rows.get(state)).get(name);
    }

    /**
     * Returns the current position within the sequence. The first
     * position is 0.
     *
     * @return the current position; -1 if there is no current position.
     **/
    int getPosition(PageState state) {
        return ((DataQuery)m_rows.get(state)).getPosition() - 1;
    }

    /**
     * Adds child components repeatedly under parent node.
     */
    public void generateXML(PageState state, Element p) {
        if ( ! isVisible(state) ) {
            return;
        }

        Element parent = generateParent(p);

        DataQuery rows = m_builder.makeRowSequence(state);
        m_rows.set(state, rows);        // give nested Components access

        while (rows.next()) {
            // generate XML for children
            for (Iterator i = children(); i.hasNext(); ) {
                Component c = (Component) i.next();
                c.generateXML(state, parent);
            }
        }
    }
}
