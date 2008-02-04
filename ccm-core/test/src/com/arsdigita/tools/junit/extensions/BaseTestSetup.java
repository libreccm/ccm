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

import com.arsdigita.db.DbHelper;
import com.arsdigita.installer.LoadSQLPlusScript;
import com.arsdigita.runtime.RuntimeConfig;
import com.arsdigita.util.DummyServletContext;
import com.arsdigita.util.ResourceManager;
import com.arsdigita.util.jdbc.Connections;
import java.io.File;
import java.sql.Connection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import junit.extensions.TestDecorator;
import junit.framework.Protectable;
import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.TestSuite;

/**
 * A Decorator to set up and tear down additional fixture state.
 * Subclass BaseTestSetup and insert it into your tests when you want
 * to set up additional state once before the tests are run.
 */
public class BaseTestSetup extends TestDecorator {

    private TestSuite m_suite;
    private Set m_initializers = new HashSet();

    private List m_setupSQLScripts = new LinkedList();
    private List m_teardownSQLScripts = new LinkedList();

    public BaseTestSetup(Test test, TestSuite suite) {
        super(test);

        m_suite = suite;
    }

    public BaseTestSetup(TestSuite suite) {
        this(suite, suite);
    }

    public void run(final TestResult result) {
        final Protectable p = new Protectable() {
            public void protect() throws Exception {
                setUp();
                basicRun(result);
                tearDown();
            }
        };

        result.runProtected(this, p);
    }

    public void addSQLSetupScript(String setupSQLScript) {
        m_setupSQLScripts.add(setupSQLScript);
    }

    public void addSQLTeardownScript(String teardown) {
        m_teardownSQLScripts.add(teardown);
    }

    /**
     * Sets up the fixture. Override to set up additional fixture
     * state.
     */
    protected void setUp() throws Exception {
        if (m_suite.testCount() > 0) {
            TestStartup testStartup = new TestStartup(m_initializers);
            testStartup.run();

            ResourceManager.getInstance().setServletContext(new DummyServletContext());

            setupSQL();
        }
    }

    /**
     * Tears down the fixture. Override to tear down the additional
     * fixture state.
     */
    protected void tearDown() throws Exception {
        if (m_suite.testCount() > 0) {
            teardownSQL ();
        }
    }

    protected void setupSQL() throws Exception {
        if (m_setupSQLScripts.size() > 0) {
            runScripts(m_setupSQLScripts);
        }
    }


    protected void teardownSQL() throws Exception {
        if (m_teardownSQLScripts.size() > 0) {
            runScripts(m_teardownSQLScripts);
        }
    }

    private void runScripts(final List scripts) throws Exception {
        LoadSQLPlusScript loader = new LoadSQLPlusScript();
        Connection conn = Connections.acquire
	    (RuntimeConfig.getConfig().getJDBCURL());

        loader.setConnection(conn);

        for (Iterator iterator = scripts.iterator(); iterator.hasNext(); ) {
            final String script = (String) iterator.next();
            loader.loadSQLPlusScript(resolveScript(script));
        }

        conn.commit();
        conn.close();
    }

    private String resolveScript(final String script) {
        String sqldir = System.getProperty("test.sql.dir");
        File filename = new File(sqldir + script);
        if (filename.exists() && filename.isFile()) {
            return filename.toString();
        }
        filename = new File(sqldir + File.separator + DbHelper.getDatabaseDirectory() + script);
        if (filename.exists() && filename.isFile()) {
            return filename.toString();
        }
        filename = new File(sqldir + File.separator + "default" + script);
        if (filename.exists() && filename.isFile()) {
            return filename.toString();
        }
        return null;
    }

    public void addRequiredInitializer(final String initName) {
        m_initializers.add(initName);
    }

    public void setSetupSQLScript(String setupSQLScript) {
        m_setupSQLScripts.add(setupSQLScript);
    }

    public void setTeardownSQLScript(String teardownSQLScript) {
        m_teardownSQLScripts.add(teardownSQLScript);
    }
}
