/*
 * Copyright (C) 2002-2005 Runtime Collective Ltd. All Rights Reserved.
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

package com.arsdigita.cms.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.dispatcher.CMSPage;
import com.arsdigita.ui.UIConstants;
import com.arsdigita.xml.Element;

import java.text.SimpleDateFormat;

/**
 * This class can be used to spit out data about a content item's dates.
 **/
public class ContentItemDatesData extends SimpleComponent {

	private static final String TAG_LAUNCH_DATE = "launchDate";

	public ContentItemDatesData() {
		super();
	}

	/**
	 * Generate the XML for the selected content item's last modified date. 
         * This will appear as a "launchDate" tag.
	 * 
	 * @param state The page state
	 * @param parent Parent DOM element
	 **/
        @Override
	 public void generateXML(PageState state, Element parent) {
	 	if (isVisible(state)) {

			ContentItem item;
			Element launchDate;

			// Get the current item
			if (CMS.getContext().hasContentItem()) {
				item = CMS.getContext().getContentItem();
			} else {
				CMSPage page = (CMSPage) state.getPage();
				item = page.getContentItem(state);
			}

			launchDate = parent.newChildElement(TAG_LAUNCH_DATE, UIConstants.UI_XML_NS);

			SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM d, yyyy");
			launchDate.setText(sdf.format(item.getLastModifiedDate()));
		}
	}

}

