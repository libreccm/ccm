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

import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.security.Crypto;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.Filter;
import com.arsdigita.util.UncheckedWrapperException;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.GeneralSecurityException;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

/**
 * Provides user authentication methods on a contained
 * User object.
 *
 * @author Phong Nguyen
 * @version 1.0
 *
 * @see com.arsdigita.kernel.User
 **/
public class UserAuthentication extends DomainObject {

    public static final String versionId = "$Id: UserAuthentication.java 1230 2006-06-22 11:50:59Z apevec $ by $Author: apevec $, $DateTime: 2004/08/16 18:10:38 $";
    private static final Logger s_log =
        Logger.getLogger(UserAuthentication.class.getName());

    private User m_user;
    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.kernel.UserAuthentication";
    /**
     * Retrieves a UserAuthentication object for a user.
     *
     * @param user the user for which to retrieve authentication
     *
     * @return the UserAuthentication object for the specified user.
     *
     * @exception DataObjectNotFoundException if the specified user does not
     * have any associated user authentication object.
     */
    public static UserAuthentication retrieveForUser(User user)
        throws DataObjectNotFoundException
    {
        return new UserAuthentication(getOIDfromUserOID(user.getOID()));
    }

    /**
     * Retrieves a UserAuthentication object for the user with the specified OID.
     *
     * @param userOID the OID of the user for which to retrieve authentication
     *
     * @return the UserAuthentication object for the specified user.
     *
     * @exception DataObjectNotFoundException if the specified user does not
     * have any associated user authentication object.
     */
    public static UserAuthentication retrieveForUser(OID userOID)
        throws DataObjectNotFoundException
    {
        return new UserAuthentication(getOIDfromUserOID(userOID));
    }

    /**
     * Retrieves a UserAuthentication object for a user with the specified ID.
     *
     * @param userID The value of the ID property of the user for which to
     * retrieve authentication
     *
     * @return the UserAuthentication object for the specified user.
     *
     * @exception DataObjectNotFoundException if the specified user does not
     * have any associated user authentication object.
     */
    public static UserAuthentication retrieveForUser(BigDecimal userID)
        throws DataObjectNotFoundException
    {
        return new UserAuthentication(new OID(BASE_DATA_OBJECT_TYPE, userID));
    }

    /**
     * Retrieves a UserAuthentication object given a login name (currently,
     * this is the primary email address of the user).
     *
     * @param loginName the loginName of the user authentication object to
     * retrieve
     *
     * @return the UserAuthentication object for the specified login name.
     *
     * @exception DataObjectNotFoundException if the specified user does not
     * have any associated user authentication object.
     */
    public static UserAuthentication retrieveForLoginName(String loginName)
        throws DataObjectNotFoundException {

        DataQuery query = SessionManager.getSession().retrieveQuery(
                "com.arsdigita.kernel.UserAuthenticationForLogin");

        if (Kernel.getConfig().getPrimaryUserIdentifier().equals("email")) {
            Filter f = query.addFilter("primaryEmail=:primaryEmail");
            f.set("primaryEmail", loginName.toLowerCase());
        } else {
            Filter f = query.addFilter("lowerScreenName=:lowerScreenName");
            f.set("lowerScreenName", loginName.toLowerCase());
        }
        if (!query.next()) {
            throw new DataObjectNotFoundException(
                    "Could not retrieve a UserAuthentication object "
                            + "for login name: " + loginName);
        }
        UserAuthentication user = new UserAuthentication(new OID(
                BASE_DATA_OBJECT_TYPE, query.get("id")));
        query.close();
        return user;
    }

    /**
     * Creates a UserAuthentication object for a user.
     *
     * @param user the user for which to create a new user authentication
     *
     * @return a new user authentication object.
     */
    public static UserAuthentication createForUser(User user) {
        UserAuthentication auth = new UserAuthentication();
        auth.setUser(user);
        //Party party = (Party) DomainObjectFactory.newInstance(new OID(Party.BASE_DATA_OBJECT_TYPE, user.getID()));
        auth.set("party", user);
        return auth;
    }


    /**
     * Creates a UserAuthentication object for the user with the specified OID.
     *
     * @param userOID the OID of the user for which to create a
     * new user authentication
     *
     * @return a new user authentication object.
     */
    public static UserAuthentication createForUser(OID userOID)
        throws DataObjectNotFoundException
    {
        UserAuthentication auth = new UserAuthentication();
        auth.setUser(userOID);
        return auth;
    }

