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
package com.arsdigita.versioning;

import com.arsdigita.domain.DomainQuery;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.OID;

// merged versioning

/**
 * TagCollection
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Id: TagCollection.java 287 2005-02-22 00:29:02Z sskracic $
 **/

public class TagCollection extends DomainQuery {

    static final String TAGGED = "taggedOID";
    static final String DESC = "tag";

    private DataCollection m_tags;

    TagCollection(DataCollection tags) {
        super(tags);
        m_tags = tags;
    }

    public Tag getTag() {
        return new Tag((OID) Adapter.deserialize((String) m_tags.get(TAGGED),
                                                 Types.OID),
                       (String) m_tags.get(DESC));
    }

}
