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
 *
 */
package com.arsdigita.util;

/**
 * 
 * A general utility class for text which carries additional type
 * information.  Specifically, we recognize plain text, HTML, and
 * preformatted text.
 *
 * @author Kevin Scaldeferri 
 * @version $Id: TypedText.java 287 2005-02-22 00:29:02Z sskracic $
 */

public class TypedText {

    public static final String TEXT_HTML = "text/html";
    public static final String TEXT_PLAIN = "text/plain";
    public static final String TEXT_PREFORMATTED =
        TEXT_PLAIN + "; format=preformatted";


    private String m_text;
    private String m_type;

    public TypedText(String text, String type) {
        m_text = text;
        m_type = type;
    }


    public String getText() {
        return m_text;
    }

    public void setText(String text) {
        m_text = text;
    }

    public String getType() {
        return m_type;
    }

    public void setType(String type) {
        m_type = type;
    }

    /**
     * Generates a version of the text renderable as HTML based on
     * the type.
     */
    public String getHTMLText() {
        if (m_text == null) {
            return "";
        }

        // Should probably change this to a state pattern
        if (m_type.equals(TEXT_HTML)) {
            return m_text;
        } else if (m_type.equals(TEXT_PREFORMATTED)) {
            return "<pre>" + m_text + "</pre>";
        } else if (m_type.equals(TEXT_PLAIN)) {
            return StringUtils.textToHtml(m_text);
        } else {
            // catch-all... this is where the state pattern would be nice
            return StringUtils.textToHtml(m_text);
        }
    }

    /**
     * Returns true if the text and type are both equal
     */
    public boolean equals(Object o) {
        if (o instanceof TypedText) {
            TypedText t = (TypedText) o;
            return getText().equals(t.getText())
                && getType().equals(t.getType());
        } else {
            return false;
        }
    }

    public int hashCode() {
        int result = 17;
        // getHTMLText implies this can be null. Of course, m_type isn't guaranteed to be non-null either,
        // and both are used in equals(). Class invariants need to be better thought out, but since this class isn't
        // presently, this should be enough for now.
        if (null != m_text) {
            result = 37*result + m_text.hashCode();
        }

        result = 37*result + m_type.hashCode();
        return result;
    }

}
