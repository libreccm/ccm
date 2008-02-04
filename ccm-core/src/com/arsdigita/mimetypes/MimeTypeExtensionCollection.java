/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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

import com.arsdigita.persistence.DataCollection;
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.persistence.DataObject;

/**
 * This class contains a collection of MimeTypeExtensions.
 *
 * @author <a href="mailto:flattop@arsdigita.com">Jack Chung</a>
 * @version 1.0
 * @see com.arsdigita.domain.DomainCollection
 * @see com.arsdigita.persistence.DataCollection
 */
public class MimeTypeExtensionCollection extends DomainCollection {

    public static final String versionId = "$Id: MimeTypeExtensionCollection.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    /**
     * Constructor.
     *
     **/
    public MimeTypeExtensionCollection(DataCollection dataCollection) {
        super(dataCollection);
    }


    /**
     * Returns a <code>DomainObject</code> for the current position in
     * the collection.
     *
     **/
    public DomainObject getDomainObject() {
        return new MimeType(m_dataCollection.getDataObject());
    }

    /**
     * Returns a <code>MimeType</code> for the current position in
     * the collection.
     *
     **/
    public MimeTypeExtension getMimeTypeExtension() {
        return (MimeTypeExtension)getDomainObject();
    }

    public MimeType getMimeType() {
        DataObject object = (DataObject)get(MimeTypeExtension.MIME_TYPE);
        if (object != null) {
            return new MimeType(object);
        } else {
            return null;
        }
    }

    public String fileExtension() {
        return (String)get(MimeTypeExtension.FILE_EXTENSION);
    }
}
