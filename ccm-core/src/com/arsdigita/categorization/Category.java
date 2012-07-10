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

import com.arsdigita.db.Sequences;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.DomainServiceInterfaceExposer;
import com.arsdigita.globalization.GlobalizationHelper;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.persistence.DataAssociationCursor;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataOperation;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.DataQueryDataCollectionAdapter;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.util.Assert;
import com.arsdigita.util.HierarchyDenormalization;
import com.arsdigita.util.StringUtils;
import com.arsdigita.util.UncheckedWrapperException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import org.apache.log4j.Logger;

/**
 *
 * <p>Implements persistent storage of categories. See {@link
 * com.arsdigita.categorization} for a more detailed description of what
 * <em>categories</em> are and what they can be used for. </p>
 *
 * <p>This is a persistent class backed by the data object type whose name is
 * specified by {@link #BASE_DATA_OBJECT_TYPE}. The attribute names of of this
 * data object type are exposed via the public string fields such as {@link
 * #CHILD}, {@link #DEFAULT_ANCESTORS}.  See below.</p>
 *
 * <p>This version of the class deprecates the notion of <em>category
 * purposes</em> and replaces it with the notion of <em>use contexts</em>.
 * The following methods should no longer be used:</p>
 *
 * <ul>
 *   <li>{@link #getPurposes()}</li>
 *   <li>{@link #addPurpose(CategoryPurpose)}</li>
 *   <li>{@link #removePurpose(CategoryPurpose)}</li>
 * </ul>
 *
 * <p>The following methods should be used instead:</p>
 *
 * <ul>
 *   <li>{@link #getRootForObject(ACSObject, String)}</li>
 *   <li>{@link #setRootForObject(ACSObject, Category,String)}</li>
 *   <li>{@link #clearRootForObject(ACSObject, String)}</li>
 *   <li>{@link #getRootCategories(ACSObject)}</li>
 * </ul>
 *
 * @author Randy Graebner
 * @version $Revision: 1.1 $ $DateTime: $
 *
 * <p>Localization is done with some new classes, so the category tree is
 * now multilanguage. This is completly transparent to the rest of the
 * system (hopefully) and uses the negotiated language from the browser
 * environment. The following attributes are localizable:
 *
 * <ul>
 *   <li>Name</li>
 *   <li>Description</li>
 *   <li>URL</li>
 *   <li>IsEnabled</li>
 * </ul>
 *
 * To use localized URLs I had to change NavigationFileReolver.resolveCategory()
 * in ccm-ldn-navigation to filter the categories in Java. There might be other
 * location in the code where this patch may also be needed. So fix it.
 *
 * Quasimodo
 */
public class Category extends ACSObject {

    private static final Logger s_log = Logger.getLogger(Category.class);
    /**
     * @see ObjectType
     **/
    public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.categorization.Category";
    private static final String BASE_DATA_OBJECT_PACKAGE =
                                "com.arsdigita.categorization";
    /**
     * The <code>PrivilegeDescriptor corresponding</code> to the privilege to
     * map subcategories and objects to this category
     **/
    public static final PrivilegeDescriptor MAP_DESCRIPTOR =
                                            new PrivilegeDescriptor(
            "map_to_category");
    // Quasimodo: Begin
    private static CategorizationConfig s_config = CategorizationConfig
                                                   .getConfig();
    // Quasimodo: End
    public static final String ROOT_CATEGORY = "rootCategory";
    public static final String USE_CONTEXT = "useContext";
    public static final String CATEGORY_OWNER = "categoryOwner";
    public static final String ROOT_USE_CONTEXT = "rootUseContext";
    public static final String OWNER_USE_CONTEXT = "ownerUseContext";
    // category-category mapping types
    /** An attribute name for the underlying data object. */
    public static final String CHILD = "child";
    /** An attribute name for the underlying data object. */
    public static final String PARENTS = "parents";
    /** An attribute name for the underlying data object. */
    public static final String RELATED = "related";
    public static final String PREFERRED = "preferred";
    /** An attribute name for the underlying data object. */
    public static final String REL_TYPE = "relationType";
    /** An attribute name for the underlying data object. */
    public static final String SORT_KEY = "sortKey";
    public static final String IS_DEFAULT = "isDefault";
    public static final String IS_INDEX = "isIndex";
    public static final String PARENT_CATEGORY = "parentCategory";
    public static final String CATEGORY_ID = "categoryID";
    // these are some constants to use in the code
    /** An attribute name for the underlying data object. */
    public static final String NAME = "name";
    /** An attribute name for the underlying data object. */
    public static final String DESCRIPTION = "description";
    /** An attribute name for the underlying data object. */
    public static final String URL = "url";
    /** An attribute name for the underlying data object. */
    public static final String IS_ENABLED = "isEnabled";
    /** An attribute name for the underlying data object. */
    public static final String IS_ABSTRACT = "isAbstract";
    /** An attribute name for the underlying data object. */
    public static final String DEFAULT_ANCESTORS = "defaultAncestors";
    /** An attribute name for the underlying data object. */
    public static final String IGNORE_PARENT_INDEX_ITEM =
                               "ignoreParentIndexItem";
    /** An attribute name for the underlying data object. */
    private static final String PURPOSES = "purposes";
    // this contains association names from the pdl file
    public final static String CHILD_OBJECTS = "childObjects";
    public final static String RELATED_CATEGORIES = RELATED;
    public final static String CATEGORIES = "categories";
    public static final String LOCALIZATIONS = "localizations";
    // some named queries in the pdl files
    private static final String CHILD_CATEGORY_IDS =
                                "com.arsdigita.categorization.childCategoryIDs";
    private static final String CURRENT_SORT_KEY = "currentSortKey";
    private HierarchyDenormalization m_hierarchy;
    // Quasimodo: Begin
    // Save the localized parts of category
    private CategoryLocalizationCollection m_categoryLocalizationCollection;
    // Quasimodo: End

    @Override
    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    /**
     * Returns the model name of {@link #BASE_DATA_OBJECT_TYPE}.
     **/
    public static String getBaseDataObjectPackage() {
        return BASE_DATA_OBJECT_PACKAGE;
    }

    /**
     * Initializes the category with the specified data object.
     *
     * @param categoryObjectData the data object
     **/
    public Category(DataObject categoryObjectData) {
        super(categoryObjectData);
    }

