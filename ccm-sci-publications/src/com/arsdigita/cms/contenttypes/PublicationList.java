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

import java.math.BigDecimal;

import com.arsdigita.cms.ContentPage;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.Assert;

/**
 * This content type is used to create a simple list of {@link Publication}
 * items. This is only a temporally solution. This content type be replaced by
 * a more flexible portlet or application soon.
 *
 * @author Jens Pelzetter
 */
public class PublicationList extends ContentPage {

    public static final String DESCRIPTION = "description";
    public static final String PUBLICATIONS = "publications";
    public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.cms.contenttypes.PublicationList";

    public PublicationList() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    public PublicationList(BigDecimal id)
            throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public PublicationList(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    public PublicationList(DataObject obj) {
        super(obj);
    }

    public PublicationList(String type) {
        super(type);
    }

    public String getDescription() {
        return (String) get(DESCRIPTION);
    }

    public void setDescription(String desc) {
        set(DESCRIPTION, desc);
    }

    public PublicationCollection getPublications() {
        return new PublicationCollection((DataCollection) get(PUBLICATIONS));
    }

    public void addPublication(Publication publication) {
        Assert.exists (publication, Publication.class);

        add(PUBLICATIONS, publication);
    }

    public void removePublication(Publication publication) {
        Assert.exists (publication, Publication.class);

        remove(PUBLICATIONS, publication);
    }

    public boolean hasPublications() {
        return !getPublications().isEmpty();
    }
}
