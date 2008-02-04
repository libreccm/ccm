/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.mimetypes;

import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.DataAssociation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.arsdigita.persistence.DataObject;
import com.arsdigita.util.UncheckedWrapperException;
import org.apache.log4j.Logger;

/**
 * Provides information about a single mime type, such as "image/gif"
 * or "text/plain". The mime-type itself may be accessed by calling
 * {@link #getMimeType}; the user-readable label can be accessed by
 * calling {@link #getLabel}.
 *
 * <p> This class is a member of each {@link com.arsdigita.cms.Asset}; it is used in
 * (among other places) {@link
 * com.arsdigita.cms.ui.authoring.TextPageBody} and {@link
 * com.arsdigita.cms.ui.authoring.ArticleImage} in order to properly
 * handle uploaded files.
 *
 * @author Jack Chung
 * @author Stanislav Freidin
 *
 * @version $Revision: #10 $ $DateTime: 2004/08/16 18:10:38 $
 */

public class MimeType extends DomainObject {

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.cms.MimeType";
    public static final String MIME_TYPE = "mimeType";
    public static final String LABEL = "label";
    public static final String FILE_EXTENSION = "fileExtension";
    public static final String ALL_FILE_EXTENSIONS = "extensions";
    public static final String JAVA_CLASS = "javaClass";
    public static final String OBJECT_TYPE = "objectType";

    private static Class[] s_dataArgs = new Class[]{DataObject.class};
    private static Class[] s_newArgs = new Class[] {String.class,String.class};

    private static Logger s_log = Logger.getLogger(MimeType.class);

