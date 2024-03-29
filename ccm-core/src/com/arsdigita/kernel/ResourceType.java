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

import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.db.Sequences;
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.kernel.ui.ResourceConfigComponent;
import com.arsdigita.kernel.ui.ResourceConfigFormSection;
import com.arsdigita.persistence.*;
import com.arsdigita.util.Assert;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.*;

/**
 * XXX JAVADOC XXX
 *
 * ResourceType / Resource.pdl maintains the tables application_types and
 * applications. Creating a ResourceType results in a kind of legacy free
 * application type (without linkage to permissions and legacy compatible layer).
 *
 * @see com.arsdigita.kernel.Resource
 * @see com.arsdigita.kernel.ResourceTypeCollection
 * @author Jim Parsons &lt;<a href="mailto:jparsons@redhat.com">jparsons@redhat.com</a>&gt;
 * @version $Id: ResourceType.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class ResourceType extends DomainObject {

    /** The logging object for this class. */
    private static final Logger s_log = Logger.getLogger(ResourceType.class);

    // ===== Constants ====================================================== //

    /** The fully qualified model name of the underlying data object, which in
     *  this case is the same as the Java type.                               */
    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.kernel.ResourceType";

    /**
     * Accessor method for this class Base Data Object Type, i.e. the
     * underlying data object.
     *  
     * @return String data object type name
     */
    @Override
    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    protected static ResourceTypeConfig s_defaultConfig =
        new ResourceTypeConfig();

    // ===== Constructors ==================================================== //

    /**
     * Creates a new ResourceObject instance to encapsulate a given data object.
     * 
     * @param dataObject
     */
    protected ResourceType(DataObject dataObject) {
        super(dataObject);
    }

    protected ResourceType(String dataObjectType) {
        super(dataObjectType);

        setID(generateID());
    }

    protected ResourceType(String dataObjectType,
                           String title,
                           String resourceObjectType) {
        this(dataObjectType);

        Assert.exists(title, "title");
        Assert.exists(resourceObjectType, "resourceObjectType");

        setTitle(title);
        setResourceObjectType(resourceObjectType);

    }

    // ===== Class Methods =================================================== //

    /**
     * 
     * @param title
     * @param resourceObjectType
     * @return
     */
    public static ResourceType createResourceType(String title,
                                                  String resourceObjectType) {
        return new ResourceType(BASE_DATA_OBJECT_TYPE, title, resourceObjectType);
    }

    // No null params.

    // Param
    public static ResourceType retrieveResourceType(BigDecimal id) {
        Assert.exists(id, "id");

        return ResourceType.retrieveResourceType
            (new OID(ResourceType.BASE_DATA_OBJECT_TYPE, id));
    }

    // Param oid cannot be null.
    public static ResourceType retrieveResourceType(OID oid) {
        Assert.exists(oid, "oid");

        DataObject dataObject = SessionManager.getSession().retrieve(oid);

        Assert.exists(dataObject);

        return ResourceType.retrieveResourceType(dataObject);
    }

    // Param dataObject cannot be null.  Can return null?
    public static ResourceType retrieveResourceType
        (DataObject dataObject) {
        Assert.exists(dataObject, "dataObject");

        return new ResourceType(dataObject);
    }

    // Can return null.
    public static ResourceType retrieveResourceTypeForResource
        (String resourceObjectType) {
        Assert.exists(resourceObjectType, "resourceObjectType");

        DataCollection collection =
            SessionManager.getSession().retrieve(BASE_DATA_OBJECT_TYPE);

        collection.addEqualsFilter("objectType", resourceObjectType);

        ResourceType resourceType = null;

        if (collection.next()) {
            resourceType = ResourceType.retrieveResourceType
                (collection.getDataObject());
        }

        collection.close();

        return resourceType;
    }

    public static ResourceTypeCollection retrieveAllResourceTypes() {
        DataCollection collection =
            SessionManager.getSession().retrieve(BASE_DATA_OBJECT_TYPE);

        Assert.exists(collection, "collection");

        return new ResourceTypeCollection(collection);
    }


    //
    // Association properties (some by proxy)
    //

    //
    // Member properties
    //

    public String getTitle() {
        String title = (String) get("title");

        Assert.exists(title, "title");

        return title;
    }

    public void setTitle(String title) {
        Assert.exists(title, "title");

        set("title", title);
    }

    // Can return null.
    public String getDescription() {
        String description = (String) get("description");

        return description;
    }

    // Param description can be null.
    public void setDescription(String description) {
        set("description", description);
    }

    /**
     * <p>Get the list of relevant privileges for this
     * ResourceType.</p>
     *
     * @return A Collection of {@link PrivilegeDescriptor
     * PrivilegeDescriptors}
     */
    public Collection getRelevantPrivileges() {
        LinkedList result = new LinkedList();

        DataAssociationCursor dac =
            ((DataAssociation) get("relevantPrivileges")).cursor();

        while (dac.next()) {
            PrivilegeDescriptor priv =
                PrivilegeDescriptor.get((String)dac.get("privilege"));
            result.add(priv);
        }

        return result;
    }

    /**
     * <p>Add an entry to the list of relevant privileges for this
     * ResourceType.</p>
     */
    public void addRelevantPrivilege(PrivilegeDescriptor privilege) {
        addRelevantPrivilege(privilege.getName());
    }

    /**
     * <p>Add an entry to the list of relevant privileges for this
     * ResourceType.</p>
     */
    public void addRelevantPrivilege(String privilegeName) {
        OID privOID = new OID("com.arsdigita.kernel.permissions.Privilege",
                              privilegeName);
        DataObject privDO = SessionManager.getSession().retrieve(privOID);
        add("relevantPrivileges", privDO);
    }

    /**
     * <p>Remove an entry from the list of relevant privileges for
     * this ResourceType.</p>
     */
    public void removeRelevantPrivilege(PrivilegeDescriptor privilege) {
        removeRelevantPrivilege(privilege.getName());
    }

    /**
     * <p>Remove an entry from the list of relevant privileges for
     * this ResourceType.</p>
     */
    public void removeRelevantPrivilege(String privilegeName) {
        OID privOID = new OID("com.arsdigita.kernel.permissions.Privilege",
                              privilegeName);
        DataObject privDO = SessionManager.getSession().retrieve(privOID);
        remove("relevantPrivileges", privDO);
    }

    public String getResourceObjectType() {
        String objectType = (String)get("objectType");

        Assert.exists(objectType);

        return objectType;
    }

    protected void setResourceObjectType(String objectType) {
        Assert.exists(objectType);

        set("objectType", objectType);
    }

    /**
     * Gets the value of the ID property.
     *
     * @return the value of the ID property.
     */
    public BigDecimal getID() {
        BigDecimal id = (BigDecimal)get("id");

        Assert.exists(id, "id");

        return id;
    }

    /**
     * Sets the value of the ID property only if this is new.
     * Returns the value that the ID is set to.
     *
     * @param id the value to try to set the ID property to
     * @return the value that the ID property is set to.
     */
    private BigDecimal setID(BigDecimal id) {
        Assert.exists(id, "id");

        if (isNew() && get("id") == null) {
            set("id", id);
            return id;
        } else {
            return getID();
        }
    }

    public static boolean isInstalled(String resourceObjectType) {
        return (ResourceType.retrieveResourceTypeForResource
                (resourceObjectType) != null);
    }



    //
    // Other
    //


    private BigDecimal generateID() throws PersistenceException {
        try {
            return Sequences.getNextValue();
        } catch (SQLException e) {
            final String errorMsg = "Unable to generate a unique " +
                "id.";
            s_log.error(errorMsg);
            throw new PersistenceException(errorMsg);
        }
    }

    //
    // Configuration
    //

    static final Map s_configMap = Collections.synchronizedMap(new HashMap());

    public static void registerResourceTypeConfig
            (final String resourceObjectType,
             final ResourceTypeConfig config) {
        s_configMap.put(resourceObjectType, config);
    }

    public static Iterator getConfigs() {
        return s_configMap.entrySet().iterator();
    }

    // Can return null.
    public final ResourceTypeConfig getConfig() {
        final String objectType = getResourceObjectType();

        if (s_log.isDebugEnabled()) {
            s_log.debug("Using object type '" + objectType + "' to " +
                        "lookup config for resource type " + this.getTitle());
            s_log.debug(s_configMap);
        }

        final ResourceTypeConfig config =
            (ResourceTypeConfig) s_configMap.get(objectType);

        if (s_log.isDebugEnabled()) {
            s_log.debug("Found config " + config);
        }

        return config;
    }

    public final boolean hasConfig() {
        return (getConfig() != null);
    }

    public final ResourceConfigFormSection getCreateFormSection
            (final RequestLocal parentResource) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Getting the application config form section " +
                        "for resource type " + this);
        }

        if (getConfig() == null) {
            s_log.debug("No config was registered for this " +
                        "resource type; using the default");

            return s_defaultConfig.getCreateFormSection(this, parentResource);
        } else {
            s_log.debug("Found a config");

            return getConfig().getCreateFormSection(this, parentResource);
        }
    }

    public final ResourceConfigFormSection getModifyFormSection
            (final RequestLocal resource) {
        if (getConfig() == null) {
            return s_defaultConfig.getModifyFormSection(resource);
        }

        return getConfig().getModifyFormSection(resource);
    }

    public final ResourceConfigComponent getCreateComponent
            (final RequestLocal parentResource) {
        if (getConfig() == null) {
            return s_defaultConfig.getCreateComponent(this, parentResource);
        }

        return getConfig().getCreateComponent(this, parentResource);
    }

    public ResourceConfigComponent getModifyComponent
            (RequestLocal resource) {
        if (getConfig() == null) {
            return s_defaultConfig.getModifyComponent(resource);
        }

        return getConfig().getModifyComponent(resource);
    }

    /**
     * Retrieves all objects of this type stored in the database. Very
     * necessary for exporting all entities of the current work environment.
     *
     * @return List of all resource types
     */
    public static List<ResourceType> getAllObjectResourceTypes() {
        List<ResourceType> resourceTypeList = new ArrayList<>();

        ResourceTypeCollection collection = ResourceType
                .retrieveAllResourceTypes();

        while (collection.next()) {
            ResourceType resourceType = (ResourceType) collection.getDomainObject();
            if (resourceType != null) {
                resourceTypeList.add(resourceType);
            }
        }

        collection.close();
        return resourceTypeList;
    }
}
