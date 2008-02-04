/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
 *
 */
package com.arsdigita.templating.html;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A parent class for a number of classes that define <a
 * href="http://www.w3.org/TR/REC-html40/sgml/entities.html">character entity
 * references</a> for ISO 8859-1 characters</a>. (See also <a
 * href="http://www.htmlhelp.com/reference/html40/entities/">Entities</a>.)
 *
 * @author Vadim Nasardinov (vadimn@redhat.com)
 * @since 2002-09-04
 * @version $Id: CharacterReferenceEntities.java 287 2005-02-22 00:29:02Z sskracic $
 **/
class CharacterReferenceEntities {
    private final static String LINE_END = System.getProperty("line.separator");

    private Map m_names;
    private List m_orderedNames;

    CharacterReferenceEntities() {
        m_names = new HashMap();
        m_orderedNames = new ArrayList();
    }
    /**
     * Returns the decimal reference for the <code>character</code>.  For
     * instance, <code>getDecimalReference(HTMLlat1.nbsp)</code> returns
     * <code>"&amp;#160;"</code>.
     **/
    public String getDecimalReference(String character) {
        CharacterReferenceEntity refEnt =
            (CharacterReferenceEntity) m_names.get(character);
        return refEnt == null ? null : refEnt.getDecimalReference();
    }

    /**
     * Returns the name of the character reference entity for this character.
     * For example, <code>HTMLlat1.getName(HTMLlat1.sect)</code> returns the
     * string <code>"sect"</code>.
     **/
    public String getName(String character) {
        CharacterReferenceEntity refEnt =
            (CharacterReferenceEntity) m_names.get(character);
        return refEnt == null ? null : refEnt.getName();
    }

    /**
     * Returns a string of the form <code>&lt;!ENTITY sect "&amp;#167;"></code>,
     * if called like so:
     *
     * <pre>
     * HTMLlat1.getEntityDeclaration(HTMLlat1.sect);
     * </pre>
     **/
    public String getEntityDeclaration(String character) {
        CharacterReferenceEntity refEnt =
            (CharacterReferenceEntity) m_names.get(character);
        return refEnt == null ? null : refEnt.getEntityDeclaration();
    }

    /**
     * Returns all character entity declarations as a single string of the
     * following form:
     *
     * <pre>
     * &lt;!ENTITY nbsp   "&amp;#160;">
     * &lt;!ENTITY iexcl  "&amp;#161;">
     *   [ ... skipped for brevity ...]
     * &lt;!ENTITY yacute "&amp;#253;">
     * &lt;!ENTITY thorn  "&amp;#254;">
     * &lt;!ENTITY yuml   "&amp;#255;">
     * </pre>
     **/
    public String getAllEntityDeclarations() {
        // FIXME: this should be computed once
        StringBuffer sb = new StringBuffer();
        for (Iterator i=m_orderedNames.iterator(); i.hasNext(); ) {
            sb.append(getEntityDeclaration((String) i.next()));
            sb.append(LINE_END);
        }
        return sb.toString();
    }

    protected String registerEntity(String name,
                                           String decimalReference) {

        CharacterReferenceEntity entity =
            new CharacterReferenceEntity(name, decimalReference);
        m_names.put(entity.toString(), entity);
        m_orderedNames.add(entity.toString());
        return entity.toString();
    }

    private static class CharacterReferenceEntity {
        private String m_character;
        private String m_decimalReference;
        private String m_entityName;
        private String m_entityDeclaration;

        public CharacterReferenceEntity(String name, String decimalReference) {
            m_entityName = name;
            m_decimalReference = decimalReference;
            m_character = "" + (char)
                Integer.parseInt(decimalReference.substring
                                 (2,
                                  decimalReference.length()-1));

            StringBuffer sb = new StringBuffer();
            sb.append("<!ENTITY ").append(getName());
            sb.append(" \"").append(getDecimalReference());
            sb.append("\">");
            m_entityDeclaration = sb.toString();
        }

        public String getEntityDeclaration() {
            return m_entityDeclaration;
        }

        public String toString() {
            return m_character;
        }

        public String getDecimalReference() {
            return m_decimalReference;
        }

        public String getName() {
            return m_entityName;
        }
    }
}