    /**
     * Load an existing <code>MimeType</code>.
     */
    public MimeType(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    /**
     * Construct a new <code>MimeType</code> from the given {@link DataObject}.
     * All subclasses must implement this constructor.
     */
    public MimeType(DataObject obj) {
        super(obj);
    }

    /**
     * Construct a new <code>MimeType</code> with a given object type.
     * All subclasses must implement this constructor.
     */
    public MimeType(String type, String mimeType) {
        super(type);
        setMimeType(mimeType);
        setSpecificObjectType(type);
    }

    /**
     * Get the actual mime-type, such as "text/plain" or "image/gif"
     */
    public String getMimeType() {
        return (String) get(MIME_TYPE);
    }

    /**
     * Set the mime-type
     */
    public void setMimeType(String value) {
        set(MIME_TYPE, value);
    }

    /**
     * Get the user-readable label for this mime-type, such as "Plain Text"
     */
    public String getLabel() {
        return (String) get(LABEL);
    }

    /**
     * Set the user-readable label
     */
    public void setLabel(String value) {
        set(LABEL, value);
    }

    /**
     * Get the canonical file extension for the files of this mime type,
     * f.ex. "jpeg" or "txt"
     */
    public String getFileExtension() {
        return (String) get(FILE_EXTENSION);
    }

    /**
     *  this returns a collection of Strings representing all of the
     *  file extensions associated with this mime type
     */
    public MimeTypeExtensionCollection getAllFileExtensions() {
        DataAssociation association = (DataAssociation)get(ALL_FILE_EXTENSIONS);
        if (association != null) {
            return new MimeTypeExtensionCollection(association);
        } else {
            return null;
        }
    }

    /**
     *  this tells you if a particular extension is part of the mime type
     */
    public boolean hasFileExtension(String fileExtension) {
        DataAssociation association = (DataAssociation)get(ALL_FILE_EXTENSIONS);
        if (association != null) {
            association.addEqualsFilter(MimeTypeExtension.FILE_EXTENSION,
                                        fileExtension);
            boolean exists = association.next();
            association.close();
            return exists;
        } else {
            return false;
        }        
    }


    /**
     * Set the the canonical file extension for the files of this mime type
     */
    public void setFileExtension(String value) {
        set(FILE_EXTENSION, value);
    }

    /**
     * Return the name of the proper Java subclass of this mime type
     */
    public String getJavaClass() {
        return (String) get(JAVA_CLASS);
    }

    /**
     * Set the name of the proper Java subclass of this mime type
     */
    public void setJavaClass(String javaClassName) {
        set(JAVA_CLASS, javaClassName);
    }

    /**
     * Return the name of the specific object type of this mime type
     */
    public String getSpecificObjectType() {
        return (String) get(OBJECT_TYPE);
    }

    /**
     * Set the name of the specific object type of this mime type
     */
    public void setSpecificObjectType(String objectType) {
        set(OBJECT_TYPE, objectType);
    }

    /**
     * Return a specific Java subclass of this mime type
     *
     * @return the specific Java subclass of this mime type (could
     *  be the same as "this")
     */
    public MimeType specialize() {

        // Check if we need to specialize
        if (getClass().getName().equals(getJavaClass())) {
            return this;
        }

        // Specialize by specializing the data object and
        // feeding it to a child class
        try {
            Class mimeClass = Class.forName(getJavaClass());
            Constructor constr = mimeClass.getConstructor(s_dataArgs);
            DataObject data = GenericDomainService.getDataObject(this);
            data.specialize(getSpecificObjectType());
            return (MimeType)constr.newInstance(new DataObject[]{data});
        } catch (Exception e) {
            s_log.error("Error in specialization", e);
            return this;
        }
    }

    /**
     * Return the base data object type for this mime-type
     */
    public String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    /**
     * Return the prefix of this mime type; the prefix is
     * everything before the "/".
     *
     * @return the prefix of this mime type
     */
    public String getPrefix() {
        String mt = getMimeType();
        int i = mt.indexOf("/");
        if (i == -1) {
            return mt;
        } else {
            return mt.substring(0, i);
        }
    }

    /**
     * Return an instance of the specified mime type. If no such mime
     * type exists, return null. Will return an appropriate java
     * subclass of MimeType
     *
     * @param mimeTypeName The name of the mime type to load, such as
     *   "text/html" or "image/jpeg"
     * @return a proper subclass of MimeType, or null if no such mime type
     *   exists
     */
    public static MimeType loadMimeType(String mimeTypeName) {
        String mimeClassName;
        MimeType mime;

        try {
            mime = new MimeType
                (new OID(MimeType.BASE_DATA_OBJECT_TYPE, mimeTypeName));
        } catch (DataObjectNotFoundException ex) {
            return null;
        }

        mimeClassName = mime.getJavaClass();

        try {
                Class mimeClass = Class.forName(mimeClassName);
                Constructor constr = mimeClass.getConstructor(s_dataArgs);
                DataObject data = GenericDomainService.getDataObject(mime);
                data.specialize(mime.getSpecificObjectType());
                return (MimeType)constr.newInstance(new DataObject[]{data});

        } catch(Exception e) {
            throw new UncheckedWrapperException("Error instantiating MimeType", e);
        }
    }

    /**
     * Create a new instance of the specified Java subclass of
     * MimeType.
     *
     * @param mimeTypeName The name of the mime type to create, such as
     *   "text/html" or "image/jpeg"
     * @param javaClass The Java class of the new mime type
     * @param objectType The PDL object type of the new mime type
     * @return a proper subclass of MimeType
     */
    public static MimeType createMimeType(
                                          String mimeTypeName, String javaClass, String objectType
                                          ) {

            Exception exception = null;
			try {
				Class mimeClass = Class.forName(javaClass);
				Constructor constr = mimeClass.getConstructor(s_newArgs);
				MimeType mime = (MimeType)constr.newInstance(new String[]{objectType,mimeTypeName});
				mime.setJavaClass(javaClass);
				return mime;
			} catch (SecurityException e) {
				s_log.error("SecurityException", e);
				exception = e;
			} catch (IllegalArgumentException e) {
				s_log.error("IllegalArgumentException", e);
				exception = e;
			} catch (ClassNotFoundException e) {
				s_log.error("ClassNotFoundException", e);
				exception = e;
			} catch (NoSuchMethodException e) {
				s_log.error("NoSuchMethodException", e);
				exception = e;
			} catch (InstantiationException e) {
				s_log.error("InstantiationException", e);
				exception = e;
			} catch (IllegalAccessException e) {
				s_log.error("IllegalAccessException", e);
				exception = e;
			} catch (InvocationTargetException e) {
				s_log.error("InvocationTargetException", e);
				exception = e;
			}

        String msg = "Couldn't createMimeType for mimeType " +
                mimeTypeName +
                " Class: " + javaClass +
                " Object Type: " + objectType;

        throw new UncheckedWrapperException(msg, exception);

    }

    /**
     * Guess the mime type from a file extension.
     *
     * @param fileExtension the extension associated with a mime type
     * @return the guessed mime type, or null if there is no
     * corresponding mime type in the database.
     */
    public static MimeType guessMimeType(String fileExtension) {
        MimeTypeExtension ext = 
            MimeTypeExtension.retrieve(fileExtension.toLowerCase());
        if (ext == null) {
            return null;
        } else {
            return ext.getMimeTypeObject();
        }
    }

    /**
     * Guess the mime type from the given file name. Computes the file
     * extension as the substring following the last '.' and calls
     * {@link #guessMimeType} to lookup the corresponding type.  If the
     * file has no extension, this will always return null.
     *
     * @param fileName the file name
     * @return the guessed mime type, or null if there is no
     * corresponding mime type in the database.
     */
    public static MimeType guessMimeTypeFromFile(String fileName) {
        int i = fileName.lastIndexOf(".");
        if (i != -1) {
            return MimeType.guessMimeType(fileName.substring(i+1));
        } else {
            return null;
        }
    }

    /**
     * Save this mime type and remember the correct java class
     */
    protected void beforeSave() {

        if (isNew()) {
            if (null == getJavaClass()) {
                setJavaClass(getClass().getName());
            }
            if (null == getSpecificObjectType()) {
                setSpecificObjectType
                    (getOID().getObjectType().getQualifiedName());
            }
        }

        super.beforeSave();
    }

    /**
     * Get all mime types in the system
     */
    public static MimeTypeCollection getAllMimeTypes() {
        DataCollection da = SessionManager.getSession().retrieve
            (BASE_DATA_OBJECT_TYPE);
        return new MimeTypeCollection(da);
    }

    /**
     * Search all the mime types that starts with "startsWith".
     */
    public static MimeTypeCollection searchMimeTypes(String startsWith) {
        // this should be not using a data association when data
        // collection event is implemented, use it
        DataCollection da =  SessionManager.getSession().retrieve
            (BASE_DATA_OBJECT_TYPE);
        Filter filter = da.addFilter("mimeType like (:startsWith || \'%\')");
        filter.set("startsWith", startsWith);

        return new MimeTypeCollection(da);
    }

}
