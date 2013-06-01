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


package com.arsdigita.cms.contentassets;

import com.arsdigita.navigation.RelatedItemsQuery;
import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.categorization.Category;

/**
 * 
 * 
 */
class RelatedItemsQueryImpl extends RelatedItemsQuery {
    
    /** PDL connector                                                         */
    public static final String QUERY_NAME 
        = "com.arsdigita.cms.contentassets.getRelatedItems";

    public static final String ITEM_ID = "itemID";
    public static final String BUNDLE_ID = "bundleID";
    public static final String KEYWORD = "keyword";

    public static final String NAV_CATEGORY_ID = "navCategoryID";
    public static final String SUBJECT_DOMAIN = "subjectDomain";

    /**
     * 
     * @param bundle
     * @param page
     * @param keyword
     * @param current 
     */
    RelatedItemsQueryImpl(ContentBundle bundle,
                          ContentPage page,
                          String keyword,
                          Category current) {
        super(QUERY_NAME);
        
        setParameter(ITEM_ID, page.getID());
        setParameter(BUNDLE_ID, bundle.getID());
        setParameter(KEYWORD, keyword);
        
        setParameter(SUBJECT_DOMAIN, 
                     DublinCoreES.getConfig()
                     .getRelatedItemsSubjectDomain());
        
        setParameter(NAV_CATEGORY_ID,
                     current.getID());
    }    
}
