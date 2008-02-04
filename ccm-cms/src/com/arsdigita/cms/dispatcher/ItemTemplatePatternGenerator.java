/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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

import com.arsdigita.templating.PatternGenerator;

import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.dispatcher.RequestContext;

import com.arsdigita.mimetypes.MimeType;

import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentSectionServlet;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.Template;
import com.arsdigita.cms.ItemTemplateMapping;
import com.arsdigita.cms.TemplateManager;
import com.arsdigita.cms.TemplateManagerFactory;

import javax.servlet.http.HttpServletRequest;


/**
 *  This looks to see if there is a given item and if there is it returns
 *  the oid for that item as the gererated value
 */
public class ItemTemplatePatternGenerator implements PatternGenerator {

    private static ContentItemDispatcher s_disp = new ContentItemDispatcher();

    public String[] generateValues(String key,
                                   HttpServletRequest req) {
        if (CMS.getContext().hasContentItem()) {
            ContentItem item = CMS.getContext().getContentItem();
            String context = getTemplateContext(req);

            ItemTemplateMapping mapping = 
                ItemTemplateMapping.getMapping
                (item, context,
                 MimeType.loadMimeType(Template.XSL_MIME_TYPE));

            Template template = null;
            if (mapping == null) {
                // there is no mapping so we try to get the default for the
                // content type
                template =
                    TemplateManagerFactory.getInstance().getDefaultTemplate
                    (item.getContentSection(), item.getContentType(), 
                     context, MimeType.loadMimeType(Template.XSL_MIME_TYPE));
            } else {
                template = mapping.getTemplate();
            } 

            if (template != null) {
                return new String[] { template.getOID().toString() };
            }
        }
        
        return new String[] {};
    }

    protected String getTemplateContext(HttpServletRequest req) {
        TemplateResolver templateResolver =
            s_disp.getTemplateResolver(CMS.getContext().getContentSection());
        RequestContext ctx = DispatcherHelper.getRequestContext();
        String templateURL = ctx.getRemainingURLPart();
        
        if (!templateURL.startsWith("/")) {
            templateURL = "/" + templateURL; 
        }
        
        if (templateURL.startsWith(ContentSectionServlet.PREVIEW)) {
            templateURL = templateURL.substring
                (ContentSectionServlet.PREVIEW.length());
        } 
            
        if (templateURL.startsWith("/")) {
            templateURL = templateURL.substring(1);
        } 
        
        String templateContext = null;
        int index = templateURL.indexOf("/");
        if (index > -1) {
            templateContext = templateURL.substring(0, index);
        }
        
        if (templateContext != null && 
            templateContext.startsWith
            (AbstractItemResolver.TEMPLATE_CONTEXT_PREFIX)) {
            templateContext =  templateContext.substring
                (AbstractItemResolver.TEMPLATE_CONTEXT_PREFIX.length());
            return templateContext;
        } else {
            // we assume it is the default public
            return TemplateManager.PUBLIC_CONTEXT;
        }
    }
}
