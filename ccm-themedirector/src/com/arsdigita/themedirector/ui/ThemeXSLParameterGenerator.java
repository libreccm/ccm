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

package com.arsdigita.themedirector.ui;

import com.arsdigita.themedirector.dispatcher.InternalThemePrefixerServlet;
import com.arsdigita.themedirector.ThemeDirectorConstants;
import com.arsdigita.themedirector.ThemeDirectorConfig;
import com.arsdigita.themedirector.ThemeDirector;
import com.arsdigita.templating.XSLParameterGenerator;
import javax.servlet.http.HttpServletRequest;
import com.arsdigita.web.Web;
import com.arsdigita.london.subsite.Subsite;
import com.arsdigita.london.subsite.SubsiteContext;


/**
 *  This looks at the request and is able to determine if it is 
 *  production or preview as well as the theme that is in use.
 *  Then, it returns the appropriate prefix that will be something
 *  similar to /ccm-ldn-theme/__ccm__/themes-[dev|prod]/[theme-name]/
 *
 *  @author Randy Graebner (randyg@redhat.com)
 */
public class ThemeXSLParameterGenerator implements XSLParameterGenerator,
                                                   ThemeDirectorConstants {
    /**
     *  This returns the correct value for the parameter.  This is the
     *  value that is added to the transformer and is available to all
     *  stylesheets 
     */
    public String generateValue(HttpServletRequest request) {

        String themeURL = 
            InternalThemePrefixerServlet.getThemePreviewURL(request);

        String baseDir = null;

        if (themeURL != null) {
            baseDir = DEV_DIR_STUB;
            // we want to strip the final "/" and everything after it
            int index = themeURL.lastIndexOf("/");
            if (index > 0) {
                themeURL = themeURL.substring(0, index);
            }
        } else {
            baseDir = PROD_DIR_STUB;
            SubsiteContext context = Subsite.getContext();
            if (context.hasSite() && 
                context.getSite().getStyleDirectory() != null) {
                themeURL = "/" + context.getSite().getStyleDirectory();
            }

            if( null == themeURL ) {
                String defaultThemeURL = ThemeDirectorConfig.getDefaultThemeURL( request );

                if( null != defaultThemeURL ) themeURL = "/" + defaultThemeURL;
            }
        }

        if (themeURL != null) {
        //  modified as ccm-ldn-theme is no longer installed in its own context
        //  return "/" + WEB_APP_NAME + "/" + THEMES_DIR + "/" +
        //  If we want to install it as a separate web application again we
        //  should find a way to determin the name from a central configuration
            return "/" + THEMES_DIR + "/" +
                Web.getContext().getRequestURL().getContextPath() +
                baseDir + themeURL;
        } else {
            // this means that there is no theme associated with the
            // given subsite, so we return the default theme
            themeURL = 
                ThemeDirector.getConfig().getDefaultThemeContext() +
                ThemeDirector.getConfig().getDefaultThemePath();
            if (themeURL.endsWith("/")) {
                themeURL = themeURL.substring(0, themeURL.length()-1);
            }
            return themeURL;
        }
    }
} 
