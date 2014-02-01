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
package com.arsdigita.bebop.parameters;

import com.arsdigita.bebop.parameters.IntegerParameter;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.tools.junit.framework.BaseTestCase;
import com.arsdigita.util.HttpServletDummyRequest;
import java.lang.IllegalArgumentException;


public class IntegerParameterTest extends BaseTestCase {


    /**
       Constructs a test with the specified name.

       @param name Name of the test
    */
    protected ParameterModel _stringParameter;
    protected ParameterModel _integerParameter;
    protected HttpServletDummyRequest _httpRequest;

    public IntegerParameterTest(String id) {
        super(id);
    }

    /**
       Sets up the test fixture.
    */
    protected void setUp() {
        _stringParameter =  new StringParameter("StringParameter");
        _integerParameter =  new IntegerParameter("IntegerParameter");
        _httpRequest = new HttpServletDummyRequest();
    }

    /**
       Tears down the text fixture.
       Called after every test case method.
    */
    protected void tearDown() {
        _stringParameter=null;
        _integerParameter=null;
        _httpRequest=null;
    }

    public void testIntegerParameterNoValues() {

        String[] specifiedValues = {};
        _httpRequest.setParameterValues("IntegerParameter",specifiedValues );
        assertEquals(_integerParameter.transformValue(_httpRequest),null);
    }

    public void testIntegerParameterBadValue() {

        String[] specifiedValues = {"Not an Integer"};
        _httpRequest.setParameterValues("IntegerParameter",specifiedValues );
        try {
            Integer temp = (Integer)_integerParameter.transformValue(_httpRequest);
            // if you get here an exception was not raised
            fail("an IllegalArgumentException should have been raised");
        } catch (IllegalArgumentException iae) {
            // ok
        }
    }

    public void testIntegerParameterSomeBadValue() {
        String[] specifiedValues = {"5","Not an Integer"};
        _httpRequest.setParameterValues("IntegerParameter",specifiedValues );
        assertEquals(_integerParameter.transformValue(_httpRequest),new Integer("5"));
    }

    public void testIntegerParameterGoodIntegerValue() {
        String[] specifiedValues = {"5"};
        _httpRequest.setParameterValues("IntegerParameter",specifiedValues );
        assertEquals(_integerParameter.transformValue(_httpRequest),new Integer("5"));
    }
    /*
      move this test to NumberArryTest.java

      public void testIntegerParameterBadIntegerValue() {
      String[] specifiedValues = {"5.01"};
      _httpRequest.setParameterValues("IntegerParameter",specifiedValues );

      try {
      Object[] temp = _integerParameter.transformValues(_httpRequest);
      // if you get here an exception was not raised
      assert(false);
      } catch (IllegalArgumentException iae) {
      assert(true);
      }
      }

      public void testIntegerParameterSomeBadValues() {
      String[] specifiedValues = {"5", "Not an Integer"};
      _httpRequest.setParameterValues("IntegerParameter",specifiedValues );
      try {
      Object[] temp = _integerParameter.transformValues(_httpRequest);
      //if you get here an exception was not raised
      assert(false);
      } catch (IllegalArgumentException iae) {
      assert(true);
      }
      }

      public void testIntegerParameterSomeBadIntegerValues() {
      String[] specifiedValues = {"5","4.05"};
      _httpRequest.setParameterValues("IntegerParameter",specifiedValues );

      try {
      Object[] temp = _integerParameter.transformValues(_httpRequest);
      // if you get here an exception was not raised
      assert(false);
      } catch (IllegalArgumentException iae) {
      assert(true);
      }
      }
    */


}
