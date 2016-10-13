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

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.persistence.DataAssociationCursor;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.metadata.ObjectType;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a party, which can either be a group or a
 * user.
 *
 * @author Phong Nguyen
 * @version 1.0
 * @version $Id: Party.java 738 2005-09-01 12:36:52Z sskracic $
 **/
public abstract class Party extends ACSObject {

    private static final Logger s_log = Logger.getLogger( Party.class );

    /**
     * A list of EmailAddress objects associated with this party.
     **/
    private List m_emailList;
    private EmailAddress m_primaryEmail;

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.kernel.Party";

    @Override
    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    public Party(DataObject partyData) {
        super(partyData);
    }

    /**
     * Default constructor. The contained <code>DataObject</code> is
     * initialized with a new <code>DataObject</code> with an
     * <code>ObjectType</code> of "Party".
     *
     * @see ACSObject#ACSObject(String)
     * @see com.arsdigita.persistence.DataObject
     * @see com.arsdigita.persistence.metadata.ObjectType
     **/
    public Party() {
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
     * @see ACSObject#ACSObject(ObjectType)
     * @see com.arsdigita.persistence.DataObject
     * @see com.arsdigita.persistence.metadata.ObjectType
     **/
    public Party(String typeName) {
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
     * @see ACSObject#ACSObject(ObjectType)
     * @see com.arsdigita.persistence.DataObject
     * @see com.arsdigita.persistence.metadata.ObjectType
     **/
    public Party(ObjectType type) {
        super(type);
    }

    /**
     * Constructor in which the contained <code>DataObject</code> is retrieved
     * from the persistent storage mechanism with an <code>OID</code>
     * specified by <i>oid</i>.
     *
     * @param oid the <code>OID</code> for the retrieved
     * <code>DataObject</code>
     *
     * @see ACSObject#ACSObject(OID)
     * @see com.arsdigita.persistence.DataObject
     * @see com.arsdigita.persistence.OID
     **/
    public Party(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    public static PartyCollection retrieveAllParties() {
        DataCollection c = SessionManager.getSession()
            .retrieve(BASE_DATA_OBJECT_TYPE);
        return new PartyCollection(c);
    }

    /**
     * Returns the name of this party.
     *
     * @return the name of this party.
     **/
    public abstract String getName();

    /**
     * Returns a display name for this party.
     *
     * @see ACSObject#getDisplayName()
     */
    @Override
    public String getDisplayName() {
        return getName();
    }

    /**
     * Returns the primary email address. The primary email address
     * may be used to log onto the system. It is also used as the main
     * contact point for the party.
     *
     * @return the primary email address.
     *
     * @see EmailAddress
     * @see UserAuthentication
     **/
    public EmailAddress getPrimaryEmail() {
        if (m_primaryEmail == null) {
            String email = (String) get("primaryEmail");
            if (email != null) {
                m_primaryEmail = new EmailAddress( email );
            }
        }
        return m_primaryEmail;
    }

    /**
     * Marks the specified email address as this party's
     * primary email address.  If this party does not already
     * have the specified email address as an email address, it
     * will be added using the addEmailAddress method.
     *
     * @param emailAddress the email address to set as the primary one
     *
     * @see Party#addEmailAddress
     * @see EmailAddress
     **/
    public void setPrimaryEmail(EmailAddress emailAddress) {
        m_primaryEmail = emailAddress;
        if (emailAddress == null) {
            set("primaryEmail", null);
        } else {
            set("primaryEmail", emailAddress.getEmailAddress());
            addEmailAddress(emailAddress);
        }
    }

    /**
     * Returns an iterator for this party's email addresses.
     *
     * @return an iterator for this party's email addresses.
     **/
    public Iterator getEmailAddresses() {
        initializeEmails();
        return m_emailList.iterator();
    }

    /**
     * Returns an iterator for this party's alternate (non-primary)
     * email addresses.
     *
     * @return an iterator for this party's email addresses.
     **/
    public Iterator getAlternateEmails() {
        // This implementation is a quick hack to get the API working.
        // We may reimplement this later.

        Iterator iter = getEmailAddresses();
        EmailAddress primaryEmail = getPrimaryEmail();
        if (primaryEmail == null) {
            return iter;
        }
        ArrayList alternateEmails = new ArrayList();
        while (iter.hasNext()) {
            Object o = iter.next();
            if (! primaryEmail.equals(o)) {
                alternateEmails.add(o);
            }
        }
        return alternateEmails.iterator();
    }

    private void initializeEmails() {
        if (m_emailList == null) {
            m_emailList = new ArrayList();
            if (!isNew()) {
                DataAssociationCursor emails = getEmailAssociation().cursor();
                while (emails.next()) {
                    m_emailList.add(new EmailAddress((String) emails.get("emailAddress")));
                }
            }
        }

    }

    /**
     * Adds the specified email address for contacting this party (if it is
     * not already present).
     *
     * @param emailAddress an email address by which to contact this party
     *
     * @see EmailAddress
     * @see Party#removeEmailAddress
     * @see Party#setPrimaryEmail
     **/
    public void addEmailAddress(EmailAddress emailAddress) {
        initializeEmails();
        if (!m_emailList.contains(emailAddress)) {
            m_emailList.add(emailAddress);
            getEmailAssociation().add(makePartyEmail(emailAddress));
        }
    }

    /**
     * Return a <code>DataAssociation</code> for the association between
     * this party and its EmailAddress data objects.  This helper method
     * exists so that we can potentially avoid redundant get() calls to
     * the persistence layer.  For now, we get a new data association on
     * every call.
     */
    private DataAssociation getEmailAssociation() {
        // FIXME: is there a hidden meaning to retrieving the association twice?
        // -- 2002-11-26
        //DataAssociation assoc = (DataAssociation) get("emailAddresses");
        return (DataAssociation) get("emailAddresses");
    }

    /**
     * Removes the specified email address for contacting this party (if it is
     * present).
     *
     * @param emailAddress the email address to remove
     **/
    public void removeEmailAddress(EmailAddress emailAddress) {
        if (emailAddress.equals(getPrimaryEmail())) {
            throw new IllegalArgumentException(
                                       "Cannot remove the primary email address."
                                       );
        }

        initializeEmails();
        getEmailAssociation().remove(makePartyEmailOID(emailAddress));
        m_emailList.remove(emailAddress);
    }

    private OID makePartyEmailOID(EmailAddress emailAddress) {
        OID oid =  new OID("com.arsdigita.kernel.PartyEmail");
        oid.set("partyID", getID());
        oid.set("emailAddress", emailAddress.getEmailAddress());
        return oid;
    }

    private DataObject makePartyEmail(EmailAddress emailAddress) {
        DataObject emailData =
            SessionManager.getSession()
            .create("com.arsdigita.kernel.PartyEmail");
        emailData.set("partyID", getID());
        emailData.set("emailAddress", emailAddress.getEmailAddress());
        return emailData;
    }

    /**
     * Returns the URI for this party.
     *
     * @return the URI for this party.
     **/
    public String getURI() {
        return (String) get("uri");
    }

    /**
     * Sets the URI for this party.
     *
     * @param uri The URI for this party.
     **/
    public void setURI(String uri) {
        set("uri", uri);
    }

    /**
     * Retrieves all objects of this type stored in the database. Very
     * necessary for exporting all entities of the current work environment.
     *
     * @return List of all parties
     */
    public static List<Party> getAllObjectParties() {
        List<Party> partyList = new ArrayList<>();

        final Session session = SessionManager.getSession();
        DomainCollection collection = new DomainCollection(session.retrieve(
                Party.BASE_DATA_OBJECT_TYPE));

        while (collection.next()) {
            Party party = (Party) collection.getDomainObject();
            if (party != null) {
                partyList.add(party);
            }
        }

        collection.close();
        return partyList;
    }

}
