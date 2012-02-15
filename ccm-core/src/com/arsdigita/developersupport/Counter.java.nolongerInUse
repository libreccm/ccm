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

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.arsdigita.util.Tree;

import org.apache.log4j.Logger;

/**
 * A debugging tool that helps you count how many times a particular action has
 * been executed.
 *
 * <p>This API is subject to frequent change and should only be used for
 * transient debugging sessions.</p>
 *
 * <p>Set the "com.arsdigita.developersupport.Counter" log4j logger's
 * level to <code>DEBUG</code> in order to see the collected stats.</p>
 *
 * @author  Vadim Nasardinov (vadimn@redhat.com)
 * @since   2004-02-06
 * @version $DateTime: 2004/08/16 18:10:38 $ $Revision: #12 $
 **/
public final class Counter {
    private final static Logger s_log = Logger.getLogger(Counter.class);

    private final static ThreadLocal s_counters = new ThreadLocal() {
            protected Object initialValue() {
                return new Counter();
            }
        };

    private final static DateFormat DATE_FMT =
        new SimpleDateFormat("HH:mm:ss.S");

    private final static DecimalFormat DURATION_FMT = new DecimalFormat();
    static {
        s_log.debug("Static initalizer starting...");
        DURATION_FMT.setGroupingSize(3);
        DURATION_FMT.setGroupingUsed(true);
        s_log.debug("Static initalizer finished.");
    }
        

    private Tree m_root;
    private Tree m_current;
    private long m_tStamp;

    // TODO: Stack is a synchronized collection.  Replace it with a
    // non-synchronized implementation, since we don't synchronization, insofar
    // as each thread gets its own Counter.
    private final Stack m_stack;

    private Counter() {
        m_stack = new Stack();

        StringBuffer rootCtxt = new StringBuffer(128);
        rootCtxt.append("thread: ").append(Thread.currentThread().getName());

        m_root = new Tree(m_stack.push(new Context(rootCtxt.toString())));
        m_current = m_root;
        m_tStamp = System.currentTimeMillis();
    }

    public static Counter getCounter() {
        return (Counter) s_counters.get();
    }

    /**
     * Creates a new counter context.  Hold on to the returned value.  You'll
     * need it in order to {@link #end(Counter.Context) end} this context off the
     * counter.
     **/
    public Context start(String context) {
        ((Context) m_current.getRoot()).pause();

        Context result = (Context) m_stack.push(new Context(context));
        Tree child = new Tree(result);
        m_current.addSubtree(child);
        m_current = child;
        return result;
    }

    /**
     * @throws ContextMispatchException if the passed in context does not match
     * the current context of this counter.
     * @throws NullPointerException if <code>expected</code> is null
     *
     * @param expected by passing this parameter, you prove that you put it
     * there.  This forces you to correctly bracket your debugging contexts by
     * always ending what you started.
     **/
    public void end(Context context) {
        if (context==null) {throw new NullPointerException("context"); }

        final Context current = (Context) m_stack.pop();
        if ( !current.equals(context) ) {
            throw new ContextMismatchException(current, context);
        }

        current.pause();
        m_current = m_current.getParent();
        ((Context) m_current.getRoot()).restart();
    }

    /**
     * Increments the running count of the <code>action</code> in the current
     * context.
     *
     * @param action the name of the action whose number of executions we are
     * trying to compute.
     **/
    public void increment(String action) {
        currentContext().increment(action);
    }

    private Context currentContext() {
        return (Context) m_stack.peek();
    }

    /**
     * Logs the passed in <code>message</code> and the current contents of the
     * counter.
     **/
    public void log(String message) {
        s_log.debug(message);
        s_log.debug(this);
    }

