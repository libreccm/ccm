/*
 * Copyright (c) 2010 Jens Pelzetter,
 * for the Center of Social Politics of the University of Bremen
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
 *
 * @author Jens Pelzetter
 */
public abstract class UnPublished extends Publication {

    public static final String PLACE = "place";
    public static final String ORGANIZATION = "organization";
    public static final String NUMBER = "number";
    public static final String NUMBER_OF_PAGES = "numberOfPages";
    public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.cms.contenttypes.UnPublished";

    public UnPublished() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    public UnPublished(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public UnPublished(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    public UnPublished(DataObject dataObject) {
        super(dataObject);
    }

    public UnPublished(String type) {
        super(type);
    }

    public String getPlace() {
        return (String) get(PLACE);
    }

    public void setPlace(String place) {
        set(PLACE, place);
    }

    public GenericOrganizationalUnit getOrganization() {
        return (GenericOrganizationalUnit) get(ORGANIZATION);
    }

    public void setOrganization(GenericOrganizationalUnit orga) {
        set(ORGANIZATION, orga);
    }

    public String getNumber() {
        return (String) get(NUMBER);
    }

    public void setNumber(String number) {
        set(NUMBER, number);
    }

    public Integer getNumberOfPages() {
        return (Integer) get(NUMBER_OF_PAGES);
    }

    public void setNumberOfPages(Integer numberOfPages) {
        set(NUMBER_OF_PAGES, numberOfPages);
    }
}
