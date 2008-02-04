/*
 * Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
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

