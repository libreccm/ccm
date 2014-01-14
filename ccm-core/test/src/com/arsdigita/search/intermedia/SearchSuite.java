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
package com.arsdigita.search.intermedia;

import com.arsdigita.search.Search;
import com.arsdigita.tools.junit.extensions.BaseTestSetup;
import com.arsdigita.tools.junit.extensions.CoreTestSetup;
import com.arsdigita.tools.junit.framework.PackageTestSuite;
import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.TestSuite;

/**
 * SearchSuite
 *
 * @author Daniel Berrange
 * @version 1.0
 */
public class SearchSuite extends PackageTestSuite {

    public SearchSuite() {
        super();
    }

    public SearchSuite(Class theClass) {
        super(theClass);
    }

    public SearchSuite(String name) {
        super(name);
    }

    public static Test suite() {
        SearchSuite suite = new SearchSuite();
        populateSuite(suite);
        BaseTestSetup wrapper = new SearchTestSetup(suite);
        wrapper.setSetupSQLScript( "/com/arsdigita/search/setup.sql");
        wrapper.setTeardownSQLScript( "/com/arsdigita/search/teardown.sql");
        return wrapper;
    }


    // Only want to run tests if Intermedia search is enabled.
    private static class SearchTestSetup extends CoreTestSetup {
        public SearchTestSetup(TestSuite suite) {
            super(suite);
        }

        public void basicRun(TestResult testResult) {
            if (Search.getConfig().isIntermediaEnabled()) {
                super.basicRun(testResult);
            }
        }

        protected void setupSQL () throws Exception {
            if (Search.getConfig().isIntermediaEnabled()) {
                super.setupSQL();
            }
        }


        protected void teardownSQL() throws Exception {
            if (Search.getConfig().isIntermediaEnabled()) {
                super.teardownSQL();
            }
        }

    }

    public static void main(String[] args) throws Exception {
        junit.textui.TestRunner.run( suite() );
    }
}
