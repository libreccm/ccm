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
package com.arsdigita.cms;

import com.arsdigita.categorization.Category;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.Assert;

import java.math.BigDecimal;

/**
 * Represents a mapping from (category + content type) to a template.
 *
 * @version $Id: CategoryTemplateMapping.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class CategoryTemplateMapping extends TemplateMapping {

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.cms.CategoryTemplateMapping";

    public static final String CATEGORY   = "category";
    public static final String SECTION   = "contentSection";
    public static final String CONTENT_TYPE = "contentType";

    /**
     * Default constructor. The contained <code>DataObject</code> is
     * initialized with a new <code>DataObject</code> with an
     * <code>ObjectType</code> of "CategoryTemplateMapping".
     *
     * @see com.arsdigita.cms.CategoryTemplateMapping#CategoryTemplateMapping(String)
     * @see com.arsdigita.persistence.metadata.ObjectType
     **/
    public CategoryTemplateMapping() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    /**
     * Constructor in which the contained <code>DataObject</code> is retrieved
     * from the persistent storage mechanism using the
     * specified OID.
     *
     * @param oid the OID for the retrieved
     * <code>DataObject</code>
     *
     * @see com.arsdigita.domain.ObservableDomainObject#ObservableDomainObject(OID)
     * @see com.arsdigita.persistence.DataObject
     * @see com.arsdigita.persistence.OID
     **/
    public CategoryTemplateMapping(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    /**
     * Constructor in which the contained <code>DataObject</code> is retrieved
     * from the persistent storage mechanism using the
     * specified BigDecimal ID and <code>ObjectType</code> of "CategoryTemplateMapping".
     *
     * @param id the ID for the retrieved
     * <code>DataObject</code>
     *
     * @see com.arsdigita.persistence.DataObject
     * @see com.arsdigita.persistence.OID
     **/
    public CategoryTemplateMapping(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    /**
     * Constructor in which a CategoryTemplateMapping is created using
     * passed-in  <code>DataObject</code>.
     *
     * @param obj the <code>DataObject</code> to initialize this CategoryTemplateMapping
     *
     * @see com.arsdigita.domain.ObservableDomainObject#ObservableDomainObject(DataObject)
     * @see com.arsdigita.persistence.DataObject
     * @see com.arsdigita.persistence.OID
     **/
    public CategoryTemplateMapping(DataObject obj) {
        super(obj);
    }

    /**
     * Constructor in which the contained <code>DataObject</code> is
     * initialized with a new <code>DataObject</code> with an
     * <code>ObjectType</code> specified by the string
     * <i>type</i>.
     *
     * @param type the name of the <code>ObjectType</code> of the
     * contained <code>DataObject</code>
     *
     * @see com.arsdigita.domain.ObservableDomainObject#ObservableDomainObject(String)
     * @see com.arsdigita.persistence.DataObject
     * @see com.arsdigita.persistence.metadata.ObjectType
     **/
    public CategoryTemplateMapping(String type) {
        super(type);
    }

    /**
     * Returns the <code>ContentSection</code> for this
     * <code>CategoryTemplateMapping</code>
     *
     * @return The ContentSection for which this mapping is valid.
     **/
    public final ContentSection getContentSection() {
        return (ContentSection)DomainObjectFactory.newInstance
            ((DataObject) get(SECTION));
    }

    /**
     * Sets the <code>ContentSection</code> for this
     * <code>CategoryTemplateMapping</code>
     *
     * @param sec The ContentSection for which this mapping should be valid.
     **/
    public final void setContentSection(ContentSection sec) {
        Assert.assertNotNull(sec);
        setAssociation(SECTION, sec);
    }

    /**
     * Returns the <code>Category</code> for this
     * <code>CategoryTemplateMapping</code>
     *
     * @return The Category for which this mapping is valid.
     **/
    public Category getCategory() {
        return (Category)DomainObjectFactory.newInstance
            ((DataObject) get(CATEGORY));
    }

    /**
     * Sets the <code>Category</code> for this
     * <code>CategoryTemplateMapping</code>
     *
     * @param cat The Category for which this mapping should be valid.
     **/
    public void setCategory(Category cat) {
        Assert.assertNotNull(cat);
        setAssociation(CATEGORY, cat);
    }

    /**
     * Return the parent of the template within the category.
     * Currently this returns the ContentType
     *
     * @return the parent of this TemplateMapping
     */
    public ACSObject getParent() {
        // XXX parent category
        return getContentType();
    }

    /**
     * Returns the <code>ContentType</code> for this
     * <code>CategoryTemplateMapping</code>
     *
     * @return The ContentType for which this mapping is valid.
     **/
    public ContentType getContentType() {
        DataObject obj = (DataObject)get(CONTENT_TYPE);
        if(obj == null) return null;
        return (ContentType)DomainObjectFactory.newInstance(obj);
    }

    /**
     * Sets the <code>ContentType</code> for this
     * <code>CategoryTemplateMapping</code>
     *
     * @param t The ContentType for which this mapping should be valid.
     **/
    public void setContentType(ContentType t) {
        Assert.assertNotNull(t);
        setAssociation(CONTENT_TYPE, t);
    }

    /**
     * Determine if the template will be the default within its
     * context
     *
     * @return whether the template is the default within its context.
     */
    public Boolean isDefault() {
        return (Boolean)get(IS_DEFAULT);
    }

    /**
     * Set whether the template will be the default within its
     * context
     *
     * @param b whether the template is the default within its context.
     */
    public void setDefault(Boolean b) {
        Assert.assertNotNull(b);
        set(IS_DEFAULT, b);
    }

    /**
     * Load the specified mapping; return null if no such mapping
     * exists
     *
     * @param category The Category for the return mapping
     * @param type The ContentType for the return mapping
     * @param template The Template for the return mapping
     * @param useContext The use context for the return mapping
     *
     * @return the CategoryTemplateMapping matching the specified
     * inputs, null if none exist
     */
    public static CategoryTemplateMapping getMapping(
                                                     Category category, ContentType type,
                                                     Template template, String useContext
                                                     ) {
        CategoryTemplateCollection c = getTemplates(category, type, useContext);
        c.addEqualsFilter(TEMPLATE + "." + ACSObject.ID, template.getID());
        if(!c.next()) return null;
        CategoryTemplateMapping m = (CategoryTemplateMapping)c.getDomainObject();
        Assert.assertTrue(!c.next());
        c.close();
        return m;
    }

    /**
     * Get the default template for the given use context
     *
     * @param category The Category for the return mapping
     * @param type The ContentType for the return mapping
     * @param useContext The use context for the return mapping
     *
     * @return the Template matching the specified
     * inputs, null if none exist
     */
    public static Template getDefaultTemplate (
                                               Category category, ContentType type, String useContext
                                               ) {
        CategoryTemplateCollection c = getTemplates(category, type, useContext);
        c.addEqualsFilter(IS_DEFAULT, new Boolean(true));
        if(!c.next()) return null;
        CategoryTemplateMapping m = (CategoryTemplateMapping)c.getDomainObject();
        // FIXME: There HAS to be a better way to enforce uniqueness here...
        Assert.assertTrue(!c.next());
        c.close();
        return m.getTemplate();
    }

    /**
     * Retrieve all templates for the given category, type, and use
     * context
     *
     * @param category The Category for the return mapping
     * @param type The ContentType for the return mapping
     * @param useContext The use context for the return mapping
     *
     * @return the CategoryTemplateCollection matching the specified
     * inputs
     */
    public static CategoryTemplateCollection getTemplates(
                                                          Category category, ContentType type, String useContext
                                                          ) {
        CategoryTemplateCollection c = getTemplates(category, type);
        c.addEqualsFilter(USE_CONTEXT, useContext);
        return c;
    }

    /**
     * Retrieve all templates for the given category and type,
     * along with their use context
     *
     * @param category The Category for the return mapping
     * @param type The ContentType for the return mapping
     *
     * @return the CategoryTemplateCollection matching the specified
     * inputs
     */
    public static CategoryTemplateCollection getTemplates(
                                                          Category category, ContentType type
                                                          ) {
        CategoryTemplateCollection c = getTemplates(category);
        c.addEqualsFilter(CONTENT_TYPE + "." + ACSObject.ID, type.getID());
        return c;
    }

    /**
     * Retrieve all templates for the given category, and all
     * types within it, along with their use context
     *
     * @param category The Category for the return mapping
     *
     * @return the CategoryTemplateCollection matching the specified
     * inputs
     */
    public static CategoryTemplateCollection getTemplates(
                                                          Category category
                                                          ) {
        DataCollection da = SessionManager.getSession().retrieve
            (BASE_DATA_OBJECT_TYPE);
        CategoryTemplateCollection c = new CategoryTemplateCollection(da);
        c.addEqualsFilter(CATEGORY + "." + ACSObject.ID, category.getID());
        c.addOrder(CONTENT_TYPE + "." + ContentType.LABEL);
        c.addOrder(USE_CONTEXT);
        c.addOrder(TEMPLATE + "." + ContentItem.NAME);
        return c;
    }

}
