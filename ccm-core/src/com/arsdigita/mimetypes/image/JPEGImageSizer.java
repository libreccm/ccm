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


import com.arsdigita.mimetypes.util.GlobalizationUtil;
import com.arsdigita.util.Dimension;
import java.io.DataInputStream;
import java.io.IOException;
import org.apache.log4j.Logger;

/**
 * Implements the {@link ImageSizer} interface for JPEG images
 *
 * @author <a href="mailto:sfreidin@arsdigita.com">Stanislav Freidin</a>
 * @author <a href="mailto:karlg@arsdigita.com">Karl Goldstein</a>
 * @version $Id: JPEGImageSizer.java 736 2005-09-01 10:46:05Z sskracic $
 */
public class JPEGImageSizer extends ImageSizer {

    private static Logger s_log =
        Logger.getLogger(JPEGImageSizer.class);

    /*
     * JPEG markers consist of one or more 0xFF bytes, followed by a marker
     * code byte (which is not an FF).  Here are the marker codes of interest
     * in this program.  (See jdmarker.c for a more complete list.)
     **/

    private final static byte M_0FF  = (byte) 0xFF;

    /* Start Of Frame N */
    private final static byte M_SOF0  = (byte) 0xC0;
    /* N indicates which compression process */
    /* Only SOF0-SOF2 are now in common use */
    private final static byte M_SOF1  = (byte) 0xC1;
    private final static byte M_SOF2  = (byte) 0xC2;
    private final static byte M_SOF3  = (byte) 0xC3;
    /* NB: codes C4 and CC are NOT SOF markers */
    private final static byte M_SOF5  = (byte) 0xC5;
    private final static byte M_SOF6  = (byte) 0xC6;
    private final static byte M_SOF7  = (byte) 0xC7;
    private final static byte M_SOF9  = (byte) 0xC9;
    private final static byte M_SOF10 = (byte) 0xCA;
    private final static byte M_SOF11 = (byte) 0xCB;
    private final static byte M_SOF13 = (byte) 0xCD;
    private final static byte M_SOF14 = (byte) 0xCE;
    private final static byte M_SOF15 = (byte) 0xCF;
    /* Start Of Image (beginning of datastream) */
    private final static byte M_SOI   = (byte) 0xD8;
    /* End Of Image (end of datastream) */
    private final static byte M_EOI   = (byte) 0xD9;
    /* Start Of Scan (begins compressed data) */
    private final static byte M_SOS   = (byte) 0xDA;
    /* Application-specific marker, type N */
    private final static byte M_APP0  = (byte) 0xE0;
    /* (we don't bother to list all 16 APPn's) */
    private final static byte M_APP12 = (byte) 0xEC;
    /* COMment */
    private final static byte M_COM   = (byte) 0xFE;

    private short height;
    private short width;

    protected JPEGImageSizer() { super(); }

    /**
     * Read the input stream, determine the size of the image,
     * and return it
     *
     * @param in The InputStream to read
     * @return The size of the image, or null on failure
     */
    public Dimension computeSize(DataInputStream in) throws IOException {
        scanHeader(in);
        return new Dimension(width, height);
    }

    /*
     * Find the next JPEG marker and return its marker code.  We
     * expect at least one FF byte, possibly more if the compressor
     * used FFs to pad the file.  There could also be non-FF garbage
     * between markers.  The treatment of such garbage is unspecified;
     * we choose to skip over it but emit a warning msg.  NB: this
     * routine must not be used after seeing SOS marker, since it will
     * not deal correctly with FF/00 sequences in the compressed image
     * data...
     **/

    private byte nextMarker (DataInputStream in) throws IOException {

        byte c;
        int discardedBytes = 0;

        /* Find 0xFF byte; count and skip any non-FFs. */
        c = in.readByte();
        while (c != M_0FF) {
            discardedBytes++;
            c = in.readByte();
        }

        /* Get marker code byte, swallowing any duplicate FF bytes.  Extra FFs
         * are legal as pad bytes, so don't count them in discarded bytes.
         */
        do {
            c = in.readByte();
        } while (c == M_0FF);

        if (discardedBytes != 0) {
            s_log.warn("Warning: garbage data found in JPEG file");
        }

        return c;
    }


    /*
     * Read the initial marker, which should be SOI.  For a JFIF file,
     * the first two bytes of the file should be literally 0xFF M_SOI.
     * To be more general, we could use next_marker, but if the input
     * file weren't actually JPEG at all, next_marker might read the
     * whole file and then return a misleading error message...
     **/

    private byte firstMarker (DataInputStream in) throws IOException {

        byte c1, c2;

        c1 = in.readByte();
        c2 = in.readByte();
        if (c1 != M_0FF || c2 != M_SOI)
            throw new IOException( (String) GlobalizationUtil.globalize("cms.image.not_a_jpeg_file").localize());

        return c2;
    }


