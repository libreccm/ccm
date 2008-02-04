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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.jdom.Element;

public class XMLResult extends Element  {
    protected XMLResult() {
        super("testsuite");
    }


    public String getSuiteName() {
        final String name = getAttributeValue("name");
        return name;
    }

    public int getTestCount() {
        final String count = getAttributeValue("tests");
        return Integer.parseInt(count);
    }

    public int getFailureCount() {
        final String count = getAttributeValue("failures");
        return Integer.parseInt(count);
    }

    public int getErrorCount() {
        final String count = getAttributeValue("errors");
        return Integer.parseInt(count);
    }

    public String getChangelist() {
        final String changelist = getAttributeValue("changelist");
        return changelist;
    }
    public boolean hasTest(String name) {
        return getIndex().containsKey(name);
    }

    public void setChangelist(String changelist) {
        setAttribute("changelist", changelist);
    }

    public XMLTestCase getTestCase(String name) {
        return (XMLTestCase) m_index.get(name);
    }

    private Map getIndex() {
        if (null == m_index) {
            m_index = new HashMap();
            List testcases = getChildren("testcase");
            for (Iterator iterator = testcases.iterator(); iterator.hasNext();) {
                XMLTestCase test = (XMLTestCase) iterator.next();
                final String name = test.getAttributeValue("name");
                m_index.put(name, test);

            }
        }

        return m_index;
    }

    private Map m_index = null;
}
