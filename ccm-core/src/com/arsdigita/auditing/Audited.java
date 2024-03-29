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
 * Interface for Audited objects.
 *  <p>
 *    
 *  </p>
 *
 * @author Joseph Bank
 * @version 1.0
 * @version $Id: Audited.java 2089 2010-04-17 07:55:43Z pboy $
 **/
public interface Audited {

    /**
     * Gets the user who created the object. May be null.
     * @return the user.
     */
    public User getCreationUser();

    /**
     * Gets the creation date of the object.
     * @return the object's creation date.
     */
    public Date getCreationDate();

    /**
     * Gets the creation IP address. May be null.
     * @return the creation IP address.

    */
    public String getCreationIP();

    /**
     * Gets the user who last modified the object. May be null.
     * @return the user who last modified the object.
     */
    public User getLastModifiedUser();

    /**
     * Gets the last modified date.
     * @return the date that the object was last modified.
     */
    public Date getLastModifiedDate();

    /**
     * Gets the last modified IP address. May be null.
     */
    public String getLastModifiedIP();
}
