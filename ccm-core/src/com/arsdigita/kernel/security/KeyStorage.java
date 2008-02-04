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
package com.arsdigita.kernel.security;

/**
 * This interface is an implementation detail that you most likely don't want to
 * use or rely on.
 *
 * @author  Vadim Nasardinov (vadimn@redhat.com)
 * @since   2003-12-18
 * @version $Revision: #5 $ $DateTime: 2004/08/16 18:10:38 $
 **/
public interface KeyStorage {
    /**
     * Should only be used by the core loader.
     **/
    KeyStorage KERNEL_KEY_STORE = Store.INSTANCE;

    /**
     * Initializes the keystore.
     **/
    void init();
}
