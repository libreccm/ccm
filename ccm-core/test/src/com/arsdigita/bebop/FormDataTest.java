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

import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.event.ParameterListener;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.EnumerationValidationListener;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.NumberInRangeValidationListener;
import com.arsdigita.bebop.parameters.NumberParameter;
import com.arsdigita.bebop.parameters.ParameterData;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.tools.junit.framework.BaseTestCase;
import com.arsdigita.util.HttpServletDummyRequest;
import com.arsdigita.util.HttpServletDummyResponse;
import com.arsdigita.util.RequestEnvironment;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;

/**
 * @author Uday Mathur 
 * @version $Id: FormDataTest.java 742 2005-09-02 10:29:31Z sskracic $
 * */
public class FormDataTest extends BaseTestCase {


    /**
       Constructs a test with the specified name.

       @param name Name of the test
    */
    protected Form _form;
    protected Page _page;
    protected PageState _pageState;
    protected FormData _formData;
    protected ParameterModel _stringParameter;
    protected ParameterModel _numberParameter;
    protected FormModel _model;
    protected HttpServletDummyRequest _httpRequest;
    protected HttpServletDummyResponse _httpResponse;
    protected ParameterListener _validationListener;

    public FormDataTest(String id) {
        super(id);
    }

    /**
       Sets up the test fixture.
    */
    protected void setUp() {
        _form = new Form("testForm");
        createForm();
        RequestEnvironment env = new RequestEnvironment();
        _httpRequest = env.getRequest();
        _page = new Page();
        _page.add(_form);


    }

    /**s
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

    protected void createForm() {
        _stringParameter =  new StringParameter("StringParameter");
        _numberParameter =  new NumberParameter("NumberParameter");
        try {
            _form.add(new TextField(_stringParameter));
            _form.add(new TextField(_numberParameter));
        } catch (Exception e) {
            fail("Exception in trying to add parameter model" + e.getMessage());
        }
    }

    protected void createFormData() {
        createFormData(_form, _form, true);
    }

    /**
     * @param c the component to set visibility on
     * @param form the form to process
     * @param visible the visibility flag
     */
    protected void createFormData(Component c, Form form, boolean visible) {
        _page.lock();
        try {
            _pageState = _page.process(_httpRequest,_httpResponse);
            _pageState.setVisible(c, visible);
            // simulate the presence of a control event
            _formData = form.process(_pageState);
            _pageState.setAttribute(form, _formData);
            /*      if (!_formData.isSubmission()) {
                    _formData.forceValidate(_pageState);
                    }*/
        } catch (FormProcessException fpe) {
            fail("Form Process Exception trying to create FormData");
        } catch (ServletException e) {
            fail("ServletException in processing");
        }
    }

    protected void setSubmissionVar() {
        setSubmissionVar(_form.getModel());
    }

    protected void setSubmissionVar(FormModel model) {
        _httpRequest.setParameterValues(model.getMagicTagName(), "valid");
    }

    public void testFormDataEmpty() {

        createFormData();

        assertTrue(!_formData.isValid());
        assertTrue(_formData.isTransformed());
        assertTrue(!_formData.isSubmission());

    }

    public void testFormDataEmptyManualValidation() {

        createFormData();
        Page p = new Page();
        Form f = new Form("EmtpyForm");
        p.add(f);
        p.lock();


        _formData.forceValidate(_pageState);

        assertTrue(_formData.isValid());
        assertTrue(_formData.isTransformed());
        assertTrue(!_formData.isSubmission());

    }

    public void testFormDataEmptySubmission() {

        setSubmissionVar();
        createFormData();

        assertTrue(_formData.isTransformed());
        assertTrue(_formData.isSubmission());
        assertTrue(_formData.isValid());
    }

    public void testFormDataPopulatedInvalidEnumerationValidationFailure() {

        String[] allowedValues={"this","is","a","test"};
        String[] specifiedValues={"bogus"};

        _httpRequest.setParameterValues("StringParameter", specifiedValues);
        _validationListener=new EnumerationValidationListener(allowedValues);
        _stringParameter.addParameterListener(_validationListener);
        createFormData();
        assertTrue(!_formData.isValid());
        assertTrue(_formData.isTransformed());
        assertTrue(!_formData.isSubmission());
    }

    public void testFormDataPopulatedInvalidNumberTransformationFailure() {

        String[] specifiedValues={"bogus"};
        setSubmissionVar();
        _httpRequest.setParameterValues("NumberParameter", specifiedValues);
        _validationListener=new NumberInRangeValidationListener(5,6);
        _numberParameter.addParameterListener(_validationListener);
        _page.lock();
        createFormData();
        assertTrue(_formData.isTransformed());
        assertTrue(_formData.isSubmission());
        assertTrue(!_formData.getParameter("NumberParameter").isValid());
        assertTrue(_formData.getErrors("NumberParameter").hasNext());
        assertTrue(!_formData.isValid());
    }

