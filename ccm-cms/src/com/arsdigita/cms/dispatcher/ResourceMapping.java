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
package com.arsdigita.cms.dispatcher;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;

import java.math.BigDecimal;


/**
 * <p>This class represents a mapping of a
 * {@link com.arsdigita.cms.dispatcher.Resource} to a URL local to a
 * {@link com.arsdigita.cms.ContentSection content section}.</p>
 *
 * @author Michael Pih (pihman@arsdigita.com)
 * @version $Revision: #7 $ $DateTime: 2004/08/17 23:15:09 $
 **/
public class ResourceMapping extends DomainObject {

    public static final String versionId = "$Id: ResourceMapping.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/17 23:15:09 $";

    public static final String TYPE = "com.arsdigita.cms.ResourceMapping";

    private static final String SECTION_ID = "sectionId";
    private static final String URL = "url";
    private static final String RESOURCE_ID = "resourceId";


    public ResourceMapping() {
        super(TYPE);
    }

    public ResourceMapping(String type) {
        super(type);
    }

    public ResourceMapping(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    public ResourceMapping(DataObject obj) {
        super(obj);
    }

    public BigDecimal getSectionID() {
        return (BigDecimal) get(SECTION_ID);
    }

    public String getUrl() {
        return (String) get(URL);
    }

    public BigDecimal getResourceID() {
        return (BigDecimal) get(RESOURCE_ID);
    }

    public void setSectionID(BigDecimal id) {
        set(SECTION_ID, id);
    }

    public void setUrl(String s) {
        set(URL, s);
    }

    public void setResourceID(BigDecimal id) {
        set(RESOURCE_ID, id);
    }

}
