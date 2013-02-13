/*
 * Copyright (C) 2005 Chris Gilbert  All Rights Reserved.
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
package com.arsdigita.portlet.bookmarks.ui;

import com.arsdigita.portlet.bookmarks.BookmarkConstants;

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.HorizontalLine;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.portal.PortletConfigFormSection;
import com.arsdigita.kernel.ResourceType;

/**
 * Adder creates a new bookmarks portlet without any bookmarks.
 * 
 * Note this is the only portlet I have seen where a different form is 
 * registered for adding and editing.
 * 
 * It is necessary here because we need to create a persistent portlet instance 
 * before we can associate links with it.
 *
 * @author cgyg9330
 */
public class BookmarksPortletAdder
	extends PortletConfigFormSection
	implements BookmarkConstants {

	
	public BookmarksPortletAdder(
		ResourceType resType,
		RequestLocal parentAppRL) {
		super(resType, parentAppRL);
	}

	
	protected void addWidgets() {
		
		/*add(
			new Label(
				"Create links to your favourite websites or pages on this site. Specify the full address (starting http://) - you can cut and paste addresses from this site.",
				Label.BOLD),
			ColumnPanel.FULL_WIDTH);*/
		super.addWidgets();
		add(new HorizontalLine(), ColumnPanel.FULL_WIDTH);
		
		add(
			new Label(
				"Give the portlet a title and save it now, then edit it to add bookmarks.",
				Label.BOLD),
			ColumnPanel.FULL_WIDTH);
		add(new HorizontalLine(), ColumnPanel.FULL_WIDTH);

		
	}

}
