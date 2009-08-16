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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import kea.filters.KEAFilter;

import org.apache.log4j.Logger;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.TextPage;
import com.arsdigita.london.terms.Domain;
import com.arsdigita.london.terms.Term;
import com.arsdigita.london.terms.indexing.Indexer;
import com.arsdigita.london.terms.indexing.RankedTerm;

/**
 * @author <a href="https://sourceforge.net/users/terry_permeance/">terry_permeance</a>
 */
public class IndexerService {
    private static final Logger s_log = Logger.getLogger(Indexer.class);

    public List<RankedTerm> controlledIndex(Object f, Domain domain, int maxTerms, ContentItem item) {

        if (s_log.isDebugEnabled()) {
            s_log.debug("Extracting index from " + item);
        }
        final long t1 = System.currentTimeMillis();

        KEAFilter filter = (KEAFilter) f;
        filter.setNumPhrases(maxTerms);

        FastVector atts = new FastVector(3);
        atts.addElement(new Attribute("doc", (FastVector) null));
        atts.addElement(new Attribute("keyphrases", (FastVector) null));
        atts.addElement(new Attribute("filename", (String) null));
        Instances data = new Instances("keyphrase_training_data", atts, 0);

        // Extract keyphrases
        StringBuffer txtStr = new StringBuffer();
        txtStr.append(((TextPage) item).getTextAsset().getText());

        double[] newInst = new double[2];
        newInst[0] = (double) data.attribute(0).addStringValue(txtStr.toString());
        newInst[1] = Instance.missingValue();

        data.add(new Instance(1.0, newInst));
        try {
            filter.input(data.instance(0));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        data = data.stringFreeStructure();
        Instance[] topRankedInstances = new Instance[filter.getNumPhrases()];
        Instance inst;

        // Iterating over all extracted keyphrases (inst)
        while ((inst = filter.output()) != null) {
            int index = (int) inst.value(filter.getRankIndex()) - 1;
            if (index < filter.getNumPhrases()) {
                topRankedInstances[index] = inst;
            }
            if (s_log.isDebugEnabled()) {
                s_log.debug(inst.toString());
            }
        }

        // Extract the unique ID's of the matching keyphrases
        List<RankedTerm> terms = new ArrayList<RankedTerm>();
        for (int i = 0; i < filter.getNumPhrases(); i++) {
            if (topRankedInstances[i] != null) {
                String uniqueID = topRankedInstances[i].stringValue(filter.getStemmedPhraseIndex());
                BigDecimal ranking = BigDecimal.valueOf(topRankedInstances[i].value(filter.getProbabilityIndex()));
                Term term = domain.getTerm(uniqueID);
                terms.add(new RankedTerm(term, ranking));
            }
        }

        if (s_log.isDebugEnabled()) {
            s_log.debug("Extracted index from " + item + " in " + (System.currentTimeMillis() - t1) + "ms");
        }
        return terms;
    }

    public Object train(Domain domain, String language) {
        final long t1 = System.currentTimeMillis();

        if (s_log.isDebugEnabled()) {
            s_log.debug("Training indexer for domain " + domain.getKey() + "...");
        }
        KEAFilter filter = FilterCache.recreateFilter(domain, language);

        if (s_log.isDebugEnabled()) {
            s_log.debug("Trained indexer for domain " + domain.getKey() + " in " + (System.currentTimeMillis() - t1)
                    + "ms");
        }
        return filter;
    }
}
