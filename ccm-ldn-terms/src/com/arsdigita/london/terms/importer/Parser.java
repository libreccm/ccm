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

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.digester.Digester;
import org.xml.sax.SAXException;

import com.arsdigita.util.UncheckedWrapperException;

public class Parser {

    private Digester m_digester;
    
    public Parser() {
        m_digester = new Digester();

        m_digester.push(this);
        //m_digester.setValidating(true);
        m_digester.setNamespaceAware(true);
        m_digester.setRuleNamespaceURI("http://xmlns.redhat.com/london/terms/1.0");

        m_digester.addObjectCreate("domain",
                                   DomainBuilder.class);
        m_digester.addCallMethod("domain",
                                 "createDomain",
                                 6, new Class[] {
                                     String.class, // key
                                     String.class, // url
                                     String.class, // title
                                     String.class, // description
                                     String.class, // version
                                     String.class, // released
                                 });
        m_digester.addCallParam("domain",
                                0,
                                "key");
        m_digester.addCallParam("domain",
                                1,
                                "about");
        m_digester.addCallParam("domain",
                                2,
                                "title");
        m_digester.addCallParam("domain/description",
                                3);
        m_digester.addCallParam("domain",
                                4,
                                "version");
        m_digester.addCallParam("domain",
                                5,
                                "released");


        m_digester.addCallMethod("domain/term",
                                 "addTerm", 
                                 4, new Class[] {
                                     Integer.class, // id
                                     String.class, // name
                                     Boolean.class, // inAtoZ
                                     String.class // shortcut
                                 });
        m_digester.addCallParam("domain/term",
                                0,
                                "id");
        m_digester.addCallParam("domain/term",
                                1,
                                "name");
        m_digester.addCallParam("domain/term",
                                2,
                                "inAtoZ");
        m_digester.addCallParam("domain/term",
                                3,
                                "shortcut");
        

        
        m_digester.addObjectCreate("hierarchy",
                                   HierarchyBuilder.class);
        m_digester.addCallMethod("hierarchy/domain",
                                 "findDomain",
                                 1, new Class[] { 
                                     String.class // url
                                 });
        
        m_digester.addCallParam("hierarchy/domain",
                                0,
                                "resource");
        
        m_digester.addCallMethod("hierarchy/orderedPair",
                                 "addNarrowerTerm",
                                 4, new Class[] {
                                     Integer.class, // id
                                     Integer.class, // narrower
                                     Boolean.class, // default
                                     Boolean.class // preferred
                                 });
        m_digester.addCallParam("hierarchy/orderedPair/source/term",
                                0,
                                "id");
        m_digester.addCallParam("hierarchy/orderedPair/destination/term",
                                1,
                                "id");
        m_digester.addCallParam("hierarchy/orderedPair/destination",
                                2,
                                "isDefault");
        m_digester.addCallParam("hierarchy/orderedPair/destination",
                                3,
                                "isPreferred");


        m_digester.addCallMethod("hierarchy/term",
                                 "addRootTerm",
                                 1, new Class[] {
                                     Integer.class  // id
                                 });
        m_digester.addCallParam("hierarchy/term",
                                0,
                                "id");



        m_digester.addObjectCreate("mixedHierarchy",
                                   MixedHierarchyBuilder.class);
        m_digester.addCallMethod("mixedHierarchy/source/domain",
                                 "findSourceDomain",
                                 1, new Class[] { 
                                     String.class // url
                                 });        
        m_digester.addCallParam("mixedHierarchy/source/domain",
                                0,
                                "resource");
        
        m_digester.addCallMethod("mixedHierarchy/destination/domain",
                                 "findDestinationDomain",
                                 1, new Class[] { 
                                     String.class // url
                                 });        
        m_digester.addCallParam("mixedHierarchy/destination/domain",
                                0,
                                "resource");
        
        m_digester.addCallMethod("mixedHierarchy/orderedPair",
                                 "addNarrowerTerm",
                                 4, new Class[] {
                                     Integer.class, // id
                                     Integer.class, // narrower
                                     Boolean.class, // default
                                     Boolean.class // preferred
                                 });
        m_digester.addCallParam("mixedHierarchy/orderedPair/source/term",
                                0,
                                "id");
        m_digester.addCallParam("mixedHierarchy/orderedPair/destination/term",
                                1,
                                "id");
        m_digester.addCallParam("mixedHierarchy/orderedPair/destination",
                                2,
                                "isDefault");
        m_digester.addCallParam("mixedHierarchy/orderedPair/destination",
                                3,
                                "isPreferred");



        m_digester.addObjectCreate("related",
                                   RelatedBuilder.class);
        m_digester.addCallMethod("related/domain",
                                 "findSourceDomain",
                                 1, new Class[] { 
                                     String.class // url
                                 });
        
        m_digester.addCallParam("related/domain",
                                0,
                                "resource");
        
        m_digester.addCallMethod("related/unorderedPair",
                                 "addRelatedTerm",
                                 2, new Class[] {
                                     Integer.class, // source
                                     Integer.class, // destination
                                 });
        m_digester.addCallParam("related/unorderedPair/first/term",
                                0,
                                "id");
        m_digester.addCallParam("related/unorderedPair/second/term",
                                1,
                                "id");


        m_digester.addObjectCreate("mapping",
                                   RelatedBuilder.class);
        m_digester.addCallMethod("mapping/source/domain",
                                 "findSourceDomain",
                                 1, new Class[] { 
                                     String.class // url
                                 });        
        m_digester.addCallParam("mapping/source/domain",
                                0,
                                "resource");
        
        m_digester.addCallMethod("mapping/destination/domain",
                                 "findDestinationDomain",
                                 1, new Class[] { 
                                     String.class // url
                                 });        
        m_digester.addCallParam("mapping/destination/domain",
                                0,
                                "resource");
        
        m_digester.addCallMethod("mapping/orderedPair",
                                 "addRelatedTerm",
                                 2, new Class[] {
                                     Integer.class, // source
                                     Integer.class, // destination
                                 });
        m_digester.addCallParam("mapping/orderedPair/source/term",
                                0,
                                "id");
        m_digester.addCallParam("mapping/orderedPair/destination/term",
                                1,
                                "id");

        
        m_digester.addObjectCreate("itemMapping",                                   
                                   TermItemBuilder.class);

        m_digester.addCallMethod("itemMapping/domain",
                                 "findDomain",
                                 1, new Class[] { 
                                     String.class // url
                                 });
        
        m_digester.addCallParam("itemMapping/domain",
                                0,
                                "resource");

        m_digester.addCallMethod("itemMapping/mapping",
                                 "addMapping",
                                 2, new Class[] {
                                     Integer.class, // term id
                                     String.class  // item path
                                 });
        m_digester.addCallParam("itemMapping/mapping/term",
                                0,
                                "id");
        m_digester.addCallParam("itemMapping/mapping/item",
                                1,
                                "path");

        m_digester.addObjectCreate("synonyms", SynonymsBuilder.class);
        m_digester.addCallMethod("synonyms/domain", "findDomain", 1,
                new Class[] { String.class // url
                });

        m_digester.addCallParam("synonyms/domain", 0, "resource");

        m_digester.addCallMethod("synonyms/orderedPair", "addSynonymTerm", 4,
                new Class[] { Integer.class, // id
                        Integer.class, // synonym
                });
        m_digester.addCallParam("synonyms/orderedPair/source/term", 0, "id");
        m_digester.addCallParam("synonyms/orderedPair/destination/term", 1, "id");

    }
    
    public void parse(String file) {
        try {
            m_digester.parse(file);
        } catch (IOException ex) {
            throw new UncheckedWrapperException("cannot parse " + file,
                                                ex);
        } catch (SAXException ex) {
            throw new UncheckedWrapperException("cannot parse " + file,
                                                ex);
        }
    }
    
    public void parse(InputStream is) {
        try {
            m_digester.parse(is);
        } catch (IOException ex) {
            throw new UncheckedWrapperException("cannot parse " + is,
                                                ex);
        } catch (SAXException ex) {
            throw new UncheckedWrapperException("cannot parse " + is,
                                                ex);
        }
    }
    
}
