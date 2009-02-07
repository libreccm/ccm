/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.versioning;

// deprecated, use AbstractConfig#load() instead
// import com.arsdigita.runtime.RuntimeConfigLoader;
import org.apache.log4j.Logger;

/**
 * An entry point for the services of the versioning package.
 *
 * @author Justin Ross
 * @see com.arsdigita.versioning.VersioningConfig
 */
final class Versioning {
    public static final String versionId =
        "$Id: Versioning.java 287 2005-02-22 00:29:02Z sskracic $" +
        "$Author: sskracic $" +
        "$DateTime: 2004/08/16 18:10:38 $";

    private static final Logger s_log = Logger.getLogger(Versioning.class);

    private static VersioningConfig s_config;

    /**
     * Returns the versioning config record.
     *
     * @post return != null
     */
    public static final VersioningConfig getConfig() {
        if (s_config == null) {
            s_config = new VersioningConfig();
            // deprecated, use abstractConfig#load() instead. It loads the
            // default config object, which is ccm-core/versioning.properties
            // for VersioningConfig object by definition
            // final RuntimeConfigLoader loader = new RuntimeConfigLoader();
            // loader.load("ccm-core/versioning.properties", false);
            // s_config.load(loader);
            s_config.load();
        }
        return s_config;
    }
}

