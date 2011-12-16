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

package com.arsdigita.bundle.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Label;
import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.xml.Document;
import com.arsdigita.xml.Element;
import com.arsdigita.web.Web;

/**
 * <p>
 * Special implementation of <code>com.arsdigita.ui.SimplePage</code> as 
 * the base page to serve bebop pages.</p>
 *
 * <p>Activated by pointing the parameter <code>waf.bebop.base_page</code> 
 * to it.</p>
 */
public class SimplePage extends com.arsdigita.ui.SimplePage {

    public SimplePage(String application,
                      Label title,
                      String id) {
        super(application, title, id);
    }
        

    public Element generateXMLHelper(PageState state,
                                     Document parent) {
        Element page = super.generateXMLHelper(state, parent);
        
        /*
         * TODO: What specific funcionality is added here and what is
         * its purpose and why is the bebop standard implementation
         * insufficient.
         *
         * Two xml attributes are added in addition to the bebop standard
         * implementation.
         */
        if (Web.getContext().getRequestURL() != null) {
            page.addAttribute("url", Web.getContext().getRequestURL().toString());
            
            page.addAttribute("textOnly", "/text".equals(
                                  DispatcherHelper
                                  .getDispatcherPrefix(state.getRequest()))
                              ? "1" : "0"
                             );
        }

        return page;
    }
}
