/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arsdigita.subsite.dispatcher;

import com.arsdigita.categorization.Category;
import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.dispatcher.ItemURLFinder;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.subsite.Site;
import com.arsdigita.subsite.Subsite;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Sören Bernstein (quasimodo) <sbernstein@zes.uni-bremen.de>
 */
public class SubsiteItemURLFinder extends ItemURLFinder {

    /**
     * Get all categories for a content bundle, where the content bundle is an
     * index item in the current subsite.
     *
     * @param bundle
     * @return a list of categories from this subsite
     */
    @Override
    protected List<DataObject> getCategories(ContentBundle bundle) {

        List<DataObject> categories = super.getCategories(bundle);
        List<DataObject> subsiteCategories = new ArrayList<DataObject>();

        // If there is a current subsite, filter all categories which not belong
        // to this subsites root category
        if (Subsite.getContext().hasSite()) {
            Site site = Subsite.getContext().getSite();
            Category subsiteRootCat = site.getRootCategory();

            if (subsiteRootCat != null) {
                for (DataObject dobj : categories) {
                    Category cat = (Category) DomainObjectFactory.newInstance(dobj);
                    if (subsiteRootCat.isMemberOfSubtree(cat)) {
                        subsiteCategories.add(dobj);
                    }
                }
                return subsiteCategories;
            }
        }
        return categories;
    }
}
