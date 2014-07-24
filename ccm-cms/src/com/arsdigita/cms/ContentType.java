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
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.persistence.CompoundFilter;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.DataQueryDataCollectionAdapter;
import com.arsdigita.persistence.FilterFactory;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.web.Web;
import java.io.IOException;
import java.io.InputStream;

import java.math.BigDecimal;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import java.net.URL;
import java.net.MalformedURLException;
import java.util.StringTokenizer;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * <p>
 * A Content Type defines the characteristics of a content item. Content management resources are
 * registered to a content type, including the {@link com.arsdigita.cms.AuthoringKit Authoring Kit},
 * and {@link com.arsdigita.cms.Template templates}.</p>
 *
 * <p>
 * Each content type is associated with a {@link
 * com.arsdigita.domain.DomainObject domain object} and a {@link
 * com.arsdigita.persistence.DataObject data object} type.</p>
 *
 * @author Michael Pih (pihman@arsdigita.com)
 * @author Jack Chung (flattop@arsdigita.com)
 * @version $Id: ContentType.java 2277 2012-02-22 15:23:49Z pboy $
 */
public class ContentType extends ACSObject {

    /**
     * Internal logger instance to faciliate debugging. Enable logging output by editing
     * /WEB-INF/conf/log4j.properties int hte runtime environment and set
     * com.arsdigita.cms.ui.authoring.NewItemForm=DEBUG by uncommenting or adding the line.
     */
    private static final Logger s_log = Logger.getLogger(ContentType.class);

    public static final String BASE_DATA_OBJECT_TYPE = "com.arsdigita.cms.ContentType";
    /**
     * The path content types are expected to store their type definition file (usually
     * [domainObjectBaseName].xml by convention). Any content item should use this location unless
     * there are good reasons for a different location.
     */
    public static final String CONTENTTYPE_DEFINITIONFILE_PATH = "/WEB-INF/content-types/";
    public static final String OBJECT_TYPE = "associatedObjectType";
    /**
     * The name or title of the content type, e.g. "File Storage Item"
     */
    public static final String LABEL = "label";
    /**
     * A short description of the type, what is is meant to do / to use for.
     */
    public static final String DESCRIPTION = "description";
    /**
     * Fully qualified name of the (main) domain class (and main entry point)
     */
    public static final String CLASSNAME = "className";
    public static final String MODE = "mode";
    public static final String AUTHORING_KIT = "authoringKit";
    public static final String ITEM_FORM_ID = "itemFormID";
    public static final String ITEM_FORM = "itemForm";
    public static final String ANCESTORS = "ancestors";
    public static final String DESCENDANTS = "descendants";

    /**
     * Default constructor. This creates a new folder.
     *
     */
    public ContentType() {
        super(BASE_DATA_OBJECT_TYPE);
    }

