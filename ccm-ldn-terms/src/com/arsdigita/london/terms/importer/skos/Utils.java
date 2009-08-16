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

import org.xml.sax.Attributes;

import com.arsdigita.util.StringUtils;

/**
 * Utilities for parsing SKOS files.
 * 
 * @author <a href="https://sourceforge.net/users/terry_permeance/">terry_permeance</a>
 */
class Utils {
    /**
     * Extract the <code>rdf:about</code> attribute from the attributes of an element.
     * 
     * @param attrs the attributes containing the <code>rdf:about</code> attribute.
     * 
     * @return the URL for the <code>rdf:about</code>
     * 
     * @throws MalformedURLException if the <code>rdf:about</code> attribute is malformed
     */
    public static URL extractAbout(Attributes attrs) throws MalformedURLException {
        return new URL(attrs.getValue(Namespaces.RDF, "about"));
    }

    /**
     * Extract the unique ID from the URL.
     * 
     * <p>Examples:</p>
     * <ul>
     * <li><code>http://www.fao.org/aos/agrovoc#c_3</code> &raquo; <b>c_3</b>
     * <li><code>http://iaaa.cps.unizar.es/thesaurus/T5_INFORMATION AND COMMUNICATION</code> &raquo; <b>T5_INFORMATION AND COMMUNICATION</b>
     * <li><code>http://www.eionet.eu.int/gemet/concept/3395</code> &raquo; <b>3395</b>
     * </ul>
     * 
     * @param url the URL identifying the term
     * 
     * @return the unique ID
     */
    public static String extractUniqueID(URL url) {
        String uniqueID = null;
        
        if (!StringUtils.emptyString(url.getRef())) {
            uniqueID = url.getRef();
        } else {
            uniqueID = url.toExternalForm();
            while (uniqueID.endsWith("/")) {
                uniqueID = uniqueID.substring(0, uniqueID.length() - 1);
            }
            int lastSlashIndex = uniqueID.lastIndexOf('/');
            if (lastSlashIndex > 0) {
                uniqueID = uniqueID.substring(lastSlashIndex + 1);
            }
        }
        return uniqueID;
    }
}
