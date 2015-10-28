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

import com.arsdigita.util.Dimension;
import java.io.DataInputStream;
import java.io.IOException;

/**
 * Implements the {@link ImageSizer} interface for BMP images
 *
 * @author <a href="mailto:sfreidin@arsdigita.com">Stanislav Freidin</a>
 * @version $Id: BMPImageSizer.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class BMPImageSizer extends ImageSizer {

    private static final byte BMP_MARKER_1 = 0x42;
    private static final byte BMP_MARKER_2 = 0x4d;
    private static final int HEADER_SIZE = 54;

    protected BMPImageSizer() { super(); }

    /**
     * Read the input stream, determine the size of the image,
     * and return it.
     * WARNING ! Only works for BMP files whose width and height are
     * less than 32767 pixels.
     *
     * @param in The InputStream to read
     * @return The size of the image, or null on failure
     */
    public Dimension computeSize(DataInputStream in) throws IOException {
        byte[] data = new byte[HEADER_SIZE];

        if(in.read(data) < HEADER_SIZE) {
            return null;
        }

        if(BMP_MARKER_1 == data[0] && BMP_MARKER_2 == data[1]) {
            // We have to manually mask out bits since Java has no unsigned primitive
            short width = ImageSizer.toShort(data[19], data[18]);
            short height = ImageSizer.toShort(data[23], data[22]);
            return new Dimension(width, height);
        } else {
            return null;
        }

    }
}
