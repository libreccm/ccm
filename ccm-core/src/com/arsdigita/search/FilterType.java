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

import com.arsdigita.util.Assert;

/**
 * A filter type defines a condition on which a set 
 * of result documents can be restricted. An example
 * would be 'restrict by category'. 
 *
 * @see com.arsdigita.search.FilterSpecification
 */
public abstract class FilterType {

    private String m_key;
    private String m_description;
    
    /**
     * Creates a new filter type
     * @param key a unique key representing this filter type
     * @param description a human friendly description
     */
    protected FilterType(String key,
                         String description) {
        Assert.exists(key, String.class);
        Assert.exists(description, String.class);
        
        m_key = key;
        m_description = description;
    }
    
    /**
     * Gets the unique filter key
     * @return the filter key
     */
    public String getKey() {
        return m_key;
    }
    
    /** 
     * Gets the filter description
     * @return the filter description
     */
    public String getDescription() {
        return m_description;
    }
    
    /**
     * Two filter type objects compare
     * isEqual if they have the same filter
     * key
     */
    public boolean equals(Object o) {
        if (!(o instanceof FilterType)) {
            return false;
        }
        
        FilterType type = (FilterType)o;
        
        return m_key.equals(type.m_key);
    }

    /**
     * Returns a hashcode compatible with the
     * definition of the equals() method
     */
    public int hashCode() {
        return m_key.hashCode();
    }
    
}
