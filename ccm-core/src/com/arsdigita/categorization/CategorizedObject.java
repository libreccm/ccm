/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.categorization;

import com.arsdigita.persistence.DataObject;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.DomainServiceInterfaceExposer;
import com.arsdigita.persistence.DataAssociationCursor;
import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.kernel.ACSObject;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * A wrapper for a categorized {@link ACSObject}, such as the <em>Jaws</em>
 * object in <a href="package-summary.html#sample_cat">this diagram</a>.
 * 
 * <p>The reason this class exists is because the {@link ACSObject} class does
 * not know about the categorization service.  Therefore, if you have a
 * categorized <code>ACSObject</code>, it does not provides methods for
 * answering questions like: What are you parents?  What is your default
 * category?, and so on. This class serves as a wrapper that provides these and
 * other methods.</p>
 *
 *
 * @author David Eison
 * @version $Revision: #24 $ $DateTime: 2004/08/16 18:10:38 $
 */
public class CategorizedObject {

    private final static String IS_DEFAULT = Category.IS_DEFAULT;

    private ACSObject m_data;

    /**
     * Initializes with the specified ACS object.
     *
     * @param data the ACS object
     **/
    public CategorizedObject(ACSObject data) {
        m_data = data;
    }

    /**
     * Creates the appropriate type of ACS object from the passed in data
     * object.
     *
     * @throws IllegalArgumentException if the data object is not an ACS object.
     */
    public CategorizedObject(DataObject dataObject) {
        try {
            m_data = (ACSObject) DomainObjectFactory.newInstance(dataObject);
        } catch (ClassCastException ex) {
            throw new IllegalArgumentException
                ("An ACSObject cannot be constructed from this data object: " +
                 dataObject);
        }
    }

    /**
     * Gets the contained object.
     *
     * @return the contained object.
     */
    public ACSObject getObject() {
        return m_data;
    }

    /**
     * Returns the parent categories for this domain object.
     *
     * @return a collection of categories that this object is classified under.
     * @throws DataObjectNotFoundException
     * @deprecated use {@link #getParents()}
     **/
    public Collection getParentCategories() {
        Collection parents = new LinkedList();
        DataAssociationCursor categories = getParentCategoriesAssoc();

        while (categories.next()) {
            parents.add(new Category((DataObject) categories.getDataObject()));
        }
        categories.close();
        return parents;
    }

    public CategoryCollection getParents() {
        return new CategoryCollection(getParentCategoriesAssoc());
    }

    /**
     * Returns the parent categories for this domain object that correspond to a
     * particular CategoryPurpose.
     *
     * <p>For a category to correspond to a CategoryPurpose, the ancestor
     * category which is a direct child of the category root must be associated
     * with a CategoryPurpose whose key equals that passed into this method.</p>
     *
     * @param purposeKey the Integer key for the desired CategoryPurpose
     * @return a collection of categories that this object is classified under.
     * @throws DataObjectNotFoundException
     * @deprecated the notion of "category purposes" has been deprecated. Use
     * the use-context-based API, as explained in {@link Category}.
     **/
    public Collection getParentCategories(String purposeKey) {
        Iterator parents = getParentCategories().iterator();
        Collection filteredParents = new LinkedList();
        while (parents.hasNext()) {
            Category category = (Category) parents.next();

            // returns ancestors in order from the category root to
            // this category
            CategoryCollection ancestors = category.getDefaultAscendants();
            ancestors.addOrder(Category.DEFAULT_ANCESTORS);

            // discard first ancestor (category root)
            ancestors.next();

            if (ancestors.next()) {
                Category ancestor = ancestors.getCategory();
                Iterator purposes = ancestor.getPurposes().iterator();
                while (purposes.hasNext()) {
                    CategoryPurpose purpose = (CategoryPurpose) purposes.next();
                    if (purposeKey.equals(purpose.getKey())) {
                        filteredParents.add(category);
                        break;
                    }
                }
            }

        }
        return filteredParents;
    }