    /**
     * Creates a UserAuthentication object for the user with the specified ID.
     *
     * @param user The value of the ID property of the user for which
     * to create a new user authentication
     *
     * @return a new user authentication object.
     */
    public static UserAuthentication createForUser(BigDecimal userID)
        throws DataObjectNotFoundException
    {
        UserAuthentication auth = new UserAuthentication();
        auth.setUser(new OID(User.BASE_DATA_OBJECT_TYPE, userID));
        return auth;
    }

    /**
     * Default constructor.
     **/
    private UserAuthentication() {
        super(BASE_DATA_OBJECT_TYPE);
    }

    /**
     * Creates a UserAuthentication from the given OID.
     *
     * @param authOID OID of the authorization object to retrieve.
     *
     * @see com.arsdigita.persistence.OID
     **/
    private UserAuthentication(OID authOID)
        throws DataObjectNotFoundException
    {
        super(authOID);
    }

    /**
     * Creates a new UserAuthenticaion from the given DataObject.
     * Package-visible.
     *
     * @param dataObject the DataObject to use
     *
     * @see com.arsdigita.persistence.DataObject
     **/
    UserAuthentication(DataObject dataObject)
    {
        super(dataObject);
    }

    /**
     * Returns the <code>User</code> object to provide authentication
     * to.
     *
     * @return the <code>User</code> object to provide authentication
     * to.
     **/
    public User getUser() {
        if (m_user == null) {
            m_user = User.retrieve((DataObject) get("user"));
        }
        return m_user;
    }

    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    /*****************************
     * Data manipulation methods *
     *****************************/

    /**
     * Hashes this <code>User</code>'s password if it is not already hashed.
     * Used to upgrade from non-hashed deployments. Package-visible.
     **/
    void hashPassword() {
        if (get("salt") != null) {
            s_log.debug("hashPassword: password already hashed");
            return;
        }
        String password = (String)get("password");
        if (password == null) {
            s_log.debug("hashPassword: password is null");
            return;
        }
        // hash and set the password
        setPassword(password.trim());
    }

    /**
     * Hashes the given password using the salt for this <code>User</code>.
     *
     * @param password the password to hash
     *
     * @return the base-64 encoded hashed password
     *
     * @throws IllegalArgumentException if password is null or has leading
     * or trailing spaces
     **/
    private String hashPassword(String password) {
        // check the password
        if (password == null) {
            throw new IllegalArgumentException
                ("password must not be null");
        }
        if (!password.trim().equals(password)) {
            throw new IllegalArgumentException
                ("password must not have leading or trailing spaces");
        }

        try {
            // hash the password and the salt
            MessageDigest digester = Crypto.newDigester();
            digester.update(password.getBytes("UTF-8"));
            digester.update(getSalt());
            String digest = new String((new Base64()).encode(digester.digest()));
            s_log.debug("hashPassword: digest == <"+digest+">");
            return digest;
        } catch (UnsupportedEncodingException e) {
            throw new UncheckedWrapperException
                ("Could not encode password", e);
        } catch (GeneralSecurityException e) {
            throw new UncheckedWrapperException
                ("Could not calculate password hash", e);
        }
    }

    /**
     * Retrieves the salt for this <code>User</code>.
     *
     * @return the salt binary value
     *
     * @throws IllegalStateException if the salt is null
     **/
    private byte[] getSalt() {
        String salt = (String)get("salt");
        s_log.debug("getSalt: salt == <"+salt+">");
        if (salt == null) {
            throw new IllegalStateException("salt must not be null");
        }

        return (new Base64()).decode(salt.getBytes());
    }

    /**
     * Sets the salt for this <code>User</code>.  The salt is stored as a
     * base-64 encoded string.
     *
     * @param salt the salt binary value
     **/
    private void setSalt(byte[] salt) {
        set("salt", new String((new Base64()).encode(salt)));
    }

    /**
     * Sets the salt  for this <code>User</code> to a new random value.
     **/
    private void setNewSalt() {
        try {
            byte[] salt = new byte[16];
            Crypto.getRandom().nextBytes(salt);
            setSalt(salt);
        } catch (GeneralSecurityException e) {
            throw new UncheckedWrapperException
                ("Could not generate new salt", e);
        }
    }

    /**
     * Sets the password for this user.  Sets the salt to a new
     * random value.  The password is hashed with the salt and stored as a
     * base-64 encoded string.
     *
     * @param password the new password for the user
     *
     * @throws IllegalArgumentException if the password is null or it has leading
     * or trailing spaces.
     **/
    public void setPassword(String password) {
        setNewSalt();
        set("password", hashPassword(password));
    }

