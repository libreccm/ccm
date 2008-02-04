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
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * You can use this class for capturing and logging timestamped stack traces.
 * In addition to the actual stack trace, instances of this class "remember" the
 * name of the thread in which they were instantiated.
 *
 * @author  Vadim Nasardinov (vadimn@redhat.com)
 * @since   2003-12-22
 * @version $Id: StackTrace.java 287 2005-02-22 00:29:02Z sskracic $
 **/
public final class StackTrace extends Throwable {
    private final static DateFormat FORMATTER =
        new SimpleDateFormat("HH:mm:ss.S");

    private final String m_thread = Thread.currentThread().getName();
    private final long m_time = System.currentTimeMillis();

    /**
     * Returns both the {@link #getThreadName() thread name} and the {@link
     * #getTimestamp() timestamp}.
     **/
    public String getMessage() {
        StringBuffer sb = new StringBuffer();
        sb.append("[thread=").append(getThreadName());
        sb.append("; timestamp=");
        sb.append(FORMATTER.format(getTimestamp())).append("]");
        return sb.toString();
    }

    public String getThreadName() {
        return m_thread;
    }

    public Date getTimestamp() {
        return new Date(m_time);
    }
}
