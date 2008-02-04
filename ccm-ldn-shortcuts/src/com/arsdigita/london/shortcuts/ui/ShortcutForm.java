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

package com.arsdigita.london.shortcuts.ui;

import java.math.BigDecimal;

import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.SiteNode;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.london.shortcuts.ShortcutUtil;
import com.arsdigita.london.shortcuts.Shortcut;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.parameters.TrimmedStringParameter;
import com.arsdigita.bebop.parameters.NotEmptyValidationListener;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.ParameterListener;
import com.arsdigita.bebop.event.ParameterEvent;
import com.arsdigita.bebop.parameters.ParameterData;
import org.apache.log4j.Category;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.util.UncheckedWrapperException;
import org.apache.oro.text.perl.Perl5Util;
import org.apache.oro.text.perl.MalformedPerl5PatternException;

public class ShortcutForm extends Form {

	private static final Category log = Category.getInstance(ShortcutForm.class
			.getName());

	private ACSObjectSelectionModel m_selected_shortcut;

	private TextField m_url;

	private TextField m_redirect;

	private Submit m_submit;

	public ShortcutForm(ACSObjectSelectionModel selected_shortcut) {
		super("ShortcutForm");
		m_selected_shortcut = selected_shortcut;

		TrimmedStringParameter urlKeyParameter = new TrimmedStringParameter(
				"url");
		urlKeyParameter.addParameterListener(new NotEmptyValidationListener());
		m_url = new TextField(urlKeyParameter);

		TrimmedStringParameter redirectParameter = new TrimmedStringParameter(
				"redirect");
		redirectParameter
				.addParameterListener(new NotEmptyValidationListener());

		m_redirect = new TextField(redirectParameter);

		urlKeyParameter.addParameterListener(new ParameterListener() {
			public void validate(ParameterEvent e) throws FormProcessException {
				ParameterData data = e.getParameterData();

				String key = (String) data.getValue();
				if (key == null)
					return; // Something else will handle
				// this

				Perl5Util perl = new Perl5Util();
				try {
					if (!perl.match("/^(\\/[-a-zA-Z0-9_.]+)+\\/?$/", key)) {
						data.addError("URL key must start with the "
								+ "character '/' and only contains "
								+ "a-z, A-Z, 0-9, -, ., -");
						throw new FormProcessException("Invalid key");
					}
				} catch (MalformedPerl5PatternException ex) {
					throw new UncheckedWrapperException("bad regex", ex);
				}
			}
		});

		redirectParameter.addParameterListener(new ParameterListener() {
			public void validate(ParameterEvent e) throws FormProcessException {
				ParameterData data = e.getParameterData();

				String url = (String) data.getValue();
				if (url == null)
					return; // Something else will handle
				// this

				url = url.toLowerCase();

				// Absolute local url is ok
				if (url.startsWith("/"))
					return;

				// Fully qualified url is ok
				if (url.startsWith("http://"))
					return;

				// And secure ones too
				if (url.startsWith("https://"))
					return;

				data
						.addError("You must enter an absolute path "
								+ "(starting with '/') or a fully "
								+ "qualified URL (starting with 'http://' or 'https://')");
				throw new FormProcessException("invalid URL");
			}
		});

		add(new Label("URL Key:"));
		add(m_url);
		add(new Label("Redirect:"));
		add(m_redirect);

		m_submit = new Submit("Save Shortcut");
		add(m_submit);

		addInitListener(new ShortcutInitListener());
		addProcessListener(new ShortcutFormProcessListener());
		addValidationListener(new ShortcutFormValidationListener());
	}

	private class ShortcutInitListener implements FormInitListener {
		public void init(FormSectionEvent ev) throws FormProcessException {
			PageState state = ev.getPageState();
			BigDecimal shortcutKey = (BigDecimal) m_selected_shortcut
					.getSelectedKey(state);
			if (shortcutKey == null) {
				log.debug("init form for empty shortcut");
				m_url.setValue(state, null);
				m_redirect.setValue(state, null);
			} else {
				log.debug("init form for shortcut " + shortcutKey);
				Shortcut shortcut = new Shortcut(shortcutKey);
				m_url.setValue(state, shortcut.getUrlKey());
				m_redirect.setValue(state, shortcut.getRedirect());
			}
		}
	}

	private class ShortcutFormValidationListener implements
			FormValidationListener {

		public void validate(FormSectionEvent e) throws FormProcessException {

			PageState state = e.getPageState();

			// get currently edited shortcut
			BigDecimal shortcutKey = (BigDecimal) m_selected_shortcut
					.getSelectedKey(state);

			String key = ShortcutUtil.cleanURLKey((String) m_url
					.getValue(state));

			if (shortcutKey == null) {
				String target = ShortcutUtil.getTarget(key);
				if (target != null) {
					m_url.addError("that url key already exists");
					throw new FormProcessException("duplicate key");
				}
			}

			int index = key.indexOf("/", 2);
			String base = key.substring(0, index + 1);

			return;
			// disable checking application nodes -- this is not even the
			// correct thing to
			// check!
			// SiteNode node = null;
			// try {
			// log.info("checking for site node : " + base);
			// node = SiteNode.getSiteNode(base, true);
			// } catch ( DataObjectNotFoundException ex ) {
			// // node doesn't exist so return
			// return;
			// }
			//            
			// if ( !base.equals(node.getURL()) ) {
			// log.info("path does not match: " + node.getURL());
			// return;
			// }
			//            
			// m_url.addError("an application is already using the key starting
			// with " +
			// base);
			// throw new FormProcessException("clash with sitenode");
		}
	}

	private class ShortcutFormProcessListener implements FormProcessListener {

		public void process(FormSectionEvent e) throws FormProcessException {

			PageState state = e.getPageState();

			BigDecimal shortcutKey = (BigDecimal) m_selected_shortcut.getSelectedKey(state);

			String url = ShortcutUtil.cleanURLKey((String) m_url.getValue(state));
			String redirect = (String) m_redirect.getValue(state);
			
			if (shortcutKey == null) {
				log.info("save new shortcut " + url);
				Shortcut shortcut = Shortcut.create(url, redirect);
				shortcut.save();
			} else {
				log.info("save updated shortcut " + shortcutKey + " " +  url);
				Shortcut shortcut = new Shortcut(shortcutKey);
				shortcut.setUrlKey(url);
				shortcut.setRedirect(redirect);
				shortcut.save();
			}

			ShortcutUtil.repopulateShortcuts();

			m_redirect.setValue(state, "");
			m_url.setValue(state, "");
			m_selected_shortcut.clearSelection(state);
		}
	}

}