    /**
     * Gets the default parent category.
     *
     * @return the default parent category.
     * @throws DataObjectNotFoundException
     * @throws CategoryNotFoundException
     */
    public Category getDefaultParentCategory() {
        DataAssociationCursor cursor = getParentCategoriesAssoc();
        cursor.addEqualsFilter("link.isDefault", Boolean.TRUE);

        try {
            if (cursor.next()) {
                return new Category((DataObject) cursor.getDataObject());
            }
        } finally {
            cursor.close();
        }

        throw new CategoryNotFoundException
            ("The ACSObject does not have a default parent: " + m_data);
    }


    /**
     *  Gets the count of parent categories for this object.
     *
     *  @return the number of times this object has been mapped to a new category.
     */
    public long getParentCategoryCount() {
        return getParentCategoriesAssoc().size();
    }


    /**
     * Returns the default ancestor categories up to the root, with the root as
     * the first item in the iterator.
     *
     * @return an interator over the default ancestor categories.
     * @throws DataObjectNotFoundException
     * @throws CategorizationException
     */
    // SCALABILITY ALERT: this could be done much faster with a "connect by"
    // specifically, see  "getCategoryDefaultAncestors" in Category.pdl
    public Iterator getDefaultAncestors()  {
        LinkedList ancestors = new LinkedList();
        Category parent;

        try {
            parent = getDefaultParentCategory();
        } catch (CategorizationException e) {
            // this means that the object does not have a default parent
            parent = null;
        }

        while (parent != null) {
            ancestors.add(parent);
            try {
                parent = parent.getDefaultParentCategory();
            } catch (CategorizationException ex) {
                // let's make sure this is not because it was a root
                // and not from some other CategorizationExcedption
                if (parent.isRoot()) {
                    // we have reached the root so let's break out of the
                    // while loop since we do not have any more parents to add.
                    parent = null;
                } else {
                    throw ex;
                }
            }
        }

        return ancestors.iterator();
    }


    /**
     * <p>Sets the passed in category as the default parent category for this
     * category.  If the passed on object is not already a parent then this add
     * the passed in object as a new parent.</p>
     *
     * <p><b>Note: This clears the previous default mapping.</b>
     * The results are saved for the user when the transaction
     * is committed.</p>
     *
     * @param parent the new default category.
     * @throws DataObjectNotFoundException
     */
    public void setDefaultParentCategory(Category parent)  {
        DataAssociationCursor cursor = getParentCategoriesAssoc();

        boolean found = false;
        while (cursor.next()) {
            final DataObject category = cursor.getDataObject();
            final DataObject link = cursor.getLink();
            if (Boolean.TRUE.equals((Boolean) link.get(IS_DEFAULT))) {
                link.set(IS_DEFAULT, Boolean.FALSE);
            }

            if ( parent != null &&
                 parent.getID().equals(category.get("category.id"))) {

                link.set(IS_DEFAULT, Boolean.TRUE);
                found = true;
            }
        }
        cursor.close();

        if (parent != null && !found) {
            DataAssociation assoc = (DataAssociation) DomainServiceInterfaceExposer
                .get(m_data, "categories");
            DataObject link = assoc.add
                (DomainServiceInterfaceExposer.getDataObject(parent));
            link.set("isDefault", Boolean.TRUE);
        }
    }


    /**
     *  This returns the parent category association so that we reference the
     *  interface exposer as little as possible.
     */
    private DataAssociationCursor getParentCategoriesAssoc() {
        DataAssociation assoc = (DataAssociation)DomainServiceInterfaceExposer
            .get(m_data, "categories");
        assoc.addOrder("isDefault desc");
        return assoc.cursor();
    }

    /**
     * This saves all of the changes that have been made to this object as well
     * as saving the underlying ACSObject.
     */
    public void save() {
        m_data.save();
    }

    /**
     * Determines whether the contained objects are equal.
     *
     * @return <code>true</code> if the two contained objects match;
     * <code>false</code> otherwise.
     */
    public boolean equals(Object o) {
        if ( !(o instanceof CategorizedObject) ) { return false; }

        CategorizedObject co = (CategorizedObject) o;
        if (m_data == null) {
            return (co.m_data == null);
        } else {
            return m_data.equals(co.m_data);
        }
    }

    /**
     * Delegates hashCode generation to the contained ACS object.
     */
    public int hashCode() {
        if (m_data == null) {
            return 0;
        } else {
            return m_data.hashCode();
        }
    }

    public String toString() {
        return "CategorizedObject: " + m_data;
    }
}