    /**
     * Determines whether the given password matches the password for this
     * user.
     *
     * @param password the password to verify
     *
     * @return <code>true</code> if the password is valid; <code>false</code> otherwise.
     *
     * @throws IllegalArgumentException if the password is null or if it has leading
     * or trailing spaces.
     **/
    public boolean isValidPassword(String password) {
        String stored = (String)get("password");
        s_log.debug("isValidPassword: stored == <"+stored+">");
        return hashPassword(password).equals(stored);
    }

    /**
     * Retrieves the question used to reset the password for this
     * user.
     *
     * @return the password question.
     **/
    public String getPasswordQuestion() {
        return (String)get("passwordQuestion");
    }

    /**
     * Sets the question used to reset the password for this
     * user.
     *
     * @param question the password question
     **/
    public void setPasswordQuestion(String question) {
        set("passwordQuestion", question);
    }

    /**
     * Sets the answer to the password question for this user.
     *
     * @param answer the password question's answer
     **/
    public void setPasswordAnswer(String answer) {
        set("passwordAnswer", answer);
    }

    /**
     * Determines whether the given answer matches the answer for this
     * user.
     *
     * @param answer the answer to verify
     *
     * @return <code>true</code> if the answer is valid; <code>false</code> otherwise.
     **/
    public boolean isValidAnswer(String answer) {
        if (answer == null) {
            return false;
        }
        return answer.equals(get("passwordAnswer"));
    }

    /**
     * External login name.
     * Assumed to be a valid single-signon account name and supplied via
     * e.g. authentication module in front-end Apache.
     * 
     * @return SSO login name 
     */
    public String getSSOlogin() {
        return (String)get("ssoLogin");
    }
    
    /**
     * Set external login name.
     * @param ssoLogin SSO login name
     */
    public void setSSOlogin(String ssoLogin) {
        set("ssoLogin", ssoLogin);
    }
    
    /**
     * Retrieves a UserAuthentication object given a SSO login name.
     * @param ssoLogin SSO login name
     * @return the UserAuthentication object for the specified SSO login name
     * @exception DataObjectNotFoundException if the specified SSO login is not found
     */
    public static UserAuthentication retrieveForSSOlogin(String ssoLogin)
    throws DataObjectNotFoundException {

        DataQuery query = SessionManager.getSession().retrieveQuery(
                "com.arsdigita.kernel.UserAuthenticationForLogin");
        Filter f = query.addFilter("ssoLogin=:ssoLoginName");
        f.set("ssoLoginName", ssoLogin.toLowerCase());
        if (!query.next()) {
            throw new DataObjectNotFoundException(
                    "Could not retrieve a UserAuthentication object "
                            + "for SSO login name: " + ssoLogin);
        }
        UserAuthentication userAuth = new UserAuthentication(new OID(
                BASE_DATA_OBJECT_TYPE, query.get("id")));
        query.close();
        return userAuth;
    }
    
    /*******************
     * PRIVATE METHODS *
     *******************/

    private static OID getOIDfromUserOID(OID userOID) {
        return new OID(BASE_DATA_OBJECT_TYPE, userOID.get("id"));
    }

    /**
     * Sets the contained <code>User</code> with an
     * <code>OID</code> specified by <code>userOID</code> to provide
     * authentication to.
     *
     * @param userOID The <code>OID</code> for the <code>User</code>
     * to provide authentication to.
     *
     * @see com.arsdigita.persistnce.OID
     **/
    private void setUser(OID userOID) throws DataObjectNotFoundException {
        setUser(User.retrieve(userOID));
    }

    /**
     * Sets the contained <code>User</code> specified by
     * <emphasis>user</emphasis> to provide authentication to.
     *
     * @param user The <code>User</code> to provide authentication to.
     *
     * @see com.arsdigita.kernel.User
     **/
    private void setUser(User user) {
        if (m_user != null) {
            throw new RuntimeException("This UserAuthentication instance " +
                                       "already containes a User object.");
        }
        set("id", user.getID());
        setAssociation("user", user);
	set("primaryEmail", user.getPrimaryEmail().getEmailAddress());
	set("screenName", user.getScreenName());
        m_user = user;
    }


    /**
     * Returns the login name used for the user specified by
     * <i>user</i>.
     *
     * @param user The user to retrieve a login name for.
     *
     * @return The login name used for the user.
     **/
    private static String getLoginNameForUser(User user) {
	if (KernelHelper.emailIsPrimaryIdentifier()) {
	    return user.getPrimaryEmail().getEmailAddress();
	}
	return user.getScreenName();
    }

}
