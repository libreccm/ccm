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


package com.arsdigita.london.cms.dublin;

import com.arsdigita.london.navigation.RelatedItemsQuery;
import com.arsdigita.london.navigation.RelatedItemsQueryFactory;
import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.util.StringUtils;
import com.arsdigita.categorization.Category;

public class RelatedItemsQueryFactoryImpl extends RelatedItemsQueryFactory {
    
    public RelatedItemsQuery getRelatedItems(ContentPage page,
                                             Category current) {
        ContentBundle bundle = (ContentBundle)page.getParent();
        DublinCoreItem metadata = DublinCoreItem.findByOwner(page);
        
        if (metadata == null) {
            return null;
        }

        String keywordStr = metadata.getKeywords();

        String[] keywords = ("".equals(keywordStr) || keywordStr == null) ?
            new String[] {} :
            StringUtils.split(metadata.getKeywords(), ',');
        
        if (keywords.length == 0) {
            return null;
        }

        String first = keywords[0].trim();
        if (first == null ||
            "".equals(first)) {
            return null;
        }

        return new RelatedItemsQueryImpl(bundle,
                                         page,
                                         first,
                                         current);
    }
}