    public void testFormDataParameterDefaultValueSubmission() {
        _httpRequest.setParameterValues("NumberParameter", "12");
        _validationListener=new NumberInRangeValidationListener(5,20);
        _numberParameter.setDefaultValue(new Integer(42));
        _numberParameter.addParameterListener(_validationListener);
        setSubmissionVar();
        _page.lock();
        createFormData();

        assertTrue(_formData.isTransformed());
        assertTrue(_formData.isSubmission());
        try {
            assertEquals(_formData.get("NumberParameter").toString(), "12");
            ParameterData data = _formData.getParameter("NumberParameter");
            data.validate();
            assertTrue(data.isValid());
        } catch (FormProcessException fpe) {
            fail(fpe.getMessage());
        }
        assertEquals (_formData.get("NumberParameter").toString(), "12");
        assertTrue(_formData.isValid());
    }

    public void testFormDataRequestValueSubmission() {
        _validationListener=new NumberInRangeValidationListener(5,20);
        _numberParameter.setDefaultValue(new Integer(30));
        _numberParameter.addParameterListener(_validationListener);
        _numberParameter.addParameterListener(new NotNullValidationListener());
        setSubmissionVar();
        _page.lock();
        createFormData();

        assertTrue(_formData.isTransformed());
        assertTrue(_formData.isSubmission());
        assertEquals(null, _formData.get("NumberParameter"));
    }

    public void testFormDataParameterDefaultValueNoSubmission() {
        _validationListener=new NumberInRangeValidationListener(5,20);
        _numberParameter.setDefaultValue(new Integer(10));
        _numberParameter.addParameterListener(_validationListener);

        _page.lock();
        createFormData();

        assertTrue(_formData.isTransformed());
        assertTrue(!_formData.isSubmission());
        assertEquals (_numberParameter.getDefaultValue().toString(), "10");
        assertEquals (_formData.get("NumberParameter").toString(), "10");
    }

    public void testFormDataPopulatedInvalid() {

        String[] allowedValues={"this","is","a","test"};
        String[] specifiedValues={"bogus"};

        _httpRequest.setParameterValues("StringParameter", specifiedValues);
        _validationListener=new EnumerationValidationListener(allowedValues);
        _stringParameter.addParameterListener(_validationListener);
        createFormData();

        assertTrue(!_formData.isValid());
        assertTrue(_formData.isTransformed());
        assertTrue(!_formData.isSubmission());
    }

    public void testFormDataPopulatedValid() {

        String[] allowedValues={"this","is","a","test"};
        String[] specifiedValues={"test"};

        setSubmissionVar();
        _httpRequest.setParameterValues("StringParameter", specifiedValues);
        _validationListener=new EnumerationValidationListener(allowedValues);
        _stringParameter.addParameterListener(_validationListener);
        _page.lock();
        createFormData();

        assertEquals(_formData.get("NumberParameter"), null);//HERE
        assertTrue(!_formData.getErrors("StringParameter").hasNext());
        assertTrue(!_formData.getErrors("NumberParameter").hasNext());
        assertTrue(!_formData.getErrors().hasNext());

        assertTrue(_formData.isValid());//HERE
        assertTrue(_formData.isTransformed());
    }

    public void testFormDataRevalidateBadType() {

        String[] specifiedValues={"bogus"};

        setSubmissionVar();
        _httpRequest.setParameterValues("NumberParameter", specifiedValues);
        _validationListener=new NumberInRangeValidationListener(2,6);
        _numberParameter.addParameterListener(_validationListener);
        _page.lock();
        createFormData();

        assertTrue(_formData.isTransformed());
        assertTrue(_formData.getErrors("NumberParameter").hasNext());
        assertTrue(!_formData.getParameter("NumberParameter").isValid());
        assertTrue(!_formData.isValid());

        //fix bad value
        _formData.put("NumberParameter", new Integer(5));
        _formData.forceValidate(_pageState);

        assertTrue(_formData.isTransformed());
        assertEquals(_formData.get("NumberParameter").toString(),"5");
        assertTrue(!_formData.getErrors("NumberParameter").hasNext());
        assertTrue(_formData.isValid());

        //fix bad value
        _formData.put("NumberParameter", null);
        _formData.forceValidate(_pageState);

        assertTrue(_formData.isTransformed());
        assertTrue(_formData.isSubmission());
        assertEquals(_formData.get("NumberParameter"), null);
        assertTrue(!_formData.getErrors("NumberParameter").hasNext());
        assertTrue(_formData.isValid());

        //fix bad value
        _formData.put("NumberParameter", "Garbled");
        _formData.forceValidate(_pageState);

        assertTrue(_formData.isTransformed());
        assertTrue(_formData.isSubmission());
        assertEquals(_formData.get("NumberParameter"), "Garbled");
        assertTrue(_formData.getErrors("NumberParameter").hasNext());
        assertTrue(!_formData.isValid());
    }

    public void testFormDataValidateNullNumber() {
        setSubmissionVar();
        _httpRequest.setParameterValues("NumberParameter", "");
        _page.lock();
        createFormData();

        assertTrue(_formData.isTransformed());
        assertTrue(_formData.isSubmission());
        assertEquals(_formData.get("NumberParameter"),null);

        /*
        //      _pageState = _pageModel.process(_httpRequest,_httpResponse);
        _formData.forceValidate(_pageState);

        assert(_formData.isTransformed());
        assert(!_formData.isSubmission());
        assertEquals(_formData.getErrors("NumberParameter").length,1);
        assertEquals(_formData.get("NumberParameter"),null);
        assert(!_formData.isValid());
        */
    }

