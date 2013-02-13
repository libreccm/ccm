/*
 * Copyright (C) 2003 Chris Gilbert  All Rights Reserved.
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

import java.math.BigDecimal;
// import java.util.Date;

import com.arsdigita.portlet.news.ui.NewsPortletRenderer;

import com.arsdigita.bebop.portal.AbstractPortletRenderer;
// import com.arsdigita.cms.contenttypes.NewsItem;
// import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
// import com.arsdigita.persistence.SessionManager;
import com.arsdigita.portal.Portlet;

import org.apache.log4j.Logger;


/**
 * List of news content types items, in descending order of date.  The number
 * of items retrieved is set by the administrator. 
 * 
 * @author Chris Gilbert (cgyg9330) &lt;chris.gilbert@westsussex.gov.uk&gt;
 * @version $Id: NewsPortlet.java, 2007/08/08 09:28:26 cgyg9330 $
 */
public class NewsPortlet extends Portlet implements NewsConstants {

    /** Private Logger instance for debugging purpose.                        */
    private static final Logger s_log = Logger.getLogger(NewsPortlet.class);

    /** PDL stuff                                                             */
    public static final String BASE_DATA_OBJECT_TYPE =
		                       "com.arsdigita.portlet.NewsPortlet";

	/**
	 * allows non personalised news page to be cached between updates
	 */
	private static BigDecimal s_latestNews = new BigDecimal(0);
	private static long s_newsCount = 0;

    
	private static final NewsPortletConfig s_config = NewsPortletConfig.getConfig();

		   static {
			   s_config.load();
		   }

		   public static NewsPortletConfig getConfig() {
			   return s_config;
		   }

	
	public NewsPortlet(DataObject dataObject) {
		super(dataObject);
	}

	
    @Override
	protected String getBaseDataObjectType() {
		return BASE_DATA_OBJECT_TYPE;
	}

    @Override
	protected AbstractPortletRenderer doGetPortletRenderer() {
		return new NewsPortletRenderer(this);
	}

	public int getItemCount() {
		return ((Integer) get(ITEM_COUNT)).intValue();
	}

	public void setItemCount(int count) {
		set(ITEM_COUNT, new Integer(count));
	}

	/**
	 * 
	 * @return whether any homepage news items have been added or edited since last checked.
	 * If news has been updated, the stored value of the last update is changed. Note 
	 * deleted news items do not cause true to be returned.
	 * 
	 */
	/*
	 * 
	 * starting to look doubtful whether this would actually save any time
	 * 
	 * will recheck when more data on database 
	 public boolean isNewNews() {
		NewsItem latest = NewsItem.getMostRecentNewsItem();
		
		if (!latest.getID().equals(s_latestNews)) {
			s_latestNews = latest.getID();
			return true;
		}
		DataCollection news = SessionManager.getSession().retrieve(NewsItem.BASE_DATA_OBJECT_TYPE);
		news.addEqualsFilter(NewsItem.IS_HOMEPAGE, new Boolean(true));
		long newsCount = news.size();
		if (newsCount != s_newsCount) {
			s_newsCount = newsCount;
			return true;
		}
		return false;
		
		
		
	}*/

}
