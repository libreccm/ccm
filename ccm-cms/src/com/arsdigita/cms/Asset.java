/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.mimetypes.MimeType;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import java.io.File;
import java.io.IOException;



/**
 * Assets are extra content associated with a content item.  An image,
 * for instance, may be an asset of a news article.
 *
 * @author Jack Chung
 *
 * @version $Revision: #21 $ $DateTime: 2004/08/17 23:15:09 $
 */
public abstract class Asset extends ContentItem {

    public static final String versionId = "$Id: Asset.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/17 23:15:09 $";

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.cms.Asset";

    public static final String NAME = "name";
    public static final String DESCRIPTION = "description";
    public static final String MIME_TYPE = "mimeType";

    public Asset() {
        super(BASE_DATA_OBJECT_TYPE);
    }

    /**
     * Constructor. The contained <code>DataObject</code> is retrieved
     * from the persistent storage mechanism with an <code>OID</code>
     * specified by <i>oid</i>.
     *
     * @param oid The <code>OID</code> for the retrieved
     * <code>DataObject</code>.
     **/
    public Asset(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    public Asset(DataObject obj) {
        super(obj);
    }

    protected Asset(String type) {
        super(type);
    }

    /**
     * @return the base PDL object type for this item. Child classes should
     *  override this method to return the correct value
     */
    public String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    public String getDescription() {
        return (String) get(DESCRIPTION);
    }

    /**
     * A description of what this asset represents. For example, an
     * image might have the caption "Happy kids." The description might
     * say "This is a photo of some happy day-care children playing at
     * the park by the riverbank." For text assets, the description
     * could be used to store a short summary of that asset. For
     * example, if you upload a PDF of a thesis, the description could
     * be used to store an abstract.
     *
     * @param value The description to store
     **/
    public void setDescription(String value) {
        set(DESCRIPTION, value);
    }

    public MimeType getMimeType() {
        DataObject type = (DataObject) get(MIME_TYPE);
        if (type == null) {
            return null;
        } else {
            return new MimeType(type);
        }
    }

    public void setMimeType(MimeType value) {
        setAssociation(MIME_TYPE, value);
    }

    /**
     * Write the content of this asset to a file.
     *
     * @param file  The file on the server to write to.
     */
    public abstract void writeToFile(File file)
        throws IOException;

}
