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
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import javax.imageio.ImageIO;
import org.apache.log4j.Logger;
import org.imgscalr.Scalr;
import org.imgscalr.Scalr.*;

/**
 *
 * @author Sören Bernstein (quasimodo) <sbernstein@zes.uni-bremen.de>
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

    public CachedImage(ImageAsset imageAsset, int width, int height) {
        this(imageAsset);
        this.resizeImage(width, height);
    }

    public CachedImage(ImageAsset imageAsset) {

        this.hash = imageAsset.getOID().toString();
        this.name = imageAsset.getName();
        this.version = imageAsset.getVersion();
        this.mimetype = imageAsset.getMimeType();
        this.image = imageAsset.getContent();
        this.width = imageAsset.getWidth();
        this.height = imageAsset.getHeight();
    }

    public CachedImage(CachedImage cachedImage, String resizeParam) {
        this(cachedImage);

        int width = 0;
        int height = 0;

        String[] params = resizeParam.split("&");
        for (int i = 0; i < params.length; i++) {
            if (params[i].isEmpty()) {
                continue;
            }

            String key = params[i].substring(0, params[i].indexOf("="));
            String value = params[i].substring(params[i].indexOf("=") + 1);

            if (key.equalsIgnoreCase("width")) {
                width = Integer.parseInt(value);
            }

            if (key.equalsIgnoreCase("height")) {
                height = Integer.parseInt(value);
            }
        }

        this.resizeImage(width, height);
    }

    public CachedImage(CachedImage cachedImage, int width, int height) {
        this(cachedImage);
        this.resizeImage(width, height);
    }

    private CachedImage(CachedImage cachedImage) {
        this.hash = cachedImage.hash;
        this.name = cachedImage.getName();
        this.version = cachedImage.getVersion();
        this.mimetype = cachedImage.getMimeType();
        this.image = cachedImage.getImage();
        this.width = cachedImage.getWidth();
        this.height = cachedImage.getHeight();
    }

    public String getName() {
        return this.name;
    }

    public BigDecimal getWidth() {
        return this.width;
    }

    public BigDecimal getHeight() {
        return this.height;
    }

    public int getSize() {
        return this.image.length;
    }

    public String getVersion() {
        return this.version;
    }

    public MimeType getMimeType() {
        return this.mimetype;
    }

    public byte[] getImage() {
        return this.image;
    }

    /**
     * Retrieves the Blob content.
     *
     * @return the Blob content
     */
/*
  protected byte[] getContent() {
        byte[] content = null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            ImageIO.write(image, "JPEG", out);
            content = out.toByteArray();
        } catch (IOException ioEx) {
            s_log.warn("Could not write byte array", ioEx);
        } catch (IllegalArgumentException illEx) {
            s_log.warn("image is not initialized", illEx);
        } finally {
            return content;
        }
    }
*/
    public long writeBytes(OutputStream os) throws IOException {
        byte[] bytes = this.getImage();
        os.write(bytes);

        return (long) (bytes.length);
    }

    /**
     * Write the image asset content to a file.
     *
     * @param file The file on the server to write to.
     */
    public void writeToFile(File file)
            throws IOException {
        FileOutputStream fs = new FileOutputStream(file);
        try {
            fs.write(this.getImage());

        } finally {
            if (null != fs) {
                fs.close();
            }
        }
    }

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
            bufferedImage = Scalr.resize(bufferedImage, Scalr.Method.SPEED, width, height);
        }
        if (width > 0 && height <= 0) {
            bufferedImage = Scalr.resize(bufferedImage, Scalr.Method.SPEED, Scalr.Mode.FIT_TO_WIDTH, width);
        }
        if (width <= 0 && height > 0) {
            bufferedImage = Scalr.resize(bufferedImage, Scalr.Method.SPEED, Scalr.Mode.FIT_TO_HEIGHT, height);
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

    public int hashCode() {
        return this.hash.hashCode();
    }
    
}
