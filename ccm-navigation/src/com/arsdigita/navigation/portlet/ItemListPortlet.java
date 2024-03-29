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

package com.arsdigita.navigation.portlet;

import com.arsdigita.persistence.DataObject;
import com.arsdigita.bebop.portal.AbstractPortletRenderer;
import com.arsdigita.navigation.DataCollectionDefinition;
import com.arsdigita.navigation.cms.CMSDataCollectionDefinition;
import com.arsdigita.navigation.ui.portlet.ItemListPortletRenderer;

/**
 * Portlet to display a list of (content) items.
 *
 * The items can be selected / restricted by base type and filtered by a
 * category or category tree. Ordering and length of the list can be configured.
 *
 * @version $Id: ItemListPortlet.java 2070 2010-01-28 08:47:41Z pboy $
 */
public class ItemListPortlet extends ObjectListPortlet {
    

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.navigation.portlet.ItemListPortlet";

    public static final String VERSION = "version";

    public ItemListPortlet(DataObject dobj) {
        super(dobj);
    }

    public void setVersion(String version) {
        set(VERSION, version);
    }
    
    public String getVersion() {
        return (String)get(VERSION);
    }

    @Override
    protected DataCollectionDefinition newDataCollectionDefinition() {
        return new CMSDataCollectionDefinition();
    }

    @Override
    public DataCollectionDefinition getDataCollectionDefinition() {
        CMSDataCollectionDefinition def = (CMSDataCollectionDefinition)
            super.getDataCollectionDefinition();
        
        def.setFilterVersion(getVersion());
        
        return def;
    }

    @Override
    protected AbstractPortletRenderer doGetPortletRenderer() {
        return new ItemListPortletRenderer(this);
    }
}
