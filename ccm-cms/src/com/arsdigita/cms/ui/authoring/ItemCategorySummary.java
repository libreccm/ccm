/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.ui.authoring;

import org.apache.log4j.Logger;

import com.arsdigita.bebop.PageState;
import com.arsdigita.categorization.Category;
import com.arsdigita.categorization.RootCategoryCollection;
import com.arsdigita.categorization.ui.ACSObjectCategorySummary;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.kernel.ACSObject;

public class ItemCategorySummary extends ACSObjectCategorySummary {

    private static final Logger s_log = Logger.getLogger(ItemCategorySummary.class);


    public ItemCategorySummary() {
   		super();
    }

    protected boolean canEdit(PageState state) {
        SecurityManager sm = new SecurityManager(
            CMS.getContext().getContentSection());
        return sm.canAccess(state.getRequest(),
                            SecurityManager.EDIT_ITEM,
                            CMS.getContext().getContentItem());
    }


    /* 
     * @see com.arsdigita.categorization.ui.ObjectCategorySummary#getObject()
     */
    protected ACSObject getObject(PageState state) {
	ContentItem item = CMS.getContext().getContentItem();
	return item.getParent();
    }

    /* 
     * @see com.arsdigita.categorization.ui.ObjectCategorySummary#getXMLPrefix()
     */
    protected String getXMLPrefix() {
	return "cms";
    }



    /* 
     * @see com.arsdigita.categorization.ui.ObjectCategorySummary#getXMLNameSpace()
     */
    protected String getXMLNameSpace() {
	return CMS.CMS_XML_NS;
    }

    /* 
     * @see com.arsdigita.categorization.ui.ObjectCategorySummary#getRootCategories()
     */
    protected RootCategoryCollection getRootCategories(PageState state) {
	ContentSection section = CMS.getContext().getContentSection();
        return Category.getRootCategories(section);
    }
}
