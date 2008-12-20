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
package com.arsdigita.tools.junit.framework;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 *  PackageTestSuite
 *
 *  This class is the foundation for the test suite methodology. At each package level,
 *  an PackageTestSuite derived class is defined.  For Ant to handle TestSuites, the class
 *  must define:
 *  <pre>
 *    public static Test suite();</pre>
 *
 *  In the PackageTestSuite framework, this method works as in the following example:
 *
 *  <pre>
 *    public static Test suite()
 *    {
 *        PersistenceSuite suite = new PersistenceSuite();
 *        populateSuite(suite);
 *        return suite;
 *    }</pre>
 *  
 *  The PackageTestSuite.populateSuite method adds all the valid test cases in the same
 *  package as the derived Suite class. Optionally, if the property test.testpath is defined,
 *  the framework will look here. test.testpath must be the fully qualified path name.
 *
 * @author Jon Orris
 * @version $Revision: #13 $ $Date: 2004/08/16 $
 */

public class PackageTestSuite extends TestSuite {
    public final static String versionId = "$Id: PackageTestSuite.java 750 2005-09-02 12:38:44Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    public PackageTestSuite() {
        super();
    }

    public PackageTestSuite(String name) {
        super(name);
    }

    public PackageTestSuite(Class testClass) {
        super(testClass);
    }

    /**
     *  Overrides TestSuite.addTestSuite. This allows the class to check for wrappers
     *  and error tags.
     *
     *  If the test class has a field named FAILS, the test will not be added to the suite.
     *  FAILS can be any public static type, such as:
     *
     *  <pre>
     *  public static final boolean FAILS = true;
     *  </pre>
     *
     *  If the TestCase requires initialization of some external resources, the
     *  class should implement the following method:
     *
     *  <pre>
     *  public static Test suite()
     *  </pre>
     *
     *  This factory method can then return the TestCase wrapped in some TestDecorator
     *  that performs initialization.
     *
     *  An example would be:
     *  
     *  <pre>
     *  public FooTest extends TestCase {
     *      public static Test suite() {
     *          TestSuite suite = new TestSuite(FooTest.class);
     *          TestDecorator fooSetup = new FooSetup(suite);
     *          return fooSetup;
     *      }
     *  }
     *
     *  public FooSetup extends TestSetup {
     *      // called once before any tests are run
     *      protected void setUp() {
     *          GlobalResource.initialize();
     *          SQLLoader.loadAllSQL();
     *      }
     *
     *      // called once after all tests are run
     *      protected void tearDown() {
     *          GlobalResource.cleanup();
     *          SQLLoader.clearDatabase();
     *      }
     *  }
     *  </pre>
     *
     *  There is an alternative methodology, which may be cleaner. Since this whole system,
     *  like the original Ant test setup, relies on class names, there may be a
     *  better way. If the test for some class Foo requires a TestSetup wrapper,
     *  the classes could be named as follows:
     *
     *  <p>
     *  <ul>
     *      <li><p>FooTestImpl.java - The TestCase based class. Was FooTest in prior example</p></li>
     *      <li><p>FooTest.java - The TestSetup derived class, which is created wrapping
     *          FooTestImpl.java.  Was FooSetup in above example</p></li>
     *  </ul>
     *  </p>
     *  
     *  An example  would be:
     *  
     *  <pre>
     *  public class FooTest extends TestSetup {
     *        public FooTest(Test test)
     *        {
     *            super(test);
     *        }
     *        public static Test suite() {
     *          return new FooTest(new TestSuite(FooTestImpl.class));
     *       }
     *  }
     *  </pre>
     *  
     *  @param testClass The test class to add to the suite.
     */
    public void addTestSuite(final Class testClass) {

        if( Modifier.isAbstract(testClass.getModifiers()) ){
            return;
        }

        try {
            Field failure = testClass.getField("FAILS");
            // If the test class has a FAILS field, it is not ready to be integrated.
            return;
        }
        catch(Exception e) {
            // Ignored. There is no such Field defined on the class.
        }

        // See if the class defines a suite factory method.
        try {
            Method wrapperFactory = testClass.getMethod("suite", new Class[0]);
            try {
                Test testWrapper = (Test) wrapperFactory.invoke( null, new Object[0] );
                addTest( testWrapper );

            }
            catch(final Exception e) {
                // Something evil occured. The method is not static, public, etc.
                addTest( new TestCase("suiteFailed") {
                        public void testWrapperFailed() {
                            fail("Failed to invoke" + testClass.toString() + ".suite(). " + e.getMessage());
                        }
                    }
                         );
            }

        }
        // This class does not make a wrapper for itself.
        catch(NoSuchMethodException e) {
            //super.addTestSuite(testClass);
            addTest( new PackageTestSuite(testClass) );
        }

    }

    /**
     *  For each TestCase based class in the same package as the suite,
     *  add the TestCase to the suite.
     *
     *  @param suite The PackageTestSuite derived class.
     */
    protected static void populateSuite(PackageTestSuite suite) {
        String testCasePath = getTestCasePath(suite);

        File testFileDir = new File(testCasePath);
        String[] filenames = testFileDir.list();
        if( filenames != null && filenames.length > 0) {
            addTestCases(filenames, suite);
        } else {
            System.err.println("Warning: No tests found for test path: " + testCasePath);
        }
        if (suite.countTestCases() == 0) {
            System.err.println("Warning: no tests added for test path: " + testCasePath);
        }

    }

    /**
     *  Adds a given test to the suite. If the test somehow cannot be found, a
     *  failing test shall be added to the suite.
     *
     *  @param fullClassName The fully qualified name of the class.
     *      I.e. com.arsdigita.whatever.SomethingTest
     *
     *  @param suite The PackageTestSuite to add TestCases to.
     *
     */
    private static void addTestCase(final String fullClassName, PackageTestSuite suite) {
        try {
            Class theClass = Class.forName(fullClassName);
            suite.addTestSuite(theClass);
        }
        catch(final ClassNotFoundException e) {
            suite.addTest( new TestCase("testClassFailure") {
                    public void testClassFailure() {
                        fail("Unexpected failure to find test class " + fullClassName + ". " + e.getMessage());
                    }
                }
                           );
        }

    }

    /**
     *  Adds all of the valid Test classes to the suite. A valid test class is
     *  assumed to be named SomethingTest.
     *
     *  @param filenames The list of all files in the test class directory.
     *  @param suite The PackageTestSuite to add TestCases to.
     *
     */
    private static void addTestCases(String[] filenames, PackageTestSuite suite) {
        final String packageName =  getPackageName(suite);
        for( int i = 0; i < filenames.length; i++) {
            final String filename = filenames[i];

            final boolean isTestClass;

            String testClass = System.getProperty("junit.test", "");
            String testCactus = System.getProperty("junit.usecactus", "");

            if ( ! testClass.equals("") ) {
                isTestClass = filename.equals(testClass);
            } else {
                if ( testCactus.equalsIgnoreCase("true") ) {
                    isTestClass = filename.endsWith( "Test.class" );
                }
                else if ( testCactus.equalsIgnoreCase("only") ) {
                    isTestClass = filename.endsWith( "CactusTest.class" );
                }
                else {
                    isTestClass = filename.endsWith( "Test.class" ) &&
                        !filename.endsWith( "CactusTest.class" );
                }
            }

            if ( isTestClass ) {

                final String className = packageName + "." +
                    filename.substring( 0, filename.indexOf('.'));
                System.out.println("Class: " + className);

                addTestCase( className, suite );
            }
        }

    }

    public static Test suite() {
        PackageTestSuite suite = new PackageTestSuite();
        populateSuite(suite);
        return suite;
    }

    /**
     *  OUT OF DATE: Implementation needs to be altered!
     *
     *  Utility method to get the full path to the test class files.
     *  This makes several assumptions, which are now invalidated
     *  by the 6/19/01 reorganization:
     *
     *      1) When ant is running recursively, its cwd is the top level
     *      directory for the project, i.e. infrastructure/persistence.
     *
     *      2) The build system always places the test class files in
     * {cwd}/build/test
     *
     *  It is a real pity that java reflection doesn't have something like
     *  Package.getClasses()
     *
     *  @param suite The PackageTestSuite that tests are being added to. Is in same
     *               package as other tests.
     *
     *  @return The package name, i.e. com.arsdigita.whatever
     */
    private static String getTestCasePath(PackageTestSuite suite) {
        String definedPath = System.getProperty("test.testpath");
        if( null != definedPath ) {
            return definedPath;
        }

        String pathName = "";
        String baseDir = System.getProperty("test.base.dir");
        if( baseDir != null ) {
            pathName = baseDir;
        } else {
            File current = new File("");
            pathName = current.getAbsolutePath() + File.separator + "build" + File.separator + "tests";
        }

        String packageName =  getPackageName(suite);
        return (pathName + File.separator + packageName.replace('.', File.separatorChar));
    }

    /**
     *  Utility method to get the package name from the suite, and strip the
     *  annoying leading package from 'package com.whatever'
     *
     *  @return The package name, i.e. com.arsdigita.whatever
     */
    private static String getPackageName(PackageTestSuite suite) {
        Package p = suite.getClass().getPackage();
        String fullPackageName = p.toString();
        String packageName = fullPackageName.substring(fullPackageName.indexOf(' ') + 1 );

        return packageName;
    }

}
