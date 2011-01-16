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
package com.arsdigita.domain;

import com.arsdigita.util.Assert;

import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.persistence.metadata.Property;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.persistence.DataAssociationCursor;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.DataQueryDataCollectionAdapter;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.log4j.Logger;

/**
 * <p>This class provides a general purpose framework for iterating
 * over a domain object's properties, processing attributes and
 * traversing associations as required.</p>
 *
 * <p>Subclasses should implement the startXXX and endXXX methods to
 * provide whatever processing logic they require upon encountering
 * attributes, roles, associations and objects.</p>
 *
 * <p>The {@link com.arsdigita.domain.DomainObjectTraversalAdapter}
 * provides a means to control which properties are processed and,
 * most importantly, which associations are traversed. When
 * registering an adapter, a 'use context' is supplied allowing
 * different adapters to be used according to the requirements of any
 * implementing subclass. It is recommended that the use context be
 * based on the fully qualified name of the class using
 * DomainObjectTraversal, e.g.,
 * com.arsdigita.cms.ui.DomainObjectRenderer.</p>
 *
 * <p>The path argument provided to the adapter and the startXXX ad
 * endXXX methods indicates which associations are currently being
 * traversed. The first element in the path is always '/object'. If it
 * then starts to traverse the 'rootCategory' association, the path
 * will become '/object/rootCategory'. For self-recursive
 * associations, rather than building up a long repeating string, the
 * path will be shortened by adding a '+' for each element that is
 * repeated.  For example, '/object/container+' indicates that the
 * container association has been followed two or more times.</p>
 */
public abstract class DomainObjectTraversal {

    private Set m_visited = new HashSet();
    private static Map s_adapters = new HashMap();
    private static final Logger s_log = Logger.getLogger(
            DomainObjectTraversal.class);
    public final static String LINK_NAME = "link";

    /**
     * Registers a traversal adapter for an object type in a given
     * context.
     *
     * @param type the object type whose items will be traversed
     * @param adapter the adapter for controlling object traversal
     * @param context the context in which the adapter should be used
     */
    public static void registerAdapter(final ObjectType type,
                                       final DomainObjectTraversalAdapter adapter,
                                       final String context) {
        Assert.exists(adapter,
                      "The DomainObjectTraversalAdapter is null for context '"
                      + context + "' and object type '" + type);
        Assert.exists(type, "The ObjectType for context '" + context
                            + "' and adapter '" + adapter + "' is null");
        Assert.exists(context, String.class);
        if (s_log.isDebugEnabled()) {
            s_log.debug("Registering adapter " + adapter.getClass()
                        + " for object type " + type.getQualifiedName()
                        + " in context " + context);
        }
        s_adapters.put(new AdapterKey(type, context), adapter);
    }

    /**
     * Unregisters a traversal adapter for an object type in a
     * given context
     *
     * @param type the object type whose items will be traversed
     * @param context the context in which the adapter should be used
     */
    public static void unregisterAdapter(final ObjectType type,
                                         final String context) {
        Assert.exists(type, ObjectType.class);
        Assert.exists(context, String.class);

        if (s_log.isDebugEnabled()) {
            s_log.debug("Removing adapter " + " for object type " + type.
                    getQualifiedName() + " in context " + context);
        }

        s_adapters.remove(new AdapterKey(type, context));
    }

    /**
     * Registers a traversal adapter for an object type in a given
     * context.
     *
     * @param type the object type whose items will be traversed
     * @param adapter the adapter for controlling object traversal
     * @param context the context in which the adapter should be used
     */
    public static void registerAdapter(final String type,
                                       final DomainObjectTraversalAdapter adapter,
                                       final String context) {
        registerAdapter(SessionManager.getMetadataRoot().getObjectType(type),
                        adapter,
                        context);
    }

    /**
     * Unregisters a traversal adapter for an object type in a
     * given context
     *
     * @param type the object type whose items will be traversed
     * @param context the context in which the adapter should be used
     */
    public static void unregisterAdapter(final String type,
                                         final String context) {
        unregisterAdapter(SessionManager.getMetadataRoot().getObjectType(type),
                          context);
    }

