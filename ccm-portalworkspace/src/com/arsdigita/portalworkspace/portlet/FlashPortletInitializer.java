/*
 * Copyright (C) 2008 Permeance Technologies Ptd Ltd. All Rights Reserved.
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

package com.arsdigita.portalworkspace.portlet;

import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.ACSObjectInstantiator;
import com.arsdigita.kernel.ResourceType;
import com.arsdigita.kernel.ResourceTypeConfig;
import com.arsdigita.kernel.ui.ResourceConfigFormSection;
import com.arsdigita.portalworkspace.ui.portlet.FlashPortletEditor;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.portal.PortletType;

/**
 * Loads and initializes the {@link FlashPortlet}.
 * 
 * @author <a href="https://sourceforge.net/users/terry_permeance/">terry_permeance</a>
 */
public final class FlashPortletInitializer
{
    /**
     * Load the {@link PortletType}.
     */
    public static void loadPortletType()
    {
        PortletType type = PortletType.createPortletType(
                "Flash movie",  PortletType.WIDE_PROFILE,
                FlashPortlet.BASE_DATA_OBJECT_TYPE);
        type.setDescription("Displays a Flash movie");
    }

    /**
     * Initializes the {@link FlashPortlet} by registering the instantiator
     * and portlet editor.
     */
    public static void initialize()
    {
        DomainObjectFactory.registerInstantiator(
                FlashPortlet.BASE_DATA_OBJECT_TYPE, new ACSObjectInstantiator()
        {
            @Override
            public DomainObject doNewInstance(DataObject dataObject)
            {
                return new FlashPortlet(dataObject);
            }
        });

        new ResourceTypeConfig(FlashPortlet.BASE_DATA_OBJECT_TYPE)
        {
            @Override
            public ResourceConfigFormSection getCreateFormSection(
                    final ResourceType resType,
                    final RequestLocal parentAppRL)
            {
                return new FlashPortletEditor(resType, parentAppRL);
            }

            @Override
            public ResourceConfigFormSection getModifyFormSection(
                    final RequestLocal application)
            {
                return new FlashPortletEditor(application);
            }
        };
    }

    /**
     * Default constructor.
     */
    private FlashPortletInitializer()
    {
        // This class cannot be instantiated
    }
}
