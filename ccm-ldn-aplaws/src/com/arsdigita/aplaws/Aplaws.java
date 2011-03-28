/*
 * Copyright (C) 2002-2005 Runtime Collective Ltd. All Rights Reserved.
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

package com.arsdigita.aplaws;

import org.apache.log4j.Logger;

/**
 * Central entry point for the london APLAWS integration and configuration
 * module.
 * Provides just a handle into config file.
 * @version "$Id: Aplaws.java 1297 2006-08-25 18:17:50Z apevec $
 */
public class Aplaws {

    private static final Logger LOG = Logger.getLogger(Aplaws.class);

    private static AplawsConfig aplawsConfig = new AplawsConfig();

    static {
        aplawsConfig.load();
    }

    public static final AplawsConfig getAplawsConfig() {
        return aplawsConfig;
    }
}
