/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.contenttypes;

import com.arsdigita.cms.ContentType;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.Assert;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.Date;

/**
 * <p><code>DomainObject</code> class to represent news item <code>ContentType</code>
 * objects. <br />
 * It represents an news object and provides methods for creating new newsItem
 * objects, retrieving existing objects from the persistent storage and retrieving 
 * and setting is properties. </p>
 * <p>The type inherits title, name (filename), body (TextAsset), and metadata from
 * <code>com.arsdigita.aplaws.cms.Article</code>, and adds extended attributes
 * specific for a news item:</p>
 * <dl>
 *  <dt>Summary (lead)</dt>  <dd>optional, standard text field, short  description 
 *                               (summary), used as lead text, also part of a list 
 *                               view and a teaser on the portal / homepage</dd> 
 *  <dt>Homepage</dt>        <dd>mandatory, should the news item be published on the 
 *                               portal homepage. Will be used by the function 
 *                               recentItems</dd>
 *  <dt>Date</dt>            <dd>optional, release date  of the news item. Release 
 *                               date has nothing to do with publishing, but allows 
 *                               the author to specify the original release date for 
 *                               reference purposes.</dd>
 * </dl>
 * <p>Some of its behaviour can be configured by <code>NewsItemConfig</code>.</p>
 *
 * @author Shashin Shinde <a href="mailto:sshinde@redhat.com">sshinde@redhat.com</a>
 * @version $Revision: #7 $ $Date: 2004/08/17 $
 **/
public class NewsItem extends GenericArticle {

    /** PDL property name for lead */
    public static final String LEAD = "lead";
    /** PDL property name for news date */
    public static final String NEWS_DATE = "newsDate";
    public static final String IS_HOMEPAGE = "isHomepage";
    public static final String RECENT_NEWS =
            "com.arsdigita.cms.contenttypes.RecentNews";
    /** Data object type for this domain object */
    public static final String BASE_DATA_OBJECT_TYPE = "com.arsdigita.cms.contenttypes.NewsItem";
    private static final NewsItemConfig s_config = new NewsItemConfig();

    static {
        s_config.load();
    }

    public static final NewsItemConfig getConfig() {
        return s_config;
    }

    public NewsItem() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    public NewsItem(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public NewsItem(OID id) throws DataObjectNotFoundException {
        super(id);
    }

    public NewsItem(DataObject obj) {
        super(obj);
    }

    public NewsItem(String type) {
        super(type);
    }

    @Override
    public void initialize() {
        super.initialize();

        if (isNew()) {
            setIsHomepage(Boolean.FALSE);
        }
    }

    @Override
    public void beforeSave() {
        super.beforeSave();

        Assert.exists(getContentType(), ContentType.class);
    }

    /* accessors *****************************************************/
    public String getLead() {
        return (String) get(LEAD);
    }

    public void setLead(String lead) {
        set(LEAD, lead);
    }

    public Boolean isHomepage() {
        final Boolean isHomepage = (Boolean) get(IS_HOMEPAGE);
        return isHomepage;
    }

    public void setIsHomepage(Boolean isHomePage) {
        set(IS_HOMEPAGE, isHomePage);
    }

    public Date getNewsDate() {
        return (Date) get(NEWS_DATE);
    }

    public String getDisplayNewsDate() {
        Date d = getNewsDate();
        return (d != null) ? DateFormat.getDateInstance(DateFormat.LONG).format(d) : null;
    }

    public void setNewsDate(Date newsDate) {
        set(NEWS_DATE, newsDate);
    }

    public static final int SUMMARY_LENGTH = 200;

    @Override
    public String getSearchSummary() {
        return com.arsdigita.util.StringUtils.truncateString(getLead(),
                SUMMARY_LENGTH,
                true);
    }

    /*
     * This static method returns the most recent news item (by news_date)
     */
    public static NewsItem getMostRecentNewsItem() {
        DataQuery newsItems = SessionManager.getSession().retrieveQuery(RECENT_NEWS);

        NewsItem newsItem = null;
        if (newsItems.next()) {
            try {
                newsItem = new NewsItem((BigDecimal) newsItems.get("newsID"));
            } catch (DataObjectNotFoundException ex) {
                //
            }
        }
        newsItems.close();

        return newsItem;
    }
}
