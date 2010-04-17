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
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * Base class for instantiating Bebop components with JSP tags.  A
 * Bebop component declaration will always add the created component
 * to the container defined by the containing (parent) tag.   Intervening
 * text in the JSP (e.g., &lt;bebop:form>form label &lt;bebop:widget/>...)
 * is turned into Label objects.
 */

public abstract class DefineComponent extends BodyTagSupport {
    private String m_name;

    private final static String CONTAINER_ERROR =
        "define:component must be contained within a container definition.";

    public int doStartTag() throws JspException {
        if (getName() != null) {
            pageContext.setAttribute(getName(), getComponent());
        }
        // set all components ID
        getComponent().setIdAttr(getName());

        // nearly always, a componentTag will be contained within
        // another tag.  So we want make any intervening text
        // be treated as child Label of that container, and add the
        // component that this ComponentTag declares to the parent
        // container.
        // (PageTag is an exception.)
        getParentTag().doAfterBody();
        getParentTag().addComponent(getComponent());
        return EVAL_BODY_TAG;
    }

    protected DefineContainer getParentTag() throws JspException {
        if (this.getParent() instanceof DefineContainer) {
            return (DefineContainer)this.getParent();
        } else {
            throw new JspException(CONTAINER_ERROR);
        }
    }

    protected abstract Component getComponent();

    public final String getName() {
        return m_name;
    }

    public final void setName(String s) {
        m_name = s;
    }
}
