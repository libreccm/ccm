/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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

import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ImageAsset;
import com.arsdigita.util.Assert;
import java.math.BigDecimal;


/**
 * This content type represents an BaseContact
 *
 */
public class BaseContact extends ContentPage {

    /** PDL property names */

    /** Data object type for tihs domain object */
    public static final String BASE_DATA_OBJECT_TYPE
        = "com.arsdigita.cms.contenttypes.Organization";

    public BaseContact () {
        super(BASE_DATA_OBJECT_TYPE);
    }

    public BaseContact ( BigDecimal id )
            throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public BaseContact ( OID id )
            throws DataObjectNotFoundException {
        super(id);
    }

    public BaseContact ( DataObject obj ) {
        super(obj);
    }

    public BaseContact ( String type ) {
        super(type);
    }

    public void beforeSave() {
        super.beforeSave();
        
        Assert.exists(getContentType(), ContentType.class);
    }


    ///////////////////////////////////////////////////////////////
    // accessors

/*
    public String getLink () {
        return (String)get(LINK);
    }

    public void setLink ( String link ) {
        set(LINK, link);
    }
*/


/*
    public BigDecimal getImageID() {
        return (BigDecimal)get(IMAGE_ID);
    }

    public ImageAsset getImage() {
        DataObject obj = (DataObject)get(IMAGE);
        if ( obj == null ) {
            return null;
        }
        return new ImageAsset(obj);
    }

    public void setImage(ImageAsset image) {
        if (image != null)
            image.setMaster(this);
        setAssociation(IMAGE,image);
    }


    public void delete() {
        ImageAsset image = getImage();
        if (image != null) {
            setAssociation(IMAGE, null);
            save();
            image.delete();
        }

        super.delete();
    }
*/
}
