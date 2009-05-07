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
package com.redhat.persistence.pdl.adapters;

import com.redhat.persistence.PropertyMap;
import com.redhat.persistence.metadata.Adapter;
import com.redhat.persistence.metadata.ObjectType;


/**
 * SimpleAd (SimpleAdapter) is the base clase for the various type specific
 * classes.
 *
 * It implements general management methods (get/set the data type etc). Is is
 * only used by the type specific classes of its package.
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #7 $ $Date: 2004/08/16 $
 **/

abstract class SimpleAdapter extends Adapter {

    public final static String versionId = 
            "$Id: SimpleAdapter.java 735 2005-09-01 06:42:59Z sskracic $" +
            "$DateTime: 2004/08/16 18:10:38 $";

    private String m_type;
    private int m_defaultJDBCType;

    protected SimpleAdapter(String type, int defaultJDBCType) {
        if (type == null) { throw new IllegalArgumentException(); }
        m_type = type;
        m_defaultJDBCType = defaultJDBCType;
    }

    public PropertyMap getProperties(Object obj) {
        return new PropertyMap(getObjectType(obj));
    }

    public ObjectType getObjectType(Object obj) {
        return getRoot().getObjectType(m_type);
    }

    public int defaultJDBCType() { return m_defaultJDBCType; }

    public boolean isMutation(Object value, int jdbcType) {
        return false;
    }

    public boolean isBindable() {
        return true;
    }

}
