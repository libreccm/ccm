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

import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.Form;



public class FeedsPanel extends BoxPanel {
    
    private Table m_provider_feeds;
    private Table m_external_feeds;
    
    private Form m_provider_form;
    private Form m_external_form;
    
    private FeedSelectionModel m_provider_model;
    private FeedSelectionModel m_external_model;


    public FeedsPanel() {
	super(BoxPanel.VERTICAL);

	m_provider_model = new FeedSelectionModel(new BigDecimalParameter("provider"));
	m_external_model = new FeedSelectionModel(new BigDecimalParameter("external"));

	m_provider_feeds = new FeedsTable(m_provider_model, true);
	m_external_feeds = new FeedsTable(m_external_model, false);

	m_provider_form = new ProviderFeedForm(m_provider_model);
	m_external_form = new ExternalFeedForm(m_external_model);

	add(new Label("Feed Providers",
		      Label.BOLD));
	add(m_provider_feeds);
	add(m_provider_form);
	
	add(new Label("External Feeds",
		      Label.BOLD));
	add(m_external_feeds);
	add(m_external_form);
    }
}
