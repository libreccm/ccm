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
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.NumberParameter;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.dispatcher.BaseDispatcherServlet;
import com.arsdigita.dispatcher.RedirectException;
import com.arsdigita.dispatcher.RequestContext;
import com.arsdigita.util.URLRewriter;
import java.util.ArrayList;
import java.util.Collection;
import javax.servlet.Servlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.cactus.ServletTestCase;
import org.apache.cactus.WebRequest;

/**
 * Tests various BeBop form processing functionality
 * especially as per SDM bug #183490
 *
 * @author Anukul Kapoor
 */

public class BebopFormCactusTest extends ServletTestCase {

    public static final String versionId = "$Id: BebopFormCactusTest.java 748 2005-09-02 11:57:31Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";
    Servlet s;

    Page page;
    Form form;
    PageState pagestate;
    FormData formdata;

    boolean firedInitEvent;
    boolean firedValidationEvent;
    boolean firedProcessEvent;
    boolean firedSubmissionEvent;

    Collection listenerLog;

    public BebopFormCactusTest(String theName) {
        super(theName);
    }

    /** This instantiates a new form as part of the testing fixture.
     */

    public void initForm() {
        form = new Form("test");

        form.add(new TextField(new StringParameter("s_param")));
        form.add(new TextField(new NumberParameter("n_param")));
        form.add(new Submit("send_form"));

        page = new Page("Test Form");

        page.add(new Label("Hello, World!"));
        page.add(form);
    }

    public void setUp() {
        // Workaround for "(root cause: Request context does not subclass
        // KernelRequestContext:
        // com.arsdigita.formbuilder.test.DummyRequestContext)"
        com.arsdigita.util.URLRewriter.clearParameterProviders();

        initForm();

        s = new BaseDispatcherServlet() {
                protected RequestContext authenticateUser
                    (HttpServletRequest req,
                     HttpServletResponse resp,
                     RequestContext ctx)
                    throws RedirectException {
                    return ctx;
                }

                public void dispatch(HttpServletRequest req,
                                     HttpServletResponse resp,
                                     RequestContext actx)
                    throws javax.servlet.ServletException, java.io.IOException {
                    pagestate = page.process(request,response);
                    formdata = form.process(pagestate);
                }
            };

    }

    /** Simple validation that Init listener is run
     */

    public void beginFormInit(WebRequest req) {
        req.setURL("localhost", "/enterprise", "/foo.jsp", "", null);
    }

    public void testFormInit()
        throws javax.servlet.ServletException, java.io.IOException {
        firedInitEvent = false;

        form.addInitListener(
                             new FormInitListener() {
                                 public void init(FormSectionEvent e) {
                                     firedInitEvent = true;
                                 }
                             });

        page.lock();

        s.init(config);
        s.service(request,response);
        assertTrue(firedInitEvent);
    }

    public void setSubmissionVar(WebRequest req) {
        // this diddles the request to trigger a submission event
        req.addParameter(form.getModel().getMagicTagName(), "valid");
    }

    public void beginFormListeners(WebRequest req) {
        req.setURL("localhost", "/enterprise", "/foo.jsp", "", null);
        initForm();
        setSubmissionVar(req);
    }

    /** Tests that Process, Validation, and Form listeners are run
     *  and Init listeners are NOT run
     *  when processing a form submission
     */

    public void testFormListeners()
        throws javax.servlet.ServletException, java.io.IOException {

        firedProcessEvent = false;
        firedValidationEvent = false;
        firedSubmissionEvent = false;
        firedInitEvent = false;

        form.addProcessListener(
                                new FormProcessListener() {
                                    public void process(FormSectionEvent e) {
                                        firedProcessEvent = true;
                                    }
                                });

        form.addValidationListener(
                                   new FormValidationListener() {
                                       public void validate(FormSectionEvent e) {
                                           firedValidationEvent = true;
                                       }
                                   });

        form.addSubmissionListener(
                                   new FormSubmissionListener() {
                                       public void submitted(FormSectionEvent e) {
                                           firedSubmissionEvent = true;
                                       }
                                   });

        form.addInitListener(
                             new FormInitListener() {
                                 public void init(FormSectionEvent e) {
                                     firedInitEvent = true;
                                 }
                             });


        page.lock();

        s.init(config);
        s.service(request,response);

        assertTrue(firedProcessEvent);
        assertTrue(firedValidationEvent);
        assertTrue(firedSubmissionEvent);
        assertTrue(! firedInitEvent);
    }

    /** Inner class for testing various listeners.
     *  Records processing by stashing integer into listenerLog
     *  and throws exception if needed.
     */

