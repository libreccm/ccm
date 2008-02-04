/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.tools.junit.results;

import org.jdom.Element;

public class XMLTestCase extends Element {
    protected XMLTestCase() {
        super("testcase");
    }

    boolean passed() {
        final boolean passed = !(getFailure() == null || getError() == null);
        return passed;
    }

    Element getFailure() {
        Element failure = getChild("failure");
        return failure;
    }

    Element getError() {
        Element error = getChild("error");
        return error;
    }

    String getTestName() {
        String testName = getAttributeValue("name");
        return testName;
    }

}
