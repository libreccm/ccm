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
package com.arsdigita.domain;

import com.arsdigita.persistence.PersistenceException;
import com.arsdigita.persistence.DataObject;

/**
 * Interface for DomainObject observers. Implementations of
 * DomainObjectObserver should implement the <code>equals</code> method
 * meaningfully so that multiple copies of the same observer are not added by
 * multiple domain objects that wrap the same data object.
 *
 * @author Joseph Bank
 * @version $Id: DomainObjectObserver.java 2089 2010-04-17 07:55:43Z pboy $
 */
public interface DomainObjectObserver {

    /**
     * Callback for a set operation on the observed DomainObject
     *
     * @param dobj the observed domain object
     * @param name the name of the parameter being set
     * @param old_value the old value of the parameter being set
     * @param new_value the old value of the parameter being set
     */
    public void set(DomainObject dobj,
                    String name,
                    Object old_value,
                    Object new_value);

    /**
     * Callback for an add operation on the observed DomainObject
     *
     * @param dobj the observed domain object
     * @param name the name of the parameter being set
     * @param dobj the object added
     */
    public void add(DomainObject dobj,
                    String name, DataObject dataObject);
    /**
     * Callback for a remove operation on the observed DomainObject
     *
     * @param dobj the observed domain object
     * @param name the name of the parameter being set
     * @param dobj the object removed
     */
    public void remove(DomainObject dobj,
                       String name, DataObject dataObject);
    /**
     * Callback for a clear operation on the observed DomainObject
     *
     * @param dobj the observed domain object
     * @param name the name of the parameter being set
     */
    public void clear(DomainObject dobj, String name);

    /**
     * Callback before a save operation on the observed DomainObject
     *
     * @param dobj the observed domain object
     */
    public void beforeSave(DomainObject dobj) throws PersistenceException;

    /**
     * Callback after a save operation on the observed DomainObject
     *
     * @param dobj the observed domain object
     */
    public void afterSave(DomainObject dobj) throws PersistenceException;

    /**
     * Callback before a delete operation on the observed DomainObject
     *
     * @param dobj the observed domain object
     */
    public void beforeDelete(DomainObject dobj) throws PersistenceException;

    /**
     * Callback after a delete operation on the observed DomainObject
     *
     * @param dobj the observed domain object
     */
    public void afterDelete(DomainObject dobj) throws PersistenceException;

}
