/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arsdigita.cms;

import com.arsdigita.mimetypes.MimeType;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import javax.imageio.ImageIO;
import org.apache.log4j.Logger;
import org.imgscalr.Scalr;

/**
 * This is an in-memory copy of an {@link ImageAsset} to be stored in the image
 * cache of {@link BaseImage}. Also, this class is able to create server-side
 * resized versions of ImageAssets.
 *
 * @author Sören Bernstein <quasi@quasiweb.de>
 */
public class CachedImage {

    private String hash;
    private final String name;
    private final String version;
    private final MimeType mimetype;
    private byte[] image;
    private BigDecimal width;
    private BigDecimal height;
    private static final Logger s_log = Logger.getLogger(CachedImage.class);

    /**
     * Create a resized version of an ImageAsset for dispatching
     *
     * @param imageAsset the ImageAsset to save
     * @param maxWidth   the max width to resize the image to
     * @param maxHeight  the max height to resize the image to
     */
    public CachedImage(ImageAsset imageAsset, int maxWidth, int maxHeight) {
        this(imageAsset);
        this.resizeImage(maxWidth, maxHeight);
    }

    /**
     * Create a original size version of an ImageAsset for dispatching
     *
     * @param imageAsset The ImageAsset to save
     */
    public CachedImage(ImageAsset imageAsset) {

        this.hash = imageAsset.getOID().toString();
        this.name = imageAsset.getName();
        this.version = imageAsset.getVersion();
        this.mimetype = imageAsset.getMimeType();
        this.image = imageAsset.getContent();
        this.width = imageAsset.getWidth();
        this.height = imageAsset.getHeight();
    }

    /**
     * Create a resized version of another CachedImage. This is a convienience
     * constructor to handle the maxWidth and maxHeight param in a single
     * String.
     *
     * @param cachedImage the cachedImage to resize
     * @param resizeParam the resize paramter as
     *                    "&maxWidth=<int>&maxHeight=<int>"
     */
    public CachedImage(CachedImage cachedImage, String resizeParam) {
        this(cachedImage);

        int maxWidth = 0;
        int maxHeight = 0;

        String[] params = resizeParam.split("&");
        for (int i = 0; i < params.length; i++) {
            if (params[i].isEmpty()) {
                continue;
            }

            String key = params[i].substring(0, params[i].indexOf("="));
            String value = params[i].substring(params[i].indexOf("=") + 1);

            if (key.equalsIgnoreCase("maxWidth")) {
                maxWidth = Integer.parseInt(value);
            }

            if (key.equalsIgnoreCase("maxHeight")) {
                maxHeight = Integer.parseInt(value);
            }
        }

        this.resizeImage(maxWidth, maxHeight);
    }

    /**
     * Create a resized version aof another CacheImage.
     *
     * @param cachedImage the CachedImage to resize
     * @param maxWidth    max width of the image after resizing
     * @param maxHeight      max height of the image after resizing
     */
    public CachedImage(CachedImage cachedImage, int maxWidth, int maxHeight) {
        this(cachedImage);
        this.resizeImage(maxWidth, maxHeight);
    }

    /**
     * This is just for internal use to set all the fields.
     *
     * @param cachedImage the CacheImage
     */
    private CachedImage(CachedImage cachedImage) {
        this.hash = cachedImage.hash;
        this.name = cachedImage.getName();
        this.version = cachedImage.getVersion();
        this.mimetype = cachedImage.getMimeType();
        this.image = cachedImage.getImage();
        this.width = cachedImage.getWidth();
        this.height = cachedImage.getHeight();
    }

    /**
     * Get filename
     *
     * @return the filename
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get actual width of this instance
     *
     * @return image width
     */
    public BigDecimal getWidth() {
        return this.width;
    }

    /**
     * Get actual height of this instance
     *
     * @return image height
     */
    public BigDecimal getHeight() {
        return this.height;
    }

    /**
     * Get actual size of this instance
     *
     * @return image size
     */
    public int getSize() {
        return this.image.length;
    }

    /**
     * Get version (aka live of draft)
     */
    public String getVersion() {
        return this.version;
    }

    /**
     * Get {@link MimeType}
     *
     * @return the {@MimeType}
     */
    public MimeType getMimeType() {
        return this.mimetype;
    }

    /**
     * Get the image data
     *
     * @return the image data
     */
    public byte[] getImage() {
        return this.image;
    }

    /**
     * Write the image to an OutputStream
     *
     * @param os OutputStream to be written to
     *
     * @return number of bytes written
     *
     * @throws IOException
     */
    public long writeBytes(OutputStream os) throws IOException {
        os.write(this.getImage());
        return (long) (this.getSize());
    }

    /**
     * Write the image asset content to a file.
     *
     * @param file The file on the server to write to.
     */
    public void writeToFile(File file) throws IOException {
        FileOutputStream fs = new FileOutputStream(file);
        try {
            fs.write(this.getImage());

        } finally {
            if (null != fs) {
                fs.close();
            }
        }
    }

    /**
     * Method to proportional resize an image into the defined boundaries.
     * Either width or height can be 0.
     * If height is 0, the image will be fit to width.
     * If width is 0, the image will be fit to height.
     * If both paramters are 0, this method will not do anything.
     *
     * @param width  max width of the image after resizing
     * @param height max height of the image after resizing
     */
    private void resizeImage(int width, int height) {

        // No valid resizing imformation
        if (width <= 0 && height <= 0) {
            return;
        }

        // Read byte array in BufferedImage
        BufferedImage bufferedImage = null;

        try {
            bufferedImage = ImageIO.read(new ByteArrayInputStream(this.getImage()));
        } catch (IOException ioEx) {
            s_log.warn("Could not read image", ioEx);
        }

        // Resize image with imagescalr
        if (width > 0 && height > 0) {
            bufferedImage = Scalr.resize(bufferedImage, Scalr.Method.QUALITY, width, height);
        }
        if (width > 0 && height <= 0) {
            bufferedImage = Scalr.resize(bufferedImage, Scalr.Method.QUALITY, Scalr.Mode.FIT_TO_WIDTH, width);
        }
        if (width <= 0 && height > 0) {
            bufferedImage = Scalr.resize(bufferedImage, Scalr.Method.QUALITY, Scalr.Mode.FIT_TO_HEIGHT, height);
        }

        // Set Dimensions
        this.width = new BigDecimal(bufferedImage.getWidth());
        this.height = new BigDecimal(bufferedImage.getHeight());

        this.hash = this.hash + "&width=" + this.width + "&height=" + this.height;

        // Write BufferedImage to byte array
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            ImageIO.write(bufferedImage, "JPEG", out);
            this.image = out.toByteArray();
        } catch (IOException ioEx) {
            s_log.warn("Could not write byte array", ioEx);
        } catch (IllegalArgumentException illEx) {
            s_log.warn("image is not initialized", illEx);
        }
    }

    @Override
    public int hashCode() {
        return this.hash.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CachedImage other = (CachedImage) obj;
        if ((this.hash == null) ? (other.hash != null) : !this.hash.equals(other.hash)) {
            return false;
        }
        return true;
    }
}
