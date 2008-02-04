/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.search;

import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.persistence.metadata.ObjectTypeRegistry;


public class MetadataProviderRegistry {
    
    private static ObjectTypeRegistry s_registry = new ObjectTypeRegistry();
    
    /**
     * Registers an adapter against an object type
     * @param type the object type name
     * @param adapter the metadata provider implementation
     */
    public static void registerAdapter(String type,
                                       MetadataProvider adapter) {
        s_registry.registerAdapter(type, adapter);
    }
    
    /**
     * Unregisters an adapter against an object type
     * @param type the object type name
     */
    public static void unregisterAdapter(String type) {
        s_registry.unregisterAdapter(type);
    }

    /**
     * Registers an adapter against an object type
     * @param type the object type
     * @param adapter the metadata provider implementation
     */
    public static void registerAdapter(ObjectType type,
                                       MetadataProvider adapter) {
        s_registry.registerAdapter(type, adapter);
    }
    
    /**
     * Unregisters an adapter against an object type
     * @param type the object type
     */
    public static void unregisterAdapter(ObjectType type) {
        s_registry.unregisterAdapter(type);
    }
    
    /**
     * Returns the adapter registered against an object
     * type
     * @param type the object type name
     * @return the metadata provider adapter, or null
     */
    public static MetadataProvider getAdapter(String type) {
        return (MetadataProvider)s_registry.getAdapter(type);
    }

    /**
     * Returns the adapter registered against an object
     * type
     * @param type the object type
     * @return the metadata provider adapter, or null
     */
    public static MetadataProvider getAdapter(ObjectType type) {
        return (MetadataProvider)s_registry.getAdapter(type);
    }

    /**
     * Searches up  the object type hierarchy until
     * a metadata provider adapter is found
     *
     * @param type the object type name
     * @return the metadata provider adapter, or null
     */
    public static MetadataProvider findAdapter(String type) {
        return (MetadataProvider)s_registry.findAdapter(type);
    }

    /**
     * Searches up  the object type hierarchy until
     * a metadata provider adapter is found
     *
     * @param type the object type
     * @return the metadata provider adapter, or null
     */
    public static MetadataProvider findAdapter(ObjectType type) {
        return (MetadataProvider)s_registry.findAdapter(type);
    }

}