    /**
     * Serves as a shortcut to {@link #Category(String)
     * Category(Category.BSE_DATA_OBJECT_TYPE)}.
     *
     * @see com.arsdigita.domain.DomainObject#DomainObject(String)
     **/
    public Category() {
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
    public Category(String typeName) {
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
    public Category(ObjectType type) {
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
    public Category(OID oid) {
        super(oid);
    }

    /**
     * Retrieves the data object with the specified ID from the persistence
     * storage mechanism.  This method is just a wrapper for the {@link
     * #Category(OID)} constructor.
     *
     * @throws DataObjectNotFoundException
     */
    public Category(BigDecimal id) {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    /**
     * Creates a new category with the given name and description.
     *
     * @param name the name for the new category
     * @param description the description for the new category
     */
    public Category(String name, String description) {
        this();
        setName(name);
        setDescription(description);
    }

    /**
     * Creates a new category with the given name, description and URL
     * component.
     *
     * @param name the name for the new category
     * @param description the description for the new category
     * @param url URL component used when browsing categories.
     */
    public Category(String name, String description, String url) {
        this();
        setName(name);
        setDescription(description);
        setURL(url);
    }

    /**
     * Retrieves the category with the given category ID, and sets the name and
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
    public Category(OID categoryID, String name, String description) {
        this(categoryID);
        setName(name);
        setDescription(description);
    }

    /**
     * Retrieves the category with the given category ID, and sets the name and
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
    public Category(OID categoryID, String name, String description, String url) {
        this(categoryID);
        setName(name);
        setDescription(description);
        setURL(url);
    }

    // Quasimodo: Begin
    /**
     * Retrieves the current configuration
     */
    public static CategorizationConfig getConfig() {
        return s_config;
    }

    /**
     * @see com.arsdigita.domain.DomainObject#initialize()
     */
    @Override
    protected void initialize() {
        super.initialize();

        if (isNew()) {
            // Every category must have a non-null name so we give it
            // one here...would it be better to not do this and have
            // the system throw an error if we try to save a category
            // without a name?
            if (getName() == null) {
                setName("name me");
            }
            setEnabled(true);
            setAbstract(false);
            //by default do not ignore the parent index item
            setIgnoreParentIndexItem(false);

        }

        m_hierarchy = new HierarchyDenormalization(
                "com.arsdigita.categorization.updateCategoryDescendants", this,
                                                   DEFAULT_ANCESTORS) {
        };

        m_categoryLocalizationCollection = new CategoryLocalizationCollection(
                this);

    }
    // Quasimodo: End

    /**
     * Quasimodo:
     * Returns the localized name or the name key if localized version don't exist
     *
     * @return the category name.
     */
    public String getName(String locale) {

        // Test for localized version
        if (!locale.isEmpty() && m_categoryLocalizationCollection != null && m_categoryLocalizationCollection.
                localizationExists(locale)) {

            // Return value of isEnabled from localized version, so categories could be disabled depending on locale
            String name = m_categoryLocalizationCollection.getName();
            m_categoryLocalizationCollection.rewind();
            return name;
            //return m_categoryLocalizationCollection.getName();

        } else {

            // Return name key
            return (String) get(NAME);
        }

    }

    /**
     * @return the category name.
     */
    public String getName() {
        return getName(GlobalizationHelper.getNegotiatedLocale().getLanguage());
    }

    /**
     * Returns the display name of the category.  This overrides the parent
     * implementation.
     * @return the category name.
     */
    public String getDisplayName(String locale) {
        return getName(locale);
    }

    /**
     * Returns the display name of the category.  This overrides the parent
     * implementation.
     * @return the category name.
     */
    @Override
    public String getDisplayName() {
        return getName();
    }

    /**
     *  Returns the name of the category along with its default ancestors.
     *
     * <p>For example, when called on the <em>Televised Events</em> category in
     * <href="package-summary.html#taxonomy">this example</a>, the return value
     * would be something like "Entertainment >> Movies >> Televised
     * Events". </p>
     *
     * @see #getDefaultAscendants()
     * @param delimiter The string to use to seperate the parents from the
     * children.  For example, to obtain the above-mentioned result, you need to
     * pass in <code>" >> "</code> as the delimiter.
     *
     * @param includeRoot This indicates whether or not the root category should
     * be included in the name.
     *
     * @return the qualified name as explained above.  May return
     * <code>null</code> this is the root category, and <code>includeRoot</code>
     * is false.
     */
    public String getQualifiedName(String delimiter, boolean includeRoot) {
        // If the collection only contains the root and the root is not desired
        // then we return null
        if (!includeRoot && getDefaultAscendants().size() < 2) {
            return null;
        }

        CategoryCollection ancestors = getDefaultAscendants();
        ancestors.addOrder(DEFAULT_ANCESTORS);

        List names = new LinkedList();

        if (!includeRoot) {
            ancestors.next();
        }

        while (ancestors.next()) {
            names.add(ancestors.getCategory().getName());
        }

        return StringUtils.join(names, delimiter);
    }

    /**
     * Returns the name of the <b>preferred</b> category along with its default ancestors.
     * Equivalent to #getQualifiedName(String,boolean) if this Category is not a synonym.
     * Synonym name is appended in parentheses.
     *
     * @param delimiter string to separate category names
     * @param includeRoot should root category be shown
     * @return category path
     */
    public String getPreferredQualifiedName(String delimiter,
                                            boolean includeRoot) {
        // is this a synonym?
        CategoryCollection cc = new CategoryCollection(getRelatedCategories(
                PREFERRED));
        if (cc.next()) {
            Category preferred = cc.getCategory();
            cc.close();
            String preferredPath = preferred.getQualifiedName(delimiter,
                                                              includeRoot);
            return preferredPath + " (" + getName() + ")";
        } else {
            return getQualifiedName(delimiter, includeRoot);
        }
    }

    /**
     * Returns the url of the category along with its default ancestors
     *
     * @see #getDefaultAscendants()
     *
     * @param delimiter The string to use to seperate the parents from
     * the children.

     * @param includeRoot This indicates whether or not the root category should
     * be included in the URL.
     */
    public String getQualifiedURL(String delimiter, boolean includeRoot) {
        // If the collection only contains the root and the root is not desired
        // then we return null
        if (!includeRoot && getDefaultAscendants().size() < 2) {
            return null;
        }

        CategoryCollection ancestors = getDefaultAscendants();
        ancestors.addOrder(DEFAULT_ANCESTORS);
        List names = new LinkedList();

        if (!includeRoot) {
            ancestors.next();
        }

        while (ancestors.next()) {
            names.add(ancestors.getCategory().getURL());
        }

        return StringUtils.join(names, delimiter);
    }

    /**
     * Sets the name of the category.
     *
     * @param value the new name of the category
     */
    public void setName(String name, String locale) {

        if (!locale.isEmpty() && m_categoryLocalizationCollection != null && m_categoryLocalizationCollection.
                localizationExists(locale)) {
            m_categoryLocalizationCollection.getCategoryLocalization().setName(
                    name);
        }

    }

    /**
     * Sets the name of the category.
     *
     * @param value the new name of the category
     */
    public void setName(String name) {
        set(NAME, name);
    }

    /**
     * Returns the description of the category.
     *
     * Quasimodo:
     * Returns localized version of description or description key if localized version don't exist
     *
     * @return the category description.
     */
    public String getDescription(String locale) {

        // Test for localized version
        // HACK
        if (!locale.isEmpty() && m_categoryLocalizationCollection != null && m_categoryLocalizationCollection.
                localizationExists(locale)) {

            // Return value of isEnabled from localized version, so categories could be disabled depending on locale
            String description = m_categoryLocalizationCollection.getDescription();
            m_categoryLocalizationCollection.rewind();
            return description;
            //return m_categoryLocalizationCollection.getDescription();

        } else {

            // Return description key
            return (String) get(DESCRIPTION);
        }

    }

    /**
     * Returns the description of the category.
     * @return the category name.
     */
    public String getDescription() {
        return getDescription(
                GlobalizationHelper.getNegotiatedLocale().getLanguage());
    }

    /**
     * Returns a string repesenting the default ancestors of the category
     *
     * It can be useful and more efficient then working with the
     * CategoryCollection returned by @see getDefaultAscendants() when comparing
     * ids whose position in the path is known.
     * (Added by Chris Gilbert)
     */
    public String getDefaultAncestors() {
        return (String) get(DEFAULT_ANCESTORS);
    }

    /**
     * Sets the description of the category.
     *
     * @param value the new description of the category
     */
    public void setDescription(String description, String locale) {

        if (!locale.isEmpty() && m_categoryLocalizationCollection != null && m_categoryLocalizationCollection.
                localizationExists(locale)) {
            m_categoryLocalizationCollection.getCategoryLocalization().
                    setDescription(description);
        }

    }

    /**
     * Sets the description of the category.
     *
     * @param value the new description of the category
     */
    public void setDescription(String description) {
        set(DESCRIPTION, description);
    }

    /**
     * Returns the URL component of the category.
     *
     * Quasimodo:
     * Returns the localized version of the URL or URL-key if localized version don't exist
     *
     * @return URL component used when browsing categories
     */
    public String getURL(String locale) {

        // Test for localized version
        if (!locale.isEmpty() && m_categoryLocalizationCollection != null && m_categoryLocalizationCollection.
                localizationExists(locale)) {

            // Return value of isEnabled from localized version, so categories could be disabled depending on locale
            String url =  m_categoryLocalizationCollection.getURL();
            //return m_categoryLocalizationCollection.getURL();
            m_categoryLocalizationCollection.rewind();
            return url;

        } else {

            // Return URL-key
            return (String) get(URL);
        }

    }

    /**
     * Returns the URL component of the category.
     *
     * @return URL component used when browsing categories
     */
    public String getURL() {
        return getURL(GlobalizationHelper.getNegotiatedLocale().getLanguage());
    }

    /**
     * Sets the URL component of the category.
     *
     * @param url URL component used when browsing categories
     */
    public void setURL(String url, String locale) {

        if (!locale.isEmpty() && m_categoryLocalizationCollection != null && m_categoryLocalizationCollection.
                localizationExists(locale)) {
            m_categoryLocalizationCollection.getCategoryLocalization().setURL(
                    url);
        }

    }

    /**
     * Sets the URL component of the category.
     *
     * @param url URL component used when browsing categories
     */
    public void setURL(String url) {
        set(URL, url);
    }

    /**
     * Determines the current state of the category.
     *
     * @return <code>true</code> if the category is enabled; <code>false</code>
     * otherwise.
     *
     * Quasimodo:
     * This is getting a bit more compliated:
     * 1. Check if category is globally disabled
     * 2. If not, check if localized version exists
     *  2.1 If so, return isEnabled from localized version
     *  2.2 If not, return Category.getConfig().getShowInternalName()
     *
     */
    public boolean isEnabled(String locale) {

        // If locale is empty return global status
        // or if globally disabled, return category as disabled
        if (locale.isEmpty() || ((Boolean) get(IS_ENABLED)).booleanValue() == false) {
            return ((Boolean) get(IS_ENABLED)).booleanValue();
        }

        // Test for localized version
        // HACK
        if (!locale.isEmpty() && m_categoryLocalizationCollection != null && m_categoryLocalizationCollection.
                localizationExists(locale)) {

            // Return value of isEnabled from localized version, so categories could be disabled depending on locale
            boolean isEnabled = m_categoryLocalizationCollection.isEnabled();
            m_categoryLocalizationCollection.rewind();
            return isEnabled;
            //return m_categoryLocalizationCollection.isEnabled();

        } else {

            // Return value of Category.getConfig().getShowInternalName()
            // This will disable all categories without selected locale, if Category.getConfig().getShowInternalName() == false
            return Category.getConfig().getShowInternalName();

        }

    }

    /**
     * Determines the current state of the category.
     *
     * @return <code>true</code> if the category is enabled; <code>false</code>
     * otherwise.
     */
    public boolean isEnabled() {
        return isEnabled(GlobalizationHelper.getNegotiatedLocale().getLanguage());
    }

    /**
     * Sets whether the category is enabled.
     *
     * @param isEnabled <code>true</code> if the category is enabled;
     * <code>false</false> otherwise.
     */
    public void setEnabled(boolean isEnabled, String locale) {

        if (!locale.isEmpty() && m_categoryLocalizationCollection != null && m_categoryLocalizationCollection.
                localizationExists(locale)) {
            m_categoryLocalizationCollection.getCategoryLocalization().
                    setEnabled(isEnabled);
        }

    }

    /**
     * Sets whether the category is enabled.
     *
     * @param isEnabled <code>true</code> if the category is enabled;
     * <code>false</false> otherwise.
     */
    public void setEnabled(boolean isEnabled) {
        set(IS_ENABLED, isEnabled);
    }

    /**
     * An abstract category cannot have any child objects, but it can have child
     * categories.
     *
     * @return <code>true</code> if the category is abstract; <code>false</code>
     * otherwise.
     */
    public boolean isAbstract() {
        return ((Boolean) get(IS_ABSTRACT)).booleanValue();
    }

    /**
     * Sets whether the category is abstract.
     *
     * @see #isAbstract()
     */
    public void setAbstract(boolean isAbstract) {
        set(IS_ABSTRACT, isAbstract);
    }

    /**
     * Determine whether this category should have a default index item
     * associated with it (i.e.it's parent category).
     *
     * @return <code>true</code> if the parent index item should be ignored (no default index item)
     * <code>false</code> otherwise.
     */
    public boolean ignoreParentIndexItem() {
        return ((Boolean) get(IGNORE_PARENT_INDEX_ITEM)).booleanValue();
    }

    /**
     * Set whether this category should have a default index item
     * associated with it (i.e.it's parent category).
     *
     * @param ignoreParentIndexItem <code>true</code> if the parent index item should be ignored (no default index item)
     * <code>false</code> otherwise
     */
    public void setIgnoreParentIndexItem(boolean ignoreParentIndexItem) {
        set(IGNORE_PARENT_INDEX_ITEM, ignoreParentIndexItem);
    }

    /**
     * @deprecated use the "use context" APIs instead
     **/
    public Collection getPurposes() {
        DataAssociationCursor purposeCur = ((DataAssociation) get(PURPOSES)).
                cursor();
        Collection purposes = new LinkedList();
        while (purposeCur.next()) {
            CategoryPurpose cp = (CategoryPurpose) DomainObjectFactory.
                    newInstance(purposeCur.getDataObject());
            purposes.add(cp);
        }
        return purposes;
    }

    /**
     * Adds the specified purpose  to this category.
     *
     * @param purpose The purpose
     * @deprecated use the "use context" APIs instead
     **/
    public void addPurpose(CategoryPurpose purpose) {
        add(PURPOSES, purpose);
    }

    /**
     * Removes the specified purpose  from this category.
     *
     * @param purpose the purpose
     * @deprecated use the "use context" APIs instead
     **/
    public void removePurpose(CategoryPurpose purpose) {
        remove(PURPOSES, purpose);
    }

    public void setDefaultAncestors(Category defaultParent) {
        String value;
        if (defaultParent == null) {
            value = getID() + "/";
        } else {
            value = (String) defaultParent.get(DEFAULT_ANCESTORS) + getID().
                    toString() + "/";
        }
        set(DEFAULT_ANCESTORS, value);
    }

    /**
     * @see com.arsdigita.domain.DomainObject#beforeSave()
     */
    @Override
    protected void beforeSave() {
        super.beforeSave();
        if (get(DEFAULT_ANCESTORS) == null) {
            setDefaultAncestors(null);
        }
    }

    /**
     * Placed a hook for a CategoryListener trigger.
     */
    @Override
    protected void beforeDelete() {
        Categorization.triggerDeletionEvent(this);
        super.beforeDelete();
    }

    /**
     * Determines whether the passed in object is a category.
     *
     * @return <code>true</code> if the passed in object is a category;
     * <code>false</code> otherwise.
     *
     * @param object the object to test
     */
    public static boolean isCategory(ACSObject object) {
        return object.getSpecificObjectType().equals(
                Category.BASE_DATA_OBJECT_TYPE);
    }

    /**
     * Cleans up all the mappings where this category is a child, and then
     * deletes the category.  This method should generally not be used. Use
     * {@link #deleteCategoryAndRemap()}, {@link #deleteCategoryAndOrphan()}, or
     * {@link #deleteCategorySubtree()} instead.
     *
     * @throws CategorizationException if the category has child objects or
     * subcategories mapped to it.  If the object has children, the programmer
     * must call {@link #deleteCategoryAndOrphan()}, {@link
     * #deleteCategorySubtree()}, or {@link #deleteCategoryAndRemap()}.
     */
    @Override
    public void delete() {
        // see if the only "children" are non-default
        DataAssociationCursor children = getRelatedCategories(CHILD);
        children.addEqualsFilter("link.isDefault", Boolean.TRUE);
        try {
            if (children.next()) {
                throw new CategorizationException("This category is the default parent of another category."
                                                  + " You must explicitly delete the child categories first. "
                                                  + "Child category: " + children.
                        getDataObject());
            }
        } finally {
            children.close();
        }

        DataAssociationCursor objects =
                              ((DataAssociation) get(CHILD_OBJECTS)).cursor();
        if (objects != null) {
            try {
                if (objects.next()) {
                    throw new CategorizationException("This category has child objects. You must delete "
                                                      + " any such objects explicitly, before deleting the "
                                                      + " category.  Child object: " + objects.
                            getDataObject());
                }
            } finally {
                objects.close();
            }

        }

        clearRelations();
        super.delete();
    }

    /**
     * This clears out all relations so that we do not have any issues with
     * mutating triggers
     */
    private void clearRelations() {
        clear(RELATED);
        clear(PARENTS);
    }

    /**
     * Deletes the category after deleting all default categories in its subtree
     *
     * <p>If the category to be deleted is not the default parent of the child
     * category, this method deletes the mapping but it does not delete that
     * section of the subtree (similar to the way a UNIX <code>rm -r</code>
     * works on symbolic links).
     */
    public void deleteCategorySubtree() {
        // we get the association between this category and its children
        DataAssociationCursor cursor =
                              ((DataAssociation) get(RELATED_CATEGORIES)).cursor();

        while (cursor.next()) {
            DataObject link = cursor.getLink();
            if (RELATED.equals(link.get(REL_TYPE))) {
                remove(RELATED_CATEGORIES, cursor.getDataObject());
            } else if (Boolean.TRUE.equals(link.get(IS_DEFAULT))) {
                (new Category(cursor.getDataObject())).deleteCategorySubtree();
            }
        }

        delete();
    }

    /**
     * Deletes the passed in category after remapping all the children.  Adds a
     * mapping from the children to the default parent of the passed in category
     * if the mapping does not already exist.  If the category that is the
     * parent of the to-be-deleted category is abstract then any objects that
     * are children of that object are not remapped to anywhere
     */
    public void deleteCategoryAndRemap() {
        Category parent;

        try {
            parent = getDefaultParentCategory();
        } catch (CategoryNotFoundException ce) {
            // no default parent, just orphan the children
            deleteCategoryAndOrphan();
            return;
        }

        if (parent.isAbstract()) {
            // this means it cannot have any child objects
            deleteCategoryAndOrphan();
            return;
        }

        DataAssociationCursor cursor =
                              ((DataAssociation) get(RELATED_CATEGORIES)).cursor();

        while (cursor.next()) {
            DataObject link = cursor.getLink();
            String relationType = (String) link.get(REL_TYPE);
            Boolean isDefault = (Boolean) link.get(IS_DEFAULT);
            remove(RELATED_CATEGORIES, cursor.getDataObject());
            if ("child".equals(relationType)) {
                Category category = new Category(cursor.getDataObject());
                parent.addChild(category);
                if (Boolean.TRUE.equals(isDefault)) {
                    category.setDefaultParentCategory(parent);
                }
            }
        }

        cursor = ((DataAssociation) get(CHILD_OBJECTS)).cursor();
        DataAssociation parentChildren = (DataAssociation) parent.get(
                CHILD_OBJECTS);
        while (cursor.next()) {
            DataObject link = cursor.getLink();
            DataObject object = cursor.getDataObject();
            DataObject newLink = parentChildren.add(object);
            if (Boolean.TRUE == link.get(IS_DEFAULT)) {
                newLink.set(IS_DEFAULT, Boolean.TRUE);
            } else {
                newLink.set(IS_DEFAULT, Boolean.FALSE);
            }
        }

        delete();
    }

    /**
     * Deletes the passed in category. Does not remap any of the children
     * categories or objects.  Instead, it just deletes all records of the
     * mappings.
     */
    public void deleteCategoryAndOrphan() {
        // We have to clear all associations in order to not have issues
        // with mutating tables when the triggers fire.
        clearRelations();
        super.delete();
    }

    /**
     * This makes the ACS object a child of the category if:
     * <ul>
     * <li> The to-be child will not cause a loop in the category hierarchy
     * <li> The to-be child is not already a child.
     * </ul>
     *
     * <p>This method cannot guarantee against loops because it cannot check
     * unsaved mappings. Therefore, it is possible for users to create a loop
     * within the structure if they have not called a save method on all added
     * mappings.</p>
     *
     * <p>This method should be used both for adding objects to a category and
     * for creating subcategories with this category instance as the parent.</p>
     *
     * <p>It is important to note that a category cannot be a CHILD and a
     * RELATED category at the same time.  If the category is RELATED and you
     * call addChild, the category is removed from the group of RELATED
     * categories and becomes a CHILD category.</p>
     *
     * @param object the domain object to categorize
     *
     * @pre !isAbstract()
     * @pre canMap()
     **/
    public void addChild(ACSObject object) {
        addMapping(object, "child");
    }

    /**
     *
     * Adds a new "related" category mapping to a category object.
     *
     * <p>This method cannot guarantee against loops because it cannot check
     * unsaved mappings. Therefore, it is possible for users to create a loop
     * within the structure if they have not called a save method on all added
     * mappings.</p>
     *
     * <p>It is important to note that a category cannot be a CHILD and a
     * RELATED category at the same time.  If the category is RELATED and you
     * call addChild, the category is removed from the group of RELATED
     * categories and becomes a CHILD category.</p>
     *
     * @param category The related category
     * @pre canMap()
     *
     **/
    public void addRelatedCategory(Category category) {
        addMapping(category, RELATED);
    }

    /**
     * Add a preferred category, which marks the current category as a synonym.
     * When a synonym category is selected, preferred category should be used instead
     * i.e. assigned to the item.
     */
    public void addPreferredCategory(Category preferred) {
        addMapping(preferred, PREFERRED);
    }

    /**
     * Adds the passed in object to the correct association.
     *
     * @pre canMap()
     */
    private void addMapping(ACSObject acsObj, String relationType) {
        if (acsObj instanceof Category) {
            addMapping((Category) acsObj, relationType);
            return;
        }
        Assert.isFalse(isAbstract(),
                       "You cannot categorize an object "
                       + "within an abstract category.  If you are "
                       + "seeing this message then your UI is "
                       + "allowing you to do something that you "
                       + "are not allowed to do and you "
                       + "should email your site administrator.");

        if (RELATED.equals(relationType)) {
            throw new CategorizationException(
                    "related relation type "
                    + "is only appropriate between two categories");
        }

        DataAssociationCursor cursor =
                              ((DataAssociation) get(CHILD_OBJECTS)).cursor();
        cursor.addEqualsFilter(ID, acsObj.getID());
        if (cursor.size() == 0) {
            // if the cursor.size() > 0 then the object is already
            // a child and does not need to be added again.
            add(CHILD_OBJECTS, acsObj);
            Categorization.triggerMapEvent(this, acsObj);
            if (s_log.isDebugEnabled()) {
                s_log.debug(acsObj + " added to " + CHILD_OBJECTS + " of catID="
                            + getID() + " type=" + relationType + " (ignored)");
            }
        } else {
            if (s_log.isDebugEnabled()) {
                s_log.debug(acsObj + " is already related to catID=" + getID()
                            + " type=" + relationType + " (ignored)");
            }
        }
    }

    private void addMapping(Category category, String relationType) {
        // Let's check for loops.
        if ("child".equals(relationType) && category.isMemberOfSubtree(this)) {
            throw new CategorizationException("The object that you are "
                                              + "trying to add as a child is already "
                                              + "a member of the subtree.");
        }


        // if the item is already mapped then we just update the relation type.
        // Otherwise, we add and then update.

        DataAssociationCursor cursor =
                              ((DataAssociation) get(RELATED_CATEGORIES)).cursor();
        cursor.addEqualsFilter(ID, category.getID());
        DataObject link;
        if (cursor.next()) {
            link = cursor.getLink();
            if (s_log.isDebugEnabled()) {
                s_log.debug("existing related catID=" + category.getID() + " " + link.
                        get(REL_TYPE) + " default=" + link.get(IS_DEFAULT));
            }
        } else {
            link = add(RELATED_CATEGORIES, category);
            if (s_log.isDebugEnabled()) {
                s_log.debug("adding new related catID=" + category.getID());
            }
            Categorization.triggerAddChildEvent(this, category);
        }
        cursor.close();
        if (s_log.isDebugEnabled()) {
            s_log.debug("updating related catID=" + category.getID() + " type="
                        + relationType + " default=false");
        }

        link.set(REL_TYPE, relationType);
        link.set(IS_DEFAULT, Boolean.FALSE);
    }

    /**
     * Removes a category mapping from the domain object so that an ACS object
     * is no longer a child of a category.  This actually deletes the mapping.
     *
     * @param acsObj the domain object to uncategorize
     * @pre canMap()
     * @throws NullPointerException if <code>acsObj</code> is null
     **/
    public void removeChild(ACSObject acsObj) {
        if (acsObj == null) {
            throw new NullPointerException("acsObj");
        }
        if (acsObj instanceof Category) {
            removeChild((Category) acsObj);
        } else {
            remove(CHILD_OBJECTS, acsObj);
            Categorization.triggerUnmapEvent(this, acsObj);
        }
    }

    /**
     * Removes the specified child category.
     *
     * @throws NullPointerException if category is null
     **/
    public void removeChild(Category category) {
        Assert.exists(category, Category.class);

        try {
            if (equals(category.getDefaultParentCategory())) {
                PermissionService.setContext(category, null);
                category.setDefaultAncestors(null);
            }
        } catch (CategoryNotFoundException e) {
            // leave the context alone and thus we want to do nothing
        }
        remove(RELATED, category);
        Categorization.triggerRemoveChildEvent(this, category);
    }

    /**
     * This takes a category and removes its relation to this category.  If the
     * passed in category is also a child then it is still a child after this
     * call.
     *
     * @pre canMap()
     */
    public void removeRelatedCategory(Category category) {
        removeChild(category);
    }

    /**
     * Sets the index object for this category.
     *
     * @pre object is assigned to this category
     * @param object The object to set as Index.
     */
    public void setIndexObject(ACSObject object) {
        DataAssociationCursor items = ((DataAssociation) get(CHILD_OBJECTS)).
                cursor();
        while (items.next()) {
            DataObject obj = items.getDataObject();
            DataObject link = items.getLink();
            if (object != null && object.getOID().equals(obj.getOID())) {
                link.set(IS_INDEX, Boolean.TRUE);
            } else if (Boolean.TRUE.equals(link.get(IS_INDEX))) {
                link.set(IS_INDEX, Boolean.FALSE);
            }
        }
    }

    /**
     * Retrieves the index object for this category. Retrieves the index object
     * for the parent category if there is no explicit index object for this
     * category or null if this is the root category.
     *
     * @return The Index Object
     */
    public ACSObject getIndexObject() {
        ACSObject item = getDirectIndexObject();

        if (item == null) {
            if (!ignoreParentIndexItem()) {
                try {
                    item = getDefaultParentCategory().getIndexObject();
                } catch (CategoryNotFoundException ex) {
                    // this will rarely happen and when it does we just want to
                    // return null which is done below.  it would be much nicer if
                    // the Categorization API just returned null instead of throwing
                    // the exception
                    s_log.debug("not found", ex);
                }
            }
        }

        return item;
    }

    /**
     * This returns the index item directly mapped to this category and null if
     * it does not have one.
     *
     * @return The Index Object
     */
    public ACSObject getDirectIndexObject() {
        DataAssociationCursor items = ((DataAssociation) get(CHILD_OBJECTS)).
                cursor();
        items.addEqualsFilter("link.isIndex", Boolean.TRUE);
        if (items.next()) {
            DataObject dobj = items.getDataObject();
            items.close();
            return (ACSObject) DomainObjectFactory.newInstance(dobj);
        }
        items.close();
        return null;
    }

    /**
     * Determines whether the category is a leaf (does not have any child
     * objects or child categories).
     *
     * @return <code>true</code> if the category is a leaf; <code>false</code>
     * otherwise.
     */
    public boolean isLeaf() {
        return !hasChildCategories() && !hasChildObjects();
    }

    /**
     * Determines whether the category has child objects.
     *
     * @return <code>true</code> if the category does not have any child objects;
     * <code>false</code> otherwise.
     */
    public boolean hasChildObjects() {
        return (getNumberOfChildObjects() != 0);
    }

    /**
     * Determines whether the category has any child categories.
     *
     * @return <code>true</code> if the category does not have any child
     * categories; <code>false</code> otherwise.
     */
    public boolean hasChildCategories() {
        return (getNumberOfChildCategories() != 0);
    }

    /**
     * Determines whether the category is a root.
     *
     * @return <code>true</code> if the category does not have
     * any parents; <code>false</code> otherwise.
     */
    public boolean isRoot() {
        return (getParentCategoryCount() == 0);
    }

    /**
     *  Returns the number of parent categories for this category.
     *
     *  @return the number of times has this category has been
     *  mapped to a new category.
     */
    public long getParentCategoryCount() {
        DataAssociationCursor cursor =
                              ((DataAssociation) get(PARENTS)).cursor();
        try {
            return cursor.size();
        } finally {
            cursor.close();
        }
    }

    /**
     *  Sets this category's default parent category to the one that is passed
     *  in.
     *
     *  <p><b>This clears the previous default mapping.</b> The results will be
     *  saved for the user when the transaction is committed. </p>
     *
     *  <p>If the passed in parent is not already a parent then this makes the
     *  object a parent. </p>
     *
     *  @param parent the category that will become the new
     *             default category.  Pass in null to clear the current
     *             default parent.
     *  @exception CategoryNotFoundException if the passed in category is
     *          not a parent of this category.
     *
     */
    public void setDefaultParentCategory(Category parent) {
        // update the permissions context
        PermissionService.setContext(this, parent);

        boolean found = false;
        DataAssociationCursor cursor =
                              ((DataAssociation) get(PARENTS)).cursor();
        while (cursor.next()) {
            DataObject category = cursor.getDataObject();
            DataObject link = cursor.getLink();

            if (Boolean.TRUE.equals((Boolean) link.get(IS_DEFAULT))) {
                link.set(IS_DEFAULT, Boolean.FALSE);
            } else if (parent.getID().equals(category.get("id"))) {
                link.set(IS_DEFAULT, Boolean.TRUE);
                found = true;
            }
        }
        if (!found && parent != null) {
            DataObject link = add(PARENTS, parent);
            link.set(IS_DEFAULT, Boolean.TRUE);
            link.set(REL_TYPE, CHILD);
        }
        setDefaultAncestors(parent);
        Categorization.triggerSetDefaultParentEvent(this, parent);
    }

    /**
     * This takes the child and swaps it with the next child in the sort order.
     * For instance, if the pass in child is 5th in the list then after the
     * call, it will be 6th and the 6th item will become the 5th item.
     *
     * @see #swapWithPrevious(ACSObject)
     */
    public void swapWithNext(ACSObject child) {
        s_log.debug("swapWithNext: " + child.getOID());
        if (isCategory(child)) {
            swapWithNextCategory((Category) child);
            return;
        }

        DataAssociationCursor cursor =
                              ((DataAssociation) get(CHILD_OBJECTS)).cursor();

        cursor.addEqualsFilter(ID, child.getID());
        if (cursor.next()) {
            DataObject link = cursor.getLink();
            int key = ((BigDecimal) link.get(SORT_KEY)).intValue();
            int previousKey = key + 1;
            // key+1 is a good default but we need to check and make sure
            DataQuery query = getSession().retrieveQuery(
                    "com.arsdigita.categorization.minObjectCategorySortKey");

            query.setParameter(CATEGORY_ID, getID());
            query.setParameter(CURRENT_SORT_KEY, new Integer(key));

            if (query.next()) {
                previousKey = ((BigDecimal) query.get(SORT_KEY)).intValue();
            }
            query.close();

            swapObjectKeys(previousKey, key);
        }
        cursor.close();
    }

    private void swapWithNextCategory(Category child) {
        DataAssociationCursor cursor = getRelatedCategories(CHILD);
        cursor.addEqualsFilter("id", child.getID());

        if (cursor.next()) {
            DataObject link = cursor.getLink();
            int key = ((BigDecimal) link.get(SORT_KEY)).intValue();
            int previousKey = key + 1;
            // key+1 is a good default but we need to check and make sure
            DataQuery query = getSession().retrieveQuery(
                    "com.arsdigita.categorization.minCategoryCategorySortKey");

            query.setParameter(CATEGORY_ID, getID());
            query.setParameter(CURRENT_SORT_KEY, new Integer(key));

            if (query.next()) {
                previousKey = ((BigDecimal) query.get(SORT_KEY)).intValue();
            }
            query.close();

            swapCategoryKeys(previousKey, key);
        }
        cursor.close();
    }

    /**
     * Swaps the order of the child object with the next child object
     * categorized in the category.  So, if the original order is "A, B, C" and
     * you called B.swapWithPrevious(), the new order would be "B, A, B".  If
     * this is called on the first item of the list (e.g. "A") then nothing
     * happens.  If no sort key has been assigned yet then this does not alter
     * anything.
     *
     * @see #swapWithNext(ACSObject)
     */
    public void swapWithPrevious(ACSObject child) {
        if (isCategory(child)) {
            swapWithPreviousCategory((Category) child);
            return;
        }

        DataAssociationCursor cursor =
                              ((DataAssociation) get(CHILD_OBJECTS)).cursor();

        cursor.addEqualsFilter(ID, child.getID());
        if (cursor.next()) {
            DataObject link = cursor.getLink();
            int key = ((BigDecimal) link.get(SORT_KEY)).intValue();
            int previousKey = key - 1;
            // key-1 is a good default but we need to check and make sure
            DataQuery query = getSession().retrieveQuery(
                    "com.arsdigita.categorization.maxObjectCategorySortKey");

            query.setParameter(CATEGORY_ID, getID());
            query.setParameter(CURRENT_SORT_KEY, new Integer(key));

            if (query.next()) {
                previousKey = ((BigDecimal) query.get(SORT_KEY)).intValue();

            }
            query.close();

            swapObjectKeys(previousKey, key);
        }
        cursor.close();
    }

    private void swapWithPreviousCategory(Category child) {
        DataAssociationCursor cursor = getRelatedCategories(CHILD);
        cursor.addEqualsFilter(ID, child.getID());

        if (cursor.next()) {
            DataObject link = cursor.getLink();
            int key = ((BigDecimal) link.get(SORT_KEY)).intValue();
            int previousKey = key - 1;
            // key-1 is a good default but we need to check and make sure
            DataQuery query = getSession().retrieveQuery(
                    "com.arsdigita.categorization.maxCategoryCategorySortKey");

            query.setParameter(CATEGORY_ID, getID());
            query.setParameter(CURRENT_SORT_KEY, new Integer(key));

            if (query.next()) {
                previousKey = ((BigDecimal) query.get(SORT_KEY)).intValue();
                query.close();
            }

            swapCategoryKeys(previousKey, key);
        }
        cursor.close();
    }

    /**
     * Swaps the keys for the categories.
     */
    private void swapCategoryKeys(int key, int nextKey) {
        swapKeys(getSession().retrieveDataOperation(
                "com.arsdigita.categorization.swapCategoryWithNextCategory"),
                 key, nextKey);
    }

    /**
     * Swaps the keys for the objects.
     */
    private void swapObjectKeys(int key, int nextKey) {
        swapKeys(getSession().retrieveDataOperation(
                "com.arsdigita.categorization.swapObjectWithNextObject"),
                 key, nextKey);
    }

    /**
     * Does the actual swapping of keys
     */
    private void swapKeys(DataOperation operation, int key, int nextKey) {
        operation.setParameter(SORT_KEY, new BigDecimal(key));
        operation.setParameter("nextSortKey", new BigDecimal(nextKey));
        operation.setParameter("parentID", getID());
        operation.execute();
    }

    /**
     * Alphabetizes the child categories so that they will always be displayed
     * in alphabetical order.
     */
    public void alphabetizeChildCategories() {
        DataAssociationCursor cursor = getRelatedCategories(CHILD);
        cursor.addOrder("lower(" + NAME + ")");

        int count = 0;
        while (cursor.next()) {
            DataObject link = cursor.getLink();
            link.set(SORT_KEY, new BigDecimal(count));
            count++;
        }
    }

    /**
     * Explicitly sets the sort key for this child object (category or
     * otherwise).
     *
     * @param child The child object or category to set the sortKey for
     * @param key The integer to use for the sortKey
     */
    public void setSortKey(ACSObject child, int key) {
        if (isCategory(child)) {
            setSortKey((Category) child, key);
            return;
        }

        DataAssociationCursor cursor =
                              ((DataAssociation) get(CHILD_OBJECTS)).cursor();

        cursor.addEqualsFilter(ID, child.getID());
        if (cursor.next()) {
            DataObject link = cursor.getLink();
            link.set(SORT_KEY, new BigDecimal(key));
        }
        cursor.close();
    }

    /**
     * Explicitly swaps the sort keys for two child objects
     *
     * @param child The child object or category to set the sortKey for
     * @param key The integer to use for the sortKey
     */
    public void swapSortKeys(BigDecimal childID1, BigDecimal childID2) {

        if (childID1 != null && childID2 != null) {

            DataObject link1 = null;
            DataObject link2 = null;
            BigDecimal key1;
            BigDecimal key2;

            DataAssociationCursor cursor =
                                  ((DataAssociation) get(CHILD_OBJECTS)).cursor();
            cursor.addEqualsFilter(ID, childID1);
            if (cursor.next()) {
                link1 = cursor.getLink();
            }
            cursor.close();

            cursor = ((DataAssociation) get(CHILD_OBJECTS)).cursor();
            cursor.addEqualsFilter(ID, childID2);
            if (cursor.next()) {
                link2 = cursor.getLink();
            }
            cursor.close();

            if (link1 != null && link2 != null) {
                key1 = (BigDecimal) link1.get(SORT_KEY);
                key2 = (BigDecimal) link2.get(SORT_KEY);
                link1.set(SORT_KEY, key2);
                link2.set(SORT_KEY, key1);
            }
        }

    }

    private void setSortKey(Category child, int key) {
        DataAssociationCursor cursor = getRelatedCategories(CHILD);

        cursor.addEqualsFilter(ID, child.getID());
        if (cursor.next()) {
            DataObject link = cursor.getLink();
            link.set(SORT_KEY, new BigDecimal(key));
        }
        cursor.close();
    }

    /**
     * Returns the Cursor for the related categories so that the caller can
     * filter, if desired.
     *
     * @param relation The type of relation to retrieve.
     * @pre relation == Category.CHILD || relation == Category.RELATED || relation == Category.PREFERRED
     */
    public DataAssociationCursor getRelatedCategories(String relation) {
        Assert.isTrue(relation.equals(CHILD) || relation.equals(RELATED)
                      || relation.equals(PREFERRED),
                      " invalid relation {" + relation + "}");
        DataAssociationCursor cursor =
                              ((DataAssociation) get(RELATED_CATEGORIES)).cursor();
        cursor.addEqualsFilter("link.relationType", relation);
        return cursor;
    }

    /**
     * Returns the number of child categories for this category.  This is more
     * efficient than retreiving the collection of categories and calling
     * <code>myCollection.size()</code>.
     *
     * @return a <code>long</code> indicating the number of child categories.
     */
    public long getNumberOfChildCategories() {
        DataAssociationCursor cursor = getRelatedCategories(CHILD);
        return cursor.size();
    }

    /**
     * Retrieves a collection of domain objects of the specified type that are
     * immediate children of this category.
     *
     * @see #getDescendants()
     * @throws NullPointerException if objectType is null
     **/
    public CategoryCollection getChildren() {
        return new CategoryCollection(getRelatedCategories(CHILD));
    }

    /**
     * A convinience wrapper for {@link #getObjects(String, String)
     * getObjects(objectType, null)}.
     *
     * @see #getObjects(String, String)
     * @throws NullPointerException if objectType is null
     **/
    public CategorizedCollection getObjects(String objectType) {
        return getObjects(objectType, null);
    }

    /**
     * Returns a collection of the child objects of this category.
     *
     * @param objectType the return collection will only contain objects of this
     * type
     * @param path the fragment of the PDL join path leading from the specified
     * object type to the <code>Category</code> object type.  The common case
     * for CMS is to pass <code>"parent"</code> as the value of
     * <code>path</code>.  The common scenario for many other applications is to
     * pass <code>null</code>, in which case {@link #getObjects(String)} can be
     * used.
     *
     * @pre objectType!=null && path!=null
     * @post return != null
     * @throws NullPointerException if either parameter is null
     **/
    public CategorizedCollection getObjects(String objectType, String path) {
        if (objectType == null) {
            throw new NullPointerException("objectType");
        }

        String sortPath = CATEGORIES + ".link." + SORT_KEY;
        if (path != null) {
            sortPath = path + "." + sortPath;
        }
        final CategorizedCollection result = new CategorizedCollection(getSession().
                retrieve(objectType), sortPath);

        result.addEqualsFilter(extendPath(path), getID());
        return result;
    }

    private static String extendPath(String path) {
        final String pathExtension = CATEGORIES + "." + ID;
        if (path == null) {
            return pathExtension;
        }

        StringBuilder sb =
                     new StringBuilder(path.length() + pathExtension.length() + 1);
        sb.append(path).append(".").append(pathExtension);
        return sb.toString();
    }

    /**
     * Returns the number of child objects for this category.  This is more
     * efficient than retreiving the collection of objects and calling
     * <code>myCollection.size()</code>.
     *
     * @return a <code>long</code> indicating the number of child objects.
     */
    public long getNumberOfChildObjects() {
        DataAssociationCursor association =
                              ((DataAssociation) get(CHILD_OBJECTS)).cursor();
        if (association == null) {
            return 0;
        } else {
            return association.size();
        }
    }

    /**
     * Returns the parent categories for this category (the categories under
     * which this category is categorized).
     **/
    public CategoryCollection getParents() {
        return new CategoryCollection(((DataAssociation) get(PARENTS)).cursor());
    }

    /**
     * Returns the default parent category.  Note that this category may not be
     * enabled.
     *
     * @return the default parent category.
     */
    public Category getDefaultParentCategory() {
        DataAssociationCursor cursor =
                              ((DataAssociation) get(PARENTS)).cursor();

        cursor.addEqualsFilter("link.isDefault", Boolean.TRUE);
        try {
            if (cursor.next()) {
                return new Category(cursor.getDataObject());
            }
        } finally {
            cursor.close();
        }

        throw new CategoryNotFoundException("The Category " + this + " does "
                                            + "not have a default parent");
    }

    /**
     * Determines whether the ACS object is in the subtree with the specified
     * category as the root.  This works for both categories and regular ACS
     * objects.  If the object, the category, or some mapping between the object
     * and category has not been saved, this method does not guarantee a correct
     * result.
     *
     * @param acsObject the item to search for
     * @return <code>true</code> if the object appears mapped somewhere in
     *         the subtree; <code>false</code> otherwise.
     * @pre acsObject != null
     */
    public boolean isMemberOfSubtree(ACSObject acsObject) {
        // before searching, let's make sure that both the child and parent have
        // not null IDs
        if (acsObject.getOID() == null || getOID() == null) {
            return false;
        }
        if (isCategory(acsObject)) {
            if (this.equals(acsObject)) {
                // the child is the same as the parent so by default it is part
                // of the subtree
                return true;
            } else {
                DataQuery query = getSession().retrieveQuery(
                        "com.arsdigita.categorization.categorySubtree");
                query.setParameter(ID, getID());
                query.addEqualsFilter("categorySubtree.id", acsObject.getID());
                return (query.size() > 0);
            }
        }

        // This means it is an ACSObject
        DataQuery query = getSession().retrieveQuery(
                "com.arsdigita.categorization.objectsInSubtree");
        query.setParameter(CATEGORY_ID, getID());
        query.addEqualsFilter("object.id", acsObject.getID());
        return (query.size() > 0);
    }

    /**
     * Returns a collection of default progenitors of this category, up to and
     * including the root category.
     */
    public CategoryCollection getDefaultAscendants() {
        DataCollection collection =
                       getSession().retrieve(BASE_DATA_OBJECT_TYPE);

        String ids = (String) get(DEFAULT_ANCESTORS);
        if (ids == null) {
            throw new IllegalStateException("null default ancestors for " + this);
        }

        collection.addFilter(DEFAULT_ANCESTORS + " in :ancestors").
                set("ancestors", subpaths(ids));

        return new CategoryCollection(collection);
    }

    private static List subpaths(String path) {
        List result = new LinkedList();
        if (path == null || "".equals(path)) {
            return result;
        }

        final String delim = "/";

        if (!path.endsWith(delim)) {
            throw new IllegalArgumentException("doesn't end with /: " + path);
        }

        StringTokenizer st = new StringTokenizer(path, delim);
        StringBuilder subpath = new StringBuilder();

        while (st.hasMoreTokens()) {
            subpath.append(st.nextToken()).append(delim);
            result.add(subpath.toString());
        }
        return result;
    }

    /**
     * Returns a collection of all the categories in this category's subtree.
     * This category is a member of the collection.
     *
     * @see #getChildren()
     */
    public CategoryCollection getDescendants() {
        final DataQuery dq = getSession().retrieveQuery(
                "com.arsdigita.categorization.categorySubtree");
        dq.setParameter(ID, getID());
        return new CategoryCollection(
                new DataQueryDataCollectionAdapter(dq, "categorySubtree"));
    }

    /**
     * Returns a collection of objects that are children of this category or its
     * descendant categories.
     *
     * @see #getDescendants()
     */
    public CategorizedCollection getDescendantObjects() {
        return getDescendantObjects(ACSObject.BASE_DATA_OBJECT_TYPE,
                                    "categories.roTransParents");
    }

    private static String appendID(String path) {
        StringBuilder sb = new StringBuilder(path.length() + 3);
        sb.append(path).append(".").append(ID);
        return sb.toString();
    }

    public CategorizedCollection getDescendantObjects(String objectType,
                                                      String path) {
        s_log.info("retrieving objectType=" + objectType + "; path=" + path);
        final CategorizedCollection result = new CategorizedCollection(getSession().
                retrieve(objectType));

        result.addEqualsFilter(appendID(path), getID());
        return result;
    }

    /**
     * Given a relative path, returns the list of constituent categories of the
     * path. The array returned will have the current category as its first
     * element, and the destination category as its final element.
     *
     * @param path A string representing the relative path.
     *
     * @return Array of constituent categories. The first element of the array
     * is the current category (hence the array will always have length >=
     * 1). If the path is bad, this returns <code>null</code>.
     *
     * This one may be patched to work with localized URLs. I didn't do it for now
     * because I don't know where it is called and if it's really needed to patch.
     * Quasimodo
     *
     */
    public Category[] getChildrenByURL(String path) {
        final List children = new LinkedList();
        final TokenizedPath urlParts = new TokenizedPath(path);
        Category current = this;
        children.add(current);

        while (urlParts.next()) {
            CategoryCollection cats = current.getChildren();
            cats.addEqualsFilter(URL, urlParts.getToken());

            if (cats.next()) {
                current = cats.getCategory();
                children.add(current);
                cats.close();
            } else {
                return null;
            }
        }

        return (Category[]) children.toArray(new Category[0]);
    }

    static class TokenizedPath {

        private StringTokenizer m_strTok;
        private String m_token;

        public TokenizedPath(String path) {
            m_strTok = new StringTokenizer(path, "/");
        }

        public boolean next() {
            if (!m_strTok.hasMoreTokens()) {
                return false;
            }
            m_token = m_strTok.nextToken();
            if ("".equals(m_token)) {
                m_token = null;
                return next();
            }
            return true;
        }

        public String getToken() {
            return m_token;
        }
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder(128);
        result.append("name=").append(getName()).append("; ");
        result.append("oid=").append(getOID());
        return result.toString();
    }

    private static DataCollection getRootCategoriesAssoc(ACSObject acsObj) {
        if (acsObj == null) {
            throw new NullPointerException("acsObj");
        }

        DataCollection dc = SessionManager.getSession().retrieve(
                "com.arsdigita.categorization.UseContext");
        dc.addFilter("categoryOwner.id = :ownerID").set("ownerID",
                                                        acsObj.getID());
        return dc;
    }

    /**
     * A shortcut for {@link #getRootForObject(ACSObject, String)} called with
     * the null context.
     *
     * @see #getRootForObject(ACSObject, String)
     * @return the root category, or null if no category is associated
     */
    public static Category getRootForObject(ACSObject object) {
        return getRootForObject(object, null);
    }

    /**
     * Returns a collection of root categories to which this object is mapped.
     **/
    public static RootCategoryCollection getRootCategories(ACSObject acsObj) {
        return new RootCategoryCollection(getRootCategoriesAssoc(acsObj));
    }

    /**
     * Retrieves the root category associated with an object in the given use
     * context.
     **/
    public static Category getRootForObject(ACSObject object, String context) {
        DataCollection cats = getRootCategoriesAssoc(object);

        cats.addEqualsFilter(USE_CONTEXT, context);
        DataObject triple = null;
        try {
            if (cats.next()) {
                triple = cats.getDataObject();
                return (Category) DomainObjectFactory.newInstance((DataObject) triple.
                        get(ROOT_CATEGORY));
            } else {
                s_log.debug("no triples, returning null");
                return null;
            }
        } finally {
            if (cats.next()) {
                DataObject secondRoot = cats.getDataObject();
                cats.close();
                throw new IllegalStateException("there is more than one root for object:\n"
                                                + object + "\nfirst root: "
                                                + triple + "\nsecond root: "
                                                + secondRoot);
            }
            cats.close();
        }
    }

    /**
     * A shortcut for calling {@link #setRootForObject(ACSObject, Category,
     * String)} with the null context.
     *
     * @see #setRootForObject(ACSObject, Category, String)
     * @param object the object to own the root category
     * @param root the root category for the object
     */
    public static void setRootForObject(ACSObject acsObj, Category root) {
        setRootForObject(acsObj, root, null);
    }

    /**
     * Sets the root category for the supplied object in the given context.
     *
     * <p>Conceptually, this method provides a means to add/remove elements
     * to/from a set of triples of the form (<em>acsObj</em>, <em>rootCat</em>,
     * <em>useContext</em>).  No two triples have the same (<em>acsObj</em>,
     * <em>rootCat</em>) pair. </p>
     *
     * <p>If <em>acsObj</em> is already mapped to some other category in the
     * specified use context, then the existing triple's category value is
     * updated to <code>rootCat</code>.  Otherwise, a new triple is added.</p>
     *
     * <p>In theory, <code>rootCat</code> should be a root category, i.e. it
     * should have no parents.  This method does not check if this is indeed the
     * case.</p>
     *
     * @see #setRootForObject(ACSObject, Category, String)
     * @param object the object to own the root category
     * @param root the root category for the object
     */
    public static void setRootForObject(ACSObject acsObj, Category rootCat,
                                        String context) {

        DataCollection rootCats = getRootCategoriesAssoc(acsObj);
        rootCats.addEqualsFilter(USE_CONTEXT, context);
        if (rootCats.next()) {
            DataObject triple = rootCats.getDataObject();
            triple.set(ROOT_CATEGORY,
                       DomainServiceInterfaceExposer.getDataObject(rootCat));
            rootCats.close();
            return;
        }
        rootCats.close();
        s_log.debug("did not find root, creating a new one");
        // acsObj is not mapped to any category in the specified context yet.

        DataObject triple = SessionManager.getSession().create(
                "com.arsdigita.categorization.UseContext");

        try {
            triple.set("id", Sequences.getNextValue());
        } catch (SQLException ex) {
            throw new UncheckedWrapperException(ex);
        }
        triple.set(CATEGORY_OWNER,
                   DomainServiceInterfaceExposer.getDataObject(acsObj));
        triple.set(ROOT_CATEGORY,
                   DomainServiceInterfaceExposer.getDataObject(rootCat));
        triple.set(USE_CONTEXT, context);
    }

    private static boolean equal(String str1, String str2) {
        if (str1 == null && str2 == null) {
            return true;
        }

        if (str1 == null) {
            return equal(str2, str1);
        }

        return str1.equals(str2);
    }

    /**
     * Calls {@link #clearRootForObject(ACSObject, String)} with the null context.
     *
     * @see #clearRootForObject(ACSObject, String)
     **/
    public static void clearRootForObject(ACSObject object) {
        clearRootForObject(object, null);
    }

    /**
     * Removes any root category associated with the object in the specified context.
     *
     * @param object the object to remove the root category from
     */
    public static void clearRootForObject(ACSObject object, String context) {
        DataCollection dc = getRootCategoriesAssoc(object);
        dc.addEqualsFilter(USE_CONTEXT, context);
        if (dc.next()) {
            dc.getDataObject().delete();
        }
        dc.close();
    }

    /**
     * Returns a boolean indicating if the current user can edit the properties
     * of the category.  This does not include mapping child objects.  For
     * mapping, use canMap.
     */
    public boolean canEdit() {
        return PermissionService.checkPermission(new PermissionDescriptor(
                PrivilegeDescriptor.EDIT, this,
                                                                          Kernel.
                getContext().getParty()));
    }

    /**
     * Returns a boolean indicating if the current user can delete this
     * category.
     */
    public boolean canDelete() {
        return PermissionService.checkPermission(new PermissionDescriptor(
                PrivilegeDescriptor.DELETE, this,
                                                                          Kernel.
                getContext().getParty()));
    }

    /**
     * Returns a boolean indicating if the current user can map a child object
     * to this category.  This must return true in order to call addChild or
     * removeChild.
     */
    public boolean canMap() {
        return PermissionService.checkPermission(new PermissionDescriptor(
                MAP_DESCRIPTOR, this,
                                                                          Kernel.
                getContext().getParty()));
    }

    /**
     * Returns a boolean indicating if the current user has the permission to
     * read properties of this category.
     */
    public boolean canRead() {
        return PermissionService.checkPermission(new PermissionDescriptor(
                PrivilegeDescriptor.READ, this,
                                                                          Kernel.
                getContext().getParty()));
    }

    /**
     * This returns a boolean indicating if the current user has the permission
     * to change the permissions on this category.  If canAdmin == true then all
     * of the other canXXX also are true
     */
    public boolean canAdmin() {
        return PermissionService.checkPermission(new PermissionDescriptor(
                PrivilegeDescriptor.ADMIN, this,
                                                                          Kernel.
                getContext().getParty()));
    }

    // Quasimodo: Begin
    public CategoryLocalizationCollection getCategoryLocalizationCollection() {
        return m_categoryLocalizationCollection;
    }

    public DataAssociation getLocalizations() {
        return ((DataAssociation) this.get(LOCALIZATIONS));
    }

    public boolean hasLocalizations() {
        return !m_categoryLocalizationCollection.isEmpty();
    }

    /**
     * Add a new language set to this category
     */
    public boolean addLanguage(String locale, String name, String description,
                               String url) {

        // If locale don't exist
        if (!locale.isEmpty() && m_categoryLocalizationCollection != null && !m_categoryLocalizationCollection.
                localizationExists(locale)) {

            // Get DataAssociation
            DataAssociation categoryLocalizationAssociation = this.
                    getLocalizations();

            // Add association with category
            (new CategoryLocalization(locale, name, description, url)).
                    addToAssociation(categoryLocalizationAssociation);

            // Reload CategoryLocalizationCollection
//            this.m_categoryLocalizationCollection = new CategoryLocalizationCollection(this);

            return true;

        }

        return false;

    }

    /**
     * Delete a language set from this category
     */
    public boolean delLanguage(String locale) {

        // If locale exist
        if (!locale.isEmpty() && m_categoryLocalizationCollection != null && m_categoryLocalizationCollection.
                localizationExists(locale)) {

            // Get DataAssociation
            DataAssociation categoryLocalizationAssociation = this.
                    getLocalizations();

            // Remove CategoryLocalization from Association
            m_categoryLocalizationCollection.getCategoryLocalization().
                    removeFromAssociation(categoryLocalizationAssociation);

            // Reload CategoryLocalizationCollection
            this.m_categoryLocalizationCollection = new CategoryLocalizationCollection(
                    this);

            return true;

        }

        return false;
    }
}
