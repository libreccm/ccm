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
 */

package com.arsdigita.aplaws;

import com.arsdigita.util.Assert;

import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.persistence.metadata.Property;
import com.arsdigita.persistence.metadata.MetadataRoot;

import java.util.Iterator;
import java.util.HashMap;

import org.apache.log4j.Logger;

// XXX this class is pretty similar to DomainObjectTraversal
// and it would be nice to figure out a way to let them share
// some of their logic (provided it didn't cripple / obfuscate 
// the API).

// At minimum the process for registering & looking up hierachical
// adapters can be shared. Also the mangling of names / paths.


/**
 * <p>This class provides a general purpose framework for iterating
 * over a domain object's properties, processing attributes and
 * traversing associations as required.</p>
 *
 * <p>Subclasses should implement the startXXX and endXXX methods to
 * provide whatever processing logic they require upon encountering
 * attributes, roles, associations and objects.</p>
 *
 * <p>The {@link com.arsdigita.domain.ObjectTypeTraversalAdapter}
 * provides a means to control which properties are processed and,
 * most importantly, which associations are traversed. When
 * registering an adapter, a 'use context' is supplied allowing
 * different adapters to be used according to the requirements of any
 * implementing subclass. It is recommended that the use context be
 * based on the fully qualified name of the class using
 * ObjectTypeTraversal, e.g.,
 * com.arsdigita.cms.ui.ObjectTypeRenderer.</p>
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
public abstract class ObjectTypeTraversal {

    private static HashMap s_adapters = new HashMap();

    private static final Logger s_log = Logger.getLogger(ObjectTypeTraversal.class);

    /**
     * Registers a traversal adapter for an object type in a given
     * context.
     *
     * @param type the object type whose items will be traversed
     * @param adapter the adapter for controlling object traversal
     * @param context the context in which the adapter should be used
     */
    public static void registerAdapter(ObjectType type,
                                       ObjectTypeTraversalAdapter adapter,
                                       String context) {
        s_adapters.put(new AdapterKey(type, context), adapter);
    }

    /**
     * Unregisteres a traversal adapter for an object type in a
     * given context
     *
     * @param type the object type whose items will be traversed
     * @param context the context in which the adapter should be used
     */
    public static void unregisterAdapter(ObjectType type,
                                         String context) {
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
    public static void registerAdapter(String type,
                                       ObjectTypeTraversalAdapter adapter,
                                       String context) {
        registerAdapter(MetadataRoot.getMetadataRoot().getObjectType(type),
                        adapter,
                        context);
    }

    /**
     * Unregisteres a traversal adapter for an object type in a
     * given context
     *
     * @param type the object type whose items will be traversed
     * @param context the context in which the adapter should be used
     */
    public static void unregisterAdapter(String type,
                                         String context) {
        unregisterAdapter(MetadataRoot.getMetadataRoot().getObjectType(type),
                          context);
    }

    /**
     * Retrieves the traversal adapter for an object type in a given
     * context.
     *
     * @param type the object type to lookup
     * @param context the adapter context
     */
    public static ObjectTypeTraversalAdapter lookupAdapter(ObjectType type,
                                                           String context) {
        return (ObjectTypeTraversalAdapter)s_adapters
            .get(new AdapterKey(type, context));
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
    public static ObjectTypeTraversalAdapter findAdapter(ObjectType type,
                                                         String context) {
        ObjectTypeTraversalAdapter adapter = null;
        while (adapter == null && type != null) {
            adapter = lookupAdapter(type, context);
            type = type.getSupertype();
        }
        return adapter;
    }

    /**
     * Walks over properties of a domain object, invoking
     * methods to handle assoications, roles and attributes.
     *
     * @param obj the domain object to traverse
     * @param context the context for the traversal adapter
     */
    public void walk(String type,
                     String context) {
        walk(MetadataRoot.getMetadataRoot().getObjectType(type),
             context);
    }

    /**
     * Walks over properties of a domain object, invoking
     * methods to handle assoications, roles and attributes.
     *
     * @param obj the domain object to traverse
     * @param context the context for the traversal adapter
     */
    public void walk(ObjectType type,
                     String context) {
        Assert.exists(type, ObjectType.class);

        ObjectTypeTraversalAdapter adapter = findAdapter(type,
                                                         context);
        Assert.exists(adapter, ObjectTypeTraversalAdapter.class);
        walk(adapter, type, "/object");
    }

    private void walk(ObjectTypeTraversalAdapter adapter,
                      ObjectType type,
                      String path) {
        beginObject(type, path);
        
        if (s_log.isInfoEnabled()) {
            s_log.info("Walking " + path + " type: " + type.getQualifiedName());
        }

        for (Iterator i = type.getProperties(); i.hasNext(); ) {
            Property prop = (Property) i.next();
            String propName = prop.getName();

            if (!adapter.processProperty(type,
                                         appendToPath(path, prop.getName()),
                                         prop)) {
                if (s_log.isDebugEnabled()) {
                    s_log.debug("Skipping property " + propName);
                }
                continue;
            }
            if (s_log.isDebugEnabled()) {
                s_log.debug("Process property " + propName + " type " + prop.getType().getQualifiedName());
            }

            if (prop.isAttribute()) {
                handleAttribute(type, path, prop);
            } else {
                if (!prop.isCollection()) {
                    beginRole(type, path, prop);
                    
                    walk(adapter, 
                         (ObjectType)prop.getType(),
                         appendToPath(path, propName));
                    
                    endRole(type, path, prop);
                } else {
                    beginAssociation(type, path, prop);
                    
                    Property roleProp = prop.getAssociatedProperty();

                    walk(adapter, 
                         (ObjectType)prop.getType(),
                         appendToPath(path, propName));
                    
                    endAssociation(type, path, prop);
                } 
            }
        }

        endObject(type, path);
    }


    /**
     * Method called when the processing of an object
     * starts
     */
    protected abstract void beginObject(ObjectType obj,
                                        String path);
    /**
     * Method called when the procesing of an object
     * completes
     */
    protected abstract void endObject(ObjectType obj,
                                      String path);

    /**
     * Method called when an attribute is encountered
     */
    protected abstract void handleAttribute(ObjectType obj,
                                            String path,
                                            Property property);

    /**
     * Method called when the processing of a role
     * starts
     */
    protected abstract void beginRole(ObjectType obj,
                                      String path,
                                      Property property);

    /**
     * Method called when the procesing of a role
     * completes
     */
    protected abstract void endRole(ObjectType obj,
                                    String path,
                                    Property property);

    /**
     * Method called when the processing of an association
     * starts
     */
    protected abstract void beginAssociation(ObjectType obj,
                                             String path,
                                             Property property);

    /**
     * Method called when the procesing of an association
     * completes
     */
    protected abstract void endAssociation(ObjectType obj,
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
            return path.substring(index + 1, path.length() - 2);
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

    private static class AdapterKey {
        private ObjectType m_type;
        private String m_context;

        public AdapterKey(ObjectType type,
                          String context) {
            m_type = type;
            m_context = context;
        }

        public boolean equals(Object o) {
            if (o instanceof AdapterKey) {
                AdapterKey k = (AdapterKey)o;
                return k.m_type.equals(m_type) &&
                    k.m_context.equals(m_context);
            } else {
                return false;
            }
        }

        public int hashCode() {
            return m_type.hashCode() + m_context.hashCode();
        }
    }

}
