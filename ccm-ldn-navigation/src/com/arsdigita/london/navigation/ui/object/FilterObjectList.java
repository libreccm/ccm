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

package com.arsdigita.london.navigation.ui.object;

import com.arsdigita.london.navigation.Navigation;
import com.arsdigita.london.navigation.ui.AbstractObjectList;

import com.arsdigita.persistence.DataCollection;
import com.arsdigita.xml.Element;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FilterObjectList extends AbstractObjectList {
    private HashMap m_filters = new HashMap();

    public void addFilter( String param, String path ) {
        m_filters.put( param, path );
    }

    protected DataCollection getObjects( HttpServletRequest request,
                                         HttpServletResponse response ) {
        DataCollection objects = super.getObjects( request, response );

        Iterator params = m_filters.entrySet().iterator();
        while( params.hasNext() ) {
            Map.Entry entry = (Map.Entry) params.next();

            String param = (String) entry.getKey();

            String value = request.getParameter( param );
            if( null != value ) {
                String path = (String) entry.getValue();

                objects.addEqualsFilter( path, value );
            }
        }

        return objects;
    }

    public Element generateXML(HttpServletRequest request,
                               HttpServletResponse response) {
        Element content = Navigation.newElement("filterObjectList");
                                                                                                             
        content.addContent(generateObjectListXML(request,
                                                 response));
                                                                                                             
        return content;
    }
}
