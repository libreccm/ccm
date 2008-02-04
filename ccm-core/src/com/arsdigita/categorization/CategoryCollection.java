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
package com.arsdigita.categorization;

import com.arsdigita.kernel.ACSObject;
import com.arsdigita.kernel.ACSObjectCollection;
import com.arsdigita.persistence.DataCollection;

/**
 * Represents a collection of categories.
 *
 * <p>Instances of this class are produced by various methods in {@link
 * Category} and other classes. See, for example, {@link Category#getChildren()}
 * or {@link Category#getDescendants()}.</p>
 *
 * @author Randy Graebner (randyg@alum.mit.edu)
 * @version $Revision: #15 $ $DateTime: 2004/08/16 18:10:38 $
 **/
public class CategoryCollection extends ACSObjectCollection {

    public CategoryCollection(DataCollection dataCollection) {
        super(dataCollection);
    }

    /**
     * Returns the name of the category.
     *
     * @return the category name.
     * @see Category#getName()
     */
    public String getName() {
        return (String) m_dataCollection.get("name");
    }

    /**
     * Returns the description.
     *
     * @return the description
     * @see Category#getDescription()
     */
    public String getDescription() {
        return (String) m_dataCollection.get("description");
    }

    /**
     *  Determines the current state of the category.
     *
     *  @return <code>true</code> if the category is enabled; <code>false</code>
     *  otherwise.
     * @see Category#isEnabled()
     */
    public boolean isEnabled() {
        return ((Boolean) m_dataCollection.get("isEnabled")).booleanValue();
    }

    /**
     * Wrapper to <code>getDomainObject()</code> that casts the returned
     * <code>DomainObject</code> as a <code>Category</code>.
     *
     * @return a <code>Category</code> for the current position in the
     * collection.
     **/
    public Category getCategory() {
        return (Category) getDomainObject();
    }

    public ACSObject getACSObject() {
        return getCategory();
    }

    /**
     * Sorts the category collection by the category sort key.
     *
     * @see CategorizedCollection#sort(boolean)
     **/
    public final void sort(boolean ascending) {
        if ( ascending ) {
            addOrder("link.sortKey asc");
        } else {
            addOrder("link.sortKey desc");
        }
    }
}
