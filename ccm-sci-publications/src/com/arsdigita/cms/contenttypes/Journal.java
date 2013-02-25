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

import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ExtraXMLGenerator;
import com.arsdigita.cms.contenttypes.ui.JournalExtraXmlGenerator;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class Journal extends ContentPage {

    public static final String ISSN = "issn";
    public static final String FIRST_YEAR = "firstYear";
    public static final String LAST_YEAR = "lastYear";
    public static final String ABSTRACT = "abstract";
    public static final String ARTICLES = "articles";
    public static final String ARTICLE_ORDER = "articleOrder";   
    public static final String SYMBOL = "symbol";
    public static final String BASE_DATA_OBJECT_TYPE = "com.arsdigita.cms.contenttypes.Journal";

    public Journal() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    public Journal(final BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public Journal(final OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    public Journal(final DataObject dobj) {
        super(dobj);
    }

    public Journal(final String type) {
        super(type);
    }

    public JournalBundle getJournalBundle() {
        return (JournalBundle) getContentBundle();
    }       
    
    public String getISSN() {
        return (String) get(ISSN);
    }

    public void setISSN(final String issn) {
        set(ISSN, issn);
    }

    public Integer getFirstYear() {
        return (Integer) get(FIRST_YEAR);
    }

    public void setFirstYear(final Integer firstYear) {
        set(FIRST_YEAR, firstYear);
    }

    public Integer getLastYear() {
        return (Integer) get(LAST_YEAR);
    }

    public void setLastYear(final Integer lastYear) {
        set(LAST_YEAR, lastYear);
    }

    public String getAbstract() {
        return (String) get(ABSTRACT);
    }

    public void setAbstract(final String abstractStr) {
        set(ABSTRACT, abstractStr);
    }

    public ArticleInJournalCollection getArticles() {
        //return new ArticleInJournalCollection((DataCollection) get(ARTICLES));
        return getJournalBundle().getArticles();
    }

    public void addArticle(final ArticleInJournal article) {
        //Assert.exists(article, ArticleInJournal.class);

        //DataObject link = add(ARTICLES, article);

        //link.set(ARTICLE_ORDER, Integer.valueOf((int) getArticles().size()));

        getJournalBundle().addArticle(article);
    }

    public void removeArticle(final ArticleInJournal article) {
        //Assert.exists(article, ArticleInCollectedVolume.class);
        //remove(ARTICLES, article);

        getJournalBundle().removeArticle(article);
    }

    public boolean hasArticles() {
        return !this.getArticles().isEmpty();
    }

    /**
     * The symbol used commonly used for referencing the journal (german: Kürzel).
     * 
     * @return 
     */
    public String getSymbol() {
        return (String) get(SYMBOL);
    }

    public void setSymbol(final String symbol) {
        set(SYMBOL, symbol);        
    }

    @Override
    public List<ExtraXMLGenerator> getExtraXMLGenerators() {
        final List<ExtraXMLGenerator> generators = super.getExtraXMLGenerators();
        generators.add(new JournalExtraXmlGenerator());
        return generators;
    }

    @Override
    public List<ExtraXMLGenerator> getExtraListXMLGenerators() {
        final List<ExtraXMLGenerator> generators = super.getExtraListXMLGenerators();
        generators.add(new JournalExtraXmlGenerator());
        return generators;
    }

}
