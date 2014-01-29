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

import com.arsdigita.mimetypes.ImageMimeType;
import com.arsdigita.mimetypes.MimeTypeCollection;

import java.util.HashMap;

import org.apache.log4j.Logger;

/**
 * Implements the Factory pattern in order to
 * supply implementation-specific instances of {@link ImageSizer}.
 * The factory is initialized in the mime-type initializer.
 *
 * @author <a href="mailto:sfreidin@arsdigita.com">Stanislav Freidin</a>
 * @version $Id: ImageSizerFactory.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class ImageSizerFactory {

    private static HashMap s_sizers = new HashMap();

    private static final Logger s_log = Logger.getLogger(ImageSizerFactory.class);
    /**
     * Constructor
     */
    public ImageSizerFactory() {}

    /**
     * Add an {@link ImageSizer} instance to the factory
     *
     * @param mime the mime-type of the image which the image sizer
     *   is able to handle
     * @param sizer an implementation-specific instance of {@link ImageSizer}
     */
    public static void addImageSizer(String mime, ImageSizer sizer) {
        s_sizers.put(mime, sizer);
    }

    /**
     * Obtain an instance of an {@link ImageSizer} which can determine
     * the size of an image with the given mime-type.
     *
     * @param mime the mime-type of the image which the image sizer
     *   should handle
     * @return an appropriate instance of {@link ImageSizer}, or null
     *   if no such instance exists
     */
    public static ImageSizer getImageSizer(String mime) {
        return (ImageSizer)s_sizers.get(mime);
    }

    /**
     * Initialize the factory by loading all image sizers from the
     * database
     */
    public static void initialize() {
        MimeTypeCollection mimes = ImageMimeType.getAllImageMimeTypes();
        while(mimes.next()) {
            ImageMimeType mime = (ImageMimeType)mimes.getMimeType();
            String sizerName = mime.getImageSizer();
            if(sizerName != null) {
                try {
                    Class sizerClass = Class.forName(sizerName);
                    ImageSizer sizer = (ImageSizer)sizerClass.newInstance();
                    addImageSizer(mime.getMimeType(), sizer);
                } catch (Exception e) {
                    s_log.error("Initialization error", e);
                    // Do nothing
                }
            }
        }
    }

}
