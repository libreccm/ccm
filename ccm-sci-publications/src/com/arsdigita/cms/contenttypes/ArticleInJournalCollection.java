/*
 * Copyright (c) 2010 Jens Pelzetter
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

import com.arsdigita.cms.ContentBundle;
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class ArticleInJournalCollection extends DomainCollection {

    public static final String LINKORDER = "link.articleOrder";
    public static final String ORDER = "articleOrder";
    private static final Logger s_log =
                                Logger.getLogger(
            ArticleInJournalCollection.class);

    public ArticleInJournalCollection(DataCollection dataCollection) {
        super(dataCollection);

        m_dataCollection.addOrder(LINKORDER);
    }

    public Integer getArticleOrder() {
        return (Integer) m_dataCollection.get(LINKORDER);
    }

    public void setArticleOrder(Integer order) {
        DataObject link = (DataObject) this.get("link");

        link.set(ORDER, order);
    }

    public void swapWithNext(ArticleInJournal article) {
        int currentPosition = 0;
        int currentIndex = 0;
        int nextIndex = 0;

        s_log.debug("Searching article...");
        this.rewind();
        while (this.next()) {
            currentPosition = this.getPosition();
            currentIndex = this.getArticleOrder();
            s_log.debug(String.format("Position: %d(%d)/%d", currentPosition,
                                      currentIndex, this.size()));
            s_log.debug(String.format("getArticleOrder(): %d",
                                      getArticleOrder()));
            if (this.getArticle().equals(article)) {
                break;
            }
        }

        if (currentPosition == 0) {
            throw new IllegalArgumentException(
                    String.format(
                    "The provided article is not "
                    + "part of this collection."));
        }

        if (this.next()) {
            nextIndex = this.getArticleOrder();
        } else {
            throw new IllegalArgumentException(
                    "The provided article is the last "
                    + "in the collection, so there is no next object "
                    + "to swap with.");
        }

        this.rewind();

        while (this.getPosition() != currentPosition) {
            this.next();
        }

        this.setArticleOrder(nextIndex);
        this.next();
        this.setArticleOrder(currentIndex);
        this.rewind();
    }

    public void swapWithPrevious(ArticleInJournal article) {
        int previousPosition = 0;
        int previousIndex = 0;
        int currentPosition = 0;
        int currentIndex = 0;

        s_log.debug("Searching article...");
        this.rewind();
        while (this.next()) {
            currentPosition = this.getPosition();
            currentIndex = this.getArticleOrder();
            s_log.debug(String.format("Position: %d(%d)/%d", currentPosition,
                                      currentIndex, this.size()));
            s_log.debug(String.format("getArticleOrder(): %d",
                                      getArticleOrder()));
            if (this.getArticle().equals(article)) {
                break;
            }

            previousPosition = currentPosition;
            previousIndex = currentIndex;
        }

        if (currentPosition == 0) {
            throw new IllegalArgumentException(
                    String.format(
                    "The provided article is not "
                    + "part of this collection."));
        }

        if (previousPosition == 0) {
            throw new IllegalArgumentException(
                    String.format(
                    "The provided article is the first one in this "
                    + "collection, so there is no previous one to switch "
                    + "with."));
        }

        this.rewind();
        while (this.getPosition() != previousPosition) {
            this.next();
        }

        this.setArticleOrder(currentIndex);
        this.next();
        this.setArticleOrder(previousIndex);
        this.rewind();
    }

    public ArticleInJournal getArticle() {
        //return (ArticleInJournal) DomainObjectFactory.newInstance(m_dataCollection.
        //        getDataObject());
        final ContentBundle bundle = (ContentBundle) DomainObjectFactory.
                newInstance(m_dataCollection.getDataObject());

        return (ArticleInJournal) bundle.getPrimaryInstance();
    }

    public ArticleInJournal getArticle(final String language) {
        final ContentBundle bundle = (ContentBundle) DomainObjectFactory.
                newInstance(m_dataCollection.getDataObject());

        return (ArticleInJournal) bundle.getInstance(language);
    }
}
