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
package com.arsdigita.kernel;


import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.persistence.metadata.Property;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Metadata Utilities:
 * Package-private class containing utility/convenience methods for
 * accessing persistence metadata.
 **/
class MDUtil {

    // map used to cache metadata lookups
    //   - The key is an ObjectType.
    //   - The value is a Property that is the composite role for
    //     the ObjectType.
    private static HashMap s_composites = new HashMap();

    static final String PACKAGE_INSTANCE = "packageInstance";

    static Property getCompositeRole(ObjectType type) {
        if (s_composites.containsKey(type)) {
            return (Property) s_composites.get(type);
        }
        Iterator properties = type.getProperties();
        while (properties.hasNext()) {
            Property prop = (Property) properties.next();
            if (prop.isRole() &&
                prop.isRequired() &&
                !prop.isCollection()) {

                // The property is a role of multiplcity 1..1.  Now we
                // have to figure out if it is a composite role.

                if (prop.isComposite() &&
                    prop.getType() instanceof ObjectType &&
                    ((ObjectType)prop.getType())
                    .isSubtypeOf(ACSObject.BASE_DATA_OBJECT_TYPE)) {
                    s_composites.put(type, prop);
                    return prop;
                }
            }
        }
        s_composites.put(type, null);
        return null;
    }


    static boolean hasPackageInstanceRole(ObjectType o) {
        Property p = o.getProperty(PACKAGE_INSTANCE);
        return
            p != null &&
            p.isRole() &&
            ((ObjectType) p.getType()).isSubtypeOf(
                getType(PackageInstance.BASE_DATA_OBJECT_TYPE));
    }


    /**
     * convenience wrapper around
     * SessionManager.getMetadataRoot().getObjectType()
     **/
    static ObjectType getType(String typeName) {
        return SessionManager.getMetadataRoot().getObjectType(typeName);
    }
}
