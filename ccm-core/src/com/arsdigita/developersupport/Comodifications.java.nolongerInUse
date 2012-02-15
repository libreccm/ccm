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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * Provides tools for debugging {@link java.util.ConcurrentModificationException
 * concurrent modification} exceptions that may occur in multi-threaded
 * programs.  This class should be used for debugging only.
 *
 * <p>Let's illustrate by example. A concurrent modification exception will
 * occur if you call {@link Iterator#next()} on an {@link Iterator iterator}
 * whose underlying {@link Collection collection} has changed after the iterator
 * was created.  Here's the simplest example:</p>
 *
 * <blockquote><pre style="border: 1px solid black; padding-left: 1ex; padding-right: 1ex;">
 * import java.util.*;
 * 
 * public final class CoMoExample {
 *     public final static void main(String[] s) {
 *         List list = new LinkedList();
 *         list.add("foo");
 *         for (Iterator it=list.iterator(); it.hasNext(); ) {
 *             System.out.println(it.next());
 *             list.add("bar");
 *         }
 *     }
 * }
 * </pre></blockquote>
 *
 * <p>If you compile <code>CoMoExample.java</code> and run it, you'll get
 * something like this:</p>
 *
 * <blockquote><pre style="border: 1px solid black; padding-left: 1ex; padding-right: 1ex;">
 * $ java -cp . CoMoExample
 * foo
 * Exception in thread "main" java.util.ConcurrentModificationException
 *         at java.util.LinkedList$ListItr.checkForComodification(LinkedList.java:530)
 *         at java.util.LinkedList$ListItr.next(LinkedList.java:471)
 *         at CoMoExample.main(CoMoExample.java:8)
 * </pre></blockquote>
 *
 * <p>In this case, the program is single-threaded and the source of the
 * concurrent modification exception is trivially discernible.  Things get more
 * complicated when you have two threads, one of which is iterating over a
 * collection, while the other is concurrently modifying the same collection.
 * You will get a <code>ConcurrentModificationException</code> similar to the
 * above, but it only gives you one half of the picture.  It shows what the
 * iterating thread was doing when the comodification was detected.  It doesn't
 * show you the other thread that was modifying the shared collection
 * concurrently.</p>
 *
 * <p>If you have a reproducible case of concurrent modification, you can debug
 * it by instrumenting the offending collection as follows.  Say, the culprit
 * collection is a {@link List list}, allocated like so:</p>
 *
 * <blockquote><pre style="border: 1px solid black; padding-left: 1ex; padding-right: 1ex;">
 * List culprit = new LinkedList();
 * </pre></blockquote>
 *
 * <p>You may edit the above piece of code like so:</p>
 *
 * <blockquote><pre style="border: 1px solid black; padding-left: 1ex; padding-right: 1ex;">
 * List culprit = Comodifications.newUnforgetfulList(new LinkedList());
 * </pre></blockquote>
 *
 * <p>This will give an "instrumented" list that tracks concurrent modifications
 * a lot better than your standard list.  When a concurrent modification occurs,
 * the following stack traces will be logged.</p>
 *
 * <ol>
 *   <li><p>The point where the offending iterator was created.  Something like
 *   this:</p>
 * <blockquote><pre style="border: 1px solid black; padding-left: 1ex; padding-right: 1ex;">
 * The iterator was created at
 * StackTrace: [thread 19; timestamp=15:02:47.439]
 *         at Comodifications$ListHandler$IteratorImpl.<init>(Comodifications.java:112)
 *         at Comodifications$ListHandler.invoke(Comodifications.java:72)
 *         at $Proxy0.iterator(Unknown Source)
 *         at Main$Page.dispatch(Main.java:63)
 * </pre></blockquote>
 *  </li>
 *
 * <li><p>The point where the other thread comodified the shared list.</p>
 * <blockquote><pre style="border: 1px solid black; padding-left: 1ex; padding-right: 1ex;">
 * Comodification occurred at
 * StackTrace: [thread 20; timestamp=15:02:47.459]
 *         at Comodifications$ListHandler.invoke(Comodifications.java:78)
 *         at $Proxy0.add(Unknown Source)
 *         at Main$Page.init(Main.java:56)
 * </pre></blockquote>
 *  </li>
 * </ol>
 *
 * <p>Note that the "instrumented" list returned by {@link
 * #newUnforgetfulList(List)} captures a stack trace every time the list is
 * mutated.  This may noticeably slow down all mutator methods and change the
 * timing, thereby accidentally eliminating the race condition are you are
 * trying to debug.</p>
 *
 * @see StackTrace
 * 
 * @author  Vadim Nasardinov (vadimn@redhat.com)
 * @since   2003-12-22
 * @version $Id: Comodifications.java 287 2005-02-22 00:29:02Z sskracic $
 **/
public final class Comodifications {
    private static final Logger s_log = Logger.getLogger(Comodifications.class);

    private Comodifications() {}

    /**
     * Wraps the passed in list inside a tracking proxy list.  The returned
     * proxy list provides better error reporting for concurrent modification
     * exceptions. 
     **/
    // FIXME: This doesn't track concurrent modifications, if the client obtains
    // the iterator via listIterator().
    public static List newUnforgetfulList(List list) {
        return (List) Proxy.newProxyInstance
            (Comodifications.class.getClassLoader(),
             new Class[] {List.class},
             new ListHandler(list));
    }

    /*
     * I'm too lazy to implement the entire List interface by hand.  I'll let
     * the Proxy class do most of the work for me.
     */
    private static class ListHandler implements InvocationHandler {
        private static final Logger logger = Logger.getLogger(ListHandler.class);
        private final static Set s_mutators = new HashSet();

        static {
            logger.debug("Static initalizer starting...");
            registerMutator("add", new Class[] {Integer.TYPE, Object.class});
            registerMutator("add", new Class[] {Object.class});
            registerMutator("addAll", new Class[] {Collection.class});
            registerMutator("addAll",
                            new Class[] {Integer.TYPE, Collection.class});
            registerMutator("clear", new Class[] {});
            registerMutator("remove", new Class[] {Integer.TYPE});
            registerMutator("remove", new Class[] {Object.class});
            registerMutator("removeAll", new Class[] {Collection.class});
            registerMutator("retainAll", new Class[] {Collection.class});
            registerMutator("set", new Class[] {Integer.TYPE, Object.class});
            logger.debug("Static initalizer finished.");
        }

        private final static Method s_iteratorMethod =
            getMethod("iterator", new Class[] {});

        private final List m_proxiedList;
        private int m_mods;
        private final StackTrace m_created;
        private StackTrace m_lastModification;

        ListHandler(List proxiedList) {
            if ( proxiedList==null ) {
                throw new NullPointerException("proxiedList");
            }

            m_proxiedList = proxiedList;
            m_mods = 0;
            m_created = new StackTrace();
        }

        //implements the InvocationHandler interface
        public Object invoke(Object proxy, Method method, Object[] args) 
            throws Throwable {

            if ( method.equals(s_iteratorMethod) ) {
                return new IteratorImpl
                    (m_proxiedList.iterator(), m_mods);
            }

            if (isMutator(method)) {
                m_mods++;
                m_lastModification = new StackTrace();
            }
            return method.invoke(m_proxiedList, args);
        }


        private static Method getMethod(String name, Class[] args) {
            try {
                return List.class.getDeclaredMethod(name, args);
            } catch (NoSuchMethodException ex) {
                throw new IllegalStateException
                    ("no " + name + "?" + Arrays.asList(args));
            }
        }

        private static void registerMutator(String name, Class[] args) {
            s_mutators.add(getMethod(name, args));
        }

        private static boolean isMutator(Method method) {
            return s_mutators.contains(method);
        }

        /**
         * This interface is short enough to implement by hand
         **/
        private class IteratorImpl implements Iterator {
            private final Iterator m_proxiedIterator;
            private final int m_mods;
            private final StackTrace m_trace;

            IteratorImpl(Iterator iter, int mods) {
                m_proxiedIterator = iter;
                m_mods = mods;
                m_trace = new StackTrace();
            }

            public boolean hasNext() {
                return m_proxiedIterator.hasNext();
            }

            private void logComodifications() {
                if ( m_mods != ListHandler.this.m_mods ) {
                    s_log.error("Concurrent modification detected. " +
                                "The list was created at", m_created);
                    s_log.error("The iterator was created at", m_trace);
                    if ( m_lastModification == null ) {
                        s_log.error("stack trace gone AWOL");
                    } else {
                        s_log.error("Comodification occurred at",
                                    m_lastModification);
                    }
                }
            }

            public Object next() {
                logComodifications();
                return m_proxiedIterator.next();
            }

            public void remove() {
                logComodifications();
                m_proxiedIterator.remove();
            }
        }
    }
}
