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

import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.persistence.metadata.Property;
import com.arsdigita.util.Graph;
import com.arsdigita.util.GraphEdge;
import com.arsdigita.util.GraphFormatter;
import com.arsdigita.util.GraphSet;
import com.arsdigita.util.Graphs;
import com.redhat.persistence.pdl.VersioningMetadata;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

// new versioning

/**
 * Sundry developer support utilities.
 *
 * @author Vadim Nasardinov (vadimn@redhat.com)
 * @since  2003-05-22
 * @version $Revision: #15 $ $Date: 2004/08/16 $
 **/
final class DevSupport {

    private DevSupport() {}

    /**
     * Pretty-prints the PDL model. The output is the dot language format used
     * by graphviz.  The graph only shows nodes that are interesting from the
     * versioning point of view.
     **/
    public static void pdlToDot(NodeFilter filter, PrintWriter writer) {
        Graph graph = new GraphSet();
        graph.setLabel("pdl_model");

        Iterator objTypes = SessionManager.getMetadataRoot().
            getObjectTypes().iterator();

        while (objTypes.hasNext()) {
            ObjectType objType = (ObjectType) objTypes.next();
            if ( !filter.test(objType.getQualifiedName(),
                              isReachable(objType)) ) {

                continue;
            }

            graph.addNode(objType);
            ObjectType supertype = objType.getSupertype();
            if ( supertype != null &&
                 filter.test(supertype.getQualifiedName(),
                             isReachable(supertype)) ) {

                graph.addEdge(supertype, objType, "extends");
            }

            for (Iterator props=objType.getProperties(); props.hasNext(); ) {
                Property prop = (Property) props.next();
                if ( !prop.getType().isCompound()) { continue; }

                graph.addEdge(objType, prop.getType(), propertyLabel(prop));
            }
        }

        Graphs.printGraph(graph, new ModelFormatter(), writer);
    }

    /**
     * A shortcut to {@link #pdlToDot(DevSupport.NodeFilter, PrintWriter)} that prints
     * to the stdout.
     **/
    public static void pdlToDot(NodeFilter filter) {
        pdlToDot(filter, new PrintWriter(System.out, true));
    }

    private static boolean isReachable(ObjectType objType) {
        return !ObjectTypeMetadata.getInstance().isUnreachable(objType);
    }

    private static final String GRAPH_ATTRS_HEADER =
        "    node[shape=box,fontsize=8,fontname=verdana,height=0.2,width=0.2,style=filled];\n" +
        "    ranksep=0.05;\n";

    private static final String GRAPH_ATTRS_FOOTER =
        "\n" +
        "    node[fontsize=10];\n" +
        "    edge[fontsize=9,fontname=verdana,style=solid,minlen=2];\n\n";


    private static class ModelFormatter implements GraphFormatter {
        private final static String GRAPH_ATTRS =
            GRAPH_ATTRS_HEADER +
            "    edge[style=invis];\n" +
            "    marked -> unmarked;\n" +
            "    unmarked -> qualifiers;\n" +
            "    subgraph cluster_legend {\n" +
            "      label=\"Legend\";\n" +
            "      fontsize=11;\n" +
            "\n" +
            "      marked[label=\"marked\\nversioned\",fillcolor=Wheat];\n" +
            "      unmarked[label=unmarked];\n" +
            "      qualifiers[label=\"rqd: required\\ncnt: component\",shape=plaintext];\n" +
            "    }\n" +
            GRAPH_ATTRS_FOOTER;



        public String graphAttributes(Graph graph) {
            return GRAPH_ATTRS;
        }

        public String nodeName(Object node) {
            ObjectType objType = (ObjectType) node;
            return objType.getName();
        }

        public String nodeAttributes(Object node) {
            ObjectType objType = (ObjectType) node;
            if ( VersioningMetadata.getVersioningMetadata().isMarkedVersioned
                 (objType.getQualifiedName()) ) {

                return "[fillcolor=Wheat]";
            } else {
                return null;
            }
        }

        public String edge(Object edge) {
            return (String) edge;
        }
    }

