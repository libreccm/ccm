/*
 * Copyright (C) 2010 pboy (pboy@barkhof.uni-bremen.de) All Rights Reserved.
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

package com.arsdigita.ui;

import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.parameter.StringArrayParameter;
import com.arsdigita.util.parameter.Parameter;

import java.util.Arrays;
// import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * A configuration record for configuration of the core UI package
 * (layoput of main UI components).
 *
 * @author Peter Boy &lt;pboy@barkhof.uni-bremen.de&gt;
 * @version $Id: $
 */
public class UIConfig extends AbstractConfig {

    /** A logger instance.  */
    private static final Logger s_log = Logger.getLogger(UIConfig.class);

    /** Singelton config object.  */
    private static UIConfig s_conf;

    /**
     * Gain a UIConfig object.
     *
     * Singelton pattern, don't instantiate a lifecacle object using the
     * constructor directly!
     * @return
     */
    public static final synchronized UIConfig getConfig() {
        if (s_conf == null) {
            s_conf = new UIConfig();
            s_conf.load();
        }

        return s_conf;
    }

    /**
     * Default set of page component objects defining the default layout for the
     * SimplePage class.
     * Format: list
     *
     */
//  From the OLD initializer:
//  If using the default SimplePage class, the following
//  two parameters specify the class names of the bebop
//  components to (optionally) add to margins of pages

//  The is default set of page components
//  defaultLayout = {
//    { "top", "com.arsdigita.ui.UserBanner" },
//    { "bottom", "com.arsdigita.ui.SiteBanner" },
//    { "bottom", "com.arsdigita.ui.DebugPanel" }
//
//    //    { "left", "com.arsdigita.x.y.z" },
//    //    { "right", "com.arsdigita.x.y.z" }
//  };

    private final Parameter m_defaultLayout = 
            new StringArrayParameter(
                    "waf.ui.default_layout",
                    Parameter.REQUIRED,
                    new String[]
                        { "top:com.arsdigita.ui.UserBanner",
                          "bottom:com.arsdigita.ui.SiteBanner",
                          "bottom:com.arsdigita.ui.DebugPanel"
                          // "left,com.arsdigita.x.y.zl",
                          // "right,com.arsdigita.x.y.zr",
                        }
                );

    /**
     * The customized layout for applications using the SimplePage class
     * Format: list
     */
    private final Parameter m_applicationLayouts = 
            new StringArrayParameter(
                    "waf.ui.application_layouts",
                    Parameter.OPTIONAL,
                    null
                );

    /**
     * Constructs an empty RuntimeConfig object.
     *
     */
    public UIConfig() {
    // pboy: According to the comment for the getConfig() method a singleton
    // pattern is to be used. Therefore the constructor must be changed to
    // private!
    // private UIConfig() {
        register(m_defaultLayout);
        register(m_applicationLayouts);

        loadInfo();

    }

    /**
     * Retrieve the set of default page component objects defining
     * the default layout for SimplePage class.
     */
    public List getDefaultLayout() {
        String[] defaultLayoutArray = (String[]) get(m_defaultLayout);
        return Arrays.asList(defaultLayoutArray);
    }

    /**
     * Retrieve the set of default page component objects defining
     * the default layout for SimplePage class.
     */
    public List getApplicationLayouts() {
        String[] layouts = (String[]) get(m_applicationLayouts);
        return Arrays.asList(layouts);
    }

}