    class TestListener
        implements FormSubmissionListener, FormInitListener,
                   FormProcessListener, FormValidationListener    {
        int num;
        boolean throwsException;

        public TestListener(int n, boolean t) {
            num = n;
            throwsException = t;
        }

        public TestListener(int n) {
            this(n,false);
        }

        public void submitted(FormSectionEvent e)
            throws FormProcessException {
            listenerLog.add(new Integer(num));
            if(throwsException) {
                throw new FormProcessException("Fake exception");
            }
        }

        public void init(FormSectionEvent e)
            throws FormProcessException {
            listenerLog.add(new Integer(num));
            if(throwsException) {
                throw new FormProcessException("Fake exception");
            }
        }

        public void validate(FormSectionEvent e)
            throws FormProcessException {
            listenerLog.add(new Integer(num));
            if(throwsException) {
                throw new FormProcessException("Fake exception");
            }
        }

        public void process(FormSectionEvent e)
            throws FormProcessException {
            listenerLog.add(new Integer(num));
            if(throwsException) {
                throw new FormProcessException("Fake exception");
            }
        }
    }

    public void beginFormSubmissionEarlyException(WebRequest req) {
        req.setURL("localhost", "/enterprise", "/foo.jsp", "", null);
        initForm();
        setSubmissionVar(req);
    }

    /**
     * Tests that all submission listeners are run despite an early exception
     */

    public void testFormSubmissionEarlyException()
        throws javax.servlet.ServletException, java.io.IOException {

        listenerLog = new ArrayList();

        form.addSubmissionListener(new TestListener(1,true));
        form.addSubmissionListener(new TestListener(2));
        form.addSubmissionListener(new TestListener(3));

        page.lock();

        s.init(config);
        s.service(request,response);

        assertTrue(listenerLog.contains(new Integer(1)));
        assertTrue(listenerLog.contains(new Integer(2)));
        assertTrue(listenerLog.contains(new Integer(3)));
    }

    public void beginFormSubmissionMiddleException(WebRequest req) {
        req.setURL("localhost", "/enterprise", "/foo.jsp", "", null);
        initForm();
        setSubmissionVar(req);
    }

    /**
     * Tests that all submission listeners are run despite an exception
     */

    public void testFormSubmissionMiddleException()
        throws javax.servlet.ServletException, java.io.IOException {


        listenerLog = new ArrayList();

        form.addSubmissionListener(new TestListener(1));
        form.addSubmissionListener(new TestListener(2,true));
        form.addSubmissionListener(new TestListener(3));

        page.lock();

        s.init(config);
        s.service(request,response);

        assertTrue(listenerLog.contains(new Integer(1)));
        assertTrue(listenerLog.contains(new Integer(2)));
        assertTrue(listenerLog.contains(new Integer(3)));
    }

    public void beginFormSubmissionLateException(WebRequest req) {
        req.setURL("localhost", "/enterprise", "/foo.jsp", "", null);
        initForm();
        setSubmissionVar(req);
    }

    /**
     * Tests that all submission listeners are run despite a late exception
     */

    public void testFormSubmissionLateException()
        throws javax.servlet.ServletException, java.io.IOException {

        listenerLog = new ArrayList();

        form.addSubmissionListener(new TestListener(1));
        form.addSubmissionListener(new TestListener(2));
        form.addSubmissionListener(new TestListener(3,true));

        page.lock();

        s.init(config);
        s.service(request,response);

        assertTrue(listenerLog.contains(new Integer(1)));
        assertTrue(listenerLog.contains(new Integer(2)));
        assertTrue(listenerLog.contains(new Integer(3)));
    }

    public void beginFormProcessListenerOrder(WebRequest req) {
        req.setURL("localhost", "/enterprise", "/foo.jsp", "", null);
        initForm();
        setSubmissionVar(req);
    }

    /**
     * Tests that form process listeners are run in the order added
     */

    public void testFormProcessListenerOrder()
        throws javax.servlet.ServletException, java.io.IOException {

        listenerLog = new ArrayList();

        form.addProcessListener(new TestListener(1));
        form.addProcessListener(new TestListener(2));
        form.addProcessListener(new TestListener(3));

        page.lock();

        s.init(config);
        s.service(request,response);

        java.util.List l = (java.util.List) listenerLog;

        assertTrue(l.get(0).equals(new Integer(1)));
        assertTrue(l.get(1).equals(new Integer(2)));
        assertTrue(l.get(2).equals(new Integer(3)));
    }

    public void beginFormValidationException(WebRequest req) {
        req.setURL("localhost", "/enterprise", "/foo.jsp", "", null);
        initForm();
        setSubmissionVar(req);
    }

    /**
     * Tests that validation listeners are run despite an exception
     */

    public void testFormValidationException()
        throws javax.servlet.ServletException, java.io.IOException {

        listenerLog = new ArrayList();

        form.addValidationListener(new TestListener(1));
        form.addValidationListener(new TestListener(2,true));
        form.addValidationListener(new TestListener(3));

        page.lock();

        s.init(config);
        s.service(request,response);

        assertTrue(listenerLog.contains(new Integer(1)));
        assertTrue(listenerLog.contains(new Integer(2)));
        assertTrue(listenerLog.contains(new Integer(3)));
    }

}
