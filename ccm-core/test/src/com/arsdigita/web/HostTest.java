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
package com.arsdigita.web;

import com.arsdigita.tools.junit.framework.BaseTestCase;

public class HostTest extends BaseTestCase {

    public static final String TEST_HOST1 = "test-app01.example.com";
    public static final String TEST_HOST2 = "test-app02.example.com";

    public HostTest(String name) {
        super(name);
    }

    public void testHosts() throws Exception {
        // Create a pair of hosts to test with
        Host host1 = Host.create(TEST_HOST1, 80);
        Host host2 = Host.create(TEST_HOST2, 8080);

        WebConfigExposer.setHosts(new Host[] { host1, host2 });
        WebConfigExposer.setCurrentHost(host1);

        // toString() should not include the port if it is 80
        assertEquals(TEST_HOST1, host1.toString());
        assertEquals(TEST_HOST2 + ":8080", host2.toString());
    }
}
