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
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;



/**
 * <p>A DomainObject that represents an type of resource.</p>
 *
 * <p>Common resource types include XML resource and streaming data.</p>
 *
 * @author Michael Pih (pihman@arsdigita.com)
 * @version $Revision: #9 $ $DateTime: 2004/08/17 23:15:09 $
 * @version $Id: ResourceType.java 2090 2010-04-17 08:04:14Z pboy $
 */
public class ResourceType extends DomainObject {

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.cms.ResourceType";

    private static final String ID = "id";
    private static final String CLASSNAME = "baseClass";
    private static final String LABEL = "label";
    private static final String DESCRIPTION = "description";


    public ResourceType() {
        super(BASE_DATA_OBJECT_TYPE);
    }

    public ResourceType(String type) {
        super(type);
    }

    public ResourceType(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    public ResourceType(DataObject obj) {
        super(obj);
    }

    public String getID() {
        return (String) get(ID);
    }

    public String getLabel() {
        return (String) get(LABEL);
    }

    public String getDescription() {
        return (String) get(DESCRIPTION);
    }

    public String getBaseClassName() {
        return (String) get(CLASSNAME);
    }

    private void setID(String id) {
        set(ID, id);
    }

    public void setLabel(String s) {
        set(LABEL, s);
    }

    public void setDescription(String s) {
        set(DESCRIPTION, s);
    }

    public void setBaseClassName(String s) {
        set(CLASSNAME, s);
    }

    /**
     * Creates a new resource object of this type.
     *
     * @param className The Java class that implements this resource
     * @return The new resource
     */
    public Resource createInstance(String className) {
        return Resource.create(this, className);
    }

    /**
     * Creates a new resourceType object.
     *
     * @param id The type key
     * @param baseClass The Java class that implements this resource type
     * @param label The pretty name
     * @param description A description of the resource type
     * @return The new resource type
     */
    public static ResourceType createResourceType(
                                                  String id, String baseClass, String label, String description)
    {

        ResourceType resourceType = new ResourceType();
        resourceType.setBaseClassName(baseClass);
        resourceType.setID(id);
        resourceType.setLabel(label);
        if ( description != null ) {
            resourceType.setDescription(description);
        }
        return resourceType;
    }


    /**
     * Creates a new resourceType object.
     *
     * @param id The type key
     * @param baseClass The Java class that implements this resource type
     * @param label The pretty name
     * @return The new resource type
     */
    public static ResourceType createResourceType(
                                                  String id, String baseClass, String label) {

        return createResourceType(id, baseClass, label, null);
    }


    public static ResourceType findResourceType(String id)
        throws DataObjectNotFoundException {

        Session session = SessionManager.getSession();
        DataQuery dq =
            session.retrieveQuery("com.arsdigita.cms.getResourceTypes");
        Filter f = dq.addFilter("id = :id");
        f.set("id", id);

        ResourceType type = null;
        String typeId = null;
        if ( dq.next() ) {
            typeId = (String) dq.get("id");

            type = new ResourceType
                (new OID(ResourceType.BASE_DATA_OBJECT_TYPE, id));
            dq.close();
        }

        return type;
    }

}
