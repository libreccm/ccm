/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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


import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.TextAsset;
import com.arsdigita.cms.TextPage;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.Assert;
import org.apache.log4j.Logger;

import java.math.BigDecimal;

/**
 * This content type represents a legal notice.
 *
 * @version $Revision: #6 $ $Date: 2004/08/17 $
 **/
public class LegalNotice extends TextPage {

    /** PDL property name for government UID */
    public static final String GOVERNMENT_UID = "governmentUID";

    /** Data object type for this domain object */
    public static final String BASE_DATA_OBJECT_TYPE
        = "com.arsdigita.cms.contenttypes.LegalNotice";

    private static final Logger s_log = Logger.getLogger(LegalNotice.class);
    public LegalNotice() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    public LegalNotice(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public LegalNotice(OID id) throws DataObjectNotFoundException {
        super(id);
    }

    public LegalNotice(DataObject obj) {
        super(obj);
    }

    public LegalNotice(String type) {
        super(type);
    }

    public void beforeSave() {
        super.beforeSave();
        
        Assert.exists(getContentType(), ContentType.class);
    }

    /* accessors *****************************************************/
    public String getGovernmentUID() {
        return (String) get(GOVERNMENT_UID);
    }

    public void setGovernmentUID(String governmentUID) {
        set(GOVERNMENT_UID, governmentUID);
    }

    // Search stuff to allow the content type to be searchable
    public static final int SUMMARY_LENGTH = 200;

    public String getSearchSummary() {
        TextAsset ta = getTextAsset();

        if (ta != null) {
            return com.arsdigita.util.StringUtils.truncateString(ta.getText(),
                                                                 SUMMARY_LENGTH,
                                                                 true);
        } else {
            return "";
        }
    }

}
