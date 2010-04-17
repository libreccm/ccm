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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * tag for including the contents of a slave page within the
 * result page's current output context.
 * 
 * @version $Id: ShowSlave.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class ShowSlave extends ShowComponent {

    public int doEndTag() throws JspException {
        com.arsdigita.xml.Document wrapper =
            (com.arsdigita.xml.Document)pageContext.getRequest()
            .getAttribute(SLAVE_DOC);
        if (wrapper == null) {
            throw new JspException("Cannot use slave tag except in master page");
        }
        Document doc = wrapper.getInternalDocument();
        Element topLevel = doc.getDocumentElement();
        ShowContainer parent = getContainerTag();
        // topLevel is
        // <bebop:page>
        //   <bebop:title/>
        //   [stuff we want to add]
        // </bebop:page>
        NodeList children = topLevel.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (i > 0 || !child.getNodeName().equals("bebop:title")) {
                Node toAdd = getResultDocument().importNode(child, true);
                parent.getOutputContext().appendChild(toAdd);
            }
        }
        return EVAL_PAGE;
    }
}
