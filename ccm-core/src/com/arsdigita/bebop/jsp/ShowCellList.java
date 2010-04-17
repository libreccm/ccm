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

import javax.servlet.jsp.JspException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.TreeWalker;

/**
 * JSP tag handler to show contents from a model-backed one-dimensional
 * list of bebop:cells.  Inside a table, this would represent a table
 * row.  Inside a list, this would just be the list itself.
 *
 * @see com.arsdigita.bebop.jsp.ShowTable
 * @see com.arsdigita.bebop.jsp.ShowList
 * 
 * @version $Id: ShowCellList.java 287 2005-02-22 00:29:02Z sskracic $
 */

public abstract class ShowCellList extends ShowContainer {

    private TreeWalker m_walker;
    private Node m_currentCell;

    /**
     * set up the input context as our parent class would except
     * evaluate the body of the current show:... tag only if the list in
     * question isn't empty.
     */
    public int doStartTag() throws JspException {
        super.doStartTag();
        m_walker = makeTreeWalker();
        return maybeNextCell();
    }

    private int maybeNextCell() {
        m_currentCell = m_walker.nextNode();
        if (m_currentCell != null) {
            // only evaluate body if the list is non-empty
            setInputContext(m_currentCell);
            return EVAL_BODY_TAG;
        } else {
            return SKIP_BODY;
        }
    }

    /**
     * We call perform the same action (handleText) as our parent class's
     * doAfterBody() handler, but we don't always return SKIP_BODY.  Instead,
     * we keep returning EVAL_BODY_TAG for each element in the
     * list until we're out of list elements.
     */
    public int doAfterBody() throws JspException {
        super.doAfterBody();
        return maybeNextCell();
    }

    /**
     * returns the contents of current list cell.  Example: given the
     * list <pre>
     * &lt;bebop:thead>
     *   &lt;bebop:cell>
     *      &lt;bebop:label>...&lt;bebop:label>
     *   &lt;/bebop:cell>
     * &lt;/bebop:thead>
     * </pre>
     *
     * we'd return the bebop:label element.
     * @pre m_currentCell != null
     */
    public Node getCurrentCell() {
        Node cell = m_currentCell;
        NodeList nl = cell.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            // return the first Element child of the current cell
            if (nl.item(i) instanceof Element) {
                return nl.item(i);
            }
        }
        return null;
    }

    /**
     * Creates the tree walker to pick out the cells specific to
     * this model-backed collection.
     */
    protected abstract TreeWalker makeTreeWalker();
}
