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
 */
public class ArticleInJournal extends Publication {
    
    public static final String VOLUME = "volume";
    public static final String ISSUE = "issue";
    public static final String PAGES_FROM = "pagesFrom";
    public static final String PAGES_TO = "pagesTo";
    public static final String JOURNAL = "journal";
    public static final String PUBLICATION_DATE = "publicationDate";
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

    public Journal getJournal() {
        DataCollection collection;

        collection = (DataCollection) get(JOURNAL);

        if (collection.size() == 0) {
            return null;
        } else {
            DataObject dobj;

            collection.next();
            dobj = collection.getDataObject();
            collection.close();

            return (Journal) DomainObjectFactory.newInstance(dobj);
        }
    }

   public void setJournal(Journal journal) {
       Journal oldJournal;

       oldJournal = getJournal();
       if (oldJournal != null) {
           remove(JOURNAL, oldJournal);
       }

       if (journal != null) {
           Assert.exists(journal, Journal.class);
           DataObject link = add(JOURNAL, journal);
           link.set(Journal.ARTICLE_ORDER,
                   Integer.valueOf((int) journal.getArticles().size()));
           link.save();
       }
   }
}
