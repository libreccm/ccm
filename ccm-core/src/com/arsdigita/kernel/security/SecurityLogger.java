/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.kernel.security;

import com.arsdigita.dispatcher.DispatcherHelper;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

/**
 *  Logs security-relevant events to a
 * distinguished log category.
 *
 * @author Sameer Ajmani
 **/
public class SecurityLogger {

    private static final Logger s_log = Logger.getLogger("SECURITY");

    private SecurityLogger() {}

    private static final DateFormat DATE_FORMAT =
        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private static StringBuffer appendTimestamp(StringBuffer buf) {
        return buf.append(DATE_FORMAT.format(new Date()));
    }


    private static StringBuffer appendIP(StringBuffer buf) {
        HttpServletRequest req = DispatcherHelper.getRequest();
        if (req == null) {
            return buf.append("no-IP-available");
        }
        return buf.append(req.getRemoteAddr());
    }

    public static void log(Priority p, Object message, Throwable t) {
        StringBuffer buf = new StringBuffer();
        appendTimestamp(buf).append(' ');
        appendIP(buf).append(' ').append(message.toString());
        s_log.log(p, buf.toString(), t);
    }

    public static void log(Priority p, Object message) {
        StringBuffer buf = new StringBuffer();
        appendTimestamp(buf).append(' ');
        appendIP(buf).append(' ').append(message.toString());
        s_log.log(p, buf.toString());
    }


    public static void debug(Object message) {
        log(Priority.DEBUG, message, null);
    }
    public static void debug(Object message, Throwable t) {
        log(Priority.DEBUG, message, t);
    }
    public static void info(Object message) {
        log(Priority.INFO, message, null);
    }
    public static void info(Object message, Throwable t) {
        log(Priority.INFO, message, t);
    }
    public static void warn(Object message) {
        log(Priority.WARN, message, null);
    }
    public static void warn(Object message, Throwable t) {
        log(Priority.WARN, message, t);
    }
    public static void error(Object message) {
        log(Priority.ERROR, message, null);
    }
    public static void error(Object message, Throwable t) {
        log(Priority.ERROR, message, t);
    }
    public static void fatal(Object message) {
        log(Priority.FATAL, message, null);
    }
    public static void fatal(Object message, Throwable t) {
        log(Priority.FATAL, message, t);
    }
}
