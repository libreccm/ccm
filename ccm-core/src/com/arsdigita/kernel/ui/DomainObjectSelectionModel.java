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
package com.arsdigita.kernel.ui;

import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.bebop.PageState;

/**
 * A {@link SingleSelectionModel} which loads an item from the database.
 *
 * The isSelected() method will return true if it was possible to load the item.
 *
 * @version $Id: DomainObjectSelectionModel.java 287 2005-02-22 00:29:02Z sskracic $
 */
public interface DomainObjectSelectionModel extends SingleSelectionModel {

    /**
     * Return the item which was selected and loaded from the database,
     * using the values supplied in PageState.
     *
     * @param state The page state
     * @return The item domain object, or null if no item is
     *         selected.
     */
    DomainObject getSelectedObject(PageState state);

    /**
     * Select the given domain object. Should extract the selected
     * key from the object in some manner, and call setSelectedKey
     * to change the key.
     *
     * @param state The page state
     * @param object The domain object to set
     */
    void setSelectedObject(PageState state, DomainObject object);

}
