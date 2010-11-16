package com.arsdigita.cms.contenttypes;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.Assert;
import java.math.BigDecimal;

/**
 *
 * @author Jens Pelzetter
 */
public class Journal extends Publication {

    public static final String ISSN = "issn";
    public static final String ARTICLES = "articles";
    public static final String ARTICLE_ORDER = "articleOrder";
    public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.cms.contenttypes.Journal";

    public Journal() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    public Journal(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public Journal(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    public Journal(DataObject dobj) {
        super(dobj);
    }

    public Journal(String type) {
        super(type);
    }

    public String getISSN() {
        return (String) get(ISSN);
    }

    public void setISSN(String issn) {
        set(ISSN, issn);
    }

    public ArticleInJournalCollection getArticles() {
        return new ArticleInJournalCollection((DataCollection) get(ARTICLES));
    }

    public void addArticle(ArticleInJournal article) {
        Assert.exists(article, ArticleInJournal.class);

        DataObject link = add(ARTICLES, article);

        link.set(ARTICLE_ORDER, Integer.valueOf((int) getArticles().size()));
    }

    public void removeArticle(ArticleInJournal article) {
        Assert.exists(article, ArticleInCollectedVolume.class);
        remove(ARTICLES, article);
    }

    public boolean hasArticles() {
        return !this.getArticles().isEmpty();
    }
}
