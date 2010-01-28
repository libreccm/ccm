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

import com.arsdigita.bebop.portal.AbstractPortletRenderer;
import com.arsdigita.london.portal.ui.portlet.RSSFeedPortletRenderer;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.portal.Portlet;

/** 
 * RSSFeedPortlet
 *
 * @version $Id: RSSFeedPortlet.java 1174 2006-06-14 14:14:15Z fabrice $
 */ 
public class RSSFeedPortlet extends Portlet {

	public static final String BASE_DATA_OBJECT_TYPE = "com.arsdigita.london.portal.portlet.RSSFeedPortlet";

	public static final String URL = "url";

	public RSSFeedPortlet(DataObject dataObject) {
		super(dataObject);
	}

	protected String getBaseDataObjectType() {
		return BASE_DATA_OBJECT_TYPE;
	}

	public String getURL() {
		return (String) get(URL);
	}

	public void setURL(String item) {
		set(URL, item);
	}

	protected AbstractPortletRenderer doGetPortletRenderer() {
		return new RSSFeedPortletRenderer(this);
	}
}
