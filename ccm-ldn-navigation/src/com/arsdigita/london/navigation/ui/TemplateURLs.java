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

package com.arsdigita.london.navigation.ui;

import com.arsdigita.london.navigation.NavigationConstants;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.TemplateContext;
import com.arsdigita.cms.TemplateContextCollection;
import com.arsdigita.xml.Element;

import java.util.Iterator;
import java.util.LinkedList;

public class TemplateURLs extends SimpleComponent {
    private static final String[] s_contexts;

    // Pull out and pre-populate the template use contexts. Note that this will
    // only be done once per server load, but this is ok because there's no UI
    // for creating contexts.
    static {
        TemplateContextCollection templates = TemplateContext.retrieveAll();
        LinkedList buffer = new LinkedList();

        while( templates.next() ) {
            buffer.add( templates.getTemplateContext().getContext() );
        }

        s_contexts = new String[buffer.size()];
        Iterator iter = buffer.iterator();
        for( int i = 0; i < s_contexts.length; i++ ) {
            s_contexts[i] = iter.next().toString();
        }
    }

    public TemplateURLs() {
        super();
    }

    public void generateXML( PageState ps, Element p ) {
        ContentItem item = CMS.getContext().getContentItem();
        if( null == item ) return;

        Element container = p.newChildElement(
            NavigationConstants.NAV_PREFIX + ":templateContexts",
            NavigationConstants.NAV_NS
        );
        container.addAttribute( "type", item.getContentType().getClassName() );

        String sectionPath = item.getContentSection().getPath();
        String itemPath = item.getPath();

        for( int i = 0; i < s_contexts.length; i++ ) {
            Element context = container.newChildElement(
                NavigationConstants.NAV_PREFIX + ":context",
                NavigationConstants.NAV_NS
            );
            context.addAttribute( "name", s_contexts[i] );

            String path = sectionPath + "/tem_" + s_contexts[i] + '/' + itemPath;
            context.addAttribute( "path", path );
        }
    }
}
