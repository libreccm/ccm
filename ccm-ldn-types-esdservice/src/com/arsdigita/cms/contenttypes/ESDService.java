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
 */
package com.arsdigita.cms.contenttypes;

import com.arsdigita.cms.contenttypes.GenericArticle;
import com.arsdigita.cms.ContentType;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.Assert;

import java.math.BigDecimal;

/**
 * Domain object class to represent <code>ESDService</code> content type.
 * 
 * @author Shashin Shinde <a href="mailto:sshinde@redhat.com">sshinde@redhat.com</a>
 * @version $Id: ESDService.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class ESDService extends GenericArticle {

    /** data object type for this domain object */
    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.cms.contenttypes.ESDService";

    public static final String SERVICE_CONTACT = "serviceContact";
    public static final String SERVICE_TIMES = "serviceTimes";
    
    /** Default constructor. */
    public ESDService() {
        super(BASE_DATA_OBJECT_TYPE);
    }
  
    /**
     * @param oid
     * @throws DataObjectNotFoundException
     */
    public ESDService(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    /**
     * @param id
     * @throws DataObjectNotFoundException
     */
    public ESDService(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE , id));
    }

    /**
     * @param obj
     */
    public ESDService(DataObject obj) {
        super(obj);
    }

    /**
     * @param type
     */
    public ESDService(String type) {
        super(type);
    }

    public void beforeSave() {
        super.beforeSave();
        Assert.exists(getContentType(), ContentType.class);
    }

    /**
     * Over-ride to return the proper type for this object.
     */
    public String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }
  
    /** 
     * Get the associated Contact object for this ESDService.
     * @return null if no Contact is present.
     */
    public Contact getContact() {
        DataObject dobj = (DataObject) get(SERVICE_CONTACT);
        if (dobj != null) {
            return (Contact) DomainObjectFactory.newInstance(dobj);
        }
        return null;
    }

    /**
     * set the Contact object association.
     * @pre ct != null
     */
    public void setContact(Contact ct) {
        Assert.exists(ct, Contact.class);
        setAssociation(SERVICE_CONTACT , ct);
    }
  
    public String getServiceTimes() {
        return (String) get(SERVICE_TIMES);
    }
  
    public void setServiceTimes(String st) {
        set(SERVICE_TIMES , st);
    }

}
