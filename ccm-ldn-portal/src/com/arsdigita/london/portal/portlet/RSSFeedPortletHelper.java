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

package com.arsdigita.london.portal.portlet;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.arsdigita.util.url.URLCache;
import com.arsdigita.util.url.URLData;
import com.arsdigita.util.url.URLFetcher;
import com.arsdigita.util.url.URLPool;
import com.arsdigita.xml.Document;
import com.arsdigita.xml.Element;

public class RSSFeedPortletHelper {

	private static final Logger s_log = Logger
			.getLogger(RSSFeedPortletHelper.class);

	private static final String CACHE_SERVICE_KEY = "RSSFeedPortletHelper";

	private static final URLCache cache = new URLCache(1000000, 15 * 60 * 1000);

	private static final URLPool pool = new URLPool();

	static {
		URLFetcher.registerService(CACHE_SERVICE_KEY, pool, cache);
	};

	public static Element getRSSElement(String location) {
		s_log.debug("getRSSElement from " + location);
		try {
			URLData data = URLFetcher.fetchURLData(location, CACHE_SERVICE_KEY);
			Document doc = new Document(data.getContent());
			return doc.getRootElement();
		} catch (Exception ex) {
			// XXX
			s_log.warn("Problem with fetching " + location);
			return null;
		}
	}

	public static Iterator getACSJHosts() {
		return parseRSSFeed(com.arsdigita.web.URL.there(
				"/channels/rss/acsj.rss", null).getURL());
	}

	public static Iterator getExternalFeeds() {
		return parseRSSFeed(com.arsdigita.web.URL.there(
				"/channels/rss/external.rss", null).getURL());
	}

	public static Iterator getACSJFeeds(String host) {
		return parseRSSFeed(host);
	}

	public static Iterator parseRSSFeed(String location) {
		List list = new ArrayList();

		try {
			URL url = new URL(location);
			URLConnection con = url.openConnection();

			InputStream is = con.getInputStream();
			try {
				SAXParserFactory spf = SAXParserFactory.newInstance();
				SAXParser parser = spf.newSAXParser();
				parser.parse(is, new ItemExtractor(list));
			} catch (ParserConfigurationException e) {
				s_log.error("error parsing rss feed", e);
			} catch (SAXException e) {
				s_log.error("error parsing rss feed", e);
			} catch (IOException e) {
				s_log.error("error parsing rss feed", e);
			}
		} catch (MalformedURLException mal) {
			mal.printStackTrace();
			// nada
		} catch (IOException io) {
			io.printStackTrace();
			// nada
		}

		return list.iterator();
	}

	protected static class ItemExtractor extends DefaultHandler {

		private List m_items;

		private boolean m_item;

		private String m_url;

		private String m_title;

		private StringBuffer m_scratch;

		public ItemExtractor(List items) {
			m_items = items;
			m_item = false;
			m_scratch = new StringBuffer("");
		}

		public void characters(char[] ch, int start, int len) {
			for (int i = 0; i < len; i++) {
				m_scratch.append(ch[start + i]);
			}
		}

		public void startElement(String uri, String localName, String qn,
				Attributes attrs) {
			if (m_item == false) {
				if (qn.equals("item")) {
					m_item = true;
					m_title = null;
					m_url = null;
				}
			} else {
				if (qn.equals("title") || qn.equals("link")) {
					m_scratch = new StringBuffer();
				}
			}
		}

		public void endElement(String uri, String localName, String qn) {
			if (m_item == true) {
				if (qn.equals("title")) {
					m_title = m_scratch.toString();
				} else if (qn.equals("link")) {
					m_url = m_scratch.toString();
				} else if (qn.equals("item")) {
					m_items.add(new String[] { m_url, m_title });
					m_item = false;
				}
			}
		}
	}
}
