/*
 * Copyright (c) 2011 Jens Pelzetter
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

import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.contenttypes.GenericPersonBundle;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.Assert;
import java.math.BigDecimal;

/**
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class PublicPersonalProfileBundle extends ContentBundle {

    public final static String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.cms.contenttypes.PublicPersonalProfileBundle";
    public static final String OWNER = "owner";

    public PublicPersonalProfileBundle(final ContentItem primary) {
        super(BASE_DATA_OBJECT_TYPE);

        Assert.exists(primary, ContentItem.class);

        setDefaultLanguage(primary.getLanguage());
        setContentType(primary.getContentType());
        addInstance(primary);

        super.setName(primary.getName());
    }

    public PublicPersonalProfileBundle(final OID oid) throws
            DataObjectNotFoundException {
        super(oid);
    }

    public PublicPersonalProfileBundle(final BigDecimal id) throws
            DataObjectNotFoundException {
        super(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public PublicPersonalProfileBundle(final DataObject dobj) {
        super(dobj);
    }

    public PublicPersonalProfileBundle(final String type) {
        super(type);
    }

    public GenericPersonBundle getOwner() {
        final DataCollection collection = (DataCollection) get(OWNER);

        if (0 == collection.size()) {
            return null;
        } else {
            DataObject dobj;

            collection.next();
            dobj = collection.getDataObject();
            collection.close();
           
            return (GenericPersonBundle) DomainObjectFactory.newInstance(dobj);
        }
    }

    public void setOwner(final GenericPerson owner) {
        final GenericPersonBundle oldOwner = getOwner();
        if (oldOwner != null) {
            remove(OWNER, oldOwner);
        }

        if (null != owner) {
            Assert.exists(owner, GenericPerson.class);
            add(OWNER, owner.getContentBundle());
        }
    }
}
