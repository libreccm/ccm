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
 */

package com.arsdigita.london.subsite.dispatcher;

import com.arsdigita.cms.Folder;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.TemplateManager;
import javax.servlet.http.HttpServletRequest;

import com.arsdigita.cms.dispatcher.DefaultTemplateResolver;
import com.arsdigita.util.ResourceManager;
import java.io.File;
import com.arsdigita.london.subsite.Site;
import com.arsdigita.london.subsite.Subsite;

import org.apache.log4j.Logger;


public class SubsiteItemTemplateResolver extends DefaultTemplateResolver {

    private static final Logger s_log = 
        Logger.getLogger(SubsiteItemTemplateResolver.class);
    

    protected String getDefaultTemplate(ContentSection section,
                                        ContentItem item,
                                        HttpServletRequest request) {
        if (Subsite.getContext().hasSite()) {
            Site site = Subsite.getContext().getSite();
            if (s_log.isDebugEnabled()) {
                s_log.debug("Got subsite " + site.getOID());
            }
            String path = "/default/" + site.getStyleDirectory() +
                ((item instanceof Folder) ? "-folder.jsp" : "-item.jsp");
            
            if (templateExists(path)) {
                if (s_log.isInfoEnabled()) {
                    s_log.info("Using specific subsite template " + path);
                }
                return path;
            }

            path = "/default/subsite" +
                ((item instanceof Folder) ? "-folder.jsp" : "-item.jsp");
            
            if (templateExists(path)) {
                if (s_log.isInfoEnabled()) {
                    s_log.info("Using generic subsite template " + path);
                }
                return path;
            }
        }
        
        if (s_log.isInfoEnabled()) {
            s_log.info("Falling back to default item.jsp");
        }

        return super.getDefaultTemplate(section, item, request);
    }

    protected boolean templateExists(String name) {
        String fullpath = ContentSection.getConfig().getTemplateRoot() 
            + "/" + name;
        File file = ResourceManager.getInstance().getResourceAsFile(fullpath);
        boolean exists = file.exists();
        if (s_log.isDebugEnabled()) {
            s_log.debug("Path is " + name + ", file is " + file + 
                        ", exists? " + exists);
        }
        return exists;
    }
    
    public String getTemplateContext(HttpServletRequest request) {
        String context = (String) request.getAttribute("templateContext");
        
        if (context == null) {
            if (Subsite.getContext().hasSite()) {
                Site site = Subsite.getContext().getSite();
                if (s_log.isDebugEnabled()) {
                    s_log.debug("Getting context from subsite " + site.getOID());
                }
                context = site.getTemplateContext().getContext();
            } else {
                if (s_log.isDebugEnabled()) {
                    s_log.debug("Using default public context");
                }
                context = TemplateManager.PUBLIC_CONTEXT;
            }
        }

        if (s_log.isInfoEnabled()) {
            s_log.info("Got context " + context);
        }
        
        return context;
    }

}
