/*
 * Copyright (c) 2010 Jens Pelzetter
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
package com.arsdigita.cms.contenttypes;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import java.math.BigDecimal;

/**
 * A concrete class extending {@link GenericPerson}. Does not add any new
 * fields, it just makes the internal type <code>GenericPerson</code> usable
 * without any other modules.
 *
 * @author Jens Pelzetter
 */
public class SciAuthor extends GenericPerson {

    public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.cms.contenttypes.SciAuthor";

    public SciAuthor() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    public SciAuthor(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public SciAuthor(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    public SciAuthor(DataObject dobj) {
            super(dobj);
    }

    public SciAuthor(String type) {
        super(type);
    }
}
