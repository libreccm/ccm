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
package com.arsdigita.web;

import com.arsdigita.kernel.Group;
import com.arsdigita.kernel.ResourceType;
import com.arsdigita.kernel.Stylesheet;
import com.arsdigita.kernel.PackageType;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.persistence.DataAssociationCursor;
import com.arsdigita.persistence.PersistenceException;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.db.Sequences;
import com.arsdigita.util.Assert;
import java.util.LinkedList;
import java.util.Collection;
import java.math.BigDecimal;
import java.sql.SQLException;
import org.apache.log4j.Logger;

/**
 * The persistent type information of an Application.
 *
 * @see com.arsdigita.web.Application
 * @see com.arsdigita.web.ApplicationTypeCollection
 * @author Justin Ross &lt;<a href="mailto:jross@redhat.com">jross@redhat.com</a>&gt;
 * @version $Id: ApplicationType.java 1520 2007-03-22 13:36:04Z chrisgilbert23 $
 */
public class ApplicationType extends ResourceType {

    /** The logging object for this class. */
    private static final Logger s_log = Logger.getLogger
        (ApplicationType.class);

    /**
     * The fully qualified model name of the underlying data object, which in
     * this case is the same as the Java type (full qualified class name).
     */
    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.web.ApplicationType";

    private PackageType m_packageType;
    boolean m_legacyFree = false;

    /**
     * Constructor creates a new ApplicationType instance to encapsulate a given
     * data object (@see com.arsdigita.persistence.Session#retrieve(String) ).
     * The super implementation uses overwritable methods initialize() and
     * postInitialization() to further process the dataObject (see below).
     * 
     * @param dataObject
     */
    public ApplicationType(DataObject dataObject) {
        super(dataObject);
    }

    protected ApplicationType(String dataObjectType) {
        super(dataObjectType);
    }

    protected ApplicationType(final String objectType,
                              final String title,
                              final String applicationObjectType) {
        this(objectType, title, applicationObjectType, false);

    }

    /**
     * Internal constructor creates a (new) legacy free application type.
     * 
     * @param objectType 
     * @param title
     * @param applicationObjectType
     * @param createContainerGroup
     */
    protected ApplicationType(final String objectType,
			      final String title,
			      final String applicationObjectType,
			      final boolean createContainerGroup) {
        this(objectType);  // creates and returns an empty data object

        Assert.exists(title, "String title");
        Assert.exists(applicationObjectType, "String applicationObjectType");

        setTitle(title);
        setApplicationObjectType(applicationObjectType);

        setDefaults();
	if (createContainerGroup) {
	    createGroup();
	}
        m_legacyFree = true;
    }

    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    // ensure legacy free instance variable is set correctly 
    // previously only set on creation of application type 
    // (to be honest I can't remember the problem that was 
    // causing, but it did cause a problem in some 
    // circumstances)
    // Method overwrtes a (overwritable) method provided by the super class to
    // process a created (empty) data object.
    public void initialize() {
        super.initialize();
        s_log.debug("initialising application type "); 
        if (!isNew() && getPackageType() == null) {
            s_log.debug("legacy free type");
            m_legacyFree = true;
            
        }
    }


    private void setDefaults() {
        // Defaults for standalone applications.
        setFullPageView(true);
        setEmbeddedView(false);
        setWorkspaceApplication(true);
        setSingleton(false);
    }

    /**
     * Creates a new "legacy free" application type.
     * 
     * Types created via this constructor are "legacy free" and do not create
     * a legacy package type for compatibility with older applications.
     */
    public ApplicationType(final String title,
                           final String applicationObjectType) {
        this(BASE_DATA_OBJECT_TYPE, title, applicationObjectType);
    }

