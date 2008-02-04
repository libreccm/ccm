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

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.web.Application;
import com.arsdigita.xml.Element;

import com.arsdigita.london.navigation.NavigationConstants;


/**
 * Outputs XML for the path to the current Application
 */

public class ApplicationPath extends SimpleComponent {  
    public static final String PATH = 
        NavigationConstants.NAV_PREFIX + ":applicationPath";
    public static final String APPLICATION = 
        NavigationConstants.NAV_PREFIX + ":application";

    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
    public static final String URL = "url";
    
    /** 
     * Adds [J]DOM nodes for the CategoryPath.  
     * The nodes have a list of the category hierarchy, starting with the root 
     * category and ending with the current one.
     */    
    public void generateXML(PageState ps, Element p) {
        Element content = p.newChildElement(PATH,
                                            NavigationConstants.NAV_NS);
        exportAttributes(content);
        
        Application app = (Kernel.getContext().getResource() instanceof Application ? 
                           (Application)Kernel.getContext().getResource() : 
                           null);
        while (app != null) {
            Element link = content.newChildElement(APPLICATION, 
                                                   NavigationConstants.NAV_NS);

            link.addAttribute( TITLE, app.getTitle());
            link.addAttribute( DESCRIPTION, app.getDescription());
            link.addAttribute( URL, app.getPath());            
            
            app = app.getParentApplication();
        }

    }
}
