/*
 * Copyright (C) 2007 Red Hat Inc. All Rights Reserved.
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

import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.search.CMSContentSectionFilterSpecification;
import com.arsdigita.search.FilterSpecification;
import com.arsdigita.search.ui.StaticFilterComponent;

/**
 * A simple filter generator that restricts search results to
 * selected content section, if section context exists.
 */
public class ContentSectionFilterComponent extends StaticFilterComponent {

    private String m_version;

    public FilterSpecification getFilter(PageState state) {
        if (CMS.getContext().hasContentSection()) {
            String section = CMS.getContext().getContentSection().getName();
            return new CMSContentSectionFilterSpecification(section);
        }
        return new CMSContentSectionFilterSpecification();
    }

}

