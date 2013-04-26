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

import java.util.Hashtable;
import java.util.Map;

/**
 * <p>A basic implementation of the Cache interface.</p>
 *
 * @author Michael Pih (pihman@arsdigita.com)
 * @version $Revision: #6 $ $DateTime: 2004/08/17 23:15:09 $
 * @version $Id: SimpleCache.java 2090 2010-04-17 08:04:14Z pboy $  
 */
public class SimpleCache extends Hashtable{

    public SimpleCache() {
        super();
    }

    public SimpleCache(int initialCapacity) {
        super(initialCapacity);
    }

    public SimpleCache(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public SimpleCache(Map map) {
        super(map);
    }


    /**
     * Lookup and fetch a cached object.
     *
     * @param key The object key
     * @return The cached object, null if there is none
     */
    @Override
    public Object get(Object key) {
        return super.get(key);
    }

    /**
     * Caches one object.
     *
     * @param key The object key
     * @param value The object to be cached
     * @return The cached object
     */
    @Override
    public Object put(Object key, Object value) {
        return super.put(key, value);
    }

    /**
     * Removes one cached object.
     *
     * @param key The object key
     * @return The formerly-cached object
     */
    @Override
    public Object remove(Object key) {
        return super.remove(key);
    }

    /**
     * Clears the cache.
     */
    public void flush() {
        clear();
    }

}
