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

import javax.security.auth.callback.Callback;

/**
 * Callback to determine whether the current login should last "forever" or
 * just until the end of the current session.
 *
 * @author Sameer Ajmani
 **/
public class LifetimeCallback implements Callback {

    public static final String versionId = "$Id: LifetimeCallback.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";
    private boolean m_forever;

    /**
     * Return <code>true</code> if the current login should last "forever",
     * <code>false</code> otherwise.
     *
     * @return <code>true</code> if the current login should last "forever",
     * <code>false</code> otherwise.
     **/
    public boolean isForever() {
        return m_forever;
    }

    /**
     * Sets "forever" to the given value.
     *
     * @param forever <code>true</code> if the current login should last
     * "forever", <code>false</code> otherwise.
     **/
    public void setForever(boolean forever) {
        m_forever = forever;
    }
}
