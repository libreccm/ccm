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

/**
 * JSP tag handler to show the current item within a show:list
 * or show:table tag.  For aesthetics in naming it may be mapped to
 * more than one JSP tag.
 *
 * <p>
 * sample Usage:
 *  <pre>
 * &lt;show:list name="mylist">
 *    show list item here <show:listItem/>
 * &lt;show:list name="mylist">
 * </pre>
 *
 *  <pre>
 * &lt;show:row name="mylist">
 *    show table column value <show:col/>
 * &lt;show:row name="mylist">
 * </pre>
 *
 * This has the effect of locally-styling the list contents in the JSP,
 * while globally styling the individual item inside the list.
 * 
 * @version $Id: ShowListItem.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class ShowListItem extends ShowComponent {

    public int doEndTag() {
        // we don't have a named component, but we find our parent
        // show:list tag copy the contents of the current cell over
        // into our result output context.
        ShowCellList tag = (ShowCellList)findAncestorWithClass
            (this, ShowCellList.class);
        Node n = tag.getCurrentCell();
        Node toAdd = getResultDocument().importNode(n, true);
        ShowContainer parent = getContainerTag();
        parent.getOutputContext().appendChild(toAdd);
        return EVAL_PAGE;
    }
}
