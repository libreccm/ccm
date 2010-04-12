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

import kea.vocab.Vocabulary;

import com.arsdigita.caching.CacheTable;
import com.arsdigita.london.terms.Domain;

/**
 * @author <a href="https://sourceforge.net/users/terry_permeance/">terry_permeance</a>
 */
class VocabularyCache {

    public static Vocabulary getVocabulary(Domain domain, String language) throws IOException {
        String key = domain.getKey() + "_" + language;
        Vocabulary vocabulary = (Vocabulary) s_cache.get(key);
        if (vocabulary == null) {
            VocabularyBuilder builder = new VocabularyBuilder(domain, language);
            vocabulary = builder.build();
            s_cache.put(key, vocabulary);
        }
        return vocabulary;
    }

    public static void reset() {
        s_cache.removeAll();
    }

    private static final CacheTable s_cache = new CacheTable("VocabularyCache", false);
}
