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
package com.arsdigita.tools.junit.extensions;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import junit.framework.Test;
import junit.framework.TestCase;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

/**
 * <P> This Utility class records and provides access to timing results and 
 * other test information stored in an xml file. </P>
 *
 * <P> An example xml file is: </P>
 * <PRE>
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;
 * &lt;tests&gt;
 *   &lt;defaults&gt;
 *     &lt;variance&gt;5&lt;/variance&gt;
 *   &lt;/defaults&gt;
 * 
 * &lt;project name="default"&gt;
 *     &lt;test test_name="com.arsdigita.categorization.CategoryTest"&gt;
 *         &lt;testcase test_case_name="testEquals"&gt;
 *             &lt;previous_fastest&gt;454&lt;/previous_fastest&gt;
 *             &lt;fastest&gt;441&lt;/fastest&gt;
 *             &lt;last_updated&gt;1059080456257&lt;/last_updated&gt;
 *         &lt;/testcase&gt;
 *     &lt;/test&gt;
 * &lt;/project&gt;
 * &lt;/tests&gt;
 * </PRE>
 *
 * <P> The document node is tests. under tests, there may be 0 or 1 default
 * elements that contain defaults for all tests, as well as one or more
 * project nodes. Each project node may contain one or more 
 * <code>test</code> element, each with a unique <code>test_name</code>
 * attribute. The test_name is the full class name of the TestCase class.
 * Each <code>test</code> element can have one or more <code>testcase</code>
 * elements which store information about a single testcase (corresponding
 * to a single test method in the parent <code>test</code>. </P>
 *
 * <P> The information stored for each testcase includes: </P>
 * <UL>
 *      <li><code>fastest</code>: The fastest this test has been
 *              recorded to run. (ms). Optional, auto-set. If this is left
 *              empty, Long.MAX_VALUE will be used.</li>
 *      <li><code>previous_fastest</code>: The previous value of fastest
 *              (ms). Optional, auto-set. This is auto-set when fastest
 *              is updated. </li>
 *      <li><code>last_updated</code>: The time (in ms) that this test
 *              information was last updated. Auto-set.</li>
 *      <li><code>variance</code>: This percentage can be added on to a
 *              fastest time recording. This is because no two runs will
 *              complete at the <i>exact</i> same time, as long as it is
 *              less than <code>fastest</code> + <code>variance</code> it
 *              it acceptable. This variance can be set high or low
 *              depending on the test. Manually set.</li> 
 * </UL>
 *
 * <P>The <code>default</code> Element provides default values for all the 
 * other tests. It is optional, and is most useful for specifying a 
 * default variance. </P>
 *
 * <P> This class also acts as a Factory for {@link TestCaseDescriptor}, which
 * are the objects used to transfer data to and from this class.
 *
 * <P> The location of the XML file is accessed through the system property
 * <code>test.perf.results</code>, while the project name is accessed through
 * <code>test.perf.project</code>. If a file is explicitly specified in the
 * <code>load</code> method, the default project name is used.</P>
 *
 * 
 * @author <a href="mailto:aahmed@redhat.com"> Aizaz Ahmed </a>
 */


public class PerfTiming {

    /* This stores the parsed xml file */
    private Document resultsXMLDoc;
    /* Pointer to the xmlFile */ 
    private File xmlFile;
    /* Document update time */
    private long docLastUpdate = 0;
    /* The project we are running tests on */
    private String project = DEFAULT_PROJECT;
    /* Environment variable that stores xmlFile loc */
    private static final String DEFAULT_PROJECT = "default";
    private String results_var="test.perf.results";
    private String project_var ="test.perf.project";
    private TestCaseDescriptor defaultDesc;
    private static Logger s_log = Logger.getLogger(PerfTiming.class);

    public PerfTiming () {

    }

    /**
     * <P>This method loads and parses the xml file into memory. If
     * the file does not exist, it is created</P>
     *
     * @param resultsFile The file to load as xml (contains results info)
     * @throws ParserConfigurationException If we cannot obtain an
     *                  appropriate xml parser
     * @throws SAXException If the xml is not well-formed
     * @throws IOException If the file cannot be read / written to
     */
    public void load ( final File resultsFile  )
                                    throws ParserConfigurationException,
                                           SAXException,
                                           IOException {

        xmlFile = resultsFile;
        if ( !xmlFile.exists() ) {
            s_log.warn("Creating new file");
            createXMLFile();
        }

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        resultsXMLDoc = db.parse(resultsFile);
        setUpdated ();

        defaultDesc = getDefaultDesc(); 
    }


