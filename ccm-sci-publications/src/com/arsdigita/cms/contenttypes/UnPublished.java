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
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.Assert;
import java.math.BigDecimal;
import org.apache.log4j.Logger;

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
        DataCollection collection;

        collection = (DataCollection) get(ORGANIZATION);

        if (0 == collection.size()) {
            return null;
        } else {
            DataObject dobj;

            collection.next();
            dobj = collection.getDataObject();
            collection.close();

            return (GenericOrganizationalUnit) DomainObjectFactory.newInstance(
                    dobj);
        }
    }

    public void setOrganization(final GenericOrganizationalUnit orga) {
        GenericOrganizationalUnit oldOrga;

        oldOrga = getOrganization();

        if (oldOrga != null) {
            remove(ORGANIZATION, oldOrga);
        }

        if (null != orga) {
            Assert.exists(orga, GenericOrganizationalUnit.class);            
            DataObject link = add(ORGANIZATION, orga);
            link.set("orgaOrder", 1);
            link.save();
        }
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
