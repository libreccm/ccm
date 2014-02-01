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
package com.arsdigita.initializer;

import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;

/**
 * ScriptTest
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #8 $ $Date: 2004/08/16 $
 */

public class ScriptTest extends TestCase {

    

    public ScriptTest(String name) {
        super(name);
    }

    public void testParser() {
        try {
            Script s = new Script(
                                  "init com.arsdigita.initializer.FooInitializer {\n" +
                                  "  stringParam = \"string\";\n" +
                                  "  objectParam = 3;\n" +
                                  "  listParam = {1, 2, 3, {4, 5, 6}};\n" +
                                  "}"
                                  );

            List inis = s.getInitializers();
            assertEquals("There should be exactly one " +
                         "initializer in the script.",
                         1, inis.size());
            for (int i = 0; i < inis.size(); i++) {
                Initializer ini = (Initializer) inis.get(i);
                Configuration conf = ini.getConfiguration();
                assertEquals("stringParam wasn't set properly",
                             "string", conf.getParameter("stringParam"));
                assertEquals("objectParam wasn't set properly",
                             new Integer(3),
                             conf.getParameter("objectParam"));
                List l = new ArrayList();
                l.add(new Integer(1));
                l.add(new Integer(2));
                l.add(new Integer(3));
                List subl = new ArrayList();
                l.add(subl);
                subl.add(new Integer(4));
                subl.add(new Integer(5));
                subl.add(new Integer(6));
                assertEquals("listParam wasn't set properly",
                             l, conf.getParameter("listParam"));
            }
        } catch (InitializationException e) {
            fail(e.getMessage());
        }
    }

    public void testStartupAndShutdown() {
        try {
            Script s = new Script(
                                  "init com.arsdigita.initializer.FooInitializer {}"
                                  );

            assertTrue("FooInitializer should start out as not started",
                   !FooInitializer.isStarted());

            s.startup();

            assertTrue("FooInitializer wasn't started",
                   FooInitializer.isStarted());

            s.shutdown();

            assertTrue("FooInitializer wasn't shutdown",
                   !FooInitializer.isStarted());
        } catch (InitializationException e) {
            fail(e.getMessage());
        }
    }

}
