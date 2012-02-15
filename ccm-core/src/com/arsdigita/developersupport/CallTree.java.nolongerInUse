/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.developersupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.arsdigita.util.StringUtils;
import com.arsdigita.util.Tree;

/**
 * A debugging tool that helps you capture call sites of a method.
 *
 * <p>To elaborate, suppose you have a method <code>foo()</code> that is called
 * from many different places in your code.  You'd like to know what
 * <code>foo</code> callers are and how frequently <code>foo()</code> is
 * called.</p>
 *
 * <p>One possible way to do this is to capture the stack trace every time
 * <code>foo()</code> is called.  Like so:</p>
 *
 * <pre>
 *   public void foo() {
 *       Thread.dumpStack();
 *       // or, better:
 *       Logger.getLogger("foo").debug("foo called", new Throwable());
 *   }
 * </pre>
 *
 * <p>The problem with this approach is that if <code>foo()</code> is called one
 * thousand times, you'll get 1000 stack traces that need to be aggregated and
 * analyzed.</p>
 *
 * <p>This class essentially performs such aggregation for you.  To switch to a
 * concrete example, consider the <code>reduce(String)</code> method in <a
 * href="doc-files/PNSystem.java.txt">PNSystem.java</a>. If we want to figure
 * out this method's callers (and its callers' callers), we would instrument it
 * as follows.</p>
 *
 * <ol>
 *  <li><p>Instrument the entry point into the program.</p>
 *
 *    <p>In this case, the natural entry point is the <code>run</code>
 *    method.</p>
 * <pre>
 *   public void run() {
 *       CallTree tree = CallTree.{@link #getThreadLocal()};
 *       tree.{@link #start(int) start(4)}; // peek 4 levels deep into the stack
 *       if ( checksOutOK() ) {
 *          ...
 *       }
 *       log(tree.{@link #upsideDown(int, int) upsideDown(0)}); // show all execution paths
 *   }
 *
 * </pre>
 * </li>
 *
 * <li><p>Instrument the studied method.</p>
 * <pre>
 *   private String reduce(String str) {
 *       CallTree.getThreadLocal().{@link #capture(String) capture("reduce")};
 *       ...
 *   }
 * </pre>
 * </li>
 * </ol>
 *
 * <p>With this instrumentation in place, running the program would yield the
 * following output.</p>
 *
 * <blockquote><pre style="border: 1px solid black; padding-left: 1ex; padding-right: 1ex;">
 * $ java -classpath $MYCP PNSystem 1000100101 0111011010
 *
 * thread1: 0111011010 --> loops starting with 011011101110100 at position 31
 * with a period of 6. (Rule 1 applied 13 times, Rule 2 applied 18 times.)
 * reduce
 *  32: PNSystem.reduce(PNSystem.java:109)
 *   1: PNSystem.run(PNSystem.java:82)
 *    1: java.lang.Thread.run(Thread.java:479)
 *   13: PNSystem.rule1(PNSystem.java:133)
 *    13: PNSystem.reduce(PNSystem.java:125)
 *     1: PNSystem.run(PNSystem.java:82)
 *     7: PNSystem.rule2(PNSystem.java:138)
 *     5: PNSystem.rule1(PNSystem.java:133)
 *   18: PNSystem.rule2(PNSystem.java:138)
 *    18: PNSystem.reduce(PNSystem.java:127)
 *     7: PNSystem.rule1(PNSystem.java:133)
 *     11: PNSystem.rule2(PNSystem.java:138)
 *
 * thread0: 1000100101 --> loops starting with 011011101110100 at position 25
 * with a period of 6. (Rule 1 applied 10 times, Rule 2 applied 15 times.)
 * reduce
 *  26: PNSystem.reduce(PNSystem.java:109)
 *   1: PNSystem.run(PNSystem.java:82)
 *    1: java.lang.Thread.run(Thread.java:479)
 *   15: PNSystem.rule2(PNSystem.java:138)
 *    15: PNSystem.reduce(PNSystem.java:127)
 *     1: PNSystem.run(PNSystem.java:82)
 *     5: PNSystem.rule1(PNSystem.java:133)
 *     9: PNSystem.rule2(PNSystem.java:138)
 *   10: PNSystem.rule1(PNSystem.java:133)
 *    10: PNSystem.reduce(PNSystem.java:125)
 *     6: PNSystem.rule2(PNSystem.java:138)
 *     4: PNSystem.rule1(PNSystem.java:133)
 * </pre></blockquote>
 *
 * <p>Several observations can be made.  By passing two arguments to
 * <code>PNSystem</code>'s <code>main</code> method, we start two separate
 * execution threads.  Our <code>CallTree</code> class keeps track of these two
 * threads <em>separately</em>.  Hence the two separate call tree traces.  To
 * remind users of the fact that stack trace gathering is scoped to the current
 * thread, the method name for obtaining an instance of <code>CallTree</code> is
 * called {@link #getThreadLocal()}.</p>
 *
 * <p>Let's examine the first trace. The trace is structured as a tree, with
 * child nodes indented relative to their parent node.  Each node in this tree
 * is a call site, prefixed with the number of times this call site has been
 * reached.  The children of a node are its callers. The number of times a call
 * site has been reached is the sum total of the numbers of times its immediate
 * child nodes have been reached.  This invariant holds at all levels of the
 * tree (but see the caveat below).</p>
 *
 * <p>For example, the first line says that the <code>reduce</code> method has
 * been called 32 times: once from <code>main</code>, 13 times from
 * <code>rule1</code>, and 18 times for <code>rule2</code>.
 *
 * <p>You may sometimes observe seeming violations of the above invariant due to
 * the following reasons.  You can limit the depth of captured stack traces.
 * That's what the <code>tree.</code>{@link #start(int) start(4)} call does in the
 * above example.</p>
 *
 * <p>If you want to trim down the aggregated tree to only show the <em>most
 * frequent</em> callers, you can pass an integer parameter to the {@link
 * #upsideDown(int, int)} method, telling it to filter out those callers that account
 * for less than the specified percentage of calls.  In the above example, we
 * called <code>tree.upsideDown(0)</code>.  This essentially tells
 * <code>CallTree</code> to display <em>all</em> execution branches.</p>
 *
 * <p>Had we specified <code>tree.upsideDown(50)</code>, the output would have
 * looked like so:</p>
 *
 * <blockquote><pre style="border: 1px solid black; padding-left: 1ex; padding-right: 1ex;">
 * $ java -classpath $MYCP PNSystem 1000100101 0111011010
 * thread1: 0111011010
 * reduce
 *  32: PNSystem.reduce(PNSystem.java:109)
 *   18: PNSystem.rule2(PNSystem.java:138)
 *    18: PNSystem.reduce(PNSystem.java:127)
 *     11: PNSystem.rule2(PNSystem.java:138)
 *
 * thread0: 1000100101
 * reduce
 *  26: PNSystem.reduce(PNSystem.java:109)
 *   15: PNSystem.rule2(PNSystem.java:138)
 *    15: PNSystem.reduce(PNSystem.java:127)
 *     9: PNSystem.rule2(PNSystem.java:138)
 * </pre></blockquote>
 *
 * <p>This doesn't show any execution paths that account for less than 50% of
 * calls to their parent nodes.</p>
 *
 * <p>Let's switch gears one more time.</p>
 *
 * <p>If you are using this class to debug web applications, what would you use
 * as an entry point for instrumentation?  One good candidate is the {@link
 * com.arsdigita.web.BaseServlet}'s <code>internalService</code> method.</p>
 *
 * <p>This class is meant to complement rather than replace "real" profilers
 * such as <a href="http://www.borland.com/optimizeit/">OptmizeIt</a> or <a
 * href="http://research.sun.com/projects/jfluid/">JFluid</a>.  Once you've
 * found a hot spot with a profiler, you can instrument it with
 * <code>CallTree</code> in order to get an aggregated snapshot of your
 * program's execution paths that lead to the hot spot.</p>
 *
 * <p>This API is subject to frequent change and should only be used for
 * transient debugging sessions.</p>
 *
 * @author  Vadim Nasardinov (vadimn@redhat.com)
 * @since   2004-02-09
 * @version $DateTime: 2004/08/16 18:10:38 $ $Revision: #17 $
 **/
