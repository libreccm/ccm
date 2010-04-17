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
package com.arsdigita.search;

import com.arsdigita.tools.junit.extensions.BaseTestSetup;
import com.arsdigita.tools.junit.extensions.CoreTestSetup;
import com.arsdigita.tools.junit.framework.PackageTestSuite;
import junit.framework.Test;

/**
 * SearchSuite
 *
 * @author Daniel Berrange
 * @version $Id: SearchSuite.java 287 2005-02-22 00:29:02Z sskracic $
 **/

public class SearchSuite extends PackageTestSuite {

    private static class TestIndexerType extends IndexerType {
        TestIndexerType() {
            super("test",
                  new ContentType[] {ContentType.TEXT},
                  new TestDocumentObserver());
        }
    }
    public static final IndexerType TEST_INDEXER = new TestIndexerType();
    
    public SearchSuite() {}

    public SearchSuite(Class theClass) {
        super(theClass);
    }

    public SearchSuite(String name) {
        super(name);
    }

    public static Test suite() {
        SearchSuite suite = new SearchSuite();
        populateSuite(suite);
        BaseTestSetup wrapper = new CoreTestSetup(suite);
        wrapper.addSQLSetupScript("/com/arsdigita/search/setup.sql");
        wrapper.addSQLTeardownScript("/com/arsdigita/search/teardown.sql");
        return wrapper;
    }

    public static void main(String[] args) throws Exception {
        junit.textui.TestRunner.run(suite());
    }

}
