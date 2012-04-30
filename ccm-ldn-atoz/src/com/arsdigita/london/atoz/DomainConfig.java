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

package com.arsdigita.london.atoz;

import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.ResourceParameter;


import java.io.InputStream;

import org.apache.log4j.Logger;

/**
 *  This is the configuration file for the AtoZ application
 */
public class DomainConfig extends AbstractConfig {
    
    /** A logger instance to assist debugging.  */
    private static final Logger s_log = Logger.getLogger(DomainConfig.class);

    /** Singelton config object.  */
    private static DomainConfig s_conf;

    /**
     * Gain a DomainConfig object.
     *
     * Singelton pattern, don't instantiate a config object using the
     * constructor directly!
     * @return
     */
    public static synchronized DomainConfig getConfig() {
        if (s_conf == null) {
            s_conf = new DomainConfig();
            s_conf.load();
        }

        return s_conf;
    }


    // ///////////////////////////////////////////////////////////////////////
    //
    // set of configuration parameters

    /** Rules for configuring information in generated XML                    */
    private Parameter m_adapters = new ResourceParameter
            ("com.arsdigita.london.atoz.traversal_adapters", 
             Parameter.REQUIRED,
             "/WEB-INF/resources/atoz-ldn-domain-adapters.xml");

    /**
     * Constructor
     */
    public DomainConfig() {

        register(m_adapters);

        loadInfo();
    }


    /**
     * Provides access to the traversal adapter as stream.
     * @return 
     */
    InputStream getTraversalAdapters() {
        return (InputStream)get(m_adapters);
    }
    
}