    /*
     * Most types of marker are followed by a variable-length parameter
     * segment.  This routine skips over the parameters for any marker
     * we don't otherwise want to process.  Note that we MUST skip the
     * parameter segment explicitly in order not to be fooled by 0xFF
     * bytes that might appear within the parameter segment; such bytes
     * do NOT introduce new markers.
     **/

    private void skipVariable (DataInputStream in) throws IOException {

        short length;

        /* Get the marker parameter length count */
        length = in.readShort();

        /* Length includes itself, so must be at least 2 */
        if (length < 2)
            throw new IOException( (String) GlobalizationUtil.globalize("cms.image.erroneous_jpeg_marker_length").localize());

        length -= 2;

        /* Skip over the remaining bytes */
        in.skipBytes(length);
    }

    /*
     * Process a COM marker.  We want to print out the marker contents
     * as legible text; we must guard against non-text junk and varying
     * newline representations.
     **/

    private String processComment (DataInputStream in) throws IOException {

        short length;

        int ch;
        int lastch = 0;

        /* Get the marker parameter length count */
        length = in.readShort();

        /* Length includes itself, so must be at least 2 */
        if (length < 2)
            throw new IOException( (String) GlobalizationUtil.globalize("cms.image.erroneous_jpeg_marker_length").localize());

        length -= 2;

        byte[] data = new byte[length];
        in.read(data);

        String comment = new String(data);

        return comment;
    }


    /*
     * Process a SOFn marker.  This code is only needed if you want to
     * know the image dimensions...
     **/

    private void processSOFn (DataInputStream in, byte marker)
        throws IOException {

        short length = in.readShort();  /* usual parameter length count */

        byte data_precision = in.readByte();  // number of bits per sample
        height = in.readShort();
        width = in.readShort();
        byte num_components = in.readByte();  // color components

        String process;

        switch (marker) {

        case M_SOF0:    process = "Baseline";
            break;
        case M_SOF1:    process = "Extended sequential";
            break;
        case M_SOF2:    process = "Progressive";
            break;
        case M_SOF3:    process = "Lossless";
            break;
        case M_SOF5:    process = "Differential sequential";
            break;
        case M_SOF6:    process = "Differential progressive";
            break;
        case M_SOF7:    process = "Differential lossless";
            break;
        case M_SOF9:    process = "Extended sequential, arithmetic coding";
            break;
        case M_SOF10:   process = "Progressive, arithmetic coding";
            break;
        case M_SOF11:   process = "Lossless, arithmetic coding";
            break;
        case M_SOF13:   process = "Differential sequential arithmetic coding";
            break;
        case M_SOF14:   process = "Differential progressive arithmetic coding";
            break;
        case M_SOF15:   process = "Differential lossless, arithmetic coding";
            break;

        default:    process = "Unknown";  break;
        }

        if (length != 8 + num_components * 3)
            s_log.warn("Bogus SOF marker length");

        for (int ci = 0; ci < num_components; ci++) {
            in.readByte();    /* Component ID code */
            in.readByte();    /* H, V sampling factors */
            in.readByte();    /* Quantization table number */
        }
    }


    /*
     * Parse the marker stream until SOS or EOI is seen;
     * display any COM markers.
     * While the companion program wrjpgcom will always insert COM markers before
     * SOFn, other implementations might not, so we scan to SOS before stopping.
     * If we were only interested in the image dimensions, we would stop at SOFn.
     * (Conversely, if we only cared about COM markers, there would be no need
     * for special code to handle SOFn; we could treat it like other markers.)
     **/

    private byte scanHeader (DataInputStream in) throws IOException {

        byte marker;

        /* Expect SOI at start of file */
        if (firstMarker(in) != M_SOI)
            throw new IOException( (String) GlobalizationUtil.globalize("cms.image.expected_soi_marker_first").localize());

        /* Scan miscellaneous markers until we reach SOS. */
        while (true) {

            marker = nextMarker(in);

            switch (marker) {

                /* Note that marker codes 0xC4, 0xC8, 0xCC are not, and must
                 * not be, treated as SOFn.  C4 in particular is actually DHT.  */

            case M_SOF0:
            case M_SOF1:
            case M_SOF2:
            case M_SOF3:
            case M_SOF5:
            case M_SOF6:
            case M_SOF7:
            case M_SOF9:
            case M_SOF10:
            case M_SOF11:
            case M_SOF13:
            case M_SOF14:
            case M_SOF15:
                processSOFn(in, marker);
                break;

            case M_SOS:       /* stop before hitting compressed data */
                return marker;

            case M_EOI:       /* in case it's a tables-only JPEG stream */
                return marker;

            case M_COM:
                skipVariable(in);
                // processComment(in);
                break;

            case M_APP12:
                /* Some digital camera makers put useful textual information into
                 * APP12 markers, so we print those out too when in -verbose mode.
                 */
                skipVariable(in);
                break;

            default:          /* Anything else just gets skipped */
                skipVariable(in);   /* we assume it has a parameter count... */
                break;
            }
        } /* end loop */
    }

}
