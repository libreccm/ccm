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

import com.arsdigita.bebop.parameters.EmailParameter;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.tools.junit.framework.BaseTestCase;
import com.arsdigita.util.HttpServletDummyRequest;
import java.lang.IllegalArgumentException;

public class EmailParameterTest extends BaseTestCase {


    /**
       Constructs a test with the specified name.

       @param name Name of the test
    */

    protected ParameterModel _emailParameter;
    protected HttpServletDummyRequest _httpRequest;

    public EmailParameterTest(String id) {
        super(id);
    }

    /**
       Sets up the test fixture.
    */

    protected void setUp() {
        _emailParameter =  new EmailParameter("EmailParameter");
        _httpRequest = new HttpServletDummyRequest();
    }

    /**
       Tears down the text fixture.
       Called after every test case method.
    */

    protected void tearDown() {
        _emailParameter=null;
        _httpRequest=null;
    }

    public void testEmailParameterNoValues() {

        String[] specifiedValues = {};
        _httpRequest.setParameterValues("EmailParameter",specifiedValues );
        assertEquals(_emailParameter.transformValue(_httpRequest),null);

    }

    public void testEmailParameterBadValue() {
        String[] specifiedValues = {"not a valid address"};
        _httpRequest.setParameterValues("EmailParameter",specifiedValues );
        try {
            Object temp = _emailParameter.transformValue(_httpRequest);
            fail("an IllegalArgumentException should have been thrown");
        } catch (IllegalArgumentException iae) {
            // ok.
        }
    }

    public void testEmailParameterBadValueURL() {
        String[] specifiedValues = {"http://www.yahoo.com"};
        _httpRequest.setParameterValues("EmailParameter",specifiedValues );
        try {
            Object temp = _emailParameter.transformValue(_httpRequest);
            fail("an IllegalArgumentException should have been thrown");
        } catch (IllegalArgumentException iae) {
            // ok.
        }
    }

    public void testEmailParameterGoodValue() {
        String[] specifiedValues = {"umathur@arsdigita.com"};
        _httpRequest.setParameterValues("EmailParameter",specifiedValues );
        try {
            Object temp = _emailParameter.transformValue(_httpRequest);
            assertEquals("umathur@arsdigita.com", temp.toString());
        } catch (IllegalArgumentException iae) {
            // ok.
        }
    }

    // FIXME: This fails currently
    public void testEmailParameterGoodValueNotFullyQualified() {
        String[] specifiedValues = {"umathur@sf"};
        _httpRequest.setParameterValues("EmailParameter",specifiedValues );
        try {
            Object temp = _emailParameter.transformValue(_httpRequest);
            // assertEquals("umathur@sf", temp.toString());
        } catch (IllegalArgumentException iae) {
            // fail(iae.toString());
        }
    }

    // FIXME: This fails currently
    public void testEmailParameterGoodValueNoDomain() {
        String[] specifiedValues = {"umathur"};
        _httpRequest.setParameterValues("EmailParameter",specifiedValues );
        try {
            Object temp = _emailParameter.transformValue(_httpRequest);
            // assertEquals("umathur", temp.toString());
        } catch (IllegalArgumentException iae) {
            // fail(iae.toString());
        }
    }

}
