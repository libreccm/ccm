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

import kea.filters.KEAFilter;
import kea.stemmers.PorterStemmer;

import org.apache.log4j.Logger;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

import com.arsdigita.cms.ContentItem;
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.london.terms.Domain;
import com.arsdigita.london.terms.Term;
import com.arsdigita.london.terms.indexing.IndexingConfig;
import com.arsdigita.search.ContentProvider;
import com.arsdigita.search.ContentType;
import com.arsdigita.search.MetadataProvider;
import com.arsdigita.search.MetadataProviderRegistry;
import com.arsdigita.util.Assert;

/**
 * @author <a href="https://sourceforge.net/users/terry_permeance/">terry_permeance</a>
 */
class FilterBuilder {
    private static final Logger s_log = Logger.getLogger(FilterBuilder.class);

    private final Domain m_domain;

    private final String m_language;

    private final IndexingConfig m_config = IndexingConfig.getInstance();

    public FilterBuilder(Domain domain, String language) {
        Assert.exists(domain);
        Assert.exists(language);

        m_domain = domain;
        m_language = language;
    }

    public KEAFilter build() {

        try {
            FastVector atts = new FastVector(2);
            atts.addElement(new Attribute("doc", (FastVector) null));
            atts.addElement(new Attribute("keyphrases", (FastVector) null));
            Instances data = new Instances("keyphrase_training_data", atts, 0);

            // Build model
            KEAFilter filter = new KEAFilter();

            filter.setDebug(false);
            filter.setDisallowInternalPeriods(m_config.disallowInternalPeriods());
            filter.setKFused(m_config.keyphraseFrequencyEnabled());
            filter.setMaxPhraseLength(m_config.getMaxPhraseLength());
            filter.setMinPhraseLength(m_config.getMinPhraseLength());
            filter.setMinNumOccur(m_config.getMinPhraseOccurrences());
            filter.setCheckForProperNouns(m_config.checkForProperNouns());
            filter.setStemmer(new PorterStemmer());
            filter.setDocumentLanguage(m_language);
            filter.setVocabulary(m_domain.getKey());
            filter.setVocabularyFormat("aplaws");
            filter.setStopwords(new Stopwords(m_language));
            filter.setInputFormat(data);
            filter.setNumFeature();
            filter.m_Vocabulary = VocabularyCache.getVocabulary(m_domain, filter.getDocumentLanguage());

            s_log.debug("Reading the training content... ");
            Queries.TrainingItems items = new Queries.TrainingItems(m_domain, filter.getDocumentLanguage());
            items.setRange(0, m_config.getMaxTrainingItems() + 1);

            try {
                while (items.next()) {
                    ContentItem item = new ContentItem(items.getID());

                    if (s_log.isDebugEnabled()) {
                        s_log.debug("    --> Reading " + item.getName());
                    }

                    double[] newInst = new double[2];

                    // Text content
                    MetadataProvider adapter = MetadataProviderRegistry.findAdapter(item.getObjectType());
                    ContentProvider[] content = adapter.getContent(item, ContentType.TEXT);
                    StringBuffer buf = new StringBuffer();
                    for (int i = 0, n = content.length; i < n; i++) {
                        if (content[i].getType().equals(ContentType.TEXT)) {
                            buf.append(new String(content[i].getBytes()));
                        }
                    }
                    newInst[0] = (double) data.attribute(0).addStringValue(buf.toString());

                    // Assigned terms
                    StringBuffer keyStr = new StringBuffer();
                    DomainCollection terms = m_domain.getTerms();
                    try {
                        terms.addEqualsFilter("model.childObjects.contentChildren", item.getID());

                        while (terms.next()) {
                            Term nextTerm = (Term) terms.getDomainObject();
                            keyStr.append(nextTerm.getModel().getName().toUpperCase());
                            keyStr.append("\n");
                        }
                    } finally {
                        terms.close();
                    }
                    newInst[1] = (double) data.attribute(1).addStringValue(keyStr.toString());

                    // Train
                    data.add(new Instance(1.0, newInst));
                    filter.input(data.instance(0));
                    data = data.stringFreeStructure();
                }
            } finally {
                items.close();
            }

            filter.batchFinished();

            while ((filter.output()) != null) {
                // Nothing to do here!
            }
            return filter;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
