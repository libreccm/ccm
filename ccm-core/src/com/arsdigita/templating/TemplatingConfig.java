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
package com.arsdigita.templating;

import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.IntegerParameter;
import com.arsdigita.util.parameter.SingletonParameter;
import com.arsdigita.util.parameter.StringParameter;
import org.apache.log4j.Logger;

/**
 * @author Justin Ross
 */
public final class TemplatingConfig extends AbstractConfig {
    public static final String versionId =
        "$Id: TemplatingConfig.java 287 2005-02-22 00:29:02Z sskracic $" +
        "$Author: sskracic $" +
        "$DateTime: 2004/08/16 18:10:38 $";

    private static final Logger s_log = Logger.getLogger
        (TemplatingConfig.class);

    private final Parameter m_paths;
    private final Parameter m_resolver;
    private final Parameter m_cacheSize;
    private final Parameter m_cacheAge;

    public TemplatingConfig() {
        m_paths = new StringParameter
            ("waf.templating.stylesheet_paths", Parameter.REQUIRED,
             "/WEB-INF/resources/stylesheet-paths.txt");

        m_resolver = new SingletonParameter
            ("waf.templating.stylesheet_resolver", Parameter.REQUIRED,
             new PatternStylesheetResolver());

        m_cacheSize = new IntegerParameter
            ("waf.templating.stylesheet_cache_size", Parameter.OPTIONAL,
             null);

        m_cacheAge = new IntegerParameter
            ("waf.templating.stylesheet_cache_age", Parameter.OPTIONAL,
             null);

        register(m_paths);
        register(m_resolver);
        register(m_cacheSize);
        register(m_cacheAge);

        loadInfo();
    }

    final String getStylesheetPaths() {
        return (String) get(m_paths);
    }

    /**
     * Gets the stylesheet resolver.  This value is set via the
     * <code>com.arsdigita.templating.stylesheet_resolver</code>
     * system property.
     */
    public final StylesheetResolver getStylesheetResolver() {
        return (StylesheetResolver) get(m_resolver);
    }

    /** Can be null. */
    public final Integer getCacheSize() {
        return (Integer) get(m_cacheSize);
    }

    /** Can be null. */
    public final Integer getCacheAge() {
        return (Integer) get(m_cacheAge);
    }
}
