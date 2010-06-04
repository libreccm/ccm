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
package com.arsdigita.globalization;

import com.arsdigita.db.Sequences;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.UncheckedWrapperException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * <p>
 * MessageCatalog DomainObject comprised of:
 * </p>
 * <p>
 *   <ul>
 *     <li>name</li>
 *     <li>locale</li>
 *     <li>catalog</li>
 *   </ul>
 * </p>
 *
 * @version $Revision: #14 $ $Date: 2004/08/16 $
 * @version $Id: MessageCatalog.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class MessageCatalog extends DomainObject implements java.io.Serializable
{

    private static final Logger s_cat =
        Logger.getLogger(MessageCatalog.class.getName());

    public final static String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.globalization.MessageCatalog";
    private boolean m_isReadOnly = true;

    /**
     * <p>
     * Constructor for a new root MessageCatalog.
     * </p>
     *
     * @param name The name for this new MessageCatalog
     *
     */
    public MessageCatalog(String name) {
        super(BASE_DATA_OBJECT_TYPE);
        setReadWrite();
        setName(name);
    }

    /**
     * <p>
     * Constructor for a new MessageCatalog associated with a particular
     * Locale.
     * </p>
     *
     * @param name The name for this new MessageCatalog
     * @param locale The locale associated with this MessageCatalog
     *
     */
    public MessageCatalog(String name, java.util.Locale locale)
        throws GlobalizationException {

        this(name);
        setLocale(locale);
    }

    /**
     * <p>
     * Constructor for retrieving a MessageCatalog.
     * </p>
     *
     * @param dataObject The dataObject for this MessageCatalog
     *
     */
    private MessageCatalog(DataObject dataObject) {
        super(dataObject);
    }


    protected void initialize() {
        super.initialize();
        if (isNew() && getID() == null) {
            try {
                set("id", Sequences.getNextValue());
            } catch (java.sql.SQLException sqle) {
                s_cat.error("SQL Exception", sqle);
                throw new UncheckedWrapperException("could not get id", sqle);
            }
        }
    }

    /**
     * <p>
     * Returns the appropriate object type for a MessageCatalog so that the
     * proper type validation can take place when retrieving MessageCatalogs
     * by OID.
     * </p>
     *
     * @return String The fully qualified name of the base data object type
     *         for the MessageCatalog DataObject.
     */
    public String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    /**
     * <p>
     * Return ID.
     * </p>
     *
     * @return BigDecimal The ID of this DataObject
     */
    public BigDecimal getID() {
        return (BigDecimal) get("id");
    }

    /**
     * <p>
     * Get the date that this MessageCatalog was last modified.
     * </p>
     *
     * @return Date representing the date this MessageCatalog was last
     *         modified.
     */
    public Date getLastModified() {
        return (Date) get("lastModified");
    }

    /**
     * <p>
     * Set the date this MessageCatalog was last modified.
     * </p>
     */
    private void setLastModified() throws GlobalizationException {
        if (!isReadOnly()) {
            set("lastModified", new Date());
        } else {
            throw new GlobalizationException(
                                             "MessageCatalog is in read-only mode. You must use the " +
                                             "MessageCatalog.retrieveForEdit() static method to edit " +
                                             "a MessageCatalog."
                                             );
        }
    }

    /**
     * <p>
     * Get the locale associated with this MessageCatalog.
     * </p>
     *
     * @return java.util.Locale representing the language, country,
     *         and variant associated with this MessageCatalog
     */
    public java.util.Locale getLocale() {
        DataObject localeDataObject = (DataObject) get("locale");
        java.util.Locale locale = null;

        if (localeDataObject != null) {
            Locale localeDomainObject = new Locale(localeDataObject);
            if (localeDomainObject != null) {
                locale = localeDomainObject.toJavaLocale();
            }
        }

        return locale;
    }

    /**
     * <p>
     * Set the locale associated with this MessageCatalog.
     * </p>
     *
     * @param locale java.util.Locale representing the language, country,
     *        and variant associated with this MessageCatalog.
     */
    public void setLocale(java.util.Locale locale)
        throws GlobalizationException {

        if (!isReadOnly()) {
            if (locale != null) {
                Locale localeObject = Locale.fromJavaLocale(locale);

                if (localeObject == null) {
                    throw new GlobalizationException(
                                                     "Locale " + locale.toString() + " is not supported."
                                                     );
                }

                setLocale(localeObject);
            } else {
                clearLocale();
            }
        } else {
            throw new GlobalizationException(
                                             "MessageCatalog is in read-only mode. You must use the " +
                                             "MessageCatalog.retrieveForEdit() static method to edit " +
                                             "a MessageCatalog."
                                             );
        }
    }

    /**
     * <p>
     * Set the locale associated with this MessageCatalog.
     * </p>
     *
     * @param locale representing the language, country, and variant
     *        associated with this MessageCatalog.
     */
    public void setLocale(Locale locale) {
        setAssociation("locale", locale);
    }

    /**
     * <p>
     * Clear the locale associated with this MessageCatalog.
     * </p>
     */
    private void clearLocale() {
        setAssociation("locale", null);
    }

    /**
     * <p>
     * De-serialize the MessageCatalog from the database into a Map object.
     * </p>
     *
     * @return Map representing the MessageCatalog
     */
    public Map getMap() {
        Map catalog = null;
        Object object = get("catalog");

        if (object != null) {
            byte[] objectBytes = null;
            ObjectInputStream ois = null;

            if (object instanceof byte[]) {
                objectBytes = (byte[]) object;
            } else if (object instanceof Blob) {
                try {
                    Blob objectBlob = (Blob) object;
                    objectBytes = objectBlob.getBytes(
                                                      1L,
                                                      (int)objectBlob.length()
                                                      );
                } catch (SQLException sqle) {
                    s_cat.error("SQL Exception", sqle);
                    throw new UncheckedWrapperException("SQLException: " + sqle.getMessage(), sqle);
                }
            } else {
                throw new RuntimeException("Was not able to determine BLOB type.");
            }

            try {
                ois =
                    new ObjectInputStream(
                                          new ByteArrayInputStream(objectBytes)
                                          );
            } catch (java.io.IOException ioe) {
                s_cat.debug(get("catalog"), ioe);
                throw new IllegalStateException("Unable to get catalog.");
            }

            // Reconstitute the ObjectInputStream as a Map
            try {
                catalog = (Map) ois.readObject();
            } catch (java.io.IOException ioe) {
                throw new IllegalStateException();
            } catch (ClassNotFoundException cnfe) {
                throw new IllegalStateException();
            } finally {
                try {
                    ois.close();
                } catch(IOException e) {
                    s_cat.error("Problem closing ObjectInputStream.", e);
                }

            }
        }

        return catalog;
    }

    /**
     * <p>
     * Serialize the MessageCatalog to the database from a Map object.
     * </p>
     *
     * @param catalog representing the MessageCatalog
     */
    public void setMap(Map catalog) throws GlobalizationException {
        if (!isReadOnly()) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            ObjectOutputStream oos = null;
            try {
                oos = new ObjectOutputStream(baos);
                oos.writeObject(catalog);
            } catch (java.io.IOException ioe) {
                throw new IllegalStateException();
            } finally {
                if (null != oos) {
                    try {
                        oos.close();
                    } catch(IOException e) {
                        s_cat.error("Problem closing ObjectOutputStream.", e);
                    }
                }
            }

            s_cat.debug(baos.toByteArray());

            set("catalog", baos.toByteArray());
        } else {
            throw new GlobalizationException(
                                             "MessageCatalog is in read-only mode. You must use the " +
                                             "MessageCatalog.retrieveForEdit() static method to edit " +
                                             "a MessageCatalog."
                                             );
        }
    }

    /**
     * <p>
     * Get the name of this MessageCatalog.
     * </p>
     *
     * @return String representing the MessageCatalog's name
     */
    public String getName() {
        return (String) get("name");
    }

    /**
     * <p>
     * Set the name of this MessageCatalog.
     * </p>
     *
     * @param name representing the MessageCatalog's name
     */
    private void setName(String name) {
        if (name == null || name.length() == 0) {
            throw new IllegalArgumentException("name cannot be empty.");
        }
        set("name", name);
    }

    /**
     * <p>
     * Check whether or not this MessageCatalog is read-only or not.
     * </p>
     *
     * @return boolean true if it is read-only
     */
    public boolean isReadOnly() {
        return m_isReadOnly;
    }

    /**
     * <p>
     * Toggle read/write-ability of this MessageCatalog.
     * </p>
     */
    private void setReadWrite() {
        m_isReadOnly = false;
    }

    /**
     * <p>
     * Retrieve a MessageCatalog object from persistent storage.
     * </p>
     *
     * @param name The name of the MessageCatalog to retrieve.
     *
     * @return DataObject MessageCatalog
     */
    private static DataObject load(String name)
        throws GlobalizationException {

        Locale locale = null;
        return load(name, locale);
    }

    /**
     * <p>
     * Retrieve a MessageCatalog object from persistent storage.
     * </p>
     *
     * @param name The name of the MessageCatalog to retrieve.
     * @param locale The locale of the MessageCatalog to retrieve.
     *
     * @return DataObject MessageCatalog
     */
    private static DataObject load(String name, java.util.Locale locale)
        throws GlobalizationException {

        Locale localeObject = null;

        if (locale != null) {
            localeObject = Locale.fromJavaLocale(locale);
        }

        return load(name, localeObject);
    }

    /**
     * <p>
     * Retrieve a MessageCatalog object from persistent storage.
     * </p>
     *
     * @param name The name of the MessageCatalog to retrieve.
     * @param locale The locale of the MessageCatalog to retrieve.
     *
     * @return DataObject MessageCatalog
     */
    private static DataObject load(String name, Locale locale)
        throws GlobalizationException {

        Session s = SessionManager.getSession();
        DataCollection catalogs = s.retrieve(BASE_DATA_OBJECT_TYPE);

        catalogs.addEqualsFilter("name", name);
        if (locale != null) {
            catalogs.addEqualsFilter("locale.id", locale.getID());
        } else {
            catalogs.addEqualsFilter("locale.id", null);
        }

        DataObject catalog = null;
        try {
            if (catalogs.next()) {
                catalog = catalogs.getDataObject();
            } else {
                throw new GlobalizationException("No MessageCatalog found");
            }
        } finally {
            catalogs.close();
        }

        return catalog;
    }

    /**
     * <p>
     * Retrieve the MessageCatalog from persistent storage.
     * </p>
     *
     * @param name The name of the MessageCatalog to retrieve.
     *
     * @return MessageCatalog
     */
    public static MessageCatalog retrieve(String name)
        throws GlobalizationException {

        return new MessageCatalog(load(name));
    }

    /**
     * <p>
     * Retrieve the MessageCatalog from persistent storage.
     * </p>
     *
     * @param name The name of the MessageCatalog to retrieve.
     * @param locale The locale of the MessageCatalog to retrieve.
     *
     * @return MessageCatalog
     */
    public static MessageCatalog retrieve(String name, java.util.Locale locale)
        throws GlobalizationException {

        return new MessageCatalog(load(name, locale));
    }

    /**
     * <p>
     * Retrieve the MessageCatalog from persistent storage for edit.
     * </p>
     *
     * @param name The name of the MessageCatalog to retrieve.
     *
     * @return MessageCatalog
     */
    public static MessageCatalog retrieveForEdit(String name)
        throws GlobalizationException {

        MessageCatalog mc = new MessageCatalog(load(name));
        mc.setReadWrite();
        return mc;
    }

    /**
     * <p>
     * Retrieve the MessageCatalog from persistent storage for edit.
     * </p>
     *
     * @param name The name of the MessageCatalog to retrieve.
     * @param locale The locale of the MessageCatalog to retrieve.
     *
     * @return MessageCatalog
     */
    public static MessageCatalog retrieveForEdit(
                                                 String name, java.util.Locale locale
                                                 ) throws GlobalizationException {
        MessageCatalog mc = new MessageCatalog(load(name, locale));
        mc.setReadWrite();
        return mc;
    }

    protected void beforeSave() {
        try {
            setLastModified();
        } catch (GlobalizationException ge) {
            s_cat.error
                ("MessageCatalog is in read-only mode. You must " +
                 "use the MessageCatalog.retrieveForEdit() " +
                 "static method to edit a MessageCatalog.", ge);
            throw new IllegalStateException
                ("MessageCatalog is in read-only mode. You must " +
                 "use the MessageCatalog.retrieveForEdit() " +
                 "static method to edit a MessageCatalog.");
        }
    }
}
