/*
 * Copyright (C) 2006 Chris Gilbert. All Rights Reserved.
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
package com.arsdigita.forum.categorised;

import com.arsdigita.bebop.PageState;
import com.arsdigita.categorization.Category;
import com.arsdigita.categorization.RootCategoryCollection;
import com.arsdigita.categorization.ui.ACSObjectCategorySummary;
import com.arsdigita.forum.Forum;
import com.arsdigita.forum.ForumContext;
import com.arsdigita.forum.ui.Constants;
import com.arsdigita.kernel.ACSObject;

/**
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 *
 * @author Chris Gilbert (cgyg9330)
 */
public class ForumCategorySummary extends ACSObjectCategorySummary implements Constants {

	
	protected ACSObject getObject(PageState state) {
		return ForumContext.getContext(state).getForum();
	}

	/* (non-Javadoc)
	 * @see com.arsdigita.categorization.ui.ACSObjectCategorySummary#getXMLPrefix()
	 */
	protected String getXMLPrefix() {
		return "forum";
	}

	/* (non-Javadoc)
	 * @see com.arsdigita.categorization.ui.ACSObjectCategorySummary#getXMLNameSpace()
	 */
	protected String getXMLNameSpace() {
		return FORUM_XML_NS;
	}

	/**
	 * retrieve any domains that are mapped to this forum. If there are none, 
	 * retrieve domains mapped to the parent application (eg ccm/portal)  
	 * @see com.arsdigita.categorization.ui.ACSObjectCategorySummary#getRootCategories()
	 */
	protected RootCategoryCollection getRootCategories(PageState state) {
		Forum forum = ForumContext.getContext(state).getForum();
		RootCategoryCollection roots =  Category.getRootCategories(forum);
		if (roots.size() == 0) {
			// forum has no domains - check if parent application has, and use those 
			roots = Category.getRootCategories(forum.getParentApplication());
		}
		return roots;
	}

}
