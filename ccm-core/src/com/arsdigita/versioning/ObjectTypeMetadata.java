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

import com.arsdigita.persistence.metadata.MetadataRoot;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.persistence.metadata.Property;
import com.arsdigita.util.Assert;
import com.arsdigita.util.AssertionError;
import com.arsdigita.util.Graph;
import com.arsdigita.util.GraphEdge;
import com.arsdigita.util.GraphSet;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

// new versioning

/**
 * Versioning metadata.
 *
 * <p>If <code>ot</code> is an {@link ObjectType object type}, then the
 * following exclusive OR is always true and </p>
 *
 * <pre>
 * ObjectTypeMetadata m = ObjectTypeMetadata.getInstance();
 * xor(m.isFullyVersioned(ot), m.isRecoverable(ot), m.isUnreachable(ot));
 * </pre>
 *
 * <p>In practical terms, you only need two of these four methods.  If an object
 * type is "unreachable", we don't record any versioning information for
 * instances of this type.  If an object type is marked as "recoverable", we
 * record enough information to undelete a deleted instance of this type.  If an
 * object type is marked neither "unreachable", nor "recoverable", we fully
 * version instance of this type.  That is to say, we record enough information
 * to roll any instance of this type back to any previous point in its
 * history.</p>
 *
 * @author Vadim Nasardinov (vadimn@redhat.com)
 * @since 2003-02-19
 * @version $Revision: #12 $ $Date: 2004/08/16 $
 */
final class ObjectTypeMetadata {
    private final static Logger s_log =
        Logger.getLogger(ObjectTypeMetadata.class);

    private Graph m_dependenceGraph;
    private Graph m_inheritanceForest;
    private Set m_unversionedProperties;
    private Set m_versionedProperties;

    private final static ObjectTypeMetadata s_singleton =
        new ObjectTypeMetadata();

    private ObjectTypeMetadata() {
        m_dependenceGraph = new GraphSet();
        m_unversionedProperties = new HashSet();
        m_versionedProperties = new HashSet();
    }

    /**
     * Returns the singleton instance of this class.
     **/
    public static ObjectTypeMetadata getInstance() {
        return s_singleton;
    }

    /**
     * Returns <code>true</code> if <code>objType</code> is versioned.
     **/
    boolean isVersionedType(ObjectType objType) {
        return GraphNode.getInstance(objType).isVersionedType();
    }

    /**
     * @see #isVersionedType(ObjectType)
     * @param objTypeFQN the fully qualified name of an object type
     **/
    boolean isVersionedType(String objTypeFQN) {
        return isVersionedType(objType(objTypeFQN));
    }

    boolean isUnversionedProperty(Property prop) {
        return m_unversionedProperties.contains(prop);
    }

    boolean isUnversionedProperty(String containerName, String propertyName) {
        ObjectType objType = objType(containerName);
        return isUnversionedProperty(objType.getProperty(propertyName));
    }

    boolean isVersionedProperty(Property prop) {
        return m_versionedProperties.contains(prop);
    }

    boolean isVersionedProperty(String containerName, String propertyName) {
        ObjectType objType = objType(containerName);
        return isVersionedProperty(objType.getProperty(propertyName));
    }

    /**
     * Returns <code>true</code> if it can be proved, by analyzing the PDL
     * model, that there is no need to make object either fully versioned, or
     * recoverable.
     **/
    public boolean isUnreachable(ObjectType objType) {
        return GraphNode.getInstance(objType).isUnreachable();
    }

    /**
     * @see #isUnreachable(ObjectType)
     **/
    public boolean isUnreachable(String objTypeFQN) {
        return isUnreachable(objType(objTypeFQN));
    }

