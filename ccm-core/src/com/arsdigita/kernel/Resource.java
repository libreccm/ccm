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
package com.arsdigita.kernel;

import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.Assert;
import java.util.Date;
import java.math.BigDecimal;
import org.apache.log4j.Logger;

/**
  * @author Jim Parsons
  */

public class Resource extends ACSObject {

    private static final Logger s_log = Logger.getLogger(Resource.class);

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.kernel.Resource";

    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
    public static final String PARENT_RESOURCE = "parentResource";
    public static final String CHILD_RESOURCE = "childResource";
    public static final String RESOURCE_TYPE = "resourceType";
    public static final String TIMESTAMP = "timestamp";

    private boolean m_parentModified = false;

    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    protected Resource(DataObject dataObject) {
        super(dataObject);
    }

    protected Resource(String dataObjectType) {
        super(dataObjectType);
    }

    protected Resource(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    public static Resource createResource
        (ResourceType resourceType, String title, Resource parent) {
        Assert.exists(resourceType, ResourceType.class);
        Assert.exists(title, String.class);

        return Resource.makeResource
            (resourceType, title, parent);
    }

    // For convenience.
    public static Resource createResource
        (String resourceObjectType, String title, Resource parent) {
        return Resource.createResource
            (ResourceType.retrieveResourceTypeForResource
             (resourceObjectType), title, parent);
    }

    // Actually does the work.
    private static Resource makeResource
            (final ResourceType resourceType,
             final String title,
             final Resource parent) {
        final String objectType = resourceType.getResourceObjectType();
        final DataObject dataObject =
            SessionManager.getSession().create(objectType);

        // the minimum set of properties required ACSObjectInstantiator
        dataObject.set(ACSObject.ID, ACSObject.generateID());
        dataObject.set(ACSObject.OBJECT_TYPE, objectType);
        dataObject.set(TITLE, title);

        Resource resource = Resource.retrieveResource(dataObject);
        Assert.exists(resource, Resource.class);
        resource.setResourceType(resourceType);

        if (parent != null) {
            resource.setParentResource(parent);
        }

        resource.setTitle(title);

        return resource;
    }

    @Override
    protected void beforeSave() {
        if (isNew() || isPropertyModified("parentResource")) {
            m_parentModified = true;
        }

        super.beforeSave();

        if (isModified()) {
            set(TIMESTAMP, new Date());
        }
    }

    @Override
    protected void afterSave() {
        super.afterSave();

        if (m_parentModified) {
            Resource parent = getParentResource();

            if (parent != null) {
                PermissionService.setContext(this, parent);
            }
            m_parentModified = false;
        }

    }

    public static Resource retrieveResource(BigDecimal id) {
        OID oid = new OID(BASE_DATA_OBJECT_TYPE, id);

        return Resource.retrieveResource(oid);
    }

    public static Resource retrieveResource(OID oid) {
        DataObject dataObject = SessionManager.getSession().retrieve(oid);

        if (dataObject == null) {
            return null;
        }

        return Resource.retrieveResource(dataObject);
    }

    public static Resource retrieveResource(DataObject dobj) {
        Assert.exists(dobj, DataObject.class);

        ACSObject obj = (ACSObject) DomainObjectFactory.newInstance(dobj);

        if (obj instanceof Resource) {
            return (Resource) obj;
        } else {
            return getContainingResource(obj);
        }
    }

    public static final Resource getContainingResource(ACSObject obj) {
        Assert.exists(obj, ACSObject.class);
        ACSObject result = obj.gimmeContainer();

        while (result != null &&
               !(result instanceof Resource)) {
            result = result.gimmeContainer();
        }

        return (Resource) result;
    }


    //
    // Association properties
    //

    // Cannot return null.
    public ResourceType getResourceType() {
        DataObject dataObject = (DataObject) get("resourceType");

        Assert.exists(dataObject, DataObject.class);

        return new ResourceType(dataObject);
    }

    protected void setResourceType(ResourceType resourceType) {
        Assert.exists(resourceType, ResourceType.class);

        setAssociation(RESOURCE_TYPE, resourceType);
    }

    // Can return null.
    public Resource getParentResource() {
        DataObject dataObject = (DataObject) get(PARENT_RESOURCE);
        if (dataObject == null) {
            return null;
        }

        return Resource.retrieveResource(dataObject);
    }

    // Param application can be null.
    public void setParentResource(Resource resource) {
        setAssociation(PARENT_RESOURCE, resource);
    }

    // Cannot return null.
    public ResourceCollection getChildResources() {
        DataAssociation association = (DataAssociation) get(CHILD_RESOURCE);

        return new ResourceCollection(association.cursor());
    }





    //
    // Member properties
    //

    public String getTitle() {
        String title = (String) get(TITLE);

        Assert.exists(title, String.class);

        return title;
    }

    public void setTitle(String title) {
        Assert.exists(title, String.class);

        set(TITLE, title);
    }

    public String getDescription() {
        return (String) get(DESCRIPTION);
    }

    public void setDescription(String description) {
        set(DESCRIPTION, description);
    }


    public Date getTimestamp() {
        return (Date) get(TIMESTAMP);
    }


    public static ResourceCollection retrieveAllResources() {
        DataCollection dataCollection =
            SessionManager.getSession().retrieve(BASE_DATA_OBJECT_TYPE);

        ResourceCollection rcs = new ResourceCollection
            (dataCollection);

        return rcs;
    }


    //
    // To support ACSObject services
    //

    // Expose title to admin interfaces.
    public String getDisplayName() {
        return getTitle();
    }

    // Can return null.
    protected ACSObject getContainer() {
        return getParentResource();
    }

    protected boolean isContainerModified() {
        return isPropertyModified(PARENT_RESOURCE);
    }

    //
    // Helpers
    //


}