public final class CallTree {
    private final static ThreadLocal s_instances = new ThreadLocal() {
            protected Object initialValue() {
                return new CallTree();
            }
        };

    private final static TreeType UPSIDE_DOWN   = new TreeType();
    private final static TreeType RIGHT_SIDE_UP = new TreeType();

    private Map m_callSites;
    private int m_maxDepth;
    private Guard m_guard;

    private CallTree() { }

    /**
     * Returns an instance scoped to the current thread.
     **/
    public static CallTree getThreadLocal() {
        return (CallTree) s_instances.get();
    }

    /**
     * Specifies the maximum depth to which captured stack traces should be
     * examined.  For example, if you specify 10 and the actual stack trace
     * captured by {@link #capture(String)} is 20 levels deep, then half
     * the stack trace will be discarded.
     *
     * @return a guard object to protect against reentrant calls.  Hold on to
     * the return value, because you will need to pass it to {@link
     * #end(CallTree.Guard)}.
     *
     * @see #end(CallTree.Guard)
     * @throws IllegalArgumentException if <code>maxDepth < 1</code>
     **/
    public Guard start(int maxDepth) {
        if ( m_guard!=null ) {
            // ignore reentrant call.
            return new Guard();
        }

        if ( maxDepth < 1 ) {
            throw new IllegalArgumentException("maxDepth<1: " + maxDepth);
        }
        m_callSites = new HashMap();
        m_maxDepth = maxDepth;
        m_guard = new Guard();
        return m_guard;
    }


