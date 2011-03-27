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

import javax.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.ArrayList;

/**
 *  This looks at the current state and returns the correct directory
 *  to look for the themes.  This is especially useful when the distinction
 *  between "dev" and "prod" is needed.
 */
public class ThemeDirectoryPatternGenerator implements PatternGenerator,
                                                       ThemeDirectorConstants {
    public String[] generateValues(String key,
                                   HttpServletRequest req) {
        
        List themes = new ArrayList();
        
        // Create a list of theme directories, most preferrable
        // theme listed first.

        // if it is in "preview" mode, we use the devevlopment directory;
        // otherwise, we use the production directory
        String value = DispatcherHelper.getDispatcherPrefix(req);
        if (value != null) {
            themes.add(DEV_DIR_STUB);
        } else {
            themes.add(PROD_DIR_STUB);
        }
        
        return (String[])themes.toArray(new String[themes.size()]);
    }
}
