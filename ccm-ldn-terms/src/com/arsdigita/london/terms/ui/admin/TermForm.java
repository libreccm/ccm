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
import com.arsdigita.bebop.event.ParameterEvent;
import com.arsdigita.bebop.event.ParameterListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.RadioGroup;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.BooleanParameter;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.StringInRangeValidationListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.london.terms.Domain;
import com.arsdigita.london.terms.Term;
import com.arsdigita.london.terms.Terms;
import com.arsdigita.london.terms.Util;
import com.arsdigita.london.terms.util.TermsGlobalizationUtil;
import com.arsdigita.london.util.ui.parameters.DomainObjectParameter;
import com.arsdigita.util.UncheckedWrapperException;

public class TermForm extends Form {

    private DomainObjectParameter m_domain;
    private DomainObjectParameter m_term;

    private TextField m_uniqueid;
    private TextField m_name;
    private TextArea m_desc;
    private TextField m_shortcut;
    private RadioGroup m_inatoz;

    private SaveCancelSection m_buttons;

    public TermForm(String name,
                    DomainObjectParameter domain,
                    DomainObjectParameter term) {
        super(name, new SimpleContainer(Terms.XML_PREFIX + 
                                        ":termForm",
                                        Terms.XML_NS));
        setRedirecting(true);
        
        m_domain = domain;
        m_term = term;
        
        addWidgets();
        
        m_buttons = new SaveCancelSection(new SimpleContainer());
        add(m_buttons);

        addInitListener(new DomainInitListener());
        addProcessListener(new DomainProcessListener());
        addSubmissionListener(new DomainSubmissionListener());
    }
    
    protected void addWidgets() {
        m_uniqueid = new TextField(new StringParameter("uniqueID"));
        m_uniqueid.setSize(32);
        m_uniqueid.setMaxLength(128);
        m_uniqueid.addValidationListener(new NotNullValidationListener());
        m_uniqueid.addValidationListener(new UniqueValidationListener());
        m_uniqueid.setMetaDataAttribute("label", "Unique ID");
        m_uniqueid.setHint("The unique identifier within the domain");
        add(m_uniqueid);

        try {
            m_uniqueid.addPrintListener(new PrintListener() {
                    public void prepare(PrintEvent e) {
                        TextField f = (TextField)e.getTarget();
                        if (e.getPageState().getValue(m_term) != null) {
                            f.setReadOnly();
                        }
                    }
                });
        } catch (TooManyListenersException ex) {
            throw new UncheckedWrapperException("cannot happen", ex);
        }

        m_name = new TextField("name");
        m_name.setSize(50);
        m_name.addValidationListener(new NotNullValidationListener());
        m_name.addValidationListener(new StringInRangeValidationListener(1, 300));
        m_name.setMetaDataAttribute("label", "Name");
        m_name.setHint("The short name of the term");
        add(m_name);

        m_desc = new TextArea("description");
        m_desc.setRows(5);
        m_desc.setCols(50);
        m_desc.addValidationListener(new StringInRangeValidationListener(0, 4000));
        m_desc.setMetaDataAttribute("label", "Description");
        m_desc.setHint("The long description of the term");
        add(m_desc);

        m_shortcut = new TextField("shortcut");
        m_shortcut.setSize(20);
        m_shortcut.addValidationListener(new StringInRangeValidationListener(0, 50));
        m_shortcut.setMetaDataAttribute("label", "Shortcut");
        m_shortcut.setHint("A URL shortcut for viewing the term");
        add(m_shortcut);

        m_inatoz = new RadioGroup(new BooleanParameter("inAtoZ"));
        m_inatoz.addValidationListener(new NotNullValidationListener());
        m_inatoz.setMetaDataAttribute("label", "In A-Z");
        m_inatoz.setHint("Suitable for inclusion in an A-Z of terms");
        m_inatoz.addOption(new Option(Boolean.TRUE.toString(),
                                      "Yes"));
        m_inatoz.addOption(new Option(Boolean.FALSE.toString(),
                                      "No"));
        add(m_inatoz);
    }

    private class DomainInitListener implements FormInitListener {
        public void init(FormSectionEvent ev) 
            throws FormProcessException {
            PageState state = ev.getPageState();
            Term term = (Term)state.getValue(m_term);

            if (term == null) {
                Domain domain = (Domain)state.getValue(m_domain);
                m_uniqueid.setValue(state, Util.getNextTermID(domain));
                m_name.setValue(state, null);
                m_desc.setValue(state, null);
                m_shortcut.setValue(state, null);
                m_inatoz.setValue(state, Boolean.FALSE);
            } else {
                m_uniqueid.setValue(state, term.getUniqueID());
                m_name.setValue(state, term.getName());
                m_desc.setValue(state, term.getDescription());
                m_shortcut.setValue(state, term.getShortcut());
                m_inatoz.setValue(state, Boolean.valueOf(term.isInAtoZ()));
             }
        }
    }

    private class DomainSubmissionListener implements FormSubmissionListener {
        public void submitted(FormSectionEvent ev) 
            throws FormProcessException {
            PageState state = ev.getPageState();
            
            if (m_buttons.getCancelButton().isSelected(state)) {
                fireCompletionEvent(state);
                throw new FormProcessException(TermsGlobalizationUtil.globalize("terms.cancelled"));
            }
        }
    }
    
    private class DomainProcessListener implements FormProcessListener {
        public void process(FormSectionEvent ev) 
            throws FormProcessException {
            PageState state = ev.getPageState();
            Term term = (Term)state.getValue(m_term);

            if (term == null) {
                Domain domain = (Domain)state.getValue(m_domain);
                term = Term.create((String)m_uniqueid.getValue(state),
                                   (String)m_name.getValue(state),
                                   ((Boolean)m_inatoz.getValue(state))
                                   .booleanValue(),
                                   (String)m_shortcut.getValue(state),
                                   domain);
                 term.setDescription((String)m_desc.getValue(state));
                 state.setValue(m_term, term);
            } else {
                //term.setKey((String)m_key.getValue(state));
                term.setName((String)m_name.getValue(state));
                term.setDescription((String)m_desc.getValue(state));
                term.setShortcut((String)m_shortcut.getValue(state));
                term.setInAtoZ(((Boolean)m_inatoz.getValue(state)).booleanValue());
            }
            
            fireCompletionEvent(state);
        }
    }
   
    
    private class UniqueValidationListener implements ParameterListener {
       @Override
       public void validate(ParameterEvent e)
           throws FormProcessException {
            PageState state = e.getPageState();
            Term term = (Term)state.getValue(m_term);
            if (term == null) {
                String id = (String)m_uniqueid.getValue(state);
                
                Domain domain = (Domain)state.getValue(m_domain);
                try {
                    Term other = domain.getTerm(id);
                    
                    e.getParameterData().addError("A term with ID " + 
                                                  id + " already exists");
                } catch (DataObjectNotFoundException ex) {
                    // Expected !
                }
            }
       }
    }
}
