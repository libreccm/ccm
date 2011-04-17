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
package com.arsdigita.docmgr;

import com.arsdigita.xmlutil.XMLTestCase;
import com.arsdigita.docmgr.xml.DocsJDOMFactory;

public class PermissionsTest extends XMLTestCase {
    public PermissionsTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
        //TestRepository.get();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        //TestRepository.clear();
    }

    public void testDocsPermissions() throws Exception {
        //executeXMLTest(new DocsJDOMFactory(), "com/arsdigita/docmgr/xml/CreateTests.xml", false);
    }
    public void testMovePermissions() throws Exception {
        //executeXMLTest(new DocsJDOMFactory(), "com/arsdigita/docmgr/xml/MoveTests.xml", false);
    }
}

