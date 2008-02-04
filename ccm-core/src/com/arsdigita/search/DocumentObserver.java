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
package com.arsdigita.search;

import com.arsdigita.domain.DomainObject;

/**
 * This interface is implemented by a search indexer
 * to recieve notification of updates to documents that
 * are searchable. It is invoked during the beforeCommit
 * event of a transaction.
 *
 * This interface is not intended to be used by applications.
 * A search indexer will register an implementation of this
 * class using the SearchConfig#registerIndexer method.
 */
public interface DocumentObserver {
    
    /**
     * Invoked after a searchable object has been 
     * created or updated.
     *
     * @param dobj the updated object
     */
    void onSave(DomainObject dobj);

    /**
     * Invoked after a searchable object has been 
     * deleted. NB, the only guarenteed valid method
     * that can be invoked on the DomainObject is
     * getOID().
     *
     * @param dobj the deleted object
     */
    void onDelete(DomainObject dobj);
}
