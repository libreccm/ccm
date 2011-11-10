/*
 * Copyright (C) 2007 Chris Gilbert All Rights Reserved.
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
package com.arsdigita.navigation.ui.category;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.arsdigita.categorization.Category;
// import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataQuery;
// import com.arsdigita.persistence.DataQueryDataCollectionAdapter;
import com.arsdigita.persistence.SessionManager;

/**
 * @author chris.gilbert@westsussex.gov.uk
 * 
 * outputs subcategories of categories included in the list where 
 * the subcategory contains at least one object visible to the 
 * current user.
 * This means that categories that only contain 
 * other categories will be omitted by default. If
 * necessary this can be controlled by the use of 
 * index pages.
 * 
 * To use this feature, 
 * ccm set com.arsdigita.navigation.default_menu_cat_provider=com.arsdigita.navigation.ui.category.PopulatedSubcategoryTreeCatProvider
 * and ccm set com.arsdigita.navigation.index_page_cache_lifetime=0
 * 
 * This provider includes a permission check. An alternative could be written without this check
 * that may be faster.
 * 
 * 
 */
public class PopulatedSubcategoryTreeCatProvider implements TreeCatProvider {

    public DataCollection getTreeCats(List catIDs, BigDecimal[] selectedIDs) {
	DataQuery items =
		SessionManager.getSession().retrieveQuery(
		"com.arsdigita.navigation.PopulatedSubcategories");
	Party party = Kernel.getContext().getParty();
	if (party == null) {
	    party = Kernel.getPublicUser();
	}
	PermissionService.filterQuery(
			items,
			"objectInSubCat",
			PrivilegeDescriptor.READ,
			party.getOID());

	Set catSet = new HashSet();

	// add root category, then restricted children of all categories 
	// included in the list

	catSet.add(selectedIDs[0]);

	Iterator it = catIDs.iterator();
	while (it.hasNext()) {
	    BigDecimal id = (BigDecimal) it.next();
	    items.setParameter("categoryID", id);
	    while (items.next()) {
		Category thisCat = new Category((DataObject) items.get("subCat"));
		catSet.add(thisCat.getID());
	    }
	    items.rewind();
	}
		
	DataCollection cats =
			SessionManager.getSession().retrieve(
			Category.BASE_DATA_OBJECT_TYPE);
	cats.addFilter(Category.ID + " in :cats").set("cats", catSet);
	return cats;

    }

}
