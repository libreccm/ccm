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

import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.util.Assert;

import java.util.HashMap;
import java.util.Map;

// new versioning

/**
 * This class represents a node in the versioning dependence graph.
 *
 * @author Vadim Nasardinov (vadimn@redhat.com)
 * @since 2003-03-03
 * @version $Revision: #8 $ $DateTime: 2004/08/16 18:10:38 $
 **/
final class GraphNode {
    private static final Map s_instances = new HashMap();

    private ObjectType m_objType;
    private NodeType m_nodeType;

    private GraphNode(ObjectType objType) {
        m_objType = objType;
        m_nodeType = NodeType.UNREACHABLE;
    }

    /**
     * Returns the singleton instance of graph node for this
     * <code>objType</code>.
     **/
    public static synchronized GraphNode getInstance(ObjectType objType) {
        Assert.exists(objType, ObjectType.class);
        GraphNode node = (GraphNode) s_instances.get(objType.getQualifiedName());
        if (node == null) {
            node = new GraphNode(objType);
            s_instances.put(objType.getQualifiedName(), node);
        }
        return node;
    }

    public ObjectType getObjectType() {
        return m_objType;
    }

    public boolean equals(Object obj) {
        return this == obj;
    }

    public int hashCode() {
        return m_objType.getQualifiedName().hashCode();
    }

    public void setNodeType(NodeType nodeType) {
        m_nodeType = nodeType;
    }

    /**
     * @see #isVersionedType()
     * @see #isCoversionedType()
     * @see #isRecoverable()
     * @see #isUnreachable()
     **/
    public NodeType getNodeType() {
        return m_nodeType;
    }

    /**
     * A convenience wrapper around {@link #getNodeType}.
     **/
    boolean isVersionedType() {
        return getNodeType() == NodeType.VERSIONED_TYPE;
    }

    /**
     * A convenience wrapper around {@link #getNodeType}.
     **/
    boolean isCoversionedType() {
        return getNodeType() == NodeType.COVERSIONED_TYPE;
    }

    /**
     * A convenience wrapper around {@link #getNodeType}.
     **/
    public boolean isRecoverable() {
        return getNodeType() == NodeType.RECOVERABLE;
    }

    /**
     * A convenience wrapper around {@link #getNodeType}.
     **/
    public boolean isUnreachable() {
        return getNodeType() == NodeType.UNREACHABLE;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(getObjectType().getQualifiedName());
        sb.append(": ").append(getNodeType());
        return sb.toString();
    }
}
