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
package com.arsdigita.domain;

import com.arsdigita.persistence.metadata.Property;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.xml.Formatter;
import com.arsdigita.xml.NodeGenerator;
import com.arsdigita.xml.XML;

import java.util.Map;
import java.util.HashMap;

import org.apache.log4j.Logger;

public class SimpleDomainObjectXMLFormatter 
    implements DomainObjectXMLFormatter {
    
    private static final Logger s_log = 
        Logger.getLogger(SimpleDomainObjectXMLFormatter.class);

    private Map m_formatters = new HashMap();

    private Map m_nodeGenerators = new HashMap();

    public void addFormatter(String property,
                             Formatter formatter) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Adding formatter for " + property +
                        ": " + formatter.getClass());
        }
        if (m_nodeGenerators.containsKey(property)) {
	    throw new UncheckedWrapperException("A node generator is registered for " + property + ". A formatter cannot be registered for the same path");
        }
        m_formatters.put(property, formatter);
    }

    public void addGenerator(String property, NodeGenerator generator) {
	s_log.debug("Adding generator for " + property +
						": " + generator.getClass());
	if (m_formatters.containsKey(property)) {
	    throw new UncheckedWrapperException("A formatter is registered for " + property + ". A node generator cannot be registered for the same path");
	}
	m_nodeGenerators.put(property, generator);
    }
 
    public Object format(DomainObject obj,
                         String path,
                         Property prop,
                         Object value) {
        Formatter formatter = (Formatter)m_formatters.get(path);
        NodeGenerator generator = (NodeGenerator)m_nodeGenerators.get(path);
        s_log.debug("formatter for path : " + path + ": " + formatter);
	s_log.debug("generator for path : " + path + ": " + generator);
         
	if (formatter == null && generator == null) {
		s_log.debug("No formatter or Generator for path");
        	// don't fallback to default here
        	// let the upper layer (XMLRenderer) try super types
        	return null;
        }
        if (s_log.isDebugEnabled()) {
            s_log.debug("Processing property " + path + 
                        " for object "+obj+
                        " and property "+prop+
                        " and value "+value+
                        " with " + (formatter == null ? generator.getClass() : formatter.getClass()));
        }
        
        Object formatted = null;
        if (formatter == null) {
        	formatted = generator.format(value);
        } else {
        	formatted = formatter.format(value);
        }
        return formatted;
    }
    
    public boolean isEmpty() {
    	return m_formatters.isEmpty() && m_nodeGenerators.isEmpty();
    }

}
