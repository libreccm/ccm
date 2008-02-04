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

import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.kernel.ACSObjectCollection;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;

/**
 * <p>Represents a collection of root categories.  See {@link
 * com.arsdigita.categorization} for details.</p>
 *
 * @see com.arsdigita.categorization
 *
 * @author  Vadim Nasardinov (vadimn@redhat.com)
 * @since   2003-09-26
 * @version $Revision: #9 $ $DateTime: 2004/08/16 18:10:38 $
 **/
public class RootCategoryCollection extends ACSObjectCollection {
    private static final String NAME =
        Category.ROOT_CATEGORY + "." + Category.NAME;
    private static final String DESCRIPTION =
        Category.ROOT_CATEGORY + "." + Category.DESCRIPTION;
    private static final String IS_ENABLED =
        Category.ROOT_CATEGORY + "." + Category.IS_ENABLED;

    public RootCategoryCollection(DataCollection dataCollection) {
        super(dataCollection);
        addPath(NAME);
        addPath(DESCRIPTION);
        addPath(IS_ENABLED);
    }

    /**
     * Returns the name of the category.
     *
     * @return the category name.
     * @see Category#getName()
     */
    public String getName() {
        return (String) m_dataCollection.get(NAME);
    }

    /**
     * @return the description
     * @see Category#getDescription()
     */
    public String getDescription() {
        return (String) m_dataCollection.get(DESCRIPTION);
    }

    public String getUseContext() {
        return (String) m_dataCollection.get(Category.USE_CONTEXT);
    }

    /**
     * Determines the current state of the category.
     *
     * @return <code>true</code> if the category is enabled; <code>false</code>
     * otherwise.
     * @see Category#isEnabled()
     */
    public boolean isEnabled() {
        return ((Boolean) m_dataCollection.get(IS_ENABLED)).booleanValue();
    }

    /**
     * Wrapper to <code>getDomainObject()</code> that casts the returned
     * <code>DomainObject</code> as a <code>Category</code>.
     *
     * @return a <code>Category</code> for the current position in the
     * collection.
     **/
    public Category getCategory() {
        DataObject triple = m_dataCollection.getDataObject();
        return (Category) DomainObjectFactory.newInstance
            ((DataObject) triple.get(Category.ROOT_CATEGORY));
    }

    public ACSObject getACSObject() {
        return getCategory();
    }
}
