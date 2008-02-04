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
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;

public class TestDefinition extends Element {
    private static Logger s_log = Logger.getLogger(TestDefinition.class);

    public static final String NAME = "test_def";
    public TestDefinition() {
        this( NAME, Namespaces.TEST );
    }
    public TestDefinition(String name) {
        super(NAME, Namespaces.TEST);
    }
    public TestDefinition(String name, Namespace ns) {
        super(NAME, Namespaces.TEST);
    }
    public TestDefinition(String name, String uri) {
        super(NAME, Namespaces.TEST);
    }
    public TestDefinition(String name, String prefix, String uri) {
        super(NAME, Namespaces.TEST);
    }

   public void runTest() throws Exception {
       executeDependentTests();
       List testActions = getChildren("actions", Namespaces.TEST);
       s_log.warn("TestDef has " + testActions.size() + " Elements.");
       for (Iterator iterator = testActions.iterator(); iterator.hasNext();) {
           Element actionsElement = (Element) iterator.next();
           executeTestActions(actionsElement);

       }
   }

    private void executeDependentTests() throws Exception {
        Element externalDefs = getChild("dependent_tests", Namespaces.TEST);
        if (null != externalDefs) {
            TestDocument parentDoc = (TestDocument) getDocument();
            DocImporter importer = new DocImporter(false);
            importer.setJDOMFactory(parentDoc.getFactory());

            List tests = externalDefs.getChildren();
            for (Iterator iterator = tests.iterator(); iterator.hasNext();) {
                Element externalTest = (Element) iterator.next();
                String testFile = externalTest.getAttributeValue("file");
                executeDependentTest(testFile, importer);
            }
        }
    }

    private void executeDependentTest(String testFile, DocImporter importer) throws Exception {
        Document doc = importer.getDocumentAsResource(testFile);
        TestSet set = (TestSet) doc.getRootElement();
        set.runTests();
     }

    private void executeTestActions(Element actionsElement) throws Exception {
        List children = actionsElement.getChildren();

        for (Iterator actions = children.iterator(); actions.hasNext();) {
            TestAction action  = (TestAction) actions.next();
            Exception error = null;
            try {
                action.execute();
                if ( action.shouldFail() ) {
                    error = new XMLException("Test acton didn't fail!", action);
                }
            } catch (Exception e) {
                if ( action.shouldFail() == false ) {
                    error = new XMLException("Unexpected failure of test action!", e, action);
                }
                // Log the exception just in case it is invalid. The way the system is set up,
                // if an unexpected error occurs during a test that should fail, it will be
                // considered to 'pass'. This may not be correct.
                s_log.info("shouldFail with exception", e);
            }

            if ( null != error ) {
                throw error;
            }
        }
    }
}
