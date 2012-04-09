/*
 * Copyright (c) 2010 Jens Pelzetter,
 * for the Center of Social Politics of the University of Bremen
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

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.Assert;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class ArticleInJournal extends Publication {

    public static final String VOLUME = "volume";
    public static final String ISSUE = "issue";
    public static final String PAGES_FROM = "pagesFrom";
    public static final String PAGES_TO = "pagesTo";
    public static final String JOURNAL = "journal";
    public static final String PUBLICATION_DATE = "publicationDate";
    //public static final String REVIEWED = "reviewed";
    public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.cms.contenttypes.ArticleInJournal";

    public ArticleInJournal() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    public ArticleInJournal(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public ArticleInJournal(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    public ArticleInJournal(DataObject dataObject) {
        super(dataObject);
    }

    public ArticleInJournal(String type) {
        super(type);
    }

    public ArticleInJournalBundle getArticleInJournalBundle() {
        return (ArticleInJournalBundle) getContentBundle();
    }

    public Integer getVolume() {
        return (Integer) get(VOLUME);
    }

    public void setVolume(Integer volume) {
        set(VOLUME, volume);
    }

    public String getIssue() {
        return (String) get(ISSUE);
    }

    public void setIssue(String issue) {
        set(ISSUE, issue);
    }

    public Integer getPagesFrom() {
        return (Integer) get(PAGES_FROM);
    }

    public void setPagesFrom(Integer pagesFrom) {
        set(PAGES_FROM, pagesFrom);
    }

    public Integer getPagesTo() {
        return (Integer) get(PAGES_TO);
    }

    public void setPagesTo(Integer pagesTo) {
        set(PAGES_TO, pagesTo);
    }

    public Date getPublicationDate() {
        return (Date) get(PUBLICATION_DATE);
    }

    public void setPublicationDate(Date publicationDate) {
        set(PUBLICATION_DATE, publicationDate);
    }

    /* public Boolean getReviewed() {
     return (Boolean) get(REVIEWED);
     }

     public void setReviewed(Boolean reviewed) {
     set(REVIEWED, reviewed);
     }*/
    public Journal getJournal() {
//        DataCollection collection;
//
//        collection = (DataCollection) get(JOURNAL);
//
//        if (collection.size() == 0) {
//            return null;
//        } else {
//            DataObject dobj;
//
//            collection.next();
//            dobj = collection.getDataObject();
//            collection.close();
//
//            return (Journal) DomainObjectFactory.newInstance(dobj);
//        }

        final JournalBundle bundle = getArticleInJournalBundle().getJournal();
        if (bundle == null) {
            return null;            
        } else {
            return (Journal) bundle.getPrimaryInstance();
        }                
    }

    public Journal getJournal(final String language) {
        final JournalBundle bundle = getArticleInJournalBundle().getJournal();
        if (bundle == null) {
            return null;            
        } else {
            return (Journal) bundle.getInstance(language);
        }                
    }

    public void setJournal(final Journal journal) {
//        Journal oldJournal;
//
//        oldJournal = getJournal();
//        if (oldJournal != null) {
//            remove(JOURNAL, oldJournal);
//        }
//
//        if (journal != null) {
//            Assert.exists(journal, Journal.class);
//            DataObject link = add(JOURNAL, journal);
//            link.set(Journal.ARTICLE_ORDER,
//                     Integer.valueOf((int) journal.getArticles().size()));
//            link.save();
//        }
        
        getArticleInJournalBundle().setJournal(journal);
    }        
}
