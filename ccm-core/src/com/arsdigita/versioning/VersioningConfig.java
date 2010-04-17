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

import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.parameter.BooleanParameter;
import com.arsdigita.util.parameter.Parameter;
// import com.arsdigita.util.parameter.ParameterRecord;
// replaced by c.ad.runtime.AbstractConfig, should be deleted after testing
import org.apache.log4j.Logger;

/**
 * A configuration record for configuration of the versioning system.
 * 
 * Extends runtime.AbstractConfig. A noargs constructor registers the
 * parameters with the superclass.
 * 
 * @author Justin Ross
 * Modified: Peter Boy pboy@barkhof.uni-bremen.de
 * @version $Id: VersioningConfig.java 287 2005-02-22 00:29:02Z sskracic $
 */
public final class VersioningConfig extends AbstractConfig {

    private static final Logger s_log = Logger.getLogger(VersioningConfig.class);

    private final Parameter m_debug;

    public VersioningConfig() {
        super();

        m_debug = new BooleanParameter
            ("waf.versioning.debug_ui_enabled", Parameter.REQUIRED, Boolean.FALSE);

        register(m_debug);
        
	loadInfo();
    }

    boolean isDebugUIEnabled() {
        return ((Boolean) get(m_debug)).booleanValue();
    }
}
