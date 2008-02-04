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
package com.arsdigita.cms.dispatcher;

import com.arsdigita.bebop.PageState;
import com.arsdigita.categorization.Category;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentSection;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

/**
 * <p><tt>CategoryItemResolver</tt> extends the <tt>ItemResolver</tt>
 * interface to provide alternate methods for generating item URLs
 * which take a Category as input, allowing for a URL generation
 * scheme which produces different URLs for an item in different categories</p>
 *
 *
 * @author Scott Seago (sseago@redhat.com)
 * @version $Revision: #6 $ $DateTime: 2004/08/17 23:15:09 $ */
public interface CategoryItemResolver extends ItemResolver {



    /**
     * Gets the category for the current request (if set by
     *    getItem(section, url, context)
     *
     * @param request The current request
     *
     * @return the Category for the current request
     */
    public Category getCategory(HttpServletRequest request);
    
    /**
     * Gets the category path for the current request (if set by
     *    getItem(section, url, context)
     *
     * @param request The current request
     *
     * @return the Category path for the current request
     */
    public Category[] getCategoryPath(HttpServletRequest request);

    /**
     * Whether the current request is an index item request (i.e. the
     * item name is not specified in the URL)
     *
     * @param request The current request
     *
     * @return Whether the current request is an index item request
     */
    public boolean isIndexRequest (HttpServletRequest request);

    /**
     * Generates a URL for a content item.
     *
     * @param itemId The item ID
     * @param name The name of the content page
     * @param state The page state
     * @param section the content section to which the item belongs
     * @param context the context of the URL, such as "live" or "admin"
     * @param category the Category to use as the context for
     * generating the URL
     * @return The URL of the item
     * @see #getCurrentContext
     */
    public String generateItemURL (
                                   PageState state, BigDecimal itemId, String name,
                                   ContentSection section, String context, Category category
                                   );

    /**
     * Generates a URL for a content item.
     *
     * @param itemId The item ID
     * @param name The name of the content page
     * @param state The page state
     * @param section the content section to which the item belongs
     * @param context the context of the URL, such as "live" or "admin"
     * @param templateContext the context for the URL, such as "public"
     * @param category the Category to use as the context for
     * @return The URL of the item
     * @see #getCurrentContext
     */
    public String generateItemURL (
                                   PageState state, BigDecimal itemId, String name,
                                   ContentSection section, String context, String templateContext,
				   Category category
                                   );

    /**
     * Generates a URL for a content item.
     *
     * @param item The item
     * @param state The page state
     * @param section the content section to which the item belongs
     * @param context the context of the URL, such as "live" or "admin"
     * @param category the Category to use as the context for
     * @return The URL of the item
     * @see #getCurrentContext
     */
    public String generateItemURL (
                                   PageState state, ContentItem item, ContentSection section, 
				   String context, Category category
                                   );

    /**
     * Generates a URL for a content item.
     *
     * @param item The item
     * @param state The page state
     * @param section the content section to which the item belongs
     * @param context the context of the URL, such as "live" or "admin"
     * @param templateContext the context for the URL, such as "public"
     * @param category the Category to use as the context for
     * @return The URL of the item
     * @see #getCurrentContext
     */
    public String generateItemURL (
                                   PageState state, ContentItem item, ContentSection section, 
				   String context, String templateContext, Category category
                                   );

}
