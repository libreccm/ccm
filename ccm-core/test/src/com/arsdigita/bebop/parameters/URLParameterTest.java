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

import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.URLParameter;
import com.arsdigita.tools.junit.framework.BaseTestCase;
import com.arsdigita.util.HttpServletDummyRequest;
import java.lang.IllegalArgumentException;

public class URLParameterTest extends BaseTestCase {

    public static final String versionId = "$Id: URLParameterTest.java 742 2005-09-02 10:29:31Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    /**
       Constructs a test with the specified name.

       @param name Name of the test
    */

    protected ParameterModel _URLParameter;
    protected HttpServletDummyRequest _httpRequest;

    public URLParameterTest(String id) {
        super(id);
    }

    /**
       Sets up the test fixture.
    */

    protected void setUp() {
        _URLParameter =  new URLParameter("URLParameter");
        _httpRequest = new HttpServletDummyRequest();
    }

    /**
       Tears down the text fixture.
       Called after every test case method.
    */

    protected void tearDown() {
        _URLParameter=null;
        _httpRequest=null;
    }

    public void testURLParameterNoValues() {

        String[] specifiedValues = {};
        _httpRequest.setParameterValues("URLParameter",specifiedValues );
        assertEquals(_URLParameter.transformValue(_httpRequest),null);

    }

    public void testURLParameterGoodValue() {
        String[] specifiedValues = {"http://www.arsdigita.com"};
        _httpRequest.setParameterValues("URLParameter",specifiedValues );
        try {
            Object temp = _URLParameter.transformValue(_httpRequest);
            assertEquals(specifiedValues[0], temp.toString());
        } catch (IllegalArgumentException iae) {
            fail(iae.toString());
        }
    }


    public void testURLParameterGoodValueWithPath() {
        String[] specifiedValues = {"http://www.arsdigita.com/doc"};
        _httpRequest.setParameterValues("URLParameter",specifiedValues );
        try {
            Object temp = _URLParameter.transformValue(_httpRequest);
            assertEquals(specifiedValues[0], temp.toString());
        } catch (IllegalArgumentException iae) {
            fail(iae.toString());
        }
    }


    public void testURLParameterGoodValueWithoutProtocol() {
        String[] specifiedValues = {"www.arsdigita.com"};
        _httpRequest.setParameterValues("URLParameter",specifiedValues );
        try {
            Object temp = _URLParameter.transformValue(_httpRequest);
            assertEquals(specifiedValues[0], temp.toString());
        } catch (IllegalArgumentException iae) {
            fail(iae.toString());
        }
    }


    public void testURLParameterGoodValueNotFullyQualified() {
        String[] specifiedValues = {"ultrawoman.sf"};
        _httpRequest.setParameterValues("URLParameter",specifiedValues );
        try {
            Object temp = _URLParameter.transformValue(_httpRequest);
            assertEquals(specifiedValues[0], temp.toString());
        } catch (IllegalArgumentException iae) {
            fail(iae.toString());
        }
    }
}
