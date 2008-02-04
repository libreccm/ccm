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
package com.arsdigita.xmlutil;

import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.Namespace;

public class TestSet extends Element {
    public static final String NAME = "test_set";
    private static Logger s_log = Logger.getLogger(TestSet.class);

    public TestSet() {
        this( NAME, Namespaces.TEST );
    }
    public TestSet(String name) {
        super(NAME, Namespaces.TEST);
    }
    public TestSet(String name, Namespace ns) {
        super(NAME, Namespaces.TEST);
    }
    public TestSet(String name, String uri) {
        super(NAME, Namespaces.TEST);
    }
    public TestSet(String name, String prefix, String uri) {
        super(NAME, Namespaces.TEST);
    }

    public void runTests() throws Exception {
        List testDefs = this.getChildren("test_def", Namespaces.TEST);
        s_log.warn("Test set has " + testDefs.size() + " elements");
        for (Iterator iterator = testDefs.iterator(); iterator.hasNext();) {
            TestDefinition def = (TestDefinition) iterator.next();
            def.runTest();
        }
     }



}
