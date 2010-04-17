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

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.persistence.OID;
import java.math.BigDecimal;

/**
 * OtherType
 *
 * @author <a href="mailto:jorris@redhat.com">Jon Orris</a>
 * @version $Id: OtherType.java 746 2005-09-02 10:56:35Z sskracic $
 */
public class OtherType extends ACSObject {

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.search.lucene.OtherType";

    public OtherType(BigDecimal id) throws DataObjectNotFoundException {
        super(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public OtherType() {
        super(BASE_DATA_OBJECT_TYPE);
    }

    public String getName() {
        return (String) get("name");
    }

    public void setName(String name) {
        set("name", name);
    }

    public String getText() {
        return (String) get("text");
    }

    public void setText(String text) {
        set("text", text);
    }

    static Adapter adapter() {
        return new StandardAdapter(BASE_DATA_OBJECT_TYPE,
                                   "id",
                                   "name",
                                   null,
                                   new String[] {"text"});
    }

}
