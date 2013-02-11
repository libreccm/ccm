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

package com.arsdigita.portalworkspace.ui.sitemap;

import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.ChangeEvent;
import com.arsdigita.bebop.event.ChangeListener;
import com.arsdigita.portalworkspace.WorkspacePage;
import com.arsdigita.web.Application;

import org.apache.log4j.Logger;

/**
 * Entry page for Application administration as part of ccm portal. Invocation
 * by /web/templates/ccm-ldn-portal/admin/sitemap.jsp
 *
 * A list of all installed application is created and presented along with
 * buttons for creating new instances and others.
 * 
 */
public class SiteMapPane extends SimpleContainer {

    /** Private Logger instance  */
    private static final Logger s_log = Logger.getLogger(SiteMapPane.class);

    private ApplicationSelectionModel m_app;

    private ApplicationList m_appList;

    private ApplicationPane m_appPane;

    /**
     * Constructor  creates a new application admin pane instance
     */
    public SiteMapPane() {
        setTag("portal:sitemap");
        setNamespace(WorkspacePage.PORTAL_XML_NS);

        m_app = new ApplicationSelectionModel("application", false);
        m_app.addChangeListener(new ApplicationEditListener());

        m_appList = new ApplicationList(m_app);
        // adds a list of installed applications
        add(m_appList);

        m_appPane = new ApplicationPane(m_app);
        add(m_appPane);
    }

    /**
     * 
     * @param p
     */
    @Override
    public void register(Page p) {
		super.register(p);

		p.addGlobalStateParam(m_app.getStateParameter());
		p.setVisibleDefault(m_appPane, false);
	}

	/**
     * 
     */
    private class ApplicationEditListener implements ChangeListener {
		public void stateChanged(ChangeEvent e) {
			PageState state = e.getPageState();

			Application app = (Application) m_app.getSelectedObject(state);
			m_appPane.setVisible(state, app != null);
		}
	}
}
