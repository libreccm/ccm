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
package com.arsdigita.auditing;

import com.arsdigita.kernel.User;
import java.util.Date;

/**
 * Interface required for objects to supply auditing save information.
 *
 *  <p>
 *    
 *  </p>
 *
 * @author Joseph Bank 
 * @version 1.0
 */

public interface AuditingSaveInfo {

    /**
     * Gets the user who is doing the save operation from the current
     * execution environment.  May return null.
     * @return the user.
     */
    public User getSaveUser();

    /**
     * Gets the date for the save operation.
     * @return the date.
     */
    public Date getSaveDate();

    /**
     * Gets the IP address for the save operation from the current
     * execution environment.  May return null.
     * @return the IP address.
     */
    public String getSaveIP();

    /**
     * Supports the prototype factory interface.
     */
    public AuditingSaveInfo newInstance();
}
