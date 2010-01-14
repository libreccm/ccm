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
package com.arsdigita.cms;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.formbuilder.PersistentForm;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.DataQueryDataCollectionAdapter;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.web.Web;

import java.math.BigDecimal;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import java.net.URL;
import java.net.MalformedURLException;

/**
 * <p>A Content Type defines the characteristics of a content
 * item. Content management resources are registered to a content
 * type, including the {@link com.arsdigita.cms.AuthoringKit
 * Authoring Kit}, and {@link com.arsdigita.cms.Template
 * templates}.</p>
 *
 * <p>Each content type is associated with a {@link
 * com.arsdigita.domain.DomainObject domain object} and a {@link
 * com.arsdigita.persistence.DataObject data object} type.</p>
 *
 * @author Michael Pih (pihman@arsdigita.com)
 * @author Jack Chung (flattop@arsdigita.com)
 * @version $Id: ContentType.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class ContentType extends ACSObject {

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.cms.ContentType";

    public static final String OBJECT_TYPE       = "associatedObjectType";
    public static final String LABEL             = "label";
    public static final String DESCRIPTION       = "description";
    public static final String CLASSNAME         = "className";
    public static final String IS_INTERNAL       = "isInternal";
    public static final String AUTHORING_KIT     = "authoringKit";
    public static final String ITEM_FORM_ID      = "itemFormID";
    public static final String ITEM_FORM         = "itemForm";

    /**
     * Default constructor. This creates a new folder.
     **/
    public ContentType() {
        super(BASE_DATA_OBJECT_TYPE);
    }

    /**
     * Constructor. The contained <code>DataObject</code> is retrieved
     * from the persistent storage mechanism with an <code>OID</code>
     * specified by <i>oid</i>.
     *
     * @param oid The <code>OID</code> for the retrieved
     * <code>DataObject</code>.
     **/
    public ContentType(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    /**
     * Constructor. The contained <code>DataObject</code> is retrieved
     * from the persistent storage mechanism with an <code>OID</code>
     * specified by <i>id</i> and
     * <code>ContentType.BASE_DATA_OBJECT_TYPE</code>.
     *
     * @param id The <code>id</code> for the retrieved
     * <code>DataObject</code>.
     **/
    public ContentType(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    protected ContentType(String type) {
        super(type);
    }

    public ContentType(DataObject obj) {
        super(obj);
    }

    /**
     * @return the base PDL object type for this item. Child classes should
     *  override this method to return the correct value
     */
    public String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    protected void beforeSave() {
        if ( isInternal() == null ) {
            setInternal(false);
        }
        super.beforeSave();
    }

    /**
     * Returns the object type of the items of this content type.  (For example:
     * If I create a ContentType "foo".  Then a I create an item "bar"
     * of type foo.  This associated object type is the same as
     * bar.getObjectType())
     *
     * @return The data object type representation of this content type
     */
    public String getAssociatedObjectType() {
        return (String) get(OBJECT_TYPE);
    }

    /**
     * Set the data object type representation of this content type.
     *
     * @param objType The qualified name of the data object type
     */
    public void setAssociatedObjectType(String objType) {
        set(OBJECT_TYPE, objType);
    }


    /**
     * Fetches the label for the content type.
     *
     * @return The label
     */
    public String getLabel() {
        return (String) get(LABEL);
    }

    /**
     * Sets the label for this content type.
     *
     * @param label The label
     */
    public void setLabel(String label) {
        set(LABEL, label);
    }

    /**
     * Fetches the description for the content type.
     *
     * @return The description
     */
    public String getDescription() {
        return (String) get(DESCRIPTION);
    }

    /**
     * Sets the description for this content type.
     *
     * @param description The description
     */
    public void setDescription(String description) {
        set(DESCRIPTION, description);
    }

    /**
     * Fetch the class name of the Java domain object implementation.
     *
     * @return The class name of the Java domain object
     */
    public String getClassName() {
        return (String) get(CLASSNAME);
    }

    /**
     * Set the name of the Java class implementation of this content type.
     *
     * @param className The name of the Java domain object
     */
    public void setClassName(String className) {
        set(CLASSNAME, className);
    }

    /**
     * <p>An internal content type is one that is not user-defined and maintained
     * internally. A content type should be made internal under the following
     * two conditions:</p>
     *
     * <ol>
     *   <li>The object type needs to take advantage of content type services
     *     (i.e., versioning, categorization, lifecycle, workflow) that are
     *     already implemented in CMS.</li>
     *   <li>The content type cannot be explicitly registered to a content
     *     section.</li>
     * </ol>
     *
     * <p>The {@link com.arsdigita.cms.Template} content type is one such
     * internal content type.</p>
     *
     * @return Boolean.TRUE if this content type is internal, Boolean.FALSE
     * otherwise.
     */
    public Boolean isInternal() {
        return (Boolean) get(IS_INTERNAL);
    }

    /**
     * Make this content type internal or not.
     *
     * @param isInternal true if this content type should be internal,
     *        false otherwise
     */
    public void setInternal(boolean isInternal) {
        set(IS_INTERNAL, ( isInternal ? Boolean.TRUE : Boolean.FALSE ));
    }

    /**
     * Fetch the authoring kit for this content type.
     *
     * @return The authoring kit
     */
    public AuthoringKit getAuthoringKit() {
        DataObject kit = (DataObject) get(AUTHORING_KIT);
        if (kit == null) {
            return null;
        } else {
            return new AuthoringKit(kit);
        }
    }

    /**
     * Create an authoring kit to this content type.  To save this authoring
     * kit, you need to call <code>save()</code> method on the
     * returned AuthoringKit.
     */
    public AuthoringKit createAuthoringKit() {
        return createAuthoringKit(null);
    }

    /**
     * Create an authoring kit to this content type.  To save this authoring
     * kit, you need to call <code>save()</code> method on the
     * returned AuthoringKit.
     *
     * @param createComponent the create component class associated with
     *   the authoring kit
     */
    public AuthoringKit createAuthoringKit(String createComponent) {

        if ( getAuthoringKit() == null ) {
            AuthoringKit kit = new AuthoringKit();
            kit.setContentType(this);
            if ( createComponent != null ) {
                kit.setCreateComponent(createComponent);
            }
            return kit;
        } else {
            throw new RuntimeException(
                                       "An AuthorigKit exists for this ContentType.");
        }
    }

    /**
     * Fetch the item creation form id of the Java domain object implementation.
     * applies to user-defined types
     *
     * @return The id of the persistent form used to create an item
     *         of this content type
     */
    public BigDecimal getItemFormID() {
        return (BigDecimal) get(ITEM_FORM_ID);
    }

    /**
     * Sets the item creation form id of the Java domain object implementation.
     * applies to user-defined types
     *
     * @param itemFormID The id of the persistent form used to create an item
     *        of this content type
     */
    public void setItemFormID(BigDecimal itemFormID) {
        set(ITEM_FORM_ID, itemFormID);
    }

    /**
     * Retrieve the persistent form of this content type
     *
     * @return the persistent form used to create or edit content items
     *         of this type (only applies to user-defined types)
     */
    public PersistentForm getItemForm() throws DataObjectNotFoundException {

        DataObject pForm = (DataObject) get(ITEM_FORM);
        if (pForm == null) {
            return null;
        } else {
            return new PersistentForm(pForm);
        }

    }


    //////////////////////////////////////
    //
    // Fetching/Finding content types.
    //

    /**
     * Find the content type with the associated with the object type.
     *
     * @param objType The fully-qualified name of the data object type
     * @return The content type associated with the object type
     */
    public static ContentType findByAssociatedObjectType(String objType)
        throws DataObjectNotFoundException {

        ContentTypeCollection types = getAllContentTypes();
        types.addFilter("associatedObjectType = :type").set("type", objType);

        if ( types.next() ) {
            ContentType type = types.getContentType();
            types.close();
            return type;
        } else {
            // no match
            types.close();
            throw new DataObjectNotFoundException(
                                                  "No matching content type for object type " + objType);
        }
    }

    /**
     * Fetches a collection of all content types, including internal content
     * types.
     *
     * @return A collection of all content types
     */
    public static ContentTypeCollection getAllContentTypes() {
        return getAllContentTypes(true);
    }

    /**
     * Fetches a collection of all user-defined (non-internal) content types.
     *
     * @return A collection of user-defined content types
     */
    public static ContentTypeCollection getUserDefinedContentTypes() {
        return getAllContentTypes(false);
    }

    /**
     * @param internal If true, fetch all content types, including internal
     *    content types. If false, only fetch all user-defined content types.
     */
    private static ContentTypeCollection getAllContentTypes(boolean internal) {
        DataCollection da = SessionManager.getSession().retrieve
            (BASE_DATA_OBJECT_TYPE);
        ContentTypeCollection types = new ContentTypeCollection(da);
        if ( !internal ) {
            types.addFilter("isInternal = '0'");
        }
        return types;
    }

    /**
     * Fetches a collection of content types that have been registered
     * to at least one content section, excluding internal content types.
     *
     * @return A collection of registered content types
     */
    public static ContentTypeCollection getRegisteredContentTypes() {
        final String query = "com.arsdigita.cms.registeredContentTypes";
        DataQuery dq = SessionManager.getSession().retrieveQuery(query);
        DataCollection dc = new DataQueryDataCollectionAdapter(dq, "type");
        return new ContentTypeCollection(dc);
    }


    private static List s_xsl = new ArrayList();
    
    
    /**
     * NB this interface is liable to change.
     * 
     * Registers an XSL file against a content type. 
     * @param type the content type
     * @param path the path relative to the server root
     */
    public static void registerXSLFile(ContentType type,
                                       String path) {
        s_xsl.add(new XSLEntry(type, path));
    }

    /**
     * NB this interface is liable to change.
     * 
     * Unregisters an XSL file against a content type. 
     * @param type the content type
     * @param path the path relative to the server root
     */
    public static void unregisterXSLFile(ContentType type,
                                         String path) {
        s_xsl.remove(new XSLEntry(type, path));
    }
    
    /**
     * Gets an iterator of java.net.URL objects for
     * all registered XSL files
     */
    public static Iterator getXSLFileURLs() {
        return new EntryIterator(s_xsl.iterator());
    }

    private static class EntryIterator implements Iterator {
        private Iterator m_inner;

        public EntryIterator(Iterator inner) {
            m_inner = inner;
        }

        public boolean hasNext() {
            return m_inner.hasNext();
        }
        
        public Object next() {
            XSLEntry entry = (XSLEntry)m_inner.next();
            String path = entry.getPath();
            
            try {
                return new URL(Web.getConfig().getDefaultScheme(),
                               Web.getConfig().getHost().getName(),
                               Web.getConfig().getHost().getPort(),
                               path);
            } catch (MalformedURLException ex) {
                throw new UncheckedWrapperException("path malformed" + path, ex);
            }
        }
        
        public void remove() {
            m_inner.remove();
        }
    }

    private static class XSLEntry {
        private ContentType m_type;
        private String m_path;
        
        public XSLEntry(ContentType type,
                        String path) {
            m_type = type;
            m_path = path;
        }
        
        public ContentType getType(){
            return m_type;
        }

        public String getPath() {
            return m_path;
        }
        
        public boolean equals(Object o) {
            if (!(o instanceof XSLEntry)) {
                return false;
            }
            XSLEntry e = (XSLEntry)o;
            return m_path.equals(e.m_path) &&
                m_type.equals(e.m_type);
        }
        
        public int hashCode() {
            return m_path.hashCode() + m_type.hashCode();
        }
    }
}
