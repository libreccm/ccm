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
package com.arsdigita.notification;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.kernel.Party;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.Assert;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

/**
 * Models the envelope information associated with a
 * digest. When a digest is processed, all notifications associated
 * with it are grouped for delivery as a single unit to each receiver.
 * The outbound email generated for the receivers has a common
 * subject, header, separator between the individual messages, and
 * signature.
 *
 * @author Ron Henderson 
 * @author David Dao 
 * @version $Id: Digest.java 287 2005-02-22 00:29:02Z sskracic $
 */

public class Digest extends ACSObject implements NotificationConstants {

    // Base DataObject type

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.notification.Digest";

    // Constants.  These represent the processing frequency of the
    // digest in minutes. Used as a convenience when setting the
    // processing frequency.

    public static final int HOURLY = 60;
    public static final int DAILY  = 24 * HOURLY;
    public static final int WEEKLY = 7  * DAILY;

    /**
     * Default constructor. Generates a digest scheduled to run immediately
     * and every hour thereafter. Default separator is a line
     * consisting of 78 dashes (-).
     *
     */

    public Digest () {
        super(BASE_DATA_OBJECT_TYPE);

        setSeparator('-',78);
        setFrequency(HOURLY);
        setNextRun  (new Date());
    }

    /**
     * Creates a digest with the default processing frequency and
     * separator.
     *
     * @param from the party responsible for sending the digest
     * @param subject the common subject for digest notifications
     * @param header the common header
     * @param signature the common signature
     */

    public Digest (Party  from,
                   String subject,
                   String header,
                   String signature)
    {
        this();
        
        Assert.assertNotNull(from, "Party from");
        Assert.assertNotNull(subject, "String subject");
        Assert.assertNotNull(header, "String header");
        Assert.assertNotNull(signature, "String signature");

        setFrom(from);
        setSubject(subject);
        setHeader(header);
        setSignature(signature);
    }

    /**
     * Retrieves an existing digest from the database.
     *
     * @param oid the OID of the digest
     */

    public Digest (OID oid)
        throws DataObjectNotFoundException
    {
        super(oid);
    }

    /**
     * Creates a Digest from a DataObject
     *
     * @param dataObj the DataObject the Digest will wrap
     */

    public Digest(DataObject dataObj) {
        super(dataObj);
    }

    /**
     * Retrieve a digest with the given id;
     *
     * @param id the id for the DataObject to retrieve.
     */

    public Digest (BigDecimal id)
        throws DataObjectNotFoundException
    {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    /**
     * Gets the sender of the digest.
     * @return the sender.
     */

    public Party getFrom()
        throws DataObjectNotFoundException
    {
        return (Party) DomainObjectFactory.newInstance
            (new OID(Party.BASE_DATA_OBJECT_TYPE, get(PARTY_FROM)));
    }

    /**
     * Gets the email address of the digest sender as a string.  Used
     * when preparing the digest for sending via email.
     *
     * @return the sender's email address as a String.
     */

    public String getFromEmail() {
        try {
            return getFrom().getPrimaryEmail().toString();
        } catch (DataObjectNotFoundException ex) {
            return null;
        }
    }

    /**
     * Sets the common sender for the digest.
     * @param from the common sender
     */

    public void setFrom (Party from) {
        Assert.assertNotNull(from, "Party from");

        set(PARTY_FROM, from.getID());
    }

    /**
     * Gets the common subject for the digest.
     * @return the common subject for the digest.
     */

    public String getSubject () {
        return (String) get(SUBJECT);
    }

    /**
     * Sets the common subject for the digest.
     * @param subject the common subject
     */

    public void setSubject (String subject) {
        set(SUBJECT, subject);
    }

    /**
     * Gets the common header for the digest.
     * @return the common header for the digest.
     */

    public String getHeader () {
        return (String) get(HEADER);
    }

    /**
     * Set the common header for the digest.
     * @param header the common header
     */

    public void setHeader (String header) {
        set(HEADER, header);
    }

    /**
     * Gets the current separator for elements of the digest.
     * @return the separator
     */

    public String getSeparator () {
        return (String) get(SEPARATOR);
    }

    /**
     * Sets the separator by specifying its value as a String.
     * For example, setSeparator("----------") will produce a dashed
     * line that is 10 characters long.
     *
     * @param separator the value of the separator
     */

    public void setSeparator (String separator) {
        set(SEPARATOR, separator);
    }

    /**
     * Sets the separator by specifing a single character and a repeat
     * count.  For example, setSeparater('-',78) will set the default
     * separator as a dashed line that is 78 characters long.
     *
     * @param s the separator character
     * @param n the repeat count
     */

    public void setSeparator (char s, int n) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < n; i++)
            sb.append(s);
        setSeparator(sb.toString());
    }

    /**
     * Gets the signature used for this digest.
     * @return the signature for this digest.
     */

    public String getSignature () {
        return (String) get(SIGNATURE);
    }

    /**
     * Sets the signature used for this digest.
     *
     * @param signature the signature for the digest
     */

    public void setSignature (String signature) {
        set(SIGNATURE, signature);
    }

    /**
     * Gets the current processing frequency (in minutes) for this
     * digest.
     * @return the processing frequency for this digest.
     */

    public Integer getFrequency () {
        return (Integer) get(FREQUENCY);
    }

    /**
     * Sets the processing frequency (in minutes) for this digest.
     *
     * @param frequency the processing frequency in minutes
     */

    public void setFrequency (int frequency) {
        set(FREQUENCY, new Integer(frequency));
    }

    /**
     * Sets the date when this digest will next be processed.
     */

    private void setNextRun (Date nextRun) {
        set(NEXT_RUN, nextRun);
    }

    void updateNextRun() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, getFrequency().intValue());
        setNextRun(cal.getTime());
    }

    /**
     * Get the date when this digest will next be processed.
     */

    public Date getNextRun () {
        return (Date) get(NEXT_RUN);
    }

    /**
     * Saves the Digest to the database so it can be used for sending
     * notifications.  Verifies that all required parameters have been
     * specified before saving.
     */

    protected void beforeSave() {

        String message =
            "Digest cannot be saved without a valid sender";

        if (get(PARTY_FROM) == null) {
            throw new IllegalStateException(message);
        }

        Party from;
        try {
            from = getFrom();
        } catch (DataObjectNotFoundException ex) {
            throw new IllegalStateException(message);
        }

        if (from == null || from.getPrimaryEmail() == null) {
            throw new IllegalStateException(message);
        }

        super.beforeSave();
    }
}