    /**
     * Returns <code>true</code> the object type is versioned because it is a
     * component of a versioned object type or if it is a property that is
     * explicitly marked "versioned" in the PDL definition.
     *
     * <p>In practical terms, there is no real difference between "versioned
     * type" and "versioned property". The only reason we provide API for making
     * the distinction is for generating viewable dependency graphs.  These
     * graphs can be then checked manually as to why a particular object type is
     * treated by the versioning system as fully versioned. </p>
     *
     * <p>The method is package-scoped to make it package testable.</p>
     **/
    boolean isCoversionedType(ObjectType objType) {
        return GraphNode.getInstance(objType).isCoversionedType();
    }

    /**
     * @see #isCoversionedType(ObjectType)
     **/
    boolean isCoversionedType(String objTypeFQN) {
        return isCoversionedType(objType(objTypeFQN));
    }

    /**
     * Returns <code>true</code> if this object type is recoverable.
     *
     * <p>A recoverable object type is one whose instance can be undeleted. We
     * don't record the full versioning history for such object types.  We only
     * record enough information to undelete a data object whose object type is
     * marked as "recoverable".</p>
     **/
    public boolean isRecoverable(ObjectType objType) {
        return GraphNode.getInstance(objType).isRecoverable();
    }

    /**
     * @see #isRecoverable(ObjectType)
     **/
    public boolean isRecoverable(String objTypeFQN) {
        return isRecoverable(objType(objTypeFQN));
    }


    /**
     * Returns true if this object type is {@link NodeType#VERSIONED_TYPE
     * versioned type} or {@link NodeType#COVERSIONED_TYPE}.
     **/
    public boolean isFullyVersioned(ObjectType objType) {
        GraphNode node = GraphNode.getInstance(objType);
        return node.isVersionedType() || node.isCoversionedType();
    }

    /**
     * @see #isFullyVersioned(ObjectType)
     **/
    public boolean isFullyVersioned(String objTypeFQN) {
        return isFullyVersioned(objType(objTypeFQN));
    }

    private static ObjectType objType(String fqn) {
        Assert.exists(fqn, String.class);
        return MetadataRoot.getMetadataRoot().getObjectType(fqn);
    }

    private void resetObjectTypeInheritanceForest() {
        Iterator objTypes = MetadataRoot.getMetadataRoot().
            getObjectTypes().iterator();
        m_inheritanceForest = new GraphSet();

        while (objTypes.hasNext()) {
            ObjectType objType = (ObjectType) objTypes.next();
            m_inheritanceForest.addNode(objType);
            if ( objType.getSupertype() != null ) {
                m_inheritanceForest.addEdge
                    (objType.getSupertype(), objType, null);
            }
        }
    }

