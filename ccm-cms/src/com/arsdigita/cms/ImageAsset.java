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
package com.arsdigita.cms;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.mimetypes.ImageMimeType;
import com.arsdigita.mimetypes.MimeType;
import com.arsdigita.mimetypes.image.ImageSizer;
import com.arsdigita.mimetypes.image.ImageSizerFactory;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.versioning.VersionedACSObject;
import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import org.apache.log4j.Logger;

/**
 * <p>An {@link com.arsdigita.cms.Asset asset} representing an
 * image. An ImageAsset is deleted when its parent content item is
 * deleted and is not intended to be reused between content
 * items..</p>
 *
 * @see com.arsdigita.cms.ReusableImageAsset
 * @see com.arsdigita.cms.BinaryAsset
 *
 * @author Jack Chung
 * @author Stanislav Freidin
 *
 * @version $Revision: #20 $ $DateTime: 2004/08/17 23:15:09 $
 */
public class ImageAsset extends BinaryAsset {
    public static final String versionId =
        "$Id: ImageAsset.java 287 2005-02-22 00:29:02Z sskracic $" +
        "$Author: sskracic $" +
        "$DateTime: 2004/08/17 23:15:09 $";

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.cms.ImageAsset";

    public static final String CONTENT = "content";
    public static final String HEIGHT = "height";
    public static final String WIDTH = "width";

    public static final String MIME_JPEG = "image/jpeg";
    public static final String MIME_GIF = "image/gif";

	private static final Logger s_log = Logger.getLogger(ImageAsset.class);
    /**
     * Default constructor. This creates a new image asset.
     */
    public ImageAsset() {
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
    public ImageAsset(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    /**
     * Constructor. The contained <code>DataObject</code> is retrieved
     * from the persistent storage mechanism with an <code>OID</code>
     * specified by <i>id</i> and
     * <code>ImageAsset.BASE_DATA_OBJECT_TYPE</code>.
     *
     * @param id The <code>id</code> for the retrieved
     * <code>DataObject</code>.
     **/
    public ImageAsset(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public ImageAsset(DataObject obj) {
        super(obj);
    }

    public ImageAsset(String type) {
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
     * @param file      The actual file on the server
     * @param defaultMimeType The default mime type for the file
     */
    public void loadFromFile(String fileName, File file, String defaultMimeType)
        throws IOException {

        // Guess mime type
        MimeType mime = MimeType.guessMimeTypeFromFile(fileName);

        if(mime != null && mime instanceof ImageMimeType) {
            guessSize(file, (ImageMimeType)mime);
        } else {
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
     * Guess image size by loading it from file. Set the WIDTH and HEIGHT
     * attributes, if possible
     */
    protected void guessSize(File file, ImageMimeType mime) {
        BigDecimal width = null, height = null;
        ImageSizer sizer = ImageSizerFactory.getImageSizer(mime.getMimeType());

        try {
            if(sizer != null) {
                Dimension d = sizer.computeImageSize(file);
                if(d != null) {
                    width = new BigDecimal((int)d.getWidth());
                    height = new BigDecimal((int)d.getHeight());
                }
            }
        } catch (IOException ex) {
            s_log.error("IOException guessing file size", ex);
            // do nothing
        }

        setWidth(width);
        setHeight(height);
    }

    /**
     * Retrieve all images in the database. Extremely expensive !
     *
     * @return a collection of ImageAssets
     */
    public static ImageAssetCollection getAllImages() {
        DataCollection da = SessionManager.getSession().retrieve
            (BASE_DATA_OBJECT_TYPE);
	da.addEqualsFilter(VersionedACSObject.IS_DELETED, new Integer(0));
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
    public static ImageAssetCollection getImagesByKeyword(
                                                          String keyword, String context
                                                          ) {
        ImageAssetCollection c = getAllImages();
        c.addOrder(Asset.NAME);
        Filter f;
        f = c.addFilter("name like (\'%\' || :keyword || \'%\')");
        f.set("keyword", keyword);
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
    public static ImageAssetCollection getImagesByKeyword(String keyword) {
        return getImagesByKeyword(keyword, ContentItem.DRAFT);
    }

}
