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

import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.kernel.Resource;
import com.arsdigita.kernel.ResourceType;
import com.arsdigita.kernel.ui.ResourceConfigComponent;
import com.arsdigita.portalworkspace.util.GlobalizationUtil;

public class RSSFeedPortletEditor extends ResourceConfigComponent {

	private RSSFeedPortletEditorForm m_section;

	private SaveCancelSection m_buttons;

	private RSSFeedPortletBrowserForm m_browser;

	private Form m_form;

	public RSSFeedPortletEditor(ResourceType resType, RequestLocal parentAppRL) {

		m_section = new RSSFeedPortletEditorForm(resType, parentAppRL);

		m_buttons = new SaveCancelSection();

		m_form = new Form("wrapper");
		m_form.setRedirecting(true);
		m_form.add(m_section);
		m_form.add(m_buttons);
		add(m_form);

		m_browser = new RSSFeedPortletBrowserForm();
		add(m_browser);

		addListeners();
	}

	public RSSFeedPortletEditor(RequestLocal application) {

		m_section = new RSSFeedPortletEditorForm(application);
		m_buttons = new SaveCancelSection();

		m_form = new Form("wrapper");
		m_form.setRedirecting(true);
		m_form.add(m_section);
		m_form.add(m_buttons);
		add(m_form);

		m_browser = new RSSFeedPortletBrowserForm();
		add(m_browser);

		addListeners();
	}

	public void addListeners() {
		m_form.addSubmissionListener(new FormSubmissionListener() {
			public void submitted(FormSectionEvent e)
					throws FormProcessException {
				PageState state = e.getPageState();

				if (m_buttons.getCancelButton().isSelected(state)) {
					fireCompletionEvent(state);
					throw new FormProcessException(
                                                GlobalizationUtil.globalize(
                                                        "portal.ui.cancelled"));
				} else if (m_section.isBrowsePressed(state)) {
					m_form.setVisible(state, false);
					m_browser.setVisible(state, true);
					throw new FormProcessException(
                                                GlobalizationUtil.globalize(
                                                        "portal.ui.skip"));
				}
			}
		});

		m_form.addProcessListener(new FormProcessListener() {
			public void process(FormSectionEvent e) throws FormProcessException {
				PageState state = e.getPageState();

				fireCompletionEvent(state);
			}
		});
		m_browser.addCompletionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PageState state = e.getPageState();

				m_form.setVisible(state, true);
				m_browser.setVisible(state, false);
				if (m_browser.getFeedURL(state) != null) {
					m_section.setFeedURL(state, m_browser.getFeedURL(state));
				}
			}
		});
	}

	public void register(Page p) {
		super.register(p);

		p.setVisibleDefault(m_form, true);
		p.setVisibleDefault(m_browser, false);
	}

	public Resource createResource(PageState state) {
		Resource resource = null;
		if (m_buttons.getSaveButton().isSelected(state)) {
			resource = m_section.createResource(state);
		}
		return resource;
	}

	public void modifyResource(PageState state) {
		if (m_buttons.getSaveButton().isSelected(state)) {
			m_section.modifyResource(state);
		}
	}

}
