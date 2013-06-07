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

import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.CustomCopy;
import com.arsdigita.cms.ItemCopier;
import com.arsdigita.domain.DataObjectNotFoundException;
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
 */
public class CollectedVolumeBundle extends PublicationWithPublisherBundle {

    public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.cms.contenttypes.CollectedVolumeBundle";
    public static final String ARTICLES = "articles";
    public static final String ARTICLE_ORDER = "articleOrder";

    public CollectedVolumeBundle(final ContentItem primary) {
        super(BASE_DATA_OBJECT_TYPE);

        Assert.exists(primary, ContentItem.class);

        setDefaultLanguage(primary.getLanguage());
        setContentType(primary.getContentType());
        addInstance(primary);

        setName(primary.getName());
    }

    public CollectedVolumeBundle(final OID oid)
            throws DataObjectNotFoundException {
        super(oid);
    }

    public CollectedVolumeBundle(final BigDecimal id)
            throws DataObjectNotFoundException {
        super(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public CollectedVolumeBundle(final DataObject dobj) {
        super(dobj);
    }

    public CollectedVolumeBundle(final String type) {
        super(type);
    }

    @Override
    public boolean copyProperty(final CustomCopy source,
                                final Property property,
                                final ItemCopier copier) {
        final String attribute = property.getName();

        if (copier.getCopyType() == ItemCopier.VERSION_COPY) {
            final CollectedVolumeBundle collVolBundle =
                                        (CollectedVolumeBundle) source;

            if (ARTICLES.equals(attribute)) {
                final DataCollection articles = (DataCollection) collVolBundle.
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
        final ArticleInCollectedVolumeBundle draftArticle =
                                             (ArticleInCollectedVolumeBundle) DomainObjectFactory.
                newInstance(articles.getDataObject());
        final ArticleInCollectedVolumeBundle liveArticle =
                                             (ArticleInCollectedVolumeBundle) draftArticle.
                getLiveVersion();

        if (liveArticle != null) {
            final DataObject link = add(ARTICLES, liveArticle);

            link.set(ARTICLE_ORDER,
                     articles.get(ArticleInCollectedVolumeCollection.LINKORDER));

            link.save();
        }
    }

    public ArticleInCollectedVolumeCollection getArticles() {
        return new ArticleInCollectedVolumeCollection((DataCollection) get(
                ARTICLES));
    }

    public void addArticle(final ArticleInCollectedVolume article) {
        Assert.exists(article, ArticleInCollectedVolume.class);

        final DataObject link = add(ARTICLES,
                                    article.getArticleInCollectedVolumeBundle());

        link.set(ARTICLE_ORDER, Integer.valueOf((int) getArticles().size()));
    }

    public void removeArticle(final ArticleInCollectedVolume article) {
        Assert.exists(article, ArticleInCollectedVolume.class);

        remove(ARTICLES, article.getArticleInCollectedVolumeBundle());
    }
}
