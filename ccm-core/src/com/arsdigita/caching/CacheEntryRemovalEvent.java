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
package com.arsdigita.caching;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.PageState;

/**
 * An event fired when an entry is removed from a CacheTable. Usually nobody cares when 
 * an entry is removed, as a miss when retrieving an entry just signals that the object 
 * should be created. However, if object creation is a time consuming process, then we 
 * may not want the user to be hit by this and so a listener can replace the entry when 
 * it has been invalidated from another node.
 * 
 * @author Chris Gilbert chris.gilbert@westsussex.gov.uk
 *
 * @version $Id: CacheEntryRemovalEvent 285 2005-02-22 00:29:02Z cgyg9330 $
 *
 * @see CacheEntryRemovalListener
 * 
 */

public class CacheEntryRemovalEvent  {

    public static final String versionId = "$Id: CacheEntryRemovalEvent.java 285 2005-02-22 00:29:02Z cgyg9330 $ by $Author: cgyg9330 $, $DateTime: 2004/08/16 18:10:38 $";

	public static final String STALE_ENTRY = "stale";
	public static final String INVALIDATED_ENTRY = "invalid";
	public static final String CACHE_FULL_LRU = "lru";
	
	private String m_cacheID;
	private String m_key;
	private Object m_value;
	private String m_trigger;
    /**
     * Construct a <code>CacheEntryRemovalEvent</code> using information about the removed item
     *
     * @param cacheID the unique id of the cachetable from which the entry was removed
     * @param key 
     * @param value the object assigned to that key
     * @param trigger the cause of removal - invalidated or out of date when retrieved
     */
    public CacheEntryRemovalEvent(String cacheID, String key, Object value, String trigger) {
       m_cacheID = cacheID;
       m_key = key;
       m_value = value;
       m_trigger = trigger;
    }
    
    /**
     * 
     * @return the unique key of the cache table from which the entry was removed
     */
    public String getCacheID () {
    	return m_cacheID;
    }
    /**
     * 
     * @return key of the removed item
     */
    public String getKey() {
    	return m_key;
    }
    
    /**
     * 
     * @return The object that has been removed from the cache
     */
    public Object getValue() {
    	return m_value;
    }
    
    public String getRemovalTrigger() {
    	return m_trigger;
    }

}
