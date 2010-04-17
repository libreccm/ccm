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
package com.arsdigita.search.lucene;

import com.arsdigita.search.Search;
import com.arsdigita.tools.junit.extensions.BaseTestSetup;
import com.arsdigita.tools.junit.extensions.CoreTestSetup;
import com.arsdigita.tools.junit.framework.PackageTestSuite;
import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import org.apache.log4j.Logger;

/**
 * LuceneSuite
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Id: LuceneSuite.java 746 2005-09-02 10:56:35Z sskracic $
 **/

public class LuceneSuite extends PackageTestSuite {

    private static final Logger s_log = Logger.getLogger(LuceneSuite.class);

    public LuceneSuite() {}

    public LuceneSuite(Class theClass) {
        super(theClass);
    }

    public LuceneSuite(String name) {
        super(name);
    }

    public static Test suite() {
        LuceneSuite suite = new LuceneSuite();
        populateSuite(suite);
        BaseTestSetup wrapper = new SearchTestSetup(suite);
        wrapper.addSQLSetupScript("/com/arsdigita/search/lucene/setup.sql");
        wrapper.addSQLTeardownScript("/com/arsdigita/search/lucene/teardown.sql");
        return wrapper;
    }

    private static class SearchTestSetup extends CoreTestSetup {
        private boolean m_isLuceneEnabled = false;
        public SearchTestSetup(TestSuite suite) {
            super(suite);

            if (Search.getConfig().isLuceneEnabled()) {
                if (Search.getConfig().getLazyUpdates()) {
                    s_log.warn("Lucene tests do not currently work correctly with lazy updates enabled. Tests disabled");

                } else {
                    m_isLuceneEnabled = true;
                }
            }
        }

        public void basicRun(TestResult testResult) {
            if (isLuceneEnabled()) {
                super.basicRun(testResult);
            }
        }

        private boolean isLuceneEnabled() {
            return m_isLuceneEnabled;
        }

        protected void setupSQL () throws Exception {
            if (isLuceneEnabled()) {
                super.setupSQL();
            }
        }


        protected void teardownSQL() throws Exception {
            if (isLuceneEnabled()) {
                super.teardownSQL();
            }
        }

    }

    public static void main(String[] args) throws Exception {
        junit.textui.TestRunner.run(suite());
    }

}
