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

import com.arsdigita.subsite.Subsite;
import com.arsdigita.subsite.SubsiteContext;
import com.arsdigita.themedirector.dispatcher.InternalThemePrefixerServlet;
import com.arsdigita.themedirector.ThemeDirectorConstants;
import com.arsdigita.themedirector.ThemeDirectorConfig;
import com.arsdigita.themedirector.ThemeDirector;
import com.arsdigita.templating.XSLParameterGenerator;
import com.arsdigita.web.Web;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;


/**
 * A Core extension which integrates ThemeDirector into core's templating 
 * system by providing an appropriate environment parameter ("theme-prefix") 
 * to locate XSL files managed by ThemeDirector. 
 * It looks at the request and is able to determine if it is production or 
 * preview as well as the theme that is in use. Then, it returns the appropriate 
 * prefix that will be something similar to 
 * /themes-[devel|published]-themedir/[theme-name]/
 *  @author Randy Graebner (randyg@redhat.com)
 *  @author Peter Boy (pboy@zes.uni-bremen.de)
 */
public class ThemeXSLParameterGenerator implements XSLParameterGenerator,
                                                   ThemeDirectorConstants {

    /** Internal logger instance to faciliate debugging. Enable logging output
     *  by editing /WEB-INF/conf/log4j.properties int the runtime environment
     *  and set com.arsdigita.themedirector.ui.ThemeXSLParameterGenerator=DEBUG 
     *  by uncommenting or adding the line.                                                   */
    private static final Logger s_log = 
         Logger.getLogger(ThemeXSLParameterGenerator.class);

    
    /**
     * This returns the correct value for the parameter.  This is the
     * value that is added to the transformer and is available to all
     * stylesheets.
     * 
     * @param request
     * @return 
     */
    @Override
    public String generateValue(HttpServletRequest request) {

        // Extracted from the request we take for granted we'll find a proper
        // leading slash as specified by JavaEE it it's not empty (null)
        String themeURL = InternalThemePrefixerServlet
                          .getThemePreviewURL(request);
        // determine the webapp context (JEE: ServletContext) where themedirector
        // is actually installed. Usually it's the same as the main CCM webapp
        // If Themedirector is installed in it's own webapp context, we have to
        // determine the context at runtime using an appropriate method!
        String myContextPath = Web.getWebappContextPath();
        if (!myContextPath.equals("")) {
            // Themedirector lives in a NON-ROOT context
            // ensure there is no trailing slash
            if (myContextPath.endsWith("/")) {
                myContextPath = myContextPath.substring(0, myContextPath
                                                           .length()-1);
            }
            // ensure it starts with a "/"
            if (!myContextPath.startsWith("/")) {
                myContextPath = "/"+myContextPath;
            }
            
        }

        if (s_log.isDebugEnabled()) {
            s_log.debug("Value for themeURL as retrieved from request "
                        + "parameter >>" + themeURL + "<<]");
            s_log.debug("Themedirector's webapp context >>" + 
                        myContextPath + "<<]");
        }

        String baseDir = null;
        

        if (themeURL != null) {
            // this means we are in a preview mode
            if (s_log.isDebugEnabled()) {
                s_log.debug("We are in preview mode. " +
                            "Assigning >" + DEV_DIR_STUB + "< to baseDir.");
            }
            baseDir = DEV_DIR_STUB;
            // we want to strip the final "/" and everything after it
            int index = themeURL.lastIndexOf("/");
            if (index > 0) {
                themeURL = themeURL.substring(0, index);
            }
        } else {
            // themeURL is null. we probably have to handle a default theme for
            // the site or a subsite.
            baseDir = PROD_DIR_STUB;
            SubsiteContext context = Subsite.getContext();
            if (context.hasSite() && context.getSite()
                                            .getStyleDirectory() != null) {
                themeURL = "/" + context.getSite().getStyleDirectory();
            }

            if( null == themeURL ) {
                String defaultThemeURL = ThemeDirectorConfig
                                         .getDefaultThemeURL( request );

                if( null != defaultThemeURL ) themeURL = "/" + defaultThemeURL;
                if (s_log.isDebugEnabled()) {
                    s_log.debug("No managed theme associated. " +
                                "Value for Default Theme >>" + themeURL + "<<]");
                }
            }
        }

        if (themeURL != null) {
            if (s_log.isDebugEnabled()) {
                s_log.debug("Constructing site's themeURL." );
            }
            themeURL =  myContextPath + THEMES_DIR + 
                        baseDir + themeURL;
        } else {
            // this means that there is no theme associated with the
            // given site, so we return the default theme
            themeURL = ThemeDirector.getConfig().getDefaultThemePath();
            if (themeURL.endsWith("/")) {
                themeURL = themeURL.substring(0, themeURL.length()-1);
            }
            if (!themeURL.startsWith("/")) {
                themeURL = "/" + themeURL;
            }
            themeURL = myContextPath + themeURL;
                       
            if (s_log.isDebugEnabled()) {
                s_log.debug("No managed theme associated. "
                            + "Value for Default Theme >>" + themeURL + "<<]");
            }
        }
        if (s_log.isDebugEnabled()) {
            s_log.debug("Returned value for themeURL: " +
                        ">>" + themeURL + "<<");
        }
        return themeURL;
    }
} 
