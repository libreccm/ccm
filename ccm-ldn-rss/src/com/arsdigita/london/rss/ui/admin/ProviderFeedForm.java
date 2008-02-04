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


package com.arsdigita.london.rss.ui.admin;


import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.form.RadioGroup;
import com.arsdigita.bebop.form.Option;


public class ProviderFeedForm extends FeedForm {

    private TextField m_url;

    private RadioGroup m_external;
    private Option m_external_no;
    private Option m_external_yes;

    public ProviderFeedForm(FeedSelectionModel feed) {
	super("provider", true, feed);

    }

    protected void addWidgets(String name) {
	super.addWidgets(name);

	m_url = new TextField(new StringParameter(name + "-host")); 
	m_url.setSize(50);
	add(new Label("Provider Host:"));
	add(m_url);

	m_external = new RadioGroup(name + "-external");
	m_external.setLayout(RadioGroup.VERTICAL);
	m_external_yes = new Option("yes", "External feed list");
	m_external_no = new Option("no", "CMS content feed list");
	m_external.addOption(m_external_no);
	m_external.addOption(m_external_yes);

	add(new Label("Feed:"));
	add(m_external);
    }

    protected String getURL(PageState state) {
	String postfix = (m_external_no.getValue().equals(m_external.getValue(state)) ?
			  "/channels/rss/index.rss" :
			  "/channels/rss/external.rss");
	return "http://" + (String)m_url.getValue(state) + postfix;
    }

    protected void setURL(PageState state,
			  String url) {
	if (url != null && url.length() > 8) {
	    url = url.substring(7);
	    int pos = url.indexOf("/");
	    if (url.endsWith("/channels/rss/external.rss"))
		m_external.setValue(state, m_external_yes.getValue());
	    else 
		m_external.setValue(state, m_external_no.getValue());

	    if (pos > 0)
		url = url.substring(0, pos);
	} else {
	    m_external.setValue(state, m_external_no.getValue());
	}
	m_url.setValue(state, url);
    }
}

