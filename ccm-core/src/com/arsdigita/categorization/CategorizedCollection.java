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
package com.arsdigita.categorization;

import com.arsdigita.kernel.ACSObjectCollection;
import com.arsdigita.persistence.DataCollection;

/**
 * Represents a collection of categorized objects.
 *
 * <p>This collection can be iterated over by calling the {@link #next() next()}
 * method and retrieving the current object via {@link #getDomainObject()
 * getDomainObject()}.</p>
 *
 * <p>This class inherits a number of methods from {@link
 * com.arsdigita.domain.DomainQuery} that allow filtering and ordering of this
 * collection.  In order to apply filters or order clauses, you have to know the
 * object model of the {@link DataCollection data collection} backing up this
 * class. Categorized collections are produced by {@link
 * Category#getObjects(String)} and {@link Category#getObjects(String, String)}.
 * See the Javadoc for these methods to learn about high-level guarantees of the
 * structure of the possible query paths that can be used for ordering and/or
 * filtering. </p>
 *
 * @see Category#getObjects(String)
 * @see Category#getObjects(String, String)
 *
 * @author  Vadim Nasardinov (vadimn@redhat.com)
 * @since   2003-09-17
 * @version $Revision: #7 $ $DateTime: 2004/08/16 18:10:38 $
 **/
public final class CategorizedCollection extends ACSObjectCollection {

    private String m_sortPath = null;

    CategorizedCollection(DataCollection dataCollection) {
        this(dataCollection, null);
    }

    CategorizedCollection(DataCollection dataCollection, String sortPath) {
        super(dataCollection);
        m_sortPath = sortPath;
    }

    /**
     * Specifies the sort order the categorized collection.
     *
     * <p>A categorization collection usually represents a list of child objects
     * of a particular category.  These objects can be sorted using the {@link
     * Category#swapWithNext(ACSObject)} and {@link
     * Category#swapWithPrevious(ACSObject)} methods.  This method to make use
     * of this sort order.</p>
     *
     * @see Category#SORT_KEY
     * @see Category#swapWithNext(ACSObject)
     * @see Category#swapWithPrevious(ACSObject)
     * @see Category#alphabetizeChildCategories()
     **/
    public final void sort(boolean ascending) {
        if (m_sortPath != null) {
            if ( ascending ) {
                addOrder(m_sortPath + " asc");
            } else {
                addOrder(m_sortPath + " desc");
            }
        }
    }
}
