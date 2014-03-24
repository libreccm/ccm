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

import org.apache.log4j.Logger;
import com.arsdigita.persistence.metadata.Property;
import java.util.Map;
import java.util.HashMap;
import com.arsdigita.domain.DomainObjectTraversalAdapter;
import com.arsdigita.domain.SimpleDomainObjectTraversalAdapter;
import com.arsdigita.domain.DomainObject;

/**
 * An adapter for content items allowing pluggable assets to extend the traversal.
 */
public class ContentItemTraversalAdapter
        extends SimpleDomainObjectTraversalAdapter {

    private static final Logger s_log =
                                Logger.getLogger(ContentItemTraversalAdapter.class);
    private static final Map s_assetAdapters = new HashMap();

    public static void registerAssetAdapter(String path,
                                            DomainObjectTraversalAdapter adapter,
                                            String context) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Registering asset adapter " + path + " adapter " + adapter.getClass()
                        + " in context " + context);
        }

        Map adapters = (Map) s_assetAdapters.get(context);
        if (adapters == null) {
            adapters = new HashMap();
            s_assetAdapters.put(context, adapters);
        }
        adapters.put(path, adapter);
    }

    public ContentItemTraversalAdapter() {
        super();
    }

    public ContentItemTraversalAdapter(SimpleDomainObjectTraversalAdapter adapter) {
        super(adapter);
    }

    /**
     * If the path references an asset, then delegates to the asset's adapter, otherwise delegates
     * to the content item's primary adapter
     */
    @Override
    public boolean processProperty(DomainObject obj,
                                   String path,
                                   Property prop,
                                   String context) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Process property " + path + " in context " + context);
        }
        String prefix = "/object/";
        int offset = path.indexOf("/", prefix.length());
        if (offset == -1) {
            String base = path.substring(prefix.length());
            Map adapters = (Map) s_assetAdapters.get(context);
            if (adapters != null && adapters.containsKey(base)) {
                if (s_log.isDebugEnabled()) {
                    s_log.debug("Following asset");
                }
                return true;
            } else {
                if (s_log.isDebugEnabled()) {
                    s_log.debug("Root delegate to primary adapter " + base);
                }
                return super.processProperty(obj, path, prop, context);
            }
        } else {
            String base = path.substring(prefix.length(), offset);
            String rest = path.substring(offset + 1);

            Map adapters = (Map) s_assetAdapters.get(context);
            if (adapters != null && adapters.containsKey(base)) {
                DomainObjectTraversalAdapter adapter = (DomainObjectTraversalAdapter) adapters.get(
                        base);
                if (s_log.isDebugEnabled()) {
                    s_log.debug("Delegate to asset adapter " + base + " " + rest + " " + adapter);
                }
                return adapter.processProperty(obj, "/object/" + rest, prop, context);
            } else {
                if (s_log.isDebugEnabled()) {
                    s_log.debug("Delegate to primary adapter " + base + " " + rest);
                }
                return super.processProperty(obj, path, prop, context);
            }
        }
    }
}
