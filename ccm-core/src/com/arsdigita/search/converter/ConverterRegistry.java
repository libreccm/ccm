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

import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;
import java.util.Iterator;
import org.apache.log4j.Logger;
import com.arsdigita.mimetypes.MimeType;


/**
 *  This is a location for Converters to register with mime types.
 *  This can then be used at run time to take a file and get a String
 *  representation of the contents of that document.
 */
public class ConverterRegistry {

    private static Logger s_log = Logger.getLogger(ConverterRegistry.class);

    private static Map s_converters = new HashMap();

    /**
     *  Initializers should register their converters here.  If two converters
     *  with the same MimeType are registered then the second converter
     *  will be returned with the call to getConverter.  You can clear
     *  a converter for a mime type by passing in a null converter with
     *  with non-null mimetype array.
     *
     *  @pre mimeTypes != null
     */
    public static void registerConverter(Converter converter, 
                                         MimeType[] mimeTypes) {
        Iterator iterator = Arrays.asList(mimeTypes).iterator();
        while (iterator.hasNext()) {
            s_converters.put((MimeType)iterator.next(), converter);
        }
    }


    /**
     *  This will return the appropriate converter, if one has been
     *  registered.  If there is no registered converter then this
     *  will return null.
     */
    public static Converter getConverter(MimeType mimeType) {
        return (Converter)s_converters.get(mimeType);
    }
} 
