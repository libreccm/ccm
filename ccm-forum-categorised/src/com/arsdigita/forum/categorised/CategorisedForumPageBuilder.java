/*
 * Created on 09-Feb-06
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.arsdigita.forum.categorised;

import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.forum.Forum;
import com.arsdigita.forum.ForumContext;
import com.arsdigita.forum.ForumPageBuilder;
import com.arsdigita.forum.ui.AForumUserCompactView;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.navigation.ui.category.Menu;
import com.arsdigita.navigation.ui.category.Path;

/**
 * @author cgyg9330
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CategorisedForumPageBuilder extends ForumPageBuilder {
	
	public Page buildPage() {
		
		Page page = super.buildPage();
		Path path = new Path();
		path.setModel(new ForumNavigationModel());
			
		page.add(path);
		page.add(new Menu());
		return page;
	
	}
	
	protected AForumUserCompactView getForumComponent() {
		 return new CategorisedForumComponent();
	   }
	
	
	

}
