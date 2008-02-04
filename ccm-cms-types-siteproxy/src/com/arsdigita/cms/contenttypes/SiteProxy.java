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
package com.arsdigita.cms.contenttypes;

import java.math.BigDecimal;

import org.apache.log4j.Logger;

import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentType;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;

/**
 * This content type represents a SiteProxy.
 * 
 * Remote XML document defined by "url" property is retrieved and
 * included in CMS XML output.
 */
public class SiteProxy extends ContentPage {

    /** PDL property name for definition */
    public static final String URL = "url";

    /** PDL property name for definition titleAtoZ */
    public static final String TITLE_ATOZ = "titleAtoZ";

    /** PDL property name for definition usedInAtoZ */
    public static final String USED_IN_ATOZ = "usedInAtoZ";

    /** Data object type for this domain object */
    public static final String BASE_DATA_OBJECT_TYPE = "com.arsdigita.cms.contenttypes.SiteProxy";

    /** Data object type for this domain object (for CMS compatibility) */
    public static final String TYPE = BASE_DATA_OBJECT_TYPE;

    public SiteProxy() {
        this(BASE_DATA_OBJECT_TYPE);
        try {
            setContentType(ContentType
                    .findByAssociatedObjectType(BASE_DATA_OBJECT_TYPE));
        } catch (DataObjectNotFoundException e) {
            throw new RuntimeException("SiteProxy type not registered");
        }
    }

    public SiteProxy(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public SiteProxy(OID id) throws DataObjectNotFoundException {
        super(id);
    }

    public SiteProxy(DataObject obj) {
        super(obj);
    }

    public SiteProxy(String type) {
        super(type);
    }

    public String getURL() {
        String url = (String) get(URL);
        if (url != null) {
            url.trim();
        }
        return url;
    }

    public void setURL(String url) {
        set(URL, url);
    }

    public String getAtoZTitle() {
        return (String) get(TITLE_ATOZ);
    }

    public void setAtoZTitle(String atozTitle) {
        set(TITLE_ATOZ, atozTitle);
    }

    public boolean isUsedInAtoZ() {
        return Boolean.TRUE.equals(get(USED_IN_ATOZ));
    }

    public void setUsedInAtoZ(boolean usedInAtoZ) {
        set(USED_IN_ATOZ, new Boolean(usedInAtoZ));
    }

    protected void initialize() {
        super.initialize();

        if (isNew()) {
            set(USED_IN_ATOZ, Boolean.FALSE);
        }
    }
}
