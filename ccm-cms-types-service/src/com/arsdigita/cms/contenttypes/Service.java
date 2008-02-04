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


import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentType;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.Assert;

import java.math.BigDecimal;

/**
 * This content type represents a service.
 *
 * @version $Revision: #6 $ $Date: 2004/08/17 $
 **/
public class Service extends ContentPage {

    /** PDL property name for summary */
    public static final String SUMMARY = "summary";
    /** PDL property name for address */
    public static final String ADDRESS = "address";
    /** PDL property name for services provided */
    public static final String SERVICES_PROVIDED = "servicesProvided";
    /** PDL property name for opening times */
    public static final String OPENING_TIMES = "openingTimes";
    /** PDL property name for contacts */
    public static final String CONTACTS = "contacts";

    /** Data object type for this domain object */
    public static final String BASE_DATA_OBJECT_TYPE
        = "com.arsdigita.cms.contenttypes.Service";

    public Service() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    public Service(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public Service(OID id) throws DataObjectNotFoundException {
        super(id);
    }

    public Service(DataObject obj) {
        super(obj);
    }

    public Service(String type) {
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

    public String getServicesProvided() {
        return (String) get(SERVICES_PROVIDED);
    }

    public void setServicesProvided(String servicesProvided) {
        set(SERVICES_PROVIDED, servicesProvided);
    }

    public String getAddress() {
        return (String) get(ADDRESS);
    }

    public void setAddress(String Address) {
        set(ADDRESS, Address);
    }

    public String getOpeningTimes() {
        return (String) get(OPENING_TIMES);
    }

    public void setOpeningTimes(String openingTimes) {
        set(OPENING_TIMES, openingTimes);
    }

    public String getContacts() {
        return (String) get(CONTACTS);
    }

    public void setContacts(String contacts) {
        set(CONTACTS, contacts);
    }

    public static final int SUMMARY_LENGTH = 200;
    public String getSearchSummary() {
        return com.arsdigita.util.StringUtils.truncateString(getSummary(),
                                                             SUMMARY_LENGTH,
                                                             true);
    }

}
