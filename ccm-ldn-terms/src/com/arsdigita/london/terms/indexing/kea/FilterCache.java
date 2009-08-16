/*
 * Copyright (C) 2009 Permeance Technologies Pty Ltd. All Rights Reserved.
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 */

package com.arsdigita.london.terms.indexing.kea;

import kea.filters.KEAFilter;

import com.arsdigita.caching.CacheTable;
import com.arsdigita.london.terms.Domain;
import com.arsdigita.util.Assert;

/**
 * @author <a href="https://sourceforge.net/users/terry_permeance/">terry_permeance</a>
 */
class FilterCache {

    static KEAFilter getFilter(Domain domain, String language) {
        Assert.exists(domain);
        Assert.exists(language);

        String key = domain.getKey() + "_" + language;
        KEAFilter filter = (KEAFilter) s_cache.get(key);
        if (filter == null) {
            FilterBuilder builder = new FilterBuilder(domain, language);
            filter = builder.build();
            s_cache.put(key, filter);
        }
        return filter;
    }

    public static KEAFilter recreateFilter(Domain domain, String language) {
        Assert.exists(domain);
        Assert.exists(language);
        
        String key = domain.getKey() + "_" + language;
        s_cache.remove(key);
        return getFilter(domain, language);
    }

    public static void reset() {
        s_cache.removeAll();
    }

    private static final CacheTable s_cache = new CacheTable("FilterCache", false);
}
