/*
 * Copyright (C) 2009 Permeance Technologies Pty Ltd. All Rights Reserved.
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 */

package com.arsdigita.london.terms.indexing.kea;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import kea.filters.KEAFilter;

import org.apache.log4j.Logger;

import com.arsdigita.categorization.Category;
import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.TextAsset;
import com.arsdigita.cms.contenttypes.Article;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.london.terms.Domain;
import com.arsdigita.london.terms.Term;
import com.arsdigita.london.terms.indexing.Indexer;
import com.arsdigita.london.terms.indexing.RankedTerm;
import com.arsdigita.tools.junit.framework.BaseTestCase;

/**
 * @author <a href="https://sourceforge.net/users/terry_permeance/">terry_permeance</a>
 */
public class IndexerServiceTest extends BaseTestCase {
    public IndexerServiceTest(String name) {
        super(name);
    }

    public void xtestControlledIndexingWithEmptyTrainingSet() {
    }

    public void testControlledIndexingWithTrivialTrainingSet() throws IOException {
        doTestControlledIndexing();
    }

    public void testControlledIndexingWithSmallTrainingSet() throws IOException {
        doTestControlledIndexing();
    }

    public void testControlledIndexingWithMediumTrainingSet() throws IOException {
        doTestControlledIndexing();
    }

    public void testControlledIndexingWithLargeTrainingSet() throws IOException {
        doTestControlledIndexing();
    }

    public void xtestFreeIndexingWithEmptyTrainingSet() {
    }

    public void xtestFreeIndexingWithTrivialTrainingSet() throws IOException {
        doTestFreeIndexing();
    }

    public void xtestFreeIndexingWithSmallTrainingSet() throws IOException {
        doTestFreeIndexing();
    }

    public void xtestFreeIndexingWithMediumTrainingSet() throws IOException {
        doTestFreeIndexing();
    }

    public void xtestFreeIndexingWithLargeTrainingSet() throws IOException {
        doTestFreeIndexing();
    }

    protected void setUp() throws Exception {
        super.setUp();

        m_domain = Domain.retrieve(DOMAIN_KEY);
        m_itemCount = 0;
        m_contentItem = null;

        setUpTrainingSet();
        setUpContentItem();

        VocabularyCache.getVocabulary(m_domain, m_contentItem.getLanguage());
    }

    protected void tearDown() throws Exception {
        FilterCache.reset();
    }

    private void doTestControlledIndexing() throws IOException {
        IndexerService indexer = new IndexerService();
        KEAFilter filter = FilterCache.getFilter(m_domain, m_contentItem.getLanguage());
        List<RankedTerm> keyphrases = indexer.controlledIndex(filter, m_domain, 16, m_contentItem);
        assertNotNull("keyphrases is null", keyphrases);
        assertTrue("empty keyphrases", !keyphrases.isEmpty());

        s_log.info("Controlled index (training set size " + m_itemCount + "): ");
        for (Iterator<RankedTerm> i = keyphrases.iterator(); i.hasNext();) {
            RankedTerm rankedTerm = i.next();
            s_log.info("    -> " + rankedTerm.getTerm().getName() + " @ " + rankedTerm.getRanking());
        }
    }

    private void doTestFreeIndexing() throws IOException {
        fail("Not implemented");
    }

    private void setUpTrainingSet() throws IOException {
        int maxCount = 0;
        if (getName().indexOf("TrivialTrainingSet") > 0) {
            maxCount = 1;
        } else if (getName().indexOf("SmallTrainingSet") > 0) {
            maxCount = 5;
        } else if (getName().indexOf("MediumTrainingSet") > 0) {
            maxCount = 10;
        } else if (getName().indexOf("LargeTrainingSet") > 0) {
            maxCount = 25;
        }

        s_log.info("Creating up to " + maxCount + " articles in training set...");
        StringTokenizer dataFiles = new StringTokenizer(getFileContents("training/index"), "\r\n");
        for (int i = 0; i < maxCount && dataFiles.hasMoreTokens(); i++) {
            String title = dataFiles.nextToken();
            ContentItem item = createContentItemFromFile("training/" + title);
            StringTokenizer termNames = new StringTokenizer(getFileContents("training/" + title + ".key"), "\r\n");
            while (termNames.hasMoreTokens()) {
                String termName = termNames.nextToken();
                Term term = getTerm(termName);
                if (term == null) {
                    s_log.warn("Non-existent term '" + termName + "' was assigned to " + title);
                } else {
                    ((ContentBundle) item.getParent()).addCategory(new Category(term.getModel().getOID()));
                }
            }
            item.save();
            m_itemCount++;
        }
        s_log.info("Created " + m_itemCount + " articles in training set");

        s_log.info("Training indexer...");
        Indexer indexer = Indexer.create(m_domain);
        indexer.train();
        indexer.save();
        s_log.info("Training completed");
    }

    private void setUpContentItem() throws IOException {
        m_contentItem = createContentItemFromFile(TEST_ARTICLE_NAME);
        m_contentItem.save();
    }

    private ContentItem createContentItemFromFile(String baseName) throws IOException {
        s_log.debug("Creating content item from file " + baseName);

        TextAsset textAsset = new TextAsset();
        textAsset.setName(baseName + ".textAsset");
        textAsset.setText(getFileContents(baseName + ".txt"));
        textAsset.save();

        Article article = new Article();
        article.setTitle(baseName);
        article.setDescription(baseName + ".description");
        article.setLead(baseName + ".lead");
        article.setTextAsset(textAsset);
        article.setName(baseName.toLowerCase().replace('.', '-'));
        article.setLanguage("en");
        article.save();

        ContentBundle bundle = new ContentBundle(article);
        bundle.setName(article.getName());
        bundle.save();

        return article;
    }

    private String getFileContents(String resourceName) throws IOException {
        File file = new File("ccm-ldn-terms/data/indexing/" + resourceName);
        FileReader reader = new FileReader(file);
        try {
            int bytesRead = -1;
            char[] buffer = new char[10240];
            StringBuilder contents = new StringBuilder();
            while ((bytesRead = reader.read(buffer)) > 0) {
                contents.append(buffer, 0, bytesRead);
            }
            return contents.toString();
        } finally {
            reader.close();
        }
    }

    private Term getTerm(String name) {
        DomainCollection terms = m_domain.getTerms();
        terms.addFilter("upper(model.name) = upper(:name)");
        terms.setParameter("name", name);

        if (terms.next()) {
            Term term = (Term) terms.getDomainObject();
            terms.close();
            return term;
        }
        throw new DataObjectNotFoundException("Term '" + name + "' not found in domain " + m_domain.getKey());

    }

    private int m_itemCount;

    private Domain m_domain;

    private ContentItem m_contentItem;

    private static final String DOMAIN_KEY = "AGROVOC";

    private static final String TEST_ARTICLE_NAME = "test/bostid_b12sae";

    private static final Logger s_log = Logger.getLogger(IndexerServiceTest.class);
}