    /**
     * <P> Loads the xml file pointed to by the system variable
     * test.perf.results into memory. If this variable is not 
     * defined it throws an exception. Also, set the project name to
     * the value specified by <code>test.perf.project</code>, or 
     * default if this is not defined.</P>
     *
     * @throws ParserConfigurationException If we cannot obtain an
     *                  appropriate xml parser
     * @throws SAXException If the xml is not well-formed
     * @throws IOException If the file cannot be read / written to
     * @throws NullPointerException If the test.perf.results system
     *              variable is not defined
     */
    public void load ( ) throws ParserConfigurationException,
                                SAXException,
                                IOException,
                                NullPointerException
    {
        String fileLoc = System.getProperty ( results_var );
        if ( fileLoc == null || fileLoc.equals("") ) {
            throw new NullPointerException ( 
                            "System Property " + results_var + 
                            " is null. No xml file could be loaded" );
        }
        String sys_project = System.getProperty ( project_var ); 
        if ( sys_project != null && !sys_project.equals("") ) {
            project = sys_project;
        }
        load ( new File ( fileLoc ) );
    }


    /**
     * <P> returns true if we have the lastest copy of the xml file <P>
     */
    private boolean isUpdated ( ) {
        long updatedTime = xmlFile.lastModified ();
        if ( updatedTime > docLastUpdate ) {
            return false;
        }
        return true;
    }

    private void setUpdated () {
        docLastUpdate = xmlFile.lastModified();
    }

    private void createXMLFile ( ) 
                            throws IOException, ParserConfigurationException
    {
        try {
            xmlFile.createNewFile();
        } catch ( Exception e ) {
            throw new IOException ( "Could not create file: " +
                            xmlFile.getAbsolutePath() + 
                            ", file needed to store performance results\n" +
                            e.getMessage() );
        }

        // now create the blank Document shell
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        resultsXMLDoc = db.newDocument();
        Element root = resultsXMLDoc.createElement ( "tests" );
        resultsXMLDoc.appendChild ( root );

        // a hardcoded default
        Element defaultTest = resultsXMLDoc.createElement ( "defaults" );
        Element defaultVariance = resultsXMLDoc.createElement (
                                    TestCaseDescriptor.VARIANCE );
        Text varianceValue = resultsXMLDoc.createTextNode ( "5" );

        root.appendChild ( defaultTest );
        defaultTest.appendChild ( defaultVariance );
        defaultVariance.appendChild ( varianceValue );

        save();
    }

    private static Element findElement ( NodeList list, 
                                           String attribute, 
                                           String value ) {

        for ( int n=0; n<list.getLength(); n++) {
            Element elem = (Element)list.item(n);
            String attrValue = elem.getAttribute ( attribute );
            if ( value.equals ( attrValue ) ) {
                return elem;
            }
        }
        return null;
    }


    private Element getProjectElem () {
        Element elem = resultsXMLDoc.getDocumentElement();
        NodeList projectList = elem.getElementsByTagName("project");
        return findElement ( projectList, "name", project );
    }

    /**
     * <P> Gets the XML element that corresponds to the specified
     * <code>testName</code>.</P>
     *
     * @param   testName The name of the test to retrieve
     * @return  XML Element that corresponds to the specified
     *          <code>testName</code>. Null if there is no existing
     *          Element for this test.
     */
    private Element getTestElem ( String testName ) {
        Element proj = getProjectElem(); 
        if ( proj != null ) {
            NodeList testList= proj.getElementsByTagName("test");
            return findElement ( testList, TestCaseDescriptor.TEST_NAME, testName );
        }
        return null;
    }

    
    /**
     * <P> Gets the XML element that corresponds to the specified
     * <code>testName</code> and <code>testCaseName</code></P>
     *
     * @param   testName The name of the test to retrieve
     * @param   testCaseName The name of the testCase to retrieve
     * @return  XML Element that corresponds to the specified
     *          <code>testName</code> and <code>testCaseName</code>.
     *          Null if there is no existing Element for this test.
     */
    private Element getTestCaseElem ( String testName, String testCaseName ) 
    {
        Element testXML = getTestElem ( testName );
        if ( testXML != null ) {
            NodeList testCaseList = testXML.getElementsByTagName("testcase");
            return findElement ( testCaseList, TestCaseDescriptor.TEST_CASE_NAME, testCaseName );
        }
        //we didn't find our testcase.
        return null;
    }


