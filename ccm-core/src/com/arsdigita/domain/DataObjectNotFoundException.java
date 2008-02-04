/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.domain;

/**
 * An exception thrown when the data object backing a domain object
 * cannot be found.
 *
 * @author Oumi Mehrota
 */
public class DataObjectNotFoundException extends RuntimeException {
    public static final String versionId =
        "$Id: DataObjectNotFoundException.java 287 2005-02-22 00:29:02Z sskracic $" +
        "$Author: sskracic $" +
        "$DateTime: 2004/08/16 18:10:38 $";

    public DataObjectNotFoundException(final String message) {
        super(message);
    }

    public DataObjectNotFoundException() {
        super();
    }

    /**
     * @deprecated with no replacement
     */
    public DataObjectNotFoundException(final Exception e) {
        this(e.getMessage());
    }
}
