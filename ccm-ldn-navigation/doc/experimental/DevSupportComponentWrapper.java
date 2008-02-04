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

import com.arsdigita.developersupport.DeveloperSupport;
import com.arsdigita.webdevsupport.RequestInfo;
import com.arsdigita.webdevsupport.WebDevSupport;

import com.arsdigita.xml.Element;
import com.arsdigita.xml.XML;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Date;

public class DevSupportComponentWrapper extends AbstractComponentWrapper {

    public DevSupportComponentWrapper(Component contents) {
        super(contents);
    }
    
    public Element generateXML(HttpServletRequest request,
                               HttpServletResponse response) {
        DeveloperSupport.startStage("Generate XML: " + 
                                    getContents().getClass().getName());

        /*
         * Pending bz 108109 :-(
        RequestInfo ri = WebDevSupport.getInstance().getCurrentRequest();

        long startTime = (new Date()).getTime();
        int startCount = ri == null ? 0 : ri.getNumQueries();
        */

        Element content = getContents().generateXML(request,
                                                    response);
        
        /*
        long endTime = (new Date()).getTime();
        int endCount = ri == null ? 0 : ri.getNumQueries();
        
        if (content != null) {
            content.addAttribute("debug:source",
                                 getClass().getName(),
                                 "http://xmlns.redhat.com/ui/debug/1.0");
            content.addAttribute("debug:queryTime",
                                 XML.format(new Long(endTime - startTime)),
                                 "http://xmlns.redhat.com/ui/debug/1.0");
            content.addAttribute("debug:queryCount",
                                 ri == null ?
                                 "unavailable" :
                                 XML.format(new Integer(endCount - startCount)),
                                 "http://xmlns.redhat.com/ui/debug/1.0");
        }
        */

        DeveloperSupport.endStage("Generate XML: " + 
                                  getContents().getClass().getName());
        
        return content;
    }

}
