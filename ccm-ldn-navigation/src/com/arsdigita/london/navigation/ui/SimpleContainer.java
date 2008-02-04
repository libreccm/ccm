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


import com.arsdigita.util.Assert;

import com.arsdigita.xml.Element;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Iterator;

public class SimpleContainer extends AbstractContainer {
    
    private String m_name;
    private String m_xmlns;

    public SimpleContainer(String name,
                           String xmlns) {
        Assert.exists(name, String.class);
        Assert.exists(xmlns, String.class);
        Assert.truth(name.indexOf(":") != -1, "name is qualfied");

        m_name = name;
        m_xmlns = xmlns;
    }

    public Element generateXML(HttpServletRequest request,
                               HttpServletResponse response) {
        Element content = new Element(m_name, m_xmlns);
        
        Iterator children = getChildren();
        while (children.hasNext()) {
            Component child = (Component)children.next();

            Element c = generateChildXML(request,
                                         response,
                                         child);
            
            if (c != null) {
                content.addContent(c);
            }
        }
        
        return content;
    }
}
