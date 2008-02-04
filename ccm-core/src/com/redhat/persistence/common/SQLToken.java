/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.redhat.persistence.common;

/**
 * SQLToken
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #7 $ $Date: 2004/08/16 $
 **/

public class SQLToken {

    public final static String versionId = "$Id: SQLToken.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    public static class Type {

        private String m_name;

        private Type(String name) {
            m_name = name;
        }

        public String toString() {
            return m_name;
        }

    }

    public static final Type BIND = new Type("BIND");
    public static final Type PATH = new Type("PATH");
    public static final Type RAW = new Type("RAW");
    public static final Type SPACE = new Type("SPACE");

    SQLToken m_previous = null;
    SQLToken m_next = null;
    private String m_image;
    private Type m_type;

    public SQLToken(String image, Type type) {
        m_image = image;
        m_type = type;
    }

    public SQLToken getPrevious() {
        return m_previous;
    }

    public SQLToken getNext() {
        return m_next;
    }

    public String getImage() {
        return m_image;
    }

    public Type getType() {
        return m_type;
    }

    public boolean isBind() {
        return m_type == BIND;
    }

    public boolean isPath() {
        return m_type == PATH;
    }

    public boolean isRaw() {
        return m_type == RAW;
    }

    public boolean isSpace() {
        return m_type == SPACE;
    }

}
