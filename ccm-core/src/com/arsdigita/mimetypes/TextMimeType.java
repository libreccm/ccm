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
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;

/**
 * A textual mime-type
 *
 * @author Stanislav Freidin (sfreidin@arsdigita.com)
 *
 * @version $Revision: #8 $ $DateTime: 2004/08/16 18:10:38 $
 */
public class TextMimeType extends MimeType {

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.cms.TextMimeType";

    public static final String IS_INSO = "isInso";

    public static final String TEXT_PREFIX = "text";

    public static final String MIME_TEXT_HTML = "text/html";

    /**
     * Load an existing <code>TextMimeType</code>.
     */
    public TextMimeType(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    /**
     * Construct a new <code>TextMimeType</code> from the given {@link DataObject}.
     * All subclasses must implement this constructor.
     */
    public TextMimeType(DataObject obj) {
        super(obj);
    }

    /**
     * Construct a new <code>MimeType</code> with a given object type.
     * All subclasses must implement this constructor.
     */
    public TextMimeType(String type, String mimeType) {
        super(type, mimeType);
    }

    /**
     * Return the base data object type for this mime-type
     */
    public String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    /**
     * Return true if mimeType can be converted to HTML
     * using inso filter.
     *
     * @return true if the mime type can be converted, false otherwise
     */
    public Boolean allowINSOConvert() {
        return (Boolean)get(IS_INSO);
    }

    /**
     * Set whether mimeType can be converted to HTML
     * using inso filter.
     *
     * @param canConvert true if the mime type can be converted, false otherwise
     */
    public void setAllowINSOConvert(Boolean canConvert) {
        set(IS_INSO, canConvert);
    }

    /**
     * Set whether mimeType can be converted to HTML
     * using inso filter.
     *
     * @param canConvert true if the mime type can be converted, false otherwise
     */
    public void setAllowINSOConvert(boolean canConvert) {
        setAllowINSOConvert((canConvert)?Boolean.TRUE:Boolean.FALSE);
    }

}