    /**
     * Stops the stats gathering process.
     **/
    public void end(Guard guard) {
        if (m_guard==guard) {
            m_guard = null;
        }
    }

    /**
     * Captures the current stack trace for later display.
     *
     * @see #upsideDown(int, int)
     *
     * @throws NullPointerException if <code>siteName</code> is
     * <code>null</code>.
     *
     * @param siteName a short, human-readable name for the call site at which
     * the stack trace is captured
     **/
    public void capture(String siteName) {
        if ( m_guard==null ) { return; }

        if ( siteName==null ) { throw new NullPointerException("siteName"); }

        Pair pair = (Pair) m_callSites.get(siteName);
        if ( pair == null ) {
            pair = new Pair(new Tree(siteName), new Tree(siteName));
            m_callSites.put(siteName, pair);
        }
        List callers =  StringUtils.getStackList(new Throwable());
        if ( callers.size() < 2 ) {
            throw new IllegalStateException
                ("running a little low on callers: " + callers);
        }
        // Discard the first two elements, because they are
        //   java.lang.Throwable
        //   CallTree.capture(CallTree.java:286)
        // or some such.
        final List trimmedCallers = callers.subList(2, callers.size());
        splice(pair.upsideDown, trimmedCallers);

        Collections.reverse(trimmedCallers);
        splice(pair.rightSideUp, trimmedCallers);
    }

    private void splice(Tree tree, List callers) {
        final List pared = callers.size() <= m_maxDepth ?
            callers : callers.subList(0, m_maxDepth);

        spliceRecurse(tree, pared);
    }

    private static void spliceRecurse(Tree tree, List callers) {
        final int listSize = callers.size();
        if ( listSize==0 ) { return; }

        final String caller = (String) callers.get(0);
        boolean matchFound = false;
        Iterator subtrees = tree.getSubtrees().iterator();
        Tree subtree = null;
        while ( subtrees.hasNext() ) {
            Tree.EdgeTreePair pair = (Tree.EdgeTreePair) subtrees.next();
            subtree = pair.getTree();
            Method method = (Method) subtree.getRoot();
            if ( caller.equals(method.getName() ) ) {
                method.increment();
                matchFound = true;
                break;
            }
        }

        if ( !matchFound ) {
            subtree = new Tree(new Method(caller));
            tree.addSubtree(subtree);
        }

        if ( listSize > 1 ) {
            spliceRecurse(subtree, callers.subList(1, listSize));
        }
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(upsideDown(0, Integer.MAX_VALUE));
        sb.append(rightSideUp(0, Integer.MAX_VALUE));
        return sb.toString();
    }

