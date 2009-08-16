/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
 */

package com.arsdigita.london.terms.importer;

import java.net.URL;

import org.apache.log4j.Logger;

import com.arsdigita.domain.DomainCollection;
import com.arsdigita.london.terms.Domain;
import com.arsdigita.london.terms.Term;
import com.arsdigita.util.Assert;

public class SynonymsBuilder {

    private static final Logger s_log = Logger.getLogger(SynonymsBuilder.class);

    private Domain m_domain;

    public void findDomain(String url) throws Exception {
        s_log.debug("Entering find domain");
        m_domain = Domain.find(new URL(url));
        s_log.debug("Domain " + m_domain);

        removeSynonyms();
    }

    /**
     * Find all the terms and remove all the synonym associations
     */
    private void removeSynonyms() {
        s_log.debug("Removing synonym terms from domain " + m_domain);
        DomainCollection allTerms = m_domain.getTerms();
        while (allTerms.next()) {
            Term t = (Term) allTerms.getDomainObject();
            DomainCollection preferredTerms = t.getPreferredTerms();
            while (preferredTerms.next()) {
                Term preferred = (Term) preferredTerms.getDomainObject();
                t.removePreferredTerm(preferred);
            }
            
        }

    }

    public void addSynonymTerm(Integer id, Integer synonymID) {
        Assert.exists(m_domain, Domain.class);
        Assert.exists(id, Integer.class);
        Assert.exists(synonymID, Integer.class);
        s_log.debug("addSynonymTerm id" + id + " synonymID " + synonymID);

        Term term = m_domain.getTerm(id);
        Term synonymTerm = m_domain.getTerm(synonymID);
        synonymTerm.addPreferredTerm(term);

        s_log.debug("addSynonymTerm Preferred " + term.getName() + " Synonym "
                + synonymTerm.getName());
    }

}
