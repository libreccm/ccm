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

import com.arsdigita.persistence.metadata.Property;
import com.arsdigita.util.Assert;


// new versioning

/**
 * Used for labeling edges in the versioning dependence graph.
 *
 * @author Vadim Nasardinov (vadimn@redhat.com)
 * @since 2003-05-08
 * @version $Revision: #5 $ $Date: 2004/08/16 $
 **/
final class EdgeLabel {
    private Property m_property;

    private final static int VERSIONED = 0;
    private final static int UNVERSIONED = 1;
    private final static int UNMARKED = -1;

    private int m_state;

    /**
     * @pre prop != null
     **/
    public EdgeLabel(Property prop) {
        Assert.exists(prop, Property.class);
        m_property = prop;
        m_state = UNMARKED;
    }

    /**
     * @post return != null
     **/
    public Property getProperty() {
        return m_property;
    }


    /**
     * @post isVersioned()
     **/
    public void setVersioned() {
        Assert.isTrue(m_state == UNMARKED, "is unmarked");
        m_state = VERSIONED;
    }

    public boolean isVersioned() {
        return m_state == VERSIONED;
    }

    /**
     * @post isUnversioned()
     **/
    public void setUnversioned() {
        Assert.isTrue(m_state == UNMARKED, "is unmarked");
        m_state = UNVERSIONED;
    }

    /**
     * Returns <code>false</code>, unless {@link #setUnversioned()} has been
     * previously called on this edge label.
     **/
    public boolean isUnversioned() {
        return m_state == UNVERSIONED;
    }

    public boolean equals(Object obj) {
        if ( obj == null ) return false;
        EdgeLabel that = (EdgeLabel) obj;
        return m_property.equals(that.m_property);
    }

    public int hashCode() {
        return m_property.hashCode();
    }
}
