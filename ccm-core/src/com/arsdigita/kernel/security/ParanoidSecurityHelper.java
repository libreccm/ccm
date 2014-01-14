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

import javax.servlet.http.HttpServletRequest;

/**
 *  Implementation of the
 * SecurityHelper interface that always returns <code>true</code> for
 * <code>isSecure</code>.  This implementation is not safe, since it may
 * cause the system to send sensitive data over an insecure connection.
 * This class is used for simulating secure conditions for testing.
 *
 * @author Sameer Ajmani
 * @see SecurityHelper
 */
public class  ParanoidSecurityHelper extends DefaultSecurityHelper {

    /**
     * Always returns true (to simulate secure conditions).
     *
     * @return <code>true</code>.
     */
    public boolean isSecure(HttpServletRequest req) {
        return true;
    }
}
