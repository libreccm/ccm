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
 * Navigation model that retrieves the path to one navigation category that the
 * forum is allocated to if there is no cookie available
 *
 * @author Chris Gilbert (cgyg9330)
 * @version $Id: $
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
