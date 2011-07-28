package com.arsdigita.cms.contenttypes;

import com.arsdigita.cms.ContentPage;
import com.arsdigita.domain.DataObjectNotFoundException;
import java.math.BigDecimal;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.util.Assert;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class PublicPersonalProfile extends ContentPage {

    public static final String OWNER = "owner";
    public static final String PROFILE_URL = "profileUrl";   
    public static final String LINK_LIST_NAME = "publicPersonalProfileNavItems";
    public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.cms.contenttypes.PublicPersonalProfile";

    public PublicPersonalProfile() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    public PublicPersonalProfile(BigDecimal id)
            throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public PublicPersonalProfile(OID oid)
            throws DataObjectNotFoundException {
        super(oid);
    }

    public PublicPersonalProfile(DataObject dobj) {
        super(dobj);
    }

    public PublicPersonalProfile(String type) {
        super(type);
    }

    public GenericPerson getOwner() {
        final DataCollection collection = (DataCollection) get(OWNER);

        if (0 == collection.size()) {
            return null;
        } else {
            DataObject dobj;

            collection.next();
            dobj = collection.getDataObject();
            collection.close();

            return (GenericPerson) DomainObjectFactory.newInstance(dobj);
        }
    }

    public void setOwner(GenericPerson owner) {
        GenericPerson oldOwner;

        oldOwner = getOwner();
        if (oldOwner != null) {
            remove(OWNER, oldOwner);
        }

        if (null != owner) {
            Assert.exists(owner, GenericPerson.class);
            add(OWNER, owner);
        }
    }

    public String getProfileUrl() {
        return (String) get(PROFILE_URL);
    }

    public void setProfileUrl(String profileUrl) {
        set(PROFILE_URL, profileUrl);
    }
}
