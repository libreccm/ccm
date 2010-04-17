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

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Link;
import javax.servlet.jsp.JspException;

/**
 * Tag handler for definining a Link.
 *
 * @version $Id: DefineLink.java 287 2005-02-22 00:29:02Z sskracic $ 
 */
public class DefineLink extends DefineComponent {

    private Link m_link;
    private String m_url;
    private String m_label;

    // must override this method because we're not adding the
    // link to the page until the *end* of the tag.  (Otherwise we
    // don't know the contents.)
    public int doStartTag() throws JspException {
        getParentTag().doAfterBody();
        return EVAL_BODY_TAG;
    }

    public int doAfterBody() throws JspException {
        m_label = bodyContent.getString();
        return SKIP_BODY;
    }

    public int doEndTag() throws JspException {
        m_link = new Link(m_label, m_url);
        if (getName() != null) {
            m_link.setIdAttr(getName());
        }
        getParentTag().addComponent(m_link);
        return super.doEndTag();
    }

    protected final Component getComponent() {
        return m_link;
    }

    public void setUrl(String s) {
        m_url = s;
    }
}
