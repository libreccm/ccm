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
package com.arsdigita.cms.portlet;

import com.arsdigita.bebop.portal.AbstractPortletRenderer;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.ACSObjectInstantiator;
import com.arsdigita.cms.ui.portlet.ContentSectionsPortletRenderer;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.portal.Portlet;
import com.arsdigita.portal.PortletSetup;
import com.arsdigita.portal.PortletType;

import org.apache.log4j.Logger;


public class ContentSectionsPortlet extends Portlet {

    /** Private logger instance for this class. */
    private static final Logger s_log = Logger.getLogger(ContentSectionsPortlet.class);

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.cms.portlet.ContentSectionsPortlet";

    /**
     * Constructor, retrieves a portlet from the database store 
     * based on the data object.
     * @param dataObject
     */
    public ContentSectionsPortlet(DataObject dataObject) {
        super(dataObject);
    }

    /**
     * Retrieve the base data object type (i.e. fully qualified class name).
     * @return
     */
    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    /**
     * 
     * @return
     */
    protected AbstractPortletRenderer doGetPortletRenderer() {
        return new ContentSectionsPortletRenderer(this);
    }


    /**
     * Load the portlet type into database. Part of the non-recurring loader
     * step as part of the installation.
     *
     * Usage: in loader.run(final ScriptContext ctx):
     *        ContentSectionsPortlet.loadPortletType();
     */
    public static void loadPortletType()
    {
        PortletType type = PortletType
                .createPortletType("Content Sections",
                                   PortletType.WIDE_PROFILE,
                                   ContentSectionsPortlet.BASE_DATA_OBJECT_TYPE);
        type.setDescription("Displays a list of content sections");
        s_log.info("Loading portlet type " + type);
    }

    /**
     * Reccurring initialization of the portlet each time ccm starts.
     *
     * It registers an object instantiator with the portal infrastructure.
     * Usage: in initialize.init(domainInitEvent):
     *        ContentSectionsPortlet.initPortlet();
     */
    public static void initPortlet()
    {
        // register instantiator
        DomainObjectFactory.registerInstantiator(BASE_DATA_OBJECT_TYPE,
                                                 new ACSObjectInstantiator()
        {
            public DomainObject doNewInstance(DataObject dataObject)
            {
                return new ContentItemPortlet(dataObject);
            }
        });
    }


//  Alternative way to load and initialize a portlet using class PortletSetup.
//  It performs the loading step as well as the registerInstantiator step in
//  one class and can be used by the loader as well as the initializer. Was
//  especially useful in the old initializer system where the loading step and
//  the recurring initialization step were not separated.
//
    private void loadContentSectionsPortlet() {

        PortletSetup setup = new PortletSetup(s_log);
        setup.setPortletObjectType(ContentSectionsPortlet.BASE_DATA_OBJECT_TYPE);
        setup.setTitle("Content Sections");
        setup.setDescription("Displays a list of content sections");
        setup.setProfile(PortletType.WIDE_PROFILE);

        setup.setInstantiator(new ACSObjectInstantiator() {
            protected DomainObject doNewInstance(DataObject dataObject) {
                return new ContentSectionsPortlet(dataObject);
            }
        });
        setup.run();
    }


}
