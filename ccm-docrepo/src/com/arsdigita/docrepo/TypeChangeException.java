/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.docrepo;

/**
 * Exception thrown to indicate that a resource name (or path) contains
 * invalid characters.
 *
 * @version $Id: TypeChangeException.java  pboy $
 */

public class TypeChangeException extends ResourceException {

    /**
     * Creates a new exception with a given error message.
     * @param message the error message
     */
    public TypeChangeException(String message) {
        super(message);
    }

    public TypeChangeException(String oldType, String newType) {
        super(makeErrorMsg(oldType, newType));
    }

    private static String makeErrorMsg(String oldType, String newType) {
        String msg = "Attempt to change mime type of file from " +
                oldType + " to " + newType;
        return msg;
    }


}
