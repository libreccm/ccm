/*
 * Copyright (C) 2009 Jens Pelzetter, for the Center of Social Politics of the University of Bremen
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

import com.arsdigita.cms.ContentPage;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import java.math.BigDecimal;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
public class OrganizationalUnit extends ContentPage {

    public final static String ORGANIZATIONALUNIT_NAME = "organizationalunitName";
    public final static String ORGANIZATIONALUNIT_DESCRIPTION = "organizationalunitDescription";

    public final static String BASE_DATA_OBJECT_TYPE = "com.arsdigita.cms.contenttypes.OrganizationalUnit";

    public OrganizationalUnit() {
        super(BASE_DATA_OBJECT_TYPE);
    }

    public OrganizationalUnit(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public OrganizationalUnit(OID id) throws DataObjectNotFoundException {
        super(id);
    }

    public OrganizationalUnit(DataObject obj) {
        super(obj);
    }

    public OrganizationalUnit(String type) {
        super(type);
    }

    /* accessors ***************************************************/
    public String getOrganizationalUnitName() {
        return (String)get(ORGANIZATIONALUNIT_NAME);
    }

    public void setOrganizationalUnitName(String name) {
        set(ORGANIZATIONALUNIT_NAME, name);
    }

    public String getOrganizationalUnitDescription() {
        return (String) get(ORGANIZATIONALUNIT_DESCRIPTION);
    }

    public void setOrganizationalUnitDescription(String description) {
        set(ORGANIZATIONALUNIT_DESCRIPTION, description);
    }
}
