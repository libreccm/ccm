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

import com.arsdigita.bebop.parameters.StringParameter;


import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;



public class ExternalFeedForm extends FeedForm {

    private TextField m_url;

    public ExternalFeedForm(FeedSelectionModel feed) {
	super("external", false, feed);

    }

    protected void addWidgets(String name) {
	super.addWidgets(name);

	m_url = new TextField(new StringParameter(name + "-url")); 
	m_url.setSize(50);
	add(new Label("URL:"));
	add(m_url);
    }

    protected String getURL(PageState state) {
	return (String)m_url.getValue(state);
    }

    protected void setURL(PageState state,
			  String url) {
	m_url.setValue(state, url);
    }
}