    /**
     * Returns an aggregated, printable view of the stack traces captured via
     * {@link #capture(String)}.  To display all captured execution
     * paths, pass in 0 as the threshold.  To display only those call sites that
     * account for, say, 25% of calls to their callee, pass in 25.  The greater
     * the threshold, the more trimmed down the resulting tree.</p>
     *
     * @param relativeThreshold if a node's subtree accounts for less than
     * <code>relativeThreshold</code> percent of the node's number of calls,
     * this subtree is filtered out of the tree rendering.
     *
     * @param absoluteThreshold works similar to <code>relativeThreshold</code>.
     * If a node accounts for fewer than <code>absoluteThreshold</code> calls,
     * the node and its subtree are filtered out.  Pass in {@link
     * Integer#MAX_VALUE} if you don't want to filter by
     * <code>absoluteThreshold</code>.
     *
     * @throws IllegalArgumentException if <code>relativeThreshold < 0 ||
     * relativeThreshold > 100 || absoluteThreshold < 1</code>.
     **/
    public String upsideDown(int relativeThreshold, int absoluteThreshold) {
        if ( m_guard!=null ) {
            return "ignoring reentrant call";
        }
        return printTrees(relativeThreshold, absoluteThreshold, UPSIDE_DOWN);
    }

    /**
     * @see #upsideDown(int, int)
     **/
    public String rightSideUp(int relativeThreshold, int absoluteThreshold) {
        if ( m_guard!=null ) {
            return "ignoring reentrant call";
        }
        return printTrees(relativeThreshold, absoluteThreshold, RIGHT_SIDE_UP);
    }

    private String printTrees(int relThreshold,
                              int absThreshold,
                              TreeType type) {

        if ( relThreshold < 0 || relThreshold > 100 ) {
            throw new IllegalArgumentException
                ("relThreshold out of range: " + relThreshold);
        }

        if ( absThreshold < 1 ) {
            throw new IllegalArgumentException
                ("absThreshold out of range: "  + absThreshold);
        }

        List callSites = new ArrayList(m_callSites.keySet());
        Collections.sort(callSites);
        StringBuffer result = new StringBuffer();
        for (Iterator ii=callSites.iterator(); ii.hasNext(); ) {
            String callSite = (String) ii.next();
            Pair pair = (Pair) m_callSites.get(callSite);
            if ( type == UPSIDE_DOWN ) {
                printTree(pair.upsideDown, result, relThreshold, absThreshold);
            } else if ( type == RIGHT_SIDE_UP ) {
                printTree(pair.rightSideUp, result, relThreshold, absThreshold);
            } else {
                throw new IllegalArgumentException("unknown type");
            }
        }
        return result.toString();
    }

    private void printTree(Tree root, StringBuffer result,
                           int relThreshold, int absThreshold) {

        result.append(root.getRoot()).append("\n");
        Iterator subtrees = root.getSubtrees().iterator();
        while ( subtrees.hasNext() ) {
            Tree.EdgeTreePair pair = (Tree.EdgeTreePair) subtrees.next();
            printTreeRecurse(pair.getTree(), result, " ",
                             relThreshold, absThreshold);
        }
    }

    private static void printTreeRecurse(Tree tree, StringBuffer result,
                                         String indent,
                                         int relThreshold, int absThreshold) {

        final Method callee = (Method) tree.getRoot();
        if (callee.getCount() < absThreshold ) { return; }

        result.append(indent).append(callee).append("\n");
        for (Iterator ii=tree.getSubtrees().iterator(); ii.hasNext(); ) {
            Tree.EdgeTreePair pair = (Tree.EdgeTreePair) ii.next();
            Method caller = (Method) pair.getTree().getRoot();
            if ( 100 * caller.getCount() / callee.getCount() >= relThreshold ) {
                printTreeRecurse(pair.getTree(), result, indent + " ",
                                 relThreshold, absThreshold);
            }
        }
    }


    /**
     * @see #start(int)
     * @end #end(CallTree.Guard)
     **/
    public final static class Guard {}

    private final static class TreeType {}

    private static class Pair {
        Tree upsideDown;
        Tree rightSideUp;

        Pair(Tree upsideDown, Tree rightSideUp) {
            this.upsideDown = upsideDown;
            this.rightSideUp = rightSideUp;
        }
    }

    private static class Method {
        private final String m_name;
        private final MutableInteger m_counter;

        /**
         * @throws NullPointerException if name is null
         **/
        public Method(String name) {
            if ( name==null ) { throw new NullPointerException("name"); }

            m_name = name.intern();
            m_counter = new MutableInteger();
            m_counter.increment();
        }

        public String getName() {
            return m_name;
        }

        public void increment() {
            m_counter.increment();
        }

        public String toString() {
            StringBuffer sb = new StringBuffer();
            sb.append(m_counter).append(": ").append(m_name);
            return sb.toString();
        }

        public int getCount() {
            return m_counter.intValue();
        }
    }
}
