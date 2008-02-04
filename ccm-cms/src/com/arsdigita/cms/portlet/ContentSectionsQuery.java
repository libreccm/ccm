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
package com.arsdigita.cms.portlet;

import com.arsdigita.util.SimpleDataQuery;

import java.math.BigDecimal;


public class ContentSectionsQuery extends SimpleDataQuery {
    
    public static final String QUERY_NAME 
	= "com.arsdigita.london.portal.portlet.getContentSections";

    public static final String URL = "URL";
    public static final String NAME = "name";
    public static final String SECTION_ID = "sectionID";

    public ContentSectionsQuery() {
	super(QUERY_NAME);
    }

    public String getURL() {
	return (String)get(URL);
    }

    public String getName() {
	return (String)get(NAME);
    }
    
    public BigDecimal getSectionID() {
	return (BigDecimal)get(SECTION_ID);
    }
}
