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

import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.Form;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;

public class SponsoredLinksPanel extends BoxPanel {
    
    private Table m_links;
    private Form m_linkForm;
    private ACSObjectSelectionModel m_link;

    public SponsoredLinksPanel() {
	super(BoxPanel.VERTICAL);

	m_link = new ACSObjectSelectionModel(new BigDecimalParameter("link"));

	add(new Label("Sponsored Links", Label.BOLD));

	m_links = new SponsoredLinksTable(m_link);

	m_linkForm = new SponsoredLinkForm("link", m_link);

	add(m_links);
	add(m_linkForm);
    }
}
