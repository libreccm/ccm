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
import java.io.InputStream;

/**
 *  This interface should be implemented by classes that are able to 
 *  take a document and returns the a String representation of the
 *  document's contents.
 */
public interface Converter {
    
    /**
     *  This takes in a document represented as a byte[] and returns
     *  a text representation of that document.
     */
    String convertDocument(InputStream stream) throws ConversionException;

    /**
     *  This takes the document and returns a String containing the contents
     *  of the document.
     */
    String convertDocument(File document) throws FileNotFoundException;

    /** 
     *  This takes in a document represented as a byte[] and returns
     *  a text representation of that document.
     */
    public String convertDocument(byte[] document);

    /**
     *  This returns an array of all MimeTypes that this converter is able
     *  to convert to a String.
     */
    MimeType[] getMimeTypes();
} 
