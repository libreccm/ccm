/*
 * Copyright (C) 2005 Runtime Collective Ltd. All Rights Reserved.
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
import com.arsdigita.search.ui.StaticFilterComponent;
import com.arsdigita.search.filters.ContentSectionFilterSpecification;
import com.arsdigita.bebop.PageState;

/**
 * A simple filter generator that restricts to a 
 * specified set of content sections
 * @author matt@runtime-collective.com
 * @version $Id: ContentSectionFilterComponent.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class ContentSectionFilterComponent extends StaticFilterComponent {

    private String[] m_contentSections;
    
    /**
     * Creates a filter generator restricting to a single
     * content section
     * 
     * @param contentSection 
     */
    public ContentSectionFilterComponent(String contentSection) {
        this(new String[] {contentSection});
    }

    /**
     * Creates a filter generator restricting several
     * content sections
     *
     * @param contentSections the list of object type names
     */
    public ContentSectionFilterComponent(String[] contentSections) {
        m_contentSections = contentSections;
    }

    
    public FilterSpecification getFilter(PageState state) {
        return new ContentSectionFilterSpecification(
            m_contentSections
        );
    }

}
