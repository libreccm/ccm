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
package com.arsdigita.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * CallTracer is a utility class for tracing calls to methods in a class. This
 * is a poor man's OptimizeIT or JProbe type of tool. Use to figure out where
 * methods are being called from and with what frequency. To use, do something
 * like:
 *
 * <pre>
 * Class Foo {
 *    private static CallTracer s_trace = new CallTracer(Foo.class, 100);
 *
 *    public foo() {
 *       s_trace.trace();
 *
 *       // work....
 *    }
 *
 *    public bar() {
 *       s_trace.trace();
 *
 *       // work....
 *    }
 *
 * }
 * </pre>
 *
 * <p>In this example, the CallTracer will independently track calls to foo()
 * and bar(), and send a report to the log every 100 calls of: every caller of
 * foo (or bar) and it's count.</p>
 *
 * <p>The CallTracer _will not_ log unless DEBUG level in log4j is set for the
 * class being traced.</p>
 *
 * <p>Obviously, using CallTracer can impose a signifigant overhead.</p>
 *
 * <p>See <a
 * href="https://bugzilla.redhat.com/bugzilla/show_bug.cgi?id=113696#c7">bug
 * 113696</a> for an example of how this class can be used.</p>
 *
 * @author jorris@redhat.com
 * @version $Revision $1 $ $Date: 2004/08/16 $
 */
public class CallTracer {

    private final Logger m_logger;
    private final int m_frequency;
    private final int m_level;
    private final Map m_methods = new HashMap();

    /**
     * Creates a call tracer for a given class.
     *
     * @param theClass The class we want to track methods on.
     * @param frequency The number of hits for each method before
     *                  the system dumps the callers & counts to the log.
     */
    public CallTracer(final Class theClass, final int frequency) {
        this(theClass, frequency, 1);
    }

    /**
     * Creates a call tracer for a given class.
     *
     * @param theClass The class we want to track methods on.
     * @param frequency The number of hits for each method before
     *                  the system dumps the callers & counts to the log.
     * @param level The caller level to log at. The default is one, meaning the
     *              immediate caller of the method being traced. Setting the
     *              level higher will trace the caller's caller's ....etc instead.
     *              Default should be used in most instances.
     */
    public CallTracer(final Class theClass,final int frequency, final int level) {
        m_logger = Logger.getLogger(theClass);
        m_frequency = frequency;
        m_level = level;

    }


    /**
     * Records the caller of the method that called trace().
     */
    public void trace() {
        if (m_logger.isDebugEnabled()) {
            final List trace = StringUtils.getStackList(new Throwable());
            final String method = (String) trace.get(2);
            final String caller = getCallerFromTrace(m_level, trace);
            incrementCount(method, caller);
        }
    }


    /**
     *  Increment the count for the caller of the method
     * @param method The method 'name'
     * @param caller The caller of the method
     */
    private void incrementCount(final String method, final String caller) {
        MethodTrace trace = getMethodTrace(method);
        trace.incrementCount(method, caller);
    }

    private MethodTrace getMethodTrace(final String method) {
        MethodTrace trace;
        synchronized(m_methods) {
            trace = (MethodTrace) m_methods.get(method);
            if (trace == null) {
                trace = new MethodTrace();
                m_methods.put(method, trace);
            }
        }

        return trace;
    }


    /**
     * Utility method to get the caller of a method
     * @return The file listing where the method was called from
     */
    public static String getCaller() {
        final List trace = StringUtils.getStackList(new Throwable());
        return getCallerFromTrace(1, trace);
    }

    /**
     * Utility method to get the caller of a method
     * @param level The level of the caller. 1 - immediate parent, 2 - grandparent, etc...
     * @return The file listing where the method was called from
     */
    public static String getCaller(final int level) {
        Assert.isTrue(level > 0, "Level must be greater than zero!");
        final List trace = StringUtils.getStackList(new Throwable());
        return getCallerFromTrace(level, trace);
    }

    private static String getCallerFromTrace(final int level, final List trace) {
        int entryIdx = level + 2;
        if (entryIdx >= trace.size()) {
            Logger.getLogger(CallTracer.class).warn("Level " +
                    level +
                    " is too deep; called from " +
                    trace.get(2) +
                    ". Getting last entry instead.");
            entryIdx = trace.size() - 1;
        }

        final String caller = (String) trace.get(entryIdx);
        return caller;

    }
    private  final class MethodTrace {
        private final HashMap m_callers = new HashMap();
        private long m_totalCalls = 0;

        public void incrementCount(final String method, final String caller) {

            synchronized (m_callers) {
                Integer count = (Integer) m_callers.get(caller);
                if (count == null) {
                    count = new Integer(0);
                }
                count = new Integer(count.intValue() + 1);
                m_callers.put(caller, count);
                m_totalCalls++;

                if (m_totalCalls % CallTracer.this.m_frequency == 0) {
                    logCalls(method);
                }
            }
        }


        private void logCalls(final String method) {
            m_logger.debug("----------- Callers for method " + method + " -----------");
            m_logger.debug(m_totalCalls + " total so far.");
            final Iterator iter = m_callers.keySet().iterator();
            while (iter.hasNext()) {
                final String caller =  (String) iter.next();
                final Integer count = (Integer) m_callers.get(caller);
                m_logger.debug(caller + " called " + count + " times.");
            }

            m_logger.debug("----------- DONE -----------");

        }

    }
}
