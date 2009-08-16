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

package com.arsdigita.london.terms.importer.skos;

import java.net.URL;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.ObjectCreationFactory;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.london.terms.Domain;
import com.arsdigita.london.terms.Term;

/**
 * Builds a polyhierarchy of {@link Domain} polyhierarchy of {@link Term}'s.
 * 
 * @author <a href="https://sourceforge.net/users/terry_permeance/">terry_permeance</a>
 */
class DomainBuilder {
    public void createDomain(String key, URL url, String title, String description, String version, Date released)
            throws Exception {
        try {
            m_domain = Domain.retrieve(key);

            // Clear the polyhierarchy
            DomainCollection allTerms = m_domain.getTerms();
            while (allTerms.next()) {
                Term t = (Term) allTerms.getDomainObject();
                for (DomainCollection dc = t.getNarrowerTerms(); dc.next();) {
                    t.removeNarrowerTerm((Term) dc.getDomainObject());
                }
                for (DomainCollection dc = t.getPreferredTerms(); dc.next();) {
                    t.removeNarrowerTerm((Term) dc.getDomainObject());
                }
                for (DomainCollection dc = t.getRelatedTerms(); dc.next();) {
                    t.removeNarrowerTerm((Term) dc.getDomainObject());
                }
            }
            m_domain.setTitle(title);
            m_domain.setDescription(description);
            m_domain.setVersion(version);
            m_domain.setReleased(released);
            m_domain.save();
        } catch (DataObjectNotFoundException e) {
            new com.arsdigita.london.terms.importer.DomainBuilder().createDomain(key, url.toExternalForm(), title,
                    description, version, released.toString());
            m_domain = Domain.retrieve(key);
        }
    }

    public void build() {
        ProgressBar progressBar = new ProgressBar(m_termBuilders.size(), s_log);

        // Preload all existing terms into a cache
        Map<String, Term> termCache = new HashMap<String, Term>(106033);
        for (DomainCollection dc = m_domain.getTerms(); dc.next();) {
            Term term = (Term) dc.getDomainObject();
            termCache.put(term.getUniqueID(), term);
        }
        s_log.info("Domain " + m_domain.getKey() + " currently contains " + termCache.size() + " terms");

        // Create the terms
        s_log.info("Building terms for domain " + m_domain.getKey() + "...");
        for (Iterator<TermBuilder> i = m_termBuilders.iterator(); i.hasNext();) {
            progressBar.next();
            TermBuilder builder = i.next();
            builder.buildTerm(m_domain, termCache);
        }

        // Once the terms exist, the relationships can be created
        s_log.info("Building polyhierarchy for domain " + m_domain.getKey() + "...");
        progressBar.reset();
        for (Iterator<TermBuilder> i = m_termBuilders.iterator(); i.hasNext();) {
            progressBar.next();
            TermBuilder builder = i.next();
            builder.buildPolyhierarchy(m_domain, termCache);
        }

        s_log.info("Domain " + m_domain.getKey() + " now contains " + termCache.size() + " terms");
    }

    public ObjectCreationFactory newPendingTermFactory() {
        return new ObjectCreationFactory() {
            public Object createObject(Attributes attrs) throws Exception {
                URL url = Utils.extractAbout(attrs);
                TermBuilder pendingTerm = new TermBuilder(url);
                m_termBuilders.add(pendingTerm);
                return pendingTerm;
            }

            public Digester getDigester() {
                return m_digester;
            }

            public void setDigester(Digester digester) {
                m_digester = digester;
            }

            private Digester m_digester = null;
        };
    }

    private Domain m_domain = null;

    private final List<TermBuilder> m_termBuilders = new ArrayList<TermBuilder>();

    private static final Logger s_log = Logger.getLogger(DomainBuilder.class);
}
