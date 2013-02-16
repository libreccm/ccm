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
package com.arsdigita.portlet.bookmarks;


import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.portal.AbstractPortletRenderer;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.cms.contenttypes.Link;
import com.arsdigita.cms.dispatcher.ItemResolver;
import com.arsdigita.cms.dispatcher.MultilingualItemResolver;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.persistence.DataAssociationCursor;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.FilterFactory;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.portal.Portlet;
import com.arsdigita.portlet.bookmarks.ui.BookmarksPortletRenderer;
import com.arsdigita.web.URL;

import org.apache.log4j.Logger;


/**
 * Displays a collection of links, that may be internal or external.
 * 
 * @author cgyg9330
 */
public class BookmarksPortlet extends Portlet implements BookmarkConstants {

    /** Private Logger instance for debugging purpose.                      */
    public static final Logger s_log = Logger.getLogger(BookmarksPortlet.class);

    public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.portlet.BookmarksPortlet";

    private static BookmarksPortletConfig s_config =
		new BookmarksPortletConfig();

	static {
		s_config.load();
	}

	public static BookmarksPortletConfig getConfig() {
		return s_config;
	}

	public BookmarksPortlet(DataObject dataObject) {
		super(dataObject);
	}

    @Override
	protected String getBaseDataObjectType() {
		return BASE_DATA_OBJECT_TYPE;
	}

    @Override
	protected AbstractPortletRenderer doGetPortletRenderer() {
		return new BookmarksPortletRenderer(this);

	}
	/**
	 * associate a Bookmark with this portlet and place it after existing 
     * Bookmarks
     * 
	 * @param bookmark
	 */
	public void addBookmark(Bookmark bookmark) {
		DataAssociationCursor bookmarks =
			((DataAssociation) get(BOOKMARKS)).cursor();
		bookmarks.addOrder(Link.ORDER + " desc");
		int key = 1;
		if (bookmarks.next()) {
			key = ((Integer) bookmarks.get(Link.ORDER)).intValue() + 1;

		}
		bookmarks.close();
		bookmark.setOrder(key);
		add(BOOKMARKS, bookmark);

	}

    /**
     * Return a url for an internal or external link - used for displaying 
     * the link in edit view (we don't want to just display an item id 
     * when the link is internal as that doesn't mean anything to non authors)
     * 
     * @param link
     * @param state
     * @return
     */
    public static String getURIForBookmark(Bookmark bookmark, PageState state) {

        String url = null;

        if (bookmark.getTargetType().equals(Link.EXTERNAL_LINK)) {
            url = bookmark.getTargetURI();
        } else {

			ItemResolver resolver = new MultilingualItemResolver();
			ContentItem item = bookmark.getTargetItem();
			if (item != null) {
				item = item.getLiveVersion();
				if (item != null) {

					url =
						resolver.generateItemURL(
							state,
							item,
							item.getContentSection(),
							ContentItem.LIVE);
					url = URL.there(state.getRequest(), url).toString();
				}

			}

		}
		return url;
	}

	/**
	 * Retrieve the bookmarks for this portlet in their correct order
	 * @return
	 */
	public DataCollection getBookmarks() {

		DataAssociationCursor cursor =
			((DataAssociation) get(BOOKMARKS)).cursor();
		cursor.addOrder(Link.ORDER);
		if (getConfig().checkPermissions()) {

			Party party = Kernel.getContext().getParty();
			if (party == null) {
				party = Kernel.getPublicUser();
			}

			FilterFactory factory = cursor.getFilterFactory();
			/*	cursor.addFilter(PermissionService.getFilterQuery(factory, "targetItem",
												PrivilegeDescriptor.get(SecurityManager.CMS_READ_ITEM),
												party.getOID()));*/

			// need to or it here so that we don't exclude external links
			Filter filter =
				factory.or().addFilter(
					factory.equals("targetItem", null)).addFilter(
					PermissionService.getFilterQuery(
						factory,
						"targetItem",
						PrivilegeDescriptor.get(SecurityManager.CMS_READ_ITEM),
						party.getOID()));
			// this extra and 1=1 is a fiddle to ensure the query is bracketed properly. 
			// I needed it to be 
			// 'get links for this portlet and ((link is not a content item) or (user has access to item))'
			// but just adding the or filter produced 
			// 'get links for this portlet and (link is not a content item) or (user has access to item)'
			// with the result that all content item links were returned regardless of which portlet they belonged to
			cursor.addFilter(factory.and().addFilter(filter).addFilter("1=1"));

		}

		return cursor;
	}

	/**
	 * 
	 * Return the owner of the given bookmark 
	 * 
	 * @param bookmark
	 * @return
	 */
	public static BookmarksPortlet getPortletForBookmark(Bookmark bookmark) {
		// what the hell's this????? bookmark <-> portlet is a two way association
		// so we should be using the association programatically to get the portlet
		// Never mind, I'm sure it boils down to the same sql anyway
		DataCollection portlets =
			SessionManager.getSession().retrieve(
				BookmarksPortlet.BASE_DATA_OBJECT_TYPE);
		portlets.addEqualsFilter(BOOKMARKS, bookmark.getID());
		BookmarksPortlet portlet = null;
		while (portlets.next()) {
			portlet = new BookmarksPortlet(portlets.getDataObject());
		}
		return portlet;

	}

	public void renumberBookmarks() {
		DataCollection bookmarks = getBookmarks();
		bookmarks.addOrder(Link.ORDER);
		int sortKey = 0;
		while (bookmarks.next()) {
			sortKey++;
			Bookmark link = new Bookmark(bookmarks.getDataObject());
			link.setOrder(sortKey);

		}

	}
}


