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
package com.arsdigita.cms.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.toolbox.ui.DataTable;
import com.arsdigita.util.Lockable;

/**
 * This class is used by the {@link DataTable} class in order
 * to construct a {@link DataCollection} during each request
 * 
 * @version $Id: DataCollectionBuilder.java 287 2005-02-22 00:29:02Z sskracic $
 */
public interface DataCollectionBuilder extends Lockable {

    /**
     * Perform all neccessary database operations and return
     * a {@link DataCollection} for the {@link DataTable} to use
     *
     * @param t the parent DataTable
     * @param s the page state
     */
    DataCollection makeDataCollection(DataTable t, PageState s);
}
