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
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;

/**
 * A mime type for images.
 *
 * @author Stanislav Freidin (sfreidin@arsdigita.com)
 * @version $Revision: #7 $ $DateTime: 2004/08/16 18:10:38 $
 */
public class ImageMimeType extends MimeType {
    public static final String versionId = "$Id: ImageMimeType.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.cms.ImageMimeType";

    public static final String SIZER_CLASS = "sizerClass";

    public static final String MIME_IMAGE_JPEG = "image/jpeg";

    /**
     * Load an existing <code>ImageMimeType</code>.
     */
    public ImageMimeType(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    /**
     * Construct a new <code>ImageMimeType</code> from the given {@link DataObject}.
     * All subclasses must implement this constructor.
     */
    public ImageMimeType(DataObject obj) {
        super(obj);
    }

    /**
     * Construct a new <code>MimeType</code> with a given object type.
     * All subclasses must implement this constructor.
     */
    public ImageMimeType(String type, String mimeType) {
        super(type, mimeType);
    }

    /**
     * Return the base data object type for this mime-type
     */
    public String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    /**
     * Return the name of the image sizer class for this mime-type.
     * The image sizer class can be used to guess the width and height
     * (in pixels) of an image. The class will be a subclass of
     * <code>com.arsdigita.cms.image.ImageSizer</code>
     *
     * @return the name of the class which handles image sizing for
     *   this mime type on success, null if the mime type is not an
     *   image or if its size cannot be automatically guessed
     */
    public String getImageSizer() {
        return (String)get(SIZER_CLASS);
    }

    /**
     * Set the name of the image sizer class for this mime type.
     *
     * @param imageSizerClassName the name of the Java class which will
     *   handle image sizing for this mime type
     * @see #getImageSizer
     */
    public void setImageSizer(String imageSizerClassName) {
        set(SIZER_CLASS, imageSizerClassName);
    }

    /**
     * Get all image mime-types in the system
     */
    public static MimeTypeCollection getAllImageMimeTypes() {
        DataCollection da = SessionManager.getSession().retrieve
            (BASE_DATA_OBJECT_TYPE);
        return new MimeTypeCollection(da);
    }

}
