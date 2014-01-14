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

// Support for persitent objects
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.DataObject;

/**
 * Represents an email address.  While this class
 * does not extend DomainObject and is not a domain object class, it
 * does provide email verification methods, which in turn may manipulate
 * domain objects and persist the changes.
 * <P>
 * The verification-related data
 * about an email address is persistent data, but the persistence details
 * do not concern developers using this class.  Furthermore, developers
 * can instantiate this class for any email address, regardless of whether
 * there is persistent data about the verification status of the email.
 * Persistent data about verification status is only created when necessary.
 * For example, a call to setIsBouncing() will result in persistent storage of
 * the bouncing status for this email address.
 * <P>
 * The primary reason for not making this class extend DomainObject is so
 * that developers can use it for any random email address without that
 * email address necessarily being a persistent object stored somewhere in
 * the database.
 *
 * @author Oumi Mehrotra
 * @version $Id: EmailAddress.java 738 2005-09-01 12:36:52Z sskracic $
 */
public class EmailAddress {

    private String m_emailAddress;
    private EmailAddressRecord m_emailRecord;

    /**
     * Creates a new EmailAddress instance that
     * encapsulates <i>emailAddress</i>.
     *
     * Note that the services offered by this class (to check
     * verification/bouncing status) are insensitive to the case of
     * email address.
     *
     * @param emailAddress the email address to encapsulate
     **/
    public EmailAddress(String emailAddress) {
        if (emailAddress == null) {
            throw new IllegalArgumentException("Cannot create an EmailAddress object " +
                                       "with null emailAddress string");
        }
        m_emailAddress = emailAddress.toLowerCase();
    }

    /**
     * Creates a new EmailAddress instance backed by a
     * data object of type com.arsdigita.kernel.EmailAddress.  Should
     * only be used by classes that involve an association between
     * one of their data object types and the EmailAddress data object type.
     *
     * @param emailData the data object to encapsulate in the new domain
     * object
     **/
    public EmailAddress(DataObject emailData) {
        m_emailRecord = new EmailAddressRecord(emailData);
        m_emailAddress = m_emailRecord.getEmailAddress();
    }

    /**
     * Checks whether this email is set as bouncing.
     *
     * @return <code>true</code> if this email is set as bouncing;
     * <code>false</code> otherwise.
     **/
    public boolean isBouncing() {
        EmailAddressRecord rec = this.getEmailRecord();
        return rec.isBouncing();
    }

    /**
     * Sets whether this email has been verified.
     *
     * @return <code>true</code> if this email has been verified;
     * <code>false</code> otherwise.
     **/
    public boolean isVerified() {
        EmailAddressRecord rec = this.getEmailRecord();
        return rec.isVerified();
    }

    /**
     * Sets whether email is bouncing.  Note that the setting
     * does not persist until transaction commit.
     *
     * @param isBouncing <code>true</code> if this email is set as
     * bouncing, <code>false</code> otherwise
     **/
    public void setIsBouncing(boolean isBouncing) {
        EmailAddressRecord rec = this.getEmailRecord();
        rec.setIsBouncing(isBouncing);
        rec.save();
    }

    /**
     * Sets whether this email is verified.  Note that the setting
     * does not persist until transaction commit.
     *
     * @param isVerified <code>true</code> if this email is verified,
     * <code>false</code> otherwise
     **/
    public void setIsVerified(boolean isVerified) {
        EmailAddressRecord rec = this.getEmailRecord();
        rec.setIsVerified(isVerified);
        rec.save();
    }

    /**
     * Gets the string email address encapsualted by this object.
     *
     * @return the email address encapsualted by this object.
     **/
    public String getEmailAddress() {
        return m_emailAddress;
    }

    /**
     * Gets the string email address encapsualted by this object.
     *
     * @return the email address encapsualted by this object.
     **/
    public String toString() {
        return getEmailAddress();
    }

    /**
     * Checks whether the object specified by <i>o</i> is equal to this.
     * Returns <code>true</code> if the <i>o</i> is not null, is an
     * instance of EmailAddress, and encapsulates the same email address
     * (that is, if
     * <code>o.getEmailAddress().toLower() ==
     * this.getEmailAddress().toLower()</code>).
     **/
    public boolean equals(Object o) {
        if (! (o instanceof EmailAddress) ) {
            return false;
        }

        return this.toString().equals(o.toString());
    }

