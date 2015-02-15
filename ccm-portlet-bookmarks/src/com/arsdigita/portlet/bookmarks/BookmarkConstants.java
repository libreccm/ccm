/*
 * Created on 10-Sep-04
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.arsdigita.portlet.bookmarks;

import com.arsdigita.bebop.Label;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.portlet.bookmarks.util.GlobalizationUtil;

/**
 * @author cgyg9330
 *
 * Constants used by classes in the bookmarksportlet application.
 * 
 */
 
public interface BookmarkConstants {
	
	public static final String BUNDLE_NAME = "com.arsdigita.portlet.bookmarks.ui.BookmarkResources";
   		
	
	// bookmark portlet attributes
	public static final String BOOKMARKS = "bookmarks";
	public static final String PORTLET = "portlet";
	public static final String SORT_KEY = "sortKey";
	
	// bookmark attribute values (nb target types defined in com.arsdigita.cms.contenttypes.Link used
	
	public static final String NEW_WINDOW_YES = "_blank";
	public static final String NEW_WINDOW_NO = null;
	
	
	// rendering
	    
	public static final String XML_BOOKMARK_NS = "http://wsgfl.westsussex.gov.uk/portlet/bookmarks/1.0";
	public static final String MAIN_BOOKMARK_PORTLET_ELEMENT = "portlet:bookmarks";
	public static final String BOOKMARK_ELEMENT = "bookmark-portlet:bookmark"; 
	public static final String TITLE_ATTRIBUTE = "title";
	public static final String URL_ATTRIBUTE = "url";
	public static final String WINDOW_ATTRIBUTE = "target-window";
	
	
	// editing
	
	public static final GlobalizedMessage NO_BOOKMARKS_YET = GlobalizationUtil.globalize("bookmarks.header.no-bookmarks");
	public static final GlobalizedMessage EXISTING_BOOKMARKS = GlobalizationUtil.globalize("bookmarks.header.existing-bookmarks");
	public static final GlobalizedMessage NEW_WINDOW = GlobalizationUtil.globalize("bookmarks.new-window");
	public static final Label ADD_NEW_BOOKMARK_LABEL = new Label(GlobalizationUtil.globalize("bookmarks.add"), Label.BOLD);
	public static final Label TITLE_LABEL = new Label(GlobalizationUtil.globalize("bookmarks.title"), Label.BOLD);
	public static final Label DESCRIPTION_LABEL = new Label(GlobalizationUtil.globalize("bookmarks.description"), Label.BOLD);
	public static final Label URL_LABEL = new Label(GlobalizationUtil.globalize("bookmarks.url"), Label.BOLD);
	
	// errors
	
	public static final String NO_URL = (String)new GlobalizedMessage("bookmarks.error.no-url", BUNDLE_NAME).localize();
	public static final String NO_TITLE = (String)new GlobalizedMessage("bookmarks.error.no-title", BUNDLE_NAME).localize();
	public static final String CONTENT_ITEM_NOT_FOUND = (String)new GlobalizedMessage("bookmarks.error.content-item-not-found", BUNDLE_NAME).localize();
	public static final String CONTENT_ITEM_NOT_AVAILABLE = (String)new GlobalizedMessage("bookmarks.warning.item-not-available", BUNDLE_NAME).localize();
		

}
