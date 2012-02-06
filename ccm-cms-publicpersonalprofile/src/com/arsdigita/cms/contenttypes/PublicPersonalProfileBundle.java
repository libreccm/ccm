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

            final GenericPersonBundle bundle =
                                      (GenericPersonBundle) DomainObjectFactory.
                    newInstance(dobj);

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
