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
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.search.ContentTypeFilterType;
import com.arsdigita.cms.search.ContentTypeFilterSpecification;
import com.arsdigita.cms.ui.ItemSearch;
import com.arsdigita.domain.DataObjectNotFoundException;
import java.math.BigDecimal;

public class ContentTypeFilterWidget extends FilterWidget {

    private ContentType m_parentType = null;
    private ContentSection m_section = null;
    private ContentType[] m_types = null;

    public ContentTypeFilterWidget(ContentTypeCollection types) {
        super(new ContentTypeFilterType(), new ArrayParameter(new StringParameter(ContentTypeFilterType.KEY)));

        m_types = new ContentType[(int) types.size()];

        for (int i = 0; types.next();) {
            m_types[i++] = types.getContentType();
        }
    }

    public ContentTypeFilterWidget(ContentSection section) {
        this(section.getContentTypes());
    }

    public ContentTypeFilterWidget(ContentSection section, ContentType parentType) {
        this(section.getDecendantsOfContentType(parentType));
        m_section = section;
        m_parentType = parentType;
    }

    public ContentTypeFilterWidget(ContentType parentType) {
        this(ContentType.getDescendantsOf(parentType));
        m_parentType = parentType;
    }

    public ContentTypeFilterWidget() {
        this(ContentType.getRegisteredContentTypes());
    }

    protected ContentSection getContentSection() {
        return m_section;
    }

    private ContentType getParentType(PageState state) {

        ContentType ct = m_parentType;

        BigDecimal singleTypeID =
                (BigDecimal) state.getValue(new BigDecimalParameter(ItemSearch.SINGLE_TYPE_PARAM));

        if (singleTypeID != null) {
            try {
                ct = new ContentType(singleTypeID);
            } catch (DataObjectNotFoundException ex) {
                ct = null;
            }
        }

        return ct;
    }

    protected ContentType[] getContentTypes(PageState state) {

        ContentType parentType = getParentType(state);
        ContentTypeCollection typesCollection = null;
        ContentType[] typesArray = m_types.clone();

        // If the section and parent type both equals the preset from initializer
        // there is no need to get a new ContentTypeCollection
        if (getContentSection() != m_section && parentType != m_parentType) {

            if (getContentSection() != null) {
                ContentSection section = getContentSection();

                if (parentType == null) {
                    typesCollection = section.getContentTypes();
                } else {
                    typesCollection = section.getDecendantsOfContentType(parentType);
                }

            } else {

                if (parentType == null) {
                    typesCollection = ContentType.getRegisteredContentTypes();
                } else {
                    typesCollection = ContentType.getDescendantsOf(parentType);
                }
            }

            typesArray = new ContentType[(int) typesCollection.size()];

            for (int i = 0; typesCollection.next();) {
                typesArray[i++] = typesCollection.getContentType();
            }
        }

        return typesArray;
    }

    public FilterSpecification getFilter(PageState state) {
        String[] types = (String[]) getValue(state);

        if (types == null) {
            types = new String[0];
        }

        return new ContentTypeFilterSpecification(types);
    }

    @Override
    public void generateBodyXML(PageState state, Element parent) {
        super.generateBodyXML(state, parent);

        String[] types = (String[]) getValue(state);
        if (types == null) {
            types = new String[0];
        }

        ContentType[] widgetTypes = getContentTypes(state);

        for (int i = 0; i < widgetTypes.length; i++) {
            Element type = Search.newElement("contentType");
            type.addAttribute("name", widgetTypes[i].getAssociatedObjectType());
            type.addAttribute("title", widgetTypes[i].getLabel());

            for (int j = 0; j < types.length; j++) {
                if (types[j].equals(widgetTypes[i].getAssociatedObjectType())) {
                    type.addAttribute("isSelected", "1");

                    break;
                }
            }

            parent.addContent(type);
        }
    }
}
