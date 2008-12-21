/*
 * Copyright (C) 2008 Permeance Technologies Pty Ltd. All Rights Reserved.
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 */

package com.arsdigita.london.portal.portlet;

import org.apache.log4j.Logger;

import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.portal.AbstractPortletRenderer;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.ACSObjectInstantiator;
import com.arsdigita.kernel.ResourceType;
import com.arsdigita.kernel.ResourceTypeConfig;
import com.arsdigita.kernel.ui.ResourceConfigFormSection;
import com.arsdigita.london.navigation.Navigation;
import com.arsdigita.london.portal.ui.portlet.NavigationDirectoryPortletEditor;
import com.arsdigita.london.portal.ui.portlet.NavigationDirectoryPortletRenderer;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.portal.Portlet;
import com.arsdigita.portal.PortletType;

public class NavigationDirectoryPortlet extends Portlet
{
    private static final Logger s_log = Logger.getLogger(NavigationDirectoryPortlet.class);

    public static final String BASE_DATA_OBJECT_TYPE = "com.arsdigita.london.portal.portlet.NavigationDirectoryPortlet";

    public static final String DEPTH = "depth";

    public static final String NAVIGATION = "navigation";

    public static void loadPortletType()
    {
        PortletType type = PortletType.createPortletType("Navigation Directory", PortletType.WIDE_PROFILE,
                NavigationDirectoryPortlet.BASE_DATA_OBJECT_TYPE);
        type.setDescription("Displays a directory of navigation categories");
        s_log.info("Loading portlet type " + type);
    }

    public static void registerInstantiator()
    {
        DomainObjectFactory.registerInstantiator(BASE_DATA_OBJECT_TYPE, new ACSObjectInstantiator()
        {
            public DomainObject doNewInstance(DataObject dataObject)
            {
                return new NavigationDirectoryPortlet(dataObject);
            }
        });
    }

    public static void registerResourceTypeConfig()
    {
        new ResourceTypeConfig(BASE_DATA_OBJECT_TYPE)
        {
            public ResourceConfigFormSection getCreateFormSection(final ResourceType resType,
                    final RequestLocal parentAppRL)
            {
                return new NavigationDirectoryPortletEditor(resType, parentAppRL);
            }

            public ResourceConfigFormSection getModifyFormSection(final RequestLocal application)
            {
                return new NavigationDirectoryPortletEditor(application);
            }
        };
    }

    public NavigationDirectoryPortlet(DataObject dataObject)
    {
        super(dataObject);
    }

    public void setDepth(int depth)
    {
        set(DEPTH, new Integer(depth));
    }

    public int getDepth()
    {
        return ((Integer) get(DEPTH)).intValue();
    }

    public Navigation getNavigation()
    {
        return new Navigation((DataObject) get(NAVIGATION));
    }

    public void setNavigation(Navigation navigation)
    {
        set(NAVIGATION, navigation);
    }

    protected AbstractPortletRenderer doGetPortletRenderer()
    {
        return new NavigationDirectoryPortletRenderer(this);
    }

    protected String getBaseDataObjectType()
    {
        return BASE_DATA_OBJECT_TYPE;
    }
}
