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
package com.arsdigita.portlet.news.ui;

//import uk.gov.westsussex.authentication.ExternalUserFactory;
import com.arsdigita.portlet.news.NewsConstants;
import com.arsdigita.portlet.news.NewsPortlet;
import com.arsdigita.portlet.news.PersonalisedNewsTarget;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.portal.AbstractPortletRenderer;
import com.arsdigita.categorization.Category;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.User;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.navigation.Navigation;
// import com.arsdigita.portalworkspace.ui.PortalConstants;
import com.arsdigita.london.terms.Domain;
import com.arsdigita.london.terms.Term;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.portal.PortletType;
import com.arsdigita.xml.Element;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;


/**
 *
 *
 * @author Chris Gilbert (cgyg9330) &lt;chris.gilbert@westsussex.gov.uk&gt;
 * @version $Id: NewsPortletRenderer.java 2005/03/07 13:48:49 cgyg9330 Exp $
 */
public class NewsPortletRenderer  extends AbstractPortletRenderer
	                              implements NewsConstants {

	private static final Logger s_log =
		Logger.getLogger(NewsPortletRenderer.class);

	private NewsPortlet m_portlet;


	/**
     * 
     * @param portlet 
     */
    public NewsPortletRenderer(NewsPortlet portlet) {
        m_portlet = portlet;
    }

	/* (non-Javadoc)
	 * @see 
     * com.arsdigita.bebop.portal.AbstractPortletRenderer#generateBodyXML(
     *         com.arsdigita.bebop.PageState, 
     *         com.arsdigita.xml.Element)
	 */
    protected void generateBodyXML(PageState state, Element parent) {
        s_log.debug("START - generateBodyXML");

        Element newsPortlet = parent.newChildElement( MAIN_PORTLET_ELEMENT,
				                                      PortletType.PORTLET_XML_NS);
        DateFormat formatter = new SimpleDateFormat("dd/MM/yy");
        User thisUser = (User) Kernel.getContext().getParty();
        if (null == thisUser) {
            thisUser = Kernel.getPublicUser();
        }

//		Object customUser = ExternalUserFactory.getCustomUserObject(thisUser);
//		if (customUser instanceof PersonalisedNewsTarget) {
//			newsPortlet.addAttribute(PERSONALISED_ATTRIBUTE, "true");
//			getPersonalisedNews(
//				state,
//				newsPortlet,
//				(PersonalisedNewsTarget) customUser,
//				formatter);
//
//		} else {
			newsPortlet.addAttribute(PERSONALISED_ATTRIBUTE, "false");
			getGeneralNews(newsPortlet, formatter);

//		}

		newsPortlet.addAttribute(NEWS_ROOM_ATTRIBUTE,
			                     NewsPortlet.getConfig().getNewsroomShortcut());

		s_log.debug("FINISH - generateBodyXML");
	}

	/**
	 * @param newsPortlet
	 * @param profiledUser
	 */
/*  CURRENTLY NOT AVAILABLE
    private void getPersonalisedNews( PageState state,
                                      Element main,
                                      PersonalisedNewsTarget profiledUser,
                                      DateFormat formatter) {
        s_log.debug("START - getPersonalisedNews");

        DomainCollection myNews = profiledUser.getMyNews();
		if (myNews == null) {
			// method in personalised user class has been implemented to just return null
			getGeneralNews(main, formatter);
			return;
		}

		myNews.addOrder(ContentPage.LAUNCH_DATE + " desc");

		PermissionService.filterObjects(
			myNews,
			PrivilegeDescriptor.get(SecurityManager.CMS_READ_ITEM),
			Kernel.getContext().getParty().getOID());

		myNews.setRange(
			new Integer(1),
			new Integer(m_portlet.getItemCount() + 1));

		while (myNews.next()) {
			ContentPage page = (ContentPage) myNews.getDomainObject();

			Element item = main.newChildElement(NEWS_ITEM_ELEMENT, XML_NEWS_NS);
			Date d = page.getLaunchDate();
			String date = (d != null) ? formatter.format(d) : "";
			item.addAttribute(DATE_ATTRIBUTE, date);

			item.addAttribute(TITLE_ATTRIBUTE, page.getTitle());
			item.addAttribute(LEAD_ATTRIBUTE, page.getSearchSummary());
			item.addAttribute(
				URL_ATTRIBUTE,
				Navigation.redirectURL(page.getOID()));

		}
		s_log.debug("END - getPersonalisedNews");

	}
*/
    
    
    private void getGeneralNews(Element main, DateFormat formatter) {
        s_log.debug("START - getGeneralNews");
        
		// this is the default key - maybe should parametrise
		Domain rss = Domain.retrieve("APLAWS-RSS");
		DomainCollection rssRoots = rss.getRootTerms();
		rssRoots.addEqualsFilter(Term.NAME, "News");
		Term newsTerm = null;
		while (rssRoots.next()) {
			newsTerm = (Term) rssRoots.getDomainObject();
			s_log.debug("found the news rss feed term");
			Category cat = newsTerm.getModel();
			DataCollection newsItems = SessionManager.getSession()
                                       .retrieve(ContentPage.BASE_DATA_OBJECT_TYPE);
			newsItems.addEqualsFilter("parent.categories.id", 
								cat.getID());
			
            newsItems.addEqualsFilter(ContentItem.VERSION, ContentItem.LIVE);
			newsItems.addOrder(ContentPage.LAUNCH_DATE + " desc");
			
		//	CategorizedCollection newsItems =
		//		cat.getObjects(ContentPage.BASE_DATA_OBJECT_TYPE);
			s_log.debug("total items = " + newsItems.size());
			newsItems.setRange(
				new Integer(1),
				new Integer(m_portlet.getItemCount() + 1));

			ContentPage newsItem = null;
            while (newsItems.next()) {
				Element item =
					main.newChildElement(NEWS_ITEM_ELEMENT, XML_NEWS_NS);
				//newsItem = (ContentPage) newsItems.getDomainObject();
				newsItem = (ContentPage)DomainObjectFactory
                                        .newInstance(newsItems.getDataObject());
                Date d = newsItem.getLaunchDate();
				String date = (d != null) ? formatter.format(d) : "";
				item.addAttribute(DATE_ATTRIBUTE, date);

				item.addAttribute(TITLE_ATTRIBUTE, newsItem.getTitle());

                item.addAttribute(LEAD_ATTRIBUTE, newsItem.getSearchSummary());
				item.addAttribute(
					URL_ATTRIBUTE,
					Navigation.redirectURL(newsItem.getOID()));

            }
        }

        s_log.debug("END - getGeneralNews");
    }

    
		/*
		public Object getCacheKey() {
			
			if (getProfiledUser() == null) {
				return GENERAL_NEWS_CACHE_KEY;
			} else {
				
			
			return m_portlet.getID();
			}
		}
		
		// is dirty if edited, as this means the number of entries has changed. 
        // For non personalised news, dirty if homepage
		// newsitems are added or edited. 
        // For personalised, dirty if pushed items added or edited.
		
		public boolean isDirty() {
			
			
		// has it been edited? - add in this condition when response 
        // from Redhat about AbstractPortletRenderer
		
				// if not has news changed
				if (getCacheKey().equals(GENERAL_NEWS_CACHE_KEY)) {
					isDirty = m_portlet.isNewNews();
					s_log.debug("general news: dirty? " + isDirty);
		
				} else {
					// implement later
					isDirty = true;
				}
			}
			return isDirty;
		*/
	}