    /**
     * This method is called every time a new object type node of the PDL AST is
     * traversed by the PDL loader.  This method updates the data structure in
     * which we capture our knowledge of what object types should be fully
     * versioned and which should be recoverable.
     *
     * The data structure is a graph whose nodes are object types.  There are
     * the following kinds of links between nodes in this graph.
     *
     * 1. If object type Y extends object type X, then we maintain a link
     *    directed from X to Y.
     *
     * 2. If object type Z has a required attribute x of type X, we maintain
     *    a link from Z to X.
     *
     * 3. If object type Z has a component y of type Y, then we maintain a link
     *    from Z to Y.
     *
     * 4. If object type Z has a "versioned" property of type Q, then we
     *    maintain a link from Z to Q.
     *
     * 5. If object type Z has an "unversioned" property of type R, then we
     *    maintain a maintain a link from Z to R.
     *
     * Each node is labeled with the information indicating to what degree it
     * should be versioned. A node can be labeled as "versioned type",
     * "versioned property", "recoverable", and "unreachable". Unreachable
     * basically means "none of the other three."  Nodes are labeled in a way
     * that must satisfy a number of constraints, e.g.
     *
     * a. If a node is marked "versioned" in the PDL metadata, then it is
     *    labeled "versioned type" in our versioning dependence graph.
     *
     * b. The degree to which a give node is versioned affects the degree to
     *    which its neighboring nodes are versioned. For example, if a node is
     *    marked as "versioned type", then its subtypes are also marked as
     *   "versioned type".
     *
     * Whenever a new type node is added, our view of what nodes types should be
     * versioned may potentially change.  The graph needs to be traversed and
     * new labels may need to be assigned to nodes. The current algorithm
     * basically consists in searching for the least fixed point of a monotonic
     * functional.  In recomputing the labels, we can only increase the degree
     * to which this object type is versioned. If we "upgrade" a node to a
     * higher degree of versionability, we must recursively reexamine its
     * neighboring nodes to see if their labels must also be changed to satisfy
     * the constraints (partially) described above. Since we have a finite
     * number of nodes and a finite number of degrees of versionability, the sum
     * total of degrees of versionability increases with each iteration and is
     * bounded. The recursive traversal will therefore reach the least fixed
     * point.
     *
     * To convince ourselves that this algorithm produces the same result
     * deterministically regardless of the order in which the PDL AST is
     * traversed, we must prove that there is only one such fixed point.
     *
     * FIXME: prove uniqueness of the fixed point.
     *
     * Further notes: Nodes are labeled with one of the four possible instances
     * of the NodeType enum. The enum has a total ordering imposed on it as
     * follows:
     *
     * NodeType.VERSIONED_TYPE > NodeType.VERSIONED_PROPERTY >
     * NodeType.RECOVERABLE > NodeType.UNREACHABLE
     *
     * If we assign, quite arbitrarily, integer values to each of the four node
     * types as follows:
     *
     *    VERSIONED_TYPE       3
     *    VERSIONED_PROPERTY   2
     *    RECOVERABLE          1
     *    UNREACHABLE          0
     *
     * then the monotonic functional can be defined formally as the sum total of
     * node types of all of the nodes in the current dependence graph.
     **/
    synchronized void addGraphNode(GraphNode node, boolean isMarked) {
        m_dependenceGraph.addNode(node);

        if ( isMarked ) {
            node.setNodeType(NodeType.VERSIONED_TYPE);
        }
    }

    private Iterator nodes() {
        return m_dependenceGraph.getNodes().iterator();
    }

    private Graph.Edge getPropertyEdge(GraphNode tail, Property prop) {
        Iterator edges = m_dependenceGraph.getOutgoingEdges(tail).iterator();
        while ( edges.hasNext() ) {
            Graph.Edge edge = (Graph.Edge) edges.next();
            EdgeLabel label = (EdgeLabel) edge.getLabel();
            if ( label != null &&
                 prop.getName().equals(label.getProperty().getName()) ) {

                return edge;
            }
        }
        return null;
    }

    synchronized void markEdgeUnversioned(Property prop) {
        Assert.exists(prop, Property.class);
        m_unversionedProperties.add(prop);

        if ( prop.getType().isSimple() ) {
            return;
        }

        GraphNode node = GraphNode.getInstance((ObjectType) prop.getContainer());
        Assert.isTrue(m_dependenceGraph.hasNode(node),
                     "dependence graph has " + prop.getContainer());

        Graph.Edge edge = getPropertyEdge(node, prop);
        if ( edge == null ) {
            edge = propertyEdge(node, prop);
            m_dependenceGraph.addEdge(edge);
        }

        EdgeLabel edgeLabel = (EdgeLabel) edge.getLabel();
        edgeLabel.setUnversioned();
    }

    synchronized void addVersionedProperty(Property prop) {
        Assert.exists(prop, Property.class);
        m_versionedProperties.add(prop);
        Assert.isFalse(prop.getType().isSimple(), "property is simple: " + prop);

        GraphNode tail = GraphNode.getInstance((ObjectType) prop.getContainer());

        Graph.Edge edge = null;
        if ( m_dependenceGraph.hasNode(tail) ) {
            edge = getPropertyEdge(tail, prop);
        } else {
            edge = propertyEdge(tail, prop);
            m_dependenceGraph.addEdge(edge);
        }

        if ( edge == null ) {
            edge = propertyEdge(tail, prop);
            m_dependenceGraph.addEdge(edge);
        }

        EdgeLabel label = (EdgeLabel) edge.getLabel();
        label.setVersioned();
        GraphNode head = (GraphNode) edge.getHead();

        if ( head.getNodeType().lessThan(NodeType.COVERSIONED_TYPE) ) {
            head.setNodeType(NodeType.COVERSIONED_TYPE);
        }
    }

