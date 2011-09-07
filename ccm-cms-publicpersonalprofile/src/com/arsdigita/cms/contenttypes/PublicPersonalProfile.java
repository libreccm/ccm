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

import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ExtraXMLGenerator;
import com.arsdigita.cms.publicpersonalprofile.ContentGenerator;
import com.arsdigita.domain.DataObjectNotFoundException;
import java.math.BigDecimal;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.util.Assert;
import java.util.List;

/**
 * A content type representing a profile (a personal homepage) of a person.
 * On the start page of the the profile the contact data and a photo (if any)
 * associated with the owner of the profile are shown. Other content items can
 * be associated with the profile. They are shown in special navigation. Also,
 * there special items for the profile which not backed by a content item. 
 * Instead, they are backed by a Java class which implements the 
 * {@link ContentGenerator} interface. A profile can either be shown as 
 * a standalone page (similar the separate category system) or embedded into
 * the normal site.
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

    public PublicPersonalProfile(final BigDecimal id)
            throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public PublicPersonalProfile(final OID oid)
            throws DataObjectNotFoundException {
        super(oid);
    }

    public PublicPersonalProfile(final DataObject dobj) {
        super(dobj);
    }

    public PublicPersonalProfile(final String type) {
        super(type);
    }

    /**     
     * 
     * @return The owner of the profile.
     */
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

    /**
     * Sets the owner of the profile.
     * 
     * @param owner 
     */
    public void setOwner(final GenericPerson owner) {
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

    /**
     * 
     * @return The URL fragment of the profile used to build the URL of the 
     * profile.
     
     */
    public String getProfileUrl() {
        return (String) get(PROFILE_URL);
    }

    public void setProfileUrl(String profileUrl) {
        set(PROFILE_URL, profileUrl);
    }
    
    /**
     * The profile has an extra XML Generator, which is primarily to render
     * the items and the navigation of the profile for the embedded view.
     * 
     * @return 
     */
    @Override
    public List<ExtraXMLGenerator> getExtraXMLGenerators() {
        final List<ExtraXMLGenerator> generators = super.getExtraXMLGenerators();
        
        generators.add(new PublicPersonalProfileExtraXmlGenerator());
                
        return generators;
    }
}
