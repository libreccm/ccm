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

import java.net.URL;
import java.util.TooManyListenersException;

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
import com.arsdigita.bebop.form.Date;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.StringInRangeValidationListener;
import com.arsdigita.london.terms.Domain;
import com.arsdigita.london.terms.Terms;
import com.arsdigita.london.util.ui.parameters.DomainObjectParameter;
import com.arsdigita.london.util.ui.parameters.URLParameter;
import com.arsdigita.util.UncheckedWrapperException;

public class DomainForm extends Form {

    private DomainObjectParameter m_domain;
    private TextField m_key;
    private TextField m_url;
    private TextField m_title;
    private TextArea m_desc;
    private TextField m_version;
    private Date m_released;
    private SaveCancelSection m_buttons;

    public DomainForm(String name,
                      DomainObjectParameter domain) {
        super(name, new SimpleContainer(Terms.XML_PREFIX + ":domainForm",
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
        m_key = new TextField("key");
        m_key.setSize(20);
        m_key.addValidationListener(new NotNullValidationListener());
        m_key.addValidationListener(new StringInRangeValidationListener(1, 20));
        m_key.setMetaDataAttribute("label", "Key");
        m_key.setHint("The short unique key for the domain, stable across versions");
        add(m_key);

        try {
            m_key.addPrintListener(new PrintListener() {
                public void prepare(PrintEvent e) {
                    TextField f = (TextField) e.getTarget();
                    if (e.getPageState().getValue(m_domain) != null) {
                        f.setReadOnly();
                    }
                }

            });
        } catch (TooManyListenersException ex) {
            throw new UncheckedWrapperException("cannot happen", ex);
        }

        m_url = new TextField(new URLParameter("url"));
        m_url.setSize(50);
        m_url.addValidationListener(new NotNullValidationListener());
        m_url.addValidationListener(new StringInRangeValidationListener(1, 255));
        m_url.setMetaDataAttribute("label", "URL");
        m_url.setHint("The unique URL defining the current version of the domain");
        add(m_url);

        m_title = new TextField("title");
        m_title.setSize(50);
        m_title.addValidationListener(new NotNullValidationListener());
        m_title.addValidationListener(new StringInRangeValidationListener(1, 300));
        m_title.setMetaDataAttribute("label", "Title");
        m_title.setHint("The short title of the domain");
        add(m_title);

        m_desc = new TextArea("description");
        m_desc.setRows(5);
        m_desc.setCols(50);
        m_desc.addValidationListener(new StringInRangeValidationListener(0, 4000));
        m_desc.setMetaDataAttribute("label", "Description");
        m_desc.setHint("The long description of the domain");
        add(m_desc);

        m_version = new TextField("version");
        m_version.setSize(20);
        m_version.addValidationListener(new NotNullValidationListener());
        m_version.addValidationListener(new StringInRangeValidationListener(1, 20));
        m_version.setMetaDataAttribute("label", "Version");
        m_version.setHint("The current version number, eg 1.00");
        add(m_version);

        m_released = new Date("released");
        m_released.addValidationListener(new NotNullValidationListener());
        m_released.setMetaDataAttribute("label", "Released");
        m_released.setHint("The release date of the current version");
        add(m_released);
    }

    private class DomainInitListener implements FormInitListener {

        public void init(FormSectionEvent ev)
                throws FormProcessException {
            PageState state = ev.getPageState();
            Domain domain = (Domain) state.getValue(m_domain);

            //m_key.setVisible(state, domain == null);

            if (domain == null) {
                m_key.setValue(state, null);
                m_url.setValue(state, null);
                m_title.setValue(state, null);
                m_desc.setValue(state, null);
                m_version.setValue(state, null);
                m_released.setValue(state, null);
            } else {
                m_key.setValue(state, domain.getKey());
                m_url.setValue(state, domain.getURL());
                m_title.setValue(state, domain.getTitle());
                m_desc.setValue(state, domain.getDescription());
                m_version.setValue(state, domain.getVersion());
                m_released.setValue(state, domain.getReleased());
            }
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

            if (domain == null) {
                domain = Domain.create((String) m_key.getValue(state),
                                       (URL) m_url.getValue(state),
                                       (String) m_title.getValue(state),
                                       (String) m_desc.getValue(state),
                                       (String) m_version.getValue(state),
                                       (java.util.Date) m_released.getValue(state));
                state.setValue(m_domain, domain);
            } else {
                //domain.setKey((String)m_key.getValue(state));
                domain.setURL((URL) m_url.getValue(state));
                domain.setTitle((String) m_title.getValue(state));
                domain.setDescription((String) m_desc.getValue(state));
                domain.setVersion((String) m_version.getValue(state));
                domain.setReleased((java.util.Date) m_released.getValue(state));
            }

            fireCompletionEvent(state);
        }

    }
}