    /**
     * Creates a legacy-compatible application type.  Types created
     * via this constructor use the passed in package type to back the
     * new application type.
     */
    protected ApplicationType(final String dataObjectType,
                              final PackageType packageType,
                              final String title,
                              final String applicationObjectType) {
        this(dataObjectType, packageType, title, applicationObjectType, false);
 
    }

    
    protected ApplicationType(final String dataObjectType,
			      final PackageType packageType,
			      final String title,
			      final String applicationObjectType,
			      boolean createContainerGroup) {
        this(dataObjectType);

        Assert.exists(title, "title");
        Assert.exists(applicationObjectType, "applicationObjectType");
        Assert.exists(packageType, "packageType");

        m_packageType = packageType;
        setPackageType(m_packageType);

        setTitle(title);
        setApplicationObjectType(applicationObjectType);

        setDefaults();
	if (createContainerGroup) {
	    createGroup();
	}
    }

    /**
     * Creates a legacy-compatible application type using the passed
     * in package type using an internal constructor (below).
     */
    public static ApplicationType createApplicationType(PackageType packageType, 
                                                        String title,
                                                        String applicationObjectType) {
        return new ApplicationType
            (BASE_DATA_OBJECT_TYPE, packageType, title, applicationObjectType);
    }

    /**
     * Creates a legacy-compatible application type.  The key
     * parameter is used to create a legacy package type to back the
     * new application type.
     * This variant is applicatable if packageType does not already exist!
     */
    protected ApplicationType(String dataObjectType, String key, String title,
                              String applicationObjectType) {
        this(dataObjectType, makePackageType(key, title), title,
             applicationObjectType);
    }

    /**
     * Helper method to create a packageType for a new legacy compatible 
     * application type without a already existing (i.e. installed in db) legacy
     * application.
     * 
     * @param key of the package to be created
     * @param title of the package to be created
     * @return
     */
    private static final PackageType makePackageType(String key, String title) {
        PackageType packageType = new PackageType();

        Assert.exists(key, "key");
        Assert.exists(title, "title");

        packageType.setKey(key);
        packageType.setDisplayName(title);
        packageType.setURI("http://arsdigita.com/" + key);

        return packageType;
    }

    /**
     * Creates a legacy-compatible application type.  The key
     * parameter is used to create a legacy package type to back the
     * new application type.
     */
    public static ApplicationType createApplicationType(String key, String title,
                                                        String applicationObjectType) {
        	
        return ApplicationType.createApplicationType(
                                      key, title, applicationObjectType, false);
       
    }

    /**
     * Creates a legacy-compatible application type.  The key
     * parameter is used to create a legacy package type to back the
     * new application type.
     */
    public static ApplicationType createApplicationType(
                                  String key, String title,
                                  String applicationObjectType,
                                  boolean createContainerGroup) {
        // See if the package type is already present.

        PackageType packageType = null;

        try {
            packageType = PackageType.findByKey(key);

            return new ApplicationType
                (BASE_DATA_OBJECT_TYPE, packageType, title,
                 applicationObjectType, createContainerGroup);
        } catch (DataObjectNotFoundException nfe) {
            return new ApplicationType
                (BASE_DATA_OBJECT_TYPE, key, title, applicationObjectType);
        }
    }
    
    // Param
    public static ApplicationType retrieveApplicationType(BigDecimal id) {
        Assert.exists(id, "id");

        return ApplicationType.retrieveApplicationType
            (new OID(ApplicationType.BASE_DATA_OBJECT_TYPE, id));
    }

    // Param oid cannot be null.
    public static ApplicationType retrieveApplicationType(OID oid) {
        Assert.exists(oid, "oid");

        DataObject dataObject = SessionManager.getSession().retrieve(oid);

        Assert.exists(dataObject);

        return ApplicationType.retrieveApplicationType(dataObject);
    }

    // Param dataObject cannot be null.  Can return null?
    public static ApplicationType retrieveApplicationType
        (DataObject dataObject) {
        Assert.exists(dataObject, "dataObject");

        return new ApplicationType(dataObject);
    }

