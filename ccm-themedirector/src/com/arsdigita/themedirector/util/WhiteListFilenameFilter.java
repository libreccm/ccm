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
package com.arsdigita.themedirector.util;

import com.arsdigita.themedirector.ThemeDirector;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Collection;

/**
 *  Filter that accepts files with extensions in whitelist.
 */
public class WhiteListFilenameFilter implements FilenameFilter {

    private static Collection s_extensions;

    public boolean accept(File dir, String name) {
        if (inWhiteList(name)) {
            return true;
        }
        File temp = new File(dir, name);
        return temp.exists()  &&  temp.isDirectory();
    }

    public synchronized static boolean inWhiteList(String filename) {
        if (s_extensions == null) {
            s_extensions = ThemeDirector.getConfig().getDownloadFileExtensions();
        }
        int extIndex = filename.lastIndexOf(".") + 1;
        return    extIndex > 0
              &&  filename.length() > extIndex
              &&  s_extensions.contains(filename.substring(extIndex).toLowerCase());
    }
}

