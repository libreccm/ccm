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

package com.arsdigita.london.navigation.ui.portlet;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.portal.AbstractPortletRenderer;
import com.arsdigita.london.navigation.DataCollectionPropertyRenderer;
import com.arsdigita.london.navigation.DataCollectionRenderer;
import com.arsdigita.london.navigation.portlet.ObjectListPortlet;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.util.StringUtils;
import com.arsdigita.xml.Element;

public class ObjectListPortletRenderer extends AbstractPortletRenderer {
    private ObjectListPortlet m_portlet;
    
    public ObjectListPortletRenderer(ObjectListPortlet portlet) {
        m_portlet = portlet;
    }

    protected DataCollectionRenderer newDataCollectionRenderer() {
        return new DataCollectionRenderer();
    }

    public void generateBodyXML(PageState state,
                                Element parent) {
        DataCollectionRenderer renderer = newDataCollectionRenderer();
        renderer.setPageSize(m_portlet.getCount());

        String[] dcProperties = m_portlet.getProperties();
        for( int i = 0; i < dcProperties.length; i++ ) {
            DataCollectionPropertyRenderer dcpr =
                ObjectListPortlet.getDCPropertyRenderer( dcProperties[i] );
            renderer.addProperty( dcpr );
        }
        
        String attributes = m_portlet.getAttributes();
        if (attributes != null) {
            String[] attr = StringUtils.split(attributes, ',');
            for (int i = 0 ; i < attr.length ; i++) {
                renderer.addAttribute(attr[i].trim());
            }
        }

        renderer.lock();

        DataCollection objects = m_portlet.getDataCollection();

        Element content = renderer.generateXML(objects, getPageNumber());
        parent.addContent(content);
    }
    
    protected int getPageNumber() {
        return 1;
    }
    
}
