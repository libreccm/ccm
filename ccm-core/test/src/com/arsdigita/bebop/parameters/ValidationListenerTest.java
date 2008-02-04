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
import com.arsdigita.bebop.parameters.NumberParameter;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.tools.junit.framework.BaseTestCase;
import com.arsdigita.util.HttpServletDummyRequest;
import com.arsdigita.util.RequestEnvironment;

public class ValidationListenerTest extends BaseTestCase {

    public static final String versionId = "$Id: ValidationListenerTest.java 742 2005-09-02 10:29:31Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    /**
       Constructs a test with the specified name.

       @param name Name of the test
    */
    protected FormData _formData;
    protected ParameterModel _stringParameter;
    protected ParameterModel _numberParameter;
    protected FormModel _model;
    protected HttpServletDummyRequest _httpRequest;
    protected ParameterListener _validationListener;
    private RequestEnvironment m_env;

    public ValidationListenerTest(String id) {
        super(id);
    }

    /**
       Sets up the test fixture.
    */

    protected void setUp() {
        _stringParameter =  new StringParameter("StringParameter");
        _numberParameter =  new NumberParameter("NumberParameter");
        m_env = new RequestEnvironment();
        _httpRequest = m_env.getRequest();

        _model = new FormModel("Validation Test");

        try {
            _model.addFormParam(_stringParameter);
            _model.addFormParam(_numberParameter);
        } catch (Exception e) {
            fail("Exception in trying to add parameter model" + e.getMessage());
        }

    }

    protected void createFormData() {
        _model.lock();
        try {
            _formData = new FormData(_model, _httpRequest, true);
        } catch (FormProcessException fpe) {
            fail("Form Process Exception trying to create FormData " + fpe.getMessage());
        }
    }
    /**
       Tears down the text fixture.
       Called after every test case method.
    */

    protected void tearDown() {
        _formData=null;
        _stringParameter=null;
        _numberParameter=null;
        _model=null;
        _httpRequest=null;
        _validationListener=null;
    }

    public void testDummy() {
        // FIXME: Add tests for validation listeners
    }
}
