/*
 * Created on 04-Aug-05
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.arsdigita.forum.categorised;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;


import com.arsdigita.categorization.CategorizedObject;
import com.arsdigita.categorization.Category;
import com.arsdigita.categorization.CategoryCollection;
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.london.navigation.CookieNavigationModel;
import com.arsdigita.london.navigation.GenericNavigationModel;
import com.arsdigita.london.terms.Domain;
import com.arsdigita.london.terms.Term;
import com.arsdigita.london.terms.Util;
import com.arsdigita.web.Application;

/**
 * @author cgyg9330
 *
 * Navigation model that retrieves the path to one navigation category that the forum is allocated to
 * if there is no cookie available
 */
public class ForumNavigationModel extends CookieNavigationModel {
	
	protected ACSObject getCategorisedObject() {
			return Kernel.getContext().getResource();

		}
	protected Category[] getAlternativePath(boolean cookieExists) {
		Application forum = (Application)Kernel.getContext().getResource();
				//Category defaultCat = new CategorizedObject(forum).getDefaultParentCategory();
				Domain navigation = Util.getApplicationDomain(Application.retrieveApplicationForPath("/navigation/"));
				DomainCollection forumNavigationTerms = navigation.getDirectTerms(forum);
				Category cat = null;
				if (forumNavigationTerms.next()) {
					cat = ((Term) forumNavigationTerms.getDomainObject()).getModel();
					forumNavigationTerms.close();
				}
				if (cat == null) {
					return new Category[]{ getRootCategory() };
				}
				List path = new ArrayList();
				CategoryCollection cats = cat.getDefaultAscendants();
				cats.addOrder("defaultAncestors");
				while (cats.next()) {
					path.add(cats.getDomainObject());
				}
				return (Category[]) path.toArray(new Category[(int) path.size()]);
	}
	
	
	
	

}
