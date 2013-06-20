/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arsdigita.subsite;

import com.arsdigita.categorization.Category;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.navigation.AbstractNavigationModel;

/**
 *
 * @author Sören Bernstein (quasimodo) <sbernstein@zes.uni-bremen.de>
 */
public class SearchNavigationModel extends AbstractNavigationModel {

    @Override
    protected ACSObject loadObject() {
        Category category = getCategory();
        if (category == null) {
            return null;
        }
        return category.getIndexObject();
    }

    @Override
    protected Category loadCategory() {
        Category[] path = getCategoryPath();
        if (path == null
                || path.length == 0) {
            return null;
        }
        return path[path.length - 1];
    }

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
