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
package com.arsdigita.mimetypes;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;


/**
 * Used to initialize the table cms_mime_extensions (which contains
 * mime type extensions and the corresponding mime type).
 *
 * @author Jeff Teeters (teeters@arsdigita.com)
 *
 * @version $Revision: #8 $ $DateTime: 2004/08/16 18:10:38 $
 */
public class MimeTypeExtension extends DomainObject {

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.cms.MimeTypeExtension";

    public static final String MIME_TYPE = "mimeType";
    public static final String FILE_EXTENSION = "fileExtension";

    public static MimeTypeExtension create(String fileExtension, 
                                           String mimeType) {
        MimeType mimeTypeObject = MimeType.loadMimeType(mimeType);
        if (mimeTypeObject == null) {
            throw new DataObjectNotFoundException
                ("Unable to locate mime type of " + mimeType);
        }
        return create(fileExtension, mimeTypeObject);
    }

    public static MimeTypeExtension create(String fileExtension, 
                                           MimeType mimeType) {
        MimeTypeExtension ext = new MimeTypeExtension();
        ext.setFileExtension(fileExtension);
        ext.setMimeTypeObject(mimeType);
        return ext;
    }

    /**
     *  This retrieves the MimeTypeExtension for the given object
     *  or returns null if none exists
     */
    public static MimeTypeExtension retrieve(String fileExtension) {
        try { 
            return new MimeTypeExtension(new OID(BASE_DATA_OBJECT_TYPE, 
                                                 fileExtension.toLowerCase()));
        } catch (DataObjectNotFoundException e) {
            // there is nothing specified for this mime type
            return null;
        }
    }

    protected MimeTypeExtension() {
        super(BASE_DATA_OBJECT_TYPE);
    }

    public MimeTypeExtension(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    public MimeTypeExtension(DataObject obj) {
        super(obj);
    }

    protected MimeTypeExtension(String type) {
        super(type);
    }

    public MimeType getMimeTypeObject() {
        DataObject object = (DataObject)get(MIME_TYPE);
        if (object != null) {
            return new MimeType(object).specialize();
        } else {
            return null;
        }
    }

    public void setMimeTypeObject(MimeType object) {
        set(MIME_TYPE, object);
    }

    /**
     *  @deprecated use getMimeTypeObject().getMimeType()
     */
    public String getMimeType() {
        return getMimeTypeObject().getMimeType();
    }

    /**
     *  @deprecated use getMimeTypeObject
     */
    public void setMimeType(String value) {
        MimeType mimeType = MimeType.loadMimeType(value);
        setMimeTypeObject(mimeType);
    }

    public String getFileExtension() {
        return (String) get(FILE_EXTENSION);
    }

    public void setFileExtension(String value) {
        set(FILE_EXTENSION, value.toLowerCase());
    }
}
