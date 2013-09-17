/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.london.terms;

import com.arsdigita.categorization.Category;
import com.arsdigita.categorization.CategoryCollection;
import com.arsdigita.categorization.CategoryListener;
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.SessionManager;

import org.apache.log4j.Logger;

/**
 * Used to sync terms and categories.
 * 
 * <ul>
 * <li>Attempts to create new term in the proper terms domain
 * whenever a new category is created through CMS interface.</li>
 * <li>Attempts to delete the corresponding term object if a category is deleted (if there is
 * an term object for the category)</li>
 * </ul>
 * 
 * @author Unkwown
 * @author Jens Pelzetter
 * @version $Id$
 */
public class TermCategoryListener implements CategoryListener {

    private static final Logger s_log = Logger.getLogger(
                                               TermCategoryListener.class);

    public void onDelete(Category cat) {
        final DataCollection collection = SessionManager.getSession().retrieve(Term.BASE_DATA_OBJECT_TYPE);
        collection.addPath("model.id");
        collection.addEqualsFilter("model.id", cat.getID());
        if (collection.next()) {
            final Term term = (Term) DomainObjectFactory.newInstance(collection.getDataObject());
            term.delete();
        }        
    }

    public void onAddChild(Category cat, Category child) {
    }

    public void onRemoveChild(Category cat, Category child) {
    }

    public void onMap(Category cat, ACSObject obj) {
    }

    public void onUnmap(Category cat, ACSObject obj) {
    }

    /**
     * Create new term by hooking into setDefaultParent() category
     * event.  First, check whether the category is a model for an
     * existing term.  If yes, do nothing.  Otherwise, find the
     * terms domain in which the parent cat has been defined.
     * If none can be found, do nothing.  We've tried our best.
     * Otherwise, create new term and assign it to the category
     * just created.
     */
    public void onSetDefaultParent(Category cat, Category parent) {
        // Check whether a term already exists for this category.
        DataCollection allTerms = SessionManager.getSession().retrieve(
                Term.BASE_DATA_OBJECT_TYPE);
        allTerms.addEqualsFilter(Term.MODEL, cat.getID());
        if (allTerms.next()) {
            s_log.debug("A term already exists for cat: " + cat);
            allTerms.close();
            return;
        }
        // Now find the term domain.  We look at the first entry in denormalized
        // category hierarchy, it must be a model category for the domain.
        CategoryCollection parentPath = cat.getDefaultAscendants();
        parentPath.addOrder(Category.DEFAULT_ANCESTORS);
        Category root = null;
        if (parentPath.next()) {
            root = parentPath.getCategory();
            parentPath.close();
        } else {
            s_log.warn("Could not determine root cat for: " + cat);
            return;
        }

        DataCollection domains = SessionManager.getSession().retrieve(
                Domain.BASE_DATA_OBJECT_TYPE);
        domains.addEqualsFilter(Domain.MODEL, root.getID());
        Domain termDomain = null;
        if (domains.next()) {
            termDomain = new Domain(domains.getDataObject());
            domains.close();
        } else {
            s_log.warn("Could not determine term domain for model cat: " + root);
            return;
        }
        DomainCollection terms = termDomain.getTerms();
        terms.addOrder(Term.UNIQUE_ID + " DESC");
        Integer maxID = Integer.valueOf(0);
        if (terms.next()) {
            maxID = Integer.valueOf((String) terms.get(Term.UNIQUE_ID));
            terms.close();
        }

        Term.create(cat,
                    String.valueOf(maxID.intValue() + 1),
                    false,
                    "",
                    termDomain);
    }
}
