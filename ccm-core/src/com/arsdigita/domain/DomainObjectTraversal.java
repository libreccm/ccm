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

import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.persistence.DataAssociationCursor;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.DataQueryDataCollectionAdapter;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.persistence.metadata.Property;
import com.arsdigita.util.Assert;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;

/**
 * <p>This class provides a general purpose framework for iterating over a domain object's properties, processing
 * attributes and traversing associations as required.</p>
 *
 * <p>Subclasses should implement the startXXX and endXXX methods to provide whatever processing logic they require upon
 * encountering attributes, roles, associations and objects.</p>
 *
 * <p>The {@link com.arsdigita.domain.DomainObjectTraversalAdapter} provides a means to control which properties are
 * processed and, most importantly, which associations are traversed. When registering an adapter, a 'use context' is
 * supplied allowing different adapters to be used according to the requirements of any implementing subclass. It is
 * recommended that the use context be based on the fully qualified name of the class using DomainObjectTraversal, e.g.,
 * com.arsdigita.cms.ui.DomainObjectRenderer.</p>
 *
 * <p>The path argument provided to the adapter and the startXXX ad endXXX methods indicates which associations are
 * currently being traversed. The first element in the path is always '/object'. If it then starts to traverse the
 * 'rootCategory' association, the path will become '/object/rootCategory'. For self-recursive associations, rather than
 * building up a long repeating string, the path will be shortened by adding a '+' for each element that is repeated.
 * For example, '/object/container+' indicates that the container association has been followed two or more times.</p>
 */
public abstract class DomainObjectTraversal {

    private Set m_visited = new HashSet();
    private static Map s_adapters = new HashMap();
    private static final Logger s_log = Logger.getLogger(DomainObjectTraversal.class);
    public final static String LINK_NAME = "link";

    /**
     * Registers a traversal adapter for an object type in a given context.
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
     * Unregisters a traversal adapter for an object type in a given context
     *
     * @param type the object type whose items will be traversed
     * @param context the context in which the adapter should be used
     */
    public static void unregisterAdapter(final ObjectType type,
                                         final String context) {
        Assert.exists(type, ObjectType.class);
        Assert.exists(context, String.class);

        if (s_log.isDebugEnabled()) {
            s_log.debug("Removing adapter " + " for object type " + type.getQualifiedName() + " in context " + context);
        }

        s_adapters.remove(new AdapterKey(type, context));
    }

    /**
     * Registers a traversal adapter for an object type in a given context.
     *
     * @param type the object type whose items will be traversed
     * @param adapter the adapter for controlling object traversal
     * @param context the context in which the adapter should be used
     */
    public static void registerAdapter(final String type,
                                       final DomainObjectTraversalAdapter adapter,
                                       final String context) {
        final ObjectType objectType = SessionManager.getMetadataRoot().getObjectType(type);
        if (objectType == null) {
            throw new IllegalArgumentException(String.format("Can't find object type '%s'", type));
        }
        registerAdapter(objectType,
                        adapter,
                        context);
    }

    /**
     * Unregisters a traversal adapter for an object type in a given context
     *
     * @param type the object type whose items will be traversed
     * @param context the context in which the adapter should be used
     */
    public static void unregisterAdapter(final String type,
                                         final String context) {
        final ObjectType objectType = SessionManager.getMetadataRoot().getObjectType(type);
        if (objectType == null) {
            throw new IllegalArgumentException(String.format("Can't find object type '%s'", type));
        }
        unregisterAdapter(objectType,
                          context);
    }

    /**
     * Retrieves the traversal adapter for an object type in a given context.
     *
     * @param type the object type to lookup
     * @param context the adapter context
     * @return  
     */
    public static DomainObjectTraversalAdapter lookupAdapter(final ObjectType type, final String context) {
        Assert.exists(type, ObjectType.class);
        Assert.exists(context, String.class);
        if (s_log.isDebugEnabled()) {
            s_log.debug("lookupAdapter for type " + type.getQualifiedName()
                        + " in context " + context);

        }

        return (DomainObjectTraversalAdapter) s_adapters.get(new AdapterKey(type, context));
    }

