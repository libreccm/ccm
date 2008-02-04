/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.templating.jsp;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * Embeds the current JSP within the body of another JSP.
 *
 * <p>Usage:</p>
 *
 * <pre><code>&lt;acs:master path="path/to/master.jsp" />
 **/
public class MasterTag extends TagSupport {

    static String SLAVE_ATTRIBUTE = "slavePath";

    private String path = null;

    public MasterTag() {

        super();
    }

    public void setPath(String path) {

        this.path = path;
    }

    public int doStartTag() throws JspTagException {

        HttpServletRequest request;
        request = (HttpServletRequest) pageContext.getRequest();

        if (request.getAttribute(SLAVE_ATTRIBUTE) == null) {

            try {

                request.setAttribute(SLAVE_ATTRIBUTE, request.getServletPath());
                pageContext.forward(path);

            } catch (Exception e) {

                e.printStackTrace();
                throw new JspTagException(e.getMessage());
            }
        }

        return SKIP_BODY;
    }

    public void release() {

        this.path = null;
    }
}