    private void propagateFromMarked() {
        Iterator nodes = nodes();
        while ( nodes.hasNext() ) {
            GraphNode node = (GraphNode) nodes.next();

            if ( node.isVersionedType() ) {
                propagateNodeType(node);
            }
        }
    }

    // mutually recursive with promoteSubtypes and promotePropertyContainerTypes
    private synchronized void propagateNodeType(GraphNode node) {
        promoteSubtypes(node);
        promotePropertyContainerTypes(node);
    }

    private void promoteSubtypes(GraphNode node) {
        Iterator childEdges = m_inheritanceForest.getOutgoingEdges
            (node.getObjectType()).iterator();

        while (childEdges.hasNext()) {
            Graph.Edge edge = (Graph.Edge) childEdges.next();
            GraphNode childNode = GraphNode.getInstance
                ((ObjectType) edge.getHead());
            m_dependenceGraph.addEdge(subtypeEdge(node, childNode));

            if ( childNode.getNodeType().lessThan(node.getNodeType()) ) {
                childNode.setNodeType(node.getNodeType());
                propagateNodeType(childNode);
            }
        }
    }

    private void promotePropertyContainerTypes(GraphNode node) {
        Iterator props = versionableCompoundProperties(node.getObjectType());
        while (props.hasNext()) {
            Property prop = (Property) props.next();
            if ( !(prop.getType() instanceof ObjectType) ) {
                continue;
            }
            ObjectType propType = (ObjectType) prop.getType();
            Graph.Edge edge = propertyEdge(node, prop);
            m_dependenceGraph.addEdge(edge);
            GraphNode head = (GraphNode) edge.getHead();

            if ( head.getNodeType().lessThan(node.getNodeType()) ) {

                if ( prop.isComponent() ) {
                    if ( node.getNodeType() == NodeType.RECOVERABLE ) {
                        head.setNodeType(NodeType.RECOVERABLE);
                    } else {
                        head.setNodeType(NodeType.COVERSIONED_TYPE);
                    }
                } else if ( prop.isRequired() || isVersionedProperty(prop) ) {
                    head.setNodeType(NodeType.RECOVERABLE);
                } else {
                    throw new AssertionError
                        ("can't possibly get here: " + prop);
                }
                propagateNodeType(head);
            }
        }
    }

    void initialize() {
        resetObjectTypeInheritanceForest();
        propagateFromMarked();
    }

    private static Graph.Edge subtypeEdge(GraphNode tail, GraphNode head) {
        return new GraphEdge(tail, head, null);
    }

    /**
     * @pre prop.getType() instanceof ObjectType
     **/
    private static Graph.Edge propertyEdge(GraphNode tail, Property prop) {
        return new GraphEdge(tail,
                             GraphNode.getInstance((ObjectType) prop.getType()),
                             new EdgeLabel(prop));
    }

    private static GraphNode node(ObjectType objType) {
        return GraphNode.getInstance(objType);
    }

    /**
     * Returns an iterator over properties of <code>objType</code> through which
     * propagation of versioning metadata may occur.
     **/
    private Iterator versionableCompoundProperties(ObjectType objType) {
        List props = new LinkedList();
        for (Iterator ii=objType.getProperties(); ii.hasNext(); ) {
            Property prop = (Property) ii.next();
            if ( !isUnversionedProperty(prop) && prop.getType().isCompound()
                 && (prop.isRequired() || prop.isComponent()
                     || isVersionedProperty(prop) ) ) {

                props.add(prop);
            }
        }
        return props.iterator();
    }

    /**
     * This is exposed purely for testing.
     **/
    Graph getDependenceGraph() {
        return m_dependenceGraph.copy();
    }
}