    /**
     * Retrieves the closest matching traversal adapter for an object type in a given context. The algorithm looks for
     * an exact match, then considers the supertype, and the supertype's supertype. If no match could be found at all,
     * returns null
     *
     * @param type the object type to search for
     * @param context the adapter context
     * @return  
     */
    public static DomainObjectTraversalAdapter findAdapter(final ObjectType type, final String context) {
        Assert.exists(type, ObjectType.class);
        Assert.exists(context, String.class);
        if (s_log.isDebugEnabled()) {
            s_log.debug("findAdapter for type " + type.getQualifiedName()
                        + " in context " + context);

            final StringBuilder buf = new StringBuilder();
            buf.append("Adapters contain:\n");
            final Iterator keys = s_adapters.keySet().iterator();
            while (keys.hasNext()) {
                final Object key = keys.next();
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
            s_log.warn("Could not find adapter for object type " + type.getQualifiedName() + " in context " + context);
        }
        return adapter;
    }

    /**
     * Walks over properties of a domain object, invoking methods to handle associations, roles and attributes.
     *
     * @param obj the domain object to traverse
     * @param context the context for the traversal adapter
     */
    public void walk(final DomainObject obj, final String context) {        
        final DomainObjectTraversalAdapter adapter = findAdapter(obj.getObjectType(), context);       
        if (adapter == null) {
            final String errorMsg = "No adapter for object " + obj.getOID()
                                    + " in context " + context;
            s_log.error(errorMsg);
            throw new IllegalArgumentException(errorMsg);
        }
        walk(obj, context, adapter);
    }

    protected void walk(final DomainObject obj, final String context, final DomainObjectTraversalAdapter adapter) {
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
        final OID oid = obj.getOID();
        OID linkOid = null;
        if (linkObject != null) {
            linkOid = linkObject.getOID();
        }
        
        final OID[] visitedKey = {oid, linkOid};
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

        final ObjectType type = obj.getObjectType();

        // Test all properties against the traversal xml
        for (Iterator i = type.getProperties(); i.hasNext();) {
            processProperty((Property) i.next(), obj, adapter, path, context, oid);
//            System.err.println("Next property...");
//            final long propStart = System.nanoTime();
//            final Property prop = (Property) i.next();
//            final String propName = prop.getName();
//
//            if (!adapter.processProperty(obj,
//                                         appendToPath(path, prop.getName()),
//                                         prop,
//                                         context)) {
//                if (s_log.isDebugEnabled()) {
//                    s_log.debug("Not processing " + appendToPath(path, prop.getName()) + " in object " + oid
//                                + " and context "
//                                + context + " with adapter " + adapter.getClass().
//                            getName());
//                }
//                continue;
//            }
//
//            final Object propValue = obj.get(propName);
//            if (propValue == null) {
//                if (s_log.isDebugEnabled()) {
//                    s_log.debug("Object " + oid.toString() + " doesn't "
//                                + "contain property " + propName);
//                }
//                continue;
//            }
//
//            if (prop.isAttribute()) {
//                handleAttribute(obj, path, prop);
//
//            } else if (propValue instanceof DataObject) {
//                // Property is a DataObject, so start recursion
//                if (s_log.isDebugEnabled()) {
//                    s_log.debug(prop.getName() + " is a DataObject");
//                }
//
//                beginRole(obj, path, prop);
//
//                walk(adapter,
//                     DomainObjectFactory.newInstance((DataObject) propValue),
//                     appendToPath(path, propName),
//                     context,
//                     null);
//
//                endRole(obj, path, prop);
//            } else if (propValue instanceof DataAssociation) {
//                final long assocStart = System.nanoTime();
//
//                // see #25808 - this hack prevents the content field of cms_files
//                // (which is a blob) from being queried when all we need is a
//                // list of the files on an item..
//                final long checkstart = System.nanoTime();
//                final boolean result = prop.getName().equals("fileAttachments") && !Domain.getConfig().
//                        queryBlobContentForFileAttachments();
//                System.err.printf("Checked if property is file attachment in %d ms. Result is %s\n", (System.nanoTime()
//                                                                                                      - checkstart)
//                                                                                                     / 1000000, Boolean.
//                        toString(result));
//                //if (prop.getName().equals("fileAttachments") && !Domain.getConfig().queryBlobContentForFileAttachments()) {   
//                if (result) {
//                    // make true a config                    
//                    DataQuery fileAttachmentsQuery =
//                              SessionManager.getSession().retrieveQuery(
//                            "com.arsdigita.cms.contentassets.fileAttachmentsQuery");
//
//                    fileAttachmentsQuery.setParameter("item_id",
//                                                      obj.getOID().get("id"));
//
//                    DataCollection files = new DataQueryDataCollectionAdapter(
//                            fileAttachmentsQuery, "file");
//
//                    while (files.next()) {
//                        final DataObject file = files.getDataObject();
//                        walk(adapter,
//                             DomainObjectFactory.newInstance(file),
//                             appendToPath(path, propName),
//                             context,
//                             null);
//                    }
//
//                } else {
//                    //2010-11-08: Moved to seperate Methods to allow simple
//                    //extending of the handling of data assocications
//                    /*
//                     * if (s_log.isDebugEnabled()) { s_log.debug(prop.getName() + " is a DataAssociation"); }
//                     * beginAssociation(obj, path, prop);
//                     *
//                     * DataAssociationCursor daCursor = ((DataAssociation) propValue). getDataAssociationCursor();
//                     *
//                     * while (daCursor.next()) { s_log.debug("Processing data assoication cursor..."); DataObject link =
//                     * daCursor.getLink(); DomainObject linkObj = null; if (link != null) { linkObj = new
//                     * LinkDomainObject(link); } walk(adapter, DomainObjectFactory.newInstance(daCursor.
//                     * getDataObject()), appendToPath(path, propName), context, linkObj); } endAssociation(obj, path, prop);
//                     */
//
//                    walkDataAssociations(adapter,
//                                         obj,
//                                         path,
//                                         context,
//                                         prop,
//                                         propName,
//                                         propValue);
//                }
//                System.err.printf("Proceesed data association %s (%s) in %d ms\n", path, propName, (System.nanoTime()
//                                                                                                    - assocStart)
//                                                                                                   / 1000000);
//            } else {
//                // Unknown property value type - do nothing
//            }
//            System.err.printf("Processed property %s in %d ms.", propName, (System.nanoTime() - propStart) / 1000000);
//            System.err.printf("Walking since %d ms\n", (System.nanoTime() - start) / 1000000);
        }

        // If needed, close a surrounding tag
        endObject(obj, path);
    }

    private void processProperty(final Property prop,
                                 final DomainObject obj,
                                 final DomainObjectTraversalAdapter adapter,
                                 final String path,
                                 final String context,
                                 final OID oid) {
        //final long start = System.nanoTime();
        final String propName = prop.getName();
        //System.out.printf("Processing property %30s...\n", propName);

        if (!adapter.processProperty(obj,
                                     appendToPath(path, prop.getName()),
                                     prop,
                                     context)) {
            if (s_log.isDebugEnabled()) {
                s_log.debug("Not processing " + appendToPath(path, prop.getName()) + " in object " + oid
                            + " and context "
                            + context + " with adapter " + adapter.getClass().
                        getName());
            }
            return;
        }

        final Object propValue = obj.get(propName);
        if (propValue == null) {
            if (s_log.isDebugEnabled()) {
                s_log.debug("Object " + oid.toString() + " doesn't "
                            + "contain property " + propName);
            }
            return;
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
            //final long assocStart = System.nanoTime();
            // see #25808 - this hack prevents the content field of cms_files
            // (which is a blob) from being queried when all we need is a
            // list of the files on an item..            
            final boolean result = "fileAttachments".equals(prop.getName())
                                   && !Domain.getConfig().queryBlobContentForFileAttachments();
            //if (prop.getName().equals("fileAttachments") && !Domain.getConfig().queryBlobContentForFileAttachments()) {   
            if (result) {
                // make true a config                    
                final DataQuery fileAttachmentsQuery = SessionManager.getSession().retrieveQuery(
                        "com.arsdigita.cms.contentassets.fileAttachmentsQuery");
                fileAttachmentsQuery.setParameter("item_id", obj.getOID().get("id"));

                final DataCollection files = new DataQueryDataCollectionAdapter(fileAttachmentsQuery, "file");
                //2013-02-16 jensp: Don't process the loop if there are no files...
                if (!files.isEmpty()) {                                        
                    while (files.next()) {
                        final DataObject file = files.getDataObject();
                        walk(adapter,
                             DomainObjectFactory.newInstance(file),
                             appendToPath(path, propName),
                             context,
                             null);
                    }
                }

            } else {
                //2010-11-08: Moved to seperate Methods to allow simple
                //extending of the handling of data assocications
                    /*
                 * if (s_log.isDebugEnabled()) { s_log.debug(prop.getName() + " is a DataAssociation"); }
                 * beginAssociation(obj, path, prop);
                 *
                 * DataAssociationCursor daCursor = ((DataAssociation) propValue). getDataAssociationCursor();
                 *
                 * while (daCursor.next()) { s_log.debug("Processing data assoication cursor..."); DataObject link =
                 * daCursor.getLink(); DomainObject linkObj = null; if (link != null) { linkObj = new
                 * LinkDomainObject(link); } walk(adapter, DomainObjectFactory.newInstance(daCursor.
                 * getDataObject()), appendToPath(path, propName), context, linkObj); } endAssociation(obj, path, prop);
                 */
                //System.out.printf("\tNeeded %3d ms to get here.\n", (System.nanoTime() - assocStart) / 1000000);
                walkDataAssociations(adapter,
                                     obj,
                                     path,
                                     context,
                                     prop,
                                     propName,
                                     propValue);
                //System.out.printf("\tNeeded %3d ms to get here.\n", (System.nanoTime() - assocStart) / 1000000);
            }
            //System.out.printf("\tProcssed assoc in %3d ms.\n ", (System.nanoTime() - assocStart) / 1000000);
        }

        //System.out.printf("Processed property %30s in %3d ms.\n", propName, (System.nanoTime() - start) / 1000000);
    }

    protected void walkDataAssociations(final DomainObjectTraversalAdapter adapter,
                                        final DomainObject obj,
                                        final String path,
                                        final String context,
                                        final Property prop,
                                        final String propName,
                                        final Object propValue) {
        s_log.debug(String.format("%s is a DataAssociation", prop.getName()));

        final DataAssociationCursor daCursor = ((DataAssociation) propValue).getDataAssociationCursor();

        //jensp 2013-02-06: Wrapped the while loop with an if to avoid a call of DataAssoiciationCursor#next() on a
        //empty collection. DataAssoiciationCursor#next() tooks a serious amount of time, especially on the 
        //fileAttachments association (about 25 ms per item). A call of DataAssociationCursor#isEmpty() needs less than
        //1 ms. If you have list with specialized items, maybe with assoication to other items, this speeds up the XML 
        //rendering a little bit...
        //Also the beginAssoication and endAssociation methods have been moved into the if block so that they only 
        //created if the association is not empty
        if (!daCursor.isEmpty()) {

            beginAssociation(obj, path, prop);

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

    }

    protected void walkDataAssociation(final DomainObjectTraversalAdapter adapter,
                                       final DomainObject obj,
                                       final String path,
                                       final String context,
                                       final String propName,
                                       final DataAssociationCursor daCursor) {
        s_log.debug(String.format("Processing data assoication cursor for object '%s'...",
                                  obj.getOID().toString()));
        final DataObject link = daCursor.getLink();
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
     * Method called when the processing of an object starts
     * @param obj
     * @param path  
     */
    protected abstract void beginObject(DomainObject obj, String path);

    /**
     * Method called when the procesing of an object completes
     * @param obj
     * @param path  
     */
    protected abstract void endObject(DomainObject obj, String path);

    /**
     * Method called when the processing of a Link Object starts
     * @param obj
     * @param path  
     */
    protected void beginLink(final DomainObject obj, final String path) {
        s_log.debug(String.format("Starting link with path = %s", path));
    }

    /**
     * Method called when the procesing of a Link Object completes
     * @param obj
     * @param path  
     */
    protected void endLink(final DomainObject obj, final String path) {
        s_log.debug(String.format("Finished link with path = %s", path));
    }

    /**
     * Method called when a previously visited object is encountered for a second time.
     * @param obj
     * @param path  
     */
    protected abstract void revisitObject(final DomainObject obj, final String path);

    /**
     * Method called when an attribute is encountered
     * @param obj
     * @param path
     * @param property  
     */
    protected abstract void handleAttribute(final DomainObject obj, final String path, final Property property);

    /**
     * Method called when the processing of a role starts
     * @param obj
     * @param path
     * @param property  
     */
    protected abstract void beginRole(DomainObject obj, String path, Property property);

    /**
     * Method called when the procesing of a role completes
     * @param obj
     * @param path
     * @param property  
     */
    protected abstract void endRole(DomainObject obj, String path, Property property);

    /**
     * Method called when the processing of an association starts
     * @param obj
     * @param path
     * @param property  
     */
    protected abstract void beginAssociation(DomainObject obj, String path, Property property);

    /**
     * Method called when the procesing of an association completes
     * @param obj
     * @param path
     * @param property  
     */
    protected abstract void endAssociation(DomainObject obj, String path, Property property);

//    protected String appendToPath(String path, final String name) {
//        if (path.endsWith("/" + name)) {            
//            path = path + "+";
//        } else if (!path.endsWith("/" + name + "+")) {            
//            path = path + "/" + name;
//        }
//
//        return path;
//    }
    protected String appendToPath(final String path, final String name) {
        if (path.endsWith("/" + name)) {
            return path.concat("+");
        } else if (!path.endsWith("/" + name + "+")) {
            return path.concat("/").concat(name);
        }

        return path;
    }

    protected String nameFromPath(final String path) {
        final int index = path.lastIndexOf('/');
        Assert.isTrue(index >= 0, "Path starts with /");

        if (path.endsWith("+")) {
            return path.substring(index + 1, path.length() - 1);
        } else {
            return path.substring(index + 1);
        }
    }

    protected String parentFromPath(final String path) {
        final int index = path.lastIndexOf('/');
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

        public AdapterKey(final ObjectType type, final String context) {
            Assert.exists(type, ObjectType.class);
            Assert.exists(context, String.class);
            m_type = type;
            m_context = context;
        }

        @Override
        public boolean equals(final Object object) {
            if (object instanceof AdapterKey) {
                final AdapterKey key = (AdapterKey) object;
                return key.m_type.equals(m_type) && key.m_context.equals(m_context);
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
     * this is simply a subclass since DomainObject is abstract but we don't have any other domain object to use.
     */
    protected class LinkDomainObject extends DomainObject {

        public LinkDomainObject(final DataObject object) {
            super(object);
        }

        @Override
        public Object get(final String attr) {
            return super.get(attr);
        }

    }
}
