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

package com.arsdigita.london.exporter;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.URLParameter;

public class ExporterConfig extends AbstractConfig {
    
    private Parameter m_adapters;

    public ExporterConfig() {
        try {
            m_adapters = new URLParameter
                ("com.arsdigita.london.exporter.traversal_adapters", 
                 Parameter.REQUIRED,
                 new URL(null,
                         "resource:WEB-INF/resources/exporter-adapters.xml"));
        } catch (MalformedURLException ex) {
            throw new UncheckedWrapperException("Cannot parse URL", ex);
        }
        
        register(m_adapters);
        
        loadInfo();
    }


    InputStream getTraversalAdapters() {
        try {
            return ((URL)get(m_adapters)).openStream();
        } catch (IOException ex) {
            throw new UncheckedWrapperException("Cannot read stream", ex);
        }
    }    
}
