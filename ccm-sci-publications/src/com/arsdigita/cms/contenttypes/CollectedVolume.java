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

import com.arsdigita.cms.ExtraXMLGenerator;
import com.arsdigita.cms.contenttypes.ui.CollectedVolumeExtraXmlGenerator;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import java.math.BigDecimal;
import java.util.List;

/**
 * A collected volume which consists of some {@link ArticleInCollectedVolume} 
 * instances.
 *
 * @author Jens Pelzetter
 */
public class CollectedVolume extends PublicationWithPublisher {

    public static final String ARTICLES = "articles";
    public static final String ARTICLE_ORDER = "articleOrder";
    //public static final String REVIEWED = "reviewed";
    public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.cms.contenttypes.CollectedVolume";

    public CollectedVolume() {
        super(BASE_DATA_OBJECT_TYPE);
    }

    public CollectedVolume(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public CollectedVolume(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    public CollectedVolume(DataObject dataObject) {
        super(dataObject);
    }

    public CollectedVolume(String type) {
        super(type);
    }

    /*public Boolean getReviewed() {
        return (Boolean) get(REVIEWED);
    }

    public  void setReviewed(Boolean reviewed) {
        set(REVIEWED, reviewed);
    }*/

    public CollectedVolumeBundle getCollectedVolumeBundle() {
        return (CollectedVolumeBundle) getContentBundle();
    }
    
    public ArticleInCollectedVolumeCollection getArticles() {
        //return new ArticleInCollectedVolumeCollection(
//                (DataCollection) get(ARTICLES));
            return getCollectedVolumeBundle().getArticles();
    }

    public void addArticle(ArticleInCollectedVolume article) {
//        Assert.exists(article, ArticleInCollectedVolume.class);
//
//        DataObject link = add(ARTICLES, article);
//
//        link.set(ARTICLE_ORDER, Integer.valueOf((int) getArticles().size()));
        
        getCollectedVolumeBundle().addArticle(article);
    }

    public void removeArticle(ArticleInCollectedVolume article) {
        //Assert.exists(article, ArticleInCollectedVolume.class);
        //remove(ARTICLES, article);
        getCollectedVolumeBundle().removeArticle(article);        
    }

    public boolean hasArticles() {
        return !this.getArticles().isEmpty();
    }
    
    @Override
    public List<ExtraXMLGenerator> getExtraXMLGenerators() {
        final List<ExtraXMLGenerator> generators = super.getExtraXMLGenerators();
        generators.add(new CollectedVolumeExtraXmlGenerator());
        return generators;
    }
    
    @Override
    public List<ExtraXMLGenerator> getExtraListXMLGenerators() {
        final List<ExtraXMLGenerator> generators = super.getExtraListXMLGenerators();
        generators.add(new CollectedVolumeExtraXmlGenerator());
        return generators;
    }
}
