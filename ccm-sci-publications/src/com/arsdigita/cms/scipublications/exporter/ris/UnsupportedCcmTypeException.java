/*
 * Copyright (c) 2010 Jens Pelzetter
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
package com.arsdigita.cms.scipublications.exporter.ris;

/**
 * Thrown if an unsupported type is passed to an implementation of the
 * {@link RisConverter} interface.
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class UnsupportedCcmTypeException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new instance of <code>UnsupportedCcmType</code> without detail message.
     */
    public UnsupportedCcmTypeException() {
    }

    /**
     * Constructs an instance of <code>UnsupportedCcmType</code> with the specified detail message.
     * @param msg the detail message.
     */
    public UnsupportedCcmTypeException(final String msg) {
        super(msg);
    }

    public UnsupportedCcmTypeException(final Throwable cause) {
        super(cause);
    }

    public UnsupportedCcmTypeException(final String msg, Throwable cause) {
        super(msg, cause);
    }
}
