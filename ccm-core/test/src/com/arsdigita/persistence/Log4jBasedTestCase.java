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
package com.arsdigita.persistence;

import com.arsdigita.logging.SecureLogFilter;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.log4j.Category;
import org.apache.log4j.Priority;
import org.apache.log4j.TTCCLayout;
import org.apache.log4j.WriterAppender;

/**
 * A base JUnit test class useful for writing junit test cases
 * that rely on Log4j.
 * Automatically adds/removes a new Log object that
 * will log to a member StringWriter variable,
 * turns on all debugging on setup, and restores
 * debugging to original values on teardown.
 *
 * Example code:
 * <pre>
 * StringMatchFilter filterHold = new StringMatchFilter();
 * String holdString = "connectionUserCountHitZero holding on to connection";
 * filterHold.setStringToMatch(holdString);
 * filterHold.setAcceptOnMatch(true);
 * log.addFilter(new DenyAllFilter());
 * ...
 * assertLogContains(holdString);
 * ...
 * clearLog();
 * ...
 * assertLogDoesNotContain(holdString);
 * ...
 * </pre>
 *
 * @author David Eison
 */
public class Log4jBasedTestCase extends PersistenceTestCase {

    public static final String versionId = "$Id: Log4jBasedTestCase.java 1837 2009-03-05 06:57:16Z terry $";

    /**
     * The log object.  Should be modified
     * by adding filters, and perhaps changing default priority.
     */
    protected WriterAppender log = null;

    /**
     * The StringWriter that log will log to
     */
    protected StringWriter logSW = null;

    HashMap originalPriorities = new HashMap();

    public Log4jBasedTestCase (String name) {
        super(name);
    }

    public void runBare() throws Throwable {
        try {
            logSetUp();
            super.runBare();
        } finally {
            logTearDown();
        }
    }

    /**
     * Turns on all logging so that the desired log messages will be found.
     * Adds a new logger, by default logging everything except for
     * secure messages to the member stringwriter variable.
     */
    public void logSetUp() throws Exception {
        // nuke all priorities back to DEBUG
        Category root = Category.getRoot();
        Enumeration e = root.getCurrentCategories();

        while (e.hasMoreElements()) {
            Category cat = (Category)e.nextElement();
            originalPriorities.put(cat, cat.getPriority());
            cat.setPriority(Priority.DEBUG);
        }

        root.info("All categories were temporarily set to DEBUG for a test");

        logSW = new StringWriter();
        log = new WriterAppender(new TTCCLayout(), logSW);
        log.addFilter(new SecureLogFilter());
        root.addAppender(log);
    }

    /**
     * Restores all logging & aggressive connection close setting to original state.
     */
    public void logTearDown() throws Exception {
        Category root = Category.getRoot();
        root.info("All categories will be restored to their original values");

        // restore all priorities
        Iterator it = originalPriorities.entrySet().iterator();
        while (it.hasNext()) {
            java.util.Map.Entry current = (java.util.Map.Entry)it.next();
            Category cat = (Category)current.getKey();
            Priority pri = (Priority)current.getValue();
            cat.setPriority(pri);
        }

        root.removeAppender(log);
        log.close();
    }

    /**
     * String of messages that have been logged since creation or
     * last clearLog.
     */
    public String getLogMessage() {
        return logSW.toString();
    }

    /**
     * Asserts that log contains given message
     * (case sensitive, exact match).
     */
    public void assertLogContains(String msg) {
        assertTrue("Log messages should contain " + msg +
                   " but did not.  Log was:" + Utilities.LINE_BREAK +
                   "'" + getLogMessage() + "'",
                   getLogMessage().indexOf(msg) >= 0);
    }

    /**
     * Asserts that log does not contain given message
     * (case sensitive, exact match).
     */
    public void assertLogDoesNotContain(String msg) {
        assertTrue("Log messages should not contain " + msg +
                   " but did.  Log was:" + Utilities.LINE_BREAK +
                   "'" + getLogMessage() + "'",
                   getLogMessage().indexOf(msg) < 0);
    }

    /**
     * Clears the log's messages.
     * Existing filters and priorities remain intact.
     */
    public void clearLog() {
        logSW.getBuffer().setLength(0);
        assertTrue("Log should be empty after reset, instead contained:" +
                   Utilities.LINE_BREAK + "'" + getLogMessage() + "'",
                   getLogMessage().length() == 0);
    }
}
