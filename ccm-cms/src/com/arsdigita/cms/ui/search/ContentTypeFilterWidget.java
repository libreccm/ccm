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
package com.arsdigita.cms.ui.search;


import com.arsdigita.search.ui.FilterWidget;
import com.arsdigita.search.Search;
import com.arsdigita.search.FilterSpecification;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ContentTypeCollection;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.xml.Element;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.parameters.ArrayParameter;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.search.ContentTypeFilterType;
import com.arsdigita.cms.search.ContentTypeFilterSpecification;

public class ContentTypeFilterWidget extends FilterWidget {

    private ContentType[] m_types;

    public ContentTypeFilterWidget(ContentTypeCollection types) {
        super(new ContentTypeFilterType(),
              new ArrayParameter(new StringParameter(ContentTypeFilterType.KEY)));

        m_types = new ContentType[(int)types.size()];
        int i = 0;
        while (types.next()) {
            m_types[i++] = types.getContentType();
        }
    }
    public ContentTypeFilterWidget(ContentSection section) {
        this(section.getContentTypes());
    }

    public ContentTypeFilterWidget() {
        this(ContentType.getRegisteredContentTypes());
    }

    protected ContentType[] getContentTypes(PageState state) {
        return m_types;
    }

    public FilterSpecification getFilter(PageState state) {
        String[] types = (String[])getValue(state);

        if (types == null) {
            types = new String[0];
        }

        return new ContentTypeFilterSpecification(types);
    }

    public void generateBodyXML(PageState state,
                                Element parent) {
        super.generateBodyXML(state, parent);

        String[] types = (String[])getValue(state);
        if (types == null) {
            types = new String[0];
        }

        ContentType[] widgetTypes = getContentTypes(state);

        for (int i = 0 ; i < widgetTypes.length ; i++) {
            Element type = Search.newElement("contentType");
            type.addAttribute("name", widgetTypes[i].getAssociatedObjectType());
            type.addAttribute("title", widgetTypes[i].getLabel());
            for (int j = 0 ; j < types.length ; j++) {
                if (types[j].equals(widgetTypes[i].getAssociatedObjectType())) {
                    type.addAttribute("isSelected", "1");
                    break;
                }
            }



            parent.addContent(type);
        }
    }
}
