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
package com.redhat.persistence.metadata;

/**
 * Role
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #5 $ $Date: 2004/08/16 $
 **/

public class Role extends Property {

    

    private ObjectType m_type;
    private Role m_reverse;
    private boolean m_isComponent;
    private boolean m_isCollection;
    private boolean m_isNullable;

    public Role(String name, ObjectType type, boolean isComponent,
                boolean isCollection, boolean isNullable) {
        super(name);
        m_type = type;
        m_isComponent = isComponent;
        m_isCollection = isCollection;
        m_isNullable = isNullable;
    }

    public ObjectType getType() {
        return m_type;
    }

    public boolean isComponent() {
        return m_isComponent;
    }

    public boolean isComposite() {
        return m_reverse != null && m_reverse.isComponent();
    }

    public boolean isNullable() {
        return m_isNullable;
    }

    public void setNullable(boolean value) {
        m_isNullable = value;
    }

    public boolean isCollection() {
        return m_isCollection;
    }

    public boolean isReversable() {
        return m_reverse != null;
    }

    public Role getReverse() {
        return m_reverse;
    }

    public void setReverse(Role reverse) {
        if (reverse == null) {
            throw new IllegalArgumentException
                ("reverse is null");
        }
        if (reverse.m_reverse != null) {
            throw new IllegalArgumentException
                ("Role is associated with another property already: " +
                 reverse);
        }

        this.m_reverse = reverse;
        reverse.m_reverse = this;
    }

    public void dispatch(Switch sw) {
        sw.onRole(this);
    }

}
