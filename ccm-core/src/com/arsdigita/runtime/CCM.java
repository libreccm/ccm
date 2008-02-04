/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.runtime;

import com.arsdigita.util.UncheckedWrapperException;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.log4j.Logger;

/**
 * Subject to change.
 *
 * An entry-point class for functions of the CCM runtime package.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: CCM.java 751 2005-09-02 12:52:23Z sskracic $
 */
public final class CCM {
    private static final Logger s_log = Logger.getLogger(CCM.class);

    public static final URL getHomeURL() {
        try {
            return CCM.getHomeDirectory().toURL();
        } catch (MalformedURLException e) {
            throw new UncheckedWrapperException(e);
        }
    }

    static final File getHomeDirectory() {
        final String home = System.getProperty("ccm.home");

        if (home == null) {
            throw new IllegalStateException
                ("The ccm.home system property is null or not defined");
        }

        final File file = new File(home);

        if (!file.exists()) {
            throw new IllegalStateException
                ("The file given in the ccm.home system property " +
                 "does not exist");
        }

        if (!file.isDirectory()) {
            throw new IllegalStateException
                ("The file given in the ccm.home system property " +
                 "is not a directory");
        }

        return file;
    }

    public static final URL getConfigURL() {
        try {
            return CCM.getConfigDirectory().toURL();
        } catch (MalformedURLException e) {
            throw new UncheckedWrapperException(e);
        }
    }

    public static final File getConfigDirectory() {
        final String conf = System.getProperty("ccm.conf");

        File file;
        if (conf == null) {
            file = new File(new File(CCM.getHomeDirectory(),"conf"), "registry");
        } else {
            file = new File(conf);
        }

        if (!file.exists()) {
            if (!file.mkdirs()) {
                throw new IllegalStateException
                    ("Could not create configuration directory: " + file);
            }
        }
        if (!file.isDirectory()) {
            throw new IllegalStateException
                ("Configuration directory value is not a directory: " + file);
        }

        return file;
    }

    public static final File getDataDirectory() {
        File file = new File(CCM.getHomeDirectory(),"data");
        if (!file.exists()) {
            if (!file.mkdirs()) {
                throw new IllegalStateException
                    ("Could not create data directory: " + file);
            }
        }
        if (!file.isDirectory()) {
            throw new IllegalStateException
                ("Data directory value is not a directory: " + file);
        }
        return file;
    }
}
