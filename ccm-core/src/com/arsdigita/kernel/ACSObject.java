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
package com.arsdigita.kernel;

import com.arsdigita.db.DbHelper;
import com.arsdigita.db.Sequences;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.ObservableDomainObject;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.kernel.security.SecurityLogger;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.PersistenceException;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.persistence.metadata.Property;

import java.math.BigDecimal;
import java.sql.SQLException;

import com.arsdigita.portation.AbstractMarshaller;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

/**
 * An ACSObject is a generic object that can be handled by any of a
 * number of Core services e.g., versioning, permissions. Subclass
 * ACSObject if you need any of these services.
 *
 * ACSObject contains a <code>DataObject</code> with an
 * <code>ObjectType</code> of "<code>ACSObject</code>" or any subtype
 * of it. The persistence storage mechanism guarantees that all
 * <code>DataObject</code>s that are of type "<code>ACSObject</code>"
 * can be uniquely identified from each other by a single
 * identifier. Thus any instances of this class can be uniquely
 * identified by a single identifier, which is currently found in this
 * object's <code>OID</code>.
 *
 * @version $Id: ACSObject.java 287 2005-02-22 00:29:02Z sskracic $
 **/
public abstract class ACSObject extends ObservableDomainObject {

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.kernel.ACSObject";

    private static final Logger s_log =
        Logger.getLogger(ACSObject.class);

    /**
     * The BigDecimal ID.
     */
    public static final String ID = "id";

    /**
     * The object type.
     */
    public static final String OBJECT_TYPE = "objectType";

    /**
     * The default domain class name.  This is the name of the java
     * class that should should be instantiated by the domain object
     * factory in the default case when no specific instantiator is
     * able to handle the given data object.
     *
     * @see ACSObjectInstantiator
     */
    public static final String DEFAULT_DOMAIN_CLASS = "defaultDomainClass";

    /**
     * The denormalized display name.
     */
    public static final String DISPLAY_NAME = "displayName";

    /**
     * The denormalized container.
     */
    protected static final String CONTAINER = "container";

    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    public ACSObject(DataObject acsObjectData) {
        super(acsObjectData);
    }

    /**
     * Default constructor. The contained <code>DataObject</code> is
     * initialized with a new <code>DataObject</code> with an
     * <code>ObjectType</code> of "ACSObject".
     *
     * @see com.arsdigita.domain.ObservableDomainObject#ObservableDomainObject(String)
     * @see com.arsdigita.persistence.metadata.ObjectType
     **/
    public ACSObject() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    /**
     * Constructor in which the contained <code>DataObject</code> is
     * initialized with a new <code>DataObject</code> with an
     * <code>ObjectType</code> specified by the string
     * <i>typeName</i>.
     *
     * @param typeName the name of the <code>ObjectType</code> of the
     * contained <code>DataObject</code>
     *
     * @see com.arsdigita.domain.ObservableDomainObject#ObservableDomainObject(String)
     * @see com.arsdigita.persistence.DataObject
     * @see com.arsdigita.persistence.metadata.ObjectType
     **/
    public ACSObject(String typeName) {
        super(typeName);
    }

    /**
     * Constructor in which the contained <code>DataObject</code> is
     * initialized with a new <code>DataObject</code> with an
     * <code>ObjectType</code> specified by <i>type</i>.
     *
     * @param type the <code>ObjectType</code> of the contained
     * <code>DataObject</code>
     *
     * @see com.arsdigita.domain.ObservableDomainObject#ObservableDomainObject(ObjectType)
     * @see com.arsdigita.persistence.DataObject
     * @see com.arsdigita.persistence.metadata.ObjectType
     **/
    public ACSObject(ObjectType type) {
        super(type);
    }

