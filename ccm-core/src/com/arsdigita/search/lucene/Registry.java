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
package com.arsdigita.search.lucene;

import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.persistence.metadata.MetadataRoot;

import java.util.Map;
import java.util.HashMap;


/**
 * Registry
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #4 $ $Date: 2004/08/16 $
 **/

public class Registry {

    public final static String versionId = "$Id: Registry.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private static final Registry REGISTRY = new Registry();

    public static final Registry getInstance() {
        return REGISTRY;
    }

    private Map m_adapters = new HashMap();

    // Will change to private when compatability pacage is removed
    protected Registry() {}

    public boolean hasAdapter(ObjectType type) {
        return getAdapter(type) != null;
    }

    public Adapter getAdapter(ObjectType type) {
        Adapter result = (Adapter) m_adapters.get(type.getQualifiedName());

        if (result == null && type.getSupertype() != null) {
            result = getAdapter(type.getSupertype());
        }

        return result;
    }

    public void addAdapter(ObjectType type, Adapter adapter) {
        m_adapters.put(type.getQualifiedName(), adapter);
    }

    public void addAdapter(String type, Adapter adapter) {
        addAdapter(MetadataRoot.getMetadataRoot().getObjectType(type),
                   adapter);
    }

}