    /**
     * Returns the default TestCaseDescriptor, based on the node, 
     * <code>testName=default</code> <code>testCaseName=default</code> if
     * one exists, or an empty descriptor if none exists.
     */
    private TestCaseDescriptor getDefaultDesc ( ) {

        TestCaseDescriptor dDesc = null;
        NodeList defList = resultsXMLDoc.getDocumentElement()
                                            .getElementsByTagName ( "defaults" );
        if ( defList != null ) {
            dDesc = createDescriptor ( (Element) defList.item(0) );
        }
        if ( dDesc == null ) {
            dDesc = new TestCaseDescriptor();
        }

        // set some runtime defaults here
        if ( dDesc.getProperty(TestCaseDescriptor.FASTEST) == null ) {
            dDesc.setProperty(TestCaseDescriptor.FASTEST, new Long(Long.MAX_VALUE));
        }

        return dDesc;
    }


    /**
     * <P>Returns a TestCaseDescriptor based on the specified TestCase
     * Element. The returned TestCaseDescriptor does not have it's
     * <code>testName</code> or <code>testCaseName</code> set, this needs to
     * be done at a higher level.
     */
    private TestCaseDescriptor createDescriptor ( Element elem ) {
        if ( elem == null ) {
            return null; 
        }
        TestCaseDescriptor tdesc = new TestCaseDescriptor();
        //Extract and save values from the elem.
        NodeList properties = elem.getChildNodes();
        for ( int i=0; i<properties.getLength(); i++ ) {
            Node elementNode = (Node) properties.item(i);   
            if ( elementNode.getNodeType() == Node.ELEMENT_NODE ) {
                Text txtNode = (Text) elementNode.getFirstChild();
                if ( txtNode != null ) { 
                tdesc.setProperty (
                    elementNode.getNodeName(),
                    TestCaseDescriptor.castToObject(elementNode.getNodeName(), txtNode.getData()));
                }
            }
        }
        return tdesc;
    }
    

    /**
     * <P> Returns a {@link TestCaseDescriptor} for the specified {@link Test}.
     * This is the recommended way to get TestCaseDescriptors. It loads the 
     * values from the xml file if an entry is recorded (adding default 
     * values as necessary), or if no previous entry exists, it returns 
     * the default TestCaseDescriptor.</P>
     *
     * @param test The test to retrieve a TestCaseDescriptor for
     * @return A TestCaseDescriptor for the specified test
     */

    public TestCaseDescriptor getDescriptor ( Test test ) {
        if ( ! (test instanceof TestCase) ) {
            throw new ClassCastException ("Currently, " + this.getClass() +
                        " only supports TestCase's");
        }
        String testName = ((TestCase)test).getClass().getName();
        String testCaseName = ((TestCase)test).getName();
        return getDescriptor ( testName, testCaseName ); 
    }

     
    /**
     * <P> This method returns TestCaseDescriptors for the specified
     * testName, testCaseName combination. It loads the values from the xml
     * file if an entry is recorded (adding default values as necessary), or
     * if no previous testName, testCaseName combination can be found, it
     * returns a default TestCaseDescriptor.</P>
     *
     * @param testName The name of the {@link TestCase} that defines the
     *                 test method we wish to run
     * @param testCaseName The name of the actual test method to run
     * @return A TestCaseDescriptor for the specified test
     */
    private TestCaseDescriptor getDescriptor ( String testName, String testCaseName ) {
        TestCaseDescriptor tdesc = createDescriptor (getTestCaseElem(testName, testCaseName));
        if ( tdesc == null ) {
            tdesc = new TestCaseDescriptor ();
        }
        tdesc.setProperty(TestCaseDescriptor.TEST_NAME, testName );
        tdesc.setProperty(TestCaseDescriptor.TEST_CASE_NAME, testCaseName );
        tdesc.useDefault( defaultDesc );
        return tdesc;
    }


