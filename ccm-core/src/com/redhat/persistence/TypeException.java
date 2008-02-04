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
package com.redhat.persistence;

import com.redhat.persistence.metadata.ObjectType;

/**
 * TypeException
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #5 $ $Date: 2004/08/16 $
 **/

public class TypeException extends ProtoException {

    private Role m_role;
    private ObjectType m_expected;
    private ObjectType m_actual;
    private Object m_obj;

    TypeException(Role role, ObjectType expected, ObjectType actual,
                  Object obj) {
	m_role = role;
        m_expected = expected;
        m_actual = actual;
        m_obj = obj;
    }

    public Role getRole() {
	return m_role;
    }

    public String getMessage() {
        return m_role + " (" + m_obj + ") is of type " + m_actual
            + " instead of " + m_expected;
    }

}
