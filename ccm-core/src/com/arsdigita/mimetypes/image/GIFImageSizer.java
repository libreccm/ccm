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
import java.io.IOException;

/**
 * Implements the {@link ImageSizer} interface for GIF images
 *
 * @author <a href="mailto:sfreidin@arsdigita.com">Stanislav Freidin</a>
 * @version $Id: GIFImageSizer.java 736 2005-09-01 10:46:05Z sskracic $
 */
public class GIFImageSizer extends ImageSizer {

    public static final String versionId = "$Id: GIFImageSizer.java 736 2005-09-01 10:46:05Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private static final String GIF_MARKER_87 = "GIF87a";
    private static final String GIF_MARKER_89 = "GIF89a";

    protected GIFImageSizer() { super(); }

    /**
     * Read the input stream, determine the size of the image,
     * and return it
     *
     * @param in The InputStream to read
     * @return The size of the image, or null on failure
     */
    public Dimension computeSize(DataInputStream in) throws IOException {
        byte[] data = new byte[10];

        if(in.read(data) < 10) {
            return null;
        }

        String head = new String(data, 0, 6);

        if(GIF_MARKER_87.equals(head) || GIF_MARKER_89.equals(head)) {

            // We have to manually mask out bits since Java has no unsigned primitive
            short width = ImageSizer.toShort(data[7], data[6]);
            short height = ImageSizer.toShort(data[9], data[8]);
            return new Dimension(width, height);
        } else {
            return null;
        }
    }

}
