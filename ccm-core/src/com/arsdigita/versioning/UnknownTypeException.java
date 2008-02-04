/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.versioning;

// new versioning

/**
 * This is an unchecked exception.
 *
 * @author Vadim Nasardinov (vadimn@redhat.com)
 * @since 2003-04-11
 * @version $Revision: #6 $ $DateTime: 2004/08/16 18:10:38 $
 **/
class UnknownTypeException extends VersioningException {

    public UnknownTypeException(String msg) {
        super(msg);
    }
}
