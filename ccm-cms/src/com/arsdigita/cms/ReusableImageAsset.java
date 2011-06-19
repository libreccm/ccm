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
package com.arsdigita.cms;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.mimetypes.ImageMimeType;
import com.arsdigita.mimetypes.MimeType;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.versioning.VersionedACSObject;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;

import java.awt.image.RenderedImage;
import javax.media.jai.JAI;

import org.apache.log4j.Logger;

/**
 * <p>An {@link com.arsdigita.cms.Asset asset} representing a reusable
 * image.</p>
 *
 * @see com.arsdigita.cms.ImageAsset
 *
 * @author Scott Seago (sseago@redhat.com)
 * @author Stanislav Freidin
 *
 * @version $Id: ReusableImageAsset.java 754 2005-09-02 13:26:17Z sskracic $
 */
public class ReusableImageAsset extends ImageAsset {

    private static final Logger s_log =
                                Logger.getLogger(ReusableImageAsset.class);
    public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.cms.ReusableImageAsset";

    /**
     * Default constructor. This creates a new image asset.
     */
    public ReusableImageAsset() {
        super(BASE_DATA_OBJECT_TYPE);
    }

    /**
     * Constructor. The contained <code>DataObject</code> is retrieved
     * from the persistent storage mechanism with an <code>OID</code>
     * specified by <i>oid</i>.
     *
     * @param oid The <code>OID</code> for the retrieved
     * <code>DataObject</code>.
     */
    public ReusableImageAsset(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    /**
     * Constructor. The contained <code>DataObject</code> is retrieved
     * from the persistent storage mechanism with an <code>OID</code>
     * specified by <i>id</i> and
     * <code>ReusableImageAsset.BASE_DATA_OBJECT_TYPE</code>.
     *
     * @param id The <code>id</code> for the retrieved
     * <code>DataObject</code>.
     **/
    public ReusableImageAsset(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public ReusableImageAsset(DataObject obj) {
        super(obj);
    }

    public ReusableImageAsset(String type) {
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
    protected byte[] getContent() {
        return (byte[]) get(CONTENT);
    }

    /**
     * Sets the Blob content.
     */
    protected void setContent(byte[] content) {
        set(CONTENT, content);
    }

    /**
     * Load the image asset from the specified file. Automatically guesses
     * the mime type of the file. If the file is a jpeg, tries to automatically
     * determine width and height, as well.
     *
     * @param fileName  The original name of the file
     * @param File      The actual file on the server
     * @param defaultMimeType The default mime type for the file
     */
    public void loadFromFile(String fileName, File file, String defaultMimeType)
            throws IOException {

        // Guess mime type
        MimeType mime = MimeType.guessMimeTypeFromFile(fileName);
        if (s_log.isDebugEnabled()) {
            s_log.debug("Mime type is " + (null == mime ? "null" : mime.
                                           getMimeType()));
        }

        RenderedImage image = JAI.create("FileLoad", file.getPath());

        int width = image.getWidth();
        int height = image.getHeight();

        if (s_log.isDebugEnabled()) {
            s_log.debug("Width: " + width);
            s_log.debug("Height: " + height);
        }

        if (s_log.isDebugEnabled()) {
            String[] props = image.getPropertyNames();
            for (int i = 0; i < props.length; i++) {
                String prop = props[i];
                s_log.debug(prop + ": " + image.getProperty(prop));
            }
        }

        setWidth(new BigDecimal(width));
        setHeight(new BigDecimal(height));

        if (mime == null || !(mime instanceof ImageMimeType)) {
            mime = MimeType.loadMimeType(defaultMimeType);
        }

        setMimeType(mime);

        // Extract the filename
        int i = fileName.lastIndexOf("/");
        if (i > 0) {
            fileName = fileName.substring(i + 1);
        }
        i = fileName.lastIndexOf("\\");  // DOS-style
        if (i > 0) {
            fileName = fileName.substring(i + 1);
        }

        setName(fileName);

        FileInputStream in = new FileInputStream(file);
        readBytes(in);
    }

    /**
     * Write the image asset content to a file.
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
     * Retrieve all images in the database. Expensive operation.
     *
     * @return a collection of ReusableImageAssets
     */
    public static ImageAssetCollection getAllReusableImages() {
        DataCollection da = SessionManager.getSession().retrieve(
                BASE_DATA_OBJECT_TYPE);
        //da.addEqualsFilter(VersionedACSObject.IS_DELETED, new Integer(0));
        //da.addEqualsFilter(ACSObject.OBJECT_TYPE, BASE_DATA_OBJECT_TYPE);
        da.addFilter(String.format("%s = '%s'",
                                   VersionedACSObject.IS_DELETED,
                                   "0"));
        da.addFilter(String.format("%s = '%s'",
                                   ACSObject.OBJECT_TYPE,
                                   BASE_DATA_OBJECT_TYPE));
        return new ImageAssetCollection(da);
    }

    /**
     * Find all images whose name matches the specified keyword
     *
     * @param keyword a String keyword
     * @param context the context for the retrieved items. Should be
     *   {@link ContentItem#DRAFT} or {@link ContentItem#LIVE}
     * @return a collection of images whose name matches the keyword
     */
    public static ImageAssetCollection getReusableImagesByKeyword(
            String keyword, String context) {
        ImageAssetCollection c = getAllReusableImages();
        c.addOrder(Asset.NAME);
        Filter f;
        if (!(keyword == null || keyword.length() < 1)) {
            f = c.addFilter("lower(name) like lower(\'%\' || :keyword || \'%\')");
            f.set("keyword", keyword);            
        }
        f = c.addFilter("version = :version");
        f.set("version", context);
        return c;
    }

    /**
     * Find all images whose name matches the specified keyword
     *
     * @param keyword a String keyword
     * @return a collection of images whose name matches the keyword
     */
    public static ImageAssetCollection getReusableImagesByKeyword(String keyword) {
        return getReusableImagesByKeyword(keyword, ContentItem.DRAFT);
    }
}