    /**
     * Retrieves the traversal adapter for an object type in a given
     * context.
     *
     * @param type the object type to lookup
     * @param context the adapter context
     */
    public static DomainObjectTraversalAdapter lookupAdapter(
            final ObjectType type,
            final String context) {
        Assert.exists(type, ObjectType.class);
        Assert.exists(context, String.class);
        if (s_log.isDebugEnabled()) {
            s_log.debug("lookupAdapter for type " + type.getQualifiedName()
                        + " in context " + context);

        }

        return (DomainObjectTraversalAdapter) s_adapters.get(
                new AdapterKey(type, context));
    }

    /**
     * Retrieves the closest matching traversal adapter for an object type
     * in a given context. The algorithm looks for an exact match, then
     * considers the supertype, and the supertype's supertype. If no match
     * could be found at all, returns null
     *
     * @param type the object type to search for
     * @param context the adapter context
     */
    public static DomainObjectTraversalAdapter findAdapter(ObjectType type,
                                                           final String context) {
        Assert.exists(type, ObjectType.class);
        Assert.exists(context, String.class);
        if (s_log.isDebugEnabled()) {
            s_log.debug("findAdapter for type " + type.getQualifiedName()
                        + " in context " + context);

            StringBuilder buf = new StringBuilder();
            buf.append("Adapters contain:\n");
            Iterator keys = s_adapters.keySet().iterator();
            while (keys.hasNext()) {
                Object key = keys.next();
                buf.append(key.toString()).append(": ");
                buf.append(s_adapters.get(key).toString()).append('\n');
            }

            s_log.debug(buf.toString());
        }
        DomainObjectTraversalAdapter adapter = null;
        ObjectType tmpType = type;
        while (adapter == null && tmpType != null) {
            adapter = lookupAdapter(tmpType, context);
            tmpType = tmpType.getSupertype();
        }
        if (adapter == null) {
            s_log.warn("Could not find adapter for object type " + type.
                    getQualifiedName() + " in context " + context);
        }
        return adapter;
    }

    /**
     * Walks over properties of a domain object, invoking
     * methods to handle associations, roles and attributes.
     *
     * @param obj the domain object to traverse
     * @param context the context for the traversal adapter
     */
    public void walk(final DomainObject obj,
                     final String context) {
        final DomainObjectTraversalAdapter adapter = findAdapter(obj.
                getObjectType(),
                                                                 context);
        if (adapter == null) {
            final String errorMsg = "No adapter for object " + obj.getOID()
                                    + " in context " + context;
            s_log.error(errorMsg);
            throw new IllegalArgumentException(errorMsg);
        }
        walk(obj, context, adapter);
    }

    protected void walk(final DomainObject obj,
                        final String context,
                        final DomainObjectTraversalAdapter adapter) {
        Assert.exists(adapter, DomainObjectTraversalAdapter.class);
        walk(adapter, obj, "/object", context, null);
    }

