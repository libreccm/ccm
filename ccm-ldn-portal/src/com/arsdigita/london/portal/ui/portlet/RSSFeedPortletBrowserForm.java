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

package com.arsdigita.london.portal.ui.portlet;

import java.util.Iterator;
import java.util.TooManyListenersException;

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.london.portal.portlet.RSSFeedPortletHelper;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.xml.Element;

public class RSSFeedPortletBrowserForm extends Form {

	private BoxPanel m_hosts_pnl;

	private BoxPanel m_feeds_pnl;

	private SingleSelect m_hosts;

	private SingleSelect m_feeds;

	private Label m_no_feeds;

	private Submit m_display;

	private Submit m_cancel;

	private Submit m_select;

	private Submit m_back;

	private RequestLocal m_feed = new RequestLocal();

	public RSSFeedPortletBrowserForm() {
		super("Browser");

		m_hosts_pnl = new BoxPanel(BoxPanel.HORIZONTAL);
		m_feeds_pnl = new BoxPanel(BoxPanel.HORIZONTAL);

		add(m_hosts_pnl);
		add(m_feeds_pnl);

		Label hosts_lbl = new Label("Hosts:");
		Label feeds_lbl = new Label("Feeds:");
		m_no_feeds = new Label("none available");

		m_hosts_pnl.add(hosts_lbl);
		m_feeds_pnl.add(feeds_lbl);
		m_feeds_pnl.add(m_no_feeds);

		m_hosts = new SingleSelect(new StringParameter("host"));
		m_feeds = new SingleSelect(new StringParameter("feed"));

		m_hosts_pnl.add(m_hosts);
		m_feeds_pnl.add(m_feeds);

		m_back = new Submit("back", "Back");
		m_cancel = new Submit("cancel", "Back");
		m_display = new Submit("display", "Display feeds");
		m_select = new Submit("select", "Select feed");

		m_feeds_pnl.add(m_back);
		m_feeds_pnl.add(m_select);
		m_hosts_pnl.add(m_cancel);
		m_hosts_pnl.add(m_display);

		addProcessListener(new FormProcessListener() {
			public void process(FormSectionEvent e) throws FormProcessException {
				PageState state = e.getPageState();

				if (m_select.isSelected(state)) {
					fireCompletionEvent(state);
				} else if (m_display.isSelected(state)) {
					setDisplay(state, false);
				} else if (m_back.isSelected(state)) {
					setDisplay(state, true);
				} else if (m_cancel.isSelected(state)) {
					fireCompletionEvent(state);
				}
			}
		});

		try {
			m_hosts.addPrintListener(new PrintListener() {
				public void prepare(PrintEvent e) {
					SingleSelect group = (SingleSelect) e.getTarget();

					Iterator hosts = RSSFeedPortletHelper.getACSJHosts();
					while (hosts.hasNext()) {
						String host[] = (String[]) hosts.next();

						group.addOption(new Option(host[0], host[1]));
					}
				}
			});
		} catch (TooManyListenersException ex) {
			throw new UncheckedWrapperException("This can never happen", ex);
		}

		try {
			m_feeds.addPrintListener(new PrintListener() {
				public void prepare(PrintEvent e) {
					SingleSelect group = (SingleSelect) e.getTarget();

					Iterator feeds = (Iterator) m_feed.get(e.getPageState());
					while (feeds != null && feeds.hasNext()) {
						String feed[] = (String[]) feeds.next();

						group.addOption(new Option(feed[0], feed[1]));
					}
				}
			});
		} catch (TooManyListenersException ex) {
			throw new UncheckedWrapperException("This can never happen", ex);
		}
	}

	public String getFeedURL(PageState state) {
		return (String) m_feeds.getValue(state);
	}

	public void register(Page p) {
		super.register(p);

		p.setVisibleDefault(m_hosts_pnl, true);
		p.setVisibleDefault(m_feeds_pnl, false);
	}

	private void setDisplay(PageState state, boolean initial) {
		m_hosts_pnl.setVisible(state, initial);
		m_feeds_pnl.setVisible(state, !initial);
	}

	public void generateXML(PageState state, Element parent) {
		if (m_hosts.getValue(state) == null) {
			m_feed.set(state, null);
			setDisplay(state, true);
		} else {
			String host = (String) m_hosts.getValue(state);
			Iterator feed = RSSFeedPortletHelper.getACSJFeeds(host);
			m_feed.set(state, feed);

			m_no_feeds.setVisible(state, !feed.hasNext());
			m_feeds.setVisible(state, feed.hasNext());
			m_select.setVisible(state, feed.hasNext());
		}

		super.generateXML(state, parent);
	}
}
