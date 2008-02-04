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

import java.util.Iterator;
import java.util.List;
import org.jdom.Element;

public class MissingTests extends Element {
    public static final String NAME = "missing_tests";

    protected MissingTests(XMLResult previous, XMLResult current) {
        super(NAME);
        List tests = previous.getChildren("testcase");
        for (Iterator iterator = tests.iterator(); iterator.hasNext();) {
            XMLTestCase test = (XMLTestCase) iterator.next();
            if (!current.hasTest(test.getTestName())) {
                Element missing = new Element("missing");
                missing.setAttribute("name", test.getTestName());
                addContent(missing);
            }
        }
    }

    int missingTestCount() {
        List missing =  getChildren("missing");
        final int count = (missing == null) ? 0 : missing.size();
        return count;
    }

}
