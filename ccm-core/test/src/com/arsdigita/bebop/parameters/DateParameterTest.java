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

import com.arsdigita.bebop.parameters.DateParameter;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.tools.junit.framework.BaseTestCase;
import com.arsdigita.util.HttpServletDummyRequest;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateParameterTest extends BaseTestCase {


    /**
       Constructs a test with the specified name.

       @param name Name of the test
    */

    protected ParameterModel _dateParameter;
    protected HttpServletDummyRequest _httpRequest;

    public DateParameterTest(String id) {
        super(id);
    }

    /**
       Sets up the test fixture.
    */

    protected void setUp() {
        _dateParameter =  new DateParameter("DateParameter");
        _httpRequest = new HttpServletDummyRequest();
    }

    /**
       Tears down the text fixture.
       Called after every test case method.
    */

    protected void tearDown() {
        _dateParameter=null;
        _httpRequest=null;
    }

    public void testDateParameterNoValues() {
        String[] specifiedValues = {};
        _httpRequest.setParameterValues("DateParameter",specifiedValues );
        assertEquals(_dateParameter.transformValue(_httpRequest),null);
    }

    public void testDateParameterYYYYMMDD() {
        _httpRequest.setParameterValues("DateParameter.year","1978");
        _httpRequest.setParameterValues("DateParameter.month","9");
        _httpRequest.setParameterValues("DateParameter.day","10");

        assertTrue(_dateParameter.transformValue(_httpRequest) instanceof Date);
        Date input = (Date) _dateParameter.transformValue(_httpRequest);

        Date expected = new GregorianCalendar(1978,9,10,0,0,0).getTime();
        assertEquals(expected, input);
    }

    public void testMarshalling() {
        _httpRequest.setParameterValues("DateParameter.year","1978");
        _httpRequest.setParameterValues("DateParameter.month","9");
        _httpRequest.setParameterValues("DateParameter.day","10");

        Date input = (Date) _dateParameter.transformValue(_httpRequest);
        long inputTime = input.getTime();
        String marshalled = _dateParameter.marshal(input);
        // The first arg below may change if the date parameter changes
        // how it marshals a date
        assertEquals(Long.toString(input.getTime()), marshalled);
        Date unmarshalled = (Date) _dateParameter.unmarshal(marshalled);
        assertEquals(input, unmarshalled);


        // Now do the same test with a marshalled parameter value
        _httpRequest.removeParameterValue("DateParameter.year");
        _httpRequest.removeParameterValue("DateParameter.month");
        _httpRequest.removeParameterValue("DateParameter.day");
        _httpRequest.setParameterValues("DateParameter", marshalled);

        input = (Date) _dateParameter.transformValue(_httpRequest);
        assertEquals(input.getTime(), inputTime);
        assertEquals(marshalled, _dateParameter.marshal(input));
        marshalled = _dateParameter.marshal(input);
        unmarshalled = (Date) _dateParameter.unmarshal(marshalled);
        assertEquals(input, unmarshalled);
    }

}
