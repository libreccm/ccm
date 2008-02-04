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

/**
 * Factory for auditing save information.
 *  <p>
 *    
 *  </p>
 *
 * @author Joseph Bank 
 * @version 1.0
 */

public class AuditingSaveFactory {

    // default to using WebAuditingSaveInfo

    private static ThreadLocal s_proto = new ThreadLocal() {
            public Object initialValue() {
                return new WebAuditingSaveInfo();
            }
        };

    /**
     * Sets the prototype object for the factory.
     *
     * @param a prototype AuditingSaveInfo object
     */
    public static void setPrototype(AuditingSaveInfo proto) {
        s_proto.set(proto);
    }

    /**
     * Retrieves a new instance of an AuditingSaveInfo object
     * based on the prototype.
     *
     * @return a new AuditingSaveInfo object.
     */
    public static AuditingSaveInfo newInstance() {
        return ((AuditingSaveInfo) s_proto.get()).newInstance();
    }

}
