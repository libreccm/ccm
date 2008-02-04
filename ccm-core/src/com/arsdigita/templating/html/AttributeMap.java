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

import java.util.Iterator;

/**
 * Similar to the <a
 * href="http://java.sun.com/xml/jaxp/dist/1.1/docs/api/org/xml/sax/Attributes.html"><i>Attributes</i></a>
 * interface in SAX. Not really a {@link java.util.Map}, insofar as it only
 * supports a subset of the <code>Map</code> methods.
 *
 * <p>One of the reasons this is an interface rather than a class is because we
 * don't want <code>AttributeMap</code> to have any setter methods. </p>
 *
 * @author  Vadim Nasardinov (vadimn@redhat.com)
 * @since   2002-08-29
 * @version $Id: AttributeMap.java 287 2005-02-22 00:29:02Z sskracic $
 **/
public interface AttributeMap {

    /**
     * Returns <code>true</code> if the attribute map contains an attribute
     * named <code>qName</code>.
     **/
    boolean contains(String qName);

    /**
     * Returns the value of the attribute named <code>qName</code>.
     **/
    String getValue(String qName);

    /**
     * Returns the number of attributes.
     **/
    int size();

    /**
     * Returns an iterator over the attribute names contained in this map.
     **/
     Iterator keys();

    /**
     * Returns an iterator over name-value {@link AttributeMap.Pair pairs}
     * contained in this map.
     **/
    Iterator pairs();

    /**
     * Represents a single attribute as a name-value pair. For example,
     * <code>&lt;p id="foo" class="rh"></code> has two pairs: ("id", "foo") and
     * ("class", "rh").
     **/
    public interface Pair {

        /**
         * Returns the attribute name.
         **/
        String getName();

        /**
         * Returns the attribute value.
         **/
        String getValue();
    }
}