    // Can return null.
    public static ApplicationType retrieveApplicationTypeForApplication
        (String applicationObjectType) {
        Assert.exists(applicationObjectType, "applicationObjectType");

        DataCollection collection =
            SessionManager.getSession().retrieve(BASE_DATA_OBJECT_TYPE);

        collection.addEqualsFilter("objectType", applicationObjectType);

        ApplicationType applicationType = null;

        if (collection.next()) {
            applicationType = ApplicationType.retrieveApplicationType
                (collection.getDataObject());
        }

        collection.close();

        return applicationType;
    }

    public static ApplicationTypeCollection retrieveAllApplicationTypes() {
        DataCollection collection =
            SessionManager.getSession().retrieve(BASE_DATA_OBJECT_TYPE);

        Assert.exists(collection, "collection");

        collection.addEqualsFilter("hasFullPageView", Boolean.TRUE);

        return new ApplicationTypeCollection(collection);
    }

    //
    // Association properties (some by proxy)
    //

    // Can return null.
    public PackageType getPackageType() {
        if (m_legacyFree == true) {
            throw new UnsupportedOperationException
                ("This method is only supported for legacy application types");
        }

        final DataObject dataObject = (DataObject) get("packageType");

        if (dataObject == null) {
            return null;
        } else {
            return new PackageType(dataObject);
        }
    }

    /**
     * @deprecated with no replacement.
     * @throws UnsupportedOperationException when this method is
     * called for an application type without a corresponding package
     * type.
     */
    protected void setPackageType(PackageType packageType) {
        if (m_legacyFree == true) {
            throw new UnsupportedOperationException
                ("This method is only supported for legacy application types");
        }

        Assert.exists(packageType, "packageType");
        setAssociation("packageType", packageType);
    }

    /**
     * @deprecated with no replacement.
     * @throws UnsupportedOperationException when this method is
     * called for an application type without a corresponding package
     * type.
     */
    public void addStylesheet(Stylesheet stylesheet) {
        if (m_legacyFree == true) {
            throw new UnsupportedOperationException
                ("This method is only supported for legacy application types");
        }

        if (m_packageType == null) {
            m_packageType = getPackageType();
        }

        m_packageType.addStylesheet(stylesheet);
    }

    /**
     * @deprecated with no replacement.
     * @throws UnsupportedOperationException when this method is
     * called for an application type without a corresponding package
     * type.
     */
    public void setDispatcherClass(String className) {
        if (m_legacyFree == true) {
            throw new UnsupportedOperationException
                ("This method is only supported for legacy application types");
        }

        if (m_packageType == null) {
            m_packageType = getPackageType();
        }

        m_packageType.setDispatcherClass(className);
    }

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
        final String description = (String) get("description");

