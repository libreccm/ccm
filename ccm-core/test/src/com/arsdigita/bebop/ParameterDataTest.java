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

import com.arsdigita.bebop.event.ParameterListener;
import com.arsdigita.bebop.parameters.ArrayParameter;
import com.arsdigita.bebop.parameters.NumberParameter;
import com.arsdigita.bebop.parameters.ParameterData;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.tools.junit.framework.BaseTestCase;
import com.arsdigita.util.HttpServletDummyRequest;

/**
 * @author Uday Mathur 
 * @version $Id: ParameterDataTest.java 742 2005-09-02 10:29:31Z sskracic $
 *
 */

public class ParameterDataTest extends BaseTestCase {

    /**
     * Constructs a test with the specified name.
     * @param name Name of the test
     */

    protected ParameterData _parameterData;
    protected ParameterModel _stringParameter;
    protected ParameterModel _numberParameter;
    protected ParameterModel _arrayParameter;
    protected HttpServletDummyRequest _httpRequest;
    protected ParameterListener _validationListener;

    public ParameterDataTest(String id) {
        super(id);
    }

    /**
     * Sets up the test fixture.
     */
    protected void setUp() {
        _stringParameter = new StringParameter("StringParameter");
        _numberParameter = new NumberParameter("NumberParameter");
        _arrayParameter = new ArrayParameter("ArrayParameter");
        _httpRequest = new HttpServletDummyRequest();
    }

    /**
     * Tears down the text fixture.
     * Called after every test case method.
     */

    protected void tearDown() {
        _parameterData=null;
        _stringParameter=null;
        _numberParameter=null;
        _httpRequest=null;
        _validationListener=null;
    }

    public void testParameterDataEmpty() {

        _parameterData = _stringParameter.createParameterData(_httpRequest);

        assertEquals(_parameterData.getValue(), null);
    }

    public void testParameterDataString() {

        _httpRequest.setParameterValues("StringParameter", "sample");
        _stringParameter.setDefaultValue("greble");

        // default should override existing only if this is explicitly
        // flagged as not a pass-in.
        _parameterData = _stringParameter.createParameterData(_httpRequest, false);
        assertEquals("sample", _parameterData.getValue());
        _stringParameter.setPassIn(false);
        _parameterData = _stringParameter.createParameterData(_httpRequest, false);
        assertEquals("greble", _parameterData.getValue());

        // value of 'pass in' should be ignored if this is a request.
        _parameterData = _stringParameter.createParameterData(_httpRequest, true);
        assertEquals("sample", _parameterData.getValue());
        _stringParameter.setPassIn(true);
        _parameterData = _stringParameter.createParameterData(_httpRequest, true);
        assertEquals("sample", _parameterData.getValue());
    }

    public void testParameterDataInteger() {

        _httpRequest.setParameterValues("NumberParameter", "10");
        _parameterData = _numberParameter.createParameterData(_httpRequest, true);

        assertEquals(_parameterData.getValue().toString(), "10");
        assertTrue("must be number" , _parameterData.getValue() instanceof Number);
        assertTrue("must be integer", _parameterData.getValue() instanceof Integer);
        assertTrue("mustn't be float",
                   ! (_parameterData.getValue() instanceof Float));
    }

    public void testParameterDataFloat() {

        _httpRequest.setParameterValues("NumberParameter", "10.01");
        _parameterData = _numberParameter.createParameterData(_httpRequest, true);

        assertEquals(_parameterData.getValue().toString(), "10.01");
        assertTrue(_parameterData.getValue() instanceof Number);
        assertTrue(! (_parameterData.getValue() instanceof Integer));
        assertTrue(_parameterData.getValue() instanceof Float);
        assertTrue(! (_parameterData.getValue() instanceof Object[]));
    }

    public void testParameterDataNumberInvalid() {

        _httpRequest.setParameterValues("NumberParameter", "foo");
        _parameterData = _numberParameter.createParameterData(_httpRequest, true);

        assertEquals(_parameterData.getValue(), null);
        assertEquals("NumberParameter should be a Number but is 'foo'",
                     ((GlobalizedMessage)_parameterData.getErrors().next()).getKey());
    }

    public void testParameterDataArray() {

        String[] _specifiedValues = {"a","b","c","d"};
        _httpRequest.setParameterValues("ArrayParameter", _specifiedValues );
        _parameterData = _arrayParameter.createParameterData(_httpRequest, true);

        assertTrue((_parameterData.getValue() instanceof Object[]));
        assertTrue((_parameterData.getValue() instanceof Object));
        assertTrue(_parameterData.isArray());
    }

}
