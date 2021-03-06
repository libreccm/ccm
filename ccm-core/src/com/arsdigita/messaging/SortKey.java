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
package com.arsdigita.messaging;

import org.apache.oro.text.perl.Perl5Util;

/**
 * A utility class used to manipulate sort keys for
 * messages. Sort keys are used to encode the structure of a set of
 * message threads as follows:
 *
 * <pre>
 *    message       sort key
 *    ----------    -----------
 *    msg1          05a
 *       msg2       05a000
 *       msg3       05a001
 *          msg5    05a001000
 *          msg6    05a001001
 *          ...
 *          msg95   05a00101X
 *    msg4          05b
 * </pre>
 *
 * <p>Sort keys are base-62 numbers that are encoded using the
 * alphanumeric characters 0-9, A-Z, and a-z to represent digits 0
 * through 61. This maps to the following character range:
 *
 * <pre>
 *  00-09    '0'-'9'
 *  10-35    'A'-'Z'
 *  36-61    'a'-'z'
 * </pre>
 *
 * <p>A tree structure like the one pictured above is respresented
 * with 3 digits for each level (238328 keys) and an unlimited number
 * of levels. Child keys are generated by extending the parent's key 3
 * digits to the right. In the example above you can observe this by
 * comparing the sort keys for msg2 and msg1, for example.
 *
 * <p>There are several advantages to this:
 *
 * <ol>
 *   <li>The parents and children of any key are easy to compute.
 *   <li>All keys sort lexagraphically to produce the correct
 *       structure of the tree.
 *   <li>Computing the next key in the sequence is easy.
 *   <li>It is possible to represent very large keys with a small
 *       number of bytes.
 * </ol>
 *
 * @author Ron Henderson 
 * @author Stefan Deusch 
 * @version $Id: SortKey.java 287 2005-02-22 00:29:02Z sskracic $ */

public class SortKey {

    /**
     * A character array for storing the key.
     */

    char[] m_sortkey;

    /**
     * Number of "digits" per level.  This corresponds to a limit of
     * 3^62 = 238328 individual keys per level.
     */

    private static final int CHARS_PER_LEVEL = 3;

    /**
     * Our 3-digit "zero" key, used to extend a parent key to generate
     * the first child.
     */

    private static final String ZERO = "000";

    /**
     * A valid sort key consists of alphanumeric characters from 0-9,
     * A-Z, and a-z. The following regexp is used to verify that the
     * sort key only consists of characters in this set.
     */

    private static final String INVALID_SORTKEY_PATTERN =
        "/[^a-zA-Z0-9]+/";

    /**
     * Creates a new sort key and initializes it to the zero value.
     */

    public SortKey() {
        m_sortkey = ZERO.toCharArray();
    }

    /**
     * Takes the given string and converts it into a
     * sort key.
     */

    public SortKey (String sortkey) {
        m_sortkey = sortkey.toCharArray();
        validate();
    }

    /**
     * Takes a parent sort key and returns its first
     * child.
     */

    public SortKey (SortKey parent) {
        this(parent.getChild().toString());
    }

    /**
     * Gets the first child of this sort key.  This extends the
     * current sort key's value by 3 digits to the right.
     * @return the first child of this sort key.
     */

    public SortKey getChild() {
        return new SortKey(toString() + ZERO);
    }

    /**
     * Gets the parent of this sort key.
     * @return the parent of this sort key.
     */

    public SortKey getParent() {
        return new SortKey
            (new String(m_sortkey,
                        0,
                        m_sortkey.length-CHARS_PER_LEVEL));
    }


    /**
     * Returns a string representation of this sort key.
     * @return a string representation of this sort key.
     */

    public String toString() {
        return new String(m_sortkey);
    }

    /**
     * Gets the depth of the current sort key in the tree.  Note that
     * depth is always greater than or equal to one.
     * @return the depth of the current sort key in the tree.
     */

    public int getDepth() {
        return m_sortkey.length / CHARS_PER_LEVEL;
    }

    /**
     * Gets the length of the sort key, in other words the number of
     * digits it contains.
     * @return the length of the sort key
     */

    public int length() {
        return m_sortkey.length;
    }

    /**
     * Increments the value of the sort key to the next element of the
     * sequence.
     */

    public void next() {
        incr(m_sortkey.length-1);
    }

    /**
     * Increments one character position.  Calls itself recursively to
     * handle overflow from one "digit" to the next.  With base-62
     * numbers overflow is a rare occurence, so a recursive method
     * should be relatively efficient.
     */

    private void incr(int pos) {
        char c = m_sortkey[pos];
        m_sortkey[pos] = nextChar(c);
        if (c == 'z') {
            incr(pos-1);
        }
    }

    /**
     * Returns the next "digit" in our base-62 numbering system,
     * skipping over special characters like ';'.  This is written to
     * wrap around from z to 0.
     *
     * @param c the base character to increment from
     * @return the next digit.
     */

    private char nextChar(char c) {

        char n;

        switch (c) {
        case '9':
            n = 'A';
            break;
        case 'Z':
            n = 'a';
            break;
        case 'z':
            n = '0';
            break;
        default:
            n = (char) (c + 1);
            break;
        }

        return n;
    }

    /**
     * Returns the value of the sort key as an integer.  This will run
     * out of room to fit inside an int when the depth is somewhere
     * between 2 and 3 levels, so it's really only useful for very
     * shallow trees.  Used mainly for debugging.
     *
     * @return the value of the sort key as an integer.
     */

    public int intValue() {
        int value = 0;

        for (int i = 0; i < m_sortkey.length; i++) {
            value = 62 * value + decode(m_sortkey[i]);
        }

        return value;
    }

    /**
     * Decodes a character representation of a digit.
     * @param c the character to decode
     */

    private int decode(char c) {
        if (c <= '9')
            return (int) (c - '0');
        else if (c <= 'Z')
            return (int) (c - 'A') + 10;
        else
            return (int) (c - 'a') + 36;
    }

    /**
     * Checks the validity of the sort key and throws an
     * IllegalArgumentException if the format is not correct.
     */

    private void validate() {
        if (m_sortkey.length % CHARS_PER_LEVEL != 0) {
            throw new IllegalArgumentException
                ("invalid sort key: " +
                 "length is not a multiple of " + CHARS_PER_LEVEL);
        }

        Perl5Util util = new Perl5Util();
        if (util.match(INVALID_SORTKEY_PATTERN, toString())) {
            throw new IllegalArgumentException
                ("invalid sort key: " +
                 "illegal characters: " + toString());
        }
    }
}
