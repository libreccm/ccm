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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="https://sourceforge.net/users/terry_permeance/">terry_permeance</a>
 */
class Stopwords extends kea.stopwords.Stopwords {

    public Stopwords(String language) throws IOException {
        String resource = getClass().getPackage().getName().replace('.', '/') + "/stopwords_" + language + ".txt";
        URL url = getClass().getClassLoader().getResource(resource);
        if (url == null) {
            throw new IOException("Could not find resource " + resource);
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
        String stopWord = null;
        m_stopWords = new HashSet<String>();
        try {
            while ((stopWord = br.readLine()) != null) {
                m_stopWords.add(stopWord);
            }
        } finally {
            br.close();
        }
    }

    public boolean isStopword(String str) {
        return m_stopWords.contains(str);
    }

    private final Set<String> m_stopWords;
}
