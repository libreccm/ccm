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

import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.DomainService;
import com.arsdigita.domain.InstantiatorNotFoundException;
import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.persistence.DataAssociationCursor;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.persistence.metadata.Property;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Recursively updates the "version" attribute of a content item to a
 * new value. For internal use only; users should never have to know
 * or care about this class.
 *
 * @author <a href="mailto:sfreidin@arsdigita.com">Stanislav Freidin</a>
 * @version $Id: VersionUpdater.java 287 2005-02-22 00:29:02Z sskracic $
 */

class VersionUpdater extends DomainService {

    public static final String versionId = "$Id: VersionUpdater.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/17 23:15:09 $";

    private static org.apache.log4j.Logger s_log =
        org.apache.log4j.Logger.getLogger(VersionUpdater.class);

    // A map of visited OIDs. Helps prevent infinite recursion.
    // all objects copied so far.
    protected Set m_visited;

    // The new version tag for the items
    String m_newVersion;

    /**
     * Construct a new <code>VersionUpdater</code>
     */
    protected VersionUpdater(String newVersion) {
        m_visited = new HashSet();
        m_newVersion = newVersion;
    }

    /**
     * Recursively set the version of a ContentItem
     *
     * @param item the item
     */
    public void updateItemVersion(ContentItem item) {

        // This seems wasteful, but in fact we need to specialize
        // the item anyway, in order to cache the right OID
        updateSingleObject(getDataObject(item));
    }

    /**
     * Recursively traverse the object's children and set their versions
     * to the right thing
     *
     * @param src the parent object
     */
    protected void updateChildren(DataObject src) {
        ObjectType type = src.getOID().getObjectType();

        for(Iterator i = type.getProperties(); i.hasNext(); ) {
            Property prop = (Property) i.next();

            // Skip scalars
            if (!prop.isRole()) continue;

            // Skip non-composites
            if (!prop.isComponent()) continue;

            String propName = prop.getName();

            // Get the value of the property, skip nulls
            Object data = src.get(propName);
            if (data == null)
                continue;

            if(data instanceof DataObject) {
                updateSingleObject((DataObject)data);
            } else if (data instanceof DataAssociation) {
                // Association - iterate over it
                DataAssociationCursor daCursor =
                    ((DataAssociation)data).getDataAssociationCursor();

                while (daCursor.next()) {
                    updateSingleObject(daCursor.getDataObject());
                }
            } else {
                throw new IllegalStateException("Warning: unsupported property type " +
                                                data.getClass().getName());
            }
        }
    }

    /**
     * Update the version of a single object, if it happens to be
     * a content item
     */
    protected void updateSingleObject(DataObject src) {
        // Prevent infinite recursion
        if(m_visited.contains(src.getOID()))
            return;

        m_visited.add(src.getOID());

        ContentItem item = null;

        try {
            DomainObject obj = DomainObjectFactory.newInstance(src);
            if(obj instanceof ContentItem)
                item = (ContentItem)obj;
        } catch (InstantiatorNotFoundException e) {
            // do nothing
        }

        if(item != null) {
            // Prevent unnecessary updates
            if(! (m_newVersion.equals(item.getVersion()))) {

                // Update the current item's version; this will
                // also have the effect of setting/unsetting the live version
                // for the master
                item.setVersion(m_newVersion);
                item.save();
            }
        }

        updateChildren(src);
    }


}
