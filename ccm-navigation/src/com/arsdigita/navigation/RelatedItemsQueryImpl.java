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

package com.arsdigita.navigation;

import com.arsdigita.cms.ContentBundle;
import com.arsdigita.categorization.Category;

import java.util.List;
import java.util.ArrayList;

class RelatedItemsQueryImpl extends RelatedItemsQuery {

    public static final String QUERY_NAME = 
        "com.arsdigita.navigation.RelatedItems";
    
    public static final String BUNDLE_ID = "bundleID";
    public static final String BUNDLE_IDS = "bundleIDs";
    public static final String CATEGORY_ID = "categoryID";

    RelatedItemsQueryImpl(Category root,
                          ContentBundle item) {
        super(QUERY_NAME);
        
        List bundles = new ArrayList();

        bundles.add(item.getID());

        if (item.isLive()) {
            if (item.isDraftVersion()) {
                bundles.add(item.getLiveVersion().getID());
            } else {
                bundles.add(item.getDraftVersion().getID());
            }
        }

        setParameter(BUNDLE_ID, item.getID());
        setParameter(BUNDLE_IDS, bundles);
        setParameter(CATEGORY_ID, root.getID());
    }
}
