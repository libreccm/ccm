/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.persistence.DataAssociationCursor;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.util.Assert;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.log4j.Logger;

/**
 * Implements persistent storage of category purposes, which may be used to
 * filter the list of categories associated with an object.
 *
 * @author Scott Seago
 * @version $Revision: #13 $ $DateTime: 2004/08/16 18:10:38 $
 * @deprecated See the note about "use context" methods in the documentation for
 * {@link Category} class.
 */
public class CategoryPurpose extends ACSObject {

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.categorization.CategoryPurpose";
    private static final String BASE_DATA_OBJECT_PACKAGE =
        "com.arsdigita.categorization";

    public static final String KEY = "key";
    public static final String NAME = "name";
    public static final String DESCRIPTION = "description";
    public static final String CATEGORIES = "purposeCategories";
    public static final String LANGUAGE = "language";

    /**
     * Constant for identifying the Navigation purpose (used for navbars, etc.)
     */
    public static final String NAVIGATION = "navigation";

    /**
     * Constant for identifying the Advanced Search purpose
     */
    public static final String ADVANCED_SEARCH = "advancedSearch";

    private static final Logger s_log =
        Logger.getLogger(CategoryPurpose.class);


    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }


    /**
     * Gets the base data object package.
     *
     * @return the baseDataObjectPackage for CategoryPurposes.
     */
    public static String getBaseDataObjectPackage() {
        return BASE_DATA_OBJECT_PACKAGE;
    }

    /**
     * Sets up the set of valid CategoryPurposes.
     */
    public static void setupCategoryPurposes() {
        if (!purposeExists(NAVIGATION)) {
            CategoryPurpose purpose =
                new CategoryPurpose(NAVIGATION,
                                    "Navigation");
            purpose.save();
        }
        if (!purposeExists(ADVANCED_SEARCH)) {
            CategoryPurpose purpose =
                new CategoryPurpose(ADVANCED_SEARCH,
                                    "Advanced Search");
            purpose.save();
        }
    }

    /**
     * Determines whether there exists a CatgegoryPurpose with the given key.
     *
     * @param key The integer key
     *
     * @return whether the purpose exists.
     */
    public static boolean purposeExists(String key) {
        Session ssn = SessionManager.getSession();
        DataCollection purposes = ssn.retrieve(BASE_DATA_OBJECT_TYPE);
        Filter keyFilter = purposes.addFilter(KEY + " = :key");
        keyFilter.set("key", key);
        boolean exists = purposes.next();
        if (exists) {
            purposes.close();
        }
        return exists;
    }

    /**
     * Returns a CategoryPurpose with the given key.
     *
     * @param key The integer key
     *
     * @return the CategoryPurpose
     */
    public static CategoryPurpose getPurpose(String key) {
        Session ssn = SessionManager.getSession();
        DataCollection purposes = ssn.retrieve(BASE_DATA_OBJECT_TYPE);
        Filter keyFilter = purposes.addFilter(KEY + " = :key");
        keyFilter.set("key", key);
        boolean exists = purposes.next();
        CategoryPurpose thePurpose = null;
        if (exists) {
            thePurpose = new CategoryPurpose(purposes.getDataObject());
            purposes.close();
        }
        return thePurpose;
    }

    /**
     * Initializes with the specified data object.
     *
     * @param categoryPurposeObjectData the data object
     **/
    public CategoryPurpose(DataObject categoryPurposeObjectData) {
        super(categoryPurposeObjectData);
    }

    /**
     * Initalizes the contained data object with a new data object that has the
     * <code>ObjectType</code> of CategoryPurpose.
     *
     * @see com.arsdigita.domain.DomainObject#DomainObject(String)
     * @see com.arsdigita.persistence.metadata.ObjectType
     **/
    public CategoryPurpose() {
        this(BASE_DATA_OBJECT_TYPE);
    }


    /**
     * Initializes the contained data object with a new data object whose object
     * type is specified by the passed in type name.
     *
     * @param typeName the object type for the
     * contained data object
     *
     * @see com.arsdigita.domain.DomainObject#DomainObject(String)
     * @see com.arsdigita.persistence.DataObject
     * @see com.arsdigita.persistence.metadata.ObjectType
     **/
    public CategoryPurpose(String typeName) {
        super(typeName);
    }

    /**
     * Initializes the contained data object with a new data object whose object
     * type is the specified type.
     *
     * @param type the object type for the contained
     * data object
     *
     * @see com.arsdigita.domain.DomainObject#DomainObject(ObjectType)
     * @see com.arsdigita.persistence.DataObject
     **/
    public CategoryPurpose(ObjectType type) {
        super(type);
    }


    /**
     * Retrieves the data object with the specified OID from the persistent
     * storage mechanism.  If the OID is not present, it throws a
     * DataObjectNotFoundException.
     *
     * @param oid the OID for the data object to retrieve
     * @exception DataObjectNotFoundException if this OID is
     *            invalid or has been deleted.
     *
     * @see com.arsdigita.domain.DomainObject#DomainObject(OID)
     * @see com.arsdigita.persistence.DataObject
     **/
    public CategoryPurpose(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    /**
     * Creates a new category with the given key, name, and description.
     *
     * @param key the key for the new categoryPurpose
     * @param name the name for the new categoryPurpose
     * @param description the description for the new categoryPurpose
     */
    public CategoryPurpose(String key, String name, String description) {
        this();
        setKey(key);
        setName(name);
        setDescription(description);
    }

    /**
     * Creates a new category with the given key and name.
     *
     * @param key the key for the new categoryPurpose
     * @param name the name for the new categoryPurpose
     * @return a new category.
     */
    public CategoryPurpose(String key, String name) {
        this();
        setKey(key);
        setName(name);
    }


    /**
     * Returns the key of the CategoryPurpose.
     * @return the CategoryPurpose key.
     */
    public String getKey() {
        return (String) get(KEY);
    }


    /**
     * Sets the key of the CategoryPurpose.
     *
     * @param value the new key of the CategoryPurpose
     */
    public void setKey(String value) {
        set(KEY, value);
    }


    /**
     * Returns the name of the CategoryPurpose.
     * @return the CategoryPurpose name.
     */
    public String getName() {
        return (String) get(NAME);
    }


    /**
     * Sets the name of the CategoryPurpose.
     *
     * @param value the new name of the CategoryPurpose
     */
    public void setName(String value) {
        set(NAME, value);
    }


    /**
     * Returns the description of the CategoryPurpose.
     * @return the CategoryPurpose description.
     */
    public String getDescription() {
        return (String) get(DESCRIPTION);
    }


    /**
     * Sets the description of the CategoryPurpose.
     *
     * @param value the new description of the CategoryPurpose
     */
    public void setDescription(String value) {
        set(DESCRIPTION, value);
    }

    /**
     * Returns a collection of categories for this category purpose.
     **/
    public Collection getCategories() {
        DataAssociationCursor categoryCur =
            ((DataAssociation)get(CATEGORIES)).cursor();
        try {
            categoryCur.addOrder("name asc");
            Collection categories = new LinkedList();
            while (categoryCur.next()) {
                Category category = (Category)
                    DomainObjectFactory.newInstance(categoryCur.getDataObject());
                categories.add(category);
            }
            return categories;

        } finally {
            categoryCur.close();
        }
    }

    /**
     * Returns all existing category purposes.
     **/
    public static Collection getAllPurposes() {
        Session ssn = SessionManager.getSession();
        DataCollection allPurposes = ssn.retrieve(BASE_DATA_OBJECT_TYPE);

        try {
            allPurposes.addOrder("name asc");
            Collection purposes = new LinkedList();
            while (allPurposes.next()) {
                CategoryPurpose purpose = (CategoryPurpose)
                    DomainObjectFactory.newInstance(allPurposes.getDataObject());
                purposes.add(purpose);
            }
            return purposes;

        } finally {
            allPurposes.close();
        }
    }


    /**
     *  Returns the language of the CategoryPurpose.
     *  @return the CategoryPurpose language.
     */
    public String getLanguage() {
        return (String) get(LANGUAGE);
    }


    /**
     *  Sets the language of the CategoryPurpose.
     *
     *  @param value the new language of the CategoryPurpose
     */
    public void setLanguage(String value) {
        set(LANGUAGE, value);
    }



    /*
     * @return the root Category object for the given root key
     * ie.  CategoryPurpose.getRootCategory(CategoryPurpose.SEARCH)
     *
     */
    public static Category getRootCategory(String key) {

        Assert.isTrue(purposeExists(key));

        // get the purpose according to the key
        Session ssn = SessionManager.getSession();
        DataCollection allPurposes = ssn.retrieve(BASE_DATA_OBJECT_TYPE);
        Filter keyFilter = allPurposes.addFilter(KEY + " = :key");
        keyFilter.set("key", key);

        // there should be exactly one purpose for this key
        Assert.isTrue(allPurposes.next());
        CategoryPurpose purpose = (CategoryPurpose)
            DomainObjectFactory.newInstance(allPurposes.getDataObject());
        Assert.isTrue(!allPurposes.next());

        // now need to figure out the root category out of all the categories
        // mapped to this purpose....  (no good way to do this...)  I'll do it
        // by looking for the category that has no parents...

        Collection categories = purpose.getCategories();
        Assert.isTrue(!categories.isEmpty(),
                          "Categories collection is empty");
        Iterator categoriesIterator = categories.iterator();
        Category category;

        Assert.isTrue(categoriesIterator.hasNext(),
                          "can't find core categories");
        do {
            category = (Category)categoriesIterator.next();
            if (category.isRoot()) {
                return category;
            }
        } while (categoriesIterator.hasNext());

        // error -- you couldn't find the root category in this collection
        throw new RuntimeException(
            "couldn't find root category for purpose " + key
        );

    }

}
