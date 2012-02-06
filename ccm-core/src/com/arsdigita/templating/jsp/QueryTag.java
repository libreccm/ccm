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


import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * Sets up a data query for use in a page.
 *
 * <p>Usage:</p>
 *
 * <pre><code>&lt;acs:master path="path/to/master.jsp" /&gt;</code></pre>
 */
public class QueryTag extends BodyTagSupport {

    private String name = null;

    public QueryTag() {

        super();
    }

    public void setName(String name) {

        this.name = name;
    }

    @Override
    public int doStartTag() throws JspTagException {

        Session session = SessionManager.getSession();
        DataQuery dataQuery = session.retrieveQuery(name);
        pageContext.setAttribute("dataQuery", dataQuery);

        return EVAL_BODY_TAG;
    }

    public void release() {

        this.name = null;
    }
}
