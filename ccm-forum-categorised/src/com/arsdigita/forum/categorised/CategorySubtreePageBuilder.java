/*
 * Created on 09-Feb-06
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.arsdigita.forum.categorised;

import com.arsdigita.bebop.Page;
import com.arsdigita.cms.ui.authoring.EmptyPage;
import com.arsdigita.forum.PageBuilder;
import com.arsdigita.london.terms.ui.CategorySubtree;

/**
 * @author cgyg9330
 *
 * Used by Ajax to generate branch of tree
 */
public class CategorySubtreePageBuilder implements PageBuilder {
	
	public Page buildPage() {
		
		//
		// the title of the page is important because we use the xsl in ccm-ldn-aplaws
		// web/__ccm__/themes/aplaws/category-step.xsl that refers to it. This 
		// matches the page defined in ccm-ldn-aplaws/web/packages/content-section/www/admin/load-cat.jsp
		
		Page page = new EmptyPage();
		page.setTitle("childCategories");
		page.add(new CategorySubtree());
		
		return page;
	
	}
	
	
	

}
