/*
 * Copyright (c) 2015 Jens Pelzetter
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
package com.arsdigita.cms.contenttypes;

import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.parameter.IntegerParameter;
import com.arsdigita.util.parameter.Parameter;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
public class SiteProxyConfig extends AbstractConfig {

    /**
     * Size of the URLPool used by the {@link SiteProxy}.
     */
    private final Parameter urlPoolSize;
    /**
     * Timeout for the URLPool used by the {@link SiteProxy} in milliseconds.
     */
    private final Parameter urlPoolTimeout;
    /**
     * URL cache size for the SiteProxy
     */
    private final Parameter urlCacheSize;
    /**
     * Expiry time for the URLCache used by the SiteProxy in milliseconds.
     */
    private final Parameter urlCacheExpiryTime;
    
    public SiteProxyConfig() {
        urlPoolSize = new IntegerParameter("com.arsdigita.cms.contenttypes.url_pool_size",
                                           Parameter.REQUIRED,
                                           10);
        urlPoolTimeout = new IntegerParameter("com.arsdigita.cms.contenttypes.url_pool_timeout",
                                              Parameter.REQUIRED,
                                              4 * 1000);
        urlCacheSize = new IntegerParameter("com.arsdigita.cms.contenttypes.url_cache_size",
                                            Parameter.REQUIRED,
                                            1000000);
        urlCacheExpiryTime = new IntegerParameter(
            "com.arsdigita.cms.contenttypes.url_cache_expirytime",
            Parameter.REQUIRED,
            1 * 60 * 1000);
        
        register(urlPoolSize);
        register(urlPoolTimeout);
        register(urlCacheSize);
        register(urlCacheExpiryTime);
        
        loadInfo();
    }
    
    public Integer getUrlPoolSize() {
        return (Integer) get(urlPoolSize);
    }
    
    public Integer getUrlPoolTimeout() {
        return (Integer) get(urlPoolTimeout);
    }
    
    public Integer getUrlCacheSize() {
        return (Integer) get(urlCacheSize);
    }
    
    public Integer getUrlCacheExpiryTime() {
        return (Integer) get(urlCacheExpiryTime);
    }
    
}
