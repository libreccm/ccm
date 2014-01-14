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

import java.math.BigDecimal;
import java.security.Principal;

/**
 * A Principal that stores a numeric ID, such as a party ID.
 *
 * @author Sameer Ajmani
 */
public class PartyPrincipal implements Principal {

    private BigDecimal m_id;
    /**
     * Creates a new PartyPrincipal that stores the given ID.
     */
    public PartyPrincipal(BigDecimal id) {
        m_id = id;
    }
    /**
     * Returns the stored ID.
     *
     * @return the stored ID.
     */
    public BigDecimal getID() {
        return m_id;
    }
    /**
     * Returns the ID number as a string.
     *
     * @return the ID number as a string.
     */
    public String getName() {
        return m_id.toString();
    }
    /**
     * Same as <code>getName()</code>.
     *
     * @return <code>getName()</code>.
     **/
    public String toString() {
        return getName();
    }
    /**
     * Returns <code>true</code> if the given object is a
     * <code>PartyPrincipal</code> with the same ID.
     *
     * @return <code>true</code> if the given object is a
     * <code>PartyPrincipal</code> with the same ID.
     */
    public boolean equals(Object that) {
        return (that instanceof PartyPrincipal)
            && this.m_id.equals(((PartyPrincipal)that).m_id);
    }
    /**
     * Returns the <code>hashCode</code> of the stored ID number.
     *
     * @return the <code>hashCode</code> of the stored ID number.
     */
    public int hashCode() {
        return this.m_id.hashCode();
    }
}
