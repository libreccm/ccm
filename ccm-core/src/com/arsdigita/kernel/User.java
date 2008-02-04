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

// Identity class.
import java.math.BigDecimal;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.persistence.DataAssociationCursor;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataOperation;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.PersistenceException;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.metadata.ObjectType;
import org.apache.log4j.Logger;

/**
 * Represents a user.
 *
 * @author Phong Nguyen
 * @version 1.0
 **/
public class User extends Party {

    public static final String versionId = "$Id: User.java 1586 2007-05-31 13:05:10Z chrisgilbert23 $ by $Author: chrisgilbert23 $, $DateTime: 2004/08/16 18:10:38 $";

    private PersonName m_name;

    private boolean m_external;
    
	/** An attribute name for the underlying data object. */
	public static final String BANNED = "banned";


    /**
     * Every instance of group must encapsulate a data object whose
     * object type is either this base type or a subtype of this base type.
     */
    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.kernel.User";

    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    /**
     * Retrieves a user with the given ID.  Use this method instead of
     * <code>new User(BigDecimal)</code>.  This method uses the domain
     * object factory to produce the appropriate user class for the
     * data object of type <code>User.BASE_DATA_OBJECT_TYPE</code>
     * identified by ID.
     *
     * @param id the ID for the
     * <code>DataObject</code> to retrieve
     *
     * @return the user with the specified ID.
     *
     * @see Party#Party(OID)
     * @see #BASE_DATA_OBJECT_TYPE
     * @see com.arsdigita.persistence.DataObject
     * @see com.arsdigita.persistence.OID
     **/
    public static User retrieve(BigDecimal id)
        throws DataObjectNotFoundException
    {
        return retrieve(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    /**
     * Retrieves a user with the given OID.  Use this method instead of
     * the new User(OID) method.  This method uses the domain object
     * factory to produce the appropriate user class.
     *
     * @param oid the OID for the retrieved User
     * <code>DataObject</code>
     *
     * @return  the user with the specified OID.
     *
     * @exception DataObjectNotFoundException when
     * no user could be retrieved with the given OID.
     *
     * @see Party#Party(OID)
     * @see com.arsdigita.persistence.DataObject
     * @see com.arsdigita.persistence.OID
     * @see DomainObjectFactory#newInstance(OID)
     **/
    public static User retrieve(OID oid)
        throws DataObjectNotFoundException
    {
        User user = (User) DomainObjectFactory.newInstance(oid);
        if (user==null) {
            throw new
                DataObjectNotFoundException("Domain object factory " +
                                            "produced null user for OID " +
                                            oid);
        }
        return user;
    }

    /**
     * Returns a user for the given data object.  Use this method instead
     * of the new User(DataObject) method.  This method uses the domain
     * object factory to produce the appropriate user class.
     *
     * @param userData the user <code>DataObject</code>
     *
     * @return  the user for the given data object.
     **/
    public static User retrieve(DataObject userData) {
        User user = (User) DomainObjectFactory.newInstance(userData);

        if (user==null) {
            throw new RuntimeException("Domain object factory produced " +
                                       "null user for data object " +
                                       userData);
        }
        return user;
    }

    /**
     * Retrieves all users.
     *
     * @return  a collection of all users.
     */
    public static UserCollection retrieveAll() {
        return new UserCollection( SessionManager.getSession()
                                   .retrieve(BASE_DATA_OBJECT_TYPE) );
    }

    /**
     * @deprecated Use {@link #retrieve(DataObject)}
     */
    public User(DataObject userData) {
        super(userData);
    }

    /**
     * Default constructor. The contained <code>DataObject</code> is
     * initialized with a new <code>DataObject</code> with an
     * <code>ObjectType</code> of "User".
     *
     * An external user is not granted admin permission on their
     * user object (so they cannot edit their attributes)
     *
     * @see Party#Party(String)
     * @see com.arsdigita.persistence.DataObject
     * @see com.arsdigita.persistence.metadata.ObjectType
     **/
    public User(boolean external) {
        this(BASE_DATA_OBJECT_TYPE);
    	m_external = external;
    }
    
    
    
    public User() {
    	this (false);
        
    }

    /**
     * Convenience constructor
     * @param givenName User's first name
     * @param familyName User's last name
     * @param email User's email address
     * @param external Whether attributes are retrieved from an external source
     * An external user is not granted admin permission on their
     * user object (so they cannot edit their attributes)
     *
     **/
    public User(String givenName, String familyName, String email, boolean external) {
        this(BASE_DATA_OBJECT_TYPE);
        getPersonName().setGivenName(givenName);
        getPersonName().setFamilyName(familyName);
        setPrimaryEmail(new EmailAddress(email));
        m_external = external;
    }

    public User(String givenName, String familyName, String email) {
    	this (givenName, familyName, email, false);
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
     * @see Party#Party(ObjectType)
     * @see com.arsdigita.persistence.DataObject
     * @see com.arsdigita.persistence.metadata.ObjectType
     **/
    public User(String typeName) {
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
     * @see Party#Party(ObjectType)
     * @see com.arsdigita.persistence.DataObject
     * @see com.arsdigita.persistence.metadata.ObjectType
     **/
    public User(ObjectType type) throws DataObjectNotFoundException {
        super(type);
    }

    /**
     * Constructor. The contained <code>DataObject</code> is retrieved
     * from the persistent storage mechanism with an <code>OID</code>
     * specified by <i>oid</i>.
     *
     * @param oid the <code>OID</code> for the retrieved
     * <code>DataObject</code>
     *
     * @deprecated Use {@link #retrieve(OID)} instead.  This constructor will
     * eventually be made protected.
     *
     * @see #retrieve(OID)
     * @see Party#Party(OID)
     * @see com.arsdigita.persistence.DataObject
     * @see com.arsdigita.persistence.OID
     **/
    public User(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    /**
     * Constructor. The contained <code>DataObject</code> is retrieved
     * from the persistent storage mechanism with an <code>OID</code>
     * specified by <i>id</i> and <code>User.BASE_DATA_OBJECT_TYPE</code>.
     *
     * @param id the <code>id</code> for the retrieved
     * <code>DataObject</code>
     *
     * @deprecated Use {@link #retrieve(BigDecimal)} instead.
     * This constructor will eventually be made protected.
     *
     * @see #retrieve(BigDecimal)
     * @see Party#Party(OID)
     * @see #BASE_DATA_OBJECT_TYPE
     * @see com.arsdigita.persistence.DataObject
     * @see com.arsdigita.persistence.OID
     **/
    public User(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    /**
     * Called from base class (DomainObject) constructors.
     */
    protected void initialize() {
        super.initialize();
        if (isNew()) {
            m_name = new PersonName();
            setAssociation("name", m_name);

			set(BANNED, new Boolean(false));
        }
    }

    /**
     * Returns the screen name for this user.
     *
     * @return the screen name for the user.
     **/
    public String getScreenName() {
        return (String) get("screenName");
    }

    /**
     * Sets the screen name for this user.
     *
     * @param screenName the screen name to set for this user
     **/
    public void setScreenName(String screenName) {
        set("screenName", screenName);
    }

    /**
     * Returns the name of this user as a PersonName object.
     *
     * @return the name of this user as a PersonName object.
     *
     * @see PersonName
     **/
    public PersonName getPersonName() {
        if (m_name == null) {
            DataObject nameData = (DataObject) get("name");
            m_name = new PersonName(nameData);
        }
        return m_name;
    }

    /**
     * Returns the name of this user.
     *
     * @return the name of this user.
     **/
    public String getName() {
        return getPersonName().toString();
    }

    /**
     * Marks the specified emailAddress as this user's
     * primary email address.  If this party does not already
     * have the specified emailAddress as an email address, it
     * will be added using the addEmailAddress() method.
     *
     * @param emailAddress the email address to set as primary
     *
     * @pre emailAddress != null
     *
     * @see Party#addEmailAddress
     * @see EmailAddress
     **/
    public void setPrimaryEmail(EmailAddress emailAddress) {
        if (emailAddress == null) {
            throw new IllegalArgumentException("User's primary email cannot be null");
        }
        super.setPrimaryEmail(emailAddress);
    }

    /**
     *
     * Returns all groups that the user is a direct or indirect member of.
     * For example, if the user is in groups A and B, and group A is a
     * subgroup of group C, this method returns groups A, B, and C.
     *
     * @return the groups that this user is a direct or indirect member of.
     *
     **/
    public GroupCollection getAllGroups() {
        DataAssociationCursor assoc =
            ((DataAssociation) get("allGroups")).cursor();
        return new GroupCollection(assoc);
    }

    /**
     *
     * Returns the groups that this user is a direct member of.
     *
     * @return the groups that user is a direct member of.
     **/
    public GroupCollection getGroups() {
        DataAssociationCursor assoc =
            ((DataAssociation) get("groups")).cursor();
        return new GroupCollection(assoc);
    }

    /**
     * Persists any changes made to this object.
     *
     * @see com.arsdigita.persistence.DataObject#save()
     **/
    protected void beforeSave() throws PersistenceException {
        super.beforeSave();

        // If the domain object is new or the primary email has been changed,
        // validate it.
        if ( (isNew() || isPropertyModified("primaryEmail")) &&
                KernelHelper.emailIsPrimaryIdentifier()) {
            validatePrimaryEmail();
        }
	if ((isNew() || isPropertyModified("primaryEmail")
		   || isPropertyModified("screenName")) &&
	     !KernelHelper.emailIsPrimaryIdentifier()) {

	    if (getPrimaryEmail() == null) {
		throw new RuntimeException("Primary email must be specified");
	    }
	    validateScreenName();
	}

    }


    protected void afterSave() {
        super.afterSave();
	// users have admin permissions on themselves (needed to change 
	// email, for instance).
        if (!m_external) {
		PermissionDescriptor perm = new PermissionDescriptor(PrivilegeDescriptor.ADMIN, this, this);
        PermissionService.grantPermission(perm);
    }
    }

    /**
     * Deletes this user.  The EmailAddress service may retain
     * persistent data about the email addresses that belonged to this
     * user (for example, bouncing status).
     *
     * @see com.arsdigita.persistence.DataObject#delete()
     **/
    public void delete() throws PersistenceException {
        clearUserFromGroups();
        super.delete();
    }

    public void clearUserFromGroups() throws PersistenceException {
        DataOperation op = getDataOperation("com.arsdigita.kernel.ClearUserFromGroups");
        op.setParameter("memberID", getID());
        op.execute();
    }

    protected void validatePrimaryEmail() {

        EmailAddress email = getPrimaryEmail();

        if (email == null) {
            throw new RuntimeException("Primary email must be specified");
        }

        // Verify uniqueness of email
        DataQuery query = SessionManager.getSession()
            .retrieveQuery("com.arsdigita.kernel.UserPrimaryEmail");
        Filter f =
            query.addFilter("primaryEmailAddress=:email " +
                            "and userID != :userID");
        f.set("email", email.getEmailAddress());
        f.set("userID", getID());
        if (query.size()>0) {
            throw new RuntimeException("Primary email must be unique among users");
        }
    }

    protected void validateScreenName() {

        String sn = getScreenName().toLowerCase();

        if (sn == null) {
            throw new RuntimeException("Screen Name must be specified");
        }

        // Verify uniqueness of screen name
        DataQuery query = SessionManager.getSession()
            .retrieveQuery("com.arsdigita.kernel.UserPrimaryEmail");
        Filter f =
            query.addFilter("lowerScreenName=:sn " +
                            "and userID != :userID");
        f.set("sn", sn);
        f.set("userID", getID());
        if (query.size()>0) {
            throw new RuntimeException("Screen Name must be unique among users");
        }
    }

    private DataOperation getDataOperation(String name) {
        return SessionManager.getSession().retrieveDataOperation(name);
    }

	/**
	 * Getter for the banned property, which is persisted to the database
	 */
	public boolean isBanned() {
		return ((Boolean) get(BANNED)).booleanValue();
    }

	/**
	 * Setter for the banned property, which is persisted to the database
	 */
	public void setBanned(boolean b) {
		set(BANNED, new Boolean(b));
	}

}
  
