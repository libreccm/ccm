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
package com.arsdigita.messaging;

import com.arsdigita.db.Sequences;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.ObservableDomainObject;
import com.arsdigita.mail.ByteArrayDataSource;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.PersistenceException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;

/**
 * Represents a message part (that is, an attachment).  Each part has
 * a content represented as an arbitrary block of bytes and a MIME
 * type that identifies the format of the content.
 *
 * @author Ron Henderson 
 * @version $Id: MessagePart.java 287 2005-02-22 00:29:02Z sskracic $
 */

public class MessagePart extends ObservableDomainObject implements MessageType {

    /**
     * Base DataObject type
     */

    public static final String BASE_DATA_OBJECT_TYPE = MessagePart.class.getName();

    // Keys for persistent data.

    private static final String PART_ID     = "id";
    private static final String NAME        = "name";
    private static final String DESCRIPTION = "description";
    private static final String TYPE        = "type";
    private static final String DISPOSITION = "disposition";
    private static final String CONTENT     = "content";

    /**
     * Disposition of "inline"
     */
    public static final String INLINE = com.arsdigita.mail.Mail.INLINE;

    /**
     * Disposition of "attachment"
     */
    public static final String ATTACHMENT = com.arsdigita.mail.Mail.ATTACHMENT;

    /**
     * Default constructor.
     */

    public MessagePart() {
        super(BASE_DATA_OBJECT_TYPE);
        setID(generateID());
    }

    /**
     * Retrieves an existing part from the database using its OID.
     *
     * @param oid the OID of the part
     */

    public MessagePart (OID oid)
        throws DataObjectNotFoundException
    {
        super(oid);
    }

    /**
     * Creates a part from its underlying DataObject.
     *
     * @param dataObject the DataObject that represents this part
     */

    public MessagePart (DataObject dataObject)
        throws DataObjectNotFoundException
    {
        super(dataObject);
    }

    /**
     * Creates a new MessagePart with a given name and default
     * disposition of ATTACHMENT.
     *
     * @param name the name of the part.
     */

    public MessagePart (String name)
    {
        this(name,null,ATTACHMENT);
    }

    /**
     * Creates a new MessagePart with a given name and description,
     * and a disposition of ATTACHMENT.
     *
     * @param name the name of the part.
     * @param description a description of the part.
     */

    public MessagePart (String name, String description)
    {
        this(name,description,ATTACHMENT);
    }

    /**
     * Creates a new MessagePart with a given name, description and
     * disposition.
     *
     * @param name the name of the part.
     * @param description a description of the part.
     * @param disposition the disposition of the part, INLINE or ATTACHMENT
     */

    public MessagePart (String name, String description, String disposition)
    {
        this();

        setName(name);
        setDescription(description);
        setDisposition(disposition);
    }

    /**
     * Gets the base DataObject type.
     */

    protected String getBaseDataObjectType () {
        return BASE_DATA_OBJECT_TYPE;
    }

    /**
     * Gets the name of this part.
     * @return the name of this part.
     */

    public String getName () {
        return (String) get(NAME);
    }

    /**
     * Sets the name of this part.
     * @param name the name of this part
     */

    public void setName (String name) {
        set(NAME, name);
    }

    /**
     * Gets the description of this part.
     * @return the description of this part.
     */

    public String getDescription () {
        return (String) get(DESCRIPTION);
    }

    /**
     * Sets the description of this part.
     * @param description the description of this part
     */

    public void setDescription (String description) {
        set(DESCRIPTION, description);
    }

    /**
     * Returns the MIME type of this part.  Returns null if the
     * content type cannot be determined.
     * @return the MIME type of this part, or null if the content type
     * cannot be determined.
     */

    public String getContentType() {
        return (String) get(TYPE);
    }

    /**
     * Sets the MIME type of this part.  Protected because content
     * type is normally set by one of the setContent methods.  This
     * is for internal package use only.
     *
     * @param type the content type of this part
     */

    protected void setContentType (String type) {
        set(TYPE, type);
    }

    /**
     * Gets the disposition of this part.
     * @return the disposition of this part.
     */

    public String getDisposition () {
        return (String) get(DISPOSITION);
    }

