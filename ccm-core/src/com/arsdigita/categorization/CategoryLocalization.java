/*
 * Copyright (C) 2008 Sören Bernstein All Rights Reserved.
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
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.metadata.ObjectType;
import java.math.BigDecimal;

/**
 *
 * @author Sören Bernstein (quasimodo) quasi@zes.uni-bremen.de
 */
public class CategoryLocalization extends ACSObject {
    
    public static final String BASE_DATA_OBJECT_TYPE = "com.arsdigita.categorization.CategoryLocalization";
    private static final String BASE_DATA_OBJECT_PACKAGE = "com.arsdigita.categorization";
    
    // Constants to use in the code
    public static final String LOCALE = "locale";
    public static final String NAME = "name";
    public static final String DESCRIPTION = "description";
    public static final String URL = "url";
    public static final String IS_ENABLED = "isEnabled";
    
    
    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }
    
    public static String getBaseDataObjectPackage() {
        return BASE_DATA_OBJECT_PACKAGE;
    }
    
    /**
     * Initializes the categoryLocalization with the specified data object.
     *
     * @param categoryObjectData the data object
     **/
    public CategoryLocalization(DataObject categoryLocalizationObjectData) {
        super(categoryLocalizationObjectData);
    }
    
    /**
     * Serves as a shortcut to {@link #CategoryLocalization(String)
     * CategoryLocalization(CategoryLocalization.BASE_DATA_OBJECT_TYPE)}.
     *
     * @see com.arsdigita.domain.DomainObject#DomainObject(String)
     **/
    public CategoryLocalization() {
        this(BASE_DATA_OBJECT_TYPE);
    }


    /**
     * Initializes the contained data object with a new data object whose object
     * type is specified by the passed in type name.
     *
     * @param typeName the object type for the contained data object
     *
     * @see com.arsdigita.domain.DomainObject#DomainObject(String)
     * @see com.arsdigita.persistence.DataObject
     * @see com.arsdigita.persistence.metadata.ObjectType
     **/
    public CategoryLocalization(String typeName) {
        super(typeName);
    }

    /**
     * Initializes the contained data object with a new data object whose object
     * type is the specified type.
     *
     * @param type the object type for the contained data object
     *
     * @see com.arsdigita.domain.DomainObject#DomainObject(ObjectType)
     * @see com.arsdigita.persistence.DataObject
     **/
    public CategoryLocalization(ObjectType type) {
        super(type);
    }


    /**
     * Retrieves the data object with the specified OID from the persistent
     * storage mechanism.
     *
     * @param oid the OID for the data object to retrieve
     * @throws DataObjectNotFoundException if this OID is invalid or has been
     * deleted.
     *
     * @see com.arsdigita.domain.DomainObject#DomainObject(OID)
     * @see com.arsdigita.persistence.DataObject
     **/
    public CategoryLocalization(OID oid) {
        super(oid);
    }


    /**
     * Retrieves the data object with the specified ID from the persistence
     * storage mechanism.  This method is just a wrapper for the {@link
     * #CategoryLocalization(OID)} constructor.
     *
     * @throws DataObjectNotFoundException
     */
    public CategoryLocalization(BigDecimal id) {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    /**
     * Creates a new categoryLocalization with the given name and description.
     *
     * @param name the name for the new category
     * @param description the description for the new category
     */
    public CategoryLocalization(String locale, String name, String description) {
        this();
        setLocale(locale);
        setName(name);
        setDescription(description);
    }


    /**
     * Creates a new categoryLocalization with the given name, description and URL
     * component.
     *
     * @param name the name for the new category
     * @param description the description for the new category
     * @param url URL component used when browsing categories.
     */
    public CategoryLocalization(String locale, String name, String description, String url) {
        this();
        setLocale(locale);
        setName(name);
        setDescription(description);
        setURL (url);
    }


    /**
     * Retrieves the categoryLocalization with the given category ID, and sets the name and
     * description.  For the new name and descrption to be permanent, the caller
     * must call the save() method.
     *
     * @param categoryID the category ID
     * @param name the category name
     * @param description the category description
     * @exception DataObjectNotFoundException if this OID is
     *            invalid or has been deleted.
     *
     * @see com.arsdigita.domain.DomainObject#DomainObject(OID)
     */
    public CategoryLocalization(OID categoryID, String name, String description) {
        this(categoryID);
        setName(name);
        setDescription(description);
    }


    /**
     * Retrieves the categoryLocalization with the given category ID, and sets the name and
     * description.  For the new name and descrption to be permanent, the caller
     * must call the save() method.
     *
     * @param categoryID the category ID
     * @param name the category name
     * @param description the category description
     * @param url URL component used when browsing categories.
     * @exception DataObjectNotFoundException if this OID is
     *            invalid or has been deleted.
     *
     * @see com.arsdigita.domain.DomainObject#DomainObject(OID)
     */
    public CategoryLocalization(OID categoryID, String name, String description, String url) {
        this(categoryID);
        setName(name);
        setDescription(description);
        setURL (url);
    }

    
    
    
    /**
     * @see com.arsdigita.domain.DomainObject#initialize()
     */
    protected void initialize() {
        super.initialize();

        if(isNew()) {
            setEnabled(true);
        }

    }
    
    
    // Getter / Setter methods
    
    /**
     * Returns the locale
     */
    public String getLocale() {
        return (String) get(LOCALE);
    }
    
    /**
     * Sets the locale
     */
    private void setLocale(String locale) {
        set(LOCALE, locale);
    }
    
    /**
     * Returns the localized name
     */
    public String getName() {
        return (String) get(NAME);
    }
    
    /**
     * Set localized name
     */
    public void setName(String name) {
        set(NAME, name);
    }
    
    /**
     * Returns the localized description
     */
    public String getDescription() {
        return (String) get(DESCRIPTION);
    }
    
    /**
     * Set localized description
     */
    public void setDescription(String description) {
        set(DESCRIPTION, description);
    }
    
    /**
     * Returns the localized URL
     */
    public String getURL() {
        return (String) get(URL);
    }
    
    /**
     * Set localized URL
     */
    public void setURL(String url) {
        set(URL, url);
    }
    
    /**
     * Returns the localized status
     */
    public boolean isEnabled() {
        return ((Boolean) get(IS_ENABLED)).booleanValue();
    }
    
    /**
     * Set localized status
     */
    public void setEnabled(boolean isEnabled) {
        set(IS_ENABLED, new Boolean(isEnabled));
    }
    
}
