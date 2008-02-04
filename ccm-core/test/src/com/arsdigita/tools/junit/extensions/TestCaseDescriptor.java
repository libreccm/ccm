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

import java.lang.Long;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

/**
 * <P> This class encapsulates data about a particular testcase. It is used
 * to ferry data back and forth from the {@link PerfTiming} class. It also
 * provides convenience routines to auto-cast objects.</P>
 *
 * <P> Property type casting: </P>
 * <UL>
 *      <li>String:</li>
 *      <UL>
 *          <li>TEST_NAME</li>
 *          <li>TEST_CASE_NAME</li>
 *      </UL>
 *      <li>Long:</li>
 *      <UL>
 *          <li>FASTEST</li>
 *          <li>PREVIOUS_FASTEST</li>
 *          <li>LAST_UPDATED</li> 
 *          <li>VARIANCE</li>
 *      </UL>
 * </UL>
 *
 * @see PerfTiming
 * @see Test
 * @author <a href="mailto:aahmed@redhat.com"> Aizaz Ahmed </a>
 */

public class TestCaseDescriptor {

    /* keep in sync with xml file tags */
    public static final String TEST_NAME        = "test_name";
    public static final String TEST_CASE_NAME   = "test_case_name";
    /**
     * <P> Key used to set and retrieve property </P>
     */
    public static final String FASTEST          = "fastest";
    /**
     * <P> Key used to set and retrieve property </P>
     */
    public static final String PREVIOUS_FASTEST = "previous_fastest";
    /**
     * <P> Key used to set and retrieve property </P>
     */
    public static final String LAST_UPDATED     = "last_updated";
    /**
     * <P> Key used to set and retrieve property </P>
     */
    public static final String VARIANCE         = "variance";

    private Map tdata;

    
    /*
     * <P> Only sets the correct identifier fields for the TestCaseDescriptor,
     * does not load any content. After this is called, the TEST_NAME and
     * TEST_CASE_NAME fields will be correctly set<P>
     *
     * @param test The test to name this TestCaseDescriptor after
     */
     
    /*
     * <P> We need to obtain a unique name to identify this test by.
     * Unfortunately, Test objects can be very different, and there are a
     * number of cases that need to be coded for:
     *
     * <UL>
     *     <li><i>TestCase</i>: A test that extends TestCase is identified
     *             by TEST_NAME = full class name, TEST_CASE_NAME = result
     *             of getName().</li>
     *     <li><i>TestCase with runTest overridden</i>: A test that extends
     *             TestCase but has it's runTest() overridden is identified
     *             by TEST_NAME = full class name, TEST_CASE_NAME = AUTO_RUN 
     *             </li>
     *     <li><i>TestSuite</i>: A test that extends TestSuite is identified
     *             by TEST_NAME = full class name, TEST_CASE_NAME = AUTO_RUN
     *             </li>
     *     <li><i>TestDecorator</i>: This is a bit trickier, because
     *             TestDecorators can wrap several other TestDecorators.
     *             A TestDecorator does not have a unique name, it can be used
     *             to wrap any Test. As such we need a unique signiture
     *             identifying this particular composition of TestDecorators
     *             uniquely. We therefore unfurl the testDecorators, adding
     *             their names to the TEST_NAME, until we reach a base case.
     *             So, for example, a heavily decorated TestCase maybe saved:
     *             TEST_NAME=decorator1_decorator<i>n</i>_TestCase_classname
     *             TEST_CASE_NAME=result of getName() on TestCase.
     *             </li>
     * </UL>
     * 
     * This has been determined as overkill for now, and won't be implemented
     * just yet
     */
     /*
    public TestCaseDescriptor ( Test test ) {
    }
    */
    public TestCaseDescriptor () {
        tdata = new Hashtable();
    }
    
    public Object getProperty ( String property ) {
        return tdata.get(property); 
    }

