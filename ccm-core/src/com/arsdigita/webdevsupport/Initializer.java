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
 *
 */
package com.arsdigita.webdevsupport;

import com.arsdigita.kernel.BaseInitializer;
import com.arsdigita.initializer.Configuration;
import com.arsdigita.initializer.InitializationException;

import com.arsdigita.developersupport.DeveloperSupport;
import org.apache.log4j.Logger;

/**
 * Initializer
 *
 * @version $Revision: #17 $ $Date: 2004/08/16 $
 */

public class Initializer extends BaseInitializer {

    private Configuration m_conf = new Configuration();

    public final static String ACTIVE = "active";

    private static final Logger s_log =
        Logger.getLogger(Initializer.class);

    public Initializer() throws InitializationException {
        m_conf.initParameter(ACTIVE,
                             "Flag to turn on/off developer support",
                             Boolean.class);
    }

    /**
     * Returns the configuration object used by this initializer.
     **/
    public Configuration getConfiguration() {
        return m_conf;
    }


    /**
     * Called on startup.
     **/
    protected void doStartup() {
        Boolean active = (Boolean)m_conf.getParameter(ACTIVE);
        if (Boolean.TRUE.equals(active)) {
            s_log.debug("Registering webdev listener");
            DeveloperSupport.addListener(WebDevSupport.getInstance());
        }
    }

    /**
     * Called on shutdown. It's probably not a good idea to depend on this
     * being called.
     **/
    protected void doShutdown() { }


}
