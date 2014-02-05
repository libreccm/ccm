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

package com.arsdigita.portalworkspace.ui.portlet;

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.portal.PortletConfigFormSection;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.StringInRangeValidationListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.kernel.ResourceType;
import com.arsdigita.portalworkspace.portlet.RSSFeedPortlet;
import com.arsdigita.portal.Portlet;

/**
 * 
 * @author unknown
 */
public class RSSFeedPortletEditorForm extends PortletConfigFormSection {

	private TextField m_url;

	private Submit m_browse;

	/** 
     * 
     * @param resType
     * @param parentAppRL
     */
    public RSSFeedPortletEditorForm(ResourceType resType,
			RequestLocal parentAppRL) {
		super(resType, parentAppRL);
	}

	/**
     * 
     * @param application
     */
    public RSSFeedPortletEditorForm(RequestLocal application) {
		super(application);
	}

	/**
     * 
     */
    public void addWidgets() {
		super.addWidgets();
		m_url = new TextField(new StringParameter("url"));
		m_url.setSize(50);
		m_url
				.addValidationListener(new StringInRangeValidationListener(0,
						250));

		m_browse = new Submit("Browse...");

		BoxPanel panel = new BoxPanel(BoxPanel.HORIZONTAL);
		panel.add(m_url);
		panel.add(m_browse);

		add(new Label("Feed URL:", Label.BOLD), ColumnPanel.RIGHT);
		add(panel);
	}

	/**
     * 
     * @param state
     * @return
     */
    public boolean isBrowsePressed(PageState state) {
		return m_browse.isSelected(state);
	}

	/**
     * 
     * @param state
     * @param url
     */
    public void setFeedURL(PageState state, String url) {
		m_url.setValue(state, url);
	}

	/**
     * 
     * @param state
     * @param portlet
     * @throws FormProcessException
     */
    public void initWidgets(PageState state, Portlet portlet)
			throws FormProcessException {
		super.initWidgets(state, portlet);

		if (portlet != null) {
			RSSFeedPortlet myportlet = (RSSFeedPortlet) portlet;
			m_url.setValue(state, myportlet.getURL());
		}
	}

	/** 
     * 
     * @param state
     * @param portlet
     * @throws FormProcessException
     */
    public void validateWidgets(PageState state, Portlet portlet)
			throws FormProcessException {
		super.validateWidgets(state, portlet);

		RSSFeedPortlet myportlet = (RSSFeedPortlet) portlet;
	}

	/** 
     * 
     * @param state
     * @param portlet
     * @throws FormProcessException
     */
    public void processWidgets(PageState state, Portlet portlet)
			throws FormProcessException {
		super.processWidgets(state, portlet);

		RSSFeedPortlet myportlet = (RSSFeedPortlet) portlet;
		myportlet.setURL((String) m_url.getValue(state));
	}
}
