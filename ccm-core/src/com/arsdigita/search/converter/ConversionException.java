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

import com.arsdigita.util.UncheckedWrapperException;

/**
 * An exception to indicate that a problem occurred while trying to 
 * convert the contents of the document to text.
 */
public class ConversionException extends UncheckedWrapperException {

    public static final String versionId = "$Id: ConversionException.java 738 2005-09-01 12:36:52Z sskracic $";


    /**
     * Constructor which only takes a msg, which will cause this
     * ConversionException to behave like a normal RuntimeException.
     * While it doesn't seem to make a lot of sense to have a wrapper
     * exception that doesn't wrap anything, this is needed so that it
     * can be used as a direct replacement for RuntimeException.
     */
    public ConversionException (String msg) {
        super(msg);
    }

    /**
     * Constructor which takes a root cause
     * that this exception will be wrapping.
     */
    public ConversionException (Throwable rootCause) {
        super(rootCause);
    }

    /**
     * Constructor which takes a message string and a root cause
     * that this exception will be wrapping.  The message string
     * should be something different than rootCause.getMessage()
     * would normally provide.
     */
    public ConversionException (String s, Throwable rootCause) {
        super(s, rootCause);
    }

}
