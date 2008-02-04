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

package com.arsdigita.search.filters;

import com.arsdigita.search.FilterSpecification;


/**
 * A filter spec for supplying a list of content sections
 * to the content section filter type. There are two flags
 * can alter the semantics of the filter. The first 
 * specifies whether the list of an inclusion (white)
 * or exclusion (black) list.
 * @author matt@runtime-collective.com
 * @version $Id: ContentSectionFilterSpecification.java 738 2005-09-01 12:36:52Z sskracic $
 */

public class ContentSectionFilterSpecification extends FilterSpecification {

    public final static String CONTENT_SECTIONS = "contentSections";
    public final static String EXCLUSION = "exclude";
    
    /**
     * Creates a new filter restricting results to a single
     * content section.
     * @param contentSection
     */
    public ContentSectionFilterSpecification(String contentSection) {
        super(new Object[] { CONTENT_SECTIONS, contentSection },
              new ContentSectionFilterType());

    }

    /**
     * Creates a new filter restricting results to a set
     * content sections.
     * @param contentSections
     */
    public ContentSectionFilterSpecification(String[] contentSections) {
	super(new Object[] { CONTENT_SECTIONS, contentSections },
              new ContentSectionFilterType());
    }


    /**
     * Returns the set of content sections to filter on
     * @return the content sections
     */
    public Object[] getSections() {
        return (Object[])get(CONTENT_SECTIONS);
    }


    /**
     * Sets the flag indicating that the content section list is
     * an exclusion list rather than an inclusion list.
     * Default is an inclusion list.
     *
     * @param exclude true to mark as an exclusion list
     */
    public void setExclusion(boolean exclude) {
        set(EXCLUSION, new Boolean(exclude));
    }

    /**
     * Gets the flag indicating that the content section list is
     * an exclusion list rather than an inclusion list.
     *
     * @return true if marked as an exclusion list
     */
    public boolean isExclusion() {
        return Boolean.TRUE.equals(get(EXCLUSION));
    }
}
