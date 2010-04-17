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
import com.arsdigita.bebop.Container;
import com.arsdigita.bebop.Label;
import org.apache.log4j.Logger;

/**
 * Tag for defining a Bebop container in a JSP.
 *
 * @version $Id: DefineContainer.java 287 2005-02-22 00:29:02Z sskracic $
 */
public abstract class DefineContainer extends DefineComponent {

    private static final Logger s_log =
        Logger.getLogger(DefineComponent.class.getName());

    /**
     * If we have text intervening between tags, then we turn it into
     * a Bebop Label object so it becomes part of the Page's component
     * hierarchy.
     * <p>
     * doAfterBody would be called by the JSP container at the end
     * of a tag, but we call it from ContainerTag.doStartTag() manually
     * so we get the text here in several discrete chunks separated
     * by our child component tags, instead of in one big chunk at the
     * end.
     */
    public int doAfterBody() {
        if (bodyContent == null) {
            return SKIP_BODY;
        }

        String body = bodyContent.getString();
        if (body != null && body.trim().length() > 0) {
            // create new Label object
            // with output escaping disabled, so that you can
            // put badly-formed HTML in your JSP pages.
            // (the other option is to parse $body as XML.)
            Label l = new Label(body, false);

            // now find our parent object and put it in.
            this.addComponent(l);

            try {
                bodyContent.clear();
            } catch (java.io.IOException ioe) {
                try {
                    pageContext.handlePageException(ioe);
                } catch (Exception nested) {
                    s_log.error("error serving error page", nested);
                }
                throw new JspWrapperException(ioe);
            }
        }
        return SKIP_BODY;
    }

    /**
     * adds a component to the container represented by this tag.
     */
    public void addComponent(Component c) {
        ((Container)this.getComponent()).add(c);
    }
}
