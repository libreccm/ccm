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

import java.io.IOException;
import java.net.URL;
import java.sql.Date;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.Rule;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.arsdigita.util.UncheckedWrapperException;

/**
 * Parses an SKOS-formatted file using a {@link Digester}.
 * 
 * @author <a href="https://sourceforge.net/users/terry_permeance/">terry_permeance</a>
 */
class DomainParser {
    public DomainParser(final String key, final String title, final String description, final String version,
            final Date released) {
        m_digester = new Digester();

        m_digester.push(this);
        m_digester.setNamespaceAware(true);
        m_digester.setRuleNamespaceURI(Namespaces.SKOS);

        m_digester.addRule("RDF/ConceptScheme", new Rule() {
            public void begin(String namespace, String name, Attributes attrs) throws Exception {
                URL url = Utils.extractAbout(attrs);
                m_builder.createDomain(key, url, title, description, version, released);
            }
        });

        m_digester.addFactoryCreate("RDF/Concept", m_builder.newPendingTermFactory());

        m_digester.addCallMethod("RDF/Concept/prefLabel", "setPreferredLabel", 1, new Class[] { String.class });
        m_digester.addCallParam("RDF/Concept/prefLabel", 0);

        m_digester.addCallMethod("RDF/Concept/altLabel", "addAlternateLabel", 1, new Class[] { String.class });
        m_digester.addCallParam("RDF/Concept/altLabel", 0);

        m_digester.addCallMethod("RDF/Concept/broader", "addBroaderTerm", 1, new Class[] { String.class });
        m_digester.addCallParam("RDF/Concept/broader", 0, "rdf:resource");

        m_digester.addCallMethod("RDF/Concept/broader/Concept", "addBroaderTerm", 1, new Class[] { String.class });
        m_digester.addCallParam("RDF/Concept/broader/Concept", 0, "rdf:about");

        m_digester.addCallMethod("RDF/Concept/related", "addRelatedTerm", 1, new Class[] { String.class });
        m_digester.addCallParam("RDF/Concept/related", 0, "rdf:resource");

        m_digester.addCallMethod("RDF/Concept/related/Concept", "addRelatedTerm", 1, new Class[] { String.class });
        m_digester.addCallParam("RDF/Concept/related/Concept", 0, "rdf:about");

        m_digester.addCallMethod("RDF/Concept/narrower", "addNarrowerTerm", 1, new Class[] { String.class });
        m_digester.addCallParam("RDF/Concept/narrower", 0, "rdf:resource");

        m_digester.addCallMethod("RDF/Concept/narrower/Concept", "addNarrowerTerm", 1, new Class[] { String.class });
        m_digester.addCallParam("RDF/Concept/narrower/Concept", 0, "rdf:about");
    }

    public void parse(String file) {
        try {
            m_digester.parse(file);
            m_builder.build();
        } catch (IOException ex) {
            throw new UncheckedWrapperException("cannot parse " + file, ex);
        } catch (SAXException ex) {
            throw new UncheckedWrapperException("cannot parse " + file, ex);
        }
    }

    private final DomainBuilder m_builder = new DomainBuilder();

    private final Digester m_digester;
}
