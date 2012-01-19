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


package com.arsdigita.rssfeed.ui.admin;

import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.Form;



/**
 * 
 * 
 */
public class FeedsPanel extends BoxPanel {
    
    private Table m_provider_feeds;
    private Table m_external_feeds;
    
    private Form m_provider_form;
    private Form m_external_form;
    
    private FeedSelectionModel m_provider_model;
    private FeedSelectionModel m_external_model;


    /**
     * 
     */
    public FeedsPanel() {
        super(BoxPanel.VERTICAL);

        m_provider_model = 
                new FeedSelectionModel(new BigDecimalParameter("provider"));
        m_external_model = 
                new FeedSelectionModel(new BigDecimalParameter("external"));

        m_provider_feeds = new FeedsTable(m_provider_model, true);
        m_external_feeds = new FeedsTable(m_external_model, false);

        m_provider_form = new ProviderFeedForm(m_provider_model);
        m_external_form = new ExternalFeedForm(m_external_model);

        add(new Label("Feed Providers",Label.BOLD));
        add(m_provider_feeds);
        add(m_provider_form);

        add(new Label("External Feeds",Label.BOLD));
        add(m_external_feeds);
        add(m_external_form);
    }

}
