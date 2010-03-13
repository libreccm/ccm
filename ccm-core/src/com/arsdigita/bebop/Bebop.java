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
package com.arsdigita.bebop;

import org.apache.log4j.Logger;

/**
 * @author Justin Ross
 * @see com.arsdigita.bebop.BebopConfig
 * @version $Id: Bebop.java 287 2005-02-22 00:29:02Z sskracic $
 */
public final class Bebop {

    private static final Logger s_log = Logger.getLogger(Bebop.class);

    private static BebopConfig s_config;

    /**
     * Gets the <code>BebopConfig</code> object.
     */
    public static final BebopConfig getConfig() {
        if (s_config == null) {
            s_config = new BebopConfig();
            // deprecated, use load() instead, load the default config db,
            // which is ccm-core /bebop.properties for BebogConfig by definition
            // s_config.load("ccm-core/bebop.properties");
            s_config.load();
        }
        return s_config;
    }
}
