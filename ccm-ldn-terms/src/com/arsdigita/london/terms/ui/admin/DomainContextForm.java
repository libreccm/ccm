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
 */
package com.arsdigita.london.terms.ui.admin;

import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.StringInRangeValidationListener;
import com.arsdigita.london.terms.Domain;
import com.arsdigita.london.terms.Terms;
import com.arsdigita.london.util.ui.parameters.DomainObjectParameter;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.web.Application;
import com.arsdigita.web.ApplicationCollection;

import java.util.TooManyListenersException;

/**
 *
 *
 */
public class DomainContextForm extends Form {

    private DomainObjectParameter m_domain;

    private SingleSelect m_app;
    private TextField m_context;

    private SaveCancelSection m_buttons;

    public DomainContextForm(String name,
                             DomainObjectParameter domain) {
        super(name,
              new SimpleContainer("terms:domainContextForm",
                                  Terms.XML_NS));
        setRedirecting(true);

        m_domain = domain;

        addWidgets();

        m_buttons = new SaveCancelSection(new SimpleContainer());
        add(m_buttons);

        addInitListener(new DomainInitListener());
        addProcessListener(new DomainProcessListener());
        addSubmissionListener(new DomainSubmissionListener());
    }

    protected void addWidgets() {
        m_app = new SingleSelect(new DomainObjectParameter("app"));
        m_app.addValidationListener(new NotNullValidationListener());
        try {
            m_app.addPrintListener(new PrintListener() {
                public void prepare(PrintEvent e) {
                    SingleSelect t = (SingleSelect) e.getTarget();
                    t.clearOptions();
                    ApplicationCollection apps = Application
                            .retrieveAllApplications();
                    apps.addOrder(Application.PRIMARY_URL);
                    t.addOption(new Option(null, "--select one--"));
                    while (apps.next()) {
                        Application app = apps.getApplication();
                        t.addOption(new Option(app.getOID().toString(),
                                               app.getPath()));
                    }
                }
            });
        } catch (TooManyListenersException ex) {
            throw new UncheckedWrapperException("cannot happen", ex);
        }
        m_app.setMetaDataAttribute("label", "Application");
        m_app.setHint("Select an application to set the mapping for");

        m_context = new TextField("context");
        m_context.setSize(20);
        m_context.addValidationListener(new StringInRangeValidationListener(0, 20));
        m_context.setMetaDataAttribute("label", "Context");
        m_context.setHint("Leave blank to set the default mapping");

        add(m_app);
        add(m_context);
    }

    private class DomainInitListener implements FormInitListener {

        public void init(FormSectionEvent ev)
                throws FormProcessException {
            PageState state = ev.getPageState();

            m_app.setValue(state, null);
            m_context.setValue(state, null);
        }
    }

    private class DomainSubmissionListener implements FormSubmissionListener {

        public void submitted(FormSectionEvent ev)
                throws FormProcessException {
            PageState state = ev.getPageState();

            if (m_buttons.getCancelButton().isSelected(state)) {
                fireCompletionEvent(state);
                throw new FormProcessException("cancelled");
            }
        }
    }

    private class DomainProcessListener implements FormProcessListener {

        public void process(FormSectionEvent ev)
                throws FormProcessException {
            PageState state = ev.getPageState();
            Domain domain = (Domain) state.getValue(m_domain);

            Application app = (Application) m_app.getValue(state);

            domain.setAsRootForObject(app,
                                      (String) m_context.getValue(state));

            fireCompletionEvent(state);
        }
    }

}
