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
package com.arsdigita.search.intermedia;

import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectObserver;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.PersistenceException;

import org.apache.log4j.Logger;



/**
 * Using this class is no longer required, it will be removed
 * in a future release. A global observer checks to see if
 * you implement Searchable, or prefereably have registered
 * a MetadataProvider adapter
 * @see com.arsdigita.search.MetadataProvider
 * @deprecated use MetadataProvider instead of Searchable
 **/
public class SearchableObserver implements DomainObjectObserver {

    private static final Logger s_log = Logger.getLogger(SearchableObserver.class);

    /**
     * Constructor.  Makes sure that only objects that implement Searchable
     * use this observer.
     **/
    public SearchableObserver(Searchable searchableObject) {
        super();
    }


    public void set(DomainObject dobj,
                    String name,
                    Object old_value,
                    Object new_value) {
    }
    public void add(DomainObject dobj,
                    String name, DataObject dataObject) { }
    public void remove(DomainObject dobj,
                       String name, DataObject dataObject) { }
    public void clear(DomainObject dobj, String name) { }

    public void beforeSave(DomainObject dobj) throws PersistenceException {
    }
    public void afterSave(DomainObject dobj) throws PersistenceException {
    }
    public void beforeDelete(DomainObject dobj) throws PersistenceException {
    }
    public void afterDelete(DomainObject dobj) throws PersistenceException {
    }

}
