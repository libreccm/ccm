/*
 * Copyright (C) 2004 ArsDigita Corporation. All Rights Reserved.
 *
 * The contents of this file are subject to the ArsDigita Public 
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.arsdigita.com/ADPL.txt
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */


package com.arsdigita.london.search.ui.admin;

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.CancellableValidationListener;
import com.arsdigita.bebop.parameters.NotEmptyValidationListener;
import com.arsdigita.bebop.parameters.TrimmedStringParameter;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.london.search.SponsoredLink;

import java.net.MalformedURLException;
import java.net.URL;

public class SponsoredLinkForm extends Form {
    
    private TextField m_title;
    private TextField m_term;
    private TextField m_url;

    private SaveCancelSection m_buttons;

    private ACSObjectSelectionModel m_link;

    public SponsoredLinkForm(String name,
                             ACSObjectSelectionModel link) {
	super(name);
	
	m_link = link;

	m_buttons = new SaveCancelSection();
	
	addWidgets(name);
	add(m_buttons, ColumnPanel.FULL_WIDTH);
	
	addProcessListener(new SponsoredLinkFormProcessListener());
	addValidationListener(new SponsoredLinkFormValidationListener());
	addInitListener(new SponsoredLinkFormInitListener());
    }
    
    protected void addWidgets(String name) {
        RequestLocal isCancelled = new RequestLocal() {
                protected Object initialValue(PageState state) {
                    if (m_buttons.getCancelButton().isSelected(state)) {
                        // Only consider the form cancelled if they actually 
                        // clicked the "Cancel" button.  This allows people to
                        // submit the form by pressing "Enter".
                        return Boolean.TRUE;
                    } else {
                        return Boolean.FALSE;
                    }
                }
            };

        m_title = new TextField(new TrimmedStringParameter(name + "-title"));
        m_title.setSize(30);
        m_title.addValidationListener
            (new CancellableValidationListener
             (new NotEmptyValidationListener(), isCancelled));

	m_term = new TextField(new TrimmedStringParameter(name + "-term"));
	m_term.setSize(30);
        m_term.addValidationListener
            (new CancellableValidationListener
             (new NotEmptyValidationListener(), isCancelled));

	m_url = new TextField(new TrimmedStringParameter(name + "-url"));
	m_url.setSize(30);
        m_url.addValidationListener
            (new CancellableValidationListener
             (new NotEmptyValidationListener(), isCancelled));

        add(new Label("Title:"));
        add(m_title);

	add(new Label("Term:"));
	add(m_term);
	
	add(new Label("URL:"));
	add(m_url);
    }

    private class SponsoredLinkFormInitListener implements FormInitListener {
	public void init(FormSectionEvent e)
	    throws FormProcessException {
	    
	    PageState state = e.getPageState();

	    if (m_link.isSelected(state)) {
		SponsoredLink link = (SponsoredLink) m_link.getSelectedObject(state);
                m_title.setValue(state, link.getTitle());
		m_term.setValue(state, link.getTerm());
		m_url.setValue(state, link.getURL());
	    } else {
                m_title.setValue(state, null);
		m_term.setValue(state, null);
		m_url.setValue(state, null);
	    }
	}
    }

    private class SponsoredLinkFormValidationListener implements FormValidationListener {
        public void validate(FormSectionEvent e)
            throws FormProcessException {
            PageState state = e.getPageState();
            FormData data = e.getFormData();

            // Don't do validation if we clicked Cancel
            if (m_buttons.getCancelButton().isSelected(state)) {
                return;
            }

            String hostname = (String) m_url.getValue(state);

            // Validate the URL
            try {
                URL url = new URL(hostname);
            } catch (MalformedURLException mu) {
                data.addError(m_url.getName(), "The URL is not valid: " + hostname + "\n" + mu.getMessage());
            }
	    
            String term = (String) m_term.getValue(state);
            // Make sure the term contains no whitespace
            for (int i = 0, j = term.length(); i < j; i++) {
                if (Character.isWhitespace(term.charAt(i))) {
                    data.addError(m_term.getName(), "The term may not contain any whitespace characters");
                    break;
                }
            }
        }
    }

    private class SponsoredLinkFormProcessListener implements FormProcessListener {
	public void process(FormSectionEvent e)
	    throws FormProcessException {
	    PageState state = e.getPageState();

	    if (!m_buttons.getCancelButton().isSelected(state)) {
                final SponsoredLink link;
		if (m_link.isSelected(state)) {
                    link = (SponsoredLink) m_link.getSelectedObject(state);
                } else {
                    link = new SponsoredLink();
                }

                link.setTitle((String) m_title.getValue(state));
                link.setTerm((String) m_term.getValue(state));
                link.setURL((String) m_url.getValue(state));
                link.save();
	    }

            m_title.setValue(state, null);
	    m_term.setValue(state, null);
	    m_url.setValue(state, null);

	    m_link.clearSelection(state);
	}
    }
}
