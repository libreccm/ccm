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
package com.arsdigita.initializer;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import java.io.StringReader;
import java.io.Reader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Script
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Id: Script.java 738 2005-09-01 12:36:52Z sskracic $
 * @version $Id: Script.java 738 2005-09-01 12:36:52Z sskracic $
 */

public class Script {

    private static final Logger s_log =
        Logger.getLogger(Script.class);

    private Map m_initMap = new HashMap();
    private List m_initializers = new ArrayList();
    private String m_lastInitializerToRun;
    private boolean m_isStarted = false;
    private boolean m_isShutdown = false;

    /**
     * Constructs a new initialization script from the given string buffer.
     *
     * @param bs The script.
     **/

    public Script(StringBuffer bs) throws InitializationException {
        this(bs, null);
    }

    public Script(StringBuffer bs, String iniName) throws InitializationException {
        this(new StringReader(bs.toString()), iniName);
    }

    /**
     * Constructs a new initialization script from the given string.
     *
     * @param s The script.
     **/

    public Script(String s) throws InitializationException {
        this(s, null);
    }

    public Script(String s, String iniName) throws InitializationException {
        this(new StringReader(s), iniName);
    }

    /**
     * Constructs a new initialization script from the given input stream.
     *
     * @param is The script.
     **/

    public Script(InputStream is) throws InitializationException {
        this(is, null);
    }

    public Script(InputStream is, String iniName) throws InitializationException {
        this(new InputStreamReader(is), iniName);
    }

    /**
     * Constructs a new initialization script from the given reader.
     *
     * @param r The script.
     **/

    public Script(Reader r) throws InitializationException {
        this(r, null);
    }

    /**
     * Constructs a new Script
     *
     * @param r Reader for the script parser
     * @param iniName Name of the last initializer to run, or null.
     * Used to selectively run only part of the initialization script
     *
     * @throws InitializationException
     */
    public Script(Reader r, String iniName) throws InitializationException {
        m_lastInitializerToRun = iniName;
        ScriptParser sp = new ScriptParser(r);
        try {
            sp.parse(this);
        } catch (ParseException e) {
            // FIXME: what's the purpose of the errTok variable? I'm commenting
            // it out. -- 2002-11-26
            logInitializationFailure(null, false, e);
            // Token errTok = e.currentToken.next;
            throw new InitializationException(e.getMessage());
        } catch (InitializationException e) {
            logInitializationFailure(null, false, e);
            throw e;
        } catch (RuntimeException e) {
            logInitializationFailure(null, false, e);
            throw e;
        }
    }

    private Script(final Reader r, final boolean configOnly)
           throws InitializationException {
        final ScriptParser sp = new ScriptParser(r);
        sp.setConfigOnly(configOnly);
        try {
            sp.parse(this);
        } catch (ParseException e) {
            logInitializationFailure(null, false, e);
            throw new InitializationException(e.getMessage());
        }
    }

    public static final Script readConfig(final Reader reader)
            throws InitializationException {
        if (reader == null) throw new IllegalArgumentException();

        return new Script(reader, true);
    }

    /**
     * Adds an initializer to the script.
     *
     * @param ini The initializer.
     * @return true if the parser should continue adding initializers
     **/
    public boolean addInitializer(Initializer ini)
        throws InitializationException {
        if (m_isStarted) {
            throw new InitializationException(
                "This script has already been started."
            );
        }
        String initializerName = ini.getClass().getName();

        if (initializerName.equals(GenericInitializer.class.getName())) {
            initializerName = ini.toString();
        }

        m_initializers.add(ini);
        m_initMap.put(initializerName, ini);

        final boolean continueAddingInitializers =
            !initializerName.equals(m_lastInitializerToRun);

        return continueAddingInitializers;
    }

    public Initializer getInitializer(final String name) {
        return (Initializer) m_initMap.get(name);
    }

    /**
     * Returns all the initializers specified in this script.
     *
     * @return A list of initializers.
     **/

    public List getInitializers() {
        List result = new ArrayList();
        result.addAll(m_initializers);
        return result;
    }

    /**
     * Starts up all initializers that this script contains.
     **/

    public Set startup() throws InitializationException {
        return startup(null);
    }

    /**
     * Starts up the specified initializer and any initializers it requires in
     * order to start.
     *
     * @param iniName The name of the initializer last to start. Note:
     * This parameter is redundant, as if it is set in the constructor,
     * only initializers up to the final one will be parsed.
     *
     * @return A Collection containing the names of all initalizers run
     **/

    public Set startup(String iniName) throws InitializationException {
        HashSet initializersRun = new HashSet();
        Initializer ini = null;

        for (int i = 0; i < m_initializers.size(); i++) {
            ini = (Initializer) m_initializers.get(i);

            if (s_log.isInfoEnabled()) {
                s_log.info("Running initializer " +
                           ini.getClass().getName() +
                           " (" + (i + 1) + " of " + m_initializers.size() + ")");
            }

            final String name = ini.getClass().getName();

            ini.startup();
            initializersRun.add(ini);
            if (name.equals(iniName)) {
                break;
            }
        }

        s_log.info("Initialization Complete");
        return initializersRun;
    }

    /**
     * Shuts down all initializers that this script contains.
     **/

    public void shutdown() throws InitializationException {
        shutdown(null);
    }

    /**
     * Shuts down the specified initializer and any initializers it required
     * in order to start.
     *
     * @param iniName The name of the initializer to stop.
     **/

    public void shutdown(String iniName) throws InitializationException {
        if (m_isShutdown) {
            throw new InitializationException(
                "Shutdown has already been called."
            );
        }
        if (!m_isStarted) {
            throw new InitializationException(
                "Startup hasn't been called yet."
            );
        }

        boolean shutdown = false;
        if (iniName == null) {
            shutdown = true;
        }
        for (int i = m_initializers.size() - 1; i >= 0; i--) {
            Initializer ini = (Initializer) m_initializers.get(i);

            if (ini.getClass().getName().equals(iniName)) {
                shutdown = true;
            }

            if (shutdown) {
                ini.shutdown();
            }
        }
        m_isShutdown = true;
    }

    protected void finalize() throws Throwable {
        try {
            if (m_isStarted && !m_isShutdown) {
                try {
                    shutdown();
                } catch (Throwable t) {
                    s_log.error("Error in Script.finalize:");
                    t.printStackTrace(System.err);
                }
            }

        } finally {
            super.finalize();
        }


    }

    private void logInitializationFailure(Initializer initializer,
                                          final boolean loggerIsInitialized,
                                          Throwable t) {
        InitializerErrorReport report = new InitializerErrorReport(t, initializer);

        String msg = "Fatal error loading initialization script";
        if (!loggerIsInitialized) {
            BasicConfigurator.configure();
        }
        report.logit();
    }
}
