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

import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.tools.junit.framework.BaseTestCase;
import com.arsdigita.util.HttpServletDummyRequest;
import java.lang.IllegalArgumentException;
import java.math.BigDecimal;

public class BigDecimalParameterTest extends BaseTestCase {

    public static final String versionId = "$Id: BigDecimalParameterTest.java 742 2005-09-02 10:29:31Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    /**
       Constructs a test with the specified name.

       @param name Name of the test
    */
    protected ParameterModel _stringParameter;
    protected ParameterModel _numberParameter;
    protected HttpServletDummyRequest _httpRequest;

    public BigDecimalParameterTest(String id) {
        super(id);
    }

    /**
       Sets up the test fixture.
    */

    protected void setUp() {
        _stringParameter =  new StringParameter("StringParameter");
        _numberParameter =  new BigDecimalParameter("NumberParameter");
        _httpRequest = new HttpServletDummyRequest();

    }

    /**
       Tears down the text fixture.
       Called after every test case method.
    */

    protected void tearDown() {

        _stringParameter=null;
        _numberParameter=null;
        _httpRequest=null;

    }
    public void testNumberParameterNoValues() {

        String[] specifiedValues = {};
        _httpRequest.setParameterValues("NumberParameter",specifiedValues );
        assertEquals(_numberParameter.transformValue(_httpRequest), null);

    }

    public void testNumberParameterEmptyValue() {

        String[] specifiedValues = {""};
        _httpRequest.setParameterValues("NumberParameter",specifiedValues );
        assertEquals(_numberParameter.transformValue(_httpRequest), null);
    }

    public void testNumberParameterBadValue() {

        String[] specifiedValues = {"Not a Number"};
        _httpRequest.setParameterValues("NumberParameter",specifiedValues );
        try {
            Integer temp = (Integer)_numberParameter.transformValue(_httpRequest);
            // if you get here an exception was not raised
            fail();
        } catch (IllegalArgumentException iae) {
            // ok.
        }
    }

    public void testNumberParameterGoodIntegerValue() {
        String[] specifiedValues = {"5"};
        _httpRequest.setParameterValues("NumberParameter",specifiedValues );
        assertEquals(_numberParameter.transformValue(_httpRequest),new BigDecimal("5"));
    }

    public void testNumberParameterGoodFloatValue() {
        String[] specifiedValues = {"5.01"};
        _httpRequest.setParameterValues("NumberParameter",specifiedValues );
        assertEquals(_numberParameter.transformValue(_httpRequest),new BigDecimal("5.01"));
    }
}
