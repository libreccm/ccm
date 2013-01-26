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
package com.arsdigita.kernel.security;

import org.apache.log4j.Logger;

/**
 * Logs in a user if the user has a valid authentication cookie.
 *
 * @author Sameer Ajmani
 * @version $Id: CookieLoginModule.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class CookieLoginModule extends UserLoginModule {

    /** Private logger instance for debugging purpose  */
    private static final Logger s_log =
                         Logger.getLogger(CookieLoginModule.class.getName());

    /**
     * Creates a UserLoginModule that uses a CookieManager to manage the
     * credential value.
     */
    public CookieLoginModule() {
        super(new CookieManager());
    }
}
