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
package com.arsdigita.cms.installer;

import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.DataQueryDataCollectionAdapter;
import com.arsdigita.cms.ItemCollection;
import com.arsdigita.cms.ContentItem;

// From Initializer.java:
// Update master object if upgrading from old versioning
// XXX: shouldn't we just gut this section (and
// VersioningUpgrader)? It is an upgrade fix from 5.1 or
// earlier, and relying on VersionedACSObject is
// deprecated
// (probably written  by Michael Pih (pihman@arsdigita.com) )

/**
 * VersioningUpgrader
 *
 * @author <a href="mailto:jorris@redhat.com">Jon Orris</a>
 * @version $Revision: #5 $ $DateTime: 2004/08/17 23:15:09 $
 */
final class VersioningUpgrader {

    private static final String QUERY_TOP_ITEMS =
        "com.arsdigita.cms.topLevelItems";
    private static final String QUERY_ITEM = "item";

    /**
     * Update the master object for all the content items, by assuming
     * that all items whose parent is a folder are master objects.
     */
    static void updateMasterObject() {
        DataQuery dq = SessionManager.getSession().retrieveQuery(QUERY_TOP_ITEMS);
        ItemCollection c = new ItemCollection
            (new DataQueryDataCollectionAdapter(dq, QUERY_ITEM));

        try {
            while (c.next()) {
                ContentItem item = c.getContentItem();
                item.autoPropagateMaster(item);
            }
        } finally {
            c.close();
        }
    }

}
