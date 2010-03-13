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

import com.arsdigita.london.navigation.Navigation;
import com.arsdigita.web.Web;
import com.arsdigita.web.URL;
import com.arsdigita.util.Assert;
import com.arsdigita.xml.Element;
import com.arsdigita.xml.XML;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;
import java.util.Map;

public class URLComponent extends AbstractComponent {
    
    protected URL getURL() {
        return Web.getContext().getRequestURL();
    }
    
    public Element generateXML(HttpServletRequest request,
                               HttpServletResponse response) {
        Assert.isLocked(this);

        Element content = Navigation.newElement("url");
        
        URL url = getURL();
        
        content.addAttribute("scheme", url.getScheme());
        content.addAttribute("server-name", url.getServerName());
        content.addAttribute("server-port", 
                             XML.format(new Integer(url.getServerPort())));
        content.addAttribute("context-path", url.getContextPath());
        content.addAttribute("dispatcher-path", url.getDispatcherPath());
        content.addAttribute("servlet-path", url.getServletPath());
        content.addAttribute("path-info", url.getPathInfo());
        
        Map parameters = url.getParameterMap();
        if( null != parameters ) {
            Iterator keys = parameters.keySet().iterator();
            while (keys.hasNext()) {
                String key = (String)keys.next();
                String[] values = (String[])parameters.get(key);

                Element parameter = Navigation.newElement("parameter");
                parameter.addAttribute("name", key);
                for (int i = 0 ; i < values.length ; i++) {
                    Element value = Navigation.newElement("value");
                    value.setText(values[i]);
                    parameter.addContent(value);
                }
                content.addContent(parameter);
            }
        }
        return content;
    }
}
