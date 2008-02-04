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
import java.io.ByteArrayOutputStream;

/**
 * this class exists to ensure that text attachments are also indexed.
 */
public class TextConverter extends BaseConverter {
    
    /**
     *  Returns a string array representing all of the files types
     *  used by the given converter
     */
    protected String[] getFileExtensions() {
        String[] extensions = {"txt", "html", "htm", "text", "c", "cc", "c++", "h", "pl", "java", "sgm", "sgml", "jsp", "xml"};
        return extensions;
    }

    /**
     *  This takes in a document represented as an InputStream and returns
     *  a text representation of that document.
     */
    public String convertDocument(InputStream stream) throws ConversionException {
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int position;
            
            while ((position = stream.read(buf)) != -1) {
                output.write(buf, 0, position);
            }
            return output.toString();
        } catch (Exception ex) {
            throw new ConversionException(ex);
        }
    }

    /** 
     *  This takes in a document represented as a byte[] and returns
     *  a text representation of that document.
     */
    public String convertDocument(byte[] document) {
        return new String(document);
    }
} 
