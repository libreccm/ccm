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
 */

package com.arsdigita.london.search.spider;

import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.IntegerParameter;
import org.apache.log4j.Logger;


/**
 * A record containing search spider configuration properties.
 */
public final class SpiderConfig extends AbstractConfig {
    public static final String versionId =
        "$Id: SpiderConfig.java 287 2005-02-22 00:29:02Z sskracic $" +
        "$Author: sskracic $" +
        "$DateTime: 2003/11/27 11:55:32 $";

    private static final Logger s_log = Logger.getLogger(SpiderConfig.class);

    private static final String DELAY = "delay";
    private static final String FREQUENCY = "frequency";
    private static final String MAX_DEPTH = "maxDepth";
    private static final String URLS = "urls";

    private final Parameter m_delay;
    private final Parameter m_frequency;
    private final Parameter m_maxDepth;
    private final Parameter m_urls = null;

    public SpiderConfig() {
        m_delay = new IntegerParameter
            ("com.arsdigita.london.search.spider.delay", 
             Parameter.REQUIRED, "600");
        m_maxDepth = new IntegerParameter
            ("com.arsdigita.london.search.spider.max_depth", 
             Parameter.REQUIRED, "2");
        m_frequency = new IntegerParameter
            ("com.arsdigita.london.search.spider.frequency", 
             Parameter.REQUIRED, "0");

        register(m_delay);
        register(m_maxDepth);
        register(m_frequency);
        loadInfo();
    }


    public final Integer getDelay() {
        return (Integer) get(m_delay);
    }

    public final Integer getMaxDepth() {
        return (Integer) get(m_maxDepth);
    }

    public final Integer getFrequency() {
        return (Integer) get(m_frequency);
    }
}
