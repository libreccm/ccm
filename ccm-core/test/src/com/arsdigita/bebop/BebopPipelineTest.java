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
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.IntegerParameter;
import com.arsdigita.util.RequestEnvironment;
import junit.framework.TestCase;

/**
 * @author Uday Mathur 
 * @version $Id: BebopPipelineTest.java 742 2005-09-02 10:29:31Z sskracic $
 * */
public class BebopPipelineTest extends TestCase {

    public static final String versionId = "$Id: BebopPipelineTest.java 742 2005-09-02 10:29:31Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    /**
       Constructs a test with the specified name.
    */
    protected Page _page;
    protected Form _form;
    protected TextField _text1;
    protected TextField _text2;

    private RequestEnvironment m_env;


    public BebopPipelineTest(String id) {
        super(id);
    }

    /**
       Sets up the test fixture.
    */
    protected void setUp() {

        _page = new Page();
        _form = new Form("TestForm");
        _text1 = new TextField("text1");
        _text2 = new TextField(new IntegerParameter("text2"));
        _form.add(_text1);
        _form.add(_text2);
        addListeners();
        _page.add(_form);
        m_env = new RequestEnvironment();

    }

    private void addListeners() {
        _form.addInitListener( new FormInitListener() {
                public void init(FormSectionEvent event) throws FormProcessException {

                    FormData data = event.getFormData();
                    data.put("first_name", "initValue");
                    data.put("last_name", "initValue");
                }
            }
                               );

        _form.addProcessListener( new FormProcessListener() {
                public void process(FormSectionEvent event) throws FormProcessException {

                    FormData data = event.getFormData();
                    data.put("first_name", "processedValue");
                    data.put("last_name", "processedValue");
                }
            }
                                  );
        _form.addProcessListener( new FormProcessListener() {
                public void process(FormSectionEvent event) throws FormProcessException {

                    FormData data = event.getFormData();
                    data.put("last_name", "processedValue2");
                }
            }
                                  );
    }


    /**
       Tears down the text fixture.
       Called after every test case method.
    */
    protected void tearDown() {
        _page = null;
        _form = null;
        _text1 = null;
        _text2 = null;
    }
    public void testPageLocking() {
        _page.lock();

        TextField _text3 = new TextField("text3");
        try {
            _form.add(_text3);
            fail("Added widget to locked form");
        } catch (IllegalStateException e) {}
        try {
            _page.add(_text3);
            fail("Added widget to locked page");
        } catch (IllegalStateException e) {}
        try {
            _form.addProcessListener( new FormProcessListener() {
                    public void process(FormSectionEvent event)
                        throws FormProcessException {
                    }
                }
                                      );
            fail("Added listener to locked form");
        } catch (IllegalStateException e) {}
        try {
            _form.addInitListener( new FormInitListener() {
                    public void init(FormSectionEvent event)
                        throws FormProcessException {
                    }
                }
                                   );
            fail("Added listener to locked form");
        } catch (IllegalStateException e) {}
    }

    public void TestListeners() {
        _form.addValidationListener( new FormValidationListener() {
                public void validate(FormSectionEvent event) throws FormProcessException {

                    FormData data = event.getFormData();
                    data.addError("first_name","testError");
                }
            }
                                     );
        _page.lock();
    }
}
