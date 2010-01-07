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

import com.arsdigita.cms.ContentBundle;
import com.arsdigita.dispatcher.DispatcherHelper;
import org.apache.log4j.Logger;
import com.arsdigita.persistence.metadata.Property;
import com.arsdigita.domain.SimpleDomainObjectTraversalAdapter;
import com.arsdigita.domain.DomainObject;


/**
 * An adapter for Links allowing pluggable
 * assets to extend the traversal.
 *
 * This is a modified copy of ContentItemTraversalAdapter to make the
 * Link-Objects aware of multilingual items (ContentBundle)
 */
public class LinkTraversalAdapter 
    extends ContentItemTraversalAdapter {
    
    private static final Logger s_log = 
        Logger.getLogger(LinkTraversalAdapter.class);
    
    public LinkTraversalAdapter() {
        super();
    }
        
    public LinkTraversalAdapter(SimpleDomainObjectTraversalAdapter adapter) {
        super(adapter);
    }

    /**
     * If the path references an asset, then delegates
     * to the asset's adapter, otherwise delegates to
     * the content item's primary adapter
     */
    public boolean processProperty(DomainObject obj,
                                   String path,
                                   Property prop,
                                   String context) {

        DomainObject nObj = obj;

        if (obj instanceof ContentBundle) {

            if (s_log.isDebugEnabled()) {
                s_log.debug("Found a link to a content bundle. Resolve this link to negotiated language.");
            }

            nObj = ((ContentBundle) obj).negotiate(DispatcherHelper.getRequest().getLocales());
        }

        return super.processProperty(nObj, path, prop, context);
    }
}