    public int hashCode() {
        return toString().hashCode();
    }
    /**
     * Gets or creates a
     * new <code>DomainObject</code> for this email address,
     * encapsulating a data object of type
     * <code>com.arsdigita.kernel.EmailAddress</code>.
     * First tries to retrieve a <code>DomainObject</code>
     * for this email address.  If none exists, a new one is created.
     * However, the new <code>DomainObject</code> will *not*
     * persist until one of the setXXX methods in this class is
     * called or the returned <code>DomainObject</code>'s save() method
     * is called.
     * <P>
     * This method is primarily useful for other domain objects whose
     * encapsulated data objects have associations to
     * <code>com.arsdigita.kernel.EmailAddress</code>.
     * Such domain object classes can use
     * <code>getEmailDomainObject().addToAssociation()</code>.
     * For example, the method
     * <code>Party.addEmailAddress(EmailAddress emailAddress)</code>
     * internally calls
     * <code>emailAddress.getDomainObject().addToAssociation(
     *           (DataAssociation) get ("emailAddress")
     * )</code>
     *
     * @return the DomainObject object for this email address.
     */
    protected DomainObject getEmailDomainObject() {
        return getEmailRecord();
    }

    /**
     * Gets or creates a new EmailAddressRecord domain object
     * for this email address.  Tries to retrieve an EmailAddressRecord
     * for this email address.  If none exists, a new one is created;
     * however, the new EmailAddressRecord domain object will *not*
     * persist until setIsBouncing, setIsVerified, or save() is called.
     *
     * @return EmailAddressRecord domain object for this email address.
     */
    private EmailAddressRecord getEmailRecord() {
        if (m_emailRecord == null) {
            try {
                m_emailRecord = new EmailAddressRecord
                    (new OID
                     (EmailAddressRecord.BASE_DATA_OBJECT_TYPE,
                      m_emailAddress));
            } catch (DataObjectNotFoundException e) {
                m_emailRecord = new EmailAddressRecord(m_emailAddress);
            }
        }
        return m_emailRecord;
    }


    private static class EmailAddressRecord extends DomainObject {

        private static final String BASE_DATA_OBJECT_TYPE =
            "com.arsdigita.kernel.EmailAddress";


        /**
         * Returns the base data object type for this domain object class.
         * Intended to be overrided by subclasses whenever the subclass will
         * only work if their primary data object is of a certain base type.
         *
         * @return The fully qualified name ("modelName.typeName") of the base
         * data object type for this domain object class,
         * or null if there is no restriction on the data object type for
         * the primary data object encapsulated by this class.
         **/
        protected String getBaseDataObjectType() {
            return BASE_DATA_OBJECT_TYPE;
        }

        /**
         * Constructor. Creates a new email address record.
         *
         * @param emailAddress The <code>emailAddress</code> for the new
         * object.
         **/
        public EmailAddressRecord(String emailAddress) {
            super(BASE_DATA_OBJECT_TYPE);
            set("emailAddress", emailAddress);
            setIsBouncing(false);
            setIsVerified(false);
        }

        /**
         * Constructor. The contained <code>DataObject</code> is retrieved
         * from the persistent storage mechanism with an OID specified by
         * <i>oid</i>.
         *
         * @exception DataObjectNotFoundException Thrown if we cannot
         * retrieve a data object for the specified OID
         **/
        public EmailAddressRecord(OID oid) throws DataObjectNotFoundException {
            super(oid);
        }

        public EmailAddressRecord(DataObject emailData) {
            super(emailData);
        }

        /**
         * Returns the value of the emailAddress property of the
         * encapsulated data object.
         *
         * @return emailAddress property of the encapsulated data object.
         **/
        public String getEmailAddress() {
            return (String) get("emailAddress");
        }

        /**
         * Returns true if this email is set as bouncing, false otherwise.
         *
         * @return True if this email is set as bouncing, false otherwise.
         **/
        public boolean isBouncing() {
            Boolean isBouncing = (Boolean) get("isBouncing");
            return isBouncing.booleanValue();
        }

        /**
         * Sets whether email is bouncing or not.
         *
         * @param isBouncing True if this email is set as bouncing, false otherwise.
         **/
        public void setIsBouncing(boolean isBouncing) {
            set("isBouncing", new Boolean(isBouncing));
        }

        /**
         * Returns true if this email has been verified, false otherwise.
         *
         * @return True if this email has been verified, false otherwise.
         **/
        public boolean isVerified() {
            Boolean isVerified = (Boolean) get("isVerified");
            return isVerified.booleanValue();
        }

        /**
         * Sets whether email is verified or not.
         *
         * @param isVerified True if this email is verified, false otherwise.
         **/
        public void setIsVerified(boolean isVerified) {
            set("isVerified", new Boolean(isVerified));
        }

        /**
         * Returns a string representation for this email address.
         *
         * @return A string representation for this email address.
         **/
        public String toString() {
            return this.getEmailAddress();
        }
    }
}