    /**
     * Tests that <code>FormData.get(String)</code> throws
     * an exception if the form parameter is not in the form model.
     */
    public void testFormDataGetParameterNotInModel() {
        setSubmissionVar();
        _page.lock();
        createFormData();
        try {
            _formData.get("bogusParameter");
            fail("getting parameter not in model failed silently");
        } catch (RuntimeException re) {
            assertTrue("getting parameter not in model threw RuntimeException", true);
        }
    }

    /**
     * Verifies that <code>FormData.getParameter(String)</code> returns
     * null if the form parameter is in the form model but not in the request.
     */
    public void testFormDataGetParameterInModelButNotInRequest() {
        setSubmissionVar();
        _page.lock();
        createFormData();

        assertTrue(_formData.isTransformed());
        assertTrue(_formData.isSubmission());
        assertTrue(!_formData.getErrors("NumberParameter").hasNext());
        assertTrue(_formData.isValid());
        assertEquals(_formData.get("NumberParameter"),null);
    }

    /**
     * Verifies that init listeners run with the form visible.
     */
    public void testInitListenerFormVisible() {
        _form.addInitListener(new FormInitListener() {
                public void init(FormSectionEvent fse) {
                    ServletRequest req = fse.getPageState().getRequest();
                    req.setAttribute("executedInit", "true");
                }
            });
        createFormData();
        assertEquals(_httpRequest.getAttribute("executedInit"), "true");
    }

    /**
     * Verifies that init listeners don't run when the form is invisible.
     */
    public void testInitListenerFormInvisible() {
        _form.addInitListener(new FormInitListener() {
                public void init(FormSectionEvent fse) {
                    fail("init listener shouldn't run on invisible form");
                }
            });
        createFormData(_form, _form, false);
    }

    /**
     * Verifies that init listeners don't run when the form is
     * visible inside an invisible container
     */
    public void testInitListenerFormInvisibleNested() {
        Form innerForm = new Form("innerForm");
        innerForm.addInitListener(new FormInitListener() {
                public void init(FormSectionEvent fse) {
                    fail("init listener shouldn't run on invisible form");
                }
            });
        BoxPanel bp = new BoxPanel();
        _page.add(bp);
        bp.add(innerForm);
        createFormData(bp, innerForm, false);
        assertTrue("boxpanel invisible", !_pageState.isVisible(bp));
        assertTrue("inner form visible", _pageState.isVisible(innerForm));
    }

    /**
     * Verifies that submit listeners run with the form visible.
     */
    public void testSubmitListenerFormVisible() {
        _form.addSubmissionListener(new FormSubmissionListener() {
                public void submitted(FormSectionEvent fse) {
                    ServletRequest req = fse.getPageState().getRequest();
                    req.setAttribute("executedSubmit", "true");
                }
            });
        _form.addProcessListener(new FormProcessListener() {
                public void process(FormSectionEvent fse) {
                    ServletRequest req = fse.getPageState().getRequest();
                    req.setAttribute("executedProcess", "true");
                }
            });
        setSubmissionVar();
        createFormData();
        assertEquals(_httpRequest.getAttribute("executedSubmit"), "true");
        assertEquals(_httpRequest.getAttribute("executedProcess"), "true");
    }

    /**
     * Verifies that submit listeners don't run when the form is invisible.
     */
    public void testSubmitListenerFormInvisible() {
        _form.addSubmissionListener(new FormSubmissionListener() {
                public void submitted(FormSectionEvent fse) {
                    fail("submit listener shouldn't run on invisible form");
                }
            });
        _form.addProcessListener(new FormProcessListener() {
                public void process(FormSectionEvent fse) {
                    fail("process listener shouldn't run on invisible form");
                }
            });
        setSubmissionVar();
        createFormData(_form, _form, false);
    }

    /**
     * Verifies that submit listeners don't run when the form is visible
     * but inside an invisible container.
     */
    public void testSubmitListenerFormInvisibleNested() {
        Form innerForm = new Form("innerForm");
        innerForm.addSubmissionListener(new FormSubmissionListener() {
                public void submitted(FormSectionEvent fse) {
                    fail("submit listener shouldn't run on invisible form");
                }
            });
        innerForm.addProcessListener(new FormProcessListener() {
                public void process(FormSectionEvent fse) {
                    fail("process listener shouldn't run on invisible form");
                }
            });
        BoxPanel bp = new BoxPanel();
        _page.add(bp);
        bp.add(innerForm);
        setSubmissionVar(innerForm.getModel());
        // bp is invisible, innerform is visible.
        createFormData(bp, innerForm, false);
        assertTrue("boxpanel invisible", !_pageState.isVisible(bp));
        assertTrue("inner form visible", _pageState.isVisible(innerForm));
    }
}
