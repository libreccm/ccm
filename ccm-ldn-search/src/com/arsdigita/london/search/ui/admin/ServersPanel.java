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

import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.Form;



public class ServersPanel extends BoxPanel {
    
    private Table m_servers;
    private Form m_new_server;
    private ServerSelectionModel m_server;

    public ServersPanel() {
	super(BoxPanel.VERTICAL);

	m_server = new ServerSelectionModel(new BigDecimalParameter("server"));

	m_servers = new ServersTable(m_server);

	m_new_server = new ServerForm("server", m_server);

	add(new Label("Search Hosts",
		      Label.BOLD));
	add(m_servers);
	add(m_new_server);
    }
}
