/*
 * Copyright (C) 2003 - 2004 Chris Gilbert  All Rights Reserved.
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

package com.arsdigita.portlet.news;


/**
 * Constants used by classes in the news portlet application.
 * 
 * @author cgyg9330
 */
 public interface NewsConstants {
	
	
	
	public static final String ITEM_COUNT = "itemCount";



	public static final String GENERAL_NEWS_CACHE_KEY = "generalNews";

		//queries
		public static final String RECENT_NEWS =
			"uk.gov.westsussex.portal.portlet.RecentNews";
		public static final String PERSONALISED_NEWS =
			"uk.gov.westsussex.portal.portlet.PersonalisedNews";
		public static final String LAST_UPDATE =
			"uk.gov.westsussex.portal.portlet.LatestNewsDate";

	
		
		
	
	// rendering
	    
	public static final String XML_NEWS_NS = "http://wsgfl.westsussex.gov.uk/portlet/news/1.0";
	public static final String MAIN_PORTLET_ELEMENT = "portlet:news";
	public static final String NEWS_ITEM_ELEMENT = "news-portlet:newsItem"; 
	public static final String NEWS_ROOM_ATTRIBUTE = "newsroom-shortcut";
	public static final String PERSONALISED_ATTRIBUTE = "personalised";
	public static final String DATE_ATTRIBUTE = "date";
	public static final String TITLE_ATTRIBUTE = "title";
	public static final String LEAD_ATTRIBUTE = "lead";
	public static final String URL_ATTRIBUTE = "url";
		

}
