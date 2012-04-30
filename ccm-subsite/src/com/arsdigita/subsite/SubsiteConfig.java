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

package com.arsdigita.subsite;

import com.arsdigita.runtime.AbstractConfig;

import com.arsdigita.util.parameter.ClassParameter;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.ResourceParameter;
import com.arsdigita.util.parameter.StringArrayParameter;
import com.arsdigita.util.parameter.StringParameter;
import com.arsdigita.categorization.ui.ApplicationCategoryPicker;

import java.io.InputStream;
import java.util.Map;
import java.util.HashMap;

import org.apache.log4j.Logger;


public class SubsiteConfig extends AbstractConfig {
    
    /** A logger instance to assist debugging.  */
    private static final Logger s_log = Logger.getLogger(SubsiteConfig.class);

    /** Singelton config object.  */
    private static SubsiteConfig s_conf;

    /**
     * Gain a SubsiteConfig object.
     *
     * Singelton pattern, don't instantiate a config object using the
     * constructor directly!
     * @return
     */
    public static synchronized SubsiteConfig getConfig() {
        if (s_conf == null) {
            s_conf = new SubsiteConfig();
            s_conf.load();
        }

        return s_conf;
    }

    /**                                                                      */
    private Map m_themes = new HashMap();

    // //////////////////////////////////////////////////////////////////////// 
    // Set of configuration parameters

    /** 
     * 
     */
    private Parameter m_adapters = new 
            ResourceParameter("com.arsdigita.subsite.traversal_adapters", 
                              Parameter.REQUIRED, 
                              "/WEB-INF/resources/subsite-adapters.xml");

    /** Class name of application type which should be used for front page
     *  of all created subsites. 
     *  It is not possible to use different front page appöications for
     *  different subsites.
     */
    private Parameter m_frontPageApplicationTypeParameter= new 
            StringParameter("com.arsdigita.subsite.front_page_application",
                            Parameter.REQUIRED,
                            "com.arsdigita.portalworkspace.Workspace");
    /** Array of class name of application types, which are usable as front page
     *  application for a subsite and whose instances are to be included in a
     *  selection box to choose a custom front page appplication  for a specific
     *  subsite. 
     */
    private Parameter m_frontPageApplicationTypes= new 
            StringArrayParameter("com.arsdigita.subsite.front_page_application",
                            Parameter.REQUIRED, new String[] {
                            "com.arsdigita.navigation.Navigation",    
                            "com.arsdigita.portalworkspace.Workspace",
                            "com.arsdigita.portalserver.Portalsite"
                             });

    /**  
     * 
     */
    private Parameter m_frontPageParentURLParameter = new 
            StringParameter("com.arsdigita.subsite.front_page_parent_url",
                            Parameter.REQUIRED,
                            "/portal/");

    /** 
     * 
     */
    private Parameter m_rootCategoryPicker = new 
            ClassParameter("com.arsdigita.subsite.root_category_picker",
                           Parameter.REQUIRED,
                           ApplicationCategoryPicker.class);


    /**
     * 
     */
    public SubsiteConfig() {

        register(m_adapters);
        register(m_frontPageApplicationTypeParameter);
        register(m_frontPageApplicationTypes);
        register(m_frontPageParentURLParameter);
        register(m_rootCategoryPicker);
     
        loadInfo();
    }

    InputStream getTraversalAdapters() {
        return (InputStream) get(m_adapters);
    }

    /**
     * 
     * @return 
     */
    public String getFrontPageApplicationType() {
        return (String)get(m_frontPageApplicationTypeParameter);
    }

    /**
     * 
     * @return 
     */
    public String[] getFrontPageApplicationTypes() {
        return (String[])get(m_frontPageApplicationTypes);
    }

    public String getFrontPageParentURL() {
        return (String)get(m_frontPageParentURLParameter);
    }


    public Class getRootCategoryPicker() {
        return (Class)get(m_rootCategoryPicker);
    }

    /**
     *  This returns a Map of "canned" themes.  The key is the
     *  url and the value is the "Pretty Name" that can be displayed
     *  to the user.
     */
    public Map getThemes() {
        return m_themes;
    }


    /**
     *  This adds a theme to the list of available subsites for the
     *  system.  If you add the same URL multiple times, each successive
     *  time will overwrite the previous so only the last prettyName
     *  will remain
     */
    public void addTheme(String url, String prettyName) {
        m_themes.put(url, prettyName);
    }

    /**
     *  This removes the appropriate url from the available themes
     */
    public void removeTheme(String url) {
        m_themes.remove(url);
    }
}