    /**
     * <P> Sets the specified property. Use the static string constants of
     * this class to specify a property value. When setting a value for
     * FASTEST, the existing value (if any) is immediately moved to 
     * PREVIOUS_FASTEST.</P>
     *
     * @param property  the property to set, one of the static constant strings
     *                  defined in this class
     * @param value the value to set property to
     */
    public void setProperty ( String property, Object value ) {
        // if setting fastest, we need to save the previous value
        if ( property.equals ( FASTEST ) ) {
            if ( getProperty ( FASTEST ) != null ) {
                tdata.put ( PREVIOUS_FASTEST, getProperty ( FASTEST ) );
            }
        }
        tdata.put ( property, value );
    }


    /**
     * <P> a convenience method that returns FASTEST as a long datatype,
     * instead of a Long Object. </p>
     */
    public long getFastest () {
        Long fastest = (Long) tdata.get(FASTEST);
        return fastest.longValue();
    }

    /**
     * <P>Returns the fastest time, plus the appropriate variance. Suitable
     * to be used as a timeout for a TimedTest. The variance is a
     * percentage value.</P>
     *
     * @return ( FASTEST * ( 1 + <code>variance</code>/100 ) ) as a long.
     *          ie, the decimals are truncated. If there is no variance it
     *          returns FASTEST.
     */
    public long getFastestWithVar () {
        long tFast = getFastest();
        Long tVarLong = (Long) getProperty ( VARIANCE );
        
        if ( tFast != Long.MAX_VALUE && tVarLong != null ) {
            long tVar = ( tVarLong ).longValue();
            tFast = (long) ((double)tFast * ( (double)1 + ((double)tVar / (double)100) ));
        }

        return tFast;
    }


    /**
     * <P> A convenience method to set a value for fastest.
     * Don't need to specify fastest as a long object. </P>
     */
    public void setFastest (long fastest) {
        setProperty (FASTEST, new Long(fastest));
    }


    /**
     * <P>This returns an iterator over the keySet of stored properties.
     * No distinction is made (yet) between a property that was set by
     * default and one that was explicitly user set</P>
     */
    public Iterator keyIterator() {
        /*
         * TODO: This isn't very nice in terms of encapsulation, visibility. 
         * however, we frequently need to be able to iterate over all the
         * stored properties. Better way?
         */ 
        return tdata.keySet().iterator();
    }


    /**
     * <P> Will use the values of the specified TestCaseDescriptor to fill in
     * any unset fields in this TestCaseDescriptor.</P>
     */
    public void useDefault ( TestCaseDescriptor defs ) {
        Iterator defsIter = defs.keyIterator();
        while ( defsIter.hasNext() ) {
            String key = (String) defsIter.next();
            if ( tdata.get(key) == null ) {
                tdata.put(key, defs.getProperty(key));
            }
        }
    }


    /**
     * <P>This method converts a Sring representation of a particular
     * property into an appropriate object type. This makes it possible
     * to set a property (whose value was possibly loaded as a string from
     * an xml file) without having to know it's type. </P>
     *
     * <P> The toString method of the object is relied apon for the 
     * reverse conversion. See class comment for details of mappings</P>
     *
     * @param key the <code>property</code> to set
     * @param value the string representation of the property value.
     * @return  a new object representing <code>value</code>. An object
     *          type appropriate to the specified <code>key</code>
     */
    public static Object castToObject ( String key, String value ) {
        Object retObj;
        if ( key.equals(TestCaseDescriptor.FASTEST) ||
             key.equals(TestCaseDescriptor.PREVIOUS_FASTEST) ||
             key.equals(TestCaseDescriptor.LAST_UPDATED) ||
             key.equals(TestCaseDescriptor.VARIANCE) 
           )
        {
            retObj = new Long ( value );
        } else {
            retObj = value;
        }

        return retObj;
    }


    public String toString () {
        return tdata.toString();
    }
}
