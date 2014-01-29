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
import com.arsdigita.util.UncheckedWrapperException;

import java.math.BigDecimal;

/**
 * Used to keep track of the status of the tables cms_mime_types and
 * mime_type_extensions.  Also keeps track of inso filter status.
 *
 * @author Jeff Teeters (teeters@arsdigita.com)
 *
 * @version $Revision: #6 $ $DateTime: 2004/08/16 18:10:38 $
 */
public class MimeTypeStatus extends DomainObject {

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.cms.MimeTypeStatus";

    public static final String HASH_CODE = "hashCode";
    public static final String INSO_FILTER_WORKS = "insoFilterWorks";

    public MimeTypeStatus() {
        super(BASE_DATA_OBJECT_TYPE);
    }

    public MimeTypeStatus(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    public MimeTypeStatus(DataObject obj) {
        super(obj);
    }

    protected MimeTypeStatus(String type) {
        super(type);
    }

    /**
     * Return the single MimeTypeStatus object.
     **/

    public static MimeTypeStatus getMimeTypeStatus () {
        BigDecimal id = new BigDecimal(1);
        MimeTypeStatus ms;
        try {
            ms = new MimeTypeStatus
                (new OID(MimeTypeStatus.BASE_DATA_OBJECT_TYPE, id));
            return ms;
        } catch (DataObjectNotFoundException e) {
            // need to log
            throw new UncheckedWrapperException("MimeTypeStatus.java:" +
                            "cms cms_mime_status table not initialized.", e);
        }
    }


    public BigDecimal getHashCode() {
        return (BigDecimal) get(HASH_CODE);
    }

    public void setHashCode(BigDecimal value) {
        set(HASH_CODE, value);
    }

    public BigDecimal getInsoFilterWorks() {
        return (BigDecimal) get(INSO_FILTER_WORKS);
    }

    public void setInsoFilterWorks(BigDecimal value) {
        set(INSO_FILTER_WORKS, value);
    }
}
