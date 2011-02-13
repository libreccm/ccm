/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.logging;

import com.arsdigita.util.Assert;
import com.arsdigita.util.Exceptions;
import com.arsdigita.util.JavaPropertyReader;
import com.arsdigita.util.parameter.ErrorList;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.StringParameter;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.Collator;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

import org.apache.log4j.Logger;

/**
 * Base class for handling error reports. Typical usage is:
 *
 * try {
 *   // something that goes wrong
 * } catch (Exception ex) {
 *    ErrorReport report = new ErrorReport(ex);
 *    report.logit();
 *    throw ex;
 * }
 *
 *
 * @author Dan Berrange
 * @see <a href="http://www.tuxedo.org/~esr/jargon/html/entry/guru-meditation.html">
 *         http://www.tuxedo.org/~esr/jargon/html/entry/guru-meditation.html</a>
 * @version $Id: ErrorReport.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class ErrorReport {

    // We generate ACS Error Reports in homage of Guru Meditation.

    private static final Logger s_log = Logger.getLogger(ErrorReport.class);

    static {
        s_log.debug("Static initalizer starting...");
        final JavaPropertyReader reader = new JavaPropertyReader
            (System.getProperties());

        final Parameter param = new StringParameter
            ("waf.logging.error_report_dir", Parameter.OPTIONAL, null);

        final ErrorList errors = new ErrorList();
        final String dir = (String) param.read(reader, errors);
        errors.check();

        if (dir != null) {
            ErrorReport.initializeAppender(dir);
        }
        s_log.debug("Static initalizer finished.");
    }

    public static void initializeAppender(String directory) {
        Assert.exists(directory, "directory is null");

        File file = new File(directory);
        if (!file.exists()) {
            file.mkdir();
        }

        ErrorReportAppender appender = new ErrorReportAppender(directory);
        s_log.addAppender(appender);
    }

    private Throwable[] m_throwables;
    private String m_guruMeditationCode;
    private HashMap m_sections;

    public ErrorReport(Throwable throwable) {
        try {
            m_throwables = Exceptions.unwrap(throwable);
        } catch (Exception e) {
            s_log.error("Cannot unwrap throwable: ", e);
            m_throwables = new Throwable[] { throwable };
        }

        m_sections = new HashMap();

        addSystemProperties();
        addStackTrace();
    }

    public String getHostname() {
        InetAddress localhost = null;
        try {
            localhost = InetAddress.getLocalHost();
        } catch (UnknownHostException ex) {
            // nada
        }
        return (localhost == null ? "localhost" : localhost.getHostAddress());
    }

    /**
     * Returns the globally unique error code for this
     * error report. This can be used to identity the
     * entry in the log files upon recieving feedback
     * from the end user.
     */
    public String getGuruMeditationCode() {
        // This had better be unique...
        if (m_guruMeditationCode == null) {
            m_guruMeditationCode = getHostname() + ":" +
                Integer.toHexString(Thread.currentThread().hashCode()) + ":" +
                Long.toHexString(System.currentTimeMillis());
        }
        return m_guruMeditationCode;
    }

    public void addSection(String name,
                           Collection lines) {
        m_sections.put(name, lines);
    }

    public void addSection(String name,
                           String lines[]) {
        m_sections.put(name, lines);
    }

    public void addSection(String name,
                           String line) {
        m_sections.put(name, line);
    }

    public String getReport() {
        StringBuffer log = new StringBuffer("\n");

        log.append("-*-*-*-*-*- Begin Error Report -*-*-*-*-*-\n");
        log.append("-*-*-*- ACS Error Report Code: "
                   + getGuruMeditationCode() + " -*-*-*-\n");

        for (int i = 0 ; i < m_throwables.length ; i++) {
            log.append("-*-*-*- Message " + (i+1) + ": " + 
                       m_throwables[i].getClass().getName() + ": " + 
                       m_throwables[i].getMessage() + " -*-*-*-\n");
        }

        Iterator sections = m_sections.keySet().iterator();
        while (sections.hasNext()) {
            String key = (String)sections.next();
            Object val = m_sections.get(key);

            log.append("\n-*-*-*- Section: " + key + " -*-*-*-\n");
            if (val instanceof Collection) {
                Iterator lines = ((Collection)val).iterator();
                while (lines.hasNext()) {
                    Object line = (String)lines.next();
                    log.append(line.toString() + "\n");
                }
            } else if (val instanceof String) {
                String line = (String)val;
                log.append(line + "\n");
            } else if (val instanceof String[]) {
                String lines[] = (String[])val;
                for (int i = 0 ; i < lines.length ; i++) {
                    log.append(lines[i] + "\n");
                }
            }
        }

        log.append("-*-*-*-*-*- End Error Report -*-*-*-*-*-\n");

        return log.toString();
    }

    public String toString() {
        return getReport();
    }

    /**
     * Writes a nicely formatted error report into the
     * log file.
     */
    public void logit() {
        s_log.fatal(this);
    }


    private void addStackTrace() {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);

        m_throwables[m_throwables.length-1].printStackTrace(printWriter);
        
        addSection("Stack trace", stringWriter.toString());
    }


    private void addSystemProperties() {
        TreeSet data = new TreeSet(Collator.getInstance());

        Enumeration props = System.getProperties().keys();
        while (props.hasMoreElements()) {
            String key = (String)props.nextElement();
            String value = System.getProperty(key);
            data.add(key + ": " + value);
        }

        addSection("System properties", data);
    }
}
