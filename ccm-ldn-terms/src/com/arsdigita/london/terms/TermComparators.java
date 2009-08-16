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

package com.arsdigita.london.terms;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Comparators for sorting {@link Term} objects.
 * 
 * @author <a href="https://sourceforge.net/users/terry_permeance/">terry_permeance</a>
 */
public class TermComparators {

    /**
     * Compare two {@link Term} object by name, case insensitive.
     */
    public static class OrderByName implements Comparator<Term>, Serializable {
        public int compare(Term o1, Term o2) {
            int compare = o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
            if (compare == 0) {
                compare = o1.getUniqueID().compareTo(o2.getUniqueID());
            }
            return compare;
        }
    }
}
