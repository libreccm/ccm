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

import com.arsdigita.xml.Element;

/**
 * 
 * A column in a {@link Multiple}.  Outputs the value of the indicated column.
 *
 * @author Christian Brechb&uuml;hler (christian@arsdigita.com)
 *
 * @version $Id: Column.java 287 2005-02-22 00:29:02Z sskracic $
 * */
public class Column extends SimpleComponent  {

    /** Parent holding RowSequence */
    private Multiple m_parent;
    /** name of the column to output */
    private String m_name;

    /* Create a column.
     * @param parent indicates in to which {@link Multiple} this
     * column belongs.  This is indication is necessary when Multiples
     * nest.  In the page structure, the indicated parent should
     * become an ancestor.
     * @param name the column to be output.  */
    public Column (Multiple parent, String name) {
        m_parent = parent;
        m_name   = name;
    }

    /**
     * Adds the value of the column as a <code>&lt;bebop:cell&gt;</code>.
     */
    public void generateXML(PageState state, Element parent) {
        if ( isVisible(state) ) {
            Element item  = parent.newChildElement("bebop:cell", BEBOP_XML_NS);
            item.setText(m_parent.getColumn(state, m_name).toString());
        }
    }
}
