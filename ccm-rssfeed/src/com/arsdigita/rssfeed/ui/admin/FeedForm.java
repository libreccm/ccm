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


package com.arsdigita.rssfeed.ui.admin;

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.rssfeed.Feed;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.bebop.form.FormErrorDisplay;



public abstract class FeedForm extends Form {
    
    private TextField m_title;
    private TextArea m_desc;

    private boolean m_acsj;

    private SaveCancelSection m_buttons;

    private FeedSelectionModel m_feed;

    public FeedForm(String name,
		    boolean acsj,
		    FeedSelectionModel feed) {
	super(name);
	
	m_acsj = acsj;
	m_feed = feed;

	m_buttons = new SaveCancelSection();
	
	addWidgets(name);
	add(new FormErrorDisplay(this));
	add(m_buttons, ColumnPanel.FULL_WIDTH);
	
	addProcessListener(new FeedFormProcessListener());
	addValidationListener(new FeedFormValidationListener());
	addInitListener(new FeedFormInitListener());
    }
    
    protected void addWidgets(String name) {
	m_title = new TextField(new StringParameter(name + "-title"));
	m_title.setSize(30);
	m_desc = new TextArea(new StringParameter(name + "-desc"));
	m_desc.setRows(2);
	m_desc.setCols(50);
	

	add(new Label("Title:"));
	add(m_title);
	
	add(new Label("Description"));
	add(m_desc);
    }

    public Feed getSelectedFeed(PageState state) {
	if (m_feed.isSelected(state))
	    return m_feed.getSelectedFeed(state);
	return null;
    }
    protected abstract String getURL(PageState state);
    protected abstract void setURL(PageState state,
				   String url);

    private class FeedFormInitListener implements FormInitListener {
	public void init(FormSectionEvent e)
	    throws FormProcessException {
	    
	    PageState state = e.getPageState();

	    if (m_feed.isSelected(state)) {
		Feed feed = m_feed.getSelectedFeed(state);
		m_title.setValue(state, feed.getTitle());
		m_desc.setValue(state, feed.getDescription());
		setURL(state, feed.getURL());
	    } else {
		m_title.setValue(state, null);
		m_desc.setValue(state, null);
		setURL(state, null);
	    }
	}
    }

    private class FeedFormValidationListener implements FormValidationListener {
	public void validate(FormSectionEvent e)
	    throws FormProcessException {
	    
	    PageState state = e.getPageState();
	    
	    try {
		String url = getURL(state);
		Feed feed = Feed.retrieve(url);
		
		if (m_feed.isSelected(state) &&
		    m_feed.getSelectedKey(state).equals(feed.getID())) {
		    return; // Matching itself, that's ok
		}
		
		throw new FormProcessException("A feed already exists for that URL");
	    } catch (DataObjectNotFoundException ex) {
		// No matching feed, hurrah
	    }
	}
    }

    private class FeedFormProcessListener implements FormProcessListener {
	public void process(FormSectionEvent e)
	    throws FormProcessException {
	    PageState state = e.getPageState();

	    if (m_buttons.getSaveButton().isSelected(state)) {
		if (m_feed.isSelected(state)) {
		    Feed feed = m_feed.getSelectedFeed(state);
		    feed.setTitle((String)m_title.getValue(state));
		    feed.setDescription((String)m_desc.getValue(state));
		    feed.setURL(getURL(state));
		    feed.save();
		} else {
		    Feed feed = Feed.create(getURL(state),
					    (String)m_title.getValue(state),
					    (String)m_desc.getValue(state),
					    m_acsj);
		    feed.save();
		}
	    }

	    m_title.setValue(state, null);
	    m_desc.setValue(state, null);
	    setURL(state, null);

	    m_feed.clearSelection(state);
	}
    }
}
