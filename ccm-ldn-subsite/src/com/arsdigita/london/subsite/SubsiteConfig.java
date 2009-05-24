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

package com.arsdigita.london.subsite;

import com.arsdigita.runtime.AbstractConfig;

import com.arsdigita.util.parameter.ClassParameter;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.ResourceParameter;
import com.arsdigita.util.parameter.StringParameter;
// import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.london.util.ui.ApplicationCategoryPicker;
import java.io.InputStream;
import java.io.IOException;
// import java.net.URL;
// import java.net.MalformedURLException;
import java.util.Map;
import java.util.HashMap;

import org.apache.log4j.Logger;


public class SubsiteConfig extends AbstractConfig {
    
    private static final Logger s_log = Logger.getLogger(SubsiteConfig.class);

    private Map m_themes = new HashMap();

    private ResourceParameter m_adapters;
    private StringParameter m_frontPageApplicationTypeParameter;
    private StringParameter m_frontPageParentURLParameter;
    private Parameter m_rootCategoryPicker;

    public SubsiteConfig() {

        m_adapters = new ResourceParameter
            ("com.arsdigita.london.subsite.traversal_adapters", 
             Parameter.REQUIRED, 
             "/WEB-INF/resources/subsite-adapters.xml");

        m_frontPageApplicationTypeParameter = new StringParameter
            ("com.arsdigita.london.subsite.front_page_application",
             Parameter.REQUIRED,
             "com.arsdigita.london.portal.Workspace");

        m_frontPageParentURLParameter = new StringParameter
            ("com.arsdigita.london.subsite.front_page_parent_url",
             Parameter.REQUIRED,
             "/portal/");
        m_rootCategoryPicker = new ClassParameter(
            "com.arsdigita.london.subsite.root_category_picker",
            Parameter.REQUIRED,
            ApplicationCategoryPicker.class);

        register(m_adapters);
        register(m_frontPageApplicationTypeParameter);
        register(m_frontPageParentURLParameter);
        register(m_rootCategoryPicker);
     
        loadInfo();
    }

    InputStream getTraversalAdapters() {
        return (InputStream) get(m_adapters);
    }

    public String getFrontPageApplicationType() {
        return (String)get(m_frontPageApplicationTypeParameter);
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
