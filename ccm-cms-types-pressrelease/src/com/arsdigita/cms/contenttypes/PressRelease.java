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
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.Assert;
import java.math.BigDecimal;

/**
 * This content type represents a press release. Its extended attributes are
 * release date and reference code. Release date has nothing to do with
 * publishing, but allows the author to specify the original release date for
 * reference purposes. Reference code is an arbitrary string that is used for
 * out-of-system reference. The type inherits name (filename), title, body
 * (TextAsset), and metadata from
 * <code>com.arsdigita.cms.contenttypes.Genericrticle</code>, and also provides
 * the capability to associate contact information with this press release.
 *
 * @version $Revision: #6 $ $Date: 2004/08/17 $
 **/
public class PressRelease extends GenericArticle {

    // is the CardBin sort of contact support needed here?
    //    implements ContactSupport {
    /** PDL property name for summary */
    public static final String SUMMARY = "summary";
    /** PDL property name for contact info */
    public static final String CONTACT_INFO = "contactInfo";
    /** PDL property name for reference code */
    public static final String REFERENCE_CODE = "referenceCode";
    /** Data object type for this domain object */
    public static final String BASE_DATA_OBJECT_TYPE = 
                               "com.arsdigita.cms.contenttypes.PressRelease";

    public PressRelease() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    public PressRelease(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public PressRelease(OID id) throws DataObjectNotFoundException {
        super(id);
    }

    public PressRelease(DataObject obj) {
        super(obj);
    }

    public PressRelease(String type) {
        super(type);
    }

    public void beforeSave() {
        super.beforeSave();

        Assert.exists(getContentType(), ContentType.class);
    }

    /* accessors *****************************************************/
    public String getSummary() {
        return (String) get(SUMMARY);
    }

    public void setSummary(String summary) {
        set(SUMMARY, summary);
    }

    public String getContactInfo() {
        return (String) get(CONTACT_INFO);
    }

    public void setContactInfo(String contactInfo) {
        set(CONTACT_INFO, contactInfo);
    }

    public String getReferenceCode() {
        return (String) get(REFERENCE_CODE);
    }

    public void setReferenceCode(String refCode) {
        set(REFERENCE_CODE, refCode);
    }
    public static final int SUMMARY_LENGTH = 200;

    public String getSearchSummary() {
        return com.arsdigita.util.StringUtils.truncateString(getSummary(),
                SUMMARY_LENGTH,
                true);
    }
}
