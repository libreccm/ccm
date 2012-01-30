package com.arsdigita.cms.contenttypes;

import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentItem;
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
public class GenericContactBundle extends ContentBundle {

    public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.cms.contenttypes.GenericContactBundle";
    public static final String PERSON = "person";

    public GenericContactBundle(final ContentItem primary) {
        super(BASE_DATA_OBJECT_TYPE);

        Assert.exists(primary, ContentItem.class);

        setDefaultLanguage(primary.getLanguage());
        setContentType(primary.getContentType());
        addInstance(primary);

        super.setName(primary.getName());
    }

    public GenericContactBundle(final OID oid) throws
            DataObjectNotFoundException {
        super(oid);
    }

    public GenericContactBundle(final BigDecimal id) throws
            DataObjectNotFoundException {
        super(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public GenericContactBundle(final DataObject dobj) {
        super(dobj);
    }

    public GenericContactBundle(final String type) {
        super(type);
    }

    public GenericPerson getPerson() {
        DataCollection collection;

        collection = (DataCollection) get(PERSON);

        if (collection.size() == 0) {
            return null;
        } else {
            DataObject dobj;

            collection.next();
            dobj = collection.getDataObject();

            // Close Collection to prevent an open ResultSet
            collection.close();

            final GenericPersonBundle bundle =
                                      (GenericPersonBundle) DomainObjectFactory.
                    newInstance(dobj);

            return (GenericPerson) bundle.getPrimaryInstance();
        }
    }

    public void setPerson(GenericPerson person, String contactType) {
        if (getPerson() != null) {
            unsetPerson();
        }

        if (person != null) {
            Assert.exists(person, GenericPerson.class);
            DataObject link = add(PERSON, person.getGenericPersonBundle());
            link.set(GenericPerson.CONTACTS_KEY, contactType);
            link.set(GenericPerson.CONTACTS_ORDER, new BigDecimal(person.
                    getContacts().size()));
            link.save();
        }
    }

    public void unsetPerson() {     
        GenericPerson oldPerson;
        oldPerson = getPerson();
        if (oldPerson != null) {
            remove(PERSON, oldPerson.getGenericPersonBundle());
        }
    }
}
