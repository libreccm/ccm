/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.search.converter;

import java.io.InputStream;
import java.io.IOException;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.pdfbox.pdmodel.PDDocument;

/**
 * This class provides the mechanism to perform a conversion from
 * PDF to standard text
 */
public class PDFConverter extends BaseConverter {
    

    /**
     *  Returns a string array representing all of the files types
     *  used by the given converter
     */
    protected String[] getFileExtensions() {
        String[] extensions = {"pdf"};
        return extensions;
    }

    /**
     *  This takes in a document represented as an InputStream and returns
     *  a text representation of that document.
     */
    public String convertDocument(InputStream stream) throws ConversionException {
        try {
            PDFParser parser = new PDFParser(stream);
            parser.parse();
            PDDocument pdDocument= parser.getPDDocument();
            String strippedText = (new PDFTextStripper()).getText(pdDocument);
            pdDocument.close();
            StringBuffer buf = new StringBuffer();
            byte[] bytes = strippedText.getBytes();
            // TODO: make sure that looking byte to byte is ok...I think
            // that we actually need to look multiple bytes at a time
            // so that we don't loose utf-8 items
            for (int i = 0; i < bytes.length; i++) {
                byte[] byteArray = new byte[1];
                byteArray[0] = bytes[i];
                String ch = new String(byteArray);
                if (!"0".equals(Byte.toString(bytes[i]))) {
                    buf.append(ch);
                }
            }
            //return strippedText;
            return buf.toString();
        } catch (IOException ioe) {
            throw new ConversionException(ioe);
        } catch (Exception ex) {
            throw new ConversionException(ex);
        }
    }

} 
