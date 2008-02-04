/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.search.ui.filters;

import com.arsdigita.search.FilterSpecification;
import com.arsdigita.search.Search;
import com.arsdigita.search.ui.FilterWidget;
import com.arsdigita.search.filters.ObjectTypeFilterSpecification;
import com.arsdigita.search.filters.ObjectTypeFilterType;

import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.parameters.ArrayParameter;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.xml.Element;


/**
 * A filter component for presenting a list of object
 * types to select from.
 */
public class ObjectTypeFilterWidget extends FilterWidget {

    private ObjectType[] m_types;
    
    /**
     * Creates a new filter component for selecting amongst
     * a number of object types
     * @param types the types to select
     */
    public ObjectTypeFilterWidget(ObjectType[] types) {
        super(new ObjectTypeFilterType(),
              new ArrayParameter(new StringParameter(ObjectTypeFilterType.KEY)));

        m_types = types;
    }

    public FilterSpecification getFilter(PageState state) {
        String[] types = (String[])getValue(state);

        if (types == null) {
            types = new String[0];
        }

        return new ObjectTypeFilterSpecification(types);
    }
    
    public void generateBodyXML(PageState state,
                                Element parent) {
        super.generateBodyXML(state, parent);

        String[] types = (String[])getValue(state);

        for (int i = 0 ; i < m_types.length ; i++) {
            Element type = Search.newElement("objectType");
            type.addAttribute("name", m_types[i].getQualifiedName());
            for (int j = 0 ; j < types.length ; j++) {
                if (types[j].equals(m_types[i].getQualifiedName())) {
                    type.addAttribute("isSelected", "1");
                    break;
                }
            }
            parent.addContent(type);
        }
    }
}
