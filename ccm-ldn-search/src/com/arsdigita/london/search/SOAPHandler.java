/*
 * Copyright (C) 2001 ArsDigita Corporation. All Rights Reserved.
 *
 * The contents of this file are subject to the ArsDigita Public 
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.arsdigita.com/ADPL.txt
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.london.search;

import com.arsdigita.search.QuerySpecification;
import com.arsdigita.search.ResultSet;
import com.arsdigita.search.Document;
import com.arsdigita.search.filters.PermissionFilterSpecification;
import com.arsdigita.london.util.Transaction;
import com.arsdigita.cms.search.VersionFilterSpecification;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.SecurityManager;

import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.permissions.PermissionManager;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.persistence.OID;

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;
import java.math.BigDecimal;


public class SOAPHandler {

    private static final org.apache.log4j.Logger s_log =
        org.apache.log4j.Logger.getLogger(SOAPHandler.class);

    public Collection search(final String terms) {
        final Vector results = new Vector();
        
        new Transaction() {
            public void doRun() {
                doSearch(terms, results);
            }
        }.run();

        return results;
    }

    private void doSearch(String terms,
                          Collection results) {
        QuerySpecification spec = new QuerySpecification(terms, false);
        if (com.arsdigita.search.Search.getConfig().isIntermediaEnabled()) {
            spec.addFilter(new PermissionFilterSpecification());
        }
        spec.addFilter(new VersionFilterSpecification(ContentItem.LIVE));

        ResultSet resultSet = com.arsdigita.search.Search.processInternal(
                spec,
                com.arsdigita.search.Search.getConfig().getIndexer());
        Iterator docs = resultSet.getDocuments(0, 50);

        s_log.debug("About to return results for query: " + terms);
        
        while (docs.hasNext()) {
            Document doc = (Document)docs.next();
            
            SearchResult result = new SearchResult(
                (BigDecimal)doc.getOID().get("id"),
                doc.getTitle(),
                doc.getURL().toString(),
                doc.getSummary(),
                doc.getScore());
            
            results.add(result);
        }
        resultSet.close();

        s_log.debug("results: " + results.size());
    }


}
