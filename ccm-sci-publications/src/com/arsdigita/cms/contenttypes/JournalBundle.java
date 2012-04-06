package com.arsdigita.cms.contenttypes;

import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.CustomCopy;
import com.arsdigita.cms.ItemCopier;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.metadata.Property;
import com.arsdigita.util.Assert;
import java.math.BigDecimal;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class JournalBundle extends ContentBundle {

    public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.cms.contenttypes.JournalBundle";
    public static final String ARTICLES = "articles";
    public static final String ARTICLE_ORDER = "articleOrder";

    public JournalBundle(final ContentItem primary) {
        super(BASE_DATA_OBJECT_TYPE);

        Assert.exists(primary, ContentItem.class);

        setDefaultLanguage(primary.getLanguage());
        setContentType(primary.getContentType());
        addInstance(primary);

        setName(primary.getName());
    }

    public JournalBundle(final OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    public JournalBundle(final BigDecimal id)
            throws DataObjectNotFoundException {
        super(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public JournalBundle(final DataObject dobj) {
        super(dobj);
    }

    public JournalBundle(final String type) {
        super(type);
    }

    @Override
    public boolean copyProperty(final CustomCopy source,
                                final Property property,
                                final ItemCopier copier) {
        final String attribute = property.getName();
        if (copier.getCopyType() == ItemCopier.VERSION_COPY) {
            final JournalBundle journalBundle = (JournalBundle) source;

            if (ARTICLES.equals(attribute)) {
                final DataCollection articles = (DataCollection) journalBundle.
                        get(ARTICLES);

                while (articles.next()) {
                    createArticleAssoc(articles);
                }
                
                return true;
            } else {
                return super.copyProperty(source, property, copier);
            }
        } else {
            return super.copyProperty(source, property, copier);
        }
    }

    private void createArticleAssoc(final DataCollection articles) {
        final ArticleInJournalBundle draftArticle =
                                     (ArticleInJournalBundle) DomainObjectFactory.
                newInstance(articles.getDataObject());
        final ArticleInJournalBundle liveArticle =
                                     (ArticleInJournalBundle) draftArticle.
                getLiveVersion();

        if (liveArticle != null) {
            final DataObject link = add(ARTICLES, liveArticle);

            link.set(ARTICLE_ORDER, articles.get(
                    ArticleInJournalCollection.LINKORDER));

            link.save();
        }
    }

    public ArticleInJournalCollection getArticles() {
        return new ArticleInJournalCollection((DataCollection) get(ARTICLES));
    }

    public void addArticle(final ArticleInJournal article) {
        Assert.exists(article, ArticleInJournal.class);

        final DataObject link = add(ARTICLES,
                                    article.getArticleInJournalBundle());
        link.set(ARTICLE_ORDER, Integer.valueOf((int) getArticles().size()));
    }

    public void removeArticle(final ArticleInJournal article) {
        Assert.exists(article, ArticleInJournal.class);

        remove(ARTICLES, article.getArticleInJournalBundle());
    }
}
