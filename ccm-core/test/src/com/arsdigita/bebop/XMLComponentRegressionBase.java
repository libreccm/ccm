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
package com.arsdigita.bebop;

import com.arsdigita.tools.junit.framework.BaseTestCase;
import com.arsdigita.util.RequestEnvironment;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 *      This test performs regression testing on bebop Component XML
 *      generation. Since each Bebop component has a complex set of
 *      possible outputs, each test of a Bebop component should subclass
 *      the XMLComponentRegressionBase class, and test methods to generate
 *      specific use cases of Bebop should be added.
 *
 *      For the test, XML is generated for the Component, and compared against
 *      canonical XML reference files that are under change control. If there
 *      are file differences, or any other error, the test will fail.
 *
 *      The xml files for each class are named FULL_CLASS_NAME.xml
 *      ex. com.arsdigita.bebop.Page.xml
 *
 *      At present, the XML files are expected in the same directory as this test.
 *      This may not work for automated testing, and may have to change.
 *
 *      To (re)generate the XML files, call this class directly as a main
 *      class with the -regenerate flag.
 *
 * @version $Revision: #9 $ $Date: 2004/08/16 $ */

public class XMLComponentRegressionBase extends BaseTestCase {

    public static final String versionId = "$Id: XMLComponentRegressionBase.java 743 2005-09-02 10:37:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    public XMLComponentRegressionBase(String id) {
        super(id);
    }

    /**
     *  Run a comparison test for a component.
     *
     *  @param c The component to test.
     *  @param testName The name of the test. The testName parameter
     *  should be the same for tests that should produce the same XML
     *  output but in different ways. For instance, a Bebop component
     *  may have an overloaded constructor that accepts multiple
     *  arguments that can also be set individually with methods. The
     *  XML output of the component should be the same in both cases,
     *  so the testName should be the same for both tests.
     *
     *  This method is protected to give extending classes outside the
     *  package access.
     */
    protected void testComponent(Component c, String testName) {
        try
            {
                new RequestEnvironment();
                SingleComponentXMLComparator comp =
                    new SingleComponentXMLComparator(c, testName);
                comp.testXMLGeneration();
            }
        catch(XMLComparatorError xe)
            {
                fail("Failed comparing XML.\n" + getStackTrace(xe));
            }
        catch(Exception e)
            {
                fail("Unknown Error: " + getStackTrace(e));
            }
    }

    /**
     *  Run a regular expression test for a component.
     *
     *  @param c The component to test.
     *  @param regexp The regular expression to look for in the XML
     *  generated by the component.
     *  @param is_debug Should the RequestContext indicate we're debugging?
     *
     *  This method is protected to give extending classes outside the
     *  package access.  */
    protected void regexpComponent(Component c, String regexp,
                                   boolean is_debug) {
        try
            {
                ComponentXMLRegexper comp =
                    new ComponentXMLRegexper(c, regexp);
                comp.testXMLGeneration(is_debug);
            }
        catch(XMLComparatorError xe)
            {
                fail("Failed finding regexp in XML.\n" + getStackTrace(xe));
            }
        catch(Exception e)
            {
                fail("Unknown Error: " + getStackTrace(e));
            }
    }

    /**
     *  Utility method for copying an Exception stack trace to
     *  a String.
     *
     *  @param e The Exception to get the stack trace from.
     *
     *  @return The stack trace.
     */
    static String getStackTrace(Exception e) {
        StringWriter stackTrace = new StringWriter();
        PrintWriter writer = new PrintWriter( stackTrace, true );
        e.printStackTrace(writer);
        return stackTrace.toString();
    }

    public static void main(String args[])
    {

        if( args.length != 0 )
            {
                if( args.length > 1 )
                    {
                        System.err.println("Usage: {-regenerate}");
                        System.exit(0);
                    }
                if( args[0].equals("-regenerate") )
                    {
                        System.out.println("Regenerating XML files");
                        ComponentXMLComparator.GENERATE_FILES = true;
                    }
                else
                    {
                        System.err.println("Usage: {-regenerate}");
                        System.exit(0);
                    }
            }
        junit.textui.TestRunner.run(XMLComponentRegressionBase.class);
    }

}
