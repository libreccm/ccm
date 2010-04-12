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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.arsdigita.london.terms.Domain;
import com.arsdigita.london.terms.Term;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.Assert;

/**
 * Builds a polyhierarchy of {@link Term} objects.
 * 
 * @author <a href="https://sourceforge.net/users/terry_permeance/">terry_permeance</a>
 */
class TermBuilder {
    private static final Logger s_log = Logger.getLogger(TermBuilder.class);

    public TermBuilder(URL url) {
        Assert.exists(url);
        m_url = url;
        m_uniqueID = Utils.extractUniqueID(url);
        m_preferredLabel = m_uniqueID;

        if (s_log.isDebugEnabled()) {
            s_log.debug("Pending term is " + m_url + " with unique ID " + m_uniqueID);
        }
    }

    public void setPreferredLabel(String preferredLabel) {
        m_preferredLabel = preferredLabel;
        if (s_log.isDebugEnabled()) {
            s_log.debug("--> Preferred label is " + m_preferredLabel);
        }
    }

    public void addAlternateLabel(String alternateLabel) {
        m_alternateLabels.add(alternateLabel);
        if (s_log.isDebugEnabled()) {
            s_log.debug("--> An alternate label is " + alternateLabel);
        }
    }

    public void addBroaderTerm(String url) throws MalformedURLException {
        m_broaderTerms.add(new URL(url));
        if (s_log.isDebugEnabled()) {
            s_log.debug("--> Broader term is " + url);
        }
    }

    public void addRelatedTerm(String url) throws MalformedURLException {
        m_relatedTerms.add(new URL(url));
        if (s_log.isDebugEnabled()) {
            s_log.debug("--> Related term is " + url);
        }
    }

    public void addNarrowerTerm(String url) throws MalformedURLException {
        m_narrowerTerms.add(new URL(url));
        if (s_log.isDebugEnabled()) {
            s_log.debug("--> Narrower term is " + url);
        }
    }

    public void buildTerm(Domain domain, Map<String, Term> termCache) {
        Assert.exists(domain);
        Assert.exists(termCache);
        Assert.exists(m_preferredLabel);
        Assert.exists(m_uniqueID);
        
        if (m_preferredLabel.equals(m_uniqueID)) {
            s_log.warn("Preferred label was missing for concept " + m_uniqueID);
        }

        final boolean inAtoZ = false;
        final String name = m_preferredLabel;
        final String shortcut = null;

        Term term = (Term) termCache.get(m_uniqueID);

        if (term != null) {
            term.setName(name);
            term.setInAtoZ(inAtoZ);
            term.setShortcut(shortcut);
        } else {
            term = Term.create(m_uniqueID, name, inAtoZ, shortcut, domain);
            termCache.put(term.getUniqueID(), term);
        }
        term.save();
    }

    public void buildPolyhierarchy(Domain domain, Map<String, Term> termCache) {
        Term thisTerm = (Term) termCache.get(m_uniqueID);

        if (m_broaderTerms.isEmpty()) {
            domain.addRootTerm(thisTerm);
        }
        
        for (Iterator<URL> i = m_broaderTerms.iterator(); i.hasNext();) {
            URL url = i.next();
            String uniqueID = Utils.extractUniqueID(url);
            Term targetTerm = (Term) termCache.get(uniqueID);
            if (targetTerm == null) {
                s_log.warn("Narrower term " + uniqueID + " (" + url + ") does not exist");
            } else {
                targetTerm.addNarrowerTerm(thisTerm, true, true);
            }
        }
        
        for (Iterator<URL> i = m_relatedTerms.iterator(); i.hasNext();) {
            URL url = i.next();
            String uniqueID = Utils.extractUniqueID(url);
            Term targetTerm = (Term) termCache.get(uniqueID);
            if (targetTerm == null) {
                s_log.warn("Related term " + uniqueID + " (" + url + ") does not exist");
            } else {
                thisTerm.addRelatedTerm(targetTerm);
            }
        }
        
        for (Iterator<String> i = m_alternateLabels.iterator(); i.hasNext();) {
            String alternateLabel = i.next();
            String uniqueID = String.valueOf(s_next_synonym_id--);
            Term targetTerm = Term.create(uniqueID, alternateLabel, false, null, domain);
            targetTerm.addPreferredTerm(thisTerm);
        }
        
        SessionManager.getSession().flushAll();
    }

    public URL getURL() {
        return m_url;
    }

    private final URL m_url;

    private final String m_uniqueID;

    private String m_preferredLabel;

    private final List<String> m_alternateLabels = new ArrayList<String>();

    private final List<URL> m_broaderTerms = new ArrayList<URL>();

    private final List<URL> m_relatedTerms = new ArrayList<URL>();

    private final List<URL> m_narrowerTerms = new ArrayList<URL>();

    private static int s_next_synonym_id = Integer.MAX_VALUE;
}
