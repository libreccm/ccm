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

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormModel;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.event.ParameterListener;
import com.arsdigita.bebop.parameters.ArrayParameter;
import com.arsdigita.bebop.parameters.IntegerParameter;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.ParameterData;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.tools.junit.framework.BaseTestCase;
import com.arsdigita.util.HttpServletDummyRequest;
import java.util.Arrays;
import java.util.Iterator;

public class ArrayParameterTest extends BaseTestCase {

    public static final String ARRAY_PARAM = "ArrayParameter";

    /**
       Constructs a test with the specified name.

       @param name Name of the test
    */
    protected FormData _formData;
    protected ArrayParameter _array;
    protected FormModel _model;
    protected HttpServletDummyRequest _httpRequest;
    protected ParameterListener _validationListener;

    public ArrayParameterTest(String id) {
        super(id);
    }

    /**
       Sets up the test fixture.
    */

    protected void setUp() {
        _array =  new ArrayParameter(ARRAY_PARAM);
        _httpRequest = new HttpServletDummyRequest();

        _model = new FormModel("Validation Test");

        try {
            _model.addFormParam(_array);
        } catch (Exception e) {
            fail("Exception in trying to add parameter model" + e.getMessage());
        }

    }

    protected void createFormData(boolean isSubmission) {
        try {
            _formData = new FormData(_model, _httpRequest, isSubmission);
        } catch (FormProcessException fpe) {
            fail("Form Process Exception trying to create FormData");
        }
    }

    /**
       Tears down the text fixture.
       Called after every test case method.
    */
    protected void tearDown() {
        _formData=null;
        _array=null;
        _model=null;
        _httpRequest=null;
        _validationListener=null;
    }

    public void testOneElement() {
        // The value of this test greatly depends on that the one element in
        // the array looks like an encoded array, i.e. starts with a comma
        String[] specifiedValues= { ",one" };

        _httpRequest.setParameterValues(ARRAY_PARAM, specifiedValues);
        ParameterData p = createParameterData(_array);
        assertTrue("The parameter data should be a string array",
                   p.getValue() instanceof String[]);
        assertTrue(Arrays.equals(specifiedValues, (Object[]) p.getValue()));
    }

    public void testTwoElements() {

        String[] specifiedValues= { "one", "two" };

        _httpRequest.setParameterValues(ARRAY_PARAM, specifiedValues);
        ParameterData p = createParameterData(_array);
        assertTrue("The parameter data should be a string array",
                   p.getValue() instanceof String[]);
        assertTrue(Arrays.equals(specifiedValues, (Object[]) p.getValue()));
    }

    public void testMarshalling() {
        String[] specifiedValues = { ",one", ".,two", "three,a", "four.b",
                                     "five,", "six." };
        String value = _array.marshal(specifiedValues);
        _httpRequest.setParameterValues(ARRAY_PARAM, value);
        ParameterData p = createParameterData(_array);
        assertTrue(p.getValue() instanceof String[]);

        assertTrue(Arrays.equals(specifiedValues, (Object[]) p.getValue()));
        assertEquals(_array.marshal(specifiedValues), value);
        assertTrue(Arrays.equals(specifiedValues, (Object[]) _array.unmarshal(value)));
        assertTrue("The parameter data should be an array", p.isArray());
    }

    public void testMarshallingInteger() {
        _array.setElementParameter(new IntegerParameter("Integer"));
        assertEquals("Integer", _array.getName());
        int n = 5;
        String[] specifiedValues = new String[n];
        Integer[] values = new Integer[n];
        for (int i=0; i<n; i++) {
            values[i] = new Integer(i);
            specifiedValues[i] = values[i].toString();
        }
        _httpRequest.setParameterValues(_array.getName(), specifiedValues);
        ParameterData p = createParameterData(_array);
        assertTrue(p.getValue() instanceof Integer[]);
        assertTrue(Arrays.equals(values, (Object[]) p.getValue()));
        // Now try the same with a marshalled value
        _httpRequest.removeParameterValue(_array.getName());
        _httpRequest.setParameterValues(_array.getName(), p.marshal());
        p = createParameterData(_array);
        assertTrue(Arrays.equals(values, (Object[]) p.getValue()));
    }

    public void testElementValidation() {
        _array.getElementParameter().addParameterListener(
                                                          new NotNullValidationListener());
        String[] specifiedValues = { "one", "", null, "two" };
        _httpRequest.setParameterValues(ARRAY_PARAM, specifiedValues);
        ParameterData p = createParameterData(_array);
        _array.lock();
        try {
            _array.validate(p);
        } catch(FormProcessException e) {
            // expected
        }
        assertTrue(!p.isValid());
        int cnt = 0;
        for (Iterator i = p.getErrors(); i.hasNext(); i.next(), cnt++);
        // FIXME: This should really be 2, but the validation process bails out
        // after the first error in the values is found (the empty string) and
        // doesn't check the second erroneous value, the null
        assertEquals(1, cnt);
    }


    /**
     *  The next two tests make sure that even if the element
     *  begins with the SEP_CHAR value (in this case, a "-").  We
     *  need to test to make sure that we get back the same element that
     *  we started with.
     */
    public void testOneNegativeElement() {
        String[] values = {"-2"};
        String marshaledElement = _array.marshal(values);
        String[] unmarshaledElement = (String[]) _array.unmarshal(marshaledElement);
        final boolean equals = Arrays.equals(values,unmarshaledElement);
        if (!equals) {
            fail("The only argument went in negative and came out positive!. Was: " +  values[0] + " Now: " + unmarshaledElement[0]);
        }

    }


    public void testOnePositiveElement() {
        String[] values = {"2"};
        String marshaledElement = _array.marshal(values);
        Object unmarshaledElement = _array.unmarshal(marshaledElement);
        assertTrue("The only argument went in positive and came out negative",
                   Arrays.equals(values, (String[]) _array.unmarshal
                                 (marshaledElement)));

    }

    private ParameterData createParameterData(ParameterModel m) {
        return m.createParameterData(_httpRequest, true);
    }

}
