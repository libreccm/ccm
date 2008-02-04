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
package com.arsdigita.test.ddl;

import java.io.IOException;
import java.io.Reader;
import junit.framework.TestCase;

/**
 * DDLDiffTest
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #8 $ $Date: 2004/08/16 $
 **/

public class DDLDiffTest extends TestCase {

    public final static String versionId = "$Id: DDLDiffTest.java 750 2005-09-02 12:38:44Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    public DDLDiffTest(String name) {
        super(name);
    }

    public void test() throws IOException, InterruptedException {
/*        Runtime run = Runtime.getRuntime();
        Process diff =
            run.exec("diff -udrbB " +
                     "test/src/com/arsdigita/test/ddl/canonical " +
                     new File(new File(System.getProperty("build.sql.dir")),
                              "ddl"));
        diff.waitFor();
        String error = read(new InputStreamReader(diff.getErrorStream()));
        String output = read(new InputStreamReader(diff.getInputStream()));
        assertEquals("", error);
        assertEquals("", output);
        */
    }

    private static final String read(Reader rdr) throws IOException {
        StringBuffer result = new StringBuffer();

        char[] buf = new char[1024];

        int read;

        do {
            read = rdr.read(buf);
            if (read > -1) {
                result.append(buf, 0, read);
            }
        } while (read > -1);

        return result.toString();
    }

}
