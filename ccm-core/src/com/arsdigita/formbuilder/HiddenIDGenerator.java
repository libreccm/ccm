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
package com.arsdigita.formbuilder;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.db.Sequences;
import com.arsdigita.formbuilder.PersistentHidden;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.web.Web;

import java.math.BigDecimal;
import java.sql.SQLException;
import javax.servlet.http.HttpServletRequest;

public class HiddenIDGenerator extends PersistentHidden {
    private RequestLocal m_value = new RequestLocal();

    private static final String FORMS_UNIQUE_ID = "forms_unique_id_seq";

    public static final String BASE_DATA_OBJECT_TYPE =
        PersistentHidden.BASE_DATA_OBJECT_TYPE;

    public HiddenIDGenerator() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    public HiddenIDGenerator(String typeName) {
        super(typeName);
    }

    public HiddenIDGenerator(ObjectType type) {
        super(type);
    }

    public HiddenIDGenerator(DataObject obj) {
        super(obj);
    }

    public HiddenIDGenerator(BigDecimal id)
        throws DataObjectNotFoundException {

        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public HiddenIDGenerator(OID oid)
        throws DataObjectNotFoundException {

        super(oid);
    }

    public Object getValue(FormData data) {
        HttpServletRequest req = Web.getRequest();
        BigDecimal id = (BigDecimal) req.getAttribute(FORMS_UNIQUE_ID);
        if (null != id) return id.toString();

        try {
            id = Sequences.getNextValue("forms_unique_id_seq");
        } catch (SQLException ex) {
            throw new UncheckedWrapperException(ex);
        }
        req.setAttribute(FORMS_UNIQUE_ID, id);

        return id.toString();
    }
}
