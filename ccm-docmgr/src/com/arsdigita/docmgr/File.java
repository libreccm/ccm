/*
 * Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.docmgr;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Vector;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.servlet.http.HttpServletRequest;

import org.apache.oro.text.perl.Perl5Util;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.mimetypes.MimeType;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.metadata.Property;
import com.arsdigita.util.Assert;

/**
 * Represents a File in the document manager application.
 *
 * @author Stefan Deusch (stefan@arsdigita.com)
 * @author Ron Henderson (ron@arsdigita.com)
 * @version $Id: //apps/docmgr/dev/src/com/arsdigita/docmgr/File.java#19 $
 */

public class File extends ResourceImpl implements Constants {

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.docs.File";

    public static final String DEFAULT_MIME_TYPE =
        "application/octet-stream";

    private static Perl5Util s_re = new Perl5Util();
    private static final String PATTERN =
        "/^([^\\.].*)(\\.\\w+)$/";

    // Used to cache the MIME type retrieved from the database to
    // restrict changing MIME types for persistent files.
    private String m_contentType;

    /**
     * Creates a new file by retrieving it from the underlying data
     * object.
     *
     * @param dataObject the dataObject corresponding to this file
     */
    public File(DataObject dataObject) {
        super(dataObject);
        m_contentType = getContentType();
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
        m_contentType = getContentType();
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

    public static File retrieveFile(DataObject dataObject) {
        Assert.exists(dataObject);

        return new File(dataObject);
    }



    protected void beforeSave() {
        set(IS_FOLDER, Boolean.FALSE);
        // Just verify equality if the file already has an MIME type
        if(!isNew()) {
            if (null != m_contentType) {
                if (!m_contentType.equals(getContentType())) {
                    throw new TypeChangeException
                        ("Attempt to change MIME type of an existing " +
                         "file from " + m_contentType + " to " +
                         getContentType());
                }
            }
        }
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

    public void setContentType(String type) {
        set(TYPE, type);
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
                           String description)
        throws ResourceException
    {
        DataHandler dh = new DataHandler(new FileDataSource(file));
        String mimeType = dh.getContentType();
        setDataHandler(dh);
        setName(name);
        setDescription(description);
    }

    /**
     * Convenience method for use with plain text files.  Sets the
     * content of the file to a String and sets its MIME type to
     * "text/plain".
     */

    final public void setText(String text) {
        set(CONTENT, text.getBytes());
        set(SIZE, new BigDecimal(text.getBytes().length));
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
            throw new DataObjectNotFoundException("Doc Blob not found");
        }
        DocBlobject dobj = (DocBlobject)DomainObjectFactory.newInstance(data);
        return (byte[]) dobj.getContent();
    }

    /**
     * Provides the mechanism to set this file's content.
     *
     * @param dh the DataHandler for this part's content
     */

    public void setDataHandler(DataHandler dh)
        throws ResourceException
    {
        if(isNew()) {
            String type = getBaseType(dh.getContentType());
            setContentType(type);
        }
        set(NAME, dh.getName());



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

    private void setInternalContentType() {
        m_contentType = getContentType();
    }

    /**
     * @return the base MIME type (without the parameters list)
     */

    private String getBaseType(String rawdata) {
        int i = rawdata.indexOf(";");
        if (i != -1) {
            return rawdata.substring(0, i);
        } else {
            return rawdata;
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

    protected Vector getPropertyNames() {
        Vector names = super.getPropertyNames();
        Iterator props = getObjectType().getDeclaredProperties();
        while (props.hasNext()) {
            names.addElement(((Property) props.next()).getName());
        }
        return names;
    }

    /**
     * Initialize the content type of a file by looking up a MIME type
     * from the file extension or an optional Content-Type header in
     * an HttpServletRequest.  If these fail the content will be
     * initialized to "application/octet-stream".
     *
     * This only works on a new file
     *
     * @param request an HttpServletRequest which might contain a
     * Content-Type header, and can be null.
     *
     * @pre isNew()
     */

    public void initializeContentType(HttpServletRequest request)
        throws ContentTypeException {

        if (!isNew()) {
            throw new ContentTypeException
                ("Attempt to change content type of file " + getID() +
                 " from existing type: " + getContentType());
        } else {
            setContentType(guessContentType(getName(),request));
            setInternalContentType();
        }
    }

    /**
     * Guess the content type for a file by checking the file
     * extension agains the know database of types, or for the
     * existence of a MIME type header in an HttpServletRequest.  If
     * these both fail, or if the content type set in the request is
     * not one of the recognized types on the system, return a content
     * type of "application/octet-stream".
     *
     * @param name the name of a file to be used for an
     * extension-based type lookup
     * @param request an HttpServletRequest which might contain a
     * Content-Type header, and can be null
     */

    public static String guessContentType(String name,
                                          HttpServletRequest request) {

        // Try looking up the type based on the filename extensions

        com.arsdigita.mimetypes.MimeType mimeType =
            com.arsdigita.mimetypes.MimeType.guessMimeTypeFromFile(name);

        // Try looking up from the request.  We require that the
        // resolved type correspond to a known MIME type in the

        if (mimeType == null && request != null) {
            String contentType = request.getHeader("Content-Type");
            s_log.debug("Retrieved content type " + contentType +
                        "from request " + request);
            if (contentType != null) {
                mimeType = com.arsdigita.mimetypes.MimeType.loadMimeType(contentType);
                if (mimeType == null) {
                    s_log.warn("Couldn't load mime type for " + contentType);
                }
            }
        }

        if (mimeType != null) {
            return mimeType.getMimeType();
        } else {
            return DEFAULT_MIME_TYPE;
        }
    }

    /**
     * Returns the display name of a file, that is the file name minus
     * any extension if present.  An extension is defined as the '.'
     * character followed by one or more non-whitespace characters at
     * the end of the file name.  Any file name that begins with '.'
     * will always be returned intact.  */

    public String getDisplayName() {
        String name = getName();
        if (s_re.match(PATTERN,name)) {
            return s_re.group(1);
        }
        return name;
    }

    /**
     * Returns true if the given content type matches the current
     * content type of a file.  If the file has not yet been saved,
     * any content type is allowed.
     */

    public boolean isValidContentType(String contentType) {
        if (m_contentType != null) {
            return m_contentType.equals(contentType);
        } else {
            return true;
        }
    }

    /**
     * Checks for valid characters in a file name and also checks that
     * the name corresponds to a compatible MIME type. If no extension
     * is supplied and the current file name includes an extension,
     * add that extension to the name before guessing its MIME type.
     */

    public boolean isValidNewName(String name) {

        if (m_contentType != null) {
            String contentType = guessContentType
                (appendExtension(name),null);
            if (!contentType.equals(m_contentType)) {
                return false;
            }
        }

        return isValidName(name) == 0;
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

    protected void afterSave() {
        super.afterSave();

        // Always update to the saved value in case this instance is
        // used without being reloaded from the database.
        setInternalContentType();
    }


    /**
     * Saves a new revision of this file.
     *
     * @param src The source file to upload to the database.
     * @param originalName The original name of the file. The servlet container may not return the same file extension. For example, tomcat will return some random .tmp file, which will cause mime type checking to fail.
     * @param versionDesc The description of this version of the file.
     * @param req Optional Htttp request for guessing the file's mime-type.
     */
    public void saveNewRevision(java.io.File src, String originalName, String versionDesc, HttpServletRequest req) {
        setContent(src, getName(), getDescription());
        applyTag(versionDesc);
        save();

    }
}
