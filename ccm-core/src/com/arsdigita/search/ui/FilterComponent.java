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
package com.arsdigita.search.ui;

import com.arsdigita.search.Search;
import com.arsdigita.search.FilterType;

import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.bebop.PageState;
import com.arsdigita.xml.Element;

import org.apache.log4j.Logger;

/**
 * This class provides a base for filter components
 * which don't have any form state parameters.
 * 
 * @see com.arsdigita.search.ui.FilterGenerator
 */
public abstract class FilterComponent extends SimpleComponent 
    implements FilterGenerator {

    private static final Logger s_log = 
        Logger.getLogger(FilterComponent.class);
    
    private FilterType m_type;
    
    /**
     * Creates a filter component
     * @param type the filter type
     * @param name the state parameter name
     */
    public FilterComponent(FilterType type) {
        m_type = type;
        
        setAttribute("type", m_type.getKey());
    }

    public void generateXML(PageState state,
                            Element parent) {
        Element content = Search.newElement("filter");
        exportAttributes(content);

        generateBodyXML(state, content);

        parent.addContent(content);
    }

    /**
     * THis method must generate the body of the component,
     * typically a list of possible values for the filter
     */
    protected abstract void generateBodyXML(PageState state,
                                            Element parent);
    
}
