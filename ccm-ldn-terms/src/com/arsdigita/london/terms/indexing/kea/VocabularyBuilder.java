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

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import kea.stemmers.PorterStemmer;
import kea.vocab.Vocabulary;

import org.apache.log4j.Logger;

import com.arsdigita.london.terms.Domain;

/**
 * @author <a href="https://sourceforge.net/users/terry_permeance/">terry_permeance</a>
 */
class VocabularyBuilder {

    private static final Logger s_log = Logger.getLogger(VocabularyBuilder.class);

    private final Domain m_domain;

    private final String m_language;

    public VocabularyBuilder(Domain domain, String language) {
        m_domain = domain;
        m_language = language;
    }

    public Vocabulary build() throws IOException {
        s_log.info("Building vocabulary for domain " + m_domain.getKey() + "...");

        Vocabulary vocabulary = new Vocabulary(m_domain.getKey(), "aplaws", m_language);
        vocabulary.setStemmer(new PorterStemmer());
        vocabulary.setStopwords(new Stopwords(m_language));

        Map vocabularyEN = createMap(vocabulary, "VocabularyEN");
        Map vocabularyENrev = createMap(vocabulary, "VocabularyENrev");
        Map vocabularyREL = createMap(vocabulary, "VocabularyREL");
        Map vocabularyRT = createMap(vocabulary, "VocabularyRT");
        createMap(vocabulary, "VocabularyUSE");

        Queries.PreferredTerms preferredTerms = new Queries.PreferredTerms(m_domain);
        try {
            while (preferredTerms.next()) {
                String id = preferredTerms.getUniqueID();
                String descriptor = preferredTerms.getName();
                String avterm = vocabulary.pseudoPhrase(descriptor);
                if (avterm == null) {
                    avterm = descriptor;
                }
                if (avterm.length() > 1) {
                    vocabularyEN.put(avterm, id);
                    vocabularyENrev.put(id, descriptor);
                }
            }
            if (s_log.isDebugEnabled()) {
                s_log.debug("    --> Built " + vocabularyEN.size() + " preferred terms");
            }
        } finally {
            preferredTerms.close();
        }

        Queries.NonPreferredTerms nonPreferredTerms = new Queries.NonPreferredTerms(m_domain);
        int count = 1;
        try {
            while (nonPreferredTerms.next()) {
                String preferred_id = nonPreferredTerms.getPreferredUniqueID();
                String descriptor = nonPreferredTerms.getName();
                addNonDescriptor(vocabulary, count++, preferred_id, descriptor);
            }
            if (s_log.isDebugEnabled()) {
                s_log.debug("    --> Built " + count + " non-preferred terms");
            }
        } finally {
            preferredTerms.close();
        }

        Queries.RelatedTerms relatedTerms = new Queries.RelatedTerms(m_domain);
        try {
            while (relatedTerms.next()) {
                String id = relatedTerms.getUniqueID();
                String relationType = relatedTerms.getRelationType();
                String id_related = relatedTerms.getRelatedUniqueID();

                Vector relatedIds = (Vector) vocabularyREL.get(id);
                if (relatedIds == null) {
                    relatedIds = new Vector();
                    vocabularyREL.put(id, relatedIds);
                }
                relatedIds.add(id_related);

                if ("child".equals(relationType)) {
                    vocabularyRT.put(id + "-" + id_related, "narrower");
                    vocabularyRT.put(id_related + "-" + id, "broader");
                } else {
                    vocabularyRT.put(id + "-" + id_related, "related");
                    vocabularyRT.put(id_related + "-" + id, "related");
                }
            }
            if (s_log.isDebugEnabled()) {
                s_log.debug("    --> Built " + vocabularyRT.size() + " relationships");
            }
        } finally {
            preferredTerms.close();
        }
        s_log.info("Built vocabulary for domain " + m_domain.getKey());
        return vocabulary;
    }

    private Map createMap(Vocabulary vocabulary, String fieldName) {
        try {
            Map<String, String> map = new HashMap<String, String>(106033);
            Field field = vocabulary.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(vocabulary, map);
            return map;
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (SecurityException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void addNonDescriptor(Vocabulary vocabulary, int count, String id_descriptor, String non_descriptor) {

        try {
            Method addNonDescriptor = vocabulary.getClass().getDeclaredMethod("addNonDescriptor",
                    new Class[] { Integer.TYPE, String.class, String.class });
            addNonDescriptor.setAccessible(true);
            addNonDescriptor.invoke(vocabulary, new Object[] { Integer.valueOf(count), id_descriptor, non_descriptor });
        } catch (SecurityException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
