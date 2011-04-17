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
package com.arsdigita.docrepo;


//import com.arsdigita.web.Web;
import com.arsdigita.domain.DataObjectNotFoundException;
//import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.DomainObject;
//import com.arsdigita.kernel.Kernel;
import com.arsdigita.db.Sequences;
//import com.arsdigita.kernel.KernelExcursion;
//import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
//import com.arsdigita.persistence.DataOperation;
//import com.arsdigita.persistence.DataQuery;
//import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.OID;
//import com.arsdigita.persistence.PersistenceException;
//import com.arsdigita.persistence.Session;
//import com.arsdigita.persistence.SessionManager;
//import com.arsdigita.persistence.metadata.ObjectType;

import java.math.BigDecimal;


/** 
 * 
 * 
 */
public class DocBlobject extends DomainObject {

    public static final String BASE_DATA_OBJECT_TYPE = 
                               "com.arsdigita.docrepo.DocBlobject";

    @Override
    public String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    public DocBlobject() {
        super(BASE_DATA_OBJECT_TYPE);
    }

    @Override
    protected void initialize() {

        super.initialize();
        try {
            if(isNew()) {
              set("id",Sequences.getNextValue());  
            }
        } catch (java.sql.SQLException e) {
            //s_log.error here
        }
    }

    /**
     * Creates a new Doc Blob by retrieving it from the underlying data
     * object.
     *
     * @param dataObject the dataObject corresponding to this file
     */
    public DocBlobject(DataObject dataObject) {
        super(dataObject);
    }

    /**
     * Creates a new File by retrieving it based on ID.
     *
     * @param id - the ID of this file in the database
     */
    public DocBlobject(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    /**
     * Creates a new DocBlobject by retrieving it based on OID.
     *
     * @param oid - the OID of this file
     */
    public DocBlobject(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }
   
    public byte[] getContent() {
        return (byte[])get("content");
    }

    public void setContent(byte[] content) {

        set("content",content);

    }

}
