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
package com.arsdigita.dispatcher;

/**
 * <code>ObjectNotFoundException</code> is the runtime exception that
 * is thrown whenever an object we depend on to execute a workflow
 * suddenly disappears.  This may be because another user deleted it,
 * for instance.
 *
 * @author Justin Ross (jross@redhat.com)
 * @version $Id: ObjectNotFoundException.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class ObjectNotFoundException extends RuntimeException {

    // The default error detail message.
    private final static String ERROR_MSG = "The object requested cannot be found";

    public ObjectNotFoundException() {
        this(ERROR_MSG);
    }

    /**
     * Constructs an ObjectNotFoundException with the specified detail message.
     *
     * @param msg The error detail message
     */
    public ObjectNotFoundException(String msg) {
        super(msg);
    }
}