    // Changed from private to protected because I needed to have access to
    // the class ContentBundle from package com.arsdigita.cms. The problem was
    // to change RelatedLinks and therefore Link to always link to the corresponding
    // ContentBundle instead of the content item. To get the corresponding
    // content item during XML generation, I have to test for ContentBundle and
    // negotiate the language version. This is not possible in com.arsdigita.ccm.
    protected void walk(final DomainObjectTraversalAdapter adapter,
                        final DomainObject obj,
                        final String path,
                        final String context,
                        final DomainObject linkObject) {
        s_log.debug(String.format("Walking with path %s and context %s...", path,
                                  context));
        OID oid = obj.getOID();
        OID linkOid = null;
        if (linkObject != null) {
            linkOid = linkObject.getOID();
        }
        OID[] visitedKey = {oid, linkOid};
        // Prevent infinite recursion
        if (m_visited.contains(visitedKey)) {
            revisitObject(obj, path);
            return;
        } else {
            m_visited.add(visitedKey);
        }

        // If needed, open a surrounding tag
        beginObject(obj, path);

        if (linkObject != null) {
            beginLink(linkObject, path);
            walk(adapter,
                 linkObject,
                 appendToPath(path, LINK_NAME),
                 context,
                 null);
            endLink(linkObject, path);
        }

        ObjectType type = obj.getObjectType();

        // Test all properties against the traversal xml
        for (Iterator i = type.getProperties(); i.hasNext();) {
            Property prop = (Property) i.next();
            String propName = prop.getName();

            if (!adapter.processProperty(obj,
                                         appendToPath(path, prop.getName()),
                                         prop,
                                         context)) {
                if (s_log.isDebugEnabled()) {
                    s_log.debug("Not processing " + appendToPath(path, prop.
                            getName()) + " in object " + oid + " and context "
                                + context + " with adapter " + adapter.getClass().
                            getName());
                }
                continue;
            }
            Object propValue = obj.get(propName);
            if (propValue == null) {
                if (s_log.isDebugEnabled()) {
                    s_log.debug("Object " + oid.toString() + " doesn't "
                                + "contain property " + propName);
                }
                continue;
            }

            if (prop.isAttribute()) {
                handleAttribute(obj, path, prop);

            } else if (propValue instanceof DataObject) {
                // Property is a DataObject, so start recursion
                if (s_log.isDebugEnabled()) {
                    s_log.debug(prop.getName() + " is a DataObject");
                }

                beginRole(obj, path, prop);

                walk(adapter,
                     DomainObjectFactory.newInstance((DataObject) propValue),
                     appendToPath(path, propName),
                     context,
                     null);

                endRole(obj, path, prop);
            } else if (propValue instanceof DataAssociation) {


                // see #25808 - this hack prevents the content field of cms_files
                // (which is a blob) from being queried when all we need is a
                // list of the files on an item..
                if (prop.getName().equals("fileAttachments") && !Domain.
                        getConfig().queryBlobContentForFileAttachments()) {
                    // make true a config
                    DataQuery fileAttachmentsQuery =
                              SessionManager.getSession().retrieveQuery(
                            "com.arsdigita.cms.contentassets.fileAttachmentsQuery");

                    fileAttachmentsQuery.setParameter("item_id",
                                                      obj.getOID().get("id"));

                    DataCollection files = new DataQueryDataCollectionAdapter(
                            fileAttachmentsQuery, "file");

                    while (files.next()) {
                        DataObject file = files.getDataObject();
                        walk(adapter,
                             DomainObjectFactory.newInstance(file),
                             appendToPath(path, propName),
                             context,
                             null);
                    }

                } else {
                    //2010-11-08: Moved to seperate Methods to allow simple
                    //extending of the handling of data assocications
                    /*if (s_log.isDebugEnabled()) {
                    s_log.debug(prop.getName() + " is a DataAssociation");
                    }
                    beginAssociation(obj, path, prop);

                    DataAssociationCursor daCursor =
                    ((DataAssociation) propValue).
                    getDataAssociationCursor();

                    while (daCursor.next()) {
                    s_log.debug("Processing data assoication cursor...");
                    DataObject link = daCursor.getLink();
                    DomainObject linkObj = null;
                    if (link != null) {
                    linkObj = new LinkDomainObject(link);
                    }
                    walk(adapter,
                    DomainObjectFactory.newInstance(daCursor.
                    getDataObject()),
                    appendToPath(path, propName),
                    context,
                    linkObj);
                    }
                    endAssociation(obj, path, prop);*/

                    walkDataAssociations(adapter,
                                         obj,
                                         path,
                                         context,
                                         prop,
                                         propName,
                                         propValue);
                }
            } else {
                // Unknown property value type - do nothing
            }
        }

        // If needed, close a surrounding tag
        endObject(obj, path);
    }

    protected void walkDataAssociations(DomainObjectTraversalAdapter adapter,
                                        DomainObject obj,
                                        String path,
                                        String context,
                                        Property prop,
                                        String propName,
                                        Object propValue) {
        s_log.debug(String.format("%s is a DataAssociation", prop.getName()));

        beginAssociation(obj, path, prop);

        DataAssociationCursor daCursor =
                              ((DataAssociation) propValue).
                getDataAssociationCursor();

        while (daCursor.next()) {
            walkDataAssociation(adapter,
                                obj,
                                path,
                                context,
                                propName,
                                daCursor);
        }

        endAssociation(obj, path, prop);
    }

