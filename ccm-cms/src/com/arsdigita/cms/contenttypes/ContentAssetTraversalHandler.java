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
package com.arsdigita.cms.contenttypes;

import com.arsdigita.domain.xml.TraversalHandler;
import com.arsdigita.domain.DomainObjectTraversalAdapter;

import org.apache.log4j.Logger;

/**
 * A item adapter handler that registers the adapter to the 
 * general content item handler.
 */
public class ContentAssetTraversalHandler extends TraversalHandler {

    public static final Logger s_log = 
        Logger.getLogger(ContentAssetTraversalHandler.class);

    private String m_property;
    
    public ContentAssetTraversalHandler(String property) {
        m_property = property;
    }

    protected void registerAdapter(String objectType,
                                   DomainObjectTraversalAdapter adapter,
                                   String context) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Registering asset adapter " + adapter.getClass() + 
                        " for " + objectType  + " in context " + context + 
                        " with property " + m_property);
        }
        ContentItemTraversalAdapter.registerAssetAdapter(m_property,
                                                         adapter,
                                                         context);
    }
}
