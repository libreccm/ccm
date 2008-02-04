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
package com.arsdigita.mimetypes.image;

import java.awt.Dimension;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.apache.log4j.Logger;

/**
 * A class which is capable of reading an image and spitting out its
 * size. Child classes should implement the {@link #computeImageSize} method
 * in order to provide image-format-specific functionality.
 * <p>
 * This class and its subclasses are used in the
 * {@link com.arsdigita.mimetypes.MimeType MimeType} class.
 * <p>
 * Note that the only legal way to obtain an instance of this class
 * is to call {@link ImageSizerFactory#getImageSizer}
 *
 * @author <a href="mailto:sfreidin@arsdigita.com">Stanislav Freidin</a>
 * @version $Id: ImageSizer.java 736 2005-09-01 10:46:05Z sskracic $
 */
public abstract class ImageSizer {

    public static final String versionId = "$Id: ImageSizer.java 736 2005-09-01 10:46:05Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private static Logger s_log = Logger.getLogger(ImageSizer.class);
    /**
     * This constructor is protected since the only way to get
     * an instance of the ImageSizer is to call
     * {@link ImageSizerFactory#getImageSizer}
     */
    protected ImageSizer() {}

    /**
     * Read a file from the filesystem and try to determine its size
     *
     * @param path The path of the file to read
     * @return a {@link Dimension} which will hold the size of an image
     */
    public Dimension computeImageSize(String path) throws IOException {
        DataInputStream in = null;
        Dimension size = null;
        try {
            in = new DataInputStream(new FileInputStream(path));
            size = computeSize(in);
        } catch (IOException e) {
            throw e;
        } finally {
            in.close();
        }
        return size;
    }

    /**
     * Construct a new ImageSizer. Read a file from the filesystem
     * and try to determine its size
     *
     * @param file The fike to read
     */
    public Dimension computeImageSize(File file) throws IOException {
        DataInputStream in = null;
        Dimension size = null;
        try {
            in = new DataInputStream(new FileInputStream(file));
            size = computeSize(in);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch(IOException e) {
                    s_log.error("Couldn't close input stream", e);
                }

            }

        }
        return size;
    }

    /**
     * Scale width and height down to fit within the specified
     * dimensions.  Maintain aspect ratio.  If the image already fits
     * within the specified dimensions, then return the image dimensions
     * unchanged.
     *
     * @param d a {@link Dimension} which holds the size of the image
     * @param maxWidth the maximum width
     * @param maxHeight the maximum height
     */
    public static Dimension getScaledSize(
                                          Dimension d, int maxWidth, int maxHeight
                                          ) {
        return getScaledSize((int)d.getWidth(), (int)d.getHeight(),
                             maxWidth, maxHeight);
    }

    /**
     * Scale the specified dimensions down to fit within the maximum
     * dimensions. Maintain aspect ratio. If the original dimensions
     * already fit within the maximum dimensions, return the
     * original dimensions
     *
     * @param width The current width
     * @param height The current height
     * @param maxWidth the maximum width
     * @param maxHeight the maximum height
     */
    public static Dimension getScaledSize(
                                          int width, int height, int maxWidth, int maxHeight
                                          ) {
        if ((width <= maxWidth) && (height <= maxHeight)) {
            return new Dimension(width, height);
        }

        // calculate the scaling factor for width
        double widthFactor = (double) width / (double) maxWidth;

        // calculate the scaling factor for height
        double heightFactor = (double) height / (double) maxHeight;

        double scaleFactor;

        if (widthFactor > heightFactor) {
            scaleFactor = widthFactor;
        } else {
            scaleFactor = heightFactor;
        }

        int scaleWidth = (int) (width / scaleFactor);
        int scaleHeight = (int) (height / scaleFactor);

        return new Dimension( scaleWidth, scaleHeight);
    }

    /**
     * Read the input stream, determine the size of the image,
     * and return it
     *
     * @param in The InputStream to read
     * @return The size of the image, or null on failure
     */
    public abstract Dimension computeSize(DataInputStream in) throws IOException;

    /**
     * Utility method to convert 2 bytes to a short integer
     */
    protected static short toShort(byte highOrder, byte lowOrder) {
        short result = highOrder;
        result <<= 8;
        result &= 0x7F00;
        short low = lowOrder;
        low &= 0x00FF;
        result |= low;
        return result;
    }

}
