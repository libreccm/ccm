/*
 * Created on 22-Feb-06
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.arsdigita.categorisedforum;

import com.arsdigita.bebop.PageState;
import com.arsdigita.categorization.Category;
import com.arsdigita.categorization.RootCategoryCollection;
import com.arsdigita.categorization.ui.ACSObjectCategorySummary;
import com.arsdigita.forum.Forum;
import com.arsdigita.forum.ForumContext;
import com.arsdigita.forum.ui.Constants;
import com.arsdigita.kernel.ACSObject;

/**
 * @author cgyg9330
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
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