    /**
     * When writing elements, if the value is the exact same as the default 
     * value it will not be written. The is because all TestDescriptions have
     * the default values loaded. We would end up explicitly saving the
     * values to each element (defeating the purpose of defaults). This is less
     * than optimal, notably because the user cannot explicity set a value
     * to the same as a default. (if the default changes later on, this will
     * too, even if he explicitly set it). A more elegant solution would be to
     * store a separate default data stucture in TestCaseDescriptor, to keep
     * these values distint. (and know for sure what to write and what not to)
     */
    private void createUpdateElem ( TestCaseDescriptor tdesc ) {
        String testName = (String) tdesc.getProperty ( TestCaseDescriptor.TEST_NAME );
        String testCaseName = (String) tdesc.getProperty ( TestCaseDescriptor.TEST_CASE_NAME);

        Element elem = newTestCaseElem( testName, testCaseName );

        // we now have a fresh testcase to populate
        Iterator newKids = tdesc.keyIterator();
        while ( newKids.hasNext() ) {
            String kidName = (String) newKids.next();
            // if it is default value, don't save it
            Object defaultVal = defaultDesc.getProperty(kidName);
            if ( ( (defaultVal == null) ||
                   ((defaultVal != null) && (!tdesc.getProperty(kidName).equals(defaultVal)))
                 ) &&
                 (!kidName.equals ( TestCaseDescriptor.TEST_NAME )) && 
                 (!kidName.equals ( TestCaseDescriptor.TEST_CASE_NAME )) &&
                 (!kidName.equals ( TestCaseDescriptor.LAST_UPDATED ))
               )
            {
                Element newKid = resultsXMLDoc.createElement ( kidName );
                Text value = resultsXMLDoc.createTextNode ( tdesc.getProperty(kidName).toString() );
                elem.appendChild ( newKid );
                newKid.appendChild ( value );
            }
        }
        // put in the last updated time
        Element updated = resultsXMLDoc.createElement ( TestCaseDescriptor.LAST_UPDATED );
        Text updatedTime = resultsXMLDoc.createTextNode ( String.valueOf ( new Date().getTime() ));
        elem.appendChild ( updated );
        updated.appendChild ( updatedTime );
    }

    

    /**
     * <P> If it exists, it'll clean it out
     */
    private Element newTestCaseElem ( String testName, String testCaseName ) {
        Element elem = getTestCaseElem( testName, testCaseName );
        if (elem == null) {
            // The exact element was not found, see if the test exists
            Element testElem = getTestElem ( testName );
            if ( testElem == null ) {
                // the test does not exist, see if the project exists
                Element projectElem = getProjectElem ();
                if ( projectElem == null ) {
                    // project does not exists, create it
                    projectElem = resultsXMLDoc.createElement ( "project" );
                    projectElem.setAttribute ( "name", project );
                    resultsXMLDoc.getDocumentElement().appendChild ( projectElem );
                }
                //project now exists, create test 
                testElem = resultsXMLDoc.createElement("test");
                testElem.setAttribute ( TestCaseDescriptor.TEST_NAME, testName );
                projectElem.appendChild ( testElem );
            }
            // project, test exist, create testcase
            elem = resultsXMLDoc.createElement ( "testcase" );
            elem.setAttribute ( TestCaseDescriptor.TEST_CASE_NAME, testCaseName );
            testElem.appendChild ( elem );
        } else {
            // the exact testName / testCase exists, we need to clean it out
            NodeList killChildren = elem.getChildNodes();
            while ( killChildren.getLength() != 0 ) {
                elem.removeChild ( killChildren.item ( 0 ) );
            }
        }
        return elem;
    }


    /**
     * <P> Saves test information specified by the TestCaseDescriptor to the
     * xml file. This causes a rewrite for the whole xml file.</P>
     *
     * @param tdesc The TestCaseDescriptor to save to xml
     */
    public void update ( TestCaseDescriptor tdesc ) throws IOException {
        if ( ! isUpdated () ) {
            try {
                load ( xmlFile );
            } catch ( Exception e ) {
                /*
                 * If we're this deep in the code, it's unlikely that a load
                 * exception would occur now, and not earlier. Throw it as an
                 * IOException
                 */
                 throw new IOException ( "Could not reload file: " + xmlFile
                                         + e.getMessage() );
            }
        }
        createUpdateElem ( tdesc );
        save ();
    }


    /**
     *<P>Writes the updated document to the <code>xmlfile</code>. Any node
     * changes (caused for example by update() ) are saved.</p>
     *
     * TODO: Output xml nicely formatted.
     */
    private void save () throws IOException {

        FileWriter fileOut = new FileWriter ( xmlFile );
        fileOut.write ( toString ( resultsXMLDoc ) );
        fileOut.flush();
        fileOut.close();
    }


    public static String toString ( Document doc ) {
        return com.arsdigita.xml.Document.toString ( doc, true );
    }

    
    public static void main ( String [] args ) throws Exception {

        PerfTiming test = new PerfTiming(); 
        test.load();

        System.out.println (test.getDescriptor ( "com.arsdigita.cms.largecontent.LCTest" , "testPopulate" ));
        System.out.println (test.getDescriptor ( "com.arsdigita.cms.largecontent.LCTest" , "test"));
        TestCaseDescriptor fake = test.getDescriptor ( "com.arsdigita.cms.largecontent.LCTest" , "fake");
        System.out.println ( fake );

        test.update ( fake );
    }
}
