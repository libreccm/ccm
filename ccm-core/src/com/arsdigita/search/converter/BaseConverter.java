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

import com.arsdigita.mimetypes.MimeType;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.ByteArrayInputStream;

/**
 * This class provides the mechanism to perform a conversion from
 * Excel to standard text
 */
public abstract class BaseConverter implements Converter {
    
    private MimeType[] s_types = null;


    /** 
     *  This takes in a document represented as a byte[] and returns
     *  a text representation of that document.
     */
    public String convertDocument(byte[] document) {
        return convertDocument(new ByteArrayInputStream(document));
    }

    /**
     *  This takes the document and returns a String containing the contents
     *  of the document.
     */
    public String convertDocument(File document) throws FileNotFoundException {
        return convertDocument(new FileInputStream(document));
    }

    /**
     *  Returns a string array representing all of the files types
     *  used by the given converter
     */
    protected abstract String[] getFileExtensions();

    /**
     *  This returns an array of all MimeTypes that this converter is able
     *  to convert to a String.
     */
    public MimeType[] getMimeTypes() {
        if (s_types == null) {
            String[] extensions = getFileExtensions();
            s_types = new MimeType[extensions.length];
            for (int i = 0; i < extensions.length; i++) {
                s_types[i] = MimeType.guessMimeType(extensions[i]);
            }
        }
        return s_types;
    }
} 
