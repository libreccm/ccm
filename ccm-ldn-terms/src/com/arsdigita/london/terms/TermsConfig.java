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

package com.arsdigita.london.terms;

import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.BooleanParameter;
import com.arsdigita.util.parameter.ResourceParameter;
import com.arsdigita.util.parameter.StringParameter;

import java.io.InputStream;
// import java.io.IOException;

import org.apache.log4j.Logger;


/**
 * 
 * 
 */
public class TermsConfig extends AbstractConfig {
    private static final Logger s_log = 
        Logger.getLogger(TermsConfig.class);
    
    private Parameter m_adapters;
    private Parameter m_defaultDomain;
    private final Parameter m_ajaxExpandAllBranches;

    public TermsConfig() {
        m_adapters = new ResourceParameter
            ("com.arsdigita.london.terms.traversal_adapters", 
             Parameter.REQUIRED,
             "/WEB-INF/resources/terms-adapters.xml");
        
        m_defaultDomain = new StringParameter(
            "com.arsdigita.london.terms.default_domain",
            Parameter.REQUIRED,
            "LGCL");

        /** Wether to expand all Subcategories in order to retain behavious prior
         *  to use AJAX to expand dynamically.                                   */
        m_ajaxExpandAllBranches = new BooleanParameter(
            "com.arsdigita.london.terms.ajax_expand_on_all_branches",
            Parameter.OPTIONAL,
            Boolean.FALSE);
        
        register(m_adapters);
        register(m_defaultDomain);
        register(m_ajaxExpandAllBranches);
        loadInfo();
    }

    /**
     * 
     * @return
     */
    InputStream getTraversalAdapters() {
        return (InputStream) get(m_adapters);
    }
    
    /**
     * 
     * @return
     */
    public String getDefaultDomainKey() {
        return (String)get(m_defaultDomain);
    }
    
    /**
     * 
     * @return
     */
    public Domain getDefaultDomain() {
        return Domain.retrieve(getDefaultDomainKey());
    }

    /**
     *
     * @return
     */
    public boolean ajaxExpandAllBranches () {
    	return ((Boolean)get(m_ajaxExpandAllBranches)).booleanValue();
    }

}
