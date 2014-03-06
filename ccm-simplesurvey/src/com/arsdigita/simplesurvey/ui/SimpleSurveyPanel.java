/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
 *
 */
package com.arsdigita.simplesurvey.ui;


import com.arsdigita.simplesurvey.util.GlobalizationUtil ; 

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.ui.UI;
import com.arsdigita.ui.login.UserAuthenticationListener;

import com.arsdigita.web.Application;
import javax.servlet.http.HttpServletRequest;


/**
 * The Page class that all pages in the Simple Survey application extend.
 *
 * @author <a href="mailto:pmarklun@arsdigita.com">Peter Marklund</a>
 * @version $Id: SimpleSurveyPanel.java 2286 2012-03-11 09:14:14Z pboy $
 */
public abstract class SimpleSurveyPanel extends SimpleContainer {

    protected BoxPanel m_navBar;

    private final String m_title;

    protected SimpleSurveyPanel(String pageTitle) {
	m_title = pageTitle;

        // All pages should have a Navbar
	addNavBar();

        addComponentsToPage();
    }

    public void register(Page p) {
	super.register(p);
	
	p.addRequestListener(new UserAuthenticationListener());

    }

    public String getWidgetTitle() {
	return m_title;
    }

    protected void addNavBar() {

	m_navBar = new BoxPanel(BoxPanel.HORIZONTAL);

	addParentPagesToNavbar();
	    
	// The title of the page is the last entry in the navbar
	m_navBar.add(new Label(getWidgetTitle()));

        add(m_navBar);
    }

    protected void addParentPagesToNavbar() {

	addWorkspaceToNavBar();

	// Link to the index page
	Link indexLink = new Link( new Label(GlobalizationUtil.globalize(
                               "simplesurvey.ui.simple_survey_index_page")),  
                               new PrintListener() {
        @Override
	    public void prepare(PrintEvent event) {
		Link link = (Link)event.getTarget();
		PageState pageState = event.getPageState();

		link.setTarget(getSubsiteURL(pageState.getRequest()) + "index.jsp");
	    }
	});
        if (!this.getClass().equals(com.arsdigita.simplesurvey.ui.IndexPanel.class)) {	    
	    m_navBar.add(indexLink);
	}	
    }

    protected void addWorkspaceToNavBar() {

	// Link to the workspace of the site
	m_navBar.add(new Link("Workspace",UI.getWorkspaceURL()));
    }

    protected abstract void addComponentsToPage();

    public abstract String getRelativeURL();

    /** 
     * 
     * @param request
     * @return 
     */
    public static String getSubsiteURL(HttpServletRequest request) {

        // Get the SiteNode from the request URI
    //  SiteNode siteNode;
    //  try {		
    //      siteNode = SiteNode.getSiteNode(request.getRequestURI(), true);
    //  } catch (com.arsdigita.domain.DataObjectNotFoundException e) {
    //      throw new com.arsdigita.util.UncheckedWrapperException(e);
    //  }
    //  return siteNode.getURL();

        // Application app = Web.getWebContext().getApplication();
        Application thisApp = Application.getCurrentApplication(request);
        return thisApp.getPrimaryURL();
    }
}
