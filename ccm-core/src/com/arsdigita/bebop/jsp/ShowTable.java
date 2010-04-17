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


/**
 * JSP tag handler to show contents from a model-backed Table.
 * <p>
 * Usage:
 * <pre>
 * &lt;show:table name="mylist"/>
 * </pre>
 * ... displays the list at this point in the JSP with global styling
 * for the list and its contents.  Or,
 *
 *  <pre>
 * &lt;show:table name="table">
 *    &lt;show:thead> &lt!-- iterates over table header columns -->
 *      column header: &lt;show:col/> :column header
 *    &lt;/show:thead>
 *    &lt;show:tbody> &lt!-- iterates over table rows -->
 *      table row starts here:
 *         &lt;show:row>
 *            table column: &lt;show:col/> :table column
 *         &lt;/show:row>
 *      :table row ends here
 *    &lt;/show:tbody>
 * &lt;/show:table>
 * </pre>
 *
 * ... has the effect of locally-styling the table contents in the JSP,
 * while globally styling the individual components inside the table
 * if there is a TableCellRenderer involved.
 * <p>
 * Note that a Bebop Table isn't really a Container, but from the JSP's
 * perspective of manipulating Bebop XML output, and not the components
 *  themselves, the behavior is container-like.
 * 
 * @version $Id: ShowTable.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class ShowTable extends ShowContainer {

    // maybe this is just a synonym for ShowContainer?
    // ShowContainer sets up our input context...
}
