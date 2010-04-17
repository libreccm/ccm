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
package com.arsdigita.cms.dispatcher;

/**
 * <p>An interface for caching objects.</p>
 *
 * @author Michael Pih (pihman@arsdigita.com)
 * @version $Id: Cache.java 1967 2009-08-29 21:05:51Z pboy $
 */
public interface Cache {

    /**
     * Lookup and fetch a cached object.
     *
     * @param key The object key
     * @return The cached object, null if there is none
     */
    public Object get(Object key);

    /**
     * Caches one object.
     *
     * @param key The object key
     * @param value The object to be cached
     * @return The cached object
     */
    public Object put(Object key, Object value);

    /**
     * Removes one cached object.
     *
     * @param key The object key
     * @return The formerly-cached object
     */
    public Object remove(Object key);

    /**
     * Clears the cache.
     */
    public void flush();

}
