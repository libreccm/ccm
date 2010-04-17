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

import com.arsdigita.cms.ContentSection;
import com.arsdigita.db.Sequences;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.UncheckedWrapperException;

import java.math.BigDecimal;
import java.sql.SQLException;


/**
 * <p>An instance of a {@link com.arsdigita.cms.dispatcher.ResourceType}.</p>
 *
 * @author Michael Pih (pihman@arsdigita.com)
 * @version $Revision: #9 $ $DateTime: 2004/08/17 23:15:09 $
 * @version $Id: Resource.java 287 2005-02-22 00:29:02Z sskracic $ 
 **/
public class Resource extends DomainObject {

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.cms.Resource";

    private static final String ID = "id";
    private static final String RESOURCE_TYPE = "type";
    private static final String CLASSNAME = "className";


    public Resource() {
        super(BASE_DATA_OBJECT_TYPE);
    }

    public Resource(String type) {
        super(type);
    }

    public Resource(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    public Resource(DataObject obj) {
        super(obj);
    }

    public BigDecimal getID() {
        return (BigDecimal) get(ID);
    }

    public String getClassName() {
        return (String) get(CLASSNAME);
    }

    private void setID(BigDecimal id) {
        set(ID, id);
    }

    public void setClassName(String s) {
        set(CLASSNAME, s);
    }

    public ResourceType getType() {
        DataObject type = (DataObject) get(RESOURCE_TYPE);
        if ( type == null ) {
            return null;
        } else {
            return new ResourceType(type);
        }
    }

    public void setType(ResourceType t) {
        setAssociation(RESOURCE_TYPE, t);
    }


    /**
     * Creates a new resource object.  Use this method by calling
     * createInstance() from an instance of a ResourceType.
     *
     * @param type      The resource type object
     * @param className The Java class that implements this resource
     * @return The new resource
     */
    protected static Resource create(ResourceType type, String className) {

        Resource resource = new Resource();
	try {
	    BigDecimal id = Sequences.getNextValue();
	    resource.setID(id);
	} catch(SQLException e) {
	    throw new UncheckedWrapperException("Error creating Resource", e);
	}
        resource.setClassName(className);
        resource.setType(type);
        return resource;
    }

    /**
     * Creates a new resource object.
     *
     * @param section The content section
     * @param url The URL (relative to the content section) identifying
     *            the resource
     * @return The new resource mapping
     */
    public ResourceMapping createInstance(ContentSection section, String url) {
        return createInstance(section.getID(), url);
    }

    /**
     * Creates a new resource object.
     *
     * @param sectionId The content section ID
     * @param url The URL (relative to the content section) identifying
     *            the resource
     * @return The new resource mapping
     */
    public ResourceMapping createInstance(BigDecimal sectionId, String url) {
        ResourceMapping map = new ResourceMapping();
        map.setSectionID(sectionId);
        map.setUrl(url);
        map.setResourceID(getID());
        return map;
    }


    public static Resource findResource(ContentSection section, String url)
        throws DataObjectNotFoundException {

        Session session = SessionManager.getSession();
        DataQuery dq =
            session.retrieveQuery("com.arsdigita.cms.getResourceMappings");
        Filter f = dq.addFilter("sectionId = :sectionId and url = :url");
        f.set("sectionId", section.getID());
        f.set("url", url);

        Resource resource = null;
        BigDecimal id = null;
        if ( dq.next() ) {
            id = (BigDecimal) dq.get("resourceId");

            resource = new Resource
                (new OID(Resource.BASE_DATA_OBJECT_TYPE, id));
            dq.close();
        }

        return resource;
    }

}
