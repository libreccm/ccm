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
package com.arsdigita.cms.contenttypes;

import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.cms.ContentType;
import com.arsdigita.util.Assert;
import java.math.BigDecimal;


/**
 * This content type represents an GenericContact
 *
 */
public class Contact extends GenericContact {

    /** Data object type for tihs domain object */
    public static final String BASE_DATA_OBJECT_TYPE
        = "com.arsdigita.cms.contenttypes.Contact";

    public Contact() {
        super(BASE_DATA_OBJECT_TYPE);
    }

    public Contact(BigDecimal id)
            throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public Contact(OID id)
            throws DataObjectNotFoundException {
        super(id);
    }

    public Contact(DataObject obj) {
        super(obj);
    }

    public Contact(String type) {
        super(type);
    }

    @Override
    public void beforeSave() {
        super.beforeSave();
        
        Assert.exists(getContentType(), ContentType.class);
    }

    /**
     * Retrieves the current configuration
     */
//    public static final BaseContactConfig getConfig() {
//        return s_config;
//    }
    

    ///////////////////////////////////////////////////////////////
    // accessors
}
