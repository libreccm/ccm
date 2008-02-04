/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.search;

import com.arsdigita.domain.DomainObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.search.filters.ObjectTypeFilterSpecification;
import com.arsdigita.search.filters.ObjectTypeFilterType;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * Implements a simple 'in memory' search index
 */
public class TestSearchIndex {
    
    private static final Logger s_log = 
        Logger.getLogger(TestSearchIndex.class);

    private static Map s_documents = new HashMap();

    private static int s_searches = 0;

    public static void reset() {
        s_documents = new HashMap();
    }
    
    public static void addDocument(DomainObject dobj) {
        MetadataProvider metadata = MetadataProviderRegistry
            .findAdapter(dobj.getOID().getObjectType());
        
        s_documents.put(dobj.getOID(), new TestDocument(dobj, metadata));
    }
    
    public static void removeDocument(DomainObject dobj) {
        s_documents.remove(dobj.getOID());
    }
    
    public static boolean containsDocument(DomainObject dobj) {
        return s_documents.containsKey(dobj.getOID());
    }

    public static int getSearchCount() {
        return s_searches;
    }

    public static ResultSet findDocuments(QuerySpecification spec) {
        s_log.debug("Do one search for " + spec);

        s_searches++;
        Iterator docs = s_documents.keySet().iterator();
        
        List matched  = new ArrayList();
        Date start = new Date();
        int maxScore = 0;

        while (docs.hasNext()) {
            OID oid = (OID)docs.next();
            TestDocument doc = (TestDocument)s_documents.get(oid);
            
            int score = calculateScore(doc, spec);
            if (score > maxScore) {
                maxScore = score;
            }

            if (score != 0) {
                matched.add(new Object[] {doc, new Integer(score)});
            }
        }
        
        
        return new TestResultSet(generateResults(matched, maxScore), 
                                 (new Date()).getTime() - 
                                 start.getTime());
    }
    
    public static int calculateScore(TestDocument doc,
                                     QuerySpecification spec) {
        String terms = spec.getTerms();
        String text = doc.getText();
        int score = 0;
        
        // Count occrrances of 'terms' in 'text
        int offset = text.indexOf(terms);
        while (offset != -1) {
            score++;
            offset = text.indexOf(terms, offset+1);
        }
        
        // Apply filters
        FilterSpecification[] filters = spec.getFilters();
        for (int i = 0 ; i < filters.length ; i++) {
            if (filters[i].getType().equals(ObjectTypeFilterType.KEY)) {
                ObjectType[] types = ((ObjectTypeFilterSpecification)
                                      filters[i]).getTypes();
                
                boolean found = false;
                for (int j = 0 ; j < types.length ; j++) {
                    ObjectType type = (ObjectType)types[j];
                    
                    if (doc.getOID().getObjectType().equals(type)) {
                        found = true;
                    }
                }
                if (!found) {
                    score = 0;
                }
            }
        }
        
        return score;
    }
    
    public static List generateResults(List matched, int maxScore) {
        List results = new ArrayList();
        Iterator docs = matched.iterator();
        while (docs.hasNext()) {
            Object[] entry = (Object[])docs.next();
            TestDocument doc = (TestDocument)entry[0];
            Integer score = (Integer)entry[1];
            
            double weightedScore = 
                ((double)(score.intValue() * 100) / (double)maxScore);
            
            results.add(new BaseDocument(
                            doc.getOID(),
                            doc.getLocale(),
                            doc.getTitle(),
                            doc.getSummary(),
                            doc.getCreationDate(),
                            doc.getCreationParty(),
                            doc.getLastModifiedDate(),
                            doc.getLastModifiedParty(),
                            new BigDecimal(weightedScore)
                        ));
        }

        return results;
    }
}
