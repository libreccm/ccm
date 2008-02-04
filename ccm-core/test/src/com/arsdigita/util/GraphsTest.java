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
package com.arsdigita.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import junit.framework.TestCase;

/**
 * @author Vadim Nasardinov (vadimn@redhat.com)
 * @version $Date: 2004/08/16 $
 * @since 2003-01-22
 **/
public class GraphsTest extends TestCase {
    private static final String A = "A";
    private static final String B = "B";
    private static final String C = "C";
    private static final String D = "D";
    private static final String E = "E";

    private static final String LINE_SEP =
        System.getProperty("line.separator");

    private Graph m_graph;

    public void setUp() {
        m_graph = new GraphSet();
        m_graph.addEdge(A, C, "a -> c");
        m_graph.addEdge(B, C, "b -> c");
        m_graph.addEdge(C, D, "c -> d");
        m_graph.addEdge(D, E, "d -> e");
        m_graph.addEdge(E, A, "e -> a");
    }

    public void testFindPath() {
        List expectedPath =
            Arrays.asList(new String[] {A, C, D, E});
        List computedPath = Graphs.edgePathToNodePath
            (Graphs.findPath(m_graph, A, E));
        assertEquals("path from A to E", expectedPath, computedPath);

        expectedPath =
            Arrays.asList(new String[] {D, E, A, C});
        computedPath = Graphs.edgePathToNodePath
            (Graphs.findPath(m_graph, D, C));
        assertEquals("path from D to C", expectedPath, computedPath);
    }

    public void testNodesReachableFrom() {
        List computedResult =
            Graphs.nodesReachableFrom(m_graph, A).getNodes();
        Collections.sort(computedResult);
        List expectedResult = Arrays.asList
            (new String[] {A, C, D, E});
        Collections.sort(expectedResult);
        assertEquals("nodes reachable from A", expectedResult, computedResult);

        computedResult =
            Graphs.nodesReachableFrom(m_graph, B).getNodes();
        Collections.sort(computedResult);
        expectedResult = Arrays.asList
            (new String[] {B, C, D, E, A});
        Collections.sort(expectedResult);
        assertEquals("nodes reachable from B", expectedResult, computedResult);

        Graph simpleGraph = new GraphSet();
        simpleGraph.setLabel("simple_graph");
        simpleGraph.addEdge(A, B, "a -> b");
        Graph result = Graphs.nodesReachableFrom
            (simpleGraph, B);
        result.setLabel("reachable_from_b");
        assertTrue("b is reachable from b",
                   result.nodeCount() == 1 &&
                   B.equals(result.getNodes().get(0)));
    }

    public void testGetSinkNodes() {
        List sinkNodes = Graphs.getSinkNodes(m_graph);
        assertEquals("sink node count in m_graph", 0, sinkNodes.size());

        Graph gg = new GraphSet();
        gg.addEdge(A, B, "a -> b");
        gg.addEdge(A, C, "a -> c");
        gg.addEdge(B, D, "b -> d");
        sinkNodes = Graphs.getSinkNodes(gg);
        assertEquals("sink node count in gg", 2, sinkNodes.size());
    }

    public void testPrintTree() {
        Tree aa = new Tree(A);
        aa.setLabel("test");
        aa.addChild(B).addChild(C);
        aa.addChild(D);
        aa.addChild(E);
        StringWriter swriter = new StringWriter();
        PrintWriter writer = new PrintWriter(swriter);
        Graphs.printTree(aa, new GraphFormatter() {
                public String graphAttributes(Graph graph) {
                    return null;
                }

                public String nodeName(Object node) {
                    return (String) node;
                }

                public String nodeAttributes(Object node) {
                    return null;
                }

                public String edge(Object edge) {
                    return (String) edge;
                }
            }, writer);
        writer.flush();
        String actual = swriter.toString();
        StringBuffer sb = new StringBuffer();
        sb.append("digraph test {").append(LINE_SEP);
        sb.append("    A -> B;").append(LINE_SEP);
        sb.append("    B -> C;").append(LINE_SEP);
        sb.append("    A -> D;").append(LINE_SEP);
        sb.append("    A -> E;").append(LINE_SEP);
        sb.append("}").append(LINE_SEP);
        assertEquals("printed graph", sb.toString(), actual);
    }
}
