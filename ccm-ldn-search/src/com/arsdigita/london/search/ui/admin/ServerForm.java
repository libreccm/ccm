/*
 * Copyright (C) 2001 ArsDigita Corporation. All Rights Reserved.
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
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.london.search.Server;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.bebop.form.FormErrorDisplay;

import java.net.MalformedURLException;
import java.net.URL;

public class ServerForm extends Form {
    
    private TextField m_title;
    private TextField m_hostname;

    private SaveCancelSection m_buttons;

    private ServerSelectionModel m_server;

    public ServerForm(String name,
		      ServerSelectionModel server) {
	super(name);
	
	m_server = server;

	m_buttons = new SaveCancelSection();
	
	addWidgets(name);
	add(new FormErrorDisplay(this));
	add(m_buttons, ColumnPanel.FULL_WIDTH);
	
	addProcessListener(new ServerFormProcessListener());
	addValidationListener(new ServerFormValidationListener());
	addInitListener(new ServerFormInitListener());
    }
    
    protected void addWidgets(String name) {
	m_title = new TextField(new StringParameter(name + "-title"));
	m_title.setSize(30);
	m_hostname = new TextField(new StringParameter(name + "-hostname"));
	m_hostname.setSize(30);

	add(new Label("Title:"));
	add(m_title);
	
	add(new Label("URL:"));
	add(m_hostname);
    }


    private class ServerFormInitListener implements FormInitListener {
	public void init(FormSectionEvent e)
	    throws FormProcessException {
	    
	    PageState state = e.getPageState();

	    if (m_server.isSelected(state)) {
		Server server = m_server.getSelectedServer(state);
		m_title.setValue(state, server.getTitle());
		m_hostname.setValue(state, server.getHostname());
	    } else {
		m_title.setValue(state, null);
		m_hostname.setValue(state, null);
	    }
	}
    }

    private class ServerFormValidationListener implements FormValidationListener {
        public void validate(FormSectionEvent e)
            throws FormProcessException {
	    
            PageState state = e.getPageState();

            String hostname = (String)m_hostname.getValue(state);

            // Validate the URL
            try {
                URL url = new URL(hostname);
            } catch ( MalformedURLException m ) {
                throw new FormProcessException("The URL is not valid: " + hostname + "\n" + m.getMessage());
            }
	    
            try {
                Server server = Server.retrieve(hostname);
		
                if (m_server.isSelected(state) &&
                    m_server.getSelectedKey(state).equals(server.getID())) {
                    return; // Matching itself, that's ok
                } else {
                    throw new FormProcessException("A server already exists for that URL");
                }
                
            } catch (DataObjectNotFoundException ex) {
                // No matching server, hurrah
            }
        }
    }

    private class ServerFormProcessListener implements FormProcessListener {
	public void process(FormSectionEvent e)
	    throws FormProcessException {
	    PageState state = e.getPageState();

	    if (m_buttons.getSaveButton().isSelected(state)) {
		if (m_server.isSelected(state)) {
		    Server server = m_server.getSelectedServer(state);
		    server.setTitle((String)m_title.getValue(state));
		    server.setHostname((String)m_hostname.getValue(state));
		    server.save();
		} else {
		    Server server = Server.create((String)m_hostname.getValue(state),
						  (String)m_title.getValue(state));
		    server.save();
		}
	    }

	    m_title.setValue(state, null);
	    m_hostname.setValue(state, null);

	    m_server.clearSelection(state);
	}
    }
}
