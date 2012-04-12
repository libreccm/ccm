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

package com.arsdigita.atoz;

import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.parameter.BooleanParameter;
import com.arsdigita.util.parameter.ClassParameter;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.ResourceParameter;

import com.arsdigita.london.util.ui.ApplicationCategoryPicker;

import java.io.InputStream;
import java.util.Set;
import java.util.HashSet;

import org.apache.log4j.Logger;

/**
 *  This is the configuration file for the AtoZ application
 */
public class AtoZConfig extends AbstractConfig {
    
    /** A logger instance to assist debugging.  */
    private static final Logger s_log = Logger.getLogger(AtoZConfig.class);

    /** Singelton config object.  */
    private static AtoZConfig s_conf;

    /**
     * Gain a AtoZConfig object.
     *
     * Singelton pattern, don't instantiate a config object using the
     * constructor directly!
     * @return
     */
    public static synchronized AtoZConfig getConfig() {
        if (s_conf == null) {
            s_conf = new AtoZConfig();
            s_conf.load();
        }

        return s_conf;
    }
    
    /**   */
    private Set m_types;


    // ///////////////////////////////////////////////////////////////////////
    //
    // set of configuration parameters

    /** Rules for configuring information in generated XML                    */
    private Parameter m_adapters = new ResourceParameter
            ("com.arsdigita.london.atoz.traversal_adapters", 
             Parameter.REQUIRED,
             "/WEB-INF/resources/atoz-adapters.xml");
    /** The UI widget for the Root Category Picker                            */
    private Parameter m_rootCategoryPicker = new ClassParameter(
            "com.arsdigita.london.atoz.root_category_picker",
            Parameter.REQUIRED,
            ApplicationCategoryPicker.class);
    /** Make AtoZ use subsite-specific navigation categories. Set to true, 
     *  and the AtoZ will use the subsite-specific navigation categories 
     *  if you define *any* CategoryProvider                       */
    private BooleanParameter m_useSubsiteSpecificNavigationCategory = new BooleanParameter
            ("com.arsdigita.london.atoz.use_subsite_specific_navigation_category",
             Parameter.OPTIONAL,
             Boolean.FALSE);
    /** To filter out category without published items. If TRUE, the AtoZ 
     *  category providers will only return Categories with published items   */
    private BooleanParameter m_filterCategoryProdiver = new BooleanParameter (
        		"com.arsdigita.london.atoz.filterCategoryProviders",
        		Parameter.OPTIONAL,
        		Boolean.FALSE);

    public AtoZConfig() {

        m_types = new HashSet();

        register(m_adapters);
        register(m_rootCategoryPicker);
        register(m_useSubsiteSpecificNavigationCategory);
        register(m_filterCategoryProdiver);

        loadInfo();
    }


    InputStream getTraversalAdapters() {
        return (InputStream)get(m_adapters);
    }
    
    public Class getRootCategoryPicker() {
        return (Class)get(m_rootCategoryPicker);
    }

    public void registerProviderType(AtoZProviderType type) {
        m_types.add(type);
    }

    public AtoZProviderType[] getProviderTypes() {
        return (AtoZProviderType[])m_types
            .toArray(new AtoZProviderType[m_types.size()]);
    }

    public boolean useSubsiteSpecificNavigationCategory() {
        return ((Boolean) get(m_useSubsiteSpecificNavigationCategory)).booleanValue();
    }
    
    public boolean filterCategoryProviders () {
    	return ((Boolean) get(m_filterCategoryProdiver)).booleanValue();
    }
}
