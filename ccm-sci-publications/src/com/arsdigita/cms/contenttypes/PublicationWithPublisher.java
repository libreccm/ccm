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
public class PublicationWithPublisher extends Publication {

    public final static String ISBN = "isbn";
    public final static String PUBLISHER = "publisher";    
    public final static String BASE_DATA_OBJECT_TYPE =
                                "com.arsdigita.cms.contenttypes.PublicationWithPublisher";

    public PublicationWithPublisher() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    public PublicationWithPublisher(BigDecimal id)
            throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public PublicationWithPublisher(OID oid)
            throws DataObjectNotFoundException {
        super(oid);
    }

    public PublicationWithPublisher(DataObject obj) {
        super(obj);
    }

    public PublicationWithPublisher(String type) {
        super(type);
    }

    public String getISBN() {
        return (String) get(ISBN);
    }

    public void setISBN(String isbn) {
        set(ISBN, isbn);
    }

    public Publisher getPublisher() {
        return (Publisher) get(PUBLISHER);
    }

    public void setPublisher(Publisher publisher) {
        set(PUBLISHER, publisher);
    }
}