    /**
     * Constructor in which the contained <code>DataObject</code> is retrieved
     * from the persistent storage mechanism using the
     * specified OID.
     *
     * @param oid the OID for the retrieved
     * <code>DataObject</code>
     *
     * @see com.arsdigita.domain.ObservableDomainObject#ObservableDomainObject(OID)
     * @see com.arsdigita.persistence.DataObject
     * @see com.arsdigita.persistence.OID
     **/
    public ACSObject(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    /**
     * Called from base class (DomainObject) constructors.
     */
    @Override
    protected void initialize() {
        super.initialize();

        if (isNew()) {
            if (get(ID) == null) {
                set(ID, generateID());
            }

            String typeName =
                getObjectType().getModel().getName() +
                "." + getObjectType().getName();

            set(OBJECT_TYPE, typeName);
            String className = this.getClass().getName();

            set(DEFAULT_DOMAIN_CLASS, className);
        } else {
            String baseType = getBaseDataObjectType();
            if (baseType!=null) {
                ObjectType.verifySubtype(baseType,
                                         getSpecificObjectType());
            }
        }

        if (s_log.isDebugEnabled()) {
            Party party = Kernel.getContext().getEffectiveParty();
            s_log.debug((isNew() ? "Create" : "Load") + 
                        " object " + getOID() + " under party: " + 
                        (party == null ? null : party.getOID()));
        }
    }

    /**
     * Gets the value of the ID property.
     *
     * This is a convenience method that is roughly equivalent
     * to getOID().get("id"). In general, it should be used
     * instead of the getOID method to get any ACSObject's ID.
     *
     * @return the value of the ID property.
     */
    public BigDecimal getID() {
        return (BigDecimal) get(ID);
    }

    /**
     * Sets the value of the ID property only if it is not already set.
     * Returns the value that the ID is set to after execution (which is
     * different from the passed in value if the ID had already been set).
     *
     * @param id the value to try to set the ID property to
     * @return the value that the ID property is set to after execution.
     * @deprecated No longer has any effect on the id. Will always
     *             return getID()
     **/

    public BigDecimal setID(BigDecimal id) {
        return getID();
    }

    /**
     * Sets the value of the ID property to
     * <code>com.arsdigita.db.Sequences.getNextValue()</code>
     * only if the ID property is not already set.
     * Returns the value that the ID is set to after execution.
     *
     * @param id the value to try to set the ID property to
     * @return the value that the ID property is set to after execution.
     *
     * @exception PersistenceException if a unique id could not
     * be generated.
     *
     * @deprecated No longer has any effect on the id. Will always
     *             return getID()
     **/
    public BigDecimal setID() throws PersistenceException {
        return getID();
    }

    /**
     * Returns a display name for this object.  The display name
     * is produced by domain-specific logic based on any properties
     * of the domain object.  The display name is used internally
     * for efficient access in cursors like ACSObjectCollection.
     * ACSObject.save() takes care of populating the internal data
     * object's displayName property with the result of the
     * getDisplayName method.
     *
     * While this method is not abstract, the default implementation
     * is very useless.  All subclasses should provide their own
     * implementations.
     *
     * @return the displayable name for this object.
     */
    public String getDisplayName() {
        return getSpecificObjectType() + " " + getID();
    }

    /**
     * Returns the container for this object, or null if there is no
     * container. 
     * 
     * The container is produced by domain-specific logic based on any
     * properties of the domain object.  The resulting container is
     * denormalized internally by ACSObject.save().
     * The denormalized container hierarchy is currently only used
     * for generically determining what package instance an object
     * belongs to.  In the future, other generic services may be
     * introduced that take advantage of the denormalized container
     * hierarchy.
     *
     * While this method is not abstract, the default implementation
     * "guesses" the container based on metadata about the object.
     * If this object's data object type has a composite role property
     * (required, visible property where
     * <code>com.arsdigita.persistence.metadata.Property.isComposite()==true</code>),
     * then we fetch the value of the composite role property, pass it to the
     * DomainObjectFactory, and return the resulting domain object.  If
     * no composite role property is found, then the return value is null.
     *
     * Subclasses should provide their own implementations if the metadata
     * driven default implementation is inadequate.  For example, in a
     * File Storage application, a "Folder" domain class could provide an
     * implementationof getContainer() that returns the parent folder (if
     * it exists) OR the package instance (if the folder is the root folder
     * of one File Storage application instance).
     *
     * @return this object's container.
     * @deprecated without direct replacement. Method uses old app style
     * PackageInstance no longer in use. Involing code has to be refactored
     * to use new style application code.
     */
    protected ACSObject getContainer() {
        ObjectType specificType = MDUtil.getType(getSpecificObjectType());
        Property p = MDUtil.getCompositeRole(specificType);
        DataObject containerData = null;
        if (p != null) {
            specializeDataObject(specificType);
            containerData = (DataObject) get(p.getName());
        }
        if (containerData == null) {
            throw new IllegalArgumentException(
                      "containerData is null, PackageInstance removed.");
         // if (MDUtil.hasPackageInstanceRole(specificType)) {
         //     specializeDataObject(specificType);
         //     containerData = (DataObject) get(MDUtil.PACKAGE_INSTANCE);
         // }
        }

        return (ACSObject) DomainObjectFactory.newInstance(containerData);
    }

    /**
     * Returns true if this object has been moved to a new container,
     * or null if the container has not changed.  
     * 
     * This methods is used by ACSObject.save() to determine when to
     * denormalize the result of getContainer().
     *
     * While this method is not abstract, the default implementation
     * "guesses" the container based on metadata about the object.
     * If this object's data object type has a composite role property
     * (required, visible property where
     * <code>com.arsdigita.persistence.metadata.Property.isComposite()==true</code>),
     * then we check whether the composite role property has been modified.
     * If no composite role is found, then the return value is false.
     *
     * Subclasses should provide their own implementations if the metadata
     * driven default implementation is inadequate.  See the example
     * mentioned in getContainer().
     *
     * @return this object's container.
     *
     * @deprecated
     * @see #getContainer()
     */
    protected boolean isContainerModified() {

        ObjectType specificType = MDUtil.getType(getSpecificObjectType());
        Property p = MDUtil.getCompositeRole(specificType);

        if (p != null) {
            specializeDataObject(specificType);
            if (isPropertyModified(p.getName())) {
                return true;
            }
            // The composite property is not modified.  If its value
            // is not null, then the container hasn't been modified.
            // If the value is null, then we examine the "packageInstance"
            // role.
            if (get(p.getName())!=null) {
                return false;
            }
        }

   //  Removed. EXPERIMENTAL! (pboy 2013-01-26, r2049 hb)
   //  Because package tyble is empty (there exist no old style nor new style
   //  compatible applications anymore, everything is new style legacy free)
   //  the argument in if should always return false.!
   //   if (MDUtil.hasPackageInstanceRole(specificType)) {
   //       specializeDataObject(specificType);
   //       return isPropertyModified(MDUtil.PACKAGE_INSTANCE);
   //   }

        return false;
    }

    /**
     * A public version of {@link #getContainer()}.
     * @deprecated
     **/
    public ACSObject gimmeContainer() {
        return getContainer();
    }

    /**
     * Gets the value of the "objectType" property, which is the fully
     * qualified name of the data object type that this domain object
     * had when it was first created.
     * @return the object type for this domain object.
     */
    public String getSpecificObjectType() {
        return (String) get(OBJECT_TYPE);
    }

    /**
     * Gets the specific OID for this ACSObject, which is the tuple
     * of the 'objectType' & 'id' properties.
     */
    public OID getSpecificOID() {
        return new OID(getSpecificObjectType(),
                       getID());
    }

    /**
     * Gets the name of the domain class for this object
     */
    public String getDefaultDomainClass() {
        return (String) get(DEFAULT_DOMAIN_CLASS);
    }

    /**
     * Asserts that the current user has the specified privilege on
     * this object. The current user is determined via the current
     * KernelContext. If there is no current user, we check if the
     * public has the specified privilege on this object.
     *
     * @throws PermissionException if the user does not have the privilege
     **/
    public final void assertPrivilege(PrivilegeDescriptor priv) {
        Party party = Kernel.getContext().getEffectiveParty();
        if (s_log.isDebugEnabled()) {
            s_log.debug("Check privilege " + priv.getName() + 
                        " on object " + getOID() + " against party " +
                        (party == null ? null : party.getOID()));
        }
        PermissionService.assertPermission(new PermissionDescriptor(priv, 
                                                                    this, 
                                                                    party));
    }


    /**
     * Check that the current user has the specified privilege on this
     * object. The current user is determined via the current
     * KernelContext. If there is no current user, we check if the
     * public has the specified privilege on this object.
     *
     * @return true if the current user has the specified privilege on
     * this object, false otherwise
     **/
    public final boolean checkPrivilege(PrivilegeDescriptor priv) {
        Party party = Kernel.getContext().getEffectiveParty();
        return PermissionService.checkPermission(new PermissionDescriptor(priv, 
                                                                          this, 
                                                                          party)
                                                 );
    }


    /**
     * This method is called by the PermissionsObserver when an object
     * is created.  Override this method as needed for implementing a
     * specific permission policy. The default checks to make sure the
     * user has the CREATE privilege on the parent container. If the
     * parent container does not exist, the call is logged but nothing
     * else happens.
     */
    public void doCreateCheck() {
        ACSObject container = getContainer();
        if ( container != null ) {
            container.assertPrivilege(PrivilegeDescriptor.CREATE);
        } else {
            /**
             * A more security safe approach is to throw a PermissionException:
             *
             * throw new PermissionException(PrivilegeDescriptor.CREATE,
             *                               this,
             *                               "Parent container is null.");
             *
             * However, we don't do this because the concept of a
             * container for everything in the system is immature
             * and not consistent.
             */
            SecurityLogger.log(Priority.INFO, "No parent container for " 
                                               + this.getOID() + ".");
            return;
        }
    }

    public void doWriteCheck() {
        assertPrivilege(PrivilegeDescriptor.WRITE);
    }

    @Override
    protected void beforeSave() {
        // set the display name property if necessary
        String displayName = getDisplayName();
        if (displayName != null &&
            !displayName.equals(get(DISPLAY_NAME))) {

            // If the display name returned is too long for the
            // display_name column (200 bytes), truncate it.
            if (DbHelper.varcharLength(displayName) > 200) {
                displayName = DbHelper.truncateString(displayName, 197) + "...";
            }

            set(DISPLAY_NAME, displayName);
        }

        // set the denormalized container if necessary
        if (isContainerModified()) {
            ACSObject container = getContainer();
            setAssociation(CONTAINER, container);
        }

        super.beforeSave();
    }

    static BigDecimal generateID() throws PersistenceException {
        try {
            return Sequences.getNextValue();
        } catch (SQLException e) {
            final String errorMsg = "Unable to generate a unique " +
                "ACSObject id.";
            s_log.error(errorMsg);
            throw PersistenceException.newInstance(errorMsg, e);
        }
    }
}
