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

import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.TreeWalker;

/**
 * JSP tag handler to show contents from a model-backed List.
 * <p>
 * Usage:
 * <pre>
 * &lt;show:list name="mylist"/>
 * </pre>
 * ... displays the list at this point in the JSP with global styling
 * for the list and its contents.  Or,
 *
 *  <pre>
 * &lt;show:list name="mylist">
 *    show list item here <show:listItem/>
 * &lt;/show:list>
 * </pre>
 *
 * ... has the effect of locally-styling the list contents in the JSP,
 * while globally styling the individual item inside the list.
 * <p>
 * Note that a Bebop List isn't really a Container, but from the JSP's
 * perspective of manipulating Bebop XML output, and not the components
 *  themselves, the behavior is container-like.
 * 
 * @version $Id: ShowList.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class ShowList extends ShowCellList {

    /**
     * set up the input context as our parent class would except
     * evaluate the body of the show:list tag only if the list in
     * question isn't empty.
     */
    protected TreeWalker makeTreeWalker() {
        TreeWalker tw = createTreeWalker(getInputContext(), new NodeFilter() {
                public short acceptNode(Node n) {
                    if (n.getNodeName().equals("bebop:cell")) {
                        return FILTER_ACCEPT;
                    }
                    return FILTER_SKIP;
                }
            });
        return tw;
    }
}
