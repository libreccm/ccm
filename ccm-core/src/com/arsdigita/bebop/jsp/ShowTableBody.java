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
package com.arsdigita.bebop.jsp;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.TreeWalker;

/**
 * JSP tag handler to iterate over rows in a model-backed Table body.
 * @see com.arsdigita.bebop.jsp.ShowTable
 * @version $Id: ShowTableBody.java 287 2005-02-22 00:29:02Z sskracic $
 */

public class ShowTableBody extends ShowCellList {

    /**
     * Returns a TreeWalker to pick out rows in the bebop:tbody
     * section of this table.
     */
    protected TreeWalker makeTreeWalker() {
        // walker will walk over columns in header
        // allowing each to be styled dynamically'
        // (man I should be using XPath here.)
        // select="./bebop:tbody/bebop:trow"
        TreeWalker tw = createTreeWalker(getInputContext(), new NodeFilter() {
                public short acceptNode(Node n) {
                    if (n.getNodeName().equals("bebop:trow")) {
                        return FILTER_ACCEPT;
                    } else if (n.getNodeName().equals("bebop:tbody")) {
                        return FILTER_SKIP;
                    } else if (n instanceof Element) {
                        return FILTER_REJECT;
                    } else {
                        return FILTER_SKIP;
                    }
                }
            });
        return tw;
    }
}
