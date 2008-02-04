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
package com.arsdigita.versioning;

// new versioning

/**
 * An enumeration of possible operation types.
 *
 * @see Constants#GENERIC_OPERATION
 * @see Constants#CLOB_OPERATION
 * @see Constants#BLOB_OPERATION
 *
 * @author Vadim Nasardinov (vadimn@redhat.com)
 * @since  2003-05-20
 * @version $Revision: #5 $ $Id: OpType.java 287 2005-02-22 00:29:02Z sskracic $
 **/
final class OpType {
    public final String m_datatype;
    public final Integer m_integerValue;

    OpType(String datatype, int intValue) {
        m_datatype = datatype;
        m_integerValue = new Integer(intValue);
    }

    public String datatype() {
        return m_datatype;
    }

    public Integer integerValue() {
        return m_integerValue;
    }
}
