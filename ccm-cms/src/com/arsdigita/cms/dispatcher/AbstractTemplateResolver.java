/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.dispatcher;

import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.Template;
import com.arsdigita.cms.TemplateManager;

import javax.servlet.http.HttpServletRequest;

/**
 * @author bche
 */
public abstract class AbstractTemplateResolver implements TemplateResolver {
    protected static final String TEMPLATE_CONTEXT_PREFIX = "tem_";

    /* (non-Javadoc)
     * @see com.arsdigita.cms.dispatcher.TemplateResolver#getTemplate(com.arsdigita.cms.ContentSection,
     * com.arsdigita.cms.ContentItem, javax.servlet.http.HttpServletRequest)
     */
    public abstract String getTemplate(ContentSection section,ContentItem item, HttpServletRequest request);

    /* (non-Javadoc)
     * @see com.arsdigita.cms.dispatcher.TemplateResolver#getTemplatePath(com.arsdigita.cms.Template)
     */
    public abstract String getTemplatePath(Template template);

    /* (non-Javadoc)
     * @see com.arsdigita.cms.dispatcher.TemplateResolver#setTemplateContext(java.lang.String,
     *  javax.servlet.http.HttpServletRequest)
     */
    public void setTemplateContext(String sTemplateContext, HttpServletRequest request) {
        if (sTemplateContext != null) {
            request.setAttribute("templateContext", sTemplateContext);
        }
    }

    /* (non-Javadoc)
     * @see com.arsdigita.cms.dispatcher.TemplateResolver#getTemplateContext(javax.servlet.http.HttpServletRequest)
     */
    public String getTemplateContext(HttpServletRequest request) {
        String context = (String) request.getAttribute("templateContext");
        if (context == null) {
            context = TemplateManager.PUBLIC_CONTEXT;
        }
        return context;
    }
}