    /**
     * Sets the disposition of this part.
     * @param disposition the disposition of this part
     */

    public void setDisposition (String disposition) {
        set (DISPOSITION, disposition);
    }

    /**
     * Gets the size of the content of this part in bytes, or -1 if the
     * size cannot be determined.
     * @return the size of the content in bytes, or -1 if the
     * size cannot be determined.
     */

    public int getSize() {
        byte[] content = (byte[]) get(CONTENT);
        return content.length;
    }

    /**
     * Returns the content of this part as an Object.  The type of the
     * Object returned depends on the content itself.  For any primary
     * MIME type of "text", the object returned is a String.  For
     * anything else, a ByteArrayInputStream is returned and
     * applications can deal with processing the object appropriately.
     *
     * @return the content as a Java object.
     */

    public java.lang.Object getContent () {

        byte[] bytes  = (byte[]) get(CONTENT);
        Object result;

        if (getContentType().startsWith("text")) {
            result = new String(bytes);
        } else {
            result = new ByteArrayInputStream(bytes);
        }

        return result;
    }

    /**
     * Convenience method that sets the given String as this part's
     * content, with a MIME type of "text/plain".
     *
     * @param text the plain text content of this part.
     */

    public void setText (String text) {
        setContent(text, TEXT_PLAIN);
    }

    /**
     * Convenience method that sets the given String as this part's
     * content, with the specified MIME type.
     *
     * @param text the plain text content of this part.
     * @param type the MIME type of this part.
     */

    public void setContent (String text,
                            String type) {
        set(TYPE,    type);
        set(CONTENT, text.getBytes());
    }

    /**
     * Sets the content of this part to a file with a given name and
     * description.
     *
     * @param file the File to attach content from
     * @param name the name of the file
     * @param description the description of the file
     *
     * @since 4.7.1
     */

    public void setContent (File file,
                            String name,
                            String description)
        throws MessagingException
    {
        DataHandler dh = new DataHandler(new FileDataSource(file));

        setDataHandler(dh);
        setName(name);
        setDescription(description);
    }

    /**
     * Gets the DataHandler for this part.
     * @return the DataHandler for this part.
     */

    public DataHandler getDataHandler() {
        Object content = getContent();
        DataHandler dh = null;

        if (content instanceof InputStream) {
            dh = new DataHandler
                (new ByteArrayDataSource
                 ((InputStream) content, getContentType(), getName()));
        } else {
            dh = new DataHandler
                (new ByteArrayDataSource
                 ((String) content, getContentType(), getName()));
        }

        return dh;
    }

    /**
     * Provides the mechanism to set this part's content for anything
     * other than plain text attachments.  It does not work exactly
     * like MimeBodyPart since the ACS persistence layer cannot deal
     * with an InputStream for a BLOB (our internal representation of
     * the content).  Instead, this method retrieves the data from the
     * given DataHandler and copies it to an internal buffer, where it
     * can be passed into the persistence layer.
     *
     * <p>Therefore, if you call setDataHandler / getDataHandler you
     * will get two distinct DataHandlers, one for the input data and
     * one for the internal byte array that holds the content.
     *
     * @param dh the DataHandler for this part's content
     */

    public void setDataHandler (DataHandler dh)
        throws MessagingException
    {
        set(NAME, dh.getName());
        set(TYPE, dh.getContentType());

        // Copy the data to an internal byte[] array and transfer this
        // to the persistence layer.

        try {
            InputStream is = dh.getInputStream();
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            int ch;

            while ((ch = is.read()) != -1) {
                os.write(ch);
            }

            set(CONTENT, os.toByteArray());

        } catch (IOException e) {
            throw new MessagingException("error attaching content: " + e.getMessage());
        }
    }

    /**
     * Generate a new ID for this part.
     */

    private BigDecimal generateID () throws PersistenceException {
        try {
            return Sequences.getNextValue();
        } catch (java.sql.SQLException e) {
            throw new PersistenceException("Unable to generate a unique ID");
        }
    }

    /**
     * Set the ID for this part.
     */

    private void setID (BigDecimal id) {
        set(PART_ID, id);
    }
}
