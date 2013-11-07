/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arsdigita.subsite;

import com.arsdigita.categorization.Category;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.navigation.AbstractNavigationModel;

/**
 * A subsite aware navigation model for search application
 *
 * @author Sören Bernstein <quasi@quasiweb.de>
 */
public class SearchNavigationModel extends AbstractNavigationModel {

	/**
	 * Get the index item of the current category
	 * 
	 * @return the index item, or null if there is no current category
	 */
	@Override
	protected ACSObject loadObject() {
		Category category = getCategory();
		if (category == null) {
			return null;
		}
		return category.getIndexObject();
	}

	/**
	 * Get the current category
	 *
	 * @return the current category, or null if category path is empty
	 */
	@Override
	protected Category loadCategory() {
		Category[] path = getCategoryPath();
		if (path == null
			|| path.length == 0) {
			return null;
		}
		return path[path.length - 1];
	}

	/**
	 * Subsite aware loadCategoryPath method which uses {@link Subsite} to
	 * get the category path
	 *
	 * @return category path, or null if not subsite is active
	 */
	@Override
	protected Category[] loadCategoryPath() {
		if (Subsite.getContext().hasSite()) {
			Category path[] = new Category[1];
			Site subsite = Subsite.getContext().getSite();
			path[0] = subsite.getRootCategory();
			return path;
		} else {
			return null;
		}
	}

	/**
	 * Subsite aware loadRootCaegory method which uses {@link Subsite} to
	 * get root category
	 *
	 * @return root category, or null if no subsite is active
	 */
	@Override
	protected Category loadRootCategory() {

		if (Subsite.getContext().hasSite()) {
			Site subsite = Subsite.getContext().getSite();
			return subsite.getRootCategory();
		} else {
			return null;
		}
	}
}
