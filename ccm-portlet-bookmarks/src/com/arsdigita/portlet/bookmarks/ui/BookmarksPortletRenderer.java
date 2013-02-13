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

import com.arsdigita.portlet.bookmarks.Bookmark;
import com.arsdigita.portlet.bookmarks.BookmarkConstants;
import com.arsdigita.portlet.bookmarks.BookmarksPortlet;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.portal.AbstractPortletRenderer;
// import com.arsdigita.portalworkspace.ui.PortalConstants;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.portal.PortletType;
import com.arsdigita.xml.Element;

import org.apache.log4j.Logger;

/**
 * @author cgyg9330
 *
 * 
 */
public class BookmarksPortletRenderer extends AbstractPortletRenderer
                                        implements BookmarkConstants {

    private static Logger s_log = Logger.getLogger(BookmarksPortletRenderer.class);

    private BookmarksPortlet portlet;


    /**
     * Constructor. 
     * 
	 * @param portlet
	 */
    public BookmarksPortletRenderer(BookmarksPortlet portlet) {
        this.portlet = portlet;
    }


    /* (non-Javadoc)
     * @see com.arsdigita.bebop.portal.
     *          AbstractPortletRenderer#generateBodyXML(
     *              com.arsdigita.bebop.PageState, 
     *              com.arsdigita.xml.Element)
     */
    protected void generateBodyXML(PageState state, Element document) {
        s_log.debug("START generateBodyXML");

        Element main = document.newChildElement(MAIN_BOOKMARK_PORTLET_ELEMENT,
                                               PortletType.PORTLET_XML_NS);
                                               // PortalConstants.PORTLET_XML_NS);
        DataCollection links = portlet.getBookmarks();

        while (links.next()) {
            Bookmark link = new Bookmark (links.getDataObject());
            String url = BookmarksPortlet.getURIForBookmark(link, state);
            if (url != null) {
                Element linkElement = createLink(link, state);
                main.addContent(linkElement);
            }
        }

        s_log.debug("END generateBodyXML");
    }


	private Element createLink(Bookmark bookmark, PageState state) {
		String url = BookmarksPortlet.getURIForBookmark(bookmark, state);
		Element link = new Element(BOOKMARK_ELEMENT, XML_BOOKMARK_NS);
		link.addAttribute(TITLE_ATTRIBUTE, bookmark.getTitle());
		link.addAttribute(
				URL_ATTRIBUTE,
				url);
			link.addAttribute(WINDOW_ATTRIBUTE, bookmark.getTargetWindow());
		
		return link;
	}


	/**
	 * cache key is portlet id
	 */
    @Override
	public String getCacheKey(PageState state) {
			return portlet.getID().toString();
	}

	/**
	 * if we are checking permissions, don't use cache, otherwise refresh 
     * when portlet has been edited
	 */
    @Override
	public boolean isDirty(PageState state) {
		if (!BookmarksPortlet.getConfig().checkPermissions()){
//			if cached version exists then it isn't dirty
			// because cached version is dropped whenever the portlet is edited 
			// (see init method in editor class)
			return false;
		} else {
			return true;
			
		}
		
	}
}
