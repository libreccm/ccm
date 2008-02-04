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
package com.arsdigita.cms.dispatcher;



/**
 * <tt>AccessDeniedException</tt> is the runtime exception that is thrown
 * whenever the current user does not have access to the requested resources.
 *
 * @author Michael Pih (pihman@arsdigita.com)
 * @version $Revision: #7 $ $DateTime: 2004/08/17 23:15:09 $
 * @deprecated use com.arsdigita.dispatcher.AccessDeniedException instead.
 */
public class AccessDeniedException extends com.arsdigita.dispatcher.AccessDeniedException {

    /**
     * Constructs an AccessDeniedException with the default detail message.
     */
    public AccessDeniedException() {
        super();
    }

    /**
     * Constructs an AccessDeniedException with the specified detail message.
     *
     * @param msg The error detail message
     */
    public AccessDeniedException(String msg) {
        super(msg);
    }
}
