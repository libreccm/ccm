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
package com.arsdigita.search;

import com.arsdigita.domain.DataObjectNotFoundException;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.metadata.ObjectType;


/**
 * @deprecated moved to com.arsdigita.search.intermedia package
 * @see com.arsdigita.search.intermedia.SearchableACSObject
 **/

public abstract class SearchableACSObject 
    extends com.arsdigita.search.intermedia.SearchableACSObject {

    public SearchableACSObject(DataObject SearchableACSObjectData) {
        super(SearchableACSObjectData);
    }
    public SearchableACSObject(String typeName) {
        super(typeName);
    }
    public SearchableACSObject(ObjectType type) {
        super(type);
    }
    public SearchableACSObject(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }
}
