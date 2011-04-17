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
package com.arsdigita.docrepo;


import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.mimetypes.MimeType;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.metadata.Property;
import com.arsdigita.util.Assert;
import org.apache.oro.text.perl.Perl5Util;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.activation.DataSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Vector;

/**
 * Represents a File in the document manager application.
 *
 * @author Stefan Deusch (stefan@arsdigita.com)
 * @author Ron Henderson (ron@arsdigita.com)
 * @version $Id: File.java  pboy $
 */
public class File extends ResourceImpl implements Constants {

    /** Logger instance for debugging support. */
    protected static org.apache.log4j.Logger s_log =
        org.apache.log4j.Logger.getLogger(File.class);

    public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.docrepo.File";

    public static final String DEFAULT_MIME_TYPE =
                               "application/octet-stream";

    private static Perl5Util s_re = new Perl5Util();

    private static final String PATTERN ="/^([^\\.].*)(\\.\\w+)$/";

    private static final byte[] EMPTY_BYTES = new byte[0];

    /**
     * Creates a new file by retrieving it from the underlying data
     * object.
     *
     * @param dataObject the dataObject corresponding to this file
     */
    public File(DataObject dataObject) {
        super(dataObject);
    }

    /**
     * Creates a new File by retrieving it based on ID.
     *
     * @param id - the ID of this file in the database
     */
    public File(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    /**
     * Creates a new File by retrieving it based on OID.
     *
     * @param oid - the OID of this file
     */
    public File(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    /**
     * Creates a file inside a given parent folder.
     *
     * @param name the name of the File
     * @param description the description of the file contents (may
     * be null)
     */
    public File(String name, String description, Folder parent) {
        this(BASE_DATA_OBJECT_TYPE, name, description, parent);
    }


    /**
     * Creates a file inside a given parent folder.
     *
     * @param name the name of the File
     * @param description the description of the file contents (may
     * be null)
     */
    public File(String baseDataObjectType, String name, String description,
                Folder parent) {
        super(baseDataObjectType, name, description, parent);
    }

    /**
     * Creates an empty file inside a given parent folder.
     *
     * @param parent The parent folder
     */
    public File(Folder parent) {
        super(BASE_DATA_OBJECT_TYPE, parent);
    }

    /**
     * Creates a file
     *
     * @param objectType the type of the object
     *
     */
    public File(String objectType) {
		super(objectType);
	 }

    /**
     * 
     * @param dataObject
     * @return
     */
    public static File retrieveFile(DataObject dataObject) {
        Assert.exists(dataObject, DataObject.class);

        return new File(dataObject);
    }


    /**
     *
     */
    @Override
    protected void beforeSave() {
        set(IS_FOLDER, Boolean.FALSE);
        super.beforeSave();
    }

 	/**
     * Returns the pdl object type required for this object.
     *
     */
    public String getBaseDataObjectType() {
		return BASE_DATA_OBJECT_TYPE;
	}

    /**
     * @return the MIME type of this file as a string, or null if the
     * content type cannot be determined.
     */
    public String getContentType() {
        return  (String) get(TYPE);
    }

    /**
     *  @return the pretty name for the MIME type of the file or null
     *  if no content type can be set.  For instance, getContentType
     *  will return something like "application/msexcel" where this will
     *  return "Microsoft Excel document".  If the mime type is not
     *  registered in the database this this will just return the same
     *  value as getContentType()
     */
    public String getPrettyContentType() {
        String type = getContentType();
        if (type != null) {
            MimeType mimeType = MimeType.loadMimeType(type);
            if (mimeType != null) {
                type = mimeType.getLabel();
            }
        }
        return type;
    }

    /**
     * Sets the MIME type of this file.
     *
     * @param type the content type of this file.
     */
    private void setContentType(String type) {
        if (isNew()) {
            set(TYPE, type);
        } else {
            if (!type.equals(getContentType())) {
                throw new TypeChangeException(getContentType(), type);
            }
        }
    }

    /**
     * Sets the content of this resource to a file with a given name and
     * description.
     *
     * @param file the file to read content from
     * @param name the name of the file
     * @param description the description of the file
     */
    public void setContent(java.io.File file,
                           String name,
                           String description,
                           String mimeType)
        throws ResourceException
    {

        DataSource source = new FileDataSource(file);
        setContent(source, name, description, mimeType);
        setName(name);
        setDescription(description);
        setContentType(mimeType);
    }

    /**
     * Sets the content of this resource to a file with a given name and
     * description.
     *
     * @param source the data source to read content from
     * @param name the name of the file
     * @param description the description of the file
     */
    public void setContent(DataSource source,
                           String name,
                           String description,
                           String mimeType)
        throws ResourceException
    {
        DataHandler dh = new DataHandler(source);
        setDataHandler(dh);
        setName(name);
        setDescription(description);
        setContentType(mimeType);
    }

    /**
     * Convenience method for use with plain text files.  Sets the
     * content of the file to a String and sets its MIME type to
     * "text/plain".
     */
    final public void setText(String text) {
        final byte[] content = text.getBytes();
        DocBlobject dblob = new DocBlobject();
        dblob.setContent(content);
        set(CONTENT, dblob);
        set(SIZE, BigDecimal.valueOf(content.length));
        setContentType(TEXT_PLAIN);
    }

    /**
     * Returns an InputStream suitable for reading the raw content of
     * the file.
     *
     * @return an InputStream
     */
    public InputStream getInputStream() {
        DataObject data = (DataObject)get("content");
       if(data == null) {
         throw new DataObjectNotFoundException("Doc Blob not found");
       }
        DocBlobject dobj = (DocBlobject)DomainObjectFactory.newInstance(data);
        return new ByteArrayInputStream((byte[]) dobj.getContent());
    }

    /**
     * Returns the raw content of the file as a byte array.  Do not
     * use this method for very large files.  Use the {link
     * getInputStream} method instead.
     */
    public byte[] getRawContent() {
        DataObject data = (DataObject)get("content");
        if(data == null) {
            return EMPTY_BYTES;
        }
        DocBlobject dobj = (DocBlobject)DomainObjectFactory.newInstance(data);
        return (byte[]) dobj.getContent();
    }

    /**
     * Provides the mechanism to set this file's content.
     *
     * @param dh the DataHandler for this part's content
     */
    private void setDataHandler(DataHandler dh)
        throws ResourceException
    {
        // Copy the data to an internal byte[] array and transfer this
        // to the persistence layer.

        try {
            InputStream is = dh.getInputStream();
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            int ch;

            while ((ch = is.read()) != -1) {
                os.write(ch);
            }

            // Convert to a byte array and store the data and size as
            // properties of the file

            byte[] content = os.toByteArray();
            DocBlobject dblob = new DocBlobject();
            dblob.setContent(content);
            set(CONTENT, dblob);
            set(SIZE,    BigDecimal.valueOf(content.length));

        } catch (IOException e) {
            throw new ResourceException("error reading content: " + e.getMessage());
        }
    }

    /**
     * Returns the size of the file in bytes.
     *
     * @return the size of the file in bytes, or -1 if the size
     * cannot be computed.
     */
    public BigDecimal getSize() {
        BigDecimal size = (BigDecimal) get(SIZE);
        if (size != null) {
            return size;
        } else {
            return BigDecimal.valueOf(0);
        }
    }


    public boolean isFile() {
        return true;
    }

    public boolean isFolder() {
        return false;
    }

    public Resource copyTo(String name, Resource parent) {
        File dest = new File((Folder) parent);
        copy(this,dest);
        dest.setName(name);
        dest.setContentType(getContentType());
        byte[] content = getRawContent();
        DocBlobject dblob = new DocBlobject();
        dblob.setContent(content);
        dest.set(CONTENT,dblob);
        dest.set(SIZE, BigDecimal.valueOf(content.length));
        dest.save();
        return dest;
    }

    /**
     * @return the list of directory property names for a File,
     * including those inherited from ResourceImpl.
     */
    @Override
    protected Vector getPropertyNames() {
        Vector names = super.getPropertyNames();
        Iterator props = getObjectType().getDeclaredProperties();
        while (props.hasNext()) {
            names.addElement(((Property) props.next()).getName());
        }
        return names;
    }


    /**
     * Returns the display name of a file, that is the file name minus
     * any extension if present.  An extension is defined as the '.'
     * character followed by one or more non-whitespace characters at
     * the end of the file name.  Any file name that begins with '.'
     * will always be returned intact.  */
    @Override
    public String getDisplayName() {
        String name = getName();
        if (s_re.match(PATTERN,name)) {
            return s_re.group(1);
        }
        return name;
    }



    /**
     * Checks for valid characters in a file name and also checks that
     * the name corresponds to a compatible MIME type. If no extension
     * is supplied and the current file name includes an extension,
     * add that extension to the name before guessing its MIME type.
     */
    public boolean isValidNewName(String name) {

        boolean isValid = isValidName(name) == 0;
        if (isValid && !isNew()) {

            String contentType = Util.guessContentType
                (appendExtension(name),null);
            isValid = contentType.equals(getContentType());
        }

        return isValid;
    }

    /**
     * Append the extension of this file to a given file name.  If the
     * given file name already has an extension it will be returned
     * without modification.
     */
    public String appendExtension(String name) {
        String ext = getExtension(name);
        if (ext.equals("")) {
            name += getExtension(getName());
        }
        return name;
    }

    /**
     * Return the extension of a file name, if present. Otherwise
     * return an empty string.
     */
    public static String getExtension(String name) {
        if (s_re.match(PATTERN,name)) {
            return s_re.group(2);
        }
        return "";
    }

    /**
     * Saves a new revision of this file.
     *
     * @param src The source file to upload to the database.
     * @param originalName The original name of the file. The servlet container may not return the same file extension. For example, tomcat will return some random .tmp file, which will cause mime type checking to fail.
     * @param versionDesc The description of this version of the file.
     */
    public void saveNewRevision(java.io.File src, String originalName, String versionDesc) {
        setContent(src, getName(), getDescription(), getContentType());
        applyTag(versionDesc);
        save();

    }
}
