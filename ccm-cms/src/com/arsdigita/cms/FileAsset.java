/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.mimetypes.MimeType;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Locale;


/**
 * An {@link com.arsdigita.cms.Asset asset} describing a concrete
 * file, such as an image.
 *
 * @author Scott Seago (scott@arsdigita.com)
 * @version $Id: FileAsset.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class FileAsset extends BinaryAsset {

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.cms.FileAsset";


    public static final String CONTENT = "content";
    public static final String HEIGHT = "height";
    public static final String LENGTH = "length";
    public static final String WIDTH = "width";

    //public static final String MIME_JPEG = "image/jpeg";
    //public static final String MIME_GIF = "image/gif";

    /**
     * Default constructor. This creates a new text asset.
     **/
    public FileAsset() {
        super(BASE_DATA_OBJECT_TYPE);
    }

    /**
     * Constructor. The contained <code>DataObject</code> is retrieved
     * from the persistent storage mechanism with an <code>OID</code>
     * specified by <i>oid</i>.
     *
     * @param oid The <code>OID</code> for the retrieved
     * <code>DataObject</code>.
     **/
    public FileAsset(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    /**
     * Constructor. The contained <code>DataObject</code> is retrieved
     * from the persistent storage mechanism with an <code>OID</code>
     * specified by <i>id</i> and
     * <code>FileAsset.BASE_DATA_OBJECT_TYPE</code>.
     *
     * @param id The <code>id</code> for the retrieved
     * <code>DataObject</code>.
     **/
    public FileAsset(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public FileAsset(DataObject obj) {
        super(obj);
    }

    public FileAsset(String type) {
        super(type);
    }

    /**
     * @return the base PDL object type for this item. Child classes should
     *  override this method to return the correct value
     */
    public String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    public BigDecimal getWidth() {
        return (BigDecimal) get(WIDTH);
    }

    public void setWidth(BigDecimal width) {
        set(WIDTH, width);
    }

    public BigDecimal getHeight() {
        return (BigDecimal) get(HEIGHT);
    }

    public void setHeight(BigDecimal height) {
        set(HEIGHT, height);
    }

    /**
     * Retrieves the Blob content.
     *
     * @return the Blob content
     */
    public byte[] getContent() {
        return (byte[]) get(CONTENT);
    }

    /**
     * Sets the Blob content, and sets the length as well.
     */
    protected void setContent(byte[] content) {
        set(CONTENT, content);
        setLength();
    }

    public Long getLength() {
        // just in case
        setLength();
        return (Long) get(LENGTH);
    }

    public void setLength(Long length) {
        set(LENGTH, length);
    }

    public void setLength() {
        Long length = new Long(super.getSize());
        setLength(length);
    }

    /**
     * Load the file asset from the specified file. Automatically guesses
     * the mime type of the file.
     *
     * @param fileName  The original name of the file
     * @param file The actual file on the server
     * @param defaultMimeType The default mime type for the file
     */
    public void loadFromFile(String fileName, File file, String defaultMimeType)
        throws IOException {

        // Guess mime type
        MimeType mime = MimeType.guessMimeTypeFromFile(fileName);

        if(mime == null && defaultMimeType != null) {
            // Set default mime type
            mime = MimeType.loadMimeType(defaultMimeType);
        }

        setMimeType(mime);

        // Extract the filename
        int i = fileName.lastIndexOf("/");
        if(i > 0) {
            fileName = fileName.substring(i+1);
        }
        i = fileName.lastIndexOf("\\");  // DOS-style
        if(i > 0) {
            fileName = fileName.substring(i+1);
        }

        setName(fileName);

        FileInputStream in = new FileInputStream(file);
        readBytes(in);
    }

    /**
     * Write the file asset content to a file.
     *
     * @param file      The file on the server to write to.
     */
    public void writeToFile(File file)
        throws IOException {
        FileOutputStream fs = new FileOutputStream(file);
        try {
            fs.write(getContent());

        } finally {
            if (null != fs) {
                fs.close();
            }
        }
    }


    /**
     * Retrieve all files in the database. Extremely expensive !
     *
     * @return a collection of FileAssets
     */
    public static FileAssetCollection getAllFiles() {
        DataCollection da = SessionManager.getSession().retrieve
            (BASE_DATA_OBJECT_TYPE);
        return new FileAssetCollection(da);
    }

    /**
     * Find all files whose name matches the specified keyword
     *
     * @param keyword a String keyword
     * @param context the context for the retrieved items. Should be
     *   {@link ContentItem#DRAFT} or {@link ContentItem#LIVE}
     * @return a collection of files whose name matches the keyword
     */
    public static FileAssetCollection getFilesByKeyword(
                                                        String keyword, String context
                                                        ) {
        FileAssetCollection c = getAllFiles();
        c.addOrder(Asset.NAME);
        Filter f;
        f = c.addFilter("name like (\'%\' || :keyword || \'%\')");
        f.set("keyword", keyword);
        f = c.addFilter("version = :version");
        f.set("version", context);
        return c;
    }

    /**
     * Find all files whose name matches the specified keyword
     *
     * @param keyword a String keyword
     * @return a collection of files whose name matches the keyword
     */
    public static FileAssetCollection getFilesByKeyword(String keyword) {
        return getFilesByKeyword(keyword, ContentItem.DRAFT);
    }


    protected void beforeSave() {
        if( null == getLanguage() ) {
            setLanguage( Locale.getDefault().getLanguage() );
        }

        super.beforeSave();
    }
}