    /**
     * Pretty-prints the versioning metadata graph.  The output is in the dot
     * language format defined by graphviz.
     **/
    public static void versioningGraphToDot(NodeFilter filter,
                                            PrintWriter writer) {

        Graph fullGraph = ObjectTypeMetadata.getInstance().getDependenceGraph();
        Graph subgraph = new GraphSet();
        subgraph.setLabel("dependence_graph");

        Iterator nodes = fullGraph.getNodes().iterator();
        while ( nodes.hasNext() ) {
            final GraphNode node = (GraphNode) nodes.next();
            final ObjectType objType = node.getObjectType();
            if ( !filter.test(objType.getQualifiedName(),
                              isReachable(objType)) ) {
                continue;
            }

            subgraph.addNode(node);
            Iterator edges=fullGraph.getOutgoingEdges(node).iterator();
            while ( edges.hasNext() ) {
                subgraph.addEdge((Graph.Edge) edges.next());
            }

            if ( !isReachable(objType) ) { continue; }

            for (Iterator props=objType.getProperties(); props.hasNext(); ) {
                Property prop = (Property) props.next();
                if ( !prop.getType().isCompound() || prop.isComponent() ||
                     prop.isRequired() ) {

                    continue;
                }

                GraphNode head = GraphNode.getInstance((ObjectType) prop.getType());
                subgraph.addEdge(new GraphEdge(node, head, new EdgeLabel(prop)));
            }

        }

        Graphs.printGraph(subgraph, new DependenceFormatter(), writer);
    }

    /**
     * A shortcut to {@link #versioningGraphToDot(DevSupport.NodeFilter,
     * PrintWriter)} that prints to the stdout.
     **/
    public static void versioningGraphToDot(NodeFilter filter) {
        versioningGraphToDot(filter, new PrintWriter(System.out, true));
    }

    private static class DependenceFormatter implements GraphFormatter {

        private static final String GRAPH_ATTRS =
            GRAPH_ATTRS_HEADER +
            "    edge[style=invis];\n" +
            "    versioned -> coversioned;\n" +
            "    coversioned -> recoverable;\n" +
            "    recoverable -> unreachable;\n" +
            "    unreachable -> qualifiers;\n" +
            "\n" +
            "    subgraph cluster_legend {\n" +
            "      label=\"Legend\";\n" +
            "      fontsize=11;\n" +
            "\n" +
            "      versioned[label=versioned,fillcolor=Tomato];\n" +
            "      coversioned[label=coversioned,fillcolor=Pink];\n" +
            "      recoverable[fillcolor=LemonChiffon];\n" +
            "      unreachable;\n" +
            "      qualifiers[label=\"rqd: required\\ncnt: component\\nvnd: versioned\\nunv: unversioned\",shape=plaintext];\n" +
            "    }\n"+
            GRAPH_ATTRS_FOOTER;

            private static final Map COLORS = new HashMap();

        static {
            COLORS.put(NodeType.VERSIONED_TYPE,
                       "[fillcolor=Tomato,comment=\"versioned type\"]");
            COLORS.put(NodeType.COVERSIONED_TYPE,
                       "[fillcolor=Pink,comment=\"coversioned type\"]");
            COLORS.put(NodeType.RECOVERABLE,
                       "[fillcolor=LemonChiffon,comment=recoverable]");
            COLORS.put(NodeType.UNREACHABLE, "[comment=unreachable]");
        }

        public String graphAttributes(Graph graph) {
            return GRAPH_ATTRS;
        }

        public String nodeName(Object node) {
            GraphNode graphNode = (GraphNode) node;
            StringBuffer sb = new StringBuffer();
            sb.append("\"");
            sb.append(graphNode.getObjectType().getName()).append("\"");
            return sb.toString();
        }

        public String nodeAttributes(Object node) {
            GraphNode graphNode = (GraphNode) node;
            return (String) COLORS.get(graphNode.getNodeType());
        }

        public String edge(Object edge) {
            if ( edge == null ) return "extends";

            EdgeLabel label = (EdgeLabel) edge;
            String pLabel = propertyLabel(label.getProperty());

            if ( label.isUnversioned() ) {
                return "unv," + pLabel;
            }
            if ( label.isVersioned() ) {
                return "vnd" + pLabel;
            }
            return pLabel;
        }
    }

    private static String propertyLabel(Property prop) {
        StringBuffer sb = new StringBuffer();

        if ( prop.isComponent() ) {
            sb.append("cnt");
        } else {
            if ( prop.isRequired() ) {
                sb.append("rqd");
            } else {
                // if it's not required, it's either nullable, or it is a
                // collection.
                if ( prop.isCollection() ) {
                    sb.append("0..n");
                } else {
                    sb.append("0..1");
                }
            }
        }
        sb.append(":").append(prop.getName());
        return sb.toString();
    }


    /**
     * Filters out nodes that should be excluded from the output generated by
     * methods provided by the {@link DevSupport} class.
     *
     * @see DevSupport#pdlToDot(DevSupport.NodeFilter)
     * @see DevSupport#versioningGraphToDot(DevSupport.NodeFilter)
     **/
    interface NodeFilter {

        /**
         * Returns <code>true</code> if the object type whose name is
         * <code>qName</code> passes this filter.
         *
         * @param qName the fully qualified name of the object type
         * @param isReachable object types for which this is <code>false</code>
         * are ignored by the versioning system.
         **/
        boolean test(String qName, boolean isReachable);
    }
}
