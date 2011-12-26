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

package com.arsdigita.shortcuts;

import com.arsdigita.runtime.AbstractConfig;

import org.apache.log4j.Logger;

/**
 * A record containing server-session scoped configuration properties.
 *
 * Accessors of this class may return null.  Developers should take
 * care to trap null return values in their code.
 *
 * @see com.arsdigita.web.Web
 * @author Randy Graebner &lt;randyg@redhat.com&gt;
 * @version $Id: ShortcutsConfig.java 796 2005-09-12 15:06:53Z fabrice $
 */
public final class ShortcutsConfig extends AbstractConfig {

    /** A logger instance to assist debugging.  */
    private static final Logger s_log = Logger.getLogger(ShortcutsConfig.class);

    /** Singelton config object.  */
    private static ShortcutsConfig s_conf;

    /**
     * Gain a SubsiteConfig object.
     *
     * Singelton pattern, don't instantiate a config object using the
     * constructor directly!
     * @return
     */
    public static synchronized ShortcutsConfig getConfig() {
        if (s_conf == null) {
            s_conf = new ShortcutsConfig();
            s_conf.load();
        }

        return s_conf;
    }

    // //////////////////////////////////////////////////////////////////////// 
    // Set of configuration parameters

    // Nothing to configurfe yet
    
    
    /**
     * Constructor (singleton pattern). 
     * Don't instantiate a config object using the constructor directly!
     * use getConfig instead!
     */
    public ShortcutsConfig() {

        // register([parameter]);
        
        loadInfo();
    }
}
