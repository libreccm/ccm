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

import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.parameters.EnumerationValidationListener;
import com.arsdigita.bebop.parameters.ParameterData;

public class EnumerationValidationListenerTest
    extends ValidationListenerTest {


    /**
       Constructs a test with the specified name.

       @param name Name of the test
    */


    public EnumerationValidationListenerTest(String id) {
        super(id);
    }

    public void testEnumerationEmptyParameter() {

        String[] allowedValues={"this","is","a","test"};
        String[] specifiedValues={};

        _httpRequest.setParameterValues("StringParameter", specifiedValues);
        _validationListener=new EnumerationValidationListener(allowedValues);
        _stringParameter.addParameterListener(_validationListener);

        createFormData();
        ParameterData data = _formData.getParameter("StringParameter");
        try {
            data.validate();
            assertTrue(data.isValid());
        } catch (FormProcessException fpe) {
            fail("FormProcessException fpe " + fpe.getMessage());
        }
    }

    public void testEnumerationEmptyAllowed() {

        String[] allowedValues={};
        String[] specifiedValues={"not a member"};

        _httpRequest.setParameterValues("StringParameter", specifiedValues);
        _validationListener=new EnumerationValidationListener(allowedValues);
        _stringParameter.addParameterListener(_validationListener);

        createFormData();
        ParameterData data = _formData.getParameter("StringParameter");
        try {
            data.validate();
            assertTrue(!data.isValid());
        } catch (FormProcessException fpe) {
            fail("FormProcessException fpe" + fpe.getMessage());
        }
    }

    public void testEnumerationBadParameter() {

        String[] allowedValues={"this","is","a","test"};
        String[] specifiedValues={"not a member"};

        _httpRequest.setParameterValues("StringParameter", specifiedValues);
        _validationListener=new EnumerationValidationListener(allowedValues);
        _stringParameter.addParameterListener(_validationListener);

        createFormData();
        ParameterData data = _formData.getParameter("StringParameter");
        try {
            data.validate();
            assertTrue(!data.isValid());
        } catch (FormProcessException fpe) {
            fail("FormProcessException fpe");
        }
    }
    /*
      array types used here
      public void testEnumerationBadParameterMix() {

      String[] allowedValues={"this","is","a","test"};
      String[] specifiedValues={"this","not a member"};

      _httpRequest.setParameterValues("StringParameter", specifiedValues);
      _validationListener=new EnumerationValidationListener(allowedValues);
      _stringParameter.addParameterListener(_validationListener);

      try {(!_validationListener.validate(_httpRequest,
      _stringParameter,
      _formData));

      }
    */

    public void testEnumerationBadParameterCase() {

        String[] allowedValues={"this","is","a","test"};
        String[] specifiedValues={"THIS"};

        _httpRequest.setParameterValues("StringParameter", specifiedValues);
        _validationListener=new EnumerationValidationListener(allowedValues,true);
        _stringParameter.addParameterListener(_validationListener);

        createFormData();
        ParameterData data = _formData.getParameter("StringParameter");
        try {
            data.validate();
            assertTrue(!data.isValid());
        } catch (FormProcessException fpe) {
            fail("FormProcessException fpe");
        }
    }
    public void testEnumerationGoodParameterCase() {

        String[] allowedValues={"this","is","a","test"};
        String[] specifiedValues={"THIS"};

        _httpRequest.setParameterValues("StringParameter", specifiedValues);
        _validationListener=new EnumerationValidationListener(allowedValues);
        _stringParameter.addParameterListener(_validationListener);

        createFormData();
        ParameterData data = _formData.getParameter("StringParameter");
        try {
            data.validate();
            assertTrue(data.isValid());
        } catch (FormProcessException fpe) {
            fail("FormProcessException fpe");
        }
    }

    public void testEnumerationGoodParametersCase() {

        String[] allowedValues={"this","is","a","test"};
        String[] specifiedValues={"THIS","test","test"};

        _httpRequest.setParameterValues("StringParameter", specifiedValues);
        _validationListener=new EnumerationValidationListener(allowedValues);
        _stringParameter.addParameterListener(_validationListener);

        createFormData();
        ParameterData data = _formData.getParameter("StringParameter");
        try {
            data.validate();
            assertTrue(data.isValid());
        } catch (FormProcessException fpe) {
            fail("FormProcessException fpe");
        }
    }
}
