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
package com.arsdigita.mimetypes.converters;


import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.mimetypes.util.GlobalizationUtil;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.UncheckedWrapperException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import org.apache.log4j.Logger;

/**
 * The PreConvertHTML class represents a single row
 * in the pre_convert_html table.
 *
 * @author <a href="mailto:teeters@arsdigita.com">Jeff Teeters</a>
 * @version 1.0
 **/
public class PreConvertHTML extends DomainObject {
    public static final String versionId = "$Id";

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.cms.ui.authoring.PreConvertHTML";
    private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];

    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    // Creates a s_logging category with name = to the full name of class
    private static Logger s_log =
        Logger.getLogger( PreConvertHTML.class.getName() );


    /**
     * Default constructor. The contained <code>DataObject</code> is
     * initialized with a new <code>DataObject</code> with an
     * <code>ObjectType</code> of "PreConvertHTML".
     **/
    public PreConvertHTML() { // throws DataObjectNotFoundException {
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
    public PreConvertHTML(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    private final static String ID = "id";
    private final static String CONTENT = "content";

    public void setId(BigDecimal id) {
        set(ID, id);
    }

    public void setContent(byte[] content) {
        set(CONTENT, content);
    }

    //accessors
    public BigDecimal getId() {
        return (BigDecimal)get(ID);
    }

    public byte[] getContent() {
        Object obj = get(CONTENT);
        if (obj == null) {
            return EMPTY_BYTE_ARRAY;
        }

        if (obj instanceof byte[]) {
            return (byte[])obj;
        } else if (obj instanceof Blob) {
            try {
                Blob blob = (Blob)obj;
                return blob.getBytes(1L, (int)blob.length());
            } catch (SQLException sqle) {
                throw new UncheckedWrapperException( (String) GlobalizationUtil.globalize("mimetypes.ui.sql_error_in_getting_byte_content").localize(),   sqle);
            }
        } else {
            throw new RuntimeException( (String) GlobalizationUtil.globalize("mimetypes.ui.bad_getblob_datatype").localize() + obj.getClass());
        }
    }
}
