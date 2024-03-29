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
import com.arsdigita.cms.ContentPage;
import com.arsdigita.categorization.Category;
import com.arsdigita.web.Application;
import com.arsdigita.web.Web;

import org.apache.log4j.Logger;

public class RelatedItemsQueryFactoryImpl extends RelatedItemsQueryFactory {    
    
    private static final Logger s_log = 
        Logger.getLogger(RelatedItemsQueryFactoryImpl.class);

    public RelatedItemsQuery getRelatedItems(ContentPage item,
                                             Category current) {
        ContentBundle bundle = (ContentBundle)item.getParent();

        Application app = Web.getWebContext().getApplication();
        String context = Navigation.getConfig().getRelatedItemsContext();
        if (context == null || context.length() == 0) {
        	return null;
        }
        Category root = Category.getRootForObject(app, context);
        
        if (root == null) {
            s_log.warn("No root category found for application " + 
                       app.getPath() + " " + app.getClass() + 
                       " in context " + context);
            return null;
        }

        return new RelatedItemsQueryImpl(root, bundle);
    }
}