    protected void walkDataAssociation(DomainObjectTraversalAdapter adapter,
                                       DomainObject obj,
                                       String path,
                                       String context,
                                       String propName,
                                       DataAssociationCursor daCursor) {
        s_log.debug("Processing data assoication cursor...");
        DataObject link = daCursor.getLink();
        DomainObject linkObj = null;
        if (link != null) {
            linkObj = new LinkDomainObject(link);
        }
        walk(adapter,
             DomainObjectFactory.newInstance(daCursor.getDataObject()),
             appendToPath(path, propName),
             context,
             linkObj);
    }

    /**
     * Method called when the processing of an object
     * starts
     */
    protected abstract void beginObject(DomainObject obj,
                                        String path);

    /**
     * Method called when the procesing of an object
     * completes
     */
    protected abstract void endObject(DomainObject obj,
                                      String path);

    /**
     * Method called when the processing of a Link Object
     * starts
     */
    protected void beginLink(DomainObject obj, String path) {
        s_log.debug(String.format("Starting link with path = %s", path));
    }

    /**
     * Method called when the procesing of a Link Object
     * completes
     */
    protected void endLink(DomainObject obj, String path) {
        s_log.debug(String.format("Finished link with path = %s", path));
    }

    /**
     * Method called when a previously visited object
     * is encountered for a second time.
     */
    protected abstract void revisitObject(DomainObject obj,
                                          String path);

    /**
     * Method called when an attribute is encountered
     */
    protected abstract void handleAttribute(DomainObject obj,
                                            String path,
                                            Property property);

    /**
     * Method called when the processing of a role
     * starts
     */
    protected abstract void beginRole(DomainObject obj,
                                      String path,
                                      Property property);

    /**
     * Method called when the procesing of a role
     * completes
     */
    protected abstract void endRole(DomainObject obj,
                                    String path,
                                    Property property);

    /**
     * Method called when the processing of an association
     * starts
     */
    protected abstract void beginAssociation(DomainObject obj,
                                             String path,
                                             Property property);

    /**
     * Method called when the procesing of an association
     * completes
     */
    protected abstract void endAssociation(DomainObject obj,
                                           String path,
                                           Property property);

    protected String appendToPath(String path,
                                  String name) {
        if (path.endsWith("/" + name)) {
            path = path + "+";
        } else if (!path.endsWith("/" + name + "+")) {
            path = path + "/" + name;
        }

        return path;
    }

    protected String nameFromPath(String path) {
        int index = path.lastIndexOf("/");
        Assert.isTrue(index >= 0, "Path starts with /");

        if (path.endsWith("+")) {
            return path.substring(index + 1, path.length() - 1);
        } else {
            return path.substring(index + 1);
        }
    }

    protected String parentFromPath(String path) {
        int index = path.lastIndexOf("/");
        Assert.isTrue(index >= 0, "Path starts with /");

        if (index == 0) {
            return null;
        } else {
            return path.substring(0, index - 1);
        }
    }

    protected static class AdapterKey {

        private final ObjectType m_type;
        private final String m_context;

        public AdapterKey(ObjectType type,
                          String context) {
            Assert.exists(type, ObjectType.class);
            Assert.exists(context, String.class);
            m_type = type;
            m_context = context;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof AdapterKey) {
                AdapterKey k = (AdapterKey) o;
                return k.m_type.equals(m_type) && k.m_context.equals(m_context);
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return m_type.hashCode() + m_context.hashCode();
        }

        @Override
        public String toString() {
            return m_type.getQualifiedName() + ',' + m_context;
        }
    }

    /**
     *  this is simply a subclass since DomainObject is abstract
     *  but we don't have any other domain object to use.
     */
    protected class LinkDomainObject extends DomainObject {

        public LinkDomainObject(DataObject object) {
            super(object);
        }

        @Override
        public Object get(String attr) {
        return super.get(attr);
    }
    }
}
