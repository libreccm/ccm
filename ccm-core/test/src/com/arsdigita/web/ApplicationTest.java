/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.web;

/**
 * Class ApplicationTest
 *
 * @author jross@redhat.com
 */
public class ApplicationTest extends WebTestCase {
    public ApplicationTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();

        ApplicationDispatchTest.setupApplication();
   }

    public void testRetrieveAll() throws Exception {
        // This tests for a problem described in bz 113122

        final long before = Application.retrieveAllApplications().size();

        final Application app1 = Application.createApplication
            (TestApplication.BASE_DATA_OBJECT_TYPE, "yeehah", "Yee Hah", null);

        final Application app2 = Application.createApplication
            (TestApplication.BASE_DATA_OBJECT_TYPE, "waahoo", "Waa Hoo", null);

        // Application.retrieveAllApplications() should *not* return
        // this app, since I'm making it look like a portlet.
        app2.getApplicationType().setFullPageView(false);

        final long after = Application.retrieveAllApplications().size();

        if (after - before > 1) {
            fail("Found a non-application in a set returned by " +
                 "Application.retrieveAllApplications()");
        }
    }
}
