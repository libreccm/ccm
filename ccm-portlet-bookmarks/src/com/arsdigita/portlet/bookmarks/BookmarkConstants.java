/*
 * Created on 10-Sep-04
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.arsdigita.portlet.bookmarks;

/**
 * @author cgyg9330
 *
 * Constants used by classes in the bookmarksportlet application.
 * 
 */
 
public interface BookmarkConstants {	
	
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
	
}
