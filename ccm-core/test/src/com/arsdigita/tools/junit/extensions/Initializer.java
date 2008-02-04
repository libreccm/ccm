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
package com.arsdigita.tools.junit.extensions;

import com.arsdigita.initializer.InitializationException;
import com.arsdigita.initializer.Startup;
import java.util.Collection;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 *  Initializer
 *
 * @author Dennis Gregorovic
 * @version $Revision: #10 $ $Date: 2004/08/16 $
 */

public class Initializer {

    private static Startup s_startup;
    private static String  s_webAppRoot;
    private static Collection s_initializersRun;


    /**
     *  This method reads a given test init script, and runs all of the Initializers
     *  defined in it upto and including iniName
     *
     *  @param suite  Any errors are added as failing tests to the TestSuite.
     *  @param scriptName Name of the initializer script. If null, the system property
     *          test.initscript will be used.
     *  @iniName The name of the last Initializer in scriptName to be run.
     */
    public static void startup(TestSuite suite,
                               String scriptName,
                               String iniName) {
        if (scriptName == null) {
            scriptName = System.getProperty("test.initscript");
            if (scriptName == null) {
                reportWarning (suite, "Property test.initscript not set. This " +
                               "property is defined in your ant.properties file. It " +
                               "should be set to the full path of a valid init script " +
                               "(e.g. enterprise.init)");
                return;
            }
        }
        s_webAppRoot = System.getProperty("test.webapp.dir");

        System.out.println ("starting initializers " + scriptName +
                            " ; iniName: " + iniName + " webapp dir: " + s_webAppRoot);

        try {
            s_startup = new Startup(s_webAppRoot, scriptName);
            s_startup.setLastInitializer (iniName);
            s_initializersRun = s_startup.init();
        } catch (InitializationException e) {
            reportWarning (suite, "Initialization failed with message: " +
                           e.getMessage());
        }

    }


    /**
     *
     * @param name The name of the initializer
     * @return True if the initialzier was run
     */
    public static boolean wasInitializerRun(String name) {
        final boolean wasRun = null != s_initializersRun && s_initializersRun.contains(name);
        return wasRun;
    }

    protected static void startup(TestSuite suite, String scriptName) {
        startup (suite, scriptName, null);
    }

    protected static void startup(TestSuite suite) {
        startup (suite, null);
    }

    protected static void shutdown() {
        System.out.println ("stopping initializers");
        s_initializersRun = null;
        if (s_startup != null) {
            try {
                s_startup.destroy();
            } catch (InitializationException e) { }
        }
    }

    private static void reportWarning (TestSuite suite, final String message) {
        System.err.println (message);
        suite.addTest( warning (message));
    }

    private static Test warning(final String message) {
        return new TestCase("warning") {
                protected void runTest() {
                    fail(message);
                }
            };
    }
}
