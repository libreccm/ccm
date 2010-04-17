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

import java.util.Arrays;
import java.util.HashMap;
import org.apache.log4j.Logger;

/**
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: Tracer.java 738 2005-09-01 12:36:52Z sskracic $
 */
public final class Tracer {

    private static final Logger s_log = Logger.getLogger(Tracer.class);

    private Logger m_log;
    private int m_level;
    private HashMap m_starts;

    public Tracer(final Logger log) {
        m_log = log;
        m_level = 0;
        m_starts = new HashMap();
    }

    public Tracer(final String category) {
        this(Logger.getLogger(category));
    }

    public Tracer(final Class clacc) {
        this(clacc.getName() + ".trace");
    }

    public final boolean isEnabled() {
        return m_log.isDebugEnabled();
    }

    public final void enter(final String method) {
        if (isEnabled()) enter(method, new Object[] {});
    }

    public final void enter(final String method,
                            final Object arg1) {
        if (isEnabled()) enter(method, new Object[] {arg1});
    }

    public final void enter(final String method,
                            final Object arg1,
                            final Object arg2) {
        if (isEnabled()) enter(method, new Object[] {arg1, arg2});
    }

    public final void enter(final String method,
                            final Object arg1,
                            final Object arg2,
                            final Object arg3) {
        if (isEnabled()) enter(method, new Object[] {arg1, arg2, arg3});
    }

    public final void enter(final String method, final Object[] args) {
        if (isEnabled()) {
            m_level++;

            final StringBuffer buffer = buffer();

            buffer.append(method);

            m_starts.put(buffer.toString(),
                         new Long(System.currentTimeMillis()));

            buffer.append(" ");
            buffer.append(Arrays.asList(args));

            m_log.debug(buffer.toString());
        }
    }

    public final void exit(final String method) {
        if (isEnabled()) exit(method, null);
    }

    public final void exit(final String method, final Object result) {
        if (isEnabled()) {
            final StringBuffer buffer = buffer();

            buffer.append(method);

            final long start = ((Long) m_starts.get
                                    (buffer.toString())).longValue();
            final long end = System.currentTimeMillis();

            if (result != null) {
                buffer.append(" -> ");
                buffer.append(result);
            }

            buffer.append(" (");
            buffer.append((end - start));
            buffer.append(" millis)");

            m_log.debug(buffer.toString());

            m_level--;
        }
    }

    private StringBuffer buffer() {
        final StringBuffer buffer = new StringBuffer();

        for (int i = 0; i < m_level; i++) {
            buffer.append("  ");
        }

        return buffer;
    }
}
