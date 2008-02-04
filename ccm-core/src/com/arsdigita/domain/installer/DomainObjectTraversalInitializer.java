/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.domain.installer;


import com.arsdigita.domain.xml.TraversalHandler;
import com.arsdigita.initializer.Configuration;
import com.arsdigita.initializer.InitializationException;
import com.arsdigita.xml.XML;

import org.apache.log4j.Logger;

import java.util.Iterator;
import java.util.List;

public class DomainObjectTraversalInitializer
    implements com.arsdigita.initializer.Initializer {

    public static final String ADAPTERS = "adapters";

    private static final Logger s_log =
        Logger.getLogger(DomainObjectTraversalInitializer.class);

    private Configuration m_config = new Configuration();

    public DomainObjectTraversalInitializer()
        throws InitializationException {
        m_config.initParameter
            (ADAPTERS,
             "The path to an XML file containing adapter specifications",
             List.class);
    }

    public Configuration getConfiguration() {
        return m_config;
    }

    public void startup() throws InitializationException {
        List adapters = (List) m_config.getParameter(ADAPTERS);

        if (adapters != null) {
            Iterator i = adapters.iterator();
            while (i.hasNext()) {
                String path = (String) i.next();
                XML.parseResource(path, new TraversalHandler());
            }
        } else {
            s_log.warn("No file specified for loading adapters");
        }
    }

    public void shutdown() throws InitializationException {
        // nada
    }

}
