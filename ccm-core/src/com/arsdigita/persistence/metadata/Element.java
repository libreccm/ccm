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
package com.arsdigita.persistence.metadata;

import com.redhat.persistence.metadata.Root;

import org.apache.log4j.Logger;

/**
 * The Element class is the abstract base class for functionality common to
 * all metadata classes.
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #13 $ $Date: 2004/08/16 $
 */

abstract public class Element {

    public final static String versionId = "$Id: Element.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private static final Logger s_log =
        Logger.getLogger(Element.class.getName());


    private Root m_root;
    private Object m_obj;

    Element(Root root, Object obj) {
	m_root = root;
	m_obj = obj;
    }

    /**
     * Returns the filename for this metadata element.
     **/

    public String getFilename() {
        return m_root.getFilename(m_obj);
    }


    /**
     * Returns the line number for this metadata element.
     **/

    public int getLineNumber() {
        return m_root.getLine(m_obj);
    }


    /**
     * Returns the column number for this metadata element.
     **/

    public int getColumnNumber() {
        return m_root.getColumn(m_obj);
    }


    public int hashCode() {
	return m_obj.hashCode();
    }


    public boolean equals(Object other) {
	if (other instanceof Element) {
	    return m_obj.equals(((Element) other).m_obj);
	} else {
	    return false;
	}
    }

}
