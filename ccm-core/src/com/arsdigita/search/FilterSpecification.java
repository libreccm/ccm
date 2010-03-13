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

import java.util.Map;
import java.util.HashMap;

/**
 * A filter specification defines the parameters for 
 * a plugging into a condition defined by a filter type.
 * For example a 'list of category ids' would be used
 * in a 'restrict by category' filter condition..
 * These values are typically obtained from the user
 * via a web form.
 *
 * @see com.arsdigita.search.FilterType
 */
public abstract class FilterSpecification {
    
    private Map m_values;
    private FilterType m_type;
    
    /**
     * Creates a new filter specification, initializing
     * the value map from the array of values. The values
     * array should have an even number of elements, the
     * off elements are String keys, the even eleements 
     * are the corresponding Object values.
     *
     * @param type the filter type 
     * @param values the values for the filter type
     */
    protected FilterSpecification(Object[] values,
                                  FilterType type) {
        Assert.exists(values, Object.class);
        Assert.exists(type, FilterType.class);
        Assert.isTrue(values.length % 2 == 0, "length of value list is even");

        m_values = new HashMap();
        for (int i = 0 ; i < (values.length / 2) ; i++) {
            m_values.put((String)values[i*2], values[(i*2)+1]);
        }

        m_type = type;
    }
    
    /**
     * Get the values for the filter specification
     * @param key the value
     * @return the filter values
     */
    protected Object get(String key) {
        return m_values.get(key);
    }
    
    /**
     * Set the value for the specified key
     * @param key the key
     * @param value the value
     */
    protected void set(String key,
                       Object value) {
        m_values.put(key, value);
    }
    
    /**
     * Get the filter type
     * @return the filter type
     */
    public FilterType getType() {
        return m_type;
    }
    
    /**
     * Two filter specifications are considered isEqual
     * if they refer to the same filter type
     * and their parameter sets contain the same
     * (key, value) pairs
     */
    public boolean equals(Object o) {
        if (!(o instanceof FilterSpecification)) {
            return false;
        }
        
        FilterSpecification spec = (FilterSpecification)o;

        if (!m_type.equals(spec.getType())) {
            return false;
        }

        return m_values.equals(spec.m_values);
    }
    
    /**
     * Returns a hashcode compatible with the
     * definition of the equals() method
     */
    public int hashCode() {
        return m_values.hashCode();
    }
        
}