        return description;
    }

    // Param description can be null.
    public void setDescription(String description) {
        set("description", description);
    }

    public boolean isWorkspaceApplication() {
        final Boolean result = (Boolean) get("isWorkspaceApplication");

        Assert.exists(result, "Boolean result");

        return result.booleanValue();
    }

    /**
     * @deprecated with no replacement.
     * @throws UnsupportedOperationException when this method is
     * called for an application type without a corresponding package
     * type.
     */
    public void setWorkspaceApplication(boolean isWorkspaceApplication) {
        if (m_legacyFree == true) {
            throw new UnsupportedOperationException
                ("This method is only supported for legacy application types");
        }

        set("isWorkspaceApplication", new Boolean(isWorkspaceApplication));
    }

    public boolean hasFullPageView() {
        final Boolean result = (Boolean) get("hasFullPageView");

        Assert.exists(result, "Boolean result");

        return result.booleanValue();
    }

    /**
     * @deprecated with no replacement.
     * @throws UnsupportedOperationException when this method is
     * called for an application type without a corresponding package
     * type.
     */
    protected void setFullPageView(boolean hasFullPageView) {
        if (m_legacyFree == true) {
            throw new UnsupportedOperationException
                ("This method is only supported for legacy application types");
        }

        set("hasFullPageView", new Boolean(hasFullPageView));
    }

    public boolean hasEmbeddedView() {
        final Boolean result = (Boolean) get("hasEmbeddedView");

        Assert.exists(result, "Boolean result");

        return result.booleanValue();
    }

    /**
     * @deprecated with no replacement.
     * @throws UnsupportedOperationException when this method is
     * called for an application type without a corresponding package
     * type.
     */
    protected void setEmbeddedView(boolean hasEmbeddedView) {
        if (m_legacyFree == true) {
            throw new UnsupportedOperationException
                ("This method is only supported for legacy application types");
        }

        set("hasEmbeddedView", new Boolean(hasEmbeddedView));
    }

    // Can return null.
    public String getProfile() {
        String profile = (String) get("profile");

        return profile;
    }

    // Param profile can be null.
    protected void setProfile(String profile) {
        set("profile", profile);
    }


    /**
     * <p>Get the list of relevant privileges for this
     * ApplicationType.</p>
     *
     * @return A Collection of {@link PrivilegeDescriptor PrivilegeDescriptors}
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
     * ApplicationType.</p>
     */
    public void addRelevantPrivilege(PrivilegeDescriptor privilege) {
        addRelevantPrivilege(privilege.getName());
    }

    /**
     * <p>Add an entry to the list of relevant privileges for this
     * ApplicationType.</p>
     */
    public void addRelevantPrivilege(String privilegeName) {
        OID privOID = new OID("com.arsdigita.kernel.permissions.Privilege",
                              privilegeName);
        DataObject privDO = SessionManager.getSession().retrieve(privOID);
        add("relevantPrivileges", privDO);
    }

    /**
     * <p>Remove an entry from the list of relevant privileges for
     * this ApplicationType.</p>
     */
    public void removeRelevantPrivilege(PrivilegeDescriptor privilege) {
        removeRelevantPrivilege(privilege.getName());
    }

    /**
     * <p>Remove an entry from the list of relevant privileges for
     * this ApplicationType.</p>
     */
    public void removeRelevantPrivilege(String privilegeName) {
        OID privOID = new OID("com.arsdigita.kernel.permissions.Privilege",
                              privilegeName);
        DataObject privDO = SessionManager.getSession().retrieve(privOID);
        remove("relevantPrivileges", privDO);
    }

    public String getApplicationObjectType() {
        String objectType = (String)get("objectType");

        Assert.exists(objectType);

        return objectType;
    }

    protected void setApplicationObjectType(String objectType) {
        Assert.exists(objectType);

        set("objectType", objectType);
    }

    /**
     * Declare this ApplicationType to be a singleton.  That is to
     * say, there ought to only ever be one Application of this type
     * directly under a given Workspace.
     * @deprecated with no replacement.
     * @throws UnsupportedOperationException when this method is
     * called for an application type without a corresponding package
     * type.
     */
    public void setSingleton(boolean isSingleton) {
        if (m_legacyFree == true) {
            throw new UnsupportedOperationException
                ("This method is only supported for legacy application types");
        }

        set("isSingleton", new Boolean(isSingleton));
    }

    /**
     * Tell whether this ApplicationType is a singleton.
     */
    public boolean isSingleton() {
        final Boolean result = (Boolean) get("isSingleton");

        Assert.exists(result, "Boolean result");

        return result.booleanValue();
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

    public String getStylesheet() {
        return "/__ccm__/xsl/default.xsl";
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
    
    public void setGroup(Group group) {
	setAssociation("containerGroup", group);
    }
    
    public void createGroup () {
    	Assert.isEqual(getGroup(), null, "Group has already been created for " +
                     "Application Type " + getTitle());
    	Group group = new Group();
    	group.setName(getTitle() + " Groups");
	setAssociation("containerGroup", group);
    
    }
    
    /**
     * Group associated with this application type. Usually
     * used as a container group to keep group admin tidy.
     * 
     * @return null if no group is associated with this application type
     */
    public Group getGroup() {
    	
	return (Group) DomainObjectFactory.newInstance(
					(DataObject) get("containerGroup"));

    }
}
