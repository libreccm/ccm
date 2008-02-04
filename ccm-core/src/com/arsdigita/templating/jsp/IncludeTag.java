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


import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * Includes a JSP component in the current JSP.
 *
 * <p>Usage:</p>
 *
 * <pre><code>&lt;acs:include path="path/to/component.jsp" />
 **/
public class IncludeTag extends TagSupport {

    private String path = null;

    public IncludeTag() {

        super();
    }

    public void setPath(String path) {

        this.path = path;
    }

    public int doStartTag() throws JspTagException {

        try {

            pageContext.include(path);

        } catch (Exception e) {
            throw new JspTagException(e.getMessage());
        }

        return EVAL_PAGE;
    }

    public void release() {

        this.path = null;
    }
}
