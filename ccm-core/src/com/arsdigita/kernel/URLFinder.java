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
package com.arsdigita.kernel;

import com.arsdigita.persistence.OID;

/**
 * 
 * This interface is used by the URLService to delegate the
 * job of finding a URL for a domain object to custom code.
 *
 * <p>
 * If a developer adds a new data object type and wants
 * the URLService to support it, the
 * developer must register a URLFinder for the new type
 * using URLService.registerFinder().
 * <P>
 * The GenericURLFinder may be
 * used for many object types.  If the behavior of GenericURLFinder
 * is not applicable to some data object type, the developer
 * should write a new URLFinder that uses application-specific logic.
 *
 * @author Oumi Mehrotra 
 **/
public interface URLFinder {

    public static final String versionId = "$Id: URLFinder.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    /**
     * Returns a URL path to a page that displays the object identified by
     * the given OID. The URL path is relative to the server root.
     * Only called from URLService.locate(OID).
     *
     * @throws NoValidURLException when no valid URL could be obtained
     * for the given OID.
     *
     * @see URLService#locate(OID)
     **/
    public String find(OID oid) throws NoValidURLException;
    public String find(OID oid, String context) throws NoValidURLException;

}