    private static void duration(StringBuffer sb,
                                 long start, long end, long total) {

        if ( end < start ) {
            throw new IllegalArgumentException
                ("end<start: start=" + start + ", end=" + end);
        }
        if ( total > end-start) {
            throw new IllegalArgumentException
                ("total>start-end; start=" + start + ", end=" + end +
                 ", total=" + total);
        }
        sb.append("started: ");
        sb.append(DATE_FMT.format(new Date(start)));
        sb.append("; ended: ").append(DATE_FMT.format(new Date(end)));
        sb.append("; duration: ");
        sb.append(DURATION_FMT.format(total)).append(" ms");
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        Context rootCtxt = (Context) m_root.getRoot();
        sb.append(rootCtxt.getContext()).append("\n");
        sb.append("counter ");
        final long finish = System.currentTimeMillis();
        duration(sb, m_tStamp, finish, finish-m_tStamp);
        sb.append("\n");

        Iterator subtrees = m_root.getSubtrees().iterator();
        while ( subtrees.hasNext() ) {
            Tree.EdgeTreePair pair = (Tree.EdgeTreePair) subtrees.next();
            toString(pair.getTree(), sb, "");
        }

        return sb.toString();
    }

    private void toString(Tree tree, StringBuffer result, String indent) {
        Context context = (Context) tree.getRoot();
        result.append(context.toString(indent));

        Iterator subtrees = tree.getSubtrees().iterator();
        while ( subtrees.hasNext() ) {
            Tree.EdgeTreePair pair = (Tree.EdgeTreePair) subtrees.next();
            toString(pair.getTree(), result, indent + "  ");
        }
    }


    public final static class Context {
        private final String m_context;
        private final Map m_actions;
        private final long m_started;
        private long m_restarted;
        private long m_runningTotal;
        private long m_finished;
        private boolean m_inContext;

        private Context(String context) {
            if (context==null) {throw new NullPointerException("context"); }

            m_context = context;
            m_actions = new HashMap();
            m_restarted = m_started = System.currentTimeMillis();
            m_runningTotal = 0;
            m_inContext = true;
        }

        void increment(String action) {
            if ( !m_inContext ) {
                throw new IllegalStateException("not in context: " + debug());
            }

            MutableInteger counter = (MutableInteger) m_actions.get(action);
            if ( counter == null ) {
                counter = new MutableInteger();
                m_actions.put(action, counter);
            }
            counter.increment();
        }

        public void pause() {
            if ( !m_inContext ) {
                throw new IllegalStateException("not in context: " + debug());
            }
            m_inContext = false;
            m_finished = System.currentTimeMillis();
            m_runningTotal += m_finished - m_restarted;
        }

        public void restart() {
            if ( m_inContext ) {
                throw new IllegalStateException("in context: " + debug());
            }
            m_inContext = true;
            m_restarted = System.currentTimeMillis();
        }

        String debug() {
            StringBuffer sb = new StringBuffer();
            final String d = "; ";
            sb.append("context=").append(m_context).append(d);
            sb.append("id=").append(hashCode()).append(d);
            sb.append("actions=").append(m_actions).append(d);
            sb.append("started=").append(m_started).append(d);
            sb.append("restarted=").append(m_restarted).append(d);
            sb.append("finished=").append(m_finished).append(d);
            sb.append("total=").append(m_runningTotal).append(d);
            sb.append("inContext=").append(m_inContext);

            return sb.toString();
        }

        public String toString() {
            return toString("");
        }

        String toString(String indent) {
            StringBuffer sb = new StringBuffer();
            sb.append(indent).append(m_context).append("@");
            sb.append(hashCode()).append(": ");
            duration(sb, m_started, m_finished, m_runningTotal);
            sb.append(":\n");
            List actions = new ArrayList(m_actions.keySet());
            Collections.sort(actions);
            for (Iterator ii=actions.iterator(); ii.hasNext(); ) {
                final String key = (String) ii.next();
                sb.append(indent).append("  ").append(key).append(": ");
                sb.append(m_actions.get(key)).append("\n");
            }

            return sb.toString();
        }

        String getContext() {
            return m_context;
        }
    }

    public final static class ContextMismatchException extends RuntimeException {
        private final Context m_actual;
        private final Context m_expected;

        public ContextMismatchException(Context expected, Context actual) {
            m_actual = actual;
            m_expected = expected;
        }

        public String getMessage() {
            StringBuffer sb = new StringBuffer();
            sb.append("Expected: ").append(m_expected.debug());
            sb.append("; Actual: ").append(m_actual.debug());
            return sb.toString();
        }
    }
}
