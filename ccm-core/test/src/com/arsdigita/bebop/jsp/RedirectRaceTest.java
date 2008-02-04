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
package com.arsdigita.bebop.jsp;

import com.arsdigita.test.HttpUnitTestCase;
import com.meterware.httpunit.WebResponse;

/**
 * Affirmative-validation test to make sure that /bebop-demo
 * is set up and works as advertised.   May break if stylesheets
 * are changed.
 *
 *
 * @author Bill Schneider
 * @version 1.0
 *
 */

public class RedirectRaceTest extends HttpUnitTestCase {

    public RedirectRaceTest(String name) {
        super(name);
    }

    public void testRace() throws Exception {
        String random = Long.toString(System.currentTimeMillis() % 1000);
        WebResponse resp = getResponse("/bebop-jsp/redirect-race-1.jsp?random="
                                       + random);
        // now we have the response
        assertTrue(resp.getText().indexOf(random) >= 0);
    }

}
