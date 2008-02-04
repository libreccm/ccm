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

import com.arsdigita.util.Assert;

// new versioning

/**
 * Type-safe enum providing constants for marking four types of nodes in the
 * versioning dependence graph.
 *
 * @author Vadim Nasardinov (vadimn@redhat.com)
 * @since 2003-03-03
 * @version $Revision: #6 $ $DateTime: 2004/08/16 18:10:38 $
 **/
final class NodeType {
    private String m_type;
    private int m_order;

    /**
     * Labels object types whose been PDL definition has been marked "versioned"
     * or subtypes of such object types.
     **/
    public final static NodeType VERSIONED_TYPE   =
        new NodeType("versioned type", 3);

    /**
     * Labels an object type that is not explicitly marked versioned (and whose
     * supertype is not explicitly marked versioned), but that is nonetheless
     * fully versioned, because it is a component of a versioned type.
     **/
    public final static NodeType COVERSIONED_TYPE =
        new NodeType("coversioned type", 2);

    /**
     * Labels an object type that is not fully versioned, but for which we
     * record enough information for undeleting a deleted instance.
     **/
    public final static NodeType RECOVERABLE      =
        new NodeType("recoverable", 1);

    /**
     * Labels an object type that is ignored by the versioning system. (It is
     * called unreachable, because it can be reached from versioned and/or
     * recoverable nodes in the versioning graph.)
     **/
    public final static NodeType UNREACHABLE      =
        new NodeType("unreachable", 0);

    private NodeType(String type, int order) {
        m_type = type;
        m_order = order;
    }

    public String toString() {
        return m_type;
    }

    /**
     * Defines a complete order over this enum.
     *
     * Ascending enumeration: {@link #UNREACHABLE}, {@link #RECOVERABLE}, {@link
     * #COVERSIONED_TYPE}, {@link #VERSIONED_TYPE}.
     **/
    public boolean lessThan(NodeType type) {
        Assert.exists(type, NodeType.class);
        return m_order < type.m_order;
    }
}
