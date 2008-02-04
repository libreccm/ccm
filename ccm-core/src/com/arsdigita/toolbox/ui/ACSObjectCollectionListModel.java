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
package com.arsdigita.toolbox.ui;

import com.arsdigita.bebop.list.ListModel;
import com.arsdigita.kernel.ACSObjectCollection;

public class ACSObjectCollectionListModel implements ListModel {

    ACSObjectCollection m_collection;

    public ACSObjectCollectionListModel(ACSObjectCollection collection) {
        m_collection = collection;
    }

    protected final ACSObjectCollection getCollection() {
        return m_collection;
    }

    public boolean next() {
        return (m_collection.next());
    }

    public Object getElement() {
        return m_collection.getDisplayName();
    }

    public String getKey() {
        return m_collection.getID().toString();
    }
}