    /**
     * Constructor. The contained <code>DataObject</code> is retrieved from the persistent storage
     * mechanism with an <code>OID</code> specified by <i>oid</i>.
     *
     * @param oid The <code>OID</code> for the retrieved <code>DataObject</code>.
     *
     */
    public ContentType(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    /**
     * Constructor. The contained <code>DataObject</code> is retrieved from the persistent storage
     * mechanism with an <code>OID</code> specified by <i>id</i> and
     * <code>ContentType.BASE_DATA_OBJECT_TYPE</code>.
     *
     * @param id The <code>id</code> for the retrieved <code>DataObject</code>.
     *
     */
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
     * @return the base PDL object type for this item. Child classes should override this method to
     *         return the correct value
     */
    @Override
    public String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    @Override
    protected void beforeSave() {
        if (getMode() == null) {
            setMode("default");
        }
        super.beforeSave();
    }

    /**
     * Returns the object type of the items of this content type. (For example: If I create a
     * ContentType "foo". Then a I create an item "bar" of type foo. This associated object type is
     * the same as bar.getObjectType())
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
     * Fetches the label for the content type. The label is a globalized notation displayed to the
     * user to identify the content type. As an example a content type named "Article" (as in
     * getName()) will be displayed in an english environment as the label "Article", in German as
     * "Artikel", etc.
     *
     * The label is retrieved from content type's resources. The message uses some convention to
     * retrieve the message key and resource bundle.
     *
     * Client classes may overwrite the method to provide a label from a different source.
     *
     * @return The (globalized) label.
     */
    public GlobalizedMessage getLabel() {

        GlobalizedMessage label;  // the the type's label to return

        // We assume the name of the ObjectType is the base for various 
        // resources we need so we determine it first.
        String objectTypeName = getAssociatedObjectType();
        if (s_log.isDebugEnabled()) {
            s_log.debug(
                "Object Type is " + objectTypeName);
        }
        // First we'll try to locate the resource file assuming it is named
        // as the object type with resources.properties appended.
        String bundleResourcePath = "/".concat(objectTypeName.replace(".", "/"))
            .concat("Resources.properties");
        if (s_log.isDebugEnabled()) {
            s_log.debug("resource path is " + bundleResourcePath);
        }
        // Alternatively we may try the content item's definition file. Just
        // guessing its name here.
        String typeResourcePath = CONTENTTYPE_DEFINITIONFILE_PATH
            .concat(objectTypeName.replace(".", "/"))
            .concat(".xml");

        // We assume the name of the key in resource bundle is the same as
        // the ObjectType  minus the domain part ("com.arsdigita.")
        // and starting with "cms" and suffix ".type_label" appended
        String labelKey = objectTypeName.substring(objectTypeName.indexOf("cms"))
            .concat(".type_label")
            .toLowerCase();

        // First try: check, if the resource file really exists, and if it does,
        // use it.
        if (this.getClass().getClassLoader().getResource(bundleResourcePath) != null) {
            // Property file exists, use it!
            final String bundleName = objectTypeName.concat("Resources");
            // Create the globalized label
            label = new GlobalizedMessage(labelKey, bundleName);
        } else {
            // No property file found, try to use the item's definition file
            final InputStream defFile = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(typeResourcePath);

            if (defFile == null) {
                // Giving up!

                // As a fall back use the (not globalized) "name" of the type as
                // stored in the database to display the type to the user. Is is
                // used as the "key" in a GloablizedMessage, which it is definitely
                // not. But GlobalizedMessage displays the key if it could not be
                // found in a resource file.
                label = new GlobalizedMessage(getName());
            } else {
                // item definition file found. Use it.

                // determine the bundle from attribute "descriptionBundle"
                // which should provide an item specific description (but
                // unfortunately due to lazy programmers not always does).
                // As a proper example:
                // /WEB-INF/content-types/com.arsditita.cms.contenttypes.Event.xml
                try {
                    final SAXParserFactory parserFactory = SAXParserFactory.newInstance();
                    final SAXParser parser = parserFactory.newSAXParser();
                    final BundleName bundleName = new BundleName();
                    parser.parse(defFile, bundleName);

                    if (bundleName.getName() == null) {
                        // Fallback to the non-globalized identifier (name)
                        return new GlobalizedMessage(getName());
                    } else {
                        label = new GlobalizedMessage(labelKey, bundleName.getName());
                    }
                } catch (ParserConfigurationException ex) {
                    label = new GlobalizedMessage(getName());
                } catch (SAXException ex) {
                    label = new GlobalizedMessage(getName());
                } catch (IOException ex) {
                    label = new GlobalizedMessage(getName());
                }
            }

        }

        return label;
    }

    /**
     * 
     */
    private class BundleName extends DefaultHandler {

        private String name;

        /**
         * Constructor, does nothing.
         */
        public BundleName() {
                //Nothing
        }

        /**
         * 
         * @return 
         */
        public String getName() {
            return name;
        }

        /**
         * 
         * @param namespaceURI
         * @param localName
         * @param qName
         * @param attributes 
         */
        @Override
        public void startElement(final String namespaceURI,
                                 final String localName,
                                 final String qName,
                                 final Attributes attributes) {
            if ("ctd:authoring-step".equals(qName)) {
                name = attributes.getValue("descriptionBundle");
            }
        }

    }

    /**
     * Fetches the name for the content type. The name is a fixed String, which 'names' a content
     * type and is not localizible. It is stored in the database and a symbolic name for the formal
     * ID. It may contain any characters but is preferable an english term. Examples are "FAQ item"
     * or "Article" or "Multipart Article". It has to be unique system-wide.
     *
     * @return The name (ie. may be used as a non-localized label)
     */
    public String getName() {
        return (String) get(LABEL);
    }

    /**
     * Sets the name for this content type. The name is a fixed String.
     *
     * @see getName() for additional details.
     *
     * The name is stored in the database. In the database this property is stored under 'label',
     * when globliation was not an issue. The Method is primarly used in the initial loading step.
     *
     * @param name The name
     */
    public void setName(String name) {
        set(LABEL, name);
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
     * <p>
     * An internal content type is one that is not user-defined and maintained internally. A content
     * type should be made internal under the following two conditions:</p>
     *
     * <ol>
     * <li>The object type needs to take advantage of content type services (i.e., versioning,
     * categorization, lifecycle, workflow) that are already implemented in CMS.</li>
     * <li>The content type cannot be explicitly registered to a content section.</li>
     * </ol>
     *
     * <p>
     * The {@link com.arsdigita.cms.Template} content type is one such internal content type.</p>
     *
     * @return Boolean.TRUE if this content type is internal, Boolean.FALSE otherwise.
     */
    public Boolean isInternal() {
        return "I".equalsIgnoreCase((String) get(MODE));
    }

    /**
     * <p>
     * A hidden content type is one that is not user-defined but not meant to be used directly (p.
     * ex. GenericArticle). in contrast they provide some basic features for different kind of
     * content type to be extended from. Also, they are legit perents for UDCTs.
     *
     * @return Boolean.TRUE if this content type is internal, Boolean.FALSE otherwise.
     */
    public Boolean isHidden() {
        return "H".equalsIgnoreCase((String) get(MODE));
    }

    /**
     * Save the display / user mode of this content type
     *
     * @param mode string. ATM: "interal" or "hidden" or ""
     */
    public void setMode(String mode) {
        if (mode != null && !mode.isEmpty()) {
            set(MODE, mode.toUpperCase().substring(0, 1));
        } else {
            set(MODE, "default".toUpperCase().substring(0, 1));
        }
    }

    public String getMode() {
        return (String) get(MODE);
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
     * Create an authoring kit to this content type. To save this authoring kit, you need to call
     * <code>save()</code> method on the returned AuthoringKit.
     */
    public AuthoringKit createAuthoringKit() {
        return createAuthoringKit(null);
    }

    /**
     * Create an authoring kit to this content type. To save this authoring kit, you need to call
     * <code>save()</code> method on the returned AuthoringKit.
     *
     * @param createComponent the create component class associated with the authoring kit
     */
    public AuthoringKit createAuthoringKit(String createComponent) {

        if (getAuthoringKit() == null) {
            AuthoringKit kit = new AuthoringKit();
            kit.setContentType(this);
            if (createComponent != null) {
                kit.setCreateComponent(createComponent);
            }
            return kit;
        } else {
            throw new RuntimeException(
                "An AuthorigKit exists for this ContentType.");
        }
    }

    /**
     * Fetch the item creation form id of the Java domain object implementation. applies to
     * user-defined types
     *
     * @return The id of the persistent form used to create an item of this content type
     */
    public BigDecimal getItemFormID() {
        return (BigDecimal) get(ITEM_FORM_ID);
    }

    /**
     * Sets the item creation form id of the Java domain object implementation. applies to
     * user-defined types
     *
     * @param itemFormID The id of the persistent form used to create an item of this content type
     */
    public void setItemFormID(BigDecimal itemFormID) {
        set(ITEM_FORM_ID, itemFormID);
    }

    /**
     * Retrieve the persistent form of this content type
     *
     * @return the persistent form used to create or edit content items of this type (only applies
     *         to user-defined types)
     */
    public PersistentForm getItemForm() throws DataObjectNotFoundException {

        DataObject pForm = (DataObject) get(ITEM_FORM);
        if (pForm == null) {
            return null;
        } else {
            return new PersistentForm(pForm);
        }

    }

    /**
     * Add an ancestor to the list of descendants, if not already in the list.
     *
     * @param newAncestor ID of the ancestor to add
     */
    public void addAncestor(BigDecimal newAncestor) {
        // Get the list of descendants from db
        String ancestors = (String) get(ANCESTORS);

        // Only add if the newAncestor in not yet in the list
        if (ancestors == null) {
            ancestors = newAncestor.toString();
        } else if (!ancestors.contains(newAncestor.toString())) {
            if (ancestors.length() == 0) {
                // First entry in list
                ancestors = newAncestor.toString();
            } else {
                // Additional entry in the list
                ancestors += "/" + newAncestor.toString();
            }
        }

        // Write new data back to db
        set(ANCESTORS, ancestors);
    }

    /**
     * Remove an ancestor id from the list of descendants.
     *
     * @param ancestor ID to be removed
     */
    public void delAncestor(BigDecimal ancestor) {
        // Get the list of descendants from db
        String ancestors = (String) get(ANCESTORS);

        // Only try to remove from a non-empty string
        if (ancestors != null && ancestors.length() > 0) {

            // Remove ancestor ID from list
            ancestors.replace(ancestor.toString(), "");
            // Delete the additional slash
            ancestors.replace("//", "/");

            // If the list only contains a single slash, we have just removed
            // the last list entry, so the list is empty
            if (ancestors.equals("/")) {
                ancestors = "";
            }
        }

        // Write new data back to db
        set(ANCESTORS, ancestors);
    }

    /**
     * Get the list of ancestors.
     *
     * @return
     */
    public String getAncestors() {
        return (String) get(ANCESTORS);
    }

    /**
     * Add a descendant to the list of descendants, if not already in list.
     *
     * @param newDescendant ID of the descendant to add
     */
    public void addDescendants(BigDecimal newDescendant) {

        if (getID().equals(newDescendant)) {
            return;
        }

        // Get the list of descendants from db
        String descendants = (String) get(DESCENDANTS);

        // Only add if the newDescendant in not yet in the list
        if (descendants == null) {
            descendants = newDescendant.toString();
        } else if (!descendants.contains(newDescendant.toString())) {

            if (descendants.length() == 0) {
                // First entry in list
                descendants = newDescendant.toString();
            } else {
                // Additional entry in the list
                descendants += "/" + newDescendant.toString();
            }
        }

        // Write new data back to db
        set(DESCENDANTS, descendants);
    }

    /**
     * Get the list of descendants
     *
     * @return
     */
    public String getDescendants() {
        return (String) get(DESCENDANTS);
    }

    /**
     * Remove a descendant from the list of descendants
     *
     * @param descendant ID to be removed
     */
    public void delDescendants(BigDecimal descendant) {
        // Get the list of descendants from db
        String descendants = (String) get(DESCENDANTS);

        // Only try to remove from a non-empty string
        if (descendants != null && descendants.length() > 0) {

            // Remove ancestor ID from list
            descendants.replace(descendant.toString(), "");
            // Delete the additional slash
            descendants.replace("//", "/");

            // If the list only contains a single slash,
            // we have just removed the last list entry, so the list is empty
            if (descendants.equals("/")) {
                descendants = "";
            }
        }

        // Write new data back to db
        set(DESCENDANTS, descendants);
    }

    //////////////////////////////////////
    //
    // Fetching/Finding content types.
    //
    //////////////////////////////////////
    /**
     * Find the content type with the associated with the object type.
     *
     * @param objType The fully-qualified name of the data object type
     *
     * @return The content type associated with the object type
     */
    public static ContentType findByAssociatedObjectType(String objType)
        throws DataObjectNotFoundException {

        ContentTypeCollection types = getAllContentTypes();
        types.addFilter("associatedObjectType = :type").set("type", objType);

        if (types.next()) {
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
     * Fetches a collection of all content types, including internal content types.
     *
     * @return A collection of all content types
     */
    public static ContentTypeCollection getAllContentTypes() {
        return getAllContentTypes(true, true);
    }

    /**
     * Fetches a collection of all content types, including internal content types.
     *
     * @param hidden If false, fetch all content types, ecluding hidden content types
     *
     * @return A collection of all content types
     */
    public static ContentTypeCollection getAllContentTypes(boolean hidden) {
        return getAllContentTypes(true, hidden);
    }

    /**
     * Fetches a collection of all user-defined (non-internal) content types.
     *
     * @return A collection of user-defined content types
     */
    public static ContentTypeCollection getUserDefinedContentTypes() {
        return getAllContentTypes(false, true);
    }

    /**
     * @param internal If false, fetch all content types, excluding internal content types.
     * @param hidden   If false, fetch all content types, excluding hidden content types.
     */
    private static ContentTypeCollection getAllContentTypes(boolean internal, boolean hidden) {
        DataCollection da = SessionManager.getSession().retrieve(BASE_DATA_OBJECT_TYPE);
        ContentTypeCollection types = new ContentTypeCollection(da);

        if (!internal) {
            types.addFilter("mode != 'I'");
        }

        if (!hidden) {
            types.addFilter("mode != 'H'");
        }
        return types;
    }

    /**
     * Fetches a collection of content types that have been registered to at least one content
     * section, excluding internal content types.
     *
     * @return A collection of registered content types
     */
    public static ContentTypeCollection getRegisteredContentTypes() {
        final String query = "com.arsdigita.cms.registeredContentTypes";
        DataQuery dq = SessionManager.getSession().retrieveQuery(query);
        DataCollection dc = new DataQueryDataCollectionAdapter(dq, "type");
        return new ContentTypeCollection(dc);
    }

    /**
     *
     * @param ct
     *
     * @return
     */
    public static ContentTypeCollection getDescendantsOf(ContentType ct) {
        ContentTypeCollection ctc = ContentType.getRegisteredContentTypes();

        // The Filter Factory
        FilterFactory ff = ctc.getFilterFactory();

        // Create an or-filter
        CompoundFilter or = ff.or();

        // The content type must be either of the requested type
        or.addFilter(ff.equals(ContentType.ID, ct.getID().toString()));

        // Or must be a descendant of the requested type
        try {
            StringTokenizer strTok = new StringTokenizer(ct.getDescendants(), "/");
            while (strTok.hasMoreElements()) {
                or.addFilter(ff.equals(ContentType.ID, (String) strTok.nextElement()));
            }
        } catch (Exception ex) {
            // WTF? The selected content type does not exist in the table???
        }

        ctc.addFilter(or);
        return ctc;
    }

    private static List s_xsl = new ArrayList();

    /**
     * Registers an XSL file against a content type. NB this interface is liable to change.
     *
     * @param type the content type
     * @param path the path relative to the server root
     */
    public static void registerXSLFile(ContentType type, String path) {
        s_xsl.add(new XSLEntry(type, path));
    }

    /**
     * Unregisters an XSL file against a content type. NB this interface is liable to change.
     *
     * @param type the content type
     * @param path the path relative to the server root
     */
    public static void unregisterXSLFile(ContentType type,
                                         String path) {
        s_xsl.remove(new XSLEntry(type, path));
    }

    /**
     * Gets an iterator of java.net.URL objects for all registered XSL files.
     *
     * @return
     */
    public static Iterator getXSLFileURLs() {
        return new EntryIterator(s_xsl.iterator());
    }

    /**
     *
     */
    private static class EntryIterator implements Iterator {

        private final Iterator m_inner;

        public EntryIterator(Iterator inner) {
            m_inner = inner;
        }

        @Override
        public boolean hasNext() {
            return m_inner.hasNext();
        }

        @Override
        public Object next() {
            XSLEntry entry = (XSLEntry) m_inner.next();
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

        @Override
        public void remove() {
            m_inner.remove();
        }

    }

    /**
     *
     */
    private static class XSLEntry {

        private final ContentType m_type;
        private final String m_path;

        public XSLEntry(ContentType type,
                        String path) {
            m_type = type;
            m_path = path;
        }

        public ContentType getType() {
            return m_type;
        }

        public String getPath() {
            return m_path;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof XSLEntry)) {
                return false;
            }
            XSLEntry e = (XSLEntry) o;
            return m_path.equals(e.m_path)
                       && m_type.equals(e.m_type);
        }

        @Override
        public int hashCode() {
            return m_path.hashCode() + m_type.hashCode();
        }

    }

}
