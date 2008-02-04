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

import com.arsdigita.tools.junit.framework.BaseTestCase;
import org.jdom.Document;

public abstract class XMLTestCase extends BaseTestCase {

    public XMLTestCase(String name) {
        super(name);
    }
    /**
     * Runs an XML test and reports any failures.
     *
     * @param factory The test specific JDOMFactory to use.
     * @param testFile The xml file that defines the test. It is expected to reside
     *      in the test source directory. File name should be given as
     *      com/arsdigita/whatever/Test.xml
     * @param validateDoc If true, XML validation will be turned on for the document import.
     */
    protected void executeXMLTest(TestJDOMFactory factory, String testFile, final boolean validateDoc) throws Exception {
        DocImporter importer = new DocImporter(validateDoc);
        importer.setJDOMFactory(factory);

       // String fullPath = System.getProperty("user.dir") + "/test/src/" + testFile;
        String fullPath = System.getProperty("test.base.dir") + "/" + testFile;
        Document tests = importer.getDocumentAsFile(fullPath);
        TestSet set = (TestSet) tests.getRootElement();
        try {
            set.runTests();

        } finally {
            ResourceRegistry.instance().cleanResources();
        }


    }

}
