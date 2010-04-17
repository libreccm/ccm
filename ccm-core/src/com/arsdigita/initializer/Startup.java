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

import com.arsdigita.util.ResourceManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.Set;

/**
 * Convenience class designed to run initializers. Either manually specify the
 * values for web app root and script name, or set java properties with names
 * WEB_APP_ROOT or SCRIPT_NAME.
 *
 * @author Michael Bryzek
 * @author Dennis Gregorovic
 * @version $Id: Startup.java 738 2005-09-01 12:36:52Z sskracic $
 * @since ACS 4.7
 *
 **/
public class Startup {

    /** The name of the property containing the web app root **/
    public static final String WEB_APP_ROOT = "webAppRoot";

    /** The name of the property containing the script name **/
    public static final String SCRIPT_NAME = "scriptName";

    private String m_webAppRoot;
    private String m_scriptName;
    private String m_lastInitializer;
    private Script m_ini;

    /**
     * Sets up environment variables. Example:
     *
     *<pre>
     * String scriptName = "/WEB-INF/resources/enterprise.init";
     * String webAppRoot =
     *    "/usr/local/jakarta-tomcat-3.2.3/webapps/enterprise";
     *
     *  Startup startup = new Startup(webAppRoot, scriptName);
     *  startup.init();
     *</pre>
     *
     * @param webAppRoot The web app root to use (e.g. $TOMCAT_HOME/webapps/enterprise);
     * @param scriptName The relative (from web app root) path to the
     * script that defines the initializers
     * (e.g. /WEB-INF/resources/enterprise.init)
     *
     **/
    public Startup(String webAppRoot, String scriptName) {
        m_webAppRoot = webAppRoot;
        m_scriptName = scriptName;
    }


    /**
     * Wrapper for {@link #Startup(String, String)} which looks for
     * the system properties named WEB_APP_ROOT and SCRIPT_NAME.
     *
     * @exception InitializationException If we cannot find either property.
     **/
    public Startup() throws InitializationException {
        this(getProperty(WEB_APP_ROOT), getProperty(SCRIPT_NAME));
    }


    /**
     * Sets the name of the last initializer to run. If not set, all the
     * initializers will run.
     *
     * @param lastInitializer The name of the last initializer to run
     * (e.g. com.arsdigita.persistence.Initializer)
     **/
    public void setLastInitializer(String lastInitializer) {
        m_lastInitializer = lastInitializer;
    }


    /**
     * Starts up the web environment for the ACS.
     *
     * @return Collection of the names of all initializers run.
     **/
    public Set init() throws InitializationException {
        ResourceManager rm = ResourceManager.getInstance();
        rm.setWebappRoot(new File(m_webAppRoot));

        Reader r;
        try {
            r = new FileReader(m_scriptName);
        } catch (FileNotFoundException e) {
            throw new InitializationException("Couldn't find " + m_scriptName);
        }

        Set initializersRun = null;
        try {
            if (m_lastInitializer == null) {
                m_ini = new Script(r);
                initializersRun = m_ini.startup();
            } else {
                m_ini = new Script(r, m_lastInitializer);
                initializersRun = m_ini.startup(m_lastInitializer);
            }
        } catch (InitializationException e) {
            e.printStackTrace(System.err);
            throw new InitializationException
                ("Error loading init script: " + e.getMessage());
        }

        return initializersRun;
    }


    /**
     * Shut down the startup script.
     **/
    public void destroy() {
        m_ini.shutdown();
    }

    /**
     * Helper method to retrieve the specified property or throw an
     * exception if the property doesn't exist or if the property was
     * the empty string.
     **/
    private static String getProperty(String propertyName) throws InitializationException {
        String property = System.getProperty(propertyName);
        if (property == null || property.trim().length() == 0) {
            throw new InitializationException
                ("The " + propertyName + " system property could not be " +
                 "found or was empty");
        }
        return property;
    }
}
