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

package com.arsdigita.themedirector;

import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.templating.PatternGenerator;

import com.arsdigita.london.subsite.Subsite;

import javax.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.ArrayList;

import org.apache.log4j.Logger;

public class ThemePatternGenerator implements PatternGenerator {

    /** A logger instance.  */
    private static final Logger s_log =
        Logger.getLogger( ThemePatternGenerator.class );

    /** 
     * 
     * @param key
     * @param req
     * @return
     */
    public String[] generateValues(String key,
                                   HttpServletRequest req) {
        List themes = new ArrayList();
        
        // Create a list of themes, most preferrable
        // theme listed first.

        String prefix = DispatcherHelper.getDispatcherPrefix(req);
        // TODO - move the "theme" to somewhere else
        if (prefix != null && prefix.startsWith("/theme")) {
            // We need to get the info after the "theme" without
            // any "/" in it.
            String themeDir = prefix.substring("/theme/".length());
            int slashIndex = themeDir.indexOf("/");
            if (slashIndex > -1) {
                themeDir = themeDir.substring(slashIndex);
            }
            themes.add(themeDir);
        }

        if (Subsite.getContext().hasSite()) {
            String dir = Subsite.getContext().getSite().getStyleDirectory();
            if (dir != null) {
                // Subsite specific style. 
                themes.add(dir);
            }
        } 

        String defaultThemeURL = ThemeDirectorConfig.getDefaultThemeURL( req );
        if( null != defaultThemeURL ) themes.add( defaultThemeURL );

        return (String[])themes.toArray(new String[themes.size()]);
    }
}
